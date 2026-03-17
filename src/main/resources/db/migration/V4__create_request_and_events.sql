-- V4__create_requests_and_events.sql
-- Creazione delle tabelle EVENT_REQUEST ed EVENT

CREATE TABLE EVENT_REQUEST
(
    id             SERIAL PRIMARY KEY,
    requester_id   INTEGER        NOT NULL,
    handler_id     INTEGER,
    venue_id       INTEGER        NOT NULL,
    name           VARCHAR(100)   NOT NULL,
    description    TEXT,
    begin_datetime TIMESTAMP      NOT NULL,
    end_datetime   TIMESTAMP      NOT NULL,
    status         request_status NOT NULL DEFAULT 'PENDING',
    created_at     TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    closed_at      TIMESTAMP,
    quote          NUMERIC(10, 2),
    CONSTRAINT fk_request_requester FOREIGN KEY (requester_id) REFERENCES "USER"(id) ON DELETE CASCADE,
    CONSTRAINT fk_request_handler FOREIGN KEY (handler_id) REFERENCES "USER"(id) ON DELETE SET NULL,
    CONSTRAINT fk_request_venue FOREIGN KEY (venue_id) REFERENCES VENUE(id) ON DELETE CASCADE
);

CREATE TABLE EVENT
(
    id              SERIAL PRIMARY KEY,
    venue_id        INTEGER      NOT NULL,
    organiser_id    INTEGER,
    creator_id      INTEGER        NOT NULL,
    name            VARCHAR(100) NOT NULL,
    description     TEXT,
    begin_datetime  TIMESTAMP    NOT NULL,
    end_datetime    TIMESTAMP    NOT NULL,
    poster_filepath TEXT,
    capacity        INTEGER      NOT NULL CHECK (CAPACITY > 0),
    status          event_status NOT NULL DEFAULT 'CONFIRMED',
    visibility      visibility   NOT NULL DEFAULT 'PUBLIC',
    ticket_price    NUMERIC(10, 2),
    published_at    TIMESTAMP,
    CONSTRAINT fk_event_venue FOREIGN KEY (venue_id) REFERENCES VENUE(id) ON DELETE CASCADE,
    CONSTRAINT fk_event_organiser FOREIGN KEY (organiser_id) REFERENCES "USER"(id) ON DELETE SET NULL,
    CONSTRAINT fk_event_creator FOREIGN KEY (creator_id) REFERENCES "USER"(id)
);
