-- V7__create_moderation.sql
-- creazione tabelle di moderazione e feedback REVIEW e REPORT

CREATE TABLE REVIEW
(
    id         SERIAL PRIMARY KEY,
    user_id    INTEGER   NOT NULL,
    event_id   INTEGER   NOT NULL,
    rating     INTEGER   NOT NULL CHECK ( rating >= 1 AND rating <= 5 ),
    comment    TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_review_user FOREIGN KEY (user_id) REFERENCES "USER" (id) ON DELETE CASCADE,
    CONSTRAINT fk_review_event FOREIGN KEY (event_id) REFERENCES EVENT (id) ON DELETE CASCADE
);

CREATE TABLE REPORT
(
    id         SERIAL PRIMARY KEY,
    user_id    INTEGER   NOT NULL,
    admin_id   INTEGER   NOT NULL,
    event_id   INTEGER,
    severity   severity  NOT NULL,
    comment    TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_report_user FOREIGN KEY (user_id) REFERENCES "USER" (id) ON DELETE CASCADE,
    CONSTRAINT fk_report_admin FOREIGN KEY (admin_id) REFERENCES "USER" (id) ON DELETE CASCADE,
    CONSTRAINT fk_report_event FOREIGN KEY (event_id) REFERENCES EVENT (id) ON DELETE CASCADE
);