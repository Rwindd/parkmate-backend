package com.parkmate.domain.port;

import com.parkmate.infrastructure.persistence.entity.EventEntity;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EventRepositoryPort {
    EventEntity save(EventEntity event);
    Optional<EventEntity> findById(Long id);
    List<EventEntity> findActiveByModule(String module);
    List<EventEntity> findActiveByCreatorAndActivity(Long creatorId, String activity);
    List<EventEntity> findByCreatorId(Long creatorId);
    List<EventEntity> findJoinedByUserId(Long userId);
    List<EventEntity> findExpirable(LocalDate today);
    List<EventEntity> findAllExpired();
}
