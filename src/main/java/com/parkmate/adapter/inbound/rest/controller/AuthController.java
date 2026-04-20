package com.parkmate.adapter.inbound.rest.controller;

import com.parkmate.adapter.inbound.rest.dto.request.RegisterRequest;
import com.parkmate.adapter.inbound.rest.dto.response.AuthResponse;
import com.parkmate.application.usecase.RegisterUserUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Auth controller — ONE-TIME onboarding + returning user login.
 * POST /api/auth/register → new user OR returning user (same deviceId = auto-login)
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;

    /**
     * Unified endpoint for both new & returning users.
     * Frontend always calls this on load — backend figures out new vs returning.
     * Returns isNewUser=false for returning users (frontend skips onboarding).
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.ok(registerUserUseCase.execute(req));
    }
}
