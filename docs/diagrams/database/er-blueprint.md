# Blueprint del modello Entity-Relationship

## 1. Scopo e fonte

Questo documento guida la costruzione del modello Entity-Relationship concettuale di Venue Event Manager. La fonte primaria è lo schema risultante dall'applicazione ordinata delle migration Flyway da `V1` a `V17`; codice Java e regole di business vengono usati per interpretare ruoli e cardinalità, non per inventare legami assenti dal database.

Il diagramma deve restare distinto dal successivo modello relazionale:

- il modello ER mostra entità, relazioni, attributi concettuali, ruoli e cardinalità;
- il modello relazionale mostrerà tabelle, colonne, tipi SQL, PK, FK, `UNIQUE`, `CHECK` e azioni referenziali.

## 2. Notazione e sorgente del diagramma

Per coerenza con gli esempi di relazione esaminati, usare la notazione ER classica di Chen:

- rettangolo: entità;
- rombo: relazione;
- ellisse: attributo;
- attributo identificatore sottolineato;
- ellisse collegata a più sotto-attributi: attributo composto;
- etichette `(min,max)` vicino alle estremità delle relazioni;
- nessuna freccia UML e nessuna crow's foot nella stessa figura.

Il sorgente master versionato è `entity-relationship-model.puml`, scritto con la sintassi Chen di PlantUML (`@startchen`). L'esportazione SVG omonima contiene tutto il modello. Font: Helvetica 12, coerente con gli altri elaborati. I rombi delle quattro relazioni di risorsa mostrano anche l'attributo `quantity`.

## 3. Entità principali

Le entità da inserire nel diagramma master sono diciotto a livello fisico. Nel modello concettuale quattro tabelle con attributo `quantity` vengono rappresentate come relazioni dotate di attributo, mentre le tabelle di mapping senza attributi diventano normali relazioni molti-a-molti.

### Utenti e strutture

#### USER

Rappresenta sia utenti ordinari sia amministratori. `Administrator` non è un'entità separata: è un ruolo determinato da `is_admin`.

Attributi consigliati nel diagramma:

- _id_;
- username;
- email;
- dati anagrafici;
- is_admin;
- account_status.

La password può essere indicata come `password_hash` nella spiegazione o nel dizionario, ma non è opportuno mostrarla come normale attributo del modello di dominio. Nello schema fisico la colonna si chiama `password` e contiene esclusivamente hash PBKDF2.

#### VENUE

Rappresenta la struttura fisica nella quale vengono organizzati gli eventi.

Attributi consigliati:

- _id_;
- name;
- description;
- address, attributo composto da street, street_number, city, postal_code, country e additional_info.

`Address` non è un'entità persistita autonoma: i suoi campi sono colonne di `VENUE`.

#### SPACE

Spazio appartenente obbligatoriamente a una venue.

Attributi: _id_, name, description.

#### EQUIPMENT

Attrezzatura disponibile globalmente o associata facoltativamente a una venue.

Attributi: _id_, name, description, total_quantity.

#### SERVICE

Servizio globale selezionabile da richieste ed eventi. Non è collegato direttamente a una venue.

Attributi: _id_, name, description.

### Richieste ed eventi

#### EVENT_REQUEST

Richiesta presentata da un utente e gestibile da un amministratore.

Attributi consigliati: _id_, name, description, begin_datetime, end_datetime, status, created_at, closed_at, quote.

#### EVENT

Evento programmato presso una venue.

Attributi consigliati: _id_, name, description, begin_datetime, end_datetime, capacity, status, visibility, ticket_price, published_at.

Non disegnare una relazione diretta tra `EVENT_REQUEST` ed `EVENT`: nello schema non esiste una FK che colleghi una richiesta all'eventuale evento creato successivamente.

#### EVENT_GUEST

Persona invitata a un evento privato. Non è necessariamente un `USER` registrato.

Attributi: _id_, firstname, lastname, birthday, status, note.

### Prenotazioni e feedback

#### BOOKING

Prenotazione effettuata da un utente per un evento.

Attributi: _id_, created_at, status, total_price.

#### TICKET

Titolo nominativo appartenente a una prenotazione.

Attributi: _id_, firstname, lastname, starts_at.

#### REVIEW

Recensione scritta da un utente per un evento.

Attributi: _id_, rating, comment, created_at.

#### REPORT

Segnalazione amministrativa rivolta a un utente e facoltativamente riferita a un evento.

Attributi: _id_, severity, comment, created_at.

## 4. Relazioni e cardinalità

Le cardinalità seguenti sono scritte dal punto di vista di ogni entità. Per esempio, in `USER (0,N) — submits — EVENT_REQUEST (1,1)`, un utente può presentare zero o molte richieste e ogni richiesta possiede esattamente un requester.

