# Note Interpretative - Modello del Dominio (Class Diagrams)

Questo documento illustra le scelte di modellazione del dominio (Tactical DDD) adottate nel progetto "Venue Event Manager", organizzate per sottodomini logici per facilitare l'analisi e mantenere la manutenibilità del software.

## 1. Sottodominio Utenti, Sicurezza e Moderazione (`domain.user`, `domain.moderation`)

Il nucleo di questo sottodominio è l'entità `User`, che centralizza l'anagrafica e le credenziali di accesso.

*   **Gestione dei Ruoli e Polimorfismo:** Sebbene nel diagramma dei casi d'uso siano evidenziati attori distinti (`User`, `Administrator`, `Organiser`), a livello di codice si è scelto di centralizzare le anagrafiche nella classe base `User` dotata del flag `isAdmin`. L'attore `Organiser` non rappresenta un ruolo permanente dell'utente a livello di sistema, ma un ruolo puramente contestuale e transitorio associato a un singolo `Event`.
*   **Sicurezza delle Password:** Per garantire la massima sicurezza, la password in chiaro non viene mai memorizzata. La classe `User` conserva esclusivamente l'hash `passwordHash` calcolato tramite l'algoritmo PBKDF2 accoppiato a un salt casuale per singolo account.
*   **Gestione Moderazione (`Report` e `Severity`):** Un amministratore autenticato può emettere segnalazioni (`Report`) verso utenti ordinari con tre livelli di severità (`LOW`, `MIDDLE`, `HIGH`). L'account sanzionato può passare allo stato `BANNED`, impedendogli di effettuare prenotazioni attive pur mantenendo lo storico dei suoi dati per fini amministrativi e fiscali.

## 2. Sottodominio Venue e Risorse (`domain.venue`, `domain.resource`)

Questo sottodominio rappresenta l'inventario fisico e logico delle strutture gestite dall'applicazione.

*   **La Venue come Aggregate Root:** L'entità `Venue` agisce come aggregate root per l'indirizzo e le risorse collegate.
*   **L'indirizzo come Value Object:** La classe `Address` è modellata come Value Object immutabile. Non possiede un'identità propria e due istanze con gli stessi campi sono interscambiabili. La validazione dei dati (come la consistenza del CAP e della città) viene forzata direttamente all'interno del costruttore all'atto dell'istanziazione, garantendo che non possano esistere indirizzi in uno stato non valido.
*   **Gerarchia delle Risorse:** La classe astratta `Resource` viene estesa da tre specializzazioni concrete:
    *   `Space`: definisce spazi fisici dotati di una capienza massima (`capacity`).
    *   `Equipment`: rappresenta attrezzature fisiche caratterizzate da una quantità di base (`baseQuantity`).
    *   `Service`: descrive servizi accessori dotati di un costo specifico (`cost`).

## 3. Sottodominio Richieste ed Eventi (`domain.event`, `domain.booking`, `domain.feedback`)

Questo contesto transazionale rappresenta il nucleo operativo del sistema e ne gestisce il ciclo di vita principale.

*   **Flusso Richiesta-Evento:** Un utente ordinario inserisce una `EventRequest` temporale legata a una `Venue`. Solo in seguito all'approvazione formale da parte di un amministratore (che assume il ruolo di `handler` e stabilisce un preventivo non negativo), la richiesta passa allo stato terminale `ACCEPTED` permettendo la creazione dell'oggetto `Event` in stato `DRAFT`.
*   **La composizione Booking-Ticket:** Una prenotazione (`Booking`) funge da aggregate root per uno o più biglietti (`Ticket`). A livello di database, l'eliminazione di un booking si riflette a cascata sui relativi ticket. Ogni biglietto è strettamente nominativo e memorizza l'orario di inizio dell'evento.
*   **Feedback e Unicità:** La recensione (`Review`) associa una valutazione intera (da 1 a 5) e un commento facoltativo a un evento. Per proteggere l'integrità del sistema, è imposto un vincolo di unicità sulla coppia `(userId, eventId)` sia nel codice che a livello di database, impedendo valutazioni multiple da parte dello stesso utente.

## 4. Filosofia di Progettazione: Immutabilità e Integrità

Tutti i modelli di dominio sono stati progettati per essere **immutabili**:
1.  **Assenza di Setter:** I campi sono dichiarati `final` e possono essere valorizzati solo in fase di costruzione.
2.  **Metodi Wither:** Qualsiasi variazione di stato (es. il ban di un utente o il cambio di stato di un evento) restituisce una nuova copia dell'oggetto modificato tramite metodi dedicati (es. `withStatus`), lasciando l'istanza originale inalterata. Ciò previene effetti collaterali nello stato della memoria e semplifica la programmazione concorrente e i test unitari.

## 5. Matrice di Tracciabilità delle Regole di Business (Domain Layer)

La tabella seguente mostra come le principali regole di business (Business Rules - BR) siano presidiate dalle entità del dominio e verificate all'interno della suite di test unitari.

| Codice Regola | Descrizione Sintetica | Entità Coinvolta | Test Unitario di Riferimento (Domain) |
|---|---|---|---|
| **BR-01** | Nuovo utente registrato sempre in stato ACTIVE | `User` | `UserTest.testDefaultActiveStatus` |
| **BR-04** | Password memorizzate solo tramite hash PBKDF2 | `User` | `PasswordHasherTest.testHashConsistency` |
| **BR-07** | Utente BANNED non può effettuare prenotazioni | `User` / `Booking` | `BookingServiceValidationTest.testBannedUserCannotBook` |
| **BR-08** | Validazione formale dell'indirizzo (CAP, città) | `Address` | `AddressTest.testInvalidZipCodeRaisesException` |
| **BR-12** | Nuovo evento forzato in stato DRAFT | `Event` | `EventTest.testInitialStateIsDraft` |
| **BR-16** | Requisiti minimi di pubblicazione dell'evento | `Event` | `EventServiceValidationTest.testPublishingConditions` |
| **BR-18** | Cancellazione evento annulla booking a cascata | `Event` / `Booking` | `EventServiceWorkflowTest.testCascadeCancellation` |
| **BR-28** | Unicità della recensione per utente-evento | `Review` | `PostgresReviewConstraintIntegrationTest` |