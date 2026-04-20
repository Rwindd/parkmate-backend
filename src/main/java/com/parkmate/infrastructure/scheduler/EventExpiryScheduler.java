package com.parkmate.infrastructure.scheduler;

import com.parkmate.domain.port.EventRepositoryPort;
import com.parkmate.infrastructure.persistence.entity.EventEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Runs every minute to expire events whose date+time has passed.
 * Once expired → no more joins allowed (JoinEventUseCase checks this).
 * Pattern: Scheduled Task + Template Method (hook for subclasses to extend).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EventExpiryScheduler {

    private final EventRepositoryPort eventRepo;

    @Scheduled(fixedDelay = 60_000)   // every 60 seconds
    @Transactional
    @CacheEvict(value = "events", allEntries = true)
    public void expireStaleEvents() {
        List<EventEntity> candidates = eventRepo.findExpirable(LocalDate.now());
        int count = 0;
        for (EventEntity event : candidates) {
            LocalDateTime eventStart = LocalDateTime.of(event.getEventDate(), event.getEventTime());
            if (LocalDateTime.now().isAfter(eventStart)) {
                event.setExpired(true);
                eventRepo.save(event);
                count++;
                log.debug("Expired event id={} title='{}'", event.getId(), event.getTitle());
            }
        }
        if (count > 0) log.info("EventExpiryScheduler: expired {} events", count);
    }
}
