package com.parkmate.application.mapper;

import com.parkmate.adapter.inbound.rest.dto.response.UserResponse;
import com.parkmate.application.service.ClockpointService;
import com.parkmate.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponse toDto(UserEntity u) { return toResponse(u); }
    public UserResponse toResponse(UserEntity u) {
        if (u == null) return null;
        return UserResponse.builder()
            .id(u.getId()).name(u.getName()).company(u.getCompany())
            .tower(u.getTower()).floor(u.getFloor())
            .atClockpoint(u.isAtClockpoint())
            .init(String.valueOf(u.getName().charAt(0)).toUpperCase())
            .color(ClockpointService.colorFor(u.getId()))
            .build();
    }
}
