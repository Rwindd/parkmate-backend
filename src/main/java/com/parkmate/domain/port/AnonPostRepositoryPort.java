package com.parkmate.domain.port;

import com.parkmate.infrastructure.persistence.entity.AnonPostEntity;
import java.util.List;
import java.util.Optional;

public interface AnonPostRepositoryPort {
    AnonPostEntity save(AnonPostEntity post);
    Optional<AnonPostEntity> findById(Long id);
    List<AnonPostEntity> findAllOrderedByDateDesc();
}
