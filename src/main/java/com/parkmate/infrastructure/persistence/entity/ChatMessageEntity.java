package com.parkmate.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "pm_chat_messages",
    indexes = { @Index(name = "idx_chat_sent", columnList = "sent_at") })
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ChatMessageEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private UserEntity sender;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @Column(name = "system_message", nullable = false)
    @Builder.Default
    private boolean systemMessage = false;

    @Column(name = "sent_at", nullable = false, updatable = false)
    private LocalDateTime sentAt;

    @PrePersist
    void onCreate() { sentAt = LocalDateTime.now(); }
}
