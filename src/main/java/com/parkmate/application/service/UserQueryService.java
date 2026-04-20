package com.parkmate.application.service;

import com.parkmate.adapter.inbound.rest.dto.response.CompanyStatResponse;
import com.parkmate.adapter.inbound.rest.dto.response.UserResponse;
import com.parkmate.application.mapper.UserMapper;
import com.parkmate.domain.port.UserRepositoryPort;
import com.parkmate.infrastructure.persistence.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.*;

@Service @RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryService {

    private final UserRepositoryPort userRepo;
    private final UserMapper userMapper;

    public List<UserResponse> getAll() {
        return userRepo.findAll().stream()
            .map(userMapper::toResponse).collect(Collectors.toList());
    }

    public List<CompanyStatResponse> getCompanyStats() {
        List<UserEntity> all = userRepo.findAll();
        long total = all.size();
        return all.stream()
            .collect(Collectors.groupingBy(UserEntity::getCompany))
            .entrySet().stream()
            .map(e -> CompanyStatResponse.builder()
                .company(e.getKey())
                .memberCount((long) e.getValue().size())
                .percentage(total > 0 ? (e.getValue().size() * 100L / total) : 0)
                .towers(e.getValue().stream().map(UserEntity::getTower).distinct().collect(Collectors.toList()))
                .build())
            .sorted(Comparator.comparingLong(CompanyStatResponse::getMemberCount).reversed())
            .collect(Collectors.toList());
    }
}
