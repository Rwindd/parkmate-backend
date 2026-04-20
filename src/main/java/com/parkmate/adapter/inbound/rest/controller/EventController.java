package com.parkmate.adapter.inbound.rest.controller;

import com.parkmate.adapter.inbound.rest.dto.request.CreateEventRequest;
import com.parkmate.adapter.inbound.rest.dto.response.EventResponse;
import com.parkmate.application.service.EventQueryService;
import com.parkmate.application.usecase.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Events REST controller.
 * All write operations → dedicated UseCase classes.
 * All read operations → EventQueryService (cacheable).
 */
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final CreateEventUseCase createEventUseCase;
    private final JoinEventUseCase joinEventUseCase;
    private final LeaveEventUseCase leaveEventUseCase;
    private final CancelEventUseCase cancelEventUseCase;
    private final EventQueryService eventQueryService;

    // ── READ ─────────────────────────────────────────

    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllActive(HttpServletRequest req) {
        return ResponseEntity.ok(eventQueryService.getAllActive(userId(req)));
    }

    @GetMapping("/module/{module}")
    public ResponseEntity<List<EventResponse>> byModule(@PathVariable String module,
                                                         HttpServletRequest req) {
        return ResponseEntity.ok(eventQueryService.getByModule(module, userId(req)));
    }

    @GetMapping("/history")
    public ResponseEntity<List<EventResponse>> history(HttpServletRequest req) {
        return ResponseEntity.ok(eventQueryService.getHistory(userId(req)));
    }

    @GetMapping("/mine")
    public ResponseEntity<List<EventResponse>> mine(HttpServletRequest req) {
        return ResponseEntity.ok(eventQueryService.getMyEvents(userId(req)));
    }

    // ── WRITE ────────────────────────────────────────

    @PostMapping
    public ResponseEntity<EventResponse> create(@Valid @RequestBody CreateEventRequest body,
                                                 HttpServletRequest req) {
        return ResponseEntity.status(201)
            .body(createEventUseCase.execute(userId(req), body));
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<EventResponse> join(@PathVariable Long id, HttpServletRequest req) {
        return ResponseEntity.ok(joinEventUseCase.execute(id, userId(req)));
    }

    @DeleteMapping("/{id}/join")
    public ResponseEntity<EventResponse> leave(@PathVariable Long id, HttpServletRequest req) {
        return ResponseEntity.ok(leaveEventUseCase.execute(id, userId(req)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable Long id, HttpServletRequest req) {
        cancelEventUseCase.execute(id, userId(req));
        return ResponseEntity.noContent().build();
    }

    /**
     * Cancel old event of same activity before creating new one.
     * Called by frontend "Cancel old & Create new" flow.
     */
    @DeleteMapping("/activity/{activity}")
    public ResponseEntity<Void> cancelByActivity(@PathVariable String activity,
                                                  HttpServletRequest req) {
        cancelEventUseCase.cancelByActivity(userId(req), activity);
        return ResponseEntity.noContent().build();
    }

    // ── HELPER ───────────────────────────────────────

    private Long userId(HttpServletRequest req) {
        return (Long) req.getAttribute("userId");
    }
}
