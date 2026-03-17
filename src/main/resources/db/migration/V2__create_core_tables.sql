-- V2__create_core_tables.sql
-- Creazione delle tabelle core: USER e VENUE

-- Tabella USER: contiene tutti gli utenti del sistema
CREATE TABLE "USER"
(
    id             SERIAL PRIMARY KEY,
    username       VARCHAR(50)    NOT NULL UNIQUE,
    firstname      VARCHAR(100)   NOT NULL,
    lastname       VARCHAR(100)   NOT NULL,
    birthday       DATE           NOT NULL,
    email          VARCHAR(100)   NOT NULL UNIQUE,
    phone          VARCHAR(20),
    is_admin       BOOLEAN        NOT NULL DEFAULT FALSE,
    account_status account_status NOT NULL DEFAULT 'ACTIVE'
);

-- Tabella VENUE: spazi disponibili per l'organizzazione di eventi
CREATE TABLE VENUE
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    description TEXT,
    address     VARCHAR(200) NOT NULL
);

