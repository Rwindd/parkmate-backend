package com.parkmate.infrastructure.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parkmate.adapter.inbound.rest.dto.response.ChatMessageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * WebSocket broadcaster for Clockpoint real-time chat.
 * Pattern: Observer — publishes events, all STOMP subscribers receive them.
 * Topic: /topic/clockpoint/chat
 * Topic: /topic/clockpoint/presence
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ClockpointWebSocketHandler {

    private final SimpMessagingTemplate messagingTemplate;
    private static final String CHAT_TOPIC     = "/topic/clockpoint/chat";
    private static final String PRESENCE_TOPIC = "/topic/clockpoint/presence";
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("h:mm a");

    /** Broadcast a user chat message to all connected clients */
    public void broadcastMessage(ChatMessageResponse msg) {
        messagingTemplate.convertAndSend(CHAT_TOPIC, msg);
        log.debug("WS broadcast chat from {}", msg.getSenderName());
    }

    /** Broadcast a system message (join/leave notifications) */
    public void broadcastSystem(String text) {
        ChatMessageResponse sysMsg = ChatMessageResponse.builder()
            .systemMessage(true)
            .text(text)
            .timeStr(LocalDateTime.now().format(FMT))
            .build();
        messagingTemplate.convertAndSend(CHAT_TOPIC, sysMsg);
        log.debug("WS broadcast system: {}", text);
    }

    /** Broadcast presence update (who's at clockpoint) */
    public void broadcastPresenceUpdate(Object presence) {
        messagingTemplate.convertAndSend(PRESENCE_TOPIC, presence);
    }
}
