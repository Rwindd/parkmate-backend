package com.parkmate.infrastructure.persistence.adapter;

import com.parkmate.domain.port.UserRepositoryPort;
import com.parkmate.infrastructure.persistence.entity.UserEntity;
import com.parkmate.infrastructure.persistence.repository.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final JpaUserRepository jpa;

    @Override public UserEntity save(UserEntity u)                     { return jpa.save(u); }
    @Override public Optional<UserEntity> findById(Long id)            { return jpa.findById(id); }
    @Override public Optional<UserEntity> findByDeviceId(String did)   { return jpa.findByDeviceId(did); }
    @Override public List<UserEntity> findAll()                        { return jpa.findAll(); }
    @Override public List<UserEntity> findAtClockpoint()               { return jpa.findByAtClockpointTrue(); }
    @Override public List<UserEntity> findByCompany(String co)         { return jpa.findByCompany(co); }
    @Override public boolean existsByDeviceId(String did)              { return jpa.existsByDeviceId(did); }
}
