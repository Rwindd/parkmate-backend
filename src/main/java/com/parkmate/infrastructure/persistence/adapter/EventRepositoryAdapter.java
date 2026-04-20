package com.parkmate.infrastructure.persistence.adapter;

import com.parkmate.domain.port.EventRepositoryPort;
import com.parkmate.infrastructure.persistence.entity.EventEntity;
import com.parkmate.infrastructure.persistence.repository.JpaEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EventRepositoryAdapter implements EventRepositoryPort {

    private final JpaEventRepository jpa;

    @Override public EventEntity save(EventEntity e)                                      { return jpa.save(e); }
    @Override public Optional<EventEntity> findById(Long id)                              { return jpa.findById(id); }
    @Override public List<EventEntity> findActiveByModule(String module)                  { return jpa.findActiveByModule(module); }
    @Override public List<EventEntity> findActiveByCreatorAndActivity(Long uid, String a) { return jpa.findActiveByCreatorAndActivity(uid, a); }
    @Override public List<EventEntity> findByCreatorId(Long uid)                          { return jpa.findByCreatorId(uid); }
    @Override public List<EventEntity> findJoinedByUserId(Long uid)                       { return jpa.findJoinedByUserId(uid); }
    @Override public List<EventEntity> findAllExpired()                                   { return jpa.findAllExpired(); }

    @Override
    public List<EventEntity> findExpirable(LocalDate today) {
        return jpa.findExpirable(today);
    }
}
