# UC-08 - Gestire una richiesta di evento

| Campo | Contenuto |
|---|---|
| **ID** | UC-08 |
| **Titolo** | Gestire una richiesta di evento |
| **Livello** | User goal |
| **Attore principale** | Administrator |
| **Attori secondari** | User richiedente |
| **Descrizione** | Un Administrator prende in carico una richiesta pendente e la accetta o la rifiuta; il richiedente può cancellarla finché è pendente. |
| **Trigger** | Un Administrator seleziona una richiesta `PENDING` da gestire. |

## Precondizioni

1. La richiesta esiste ed è `PENDING`.
2. Per l'accettazione è stato assegnato un handler Administrator.

## Postcondizioni di successo

1. La richiesta è `ACCEPTED`, `REJECTED` oppure `CANCELLED`.
2. Una richiesta accettata contiene un preventivo non negativo.
3. La data di chiusura è assegnata dal sistema.

## Garanzia minima

In caso di errore la richiesta conserva stato, handler, preventivo e data di chiusura precedenti.

## Flusso principale

1. L'Administrator seleziona una richiesta pendente.
2. Il sistema blocca la richiesta per evitare aggiornamenti concorrenti.
3. L'Administrator assegna un handler Administrator, se non già presente.
4. L'Administrator sceglie di accettare la richiesta e indica il preventivo.
5. Il sistema verifica stato, handler e preventivo.
6. Il sistema imposta lo stato `ACCEPTED` e la data di chiusura corrente.
7. Il sistema persiste e conferma la transazione.

## Flussi alternativi ed eccezioni

### 3a. Handler inesistente o non Administrator

1. Il sistema rifiuta l'assegnazione.
2. La richiesta rimane invariata.

### 4a. Rifiuto della richiesta

1. L'Administrator sceglie di rifiutare la richiesta pendente.
2. Il sistema imposta `REJECTED` e la data di chiusura corrente.
3. Il flusso riprende dal passo 7.

### 4b. Cancellazione da parte del richiedente

1. Il richiedente cancella la propria richiesta ancora pendente.
2. Il sistema imposta `CANCELLED` e la data di chiusura corrente.
3. Il flusso riprende dal passo 7.

### 5a. Preventivo non valido

1. Il sistema rileva un preventivo assente o negativo.
2. L'accettazione viene rifiutata.

### 5b. Richiesta già chiusa

1. Il sistema rileva che la richiesta non è più `PENDING`.
2. La nuova transizione viene rifiutata come conflitto.

## Regole di business correlate

- **BR-10:** soltanto uno User Administrator può essere assegnato come handler.
- **BR-11:** l'accettazione richiede stato `PENDING`, handler assegnato e preventivo non negativo.
- Le transizioni terminali sono `ACCEPTED`, `REJECTED` e `CANCELLED`.

## Test correlati

- `RequestAndGuestWorkflowTest.handlerAssignmentShouldRequireAnAdminAndPreserveOtherFields`.
- `RequestAndGuestWorkflowTest.nonAdminHandlerShouldNotUpdateRequest`.
- `RequestAndGuestWorkflowTest.acceptRejectAndCancelShouldClosePendingRequest`.
- `RequestAndGuestWorkflowTest.acceptedRequestCannotBeClosedAgain`.

## Osservazioni

Le operazioni verificano il ruolo dell'handler assegnato, ma non ricevono l'identità autenticata del chiamante. Non possono quindi dimostrare che chi assegna, accetta o rifiuta sia davvero Administrator, né che chi cancella sia il richiedente.

