package com.parkmate.infrastructure.persistence.repository;

import com.parkmate.infrastructure.persistence.entity.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface JpaEventRepository extends JpaRepository<EventEntity, Long> {

    @Query("SELECT e FROM EventEntity e WHERE e.module = :mod AND e.active = true AND e.expired = false ORDER BY e.eventDate ASC, e.eventTime ASC")
    List<EventEntity> findActiveByModule(@Param("mod") String module);

    @Query("SELECT e FROM EventEntity e WHERE e.creator.id = :uid AND e.activity = :act AND e.active = true AND e.expired = false")
    List<EventEntity> findActiveByCreatorAndActivity(@Param("uid") Long uid, @Param("act") String activity);

    @Query("SELECT e FROM EventEntity e WHERE e.creator.id = :uid ORDER BY e.createdAt DESC")
    List<EventEntity> findByCreatorId(@Param("uid") Long uid);

    @Query("SELECT e FROM EventEntity e JOIN e.joiners j WHERE j.id = :uid AND e.creator.id <> :uid ORDER BY e.eventDate ASC")
    List<EventEntity> findJoinedByUserId(@Param("uid") Long uid);

    @Query("SELECT e FROM EventEntity e WHERE e.active = true AND e.expired = false AND e.eventDate <= :today")
    List<EventEntity> findExpirable(@Param("today") LocalDate today);

    @Query("SELECT e FROM EventEntity e WHERE e.expired = true ORDER BY e.eventDate DESC, e.createdAt DESC")
    List<EventEntity> findAllExpired();
}
