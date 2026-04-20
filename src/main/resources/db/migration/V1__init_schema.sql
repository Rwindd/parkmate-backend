-- ParkMate Schema — PostgreSQL Production
-- Run automatically by Flyway when spring.flyway.enabled=true

CREATE TABLE IF NOT EXISTS pm_users (
    id             BIGSERIAL PRIMARY KEY,
    name           VARCHAR(100)  NOT NULL,
    company        VARCHAR(100)  NOT NULL,
    tower          VARCHAR(20)   NOT NULL,
    floor          VARCHAR(10)   NOT NULL,
    phone          VARCHAR(20),
    device_id      VARCHAR(255)  NOT NULL UNIQUE,
    at_clockpoint  BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at     TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_users_device ON pm_users(device_id);
CREATE INDEX idx_users_company ON pm_users(company);

CREATE TABLE IF NOT EXISTS pm_events (
    id             BIGSERIAL PRIMARY KEY,
    module         VARCHAR(50)   NOT NULL,
    activity       VARCHAR(100)  NOT NULL,
    activity_icon  VARCHAR(20),
    title          VARCHAR(200)  NOT NULL,
    description    TEXT,
    event_date     DATE          NOT NULL,
    event_time     TIME          NOT NULL,
    location       VARCHAR(200)  NOT NULL,
    spots          INT           NOT NULL,
    visibility     VARCHAR(20)   NOT NULL DEFAULT 'all',
    creator_id     BIGINT        NOT NULL REFERENCES pm_users(id),
    active         BOOLEAN       NOT NULL DEFAULT TRUE,
    expired        BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at     TIMESTAMP     NOT NULL DEFAULT NOW(),
    cancelled_at   TIMESTAMP
);

CREATE INDEX idx_events_module        ON pm_events(module);
CREATE INDEX idx_events_creator       ON pm_events(creator_id);
CREATE INDEX idx_events_date          ON pm_events(event_date);
CREATE INDEX idx_events_active        ON pm_events(active, expired);

CREATE TABLE IF NOT EXISTS pm_event_joiners (
    event_id BIGINT NOT NULL REFERENCES pm_events(id) ON DELETE CASCADE,
    user_id  BIGINT NOT NULL REFERENCES pm_users(id)  ON DELETE CASCADE,
    PRIMARY KEY (event_id, user_id)
);

CREATE TABLE IF NOT EXISTS pm_anon_posts (
    id            BIGSERIAL PRIMARY KEY,
    text          TEXT          NOT NULL,
    relate_count  INT           NOT NULL DEFAULT 0,
    comment_count INT           NOT NULL DEFAULT 0,
    created_at    TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_anon_created ON pm_anon_posts(created_at DESC);

CREATE TABLE IF NOT EXISTS pm_chat_messages (
    id             BIGSERIAL PRIMARY KEY,
    sender_id      BIGINT REFERENCES pm_users(id) ON DELETE SET NULL,
    text           TEXT          NOT NULL,
    system_message BOOLEAN       NOT NULL DEFAULT FALSE,
    sent_at        TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_chat_sent ON pm_chat_messages(sent_at);
