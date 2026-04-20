package com.parkmate.infrastructure.persistence.repository;

import com.parkmate.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface JpaUserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByDeviceId(String deviceId);
    boolean existsByDeviceId(String deviceId);
    List<UserEntity> findByAtClockpointTrue();
    List<UserEntity> findByCompany(String company);
}
