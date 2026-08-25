# UC-11 - Prenotare biglietti

| Campo | Contenuto |
|---|---|
| **ID** | UC-11 |
| **Titolo** | Prenotare biglietti |
| **Livello** | User goal |
| **Attore principale** | User |
| **Attori secondari** | Nessuno |
| **Descrizione** | Uno User richiede uno o più biglietti nominativi per un evento pubblico, pubblicato e non ancora iniziato. Il sistema verifica utente, evento, dati dei biglietti e capienza residua, quindi crea atomicamente la prenotazione e i relativi biglietti. |
| **Trigger** | Lo User richiede di prenotare uno o più biglietti per un evento selezionato. |

## Precondizioni

1. Lo User dispone di un account registrato e identificabile dal sistema.
2. L'evento selezionato esiste.
3. Lo User fornisce almeno un biglietto con nome e cognome dell'intestatario.

Le condizioni relative allo stato dell'account, alla prenotabilità dell'evento e alla capienza vengono nuovamente verificate dal sistema durante l'operazione, perché possono cambiare nel tempo.

## Postcondizioni di successo

1. È persistita una nuova prenotazione associata allo User e all'evento.
2. La prenotazione si trova nello stato `PENDING_PAYMENT`.
3. Il prezzo totale equivale al numero di biglietti moltiplicato per il prezzo unitario dell'evento; per un evento gratuito il totale è zero.
4. Tutti i biglietti richiesti sono persistiti e associati alla nuova prenotazione.
5. Ogni biglietto riporta come orario di inizio quello dell'evento.

## Garanzia minima

Se l'operazione non può essere completata, non viene persistita alcuna prenotazione parziale e non viene inserito alcun biglietto. Tutte le scritture appartengono alla stessa transazione.

## Flusso principale

1. Lo User seleziona un evento e richiede uno o più biglietti nominativi.
2. Il sistema verifica che la lista dei biglietti non sia nulla o vuota e che ogni biglietto contenga nome e cognome validi.
3. Il sistema verifica che lo User esista e che il suo account non sia `BANNED`.
4. Il sistema carica e blocca l'evento per impedire che prenotazioni concorrenti modifichino contemporaneamente la disponibilità considerata.
5. Il sistema verifica che l'evento sia `PUBLISHED`, abbia visibilità `PUBLIC` e non sia ancora iniziato.
6. Il sistema conta i biglietti già emessi per l'evento.
7. Il sistema verifica che la capienza residua sia sufficiente per tutti i biglietti richiesti.
8. Il sistema calcola il prezzo totale della prenotazione.
9. Il sistema crea la prenotazione nello stato `PENDING_PAYMENT` e la persiste.
10. Il sistema associa ogni biglietto alla nuova prenotazione e gli assegna l'orario iniziale dell'evento.
11. Il sistema persiste tutti i biglietti richiesti.
12. Il sistema conferma la transazione e restituisce la prenotazione creata.

## Flussi alternativi ed eccezioni

### 2a. Lista dei biglietti assente o vuota

1. Il sistema rifiuta la richiesta come non valida.
2. Il caso d'uso termina senza modificare i dati persistiti.

### 2b. Dati di un biglietto non validi

1. Il sistema rileva un biglietto nullo, già associato a una prenotazione oppure privo di nome o cognome validi.
2. Il sistema rifiuta l'intera richiesta.
3. Il caso d'uso termina senza modificare i dati persistiti.

### 3a. User inesistente

1. Il sistema segnala che lo User non esiste.
2. L'evento non viene bloccato e il caso d'uso termina.

### 3b. User bannato

1. Il sistema nega l'operazione.
2. L'evento non viene bloccato e il caso d'uso termina.

### 4a. Evento inesistente

1. Il sistema segnala che l'evento non esiste.
2. Il caso d'uso termina senza modificare i dati persistiti.

### 5a. Evento non prenotabile

1. Il sistema rileva che l'evento non è `PUBLISHED`, non è `PUBLIC` oppure è già iniziato.
2. Il sistema rifiuta la richiesta.
3. Il caso d'uso termina senza modificare i dati persistiti.

### 7a. Capienza insufficiente

1. Il sistema rileva che i posti residui non sono sufficienti per tutti i biglietti richiesti.
2. Il sistema rifiuta l'intera richiesta: non è prevista una prenotazione parziale.
3. Il caso d'uso termina senza inserire booking o ticket.

### 9a-11a. Errore durante la persistenza

1. Il sistema annulla la transazione.
2. L'eventuale prenotazione e gli eventuali biglietti inseriti durante il tentativo non restano persistiti.
3. Il caso d'uso termina con un errore.

## Regole di business correlate

- **BR-07:** un utente `BANNED` non può prenotare eventi.
- **BR-19:** sono prenotabili soltanto eventi `PUBLISHED`, `PUBLIC` e non ancora iniziati.
- **BR-20:** una prenotazione deve contenere almeno un biglietto valido e nominativo.
- **BR-21:** il prezzo totale è `numero biglietti × prezzo unitario`; un prezzo nullo equivale a zero.
- **BR-22:** il controllo della capienza e la scrittura di booking e ticket avvengono nella stessa transazione.
- **BR-23:** l'evento viene letto con lock prima del conteggio dei biglietti per impedire overbooking concorrente.
- **BR-24:** ogni ticket viene associato alla prenotazione appena creata e riceve l'orario iniziale dell'evento.

## Test correlati

- `BookingServiceValidationTest`: verifica evento prenotabile, lista e dati dei biglietti.
- `CoreServiceWorkflowTest.bookingShouldCalculateTotalAndPersistAssignedTickets`: verifica stato iniziale, prezzo totale e associazione dei ticket.
- `CoreServiceWorkflowTest.freeEventShouldProduceZeroPriceBooking`: verifica gli eventi gratuiti.
- `CoreServiceWorkflowTest.overbookingShouldNotWriteBookingOrTickets`: verifica capienza insufficiente e assenza di scritture parziali.
- `CoreServiceWorkflowTest.missingOrBannedUserShouldNotLockEvent`: verifica User inesistente o bannato.

## Osservazioni

Lo stato `PENDING_PAYMENT` rappresenta soltanto una fase del ciclo applicativo della prenotazione. Il sistema non integra un gateway di pagamento: la successiva conferma appartiene a UC-12.1 e non al presente caso d'uso.
