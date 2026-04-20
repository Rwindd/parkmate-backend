package com.parkmate.domain.port;

import com.parkmate.infrastructure.persistence.entity.UserEntity;
import java.util.List;
import java.util.Optional;

/**
 * OUTBOUND PORT — domain tells infrastructure what it needs.
 * Infrastructure implements this. Domain never imports JPA.
 */
public interface UserRepositoryPort {
    UserEntity save(UserEntity user);
    Optional<UserEntity> findById(Long id);
    Optional<UserEntity> findByDeviceId(String deviceId);
    List<UserEntity> findAll();
    List<UserEntity> findAtClockpoint();
    List<UserEntity> findByCompany(String company);
    boolean existsByDeviceId(String deviceId);
}
