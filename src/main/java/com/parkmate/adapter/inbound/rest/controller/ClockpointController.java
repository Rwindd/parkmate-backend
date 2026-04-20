package com.parkmate.adapter.inbound.rest.controller;

import com.parkmate.adapter.inbound.rest.dto.request.SendChatRequest;
import com.parkmate.adapter.inbound.rest.dto.response.ChatMessageResponse;
import com.parkmate.adapter.inbound.rest.dto.response.PresenceResponse;
import com.parkmate.application.service.ClockpointService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Clockpoint — live presence + Discord-style chat.
 * WS pushes real-time; REST provides initial state + send message.
 */
@RestController
@RequestMapping("/api/clockpoint")
@RequiredArgsConstructor
public class ClockpointController {

    private final ClockpointService clockpointService;

    @GetMapping("/presence")
    public ResponseEntity<List<PresenceResponse>> presence() {
        return ResponseEntity.ok(clockpointService.getPresence());
    }

    @PostMapping("/join")
    public ResponseEntity<Void> join(HttpServletRequest req) {
        clockpointService.joinClockpoint(userId(req));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/join")
    public ResponseEntity<Void> leave(HttpServletRequest req) {
        clockpointService.leaveClockpoint(userId(req));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/chat")
    public ResponseEntity<List<ChatMessageResponse>> chatHistory(HttpServletRequest req) {
        return ResponseEntity.ok(clockpointService.getChatHistory(userId(req)));
    }

    @PostMapping("/chat")
    public ResponseEntity<ChatMessageResponse> sendChat(@Valid @RequestBody SendChatRequest body,
                                                         HttpServletRequest req) {
        return ResponseEntity.ok(clockpointService.sendMessage(userId(req), body.getText()));
    }

    private Long userId(HttpServletRequest req) {
        return (Long) req.getAttribute("userId");
    }
}
