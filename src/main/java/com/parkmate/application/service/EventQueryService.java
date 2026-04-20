package com.parkmate.application.service;

import com.parkmate.adapter.inbound.rest.dto.response.EventResponse;
import com.parkmate.application.mapper.EventMapper;
import com.parkmate.domain.port.EventRepositoryPort;
import com.parkmate.infrastructure.persistence.entity.EventEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.*;

/**
 * READ-ONLY query service.
 * Pattern: CQRS-lite — reads cached, writes evict cache.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventQueryService {

    private final EventRepositoryPort eventRepo;
    private final EventMapper eventMapper;

    private static final List<String> ALL_MODULES = List.of("sports","lunch","build","gaming","movie");

    @Cacheable(value = "events", key = "#module + '_' + #userId")
    public List<EventResponse> getByModule(String module, Long userId) {
        return eventRepo.findActiveByModule(module).stream()
            .map(e -> eventMapper.toResponse(e, userId))
            .collect(Collectors.toList());
    }

    public List<EventResponse> getAllActive(Long userId) {
        return ALL_MODULES.stream()
            .flatMap(m -> eventRepo.findActiveByModule(m).stream())
            .sorted(Comparator.comparing(EventEntity::getEventDate)
                .thenComparing(EventEntity::getEventTime))
            .map(e -> eventMapper.toResponse(e, userId))
            .collect(Collectors.toList());
    }

    public List<EventResponse> getHistory(Long userId) {
        return eventRepo.findAllExpired().stream()
            .map(e -> eventMapper.toResponse(e, userId))
            .collect(Collectors.toList());
    }

    public List<EventResponse> getMyEvents(Long userId) {
        List<EventResponse> result = new ArrayList<>();
        eventRepo.findByCreatorId(userId).stream()
            .map(e -> eventMapper.toResponse(e, userId)).forEach(result::add);
        eventRepo.findJoinedByUserId(userId).stream()
            .map(e -> eventMapper.toResponse(e, userId)).forEach(result::add);
        return result;
    }
}