### Utenti, richieste ed eventi

| Relazione | Estremità A | Estremità B | Nome/ruoli da mostrare |
|---|---:|---:|---|
| USER presenta EVENT_REQUEST | USER `(0,N)` | EVENT_REQUEST `(1,1)` | `submits`, ruolo USER: requester |
| USER gestisce EVENT_REQUEST | USER `(0,N)` | EVENT_REQUEST `(0,1)` | `handles`, ruolo USER: handler/admin |
| VENUE è richiesta in EVENT_REQUEST | VENUE `(0,N)` | EVENT_REQUEST `(1,1)` | `requested_for` |
| VENUE ospita EVENT | VENUE `(0,N)` | EVENT `(1,1)` | `hosts` |
| USER crea EVENT | USER `(0,N)` | EVENT `(1,1)` | `creates`, ruolo USER: creator/admin |
| USER organizza EVENT | USER `(0,N)` | EVENT `(0,1)` | `organises`, ruolo USER: organiser |

Requester, handler, creator e organiser sono quattro ruoli diversi della stessa entità `USER`. Non creare entità duplicate `ADMINISTRATOR` o `ORGANISER`; usare nomi di relazione e ruoli per distinguerli. Le regole secondo cui handler e creator devono essere amministratori e organiser deve essere un utente ordinario appartengono alle business rule, non alla struttura delle FK.

### Venue e risorse

| Relazione | Estremità A | Estremità B | Note |
|---|---:|---:|---|
| VENUE contiene SPACE | VENUE `(0,N)` | SPACE `(1,1)` | ogni spazio appartiene a una venue |
| VENUE possiede EQUIPMENT | VENUE `(0,N)` | EQUIPMENT `(0,1)` | un'attrezzatura può essere globale |
| EVENT usa SPACE | EVENT `(0,N)` | SPACE `(0,N)` | M:N tramite `EVENT_SPACE` |
| EVENT_REQUEST richiede SPACE | EVENT_REQUEST `(0,N)` | SPACE `(0,N)` | M:N tramite `REQ_SPACE` |
| EVENT usa EQUIPMENT | EVENT `(0,N)` | EQUIPMENT `(0,N)` | relazione con attributo `quantity` |
| EVENT_REQUEST richiede EQUIPMENT | EVENT_REQUEST `(0,N)` | EQUIPMENT `(0,N)` | relazione con attributo `quantity` |
| EVENT usa SERVICE | EVENT `(0,N)` | SERVICE `(0,N)` | relazione con attributo `quantity` |
| EVENT_REQUEST richiede SERVICE | EVENT_REQUEST `(0,N)` | SERVICE `(0,N)` | relazione con attributo `quantity` |

Per le quattro relazioni con quantità, collegare al rombo un'ellisse `quantity`. Nel modello relazionale tali relazioni diventano `EVENT_EQUIPMENT`, `REQ_EQUIPMENT`, `EVENT_SERVICE` e `REQ_SERVICE`.

`EVENT_SPACE` e `REQ_SPACE` non possiedono attributi propri e nel modello concettuale possono restare semplici relazioni M:N. Le tabelle associative verranno mostrate nel modello relazionale.

### Prenotazioni, invitati e feedback

| Relazione | Estremità A | Estremità B | Nome/ruoli da mostrare |
|---|---:|---:|---|
| USER effettua BOOKING | USER `(0,N)` | BOOKING `(1,1)` | `makes` |
| EVENT riceve BOOKING | EVENT `(0,N)` | BOOKING `(1,1)` | `receives` |
| BOOKING contiene TICKET | BOOKING `(1,N)` applicativo | TICKET `(1,1)` | `contains` |
| EVENT include EVENT_GUEST | EVENT `(0,N)` | EVENT_GUEST `(1,1)` | `invites` |
| USER scrive REVIEW | USER `(0,N)` | REVIEW `(1,1)` | `writes` |
| EVENT riceve REVIEW | EVENT `(0,N)` | REVIEW `(1,1)` | `receives_review` |
| USER è oggetto di REPORT | USER `(0,N)` | REPORT `(1,1)` | `targets`, ruolo USER: reported user |
| USER crea REPORT | USER `(0,N)` | REPORT `(1,1)` | `creates_report`, ruolo USER: admin author |
| EVENT è riferito da REPORT | EVENT `(0,N)` | REPORT `(0,1)` | `refers_to` |

La cardinalità `BOOKING (1,N) — TICKET` esprime la regola applicativa secondo cui una prenotazione deve contenere almeno un biglietto. La sola FK del database consente strutturalmente anche zero ticket: questa differenza va dichiarata nella relazione e sarà evidenziata nel modello relazionale.

