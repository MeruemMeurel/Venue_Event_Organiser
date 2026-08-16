ALTER TABLE EVENT_REQUEST
    ADD CONSTRAINT ck_event_request_date_range CHECK (end_datetime > begin_datetime),
    ADD CONSTRAINT ck_event_request_quote CHECK (quote IS NULL OR quote >= 0);

ALTER TABLE EVENT
    ADD CONSTRAINT ck_event_date_range CHECK (end_datetime > begin_datetime),
    ADD CONSTRAINT ck_event_ticket_price CHECK (ticket_price IS NULL OR ticket_price >= 0),
    ADD CONSTRAINT ck_event_publication_state CHECK (
        (status = 'PUBLISHED' AND published_at IS NOT NULL)
        OR (status IN ('DRAFT', 'CONFIRMED') AND published_at IS NULL)
        OR status = 'CANCELLED'
    );

ALTER TABLE BOOKING
    ADD CONSTRAINT ck_booking_total_price CHECK (total_price >= 0);
