package com.parkmate.infrastructure.persistence.adapter;

import com.parkmate.domain.port.AnonPostRepositoryPort;
import com.parkmate.infrastructure.persistence.entity.AnonPostEntity;
import com.parkmate.infrastructure.persistence.repository.JpaAnonPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
@RequiredArgsConstructor
public class AnonPostRepositoryAdapter implements AnonPostRepositoryPort {
    private final JpaAnonPostRepository jpa;
    @Override public AnonPostEntity save(AnonPostEntity p)             { return jpa.save(p); }
    @Override public Optional<AnonPostEntity> findById(Long id)        { return jpa.findById(id); }
    @Override public List<AnonPostEntity> findAllOrderedByDateDesc()   { return jpa.findAllByOrderByCreatedAtDesc(); }
}
