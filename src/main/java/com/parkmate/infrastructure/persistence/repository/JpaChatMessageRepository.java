package com.parkmate.infrastructure.persistence.repository;

import com.parkmate.infrastructure.persistence.entity.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JpaChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {
    @Query(value = "SELECT m FROM ChatMessageEntity m ORDER BY m.sentAt DESC")
    List<ChatMessageEntity> findLast50Desc();
}
