# UC-10 - Gestire la lista degli invitati

| Campo | Contenuto |
|---|---|
| **ID** | UC-10 |
| **Titolo** | Gestire la lista degli invitati |
| **Livello** | User goal |
| **Attore principale** | Administrator / Organiser assegnato |
| **Attori secondari** | Invited Guest |
| **Descrizione** | Un attore autorizzato inserisce, aggiorna, conferma, cancella o rimuove invitati da un evento privato. |
| **Trigger** | L'attore apre la lista invitati di un evento. |

## Precondizioni

1. L'evento esiste, non è `CANCELLED` ed è `PRIVATE_GUEST_LIST`.
2. L'attore è Administrator oppure Organiser assegnato.

## Postcondizioni di successo

1. La lista riflette l'operazione richiesta.
2. Un nuovo invitato nasce sempre `INVITED`.
3. Evento e stato non possono essere alterati indirettamente tramite l'aggiornamento anagrafico.

## Garanzia minima

Una richiesta non autorizzata o una transizione non valida non modifica la lista.

## Flusso principale

1. L'attore seleziona un evento privato.
2. Il sistema blocca l'evento e verifica l'autorizzazione.
3. L'attore fornisce nome e cognome del nuovo invitato.
4. Il sistema valida i dati e forza lo stato `INVITED`.
5. Il sistema persiste l'invitato.
6. Il sistema conferma l'operazione.

## Flussi alternativi ed eccezioni

### 2a. Evento non idoneo

1. Il sistema rileva un evento inesistente, pubblico, con altra visibilità oppure cancellato.
2. L'operazione viene rifiutata.

### 2b. Attore non autorizzato

1. Il sistema rileva che l'attore non è Administrator né Organiser assegnato.
2. L'operazione viene negata.

### 3a. Aggiornamento dei dati dell'invitato

1. L'attore modifica i dati anagrafici di un invitato esistente.
2. Il sistema conserva evento e stato memorizzati e aggiorna soltanto i dati ammessi.

### 3b. Conferma o cancellazione dell'invito

1. L'attore richiede una transizione di stato.
2. Il sistema blocca invitato ed evento e verifica la transizione.
3. Sono consentite `INVITED` → `CONFIRMED`, `INVITED` → `CANCELLED` e `CONFIRMED` → `CANCELLED`.
4. Il sistema persiste il nuovo stato.

### 3c. Rimozione dell'invitato

1. L'attore richiede la rimozione.
2. Il sistema verifica nuovamente autorizzazione ed evento, quindi elimina l'invitato.

### 3d. Dati o transizione non validi

1. Il sistema rileva dati obbligatori mancanti, una duplicazione di stato o una riattivazione da `CANCELLED`.
2. L'operazione viene rifiutata senza modifiche.

## Regole di business correlate

- **BR-25:** gli invitati appartengono soltanto a eventi `PRIVATE_GUEST_LIST` e iniziano in stato `INVITED`.
- L'Organiser può gestire esclusivamente gli eventi a lui assegnati; l'Administrator può gestirli tutti.

## Test correlati

- `RequestAndGuestWorkflowTest.privateEventInvitationShouldForceInvitedStatus`.
- `RequestAndGuestWorkflowTest.publicOrMissingEventShouldNotInsertGuest`.
- `RequestAndGuestWorkflowTest.guestManagementShouldPreserveEventAndStatusAndUseLockedTransitions`.
- `RequestAndGuestWorkflowTest.onlyAdminOrAssignedOrganiserShouldManageGuests`.
- `EventGuestServiceValidationTest` e i test guest di `StatusTransitionTest`.

## Osservazioni

L'Invited Guest è un soggetto interessato, ma non un attore software: non usa direttamente il sistema. Administrator o Organiser registrano il suo riscontro.

