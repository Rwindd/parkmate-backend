package com.parkmate.adapter.inbound.rest.advice;

import com.parkmate.adapter.inbound.rest.dto.response.ApiError;
import com.parkmate.domain.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Centralised exception → HTTP response mapping.
 * Pattern: Chain of Responsibility (Spring tries handlers in order).
 *
 * Domain exceptions → 4xx (client error)
 * RuntimeException  → 500 (server error)
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // ── Domain exceptions → 404 ──────────────────────
    @ExceptionHandler({UserNotFoundException.class, EventNotFoundException.class})
    public ResponseEntity<ApiError> handleNotFound(DomainException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiError.of(404, "NOT_FOUND", ex.getMessage(), req.getRequestURI()));
    }

    // ── Duplicate event → 409 Conflict ───────────────
    @ExceptionHandler(DuplicateEventException.class)
    public ResponseEntity<ApiError> handleDuplicate(DuplicateEventException ex, HttpServletRequest req) {
        ApiError err = ApiError.builder()
            .status(409).error("DUPLICATE_EVENT")
            .message(ex.getMessage())
            .path(req.getRequestURI())
            .fieldErrors(Map.of("existingEventId", String.valueOf(ex.getExistingEventId()),
                                "activity", ex.getActivity()))
            .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(err);
    }

    // ── Business rule violations → 422 ───────────────
    @ExceptionHandler({EventExpiredException.class, EventFullException.class,
                       InvalidDateException.class})
    public ResponseEntity<ApiError> handleBusinessRule(DomainException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(ApiError.of(422, "BUSINESS_RULE_VIOLATION", ex.getMessage(), req.getRequestURI()));
    }

    // ── Unauthorised → 403 ───────────────────────────
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiError> handleUnauthorized(UnauthorizedException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ApiError.of(403, "FORBIDDEN", ex.getMessage(), req.getRequestURI()));
    }

    // ── Bean validation → 400 with field details ─────
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex,
                                                     HttpServletRequest req) {
        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
            .collect(Collectors.toMap(FieldError::getField,
                f -> f.getDefaultMessage() != null ? f.getDefaultMessage() : "invalid",
                (a, b) -> a));
        ApiError err = ApiError.builder()
            .status(400).error("VALIDATION_FAILED")
            .message("Request validation failed")
            .path(req.getRequestURI())
            .fieldErrors(fieldErrors)
            .build();
        return ResponseEntity.badRequest().body(err);
    }

    // ── Catch-all → 500 ──────────────────────────────
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest req) {
        log.error("Unhandled exception at {}: {}", req.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiError.of(500, "INTERNAL_SERVER_ERROR",
                "Something went wrong. Please try again.", req.getRequestURI()));
    }
}
