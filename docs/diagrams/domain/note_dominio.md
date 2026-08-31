# Note interpretative - Modello del dominio

I tre diagrammi sono viste parziali dello stesso dominio. Gli attributi sono sintetizzati e scritti in camelCase per leggibilità: non sono una copia completa delle firme Java. I package indicano la parte finale del namespace `venue.event.manager`.

Le relazioni del dominio possono essere rappresentate nel codice tramite identificativi, senza riferimenti diretti tra oggetti. Le classi marcate come riferimento ripetono entità già descritte nelle altre viste.

## Utenti e moderazione

- `User` contiene anagrafica, flag amministrativo e stato, ma non password o hash.
- `AuthService` e il repository gestiscono separatamente le credenziali PBKDF2 con salt casuale.
- Administrator e Organiser sono attori, non sottoclassi Java di `User`. Administrator è riconosciuto da `isAdmin`; Organiser è un ruolo relativo all'assegnazione a un evento.
- `Report` appartiene a `domain.model.feedback`, usa `ReportSeverity` e collega un Administrator autore a uno User segnalato. Il riferimento a un evento è facoltativo.
- Il ban impedisce nuove prenotazioni senza annullare automaticamente quelle esistenti.

## Venue e risorse

- `Venue` possiede nome, descrizione e un `Address`.
- `Address` è un record immutabile con uguaglianza per valore. La validazione dei campi obbligatori avviene in `VenueService`, non nel costruttore.
- `Resource` è la classe astratta comune a `Space`, `Equipment` e `Service`.
- `Space` appartiene a una venue e non possiede una capienza propria.
- `Equipment` possiede `totalQuantity` e può appartenere a una venue oppure essere globale: sul lato venue la cardinalità è opzionale.
- `Service` è globale, senza venue e senza campo costo.
- Capienza e prezzo del biglietto appartengono a `Event`.

## Richieste, eventi, prenotazioni e recensioni

- `EventRequest` collega richiedente ordinario, venue ed eventuale handler Administrator. Contiene proposta temporale, stato, date di creazione/chiusura e preventivo facoltativo.
- Una richiesta accettata può guidare il lavoro amministrativo, ma non è obbligatoria per creare un evento: l'Administrator può crearlo direttamente.
- `Event` ha un creatore Administrator, una venue e un eventuale Organiser. La visibilità distingue `PUBLIC` e `PRIVATE_GUEST_LIST`.
- `Booking` collega uno User a un evento e contiene uno o più ticket nel flusso applicativo di creazione. I ticket sono nominativi; l'eliminazione del booking li elimina a cascata nel database.
- `EventGuest` rappresenta una persona nella lista privata e non richiede un account `User`.
- `Review` collega autore ed evento; la coppia utente-evento è univoca. La pubblicazione richiede evento concluso e prenotazione confermata.

## Immutabilità e responsabilità

I modelli usano campi final e metodi `with...` che restituiscono copie aggiornate; `Address` è un record. Regole di business e transazioni sono coordinate dai service. Questo non implica automaticamente l'adozione di aggregate root o di un'architettura DDD completa.

## Tracciabilità verso test esistenti

| Comportamento | Test |
|---|---|
| Registrazione ordinaria e ACTIVE | `UserServiceWorkflowTest.registrationShouldStripAdminAndBannedFlagsAndHashPassword` |
| Hash PBKDF2 verificabile | `PasswordHasherTest.hashShouldCreateVerifiableEncodedPassword` |
| Salt casuale | `PasswordHasherTest.hashShouldUseADifferentSaltEachTime` |
| User bannato non prenota | `CoreServiceWorkflowTest.missingOrBannedUserShouldNotLockEvent` |
| Indirizzo e città obbligatori | `VenueServiceValidationTest.missingAddressShouldBeRejected`, `blankCityShouldBeRejected` |
| Nuovo evento DRAFT | `EventServiceWorkflowTest.createShouldAlwaysPersistDraftWithoutPublicationDate` |
| Stato e data di pubblicazione | `EventServiceWorkflowTest.publishShouldSetStatusAndCurrentPublicationTime` |
| Cancellazione coordinata dell'evento | `EventServiceWorkflowTest.cancellationShouldCascadeBeforeUpdatingEventStatus` |
| Unicità recensione nel database | `PostgresReviewConstraintIntegrationTest` |
