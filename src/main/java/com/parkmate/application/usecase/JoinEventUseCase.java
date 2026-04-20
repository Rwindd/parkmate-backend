package com.parkmate.application.usecase;

import com.parkmate.adapter.inbound.rest.dto.response.EventResponse;
import com.parkmate.application.mapper.EventMapper;
import com.parkmate.domain.exception.*;
import com.parkmate.domain.port.EventRepositoryPort;
import com.parkmate.domain.port.UserRepositoryPort;
import com.parkmate.infrastructure.persistence.entity.EventEntity;
import com.parkmate.infrastructure.persistence.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

/**
 * USE CASE: Join an event.
 * Business rules:
 *   - Cannot join expired events (started already)
 *   - Cannot join full events
 *   - Creator cannot join own event
 *   - Idempotent — joining twice is safe
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class JoinEventUseCase {

    private final EventRepositoryPort eventRepo;
    private final UserRepositoryPort userRepo;
    private final EventMapper eventMapper;

    @Transactional
    @CacheEvict(value = "events", allEntries = true)
    public EventResponse execute(Long eventId, Long userId) {
        EventEntity event = eventRepo.findById(eventId)
            .orElseThrow(() -> new EventNotFoundException(eventId));
        UserEntity user = userRepo.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        if (!event.isActive()) throw new UnauthorizedException("Event has been cancelled");

        // Check if event has started
        LocalDateTime eventStart = LocalDateTime.of(event.getEventDate(), event.getEventTime());
        if (LocalDateTime.now().isAfter(eventStart) || event.isExpired()) {
            throw new EventExpiredException();
        }

        if (event.getCreator().getId().equals(userId)) {
            throw new UnauthorizedException("Creator cannot join their own event");
        }

        if (event.isFull()) throw new EventFullException();

        boolean alreadyJoined = event.getJoiners().stream()
            .anyMatch(j -> j.getId().equals(userId));
        if (!alreadyJoined) {
            event.getJoiners().add(user);
            eventRepo.save(event);
            log.info("User {} joined event {}", userId, eventId);
        }
        return eventMapper.toResponse(event, userId);
    }
}
