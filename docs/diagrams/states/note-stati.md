# Note interpretative - Macchine a stati

Le etichette seguono la forma `operazione [guardia] / effetto`. I dettagli e i flussi di errore sono nei template dei casi d'uso. Uno stato terminale non implica l'eliminazione del record: il simbolo finale associato a `delete...()` indica invece la fine dell'esistenza dell'entità.

## Evento

- La creazione forza `DRAFT` e rimuove un'eventuale data di pubblicazione ricevuta in input.
- Sono ammesse `DRAFT → CONFIRMED`, `CONFIRMED → PUBLISHED` e la cancellazione da `DRAFT`, `CONFIRMED` o `PUBLISHED`.
- La gestione richiede un Administrator oppure l'Organiser assegnato, anche quando la guardia non è ripetuta nell'etichetta.
- La pubblicazione richiede inizio futuro, venue esistente e capienza positiva; assegna `publishedAt`.
- La cancellazione annulla booking e invitati attivi nella stessa transazione e imposta `CANCELLED`. Non effettua rimborsi.
- `CANCELLED` non permette riattivazione. L'eliminazione fisica, disponibile nel service, è omessa da questa vista delle transizioni di stato.

## Prenotazione

- La creazione verifica utente, evento pubblico pubblicato e futuro, biglietti e capienza, bloccando l'evento prima del conteggio. Dopo i controlli persiste booking e ticket atomicamente.
- La prenotazione nasce `PENDING_PAYMENT` e può passare a `CONFIRMED` oppure `CANCELLED`; anche una prenotazione confermata può essere cancellata.
- Conferma, cancellazione ed eliminazione richiedono il proprietario della prenotazione o un Administrator.
- La cancellazione dell'evento annulla le prenotazioni attive, incluse quelle `PENDING_PAYMENT`.
- Il nome `PENDING_PAYMENT` rappresenta una fase applicativa: non esistono gateway di pagamento, scadenza automatica, verifica dell'incasso o rimborso.

## Richiesta di evento

- La creazione richiede che il chiamante coincida con il richiedente ordinario e forza `PENDING`, senza handler, preventivo o data di chiusura.
- Un Administrator può assegnare un handler amministrativo mentre la richiesta è pendente. L'assegnazione non cambia lo stato.
- Solo l'handler assegnato può accettare o rifiutare; l'accettazione richiede un preventivo presente e non negativo.
- Solo il richiedente può cancellare la propria richiesta pendente.
- Le tre chiusure assegnano `closedAt`; `ACCEPTED`, `REJECTED` e `CANCELLED` non consentono ulteriori transizioni di stato.
- Aggiornamento descrittivo ed eliminazione della richiesta pendente sono riservati al richiedente e omessi dalla vista degli stati.

## Invitato

- L'inserimento riguarda un evento `PRIVATE_GUEST_LIST` non cancellato e forza `INVITED`.
- Sono ammesse `INVITED → CONFIRMED`, `INVITED → CANCELLED` e `CONFIRMED → CANCELLED`.
- Le operazioni sono eseguite da un Administrator o dall'Organiser assegnato, non direttamente dall'invitato.
- La cancellazione dell'evento annulla gli inviti attivi.
- `CANCELLED` non consente riattivazione; la rimozione fisica è omessa dalla vista degli stati.

## Account

- La registrazione crea uno User ordinario `ACTIVE`.
- Ban e riattivazione richiedono credenziali valide di un Administrator; non sono consentiti su se stesso o su altri Administrator.
- Il ban impedisce nuove prenotazioni, ma non cancella quelle già esistenti.
- L'eliminazione richiede la password dello User ed è consentita sia da `ACTIVE` sia da `BANNED`.

## Test di riferimento

- `StatusTransitionTest`: transizioni ammesse e rifiutate di eventi, booking e invitati.
- `EventServiceWorkflowTest.cancellationShouldCascadeBeforeUpdatingEventStatus`: cancellazione coordinata dell'evento.
- `CoreServiceWorkflowTest.missingOrBannedUserShouldNotLockEvent`: nuove prenotazioni rifiutate per account bannati o inesistenti.
- `UserServiceWorkflowTest.adminShouldBanAndUnbanOrdinaryUser`: ban e riattivazione.
- `RequestAndGuestWorkflowTest.onlyAssignedHandlerCanAcceptOrRejectRequest`: autorizzazione dell'handler.
- `RequestAndGuestWorkflowTest.onlyAdminOrAssignedOrganiserShouldManageGuests`: autorizzazione sugli invitati.
