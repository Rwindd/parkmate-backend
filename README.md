# ParkMate Backend — Production Grade Spring Boot

> Social platform for Olympia Tech Park, Guindy, Chennai.  
> 14,000+ employees · Sports · Lunch · Build · Gaming · Movie · Clockpoint · Anonymous

---

## Architecture: Hexagonal (Ports & Adapters)

```
src/main/java/com/parkmate/
├── domain/                    ← Pure Java — ZERO Spring/JPA imports
│   ├── model/                 ← Value Objects (UserId, Tower, Module, Visibility)
│   ├── exception/             ← Domain exceptions (DuplicateEvent, EventExpired…)
│   └── port/                  ← OUTBOUND interfaces (what domain needs from infra)
│
├── application/               ← Business logic — orchestrates domain + infra
│   ├── usecase/               ← One class per write operation (CQRS command side)
│   │   ├── RegisterUserUseCase.java
│   │   ├── CreateEventUseCase.java
│   │   ├── JoinEventUseCase.java
│   │   ├── LeaveEventUseCase.java
│   │   └── CancelEventUseCase.java
│   ├── service/               ← Read-only query services (CQRS query side, cached)
│   │   ├── EventQueryService.java
│   │   ├── ClockpointService.java
│   │   ├── AnonService.java
│   │   └── UserQueryService.java
│   └── mapper/                ← Entity → DTO mapping (no MapStruct magic, explicit)
│
├── infrastructure/            ← Spring, JPA, Security, WebSocket, Scheduler
│   ├── persistence/
│   │   ├── entity/            ← JPA entities (separate from domain models)
│   │   ├── repository/        ← Spring Data JPA interfaces
│   │   └── adapter/           ← Implement domain ports → bridge to JPA
│   ├── security/              ← JWT generation + filter
│   ├── websocket/             ← Clockpoint live chat broadcaster
│   └── scheduler/             ← Event expiry (every 60s) + DataSeeder (dev)
│
├── adapter/inbound/rest/      ← REST API surface
│   ├── controller/            ← Thin controllers — delegate to use cases
│   ├── dto/request/           ← Validated request bodies
│   ├── dto/response/          ← Response shapes
│   └── advice/                ← GlobalExceptionHandler
│
└── config/                    ← Spring config beans
    ├── SecurityConfig.java    ← JWT stateless security
    ├── WebSocketConfig.java   ← STOMP over SockJS
    ├── CacheConfig.java       ← Caffeine (swap Redis for cluster)
    └── CorsConfig.java        ← CORS for local dev
```

---

## Design Patterns Applied

| Pattern | Where | Why |
|---|---|---|
| **Hexagonal Architecture** | `domain/port` ↔ `infrastructure/adapter` | Domain stays pure, infra is swappable |
| **CQRS-lite** | `usecase/` (writes) vs `service/` (reads) | Reads are cached, writes evict cache |
| **Use Case** | One class per operation | Single Responsibility, easy to test |
| **Repository** | `JpaXxx` → `XxxAdapter` → `XxxPort` | Swap DB without touching business logic |
| **Adapter** | `*RepositoryAdapter` | Bridges domain port to JPA |
| **Value Object** | `UserId`, `Tower`, `Module` | Type safety, prevents primitive obsession |
| **Observer** | WebSocket + STOMP | Live Clockpoint chat without polling |
| **Chain of Responsibility** | `JwtAuthFilter` → Spring Security chain | Request auth pipeline |
| **Strategy** | `CacheConfig` (Caffeine/Redis) | Swap cache provider per environment |
| **Facade** | `ClockpointService` | Hides WS + DB complexity behind clean API |
| **Factory (Builder)** | All `@Builder` entities/DTOs | Immutable construction |
| **Scheduled Task** | `EventExpiryScheduler` | Auto-expire events every 60s |
| **Template Method** | `DataSeeder implements CommandLineRunner` | Dev seed hook |
| **Singleton** | `State` JS module (frontend) | Single source of truth |
| **Command** | JS `joinEvent()`, `cancelEvent()` etc. | Action encapsulation |

---

## API Endpoints

