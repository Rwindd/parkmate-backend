package com.parkmate.adapter.inbound.rest.controller;

import com.parkmate.adapter.inbound.rest.dto.request.AnonPostRequest;
import com.parkmate.adapter.inbound.rest.dto.response.AnonPostResponse;
import com.parkmate.application.service.AnonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Anonymous posts — no user identity is stored or returned.
 * Pattern: Privacy by Design — service layer never logs who posted.
 */
@RestController
@RequestMapping("/api/anon")
@RequiredArgsConstructor
public class AnonController {

    private final AnonService anonService;

    @GetMapping
    public ResponseEntity<List<AnonPostResponse>> getAll() {
        return ResponseEntity.ok(anonService.getAll());
    }

    @PostMapping
    public ResponseEntity<AnonPostResponse> create(@Valid @RequestBody AnonPostRequest req) {
        return ResponseEntity.status(201).body(anonService.create(req.getText()));
    }

    @PostMapping("/{id}/relate")
    public ResponseEntity<AnonPostResponse> relate(@PathVariable Long id) {
        return ResponseEntity.ok(anonService.relate(id));
    }
}