La coppia USER–EVENT per `REVIEW` è unica: un utente può recensire un determinato evento al massimo una volta. Nel modello concettuale la regola può essere annotata nel testo; nel modello relazionale verrà riportato `UNIQUE(user_id, event_id)`.

## 5. Relazioni da non disegnare

- Nessuna relazione diretta `EVENT_REQUEST — EVENT`.
- Nessuna relazione diretta `VENUE — SERVICE`: i servizi sono globali.
- Nessuna relazione diretta `USER — EVENT_GUEST`: un invitato non deve possedere un account.
- Nessuna entità `ADDRESS`: l'indirizzo è un attributo composto di `VENUE`.
- Nessuna entità separata `ADMINISTRATOR` o `ORGANISER`: sono ruoli contestuali di `USER`.
- Nessuna relazione diretta `BOOKING — VENUE`: la venue si raggiunge attraverso l'evento.
- Nessuna entità per gli enum PostgreSQL: sono domini degli attributi e verranno descritti nel dizionario.

## 6. Layout e leggibilità

Il layout viene calcolato da PlantUML. Il diagramma master contiene tutte le 12 entità concettuali, gli attributi selezionati e le 23 relazioni: va consultato a grandezza piena. Per le pagine della relazione sono disponibili tre viste tematiche, con attributi abbreviati ma cardinalità identiche al master:

- `er-requests-events.puml` / `.svg`: utenti, venue, richieste ed eventi;
- `er-resources.puml` / `.svg`: spazi, attrezzature e servizi;
- `er-booking-feedback.puml` / `.svg`: prenotazioni, invitati, recensioni e segnalazioni.

Disposizione suggerita:

```text
USER                 EVENT_REQUEST                 VENUE
 │                       │                         │
 │                       ├──────── SPACE ──────────┤
 │                       ├────── EQUIPMENT ────────┤
 │                       └──────── SERVICE         │
 │                                                 │
 ├────────────────────── EVENT ────────────────────┘
 │                         │
 │                 BOOKING ── TICKET
 │                         │
 ├──── REVIEW             EVENT_GUEST
 └──── REPORT ─────────────┘
```

Nelle relazioni multiple che partono da `USER`, i nomi dei rombi distinguono i ruoli: richiedente, gestore, creatore, organizzatore, autore della segnalazione e utente segnalato. Se il layout automatico incrocia molte linee o riduce troppo il testo, usare le viste tematiche senza cambiare le cardinalità del master.

Se il master risulta illeggibile alla dimensione della relazione, conservarlo comunque e derivare due viste tematiche senza cambiare le cardinalità:

- utenti, eventi, prenotazioni e feedback;
- richieste, venue e risorse.

## 7. Manutenzione del diagramma

1. Modificare il master `.puml` e aggiornare le eventuali viste tematiche interessate, non gli SVG generati.
2. Verificare ogni modifica contro migration, vincoli applicativi e cardinalità della sezione 4.
3. Rigenerare i quattro SVG con PlantUML, per esempio `java -jar plantuml.jar -tsvg docs/diagrams/database/*.puml` da Git Bash.
4. Controllare leggibilità e assenza di collegamenti vietati dalla sezione 5.
5. Aggiornare insieme sorgenti e SVG nella stessa PR.

## 8. Vincolo corretto prima del modello relazionale

La migration `V5__create_mappings.sql` non dichiarava una chiave primaria né un vincolo `UNIQUE(event_id, equipment_id)` per `EVENT_EQUIPMENT`, mentre le altre tabelle associative equivalenti impedivano le coppie duplicate.

Il problema è stato corretto senza modificare la migration storica: `V17__enforce_unique_event_equipment.sql` aggiunge la chiave primaria composta `(event_id, equipment_id)`. Nel modello ER concettuale la relazione resta una normale M:N con attributo `quantity`; nel modello relazionale sarà rappresentata come tabella associativa con PK composta.

## 9. Checklist di revisione

- [ ] Tutte le entità hanno un identificatore.
- [ ] Ogni relazione mostra cardinalità minime e massime su entrambi i lati.
- [ ] I quattro ruoli di USER sono distinguibili.
- [ ] EVENT_REQUEST ed EVENT non sono collegati direttamente.
- [ ] EVENT_GUEST non è collegato a USER.
- [ ] SERVICE non è collegato direttamente a VENUE.
- [ ] L'indirizzo è attributo composto di VENUE.
- [ ] Le quattro relazioni con risorse mostrano `quantity` quando presente nello schema.
- [ ] BOOKING–TICKET esplicita la cardinalità applicativa 1:N.
- [ ] REVIEW riporta nel testo la regola di unicità USER–EVENT.
- [ ] Nessuna linea attraversa entità, rombi o cardinalità.
- [ ] Il diagramma è leggibile nell'esportazione SVG, non soltanto nell'editor.
