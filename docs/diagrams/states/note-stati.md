# Note Interpretative - Macchine a Stati (State Diagrams)

Questo documento analizza le regole di transizione di stato, i vincoli di sicurezza (guardie) e gli effetti collaterali che governano il ciclo di vita delle entità principali del sistema.

## 1. Stato dell'Evento (`EventStatus`)

Il ciclo di vita dell'evento è progettato per garantire che nessun evento incompleto o non approvato venga esposto al pubblico per le prenotazioni.

*   **Stato Iniziale (`DRAFT`):** Ogni evento appena creato viene forzato nello stato iniziale `DRAFT`, indipendentemente dai parametri ricevuti in input dal servizio.
*   **Transizioni Ammesse:**
    *   `DRAFT` → `CONFIRMED`: L'evento viene validato e confermato internamente.
    *   `DRAFT` → `CANCELLED`: Cancellazione preventiva dell'evento.
    *   `CONFIRMED` → `PUBLISHED`: Transizione che rende l'evento visibile agli utenti e prenotabile. All'atto del passaggio viene valorizzato il timestamp `published_at`.
    *   `CONFIRMED` / `PUBLISHED` → `CANCELLED`: L'evento viene cancellato.
*   **Effetti Collaterali della Cancellazione (`CANCELLED`):** Trattandosi di uno stato terminale irreversibile, la cancellazione di un evento pubblicato attiva una transazione atomica che provvede a annullare d'ufficio tutte le prenotazioni attive (`BookingStatus.CANCELLED`) e gli inviti degli ospiti (`EventGuestStatus.CANCELLED`).

## 2. Stato della Prenotazione (`BookingStatus`)

La prenotazione regola la riserva dei posti (biglietti) ed evita l'overbooking tramite lock transazionali.

*   **Stato Iniziale (`PENDING_PAYMENT`):** La prenotazione nasce in questo stato transitorio mentre il sistema verifica la disponibilità residua di posti presso la venue e applica un lock `FOR UPDATE` sul record dell'evento.
*   **Transizioni Ammesse:**
    *   `PENDING_PAYMENT` → `CONFIRMED`: L'owner dello stabilimento o della venue conferma manualmente la ricezione del pagamento, consolidando la riserva dei biglietti.
    *   `PENDING_PAYMENT` → `CANCELLED`: Il pagamento fallisce, scade per timeout o viene annullato manualmente.
    *   `CONFIRMED` → `CANCELLED`: Annullamento tardivo della prenotazione da parte dell'utente o a causa della cancellazione complessiva dell'evento.

## 3. Stato della Richiesta di Evento (`EventRequestStatus`)

Governa il flusso burocratico di approvazione delle proposte di eventi inviate dagli utenti ordinari.

*   **Stato Iniziale (`PENDING`):** La proposta è registrata e in attesa di presa in carico.
*   **Transizioni Ammesse verso Stati Terminali:**
    *   `PENDING` → `ACCEPTED`: Un amministratore prende in carico la richiesta (`handlerId` valorizzato), inserisce un preventivo valido non negativo e approva formalmente la richiesta. Viene registrata la data di chiusura `closed_at`.
    *   `PENDING` → `REJECTED`: L'amministrazione rifiuta la proposta inserendo la data di chiusura.
    *   `PENDING` → `CANCELLED`: L'utente che ha presentato la proposta decide di ritirarla prima che venga valutata.

## 4. Stato dell'Ospite Invitato (`EventGuestStatus`)

Gestisce l'accesso e la pianificazione degli inviti nominativi per eventi di tipo privato con lista ospiti.

*   **Stato Iniziale (`INVITED`):** L'ospite viene registrato nella lista associata all'evento privato.
*   **Transizioni Ammesse:**
    *   `INVITED` → `CONFIRMED`: L'ospite accetta formalmente l'invito.
    *   `INVITED` → `CANCELLED`: L'ospite rifiuta l'invito.
    *   `CONFIRMED` → `CANCELLED`: L'ospite si cancella in un secondo momento o l'evento viene annullato dall'organizzatore.

## 5. Stato dell'Account Utente (`AccountStatus`)

Regola i permessi operativi di un utente all'interno della piattaforma.

*   **Stato Iniziale (`ACTIVE`):** Ogni nuovo utente registrato è attivo e può utilizzare tutte le funzionalità associate al proprio ruolo.
*   **Transizioni Ammesse:**
    *   `ACTIVE` → `BANNED`: Un amministratore autenticato, a seguito di segnalazioni o violazioni delle linee guida, decide di sanzionare l'utente. Questa transizione cancella automaticamente a cascata tutte le prenotazioni future effettuate dall'utente in questione.
    *   `BANNED` → `ACTIVE`: L'amministratore decide di riattivare l'account dopo una verifica delle credenziali.