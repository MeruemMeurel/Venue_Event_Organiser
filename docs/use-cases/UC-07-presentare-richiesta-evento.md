# UC-07 - Presentare una richiesta di evento

| Campo | Contenuto |
|---|---|
| **ID** | UC-07 |
| **Titolo** | Presentare una richiesta di evento |
| **Livello** | User goal |
| **Attore principale** | User |
| **Attori secondari** | Nessuno |
| **Descrizione** | Uno User propone un evento indicando venue, nome, descrizione e intervallo temporale. |
| **Trigger** | Lo User invia una nuova richiesta di evento. |

## Precondizioni

1. Lo User è registrato.
2. La venue desiderata esiste.

## Postcondizioni di successo

1. La richiesta è persistita nello stato `PENDING`.
2. La data di creazione è assegnata dal sistema.
3. La richiesta non è ancora chiusa né necessariamente assegnata a un handler.

## Garanzia minima

Una richiesta non valida non viene persistita.

## Flusso principale

1. Lo User seleziona una venue e inserisce nome, descrizione e date dell'evento proposto.
2. Il sistema verifica che il chiamante coincida con il richiedente, che questo esista e non sia Administrator.
3. Il sistema verifica l'esistenza della venue.
4. Il sistema valida nome, descrizione e intervallo temporale.
5. Il sistema verifica lo stato iniziale `PENDING` e, se assente, assegna l'istante corrente come data di creazione.
6. Il sistema persiste la richiesta e restituisce il suo identificativo.

## Flussi alternativi ed eccezioni

### 2a. Richiedente inesistente o non valido

1. Il sistema rifiuta la richiesta.
2. Nessun dato viene persistito.

### 2b. Richiedente Administrator

1. Il sistema rileva che il richiedente non è uno User ordinario.
2. Il sistema rifiuta la richiesta.

### 3a. Venue inesistente

1. Il sistema segnala che la venue non esiste.
2. Il caso d'uso termina senza persistenza.

### 4a. Dati descrittivi non validi

1. Il sistema rileva un nome vuoto o non compreso tra 2 e 100 caratteri, oppure una descrizione superiore a 1000 caratteri.
2. Il sistema rifiuta la richiesta.

### 4b. Intervallo temporale non valido

1. Il sistema rileva una data mancante oppure un inizio non precedente alla fine.
2. Il sistema rifiuta la richiesta.

## Regole di business correlate

- **BR-09:** il richiedente deve essere uno User ordinario, la venue deve esistere e nome e date devono essere validi.
- Una nuova richiesta entra nel ciclo di vita in stato `PENDING`.

## Test correlati

- `RequestAndGuestWorkflowTest.createRequestShouldSupplyCreationTimeWhenMissing`.
- `RequestAndGuestWorkflowTest.requesterCannotCreateOrCancelAnotherUsersRequest`.
- `SecondaryServiceValidationTest.invalidRequesterShouldBeRejected` e `invalidVenueShouldBeRejected`.
- `SecondaryServiceValidationTest.blankRequestNameShouldBeRejected` e `longRequestDescriptionShouldBeRejected`.
- `SecondaryServiceValidationTest.invertedRequestDatesShouldBeRejected`.

Il sistema ignora eventuali handler, stato, preventivo e data di chiusura ricevuti in input, impedendo che la creazione aggiri il ciclo di vita della richiesta.