### Auth
| Method | Path | Description |
|---|---|---|
| POST | `/api/auth/register` | New user OR returning user (same deviceId = skip onboard) |

### Events
| Method | Path | Description |
|---|---|---|
| GET | `/api/events` | All active events (home feed) |
| GET | `/api/events/module/{module}` | Events by module |
| GET | `/api/events/history` | Expired/past events |
| GET | `/api/events/mine` | Current user's events |
| POST | `/api/events` | Create event (409 if duplicate activity) |
| POST | `/api/events/{id}/join` | Join event |
| DELETE | `/api/events/{id}/join` | Leave event |
| DELETE | `/api/events/{id}` | Cancel event (creator only) |
| DELETE | `/api/events/activity/{activity}` | Cancel all of activity (for "cancel old & create new") |

### Clockpoint
| Method | Path | Description |
|---|---|---|
| GET | `/api/clockpoint/presence` | Who's at Clockpoint |
| POST | `/api/clockpoint/join` | Check in |
| DELETE | `/api/clockpoint/join` | Check out |
| GET | `/api/clockpoint/chat` | Last 50 messages |
| POST | `/api/clockpoint/chat` | Send message (also broadcasts via WS) |

WebSocket: `ws://localhost:8080/ws` (SockJS)  
Subscribe: `/topic/clockpoint/chat` — live messages  
Subscribe: `/topic/clockpoint/presence` — presence updates  

### Anonymous
| Method | Path | Description |
|---|---|---|
| GET | `/api/anon` | All posts (newest first) |
| POST | `/api/anon` | Create anonymous post |
| POST | `/api/anon/{id}/relate` | Increment relate count |

### Users / Olympians
| Method | Path | Description |
|---|---|---|
| GET | `/api/users` | All members |
| GET | `/api/users/companies` | Company breakdown with stats |

---

## Edge Cases Handled

| Case | Implementation |
|---|---|
| **One-time onboarding** | `deviceId` stored in browser localStorage, never shown onboard again |
| **Returning user auto-login** | `tryAutoLogin()` on page load — calls `/auth/register` with same deviceId |
| **Duplicate event** | `CreateEventUseCase` throws `DuplicateEventException` → 409 → frontend shows modal |
| **Cancel old & create new** | Frontend calls `DELETE /events/activity/{act}` then retries create |
| **Date limit** | Max 30 days ahead enforced in `CreateEventUseCase` AND frontend |
| **Event expiry** | `EventExpiryScheduler` runs every 60s, sets `expired=true` |
| **No joins after expiry** | `JoinEventUseCase` checks `LocalDateTime.now().isAfter(eventStart)` |
| **Phone privacy** | `EventMapper` only includes phone in `JoinerResponse` if `isCreator=true` |
| **Leave event** | Users can leave anytime if not yet started |
| **Creator cannot join own** | `JoinEventUseCase` guards against it |
| **Cache eviction** | All write operations annotated with `@CacheEvict(value="events", allEntries=true)` |

---

## Running Locally

```bash
# 1. Start backend
cd parkmate-backend
mvn spring-boot:run

# Backend: http://localhost:8080
# H2 Console: http://localhost:8080/h2-console
#   JDBC URL: jdbc:h2:mem:parkmatedb
#   User: sa, Password: (empty)

# 2. Open frontend
open parkmate_base.html
# (or serve it via any HTTP server)

# 3. Test API
curl http://localhost:8080/actuator/health
```

## Production (PostgreSQL)

```bash
# In application.properties:
# 1. Comment out H2 section
# 2. Uncomment PostgreSQL section
# 3. Set env vars:
export DB_USER=postgres
export DB_PASS=yourpassword
export JWT_SECRET=your-256-bit-secret-key

# Flyway auto-runs V1__init_schema.sql on first boot
mvn spring-boot:run
```

---

## Frontend Integration

The frontend (`parkmate_base.html`) uses:
- `deviceId` from `localStorage` for one-time login detection
- `Bearer {token}` JWT in all API calls
- SockJS + STOMP for Clockpoint real-time chat
- Pure `fetch()` API — no framework dependencies

