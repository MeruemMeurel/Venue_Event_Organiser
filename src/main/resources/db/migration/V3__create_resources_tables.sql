-- V3__create_resources_tables.sql
-- Creazione delle tabelle per le risorse: SPACE, EQUIPMENT, SERVICE

-- Tabella SPACE: spazi all'interno di una venue
CREATE TABLE SPACE
(
    id          SERIAL PRIMARY KEY,
    venue_id    INTEGER      NOT NULL,
    name        VARCHAR(100) NOT NULL,
    description TEXT,
    CONSTRAINT fk_space_venue FOREIGN KEY (venue_id) REFERENCES VENUE (id) ON DELETE CASCADE
);

-- Tabella EQUIPMENT: attrezzature disponibili in una venue
CREATE TABLE EQUIPMENT
(
    id             SERIAL PRIMARY KEY,
    venue_id       INTEGER,
    name           VARCHAR(100) NOT NULL,
    description    TEXT,
    total_quantity INTEGER      NOT NULL DEFAULT 1 CHECK (total_quantity >= 0),
    CONSTRAINT fk_equipment_venue FOREIGN KEY (venue_id) REFERENCES VENUE (id) ON DELETE CASCADE
);

-- Tabella SERVICE: servizi offerti da una venue
CREATE TABLE SERVICE
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);


