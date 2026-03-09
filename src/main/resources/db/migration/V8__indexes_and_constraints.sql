-- V8__indexes_and_constraints.sql
-- Creazione indici e vincoli

-- Indici per EVENT
CREATE INDEX idx_event_dates ON EVENT (begin_datetime, end_datetime);
CREATE INDEX idx_event_status ON EVENT (status, visibily);
CREATE INDEX idx_event_venue ON EVENT (venue_id);
CREATE INDex idx_event_organiser ON EVENT (organiser_id);

-- Indici per EVENT_REQ
CREATE INDEX idx_request_status ON EVENT_REQUEST (status);
CREATE INDEX idx_request_requester ON EVENT_REQUEST (requester_id);
CREATE INDEX idx_request_handler ON EVENT_REQUEST (handler_id);

-- Indici per TICKETING e BOOKING
CREATE INDEX idx_booking_user ON BOOKING (user_id);
CREATE INDEX idx_booking_event ON BOOKING (event_id);
CREATE INDEX idx_ticket_booking ON TICKET (booking_id);

-- Indici per le RISORSE
CREATE INDEX idx_space_venue ON SPACE (venue_id);
CREATE INDEX idx_equipment_venue ON EQUIPMENT (venue_id)

-- Indici per la MODERAZIONE
CREATE INDEX idx_review_event ON REVIEW (event_id);
CREATE INDEX idx_report_user ON REPORT (user_id);