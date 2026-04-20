package com.parkmate.application.usecase;

import com.parkmate.adapter.inbound.rest.dto.request.RegisterRequest;
import com.parkmate.adapter.inbound.rest.dto.response.AuthResponse;
import com.parkmate.application.mapper.UserMapper;
import com.parkmate.domain.port.UserRepositoryPort;
import com.parkmate.infrastructure.persistence.entity.UserEntity;
import com.parkmate.infrastructure.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * USE CASE: Register or identify a returning user.
 * One-time onboarding — if deviceId already exists, skip registration.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RegisterUserUseCase {

    private final UserRepositoryPort userRepo;
    private final UserMapper userMapper;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse execute(RegisterRequest req) {
        // RETURNING USER — deviceId already in DB
        return userRepo.findByDeviceId(req.getDeviceId())
            .map(existing -> {
                log.info("Returning user login: {} ({})", existing.getName(), existing.getId());
                String token = jwtService.generateToken(existing.getId());
                return AuthResponse.builder()
                    .userId(existing.getId())
                    .token(token)
                    .user(userMapper.toDto(existing))
                    .isNewUser(false)
                    .build();
            })
            .orElseGet(() -> {
                // NEW USER — create account
                UserEntity entity = UserEntity.builder()
                    .name(req.getName())
                    .company(req.getCompany())
                    .tower(req.getTower())
                    .floor(req.getFloor())
                    .phone(req.getPhone())
                    .deviceId(req.getDeviceId())
                    .build();
                UserEntity saved = userRepo.save(entity);
                String token = jwtService.generateToken(saved.getId());
                log.info("New user registered: {} ({})", saved.getName(), saved.getId());
                return AuthResponse.builder()
                    .userId(saved.getId())
                    .token(token)
                    .user(userMapper.toDto(saved))
                    .isNewUser(true)
                    .build();
            });
    }
}
