package com.parkmate.application.usecase;

import com.parkmate.domain.exception.*;
import com.parkmate.domain.port.EventRepositoryPort;
import com.parkmate.infrastructure.persistence.entity.EventEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service @RequiredArgsConstructor @Slf4j
public class CancelEventUseCase {
    private final EventRepositoryPort eventRepo;

    @Transactional
    @CacheEvict(value = "events", allEntries = true)
    public void execute(Long eventId, Long userId) {
        EventEntity event = eventRepo.findById(eventId)
            .orElseThrow(() -> new EventNotFoundException(eventId));
        if (!event.getCreator().getId().equals(userId))
            throw new UnauthorizedException("Only creator can cancel this event");
        event.setActive(false);
        event.setCancelledAt(LocalDateTime.now());
        eventRepo.save(event);
        log.info("Event {} cancelled by userId={}", eventId, userId);
    }

    @Transactional
    @CacheEvict(value = "events", allEntries = true)
    public void cancelByActivity(Long userId, String activity) {
        List<EventEntity> events = eventRepo.findActiveByCreatorAndActivity(userId, activity);
        events.forEach(e -> {
            e.setActive(false);
            e.setCancelledAt(LocalDateTime.now());
            eventRepo.save(e);
        });
        log.info("Cancelled {} events of activity '{}' for userId={}", events.size(), activity, userId);
    }
}
