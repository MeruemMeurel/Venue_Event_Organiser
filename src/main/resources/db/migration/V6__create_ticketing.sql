-- V6__create_ticketing.sql
-- Creazione tabelle di ticketing BOOKING, TICKET ed EVENT_GUEST

CREATE TABLE BOOKING
(
    id         SERIAL PRIMARY KEY,
    user_id    INTEGER        NOT NULL,
    event_id   INTEGER        NOT NULL,
    created_at TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status     booking_status NOT NULL DEFAULT 'PENDING_PAYMENT',
    CONSTRAINT fk_booking_user FOREIGN KEY (user_id) REFERENCES "USER" (id) ON DELETE CASCADE,
    CONSTRAINT fk_booking_event FOREIGN KEY (event_id) REFERENCES EVENT (id) ON DELETE CASCADE
);

CREATE TABLE TICKET
(
    id         SERIAL PRIMARY KEY,
    booking_id INTEGER NOT NULL,
    firstname  VARCHAR(100),
    lastname   VARCHAR(100),
    birthday   DATE,
    CONSTRAINT fk_ticket_booking FOREIGN KEY (booking_id) REFERENCES BOOKING (id) ON DELETE CASCADE
);

CREATE TABLE EVENT_GUEST
(
    id        SERIAL PRIMARY KEY,
    event_id  INTEGER      NOT NULL,
    firstname VARCHAR(100) NOT NULL,
    lastname  VARCHAR(100) NOT NULL,
    birthday  DATE,
    status    guest_status NOT NULL DEFAULT 'INVITED',
    note      TEXT,
    CONSTRAINT fk_guest_event FOREIGN KEY (event_id) REFERENCES EVENT (id) ON DELETE CASCADE
);
