package com.parkmate.adapter.inbound.rest.controller;

import com.parkmate.adapter.inbound.rest.dto.response.CompanyStatResponse;
import com.parkmate.adapter.inbound.rest.dto.response.UserResponse;
import com.parkmate.application.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Olympians page — list all users + company stats.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserQueryService userQueryService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAll() {
        return ResponseEntity.ok(userQueryService.getAll());
    }

    @GetMapping("/companies")
    public ResponseEntity<List<CompanyStatResponse>> companyStats() {
        return ResponseEntity.ok(userQueryService.getCompanyStats());
    }
}
