package com.parkmate.application.mapper;

import com.parkmate.adapter.inbound.rest.dto.response.EventResponse;
import com.parkmate.adapter.inbound.rest.dto.response.JoinerResponse;
import com.parkmate.application.service.ClockpointService;
import com.parkmate.infrastructure.persistence.entity.EventEntity;
import com.parkmate.infrastructure.persistence.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EventMapper {

    private final UserMapper userMapper;

    public EventResponse toResponse(EventEntity e, Long currentUserId) {
        boolean isCreator = currentUserId != null && e.getCreator().getId().equals(currentUserId);
        boolean isJoined  = currentUserId != null &&
            e.getJoiners().stream().anyMatch(j -> j.getId().equals(currentUserId));

        List<JoinerResponse> joiners = e.getJoiners().stream()
            .map(j -> toJoiner(j, isCreator))
            .collect(Collectors.toList());

        return EventResponse.builder()
            .id(e.getId()).module(e.getModule())
            .activity(e.getActivity()).activityIcon(e.getActivityIcon())
            .title(e.getTitle()).description(e.getDescription())
            .eventDate(e.getEventDate()).eventTime(e.getEventTime())
            .location(e.getLocation()).spots(e.getSpots())
            .spotsLeft(e.spotsLeft()).full(e.isFull())
            .visibility(e.getVisibility())
            .creator(userMapper.toResponse(e.getCreator()))
            .joiners(joiners).active(e.isActive()).expired(e.isExpired())
            .isCreator(isCreator).isJoined(isJoined)
            .createdAt(e.getCreatedAt())
            .build();
    }

    private JoinerResponse toJoiner(UserEntity u, boolean showPhone) {
        return JoinerResponse.builder()
            .id(u.getId()).name(u.getName()).company(u.getCompany())
            .tower(u.getTower()).floor(u.getFloor())
            .phone(showPhone ? u.getPhone() : null)   // privacy guard
            .init(String.valueOf(u.getName().charAt(0)).toUpperCase())
            .color(ClockpointService.colorFor(u.getId()))
            .build();
    }
}
