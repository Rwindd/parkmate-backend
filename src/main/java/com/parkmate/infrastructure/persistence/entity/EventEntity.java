package com.parkmate.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "pm_events",
    indexes = {
        @Index(name = "idx_events_module", columnList = "module"),
        @Index(name = "idx_events_creator", columnList = "creator_id"),
        @Index(name = "idx_events_date", columnList = "event_date"),
        @Index(name = "idx_events_active_expired", columnList = "active,expired")
    })
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class EventEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String module;

    @Column(nullable = false)
    private String activity;

    @Column(name = "activity_icon")
    private String activityIcon;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "event_date", nullable = false)
    private LocalDate eventDate;

    @Column(name = "event_time", nullable = false)
    private LocalTime eventTime;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private int spots;

    @Column(nullable = false)
    private String visibility;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private UserEntity creator;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "pm_event_joiners",
        joinColumns = @JoinColumn(name = "event_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id"))
    @Builder.Default
    private List<UserEntity> joiners = new ArrayList<>();

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(nullable = false)
    @Builder.Default
    private boolean expired = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
        if (joiners == null) joiners = new ArrayList<>();
    }

    public int spotsLeft() { return spots - joiners.size(); }
    public boolean isFull() { return spotsLeft() <= 0; }
}
