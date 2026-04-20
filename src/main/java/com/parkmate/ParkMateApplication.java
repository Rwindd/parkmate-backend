package com.parkmate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * ParkMate — Olympia Tech Park Social Platform
 *
 * Architecture: Hexagonal (Ports & Adapters)
 * Layers:
 *   domain/          — Pure Java domain models, value objects, exceptions, ports (interfaces)
 *   application/     — Use cases (write), query services (read), mappers
 *   infrastructure/  — JPA entities, repositories, security, websocket, scheduler
 *   adapter/         — REST controllers, DTOs, exception handlers
 *   config/          — Spring configuration (Security, Cache, WebSocket, CORS)
 *
 * Design Patterns Used:
 *   Hexagonal Architecture, CQRS-lite, Repository, Adapter,
 *   Use Case, Value Object, Observer (WS), Chain of Responsibility,
 *   Strategy (cache), Facade, Mapper, Scheduled Task, Factory (builders)
 */
@SpringBootApplication
@EnableScheduling
@Slf4j
public class ParkMateApplication {
    public static void main(String[] args) {
        SpringApplication.run(ParkMateApplication.class, args);
        log.info("╔══════════════════════════════════════╗");
        log.info("║   ParkMate Backend — RUNNING         ║");
        log.info("║   http://localhost:8080              ║");
        log.info("║   H2 Console: /h2-console            ║");
        log.info("╚══════════════════════════════════════╝");
    }
}
