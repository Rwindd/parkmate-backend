package com.parkmate.application.usecase;

import com.parkmate.adapter.inbound.rest.dto.response.EventResponse;
import com.parkmate.application.mapper.EventMapper;
import com.parkmate.domain.exception.*;
import com.parkmate.domain.port.EventRepositoryPort;
import com.parkmate.domain.port.UserRepositoryPort;
import com.parkmate.infrastructure.persistence.entity.EventEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor @Slf4j
public class LeaveEventUseCase {
    private final EventRepositoryPort eventRepo;
    private final UserRepositoryPort userRepo;
    private final EventMapper eventMapper;

    @Transactional
    @CacheEvict(value = "events", allEntries = true)
    public EventResponse execute(Long eventId, Long userId) {
        EventEntity event = eventRepo.findById(eventId)
            .orElseThrow(() -> new EventNotFoundException(eventId));
        event.getJoiners().removeIf(j -> j.getId().equals(userId));
        eventRepo.save(event);
        log.info("User {} left event {}", userId, eventId);
        return eventMapper.toResponse(event, userId);
    }
}
