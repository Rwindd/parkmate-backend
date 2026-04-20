package com.parkmate.application.service;

import com.parkmate.adapter.inbound.rest.dto.response.ChatMessageResponse;
import com.parkmate.adapter.inbound.rest.dto.response.PresenceResponse;
import com.parkmate.application.mapper.ChatMapper;
import com.parkmate.domain.exception.UserNotFoundException;
import com.parkmate.domain.port.ChatMessageRepositoryPort;
import com.parkmate.domain.port.UserRepositoryPort;
import com.parkmate.infrastructure.persistence.entity.ChatMessageEntity;
import com.parkmate.infrastructure.persistence.entity.UserEntity;
import com.parkmate.infrastructure.websocket.ClockpointWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Clockpoint — live presence + Discord-style chat.
 * Pattern: Facade over websocket + persistence.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ClockpointService {

    private final UserRepositoryPort userRepo;
    private final ChatMessageRepositoryPort chatRepo;
    private final ChatMapper chatMapper;
    private final ClockpointWebSocketHandler wsHandler;

    @Transactional
    public void joinClockpoint(Long userId) {
        UserEntity user = userRepo.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        user.setAtClockpoint(true);
        userRepo.save(user);
        // Broadcast system message
        String sysMsg = user.getName() + " (" + user.getCompany() + ") joined Clockpoint 👋";
        wsHandler.broadcastSystem(sysMsg);
        log.info("User {} joined Clockpoint", userId);
    }

    @Transactional
    public void leaveClockpoint(Long userId) {
        UserEntity user = userRepo.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        user.setAtClockpoint(false);
        userRepo.save(user);
        String sysMsg = user.getName() + " left Clockpoint 👋";
        wsHandler.broadcastSystem(sysMsg);
        log.info("User {} left Clockpoint", userId);
    }

    @Transactional(readOnly = true)
    public List<PresenceResponse> getPresence() {
        return userRepo.findAtClockpoint().stream()
            .map(u -> PresenceResponse.builder()
                .userId(u.getId())
                .name(u.getName())
                .company(u.getCompany())
                .tower(u.getTower())
                .floor(u.getFloor())
                .init(String.valueOf(u.getName().charAt(0)).toUpperCase())
                .color(colorFor(u.getId()))
                .build())
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getChatHistory(Long userId) {
        return chatRepo.findLast50().stream()
            .map(m -> chatMapper.toResponse(m, userId))
            .collect(Collectors.toList());
    }

    @Transactional
    public ChatMessageResponse sendMessage(Long userId, String text) {
        UserEntity sender = userRepo.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        ChatMessageEntity msg = ChatMessageEntity.builder()
            .sender(sender).text(text).systemMessage(false).build();
        ChatMessageEntity saved = chatRepo.save(msg);
        ChatMessageResponse response = chatMapper.toResponse(saved, userId);
        // Broadcast to all connected WS clients
        wsHandler.broadcastMessage(response);
        return response;
    }

    private static final String[] COLORS = {
        "#8B5CF6","#EC4899","#10B981","#3B82F6","#F59E0B",
        "#EF4444","#14B8A6","#A78BFA","#F97316","#06B6D4"
    };
    public static String colorFor(Long id) {
        return COLORS[(int)(id % COLORS.length)];
    }
}
