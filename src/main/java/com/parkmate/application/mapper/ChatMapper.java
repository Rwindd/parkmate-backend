package com.parkmate.application.mapper;

import com.parkmate.adapter.inbound.rest.dto.response.ChatMessageResponse;
import com.parkmate.application.service.ClockpointService;
import com.parkmate.infrastructure.persistence.entity.ChatMessageEntity;
import com.parkmate.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;
import java.time.format.DateTimeFormatter;

@Component
public class ChatMapper {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("h:mm a");

    public ChatMessageResponse toResponse(ChatMessageEntity m, Long currentUserId) {
        UserEntity sender = m.getSender();
        boolean self = sender != null && currentUserId != null && sender.getId().equals(currentUserId);
        String name = sender != null ? sender.getName() : "";
        String co   = sender != null ? sender.getCompany() : "";
        String init = (sender != null && !name.isEmpty()) ? String.valueOf(name.charAt(0)).toUpperCase() : "";
        String color = sender != null ? ClockpointService.colorFor(sender.getId()) : "#8B5CF6";
        return ChatMessageResponse.builder()
            .id(m.getId()).senderName(name).senderCompany(co)
            .senderInit(init).senderColor(color)
            .text(m.getText()).systemMessage(m.isSystemMessage())
            .timeStr(m.getSentAt().format(FMT)).self(self)
            .build();
    }
}
