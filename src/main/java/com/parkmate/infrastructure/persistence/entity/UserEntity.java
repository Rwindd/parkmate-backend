package com.parkmate.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "pm_users",
    indexes = { @Index(name = "idx_users_device", columnList = "device_id", unique = true) })
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class UserEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String company;

    @Column(nullable = false)
    private String tower;

    @Column(nullable = false)
    private String floor;

    @Column
    private String phone;

    @Column(name = "device_id", nullable = false, unique = true)
    private String deviceId;

    @Column(name = "at_clockpoint", nullable = false)
    @Builder.Default
    private boolean atClockpoint = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() { createdAt = LocalDateTime.now(); updatedAt = createdAt; }

    @PreUpdate
    void onUpdate() { updatedAt = LocalDateTime.now(); }
}
