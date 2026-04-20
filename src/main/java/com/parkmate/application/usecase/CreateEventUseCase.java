package com.parkmate.application.usecase;

import com.parkmate.adapter.inbound.rest.dto.request.CreateEventRequest;
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
import java.time.LocalDate;
import java.util.List;

/**
 * USE CASE: Create a new event.
 * Business rules enforced here:
 *   - Max 30 days ahead
 *   - No past dates
 *   - 1 active event per activity per user (duplicate detection)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CreateEventUseCase {

    private final EventRepositoryPort eventRepo;
    private final UserRepositoryPort userRepo;
    private final EventMapper eventMapper;

    private static final int MAX_DAYS_AHEAD = 30;

    @Transactional
    @CacheEvict(value = "events", allEntries = true)
    public EventResponse execute(Long userId, CreateEventRequest req) {

        UserEntity creator = userRepo.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        validateDate(req);
        checkDuplicate(userId, req.getActivity());

        EventEntity event = EventEntity.builder()
            .module(req.getModule().toLowerCase())
            .activity(req.getActivity())
            .activityIcon(req.getActivityIcon())
            .title(req.getTitle())
            .description(req.getDescription())
            .eventDate(req.getEventDate())
            .eventTime(req.getEventTime())
            .location(req.getLocation())
            .spots(req.getSpots())
            .visibility(req.getVisibility())
            .creator(creator)
            .build();

        EventEntity saved = eventRepo.save(event);
        log.info("Event created: {} by userId={}", saved.getId(), userId);
        return eventMapper.toResponse(saved, userId);
    }

    private void validateDate(CreateEventRequest req) {
        LocalDate today = LocalDate.now();
        LocalDate maxDate = today.plusDays(MAX_DAYS_AHEAD);
        if (req.getEventDate().isBefore(today)) {
            throw new InvalidDateException("Event date cannot be in the past");
        }
        if (req.getEventDate().isAfter(maxDate)) {
            throw new InvalidDateException("Event must be within " + MAX_DAYS_AHEAD + " days");
        }
    }

    private void checkDuplicate(Long userId, String activity) {
        List<EventEntity> existing = eventRepo.findActiveByCreatorAndActivity(userId, activity);
        if (!existing.isEmpty()) {
            throw new DuplicateEventException(activity, existing.get(0).getId());
        }
    }
}
