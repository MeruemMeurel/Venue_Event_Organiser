-- V5__create_mappings.sql
-- Creazione delle tabelle di mapping EVENT_SPACE, REQ_SPACE, EVENT_EQUIPMENT, REQ_EQUIPMENT

-- Mapping per gli spazi
CREATE TABLE EVENT_SPACE
(
    event_id INTEGER NOT NULL,
    space_id INTEGER NOT NULL,
    PRIMARY KEY (event_id, space_id),
    CONSTRAINT fk_event_space_event FOREIGN KEY (event_id) REFERENCES EVENT (id) ON DELETE CASCADE,
    CONSTRAINT fk_event_space_space FOREIGN KEY (space_id) REFERENCES SPACE (id) ON DELETE CASCADE
);

CREATE TABLE REQ_SPACE
(
    request_id INTEGER NOT NULL,
    space_id   INTEGER NOT NULL,
    PRIMARY KEY (request_id, space_id),
    CONSTRAINT fk_req_space_request FOREIGN KEY (request_id) REFERENCES EVENT_REQUEST (id) ON DELETE CASCADE,
    CONSTRAINT fk_req_space_space FOREIGN KEY (space_id) REFERENCES SPACE (id) ON DELETE CASCADE
);

-- Mapping per le attrezzature
CREATE TABLE EVENT_EQUIPMENT
(
    event_id     INTEGER NOT NULL,
    equipment_id INTEGER NOT NULL,
    quantity     INTEGER NOT NULL CHECK ( quantity > 0 ),
    CONSTRAINT fk_event_equip_event FOREIGN KEY (event_id) REFERENCES EVENT (id) ON DELETE CASCADE,
    CONSTRAINT fk_event_equip_equip FOREIGN KEY (equipment_id) REFERENCES EQUIPMENT (id) ON DELETE CASCADE
);

CREATE TABLE REQ_EQUIPMENT
(
    request_id   INTEGER NOT NULL,
    equipment_id INTEGER NOT NULL,
    quantity     INTEGER NOT NULL CHECK (quantity > 0),
    PRIMARY KEY (request_id, equipment_id),
    CONSTRAINT fk_req_equip_req FOREIGN KEY (request_id) REFERENCES EVENT_REQUEST (id) ON DELETE CASCADE,
    CONSTRAINT fk_re_equip_equip FOREIGN KEY (equipment_id) REFERENCES EQUIPMENT (id) ON DELETE CASCADE
);

-- Mapping per i servizi
CREATE TABLE EVENT_SERVICE
(
    event_id   INTEGER NOT NULL,
    service_id INTEGER NOT NULL,
    quantity   INTEGER NOT NULL CHECK (quantity > 0),
    PRIMARY KEY (event_id, service_id),
    CONSTRAINT fk_event_service_event FOREIGN KEY (event_id) REFERENCES EVENT (id) ON DELETE CASCADE,
    CONSTRAINT fk_event_service_service FOREIGN KEY (service_id) REFERENCES SERVICE (id) ON DELETE CASCADE
);

CREATE TABLE REQ_SERVICE
(
    request_id INTEGER NOT NULL,
    service_id INTEGER NOT NULL,
    quantity   INTEGER NOT NULL CHECK (quantity > 0),
    PRIMARY KEY (request_id, service_id),
    CONSTRAINT fk_req_service_req FOREIGN KEY (request_id) REFERENCES EVENT_REQUEST (id) ON DELETE CASCADE,
    CONSTRAINT fk_req_service_service FOREIGN KEY (service_id) REFERENCES SERVICE (id) ON DELETE CASCADE
);