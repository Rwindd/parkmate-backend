package com.parkmate.infrastructure.persistence.repository;

import com.parkmate.infrastructure.persistence.entity.AnonPostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JpaAnonPostRepository extends JpaRepository<AnonPostEntity, Long> {
    List<AnonPostEntity> findAllByOrderByCreatedAtDesc();
}
