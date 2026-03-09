-- V9__seed_data.sql
-- Inserimento dati database

-- Inserimento admin
INSERT INTO "USER" (username, firstname, lastname, birthday, email, phone, is_admin, account_status)
VALUES ('admin_mario', 'Mario', 'Rossi', '1985-10-15', 'admin@venueorganiser.com', '3331234567', TRUE, 'ACTIVE');

-- Inserimento utente normale
INSERT INTO "USER" (username, firstname, lastname, birthday, email, phone, is_admin, account_status)
VALUES ('user_luigi', 'Luigi', 'Verdi', '1992-05-20', 'luigi.verdi@email.com', '3339876543', FALSE, 'ACTIVE');

-- Inseriamento Venue
INSERT INTO VENUE (name, description, address)
VALUES ('Palazzo degli Eventi', 'Grande struttura polivalente in centro città', 'Via Roma 1, Milano');

-- Inseriamento Spazi per Venue
INSERT INTO SPACE (venue_id, name, description)
VALUES
    (1, 'Sala Conferenze A', 'Sala con 200 posti a sedere'),
    (1, 'Salone Feste', 'Ampio salone per ricevimenti e cene aziendali');

-- Inserimento Attrezzature (Alcune legate alla Venue, altre globali)
INSERT INTO EQUIPMENT (venue_id, name, description, total_quantity)
VALUES
    (1, 'Proiettore 4K', 'Proiettore ad alta risoluzione', 3),
    (1, 'Sedie Pieghevoli', 'Sedie in plastica rigida', 500),
    (NULL, 'Impianto Audio Mobile', 'Casse e mixer trasportabili', 2); -- venue_id NULL per attrezzature mobili

-- Inserimento dei Servizi (Slegati dalle singole Venue)
INSERT INTO SERVICE (name, description)
VALUES
    ('Catering Base', 'Servizio a buffet con bevande e stuzzichini'),
    ('Servizio Sicurezza', 'Personale addetto al controllo accessi e sicurezza');