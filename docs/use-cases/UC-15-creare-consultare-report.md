# UC-15 - Creare e consultare report

| Campo | Contenuto |
|---|---|
| **ID** | UC-15 |
| **Titolo** | Creare e consultare report |
| **Livello** | User goal |
| **Attore principale** | Administrator |
| **Attori secondari** | User segnalato |
| **Descrizione** | Un Administrator registra un report relativo a uno User ordinario, eventualmente collegato a un evento, e consulta i report tramite diversi filtri. |
| **Trigger** | L'Administrator richiede di registrare o consultare un report. |

## Precondizioni

1. L'Administrator indicato esiste e possiede privilegi amministrativi.
2. Lo User segnalato esiste e non è Administrator.

## Postcondizioni di successo

1. In creazione, il report è persistito con severità, commento, riferimenti e data assegnata dal sistema.
2. In consultazione, il sistema restituisce i report corrispondenti al filtro senza modificare dati.

## Garanzia minima

Un report non valido non viene persistito; una consultazione non produce scritture.

## Flusso principale

1. L'Administrator seleziona lo User da segnalare.
2. Indica severità, eventuale commento ed eventuale evento correlato.
3. Il sistema verifica che il chiamante sia Administrator e coincida con l'autore indicato.
4. Il sistema verifica che il bersaglio sia uno User ordinario.
5. Se presente, il sistema verifica l'esistenza dell'evento.
6. Il sistema valida severità e commento.
7. Il sistema assegna la data corrente e persiste il report.
8. Il sistema restituisce l'identificativo generato.

## Flussi alternativi ed eccezioni

### 1a. Consultazione dei report

1. L'Administrator sceglie un criterio: identificativo, User, Administrator autore, evento, severità o loro combinazione supportata.
2. Il sistema verifica che il chiamante sia Administrator ed esegue una transazione read-only.
3. Il sistema restituisce i risultati corrispondenti.

### 3a. Autore inesistente o non Administrator

1. Il sistema segnala l'autore mancante oppure nega l'operazione per ruolo insufficiente.
2. Nessun report viene inserito.

### 4a. Bersaglio inesistente o Administrator

1. Il sistema segnala lo User mancante oppure rifiuta la segnalazione di un Administrator.
2. Nessun report viene inserito.

### 5a. Evento correlato inesistente

1. Il sistema segnala che l'evento indicato non esiste.
2. Il caso d'uso termina senza persistenza.

### 6a. Contenuto non valido

1. Il sistema rileva severità assente oppure commento superiore a 1000 caratteri.
2. La richiesta viene rifiutata.

## Regole di business correlate

- **BR-29:** un report è creato da un Administrator, riguarda uno User ordinario e può riferirsi facoltativamente a un evento esistente.
- La severità è obbligatoria; il commento facoltativo non può superare 1000 caratteri.

## Test correlati

- `SecondaryServiceWorkflowTest.adminShouldCreateReportWithServerCreationTime`.
- `SecondaryServiceWorkflowTest.reportShouldRequireAdminAndNonAdminTarget`.
- `SecondaryServiceWorkflowTest.reportOperationsShouldRequireAuthenticatedAdminIdentity`.
- I test di validazione dei report in `SecondaryServiceValidationTest`.
- `ServiceQueryDelegationTest` verifica la delega delle interrogazioni supportate.

Creazione, consultazione, aggiornamento ed eliminazione ricevono separatamente l'identità del chiamante e sono riservate agli Administrator. In creazione, il chiamante deve inoltre coincidere con l'autore registrato nel report.

