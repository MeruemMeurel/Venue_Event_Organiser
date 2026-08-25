# UC-13 - Pubblicare una recensione

| Campo | Contenuto |
|---|---|
| **ID** | UC-13 |
| **Titolo** | Pubblicare una recensione |
| **Livello** | User goal |
| **Attore principale** | User |
| **Attori secondari** | Nessuno |
| **Descrizione** | Uno User che ha partecipato a un evento concluso pubblica una sola recensione con voto ed eventuale commento. |
| **Trigger** | Lo User richiede di recensire un evento concluso. |

## Precondizioni

1. Lo User possiede una prenotazione `CONFIRMED` per l'evento.
2. L'evento esiste ed è terminato.

## Postcondizioni di successo

1. È persistita una recensione associata allo User e all'evento.
2. La data di creazione è assegnata dal sistema.

## Garanzia minima

Se un controllo fallisce non viene inserita alcuna recensione.

## Flusso principale

1. Lo User seleziona un evento terminato e fornisce voto ed eventuale commento.
2. Il sistema verifica che l'attore coincida con l'autore indicato.
3. Il sistema valida voto e commento.
4. Il sistema verifica l'esistenza e la conclusione dell'evento.
5. Il sistema verifica una prenotazione `CONFIRMED` dello User per l'evento.
6. Il sistema verifica che lo User non abbia già recensito l'evento.
7. Il sistema assegna la data corrente e persiste la recensione.

## Flussi alternativi ed eccezioni

### 2a. Attore diverso dall'autore

1. Il sistema nega l'operazione.
2. Nessuna recensione viene inserita.

### 3a. Voto o commento non validi

1. Il sistema rileva un voto fuori dall'intervallo 1-5 o un commento superiore a 1000 caratteri.
2. La richiesta viene rifiutata.

### 4a. Evento inesistente o non concluso

1. Il sistema segnala l'evento mancante oppure non ancora terminato.
2. Il caso d'uso termina senza persistenza.

### 5a. Partecipazione non dimostrata

1. Il sistema non trova una prenotazione `CONFIRMED` per lo User e l'evento.
2. La pubblicazione viene negata.

### 6a. Recensione già presente

1. Il sistema rileva una recensione dello stesso User per lo stesso evento.
2. Il sistema rifiuta il duplicato come conflitto.

## Regole di business correlate

- **BR-26:** il voto deve essere compreso tra 1 e 5 e il commento non può superare 1000 caratteri.
- **BR-27:** può recensire soltanto chi possiede una prenotazione `CONFIRMED` per un evento concluso.
- Uno User può pubblicare una sola recensione per evento e soltanto a proprio nome.

## Test correlati

- `SecondaryServiceWorkflowTest.attendedUserShouldCreateReviewWithServerCreationTime`.
- `SecondaryServiceWorkflowTest.reviewShouldRequireAttendanceAndUniqueness`.
- `SecondaryServiceWorkflowTest.onlyAuthorShouldCreateUpdateOrDeleteReview`.
- I test di validazione delle recensioni in `SecondaryServiceValidationTest`.
- `PostgresReviewConstraintIntegrationTest` verifica il vincolo di unicità sul database.

