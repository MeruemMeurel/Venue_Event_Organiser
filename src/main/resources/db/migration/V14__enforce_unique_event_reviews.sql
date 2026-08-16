-- A user may review a given event at most once.
ALTER TABLE REVIEW
    ADD CONSTRAINT uq_review_user_event UNIQUE (user_id, event_id);
