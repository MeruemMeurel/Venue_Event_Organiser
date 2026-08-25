# Specifiche condivise - Venue Event Manager

> Documento operativo per allineare requisiti, diagrammi UML, modello ER e relazione.
> Versione iniziale ricavata dal codice, dalle migration Flyway e dai test presenti in `v1.0.1`.

## 1. Scopo del documento

Questo file è la fonte di verità condivisa usata dal gruppo prima di produrre i diagrammi e la relazione. Non sostituisce la relazione finale: raccoglie terminologia, attori, casi d'uso, stati, regole di business e relazioni che devono rimanere coerenti in tutti gli elaborati.

Quando emerge una decisione nuova, si aggiorna prima questo documento e poi gli artefatti interessati. Le voci marcate **DA CONFERMARE** richiedono una decisione esplicita del gruppo e non devono essere presentate come requisiti definitivi.

## 2. Scope del sistema

Venue Event Manager gestisce:

- utenti ordinari e amministratori;
- venue e relativi indirizzi;
- spazi, attrezzature e servizi;
- richieste per l'organizzazione di eventi;
- creazione e ciclo di vita degli eventi;
- eventi pubblici prenotabili ed eventi privati con lista invitati;
- prenotazioni e biglietti nominativi;
- recensioni successive alla partecipazione;
- report amministrativi associabili a un evento;
- persistenza PostgreSQL, transazioni e accessi concorrenti.

Il progetto è intenzionalmente focalizzato su modello del dominio, service layer, persistenza JDBC, sicurezza e testing. Non include attualmente una GUI, una CLI definitiva o una API web. I test dei service rappresentano quindi anche il principale punto di esercizio funzionale dell'applicazione.

### Fuori scope nella versione attuale

- interfaccia grafica e navigazione tra schermate;
- pagamenti reali, nonostante lo stato `PENDING_PAYMENT`;
- invio materiale di email o notifiche;
- sessioni utente e token di autenticazione;
- vendita con integrazione verso servizi esterni;
- deployment in produzione.

## 3. Glossario ufficiale

| Termine | Significato nel progetto | Nota di coerenza |
|---|---|---|
| Visitor | Persona non autenticata che può registrarsi | Non esiste come entità persistita |
| User | Utente ordinario registrato (`is_admin = false`) | Usare `User`, non `Customer` |
| Administrator | Utente con `is_admin = true` | Abbreviabile in `Admin` |
| Organiser | Utente ordinario associato facoltativamente a un evento | Nel codice è scritto con grafia britannica `organiser` |
| Creator | Amministratore che crea l'evento | Campo obbligatorio dell'evento |
| Handler | Amministratore assegnato a una richiesta | Può essere assente finché la richiesta è pendente |
| Venue | Struttura fisica che ospita eventi | Non usare alternativamente `location` |
| Resource | Generalizzazione di `Space`, `Equipment` e `Service` | È una classe astratta Java, non una tabella unica |
| Event request | Richiesta di un utente per organizzare un evento | Distinta dall'evento effettivo |
| Event | Evento programmato presso una venue | Nasce sempre in `DRAFT` |
| Booking | Prenotazione di uno o più biglietti | Nasce in `PENDING_PAYMENT` |
| Ticket | Titolo nominativo collegato a una prenotazione | L'orario coincide con l'inizio dell'evento |
| Event guest | Persona inserita nella lista di un evento privato | Non deve necessariamente essere un `User` registrato |
| Review | Valutazione da 1 a 5 di un evento concluso | Una sola per coppia utente-evento |
| Report | Segnalazione creata da un admin verso un utente ordinario | Può riferirsi facoltativamente a un evento |

## 4. Attori e responsabilità

### A1 - Visitor

- registra un nuovo account ordinario;
- fornisce dati anagrafici e password validi;
- si autentica mediante username e password se possiede un account attivo.

### A2 - User

- consulta venue, eventi e risorse;
- aggiorna o elimina il proprio profilo fornendo la password;
- cambia la propria password;
- crea e gestisce richieste di evento;
- può essere assegnato come organiser di un evento;
- prenota biglietti per eventi pubblici e pubblicati;
- conferma o annulla una prenotazione secondo le transizioni previste;
- pubblica una recensione dopo aver partecipato a un evento.

Un utente `BANNED` resta persistito, ma non può effettuare prenotazioni.

### A3 - Administrator

- crea e gestisce venue, risorse ed eventi;
- viene registrato come `creator` dell'evento;
- prende in carico, accetta o rifiuta richieste;
- assegna o rimuove un organiser;
- conferma, pubblica, riprogramma o cancella eventi;
- banna e riattiva utenti ordinari dopo verifica delle proprie credenziali;
- crea report verso utenti ordinari.

### A4 - Invited guest

- rappresenta una persona invitata a un evento privato;
- può confermare o annullare la partecipazione;
- non coincide necessariamente con un account applicativo.

### Decisione aperta sugli attori

**DA CONFERMARE:** nei diagrammi dei casi d'uso, `Organiser` può essere rappresentato come specializzazione concettuale di `User`. Nel database non è un ruolo permanente: è un utente ordinario associato a uno specifico evento. Inoltre i metodi di `EventService` non ricevono l'identità dell'attore che li invoca; la distinzione delle responsabilità è pertanto un requisito concettuale, non un controllo di autorizzazione completo implementato nel service.

## 5. Catalogo preliminare dei casi d'uso

La numerazione seguente è proposta come identificatore stabile per diagrammi, template, test e relazione.

| ID | Titolo | Attore principale | Livello | Priorità |
|---|---|---|---|---|
| UC-01 | Registrare un account | Visitor | User goal | Alta |
| UC-02 | Gestire il proprio account | User | Summary | Alta |
| UC-02.1 | Modificare il profilo | User | User goal | Alta |
| UC-02.2 | Cambiare password | User | User goal | Alta |
| UC-02.3 | Eliminare account | User | User goal | Media |
| UC-03 | Moderare un account | Administrator | User goal | Alta |
| UC-04 | Consultare venue ed eventi | User | Summary | Media |
| UC-05 | Gestire una venue | Administrator | User goal | Alta |
| UC-06 | Gestire le risorse di una venue | Administrator | User goal | Alta |
| UC-07 | Presentare una richiesta di evento | User | User goal | Alta |
| UC-08 | Gestire una richiesta di evento | Administrator | User goal | Alta |
| UC-09 | Gestire un evento | Administrator | Summary | Alta |
| UC-09.1 | Creare un evento | Administrator | User goal | Alta |
| UC-09.2 | Confermare un evento | Administrator | Function | Alta |
| UC-09.3 | Pubblicare un evento | Administrator | User goal | Alta |
| UC-09.4 | Modificare o riprogrammare un evento | Administrator | User goal | Media |
| UC-09.5 | Cancellare un evento | Administrator | User goal | Alta |
| UC-09.6 | Assegnare un organiser | Administrator | Function | Media |
| UC-10 | Gestire la lista invitati | Administrator/Organiser | User goal | Media |
| UC-11 | Prenotare biglietti | User | User goal | Alta |
| UC-12 | Gestire una prenotazione | User/Administrator | User goal | Alta |
| UC-13 | Pubblicare una recensione | User | User goal | Media |
| UC-14 | Gestire recensioni | User | User goal | Media |
| UC-15 | Creare e consultare report | Administrator | User goal | Media |
| UC-16 | Autenticarsi | Visitor | User goal | Alta |

### Casi d'uso da non sovrastimare

- Il sistema autentica un account attivo mediante username e password, ma non crea una sessione e non emette token.
- `PENDING_PAYMENT` descrive il ciclo della prenotazione, ma non esiste un gateway di pagamento. La conferma è una transizione applicativa manuale.
- Non essendoci interfaccia, mockup e page-navigation diagram non sono necessari per descrivere l'implementazione corrente. Si possono aggiungere solo come proposta progettuale futura.

## 6. Casi d'uso da sviluppare con template completo

Per contenere la lunghezza della relazione, i template completi dovrebbero concentrarsi sui flussi più significativi:

1. **UC-01 - Registrare un account**, per sicurezza delle password e validazioni;
2. **UC-03 - Moderare un account**, per autenticazione dell'admin e autorizzazioni;
3. **UC-07/UC-08 - Presentare e gestire una richiesta**, per attori multipli e stato;
4. **UC-09.3 - Pubblicare un evento**, per precondizioni e macchina a stati;
5. **UC-10 - Gestire la lista invitati**, per eventi privati;
6. **UC-11 - Prenotare biglietti**, per transazione, capienza e concorrenza;
7. **UC-13 - Pubblicare una recensione**, per partecipazione e unicità;
8. **UC-15 - Creare un report**, per vincoli admin/utente.

Ogni template deve riportare: ID, titolo, livello, attori, descrizione, precondizioni, postcondizioni, flusso principale numerato, flussi alternativi collegati al passo di origine, business rule e test correlati.

## 7. Macchine a stati

### 7.1 Event

Stato iniziale: `DRAFT`.

| Stato corrente | Stato successivo ammesso | Operazione |
|---|---|---|
| `DRAFT` | `CONFIRMED` | Conferma dell'evento |
| `DRAFT` | `CANCELLED` | Cancellazione |
| `CONFIRMED` | `PUBLISHED` | Pubblicazione |
| `CONFIRMED` | `CANCELLED` | Cancellazione |
| `PUBLISHED` | `CANCELLED` | Cancellazione |

`CANCELLED` è terminale. Sono vietate transizioni duplicate, all'indietro e la pubblicazione diretta da `DRAFT`.

Alla pubblicazione viene valorizzato `published_at`. La cancellazione annulla atomicamente prenotazioni e inviti ancora attivi prima di aggiornare lo stato dell'evento.

### 7.2 Booking

Stato iniziale: `PENDING_PAYMENT`.

| Stato corrente | Stato successivo ammesso |
|---|---|
| `PENDING_PAYMENT` | `CONFIRMED` |
| `PENDING_PAYMENT` | `CANCELLED` |
| `CONFIRMED` | `CANCELLED` |

`CANCELLED` è terminale. Non esiste ancora un processo di pagamento che effettui automaticamente la conferma.

### 7.3 Event request

Stato iniziale: `PENDING`.

| Stato corrente | Stato successivo ammesso | Condizioni aggiuntive |
|---|---|---|
| `PENDING` | `ACCEPTED` | Handler admin assegnato e preventivo non negativo |
| `PENDING` | `REJECTED` | Chiusura da parte della gestione |
| `PENDING` | `CANCELLED` | Ritiro della richiesta |

`ACCEPTED`, `REJECTED` e `CANCELLED` sono terminali. Ogni chiusura valorizza `closed_at` e usa un lock sul record per serializzare decisioni concorrenti.

### 7.4 Event guest

Stato iniziale: `INVITED`.

| Stato corrente | Stato successivo ammesso |
|---|---|
| `INVITED` | `CONFIRMED` |
| `INVITED` | `CANCELLED` |
| `CONFIRMED` | `CANCELLED` |

Un invito può essere creato solo per un evento `PRIVATE_GUEST_LIST` non cancellato. Non è obbligatorio che un evento privato possieda già almeno un invitato.

### 7.5 User account

| Stato corrente | Stato successivo ammesso | Operazione |
|---|---|---|
| `ACTIVE` | `BANNED` | Ban eseguito da admin autenticato |
| `BANNED` | `ACTIVE` | Riattivazione eseguita da admin autenticato |

Un admin non può bannare sé stesso o un altro admin. Le transizioni duplicate sono rifiutate.

## 8. Regole di business consolidate

### Utenti e sicurezza

- **BR-01:** la registrazione crea sempre un utente ordinario `ACTIVE`; valori admin o banned forniti in input vengono ignorati.
- **BR-02:** username, nome, cognome, email e data di nascita devono essere validi; l'email deve essere univoca.
- **BR-03:** una password deve contenere tra 8 e 30 caratteri.
- **BR-04:** le password vengono persistite esclusivamente come hash PBKDF2 con salt casuale.
- **BR-05:** autenticazione, modifica profilo, eliminazione account, cambio password e moderazione richiedono la verifica delle credenziali appropriate; gli account `BANNED` non possono autenticarsi.
- **BR-06:** un admin non può bannare sé stesso o un altro admin.
- **BR-07:** un utente `BANNED` non può prenotare eventi.

### Venue, richieste ed eventi

- **BR-08:** una venue richiede nome e indirizzo strutturato completo; `additional_info` è facoltativo.
- **BR-09:** una richiesta richiede requester ordinario, venue esistente, nome e intervallo temporale valido.
- **BR-10:** soltanto un admin può essere assegnato come handler.
- **BR-11:** una richiesta può essere accettata solo se ancora `PENDING`, con handler assegnato e preventivo non negativo.
- **BR-12:** un evento nuovo viene forzato a `DRAFT`, indipendentemente dallo stato ricevuto in input.
- **BR-13:** un evento richiede venue e creator esistenti, data finale successiva a quella iniziale e capienza positiva.
- **BR-14:** il creator deve essere un admin; l'organiser, quando presente, deve essere un utente ordinario.
- **BR-15:** organiser e poster sono facoltativi; il prezzo può essere nullo, interpretato come evento gratuito.
- **BR-16:** per pubblicare sono necessari stato `CONFIRMED`, dati validi, venue valida, capienza valida e coerenza temporale.
- **BR-17:** la capienza non può essere ridotta sotto il numero di biglietti già emessi.
- **BR-18:** cancellare un evento annulla nella stessa transazione prenotazioni e inviti attivi.

### Prenotazioni e invitati

- **BR-19:** sono prenotabili soltanto eventi `PUBLISHED`, `PUBLIC` e non ancora iniziati.
- **BR-20:** una prenotazione deve contenere almeno un biglietto valido e nominativo.
- **BR-21:** il prezzo totale è `numero biglietti × prezzo unitario`; un prezzo nullo equivale a zero.
- **BR-22:** il controllo della capienza e la scrittura di booking e ticket avvengono nella stessa transazione.
- **BR-23:** l'evento viene letto con lock prima del conteggio dei biglietti per impedire overbooking concorrente.
- **BR-24:** ogni ticket viene associato alla prenotazione appena creata e riceve l'orario iniziale dell'evento.
- **BR-25:** gli invitati appartengono soltanto a eventi privati con guest list; il loro stato iniziale è sempre `INVITED`.

### Feedback e moderazione

- **BR-26:** una recensione ha rating compreso tra 1 e 5 e commento facoltativo fino a 1000 caratteri.
- **BR-27:** si può recensire soltanto un evento concluso per cui l'utente possiede almeno una prenotazione `CONFIRMED`.
- **BR-28:** un utente può recensire lo stesso evento una sola volta; il vincolo esiste sia nel service sia nel database.
- **BR-29:** attualmente soltanto un admin può creare un report e il soggetto segnalato deve essere un utente non admin.
- **BR-30:** un report può riferirsi facoltativamente a un evento e possiede severità `LOW`, `MIDDLE` o `HIGH`.

## 9. Modello concettuale e cardinalità

| Relazione | Cardinalità e obbligatorietà | Comportamento DB rilevante |
|---|---|---|
| User - Booking | Un user può avere 0..N booking; ogni booking ha 1 user | Eliminazione user → cascade booking |
| Venue - Event | Una venue può ospitare 0..N eventi; ogni evento ha 1 venue | Eliminazione venue → cascade event |
| User creator - Event | Un admin può creare 0..N eventi; ogni evento ha 1 creator | Creator obbligatorio |
| User organiser - Event | Un user può organizzare 0..N eventi; ogni evento ha 0..1 organiser | Eliminazione organiser → `NULL` |
| User requester - EventRequest | Un user può creare 0..N richieste; ogni richiesta ha 1 requester | Eliminazione requester → cascade richiesta |
| User handler - EventRequest | Un admin può gestire 0..N richieste; ogni richiesta ha 0..1 handler | Eliminazione handler → `NULL` |
| Venue - EventRequest | Una venue può ricevere 0..N richieste; ogni richiesta indica 1 venue | Eliminazione venue → cascade richiesta |
| Event - Booking | Un evento può ricevere 0..N booking; ogni booking riguarda 1 evento | Eliminazione evento → cascade booking |
| Booking - Ticket | Un booking contiene 1..N ticket a livello applicativo | Eliminazione booking → cascade ticket |
| Event - EventGuest | Un evento privato può avere 0..N invitati | Eliminazione evento → cascade invitati |
| User - Review | Un user può scrivere 0..N recensioni | Eliminazione user → cascade review |
| Event - Review | Un evento può avere 0..N recensioni | `UNIQUE(user_id,event_id)` |
| Admin/User/Event - Report | Un admin crea 0..N report verso un user; evento 0..1 | Eliminazioni collegate → cascade |
| Venue - Space | Una venue contiene 0..N spazi; ogni spazio ha 1 venue | Eliminazione venue → cascade spazio |
| Venue - Equipment | Una venue ha 0..N attrezzature; equipment può avere 0..1 venue | Eliminazione venue → cascade equipment associato |
| Event - Space | N..M tramite `EVENT_SPACE` | Mapping eliminato in cascade |
| Request - Space | N..M tramite `REQ_SPACE` | Mapping eliminato in cascade |
| Event/Request - Equipment | N..M con attributo `quantity` | Quantità strettamente positiva |
| Event/Request - Service | N..M con attributo `quantity` | Service globale, quantità positiva |

### Vincoli strutturali da mostrare nell'ER

- email e username univoci;
- nome del `SERVICE` univoco;
- rating tra 1 e 5;
- quantità e capienza positive secondo i rispettivi vincoli;
- date finali successive alle date iniziali;
- prezzi e preventivi non negativi;
- coerenza tra `EventStatus` e `published_at`;
- unicità della coppia `(user_id, event_id)` in `REVIEW`.

## 10. Architettura condivisa

| Package | Responsabilità |
|---|---|
| `domain.model` | Entità, value object ed enum del dominio; modelli immutabili e metodi `with...` |
| `service` | Casi applicativi, validazione, autorizzazioni disponibili, transizioni e orchestrazione transazionale |
| `repository` | Contratti astratti di persistenza |
| `repository.jdbc` | Implementazioni PostgreSQL tramite JDBC e mapping riga-oggetto |
| `config` | Configurazione DB, pool HikariCP e `TransactionManager` |
| `exception` | Eccezioni applicative e di persistenza |

Dipendenza logica principale:

```text
Domain model
    ↑
Service layer
    ↓
Repository interfaces
    ↓
JDBC repositories → PostgreSQL

Service layer → TransactionManager → DataSource/HikariCP
```

Le letture usano transazioni `read-only`; le operazioni che modificano più repository condividono una sola `Connection`. Il `TransactionManager` applica il pattern execute-around, gestisce commit, rollback e ripristino dello stato della connessione.

## 11. Decisioni progettuali consolidate

| Decisione | Motivazione |
|---|---|
| Layered architecture | Separazione chiara tra dominio, logica e persistenza |
| Repository interfaces | Service indipendenti dalle implementazioni JDBC |
| Modelli immutabili | Stato prevedibile e modifiche esplicite tramite copie |
| TransactionManager | Confini transazionali uniformi e rollback centralizzato |
| Transazioni read-only | Rendere esplicite le operazioni di sola lettura |
| Lock `FOR UPDATE` | Serializzare prenotazioni e transizioni concorrenti |
| PBKDF2 con salt | Evitare password in chiaro e hash deterministici |
| Flyway | Schema riproducibile ed evoluzione versionata |
| Vincoli anche nel DB | Protezione dell'integrità oltre le sole validazioni Java |
| Dependency injection nei service | Test unitari indipendenti da PostgreSQL |
| Proxy JDBC nei test | Compatibilità Java 24 senza strumentare classi JDK con Byte Buddy |
| Nessuna UI obbligatoria | Priorità a progettazione, dominio, persistenza e relazione |

## 12. Mappa tra regole e test

| Area | Test principali |
|---|---|
| Password e credenziali | `PasswordHasherTest`, `PasswordCredentialIntegrationTest` |
| Utenti e moderazione | `UserServiceValidationTest`, `UserServiceWorkflowTest` |
| Eventi | `EventServiceValidationTest`, `EventServiceWorkflowTest`, `StatusTransitionTest` |
| Prenotazioni e capienza | `BookingServiceValidationTest`, `CoreServiceWorkflowTest` |
| Richieste e invitati | `RequestAndGuestWorkflowTest`, `EventGuestServiceValidationTest` |
| Risorse, recensioni, report | `SecondaryServiceValidationTest`, `SecondaryServiceWorkflowTest` |
| Deleghe di lettura | `ServiceQueryDelegationTest` |
| Repository JDBC | `PostgresRepositoryCrudIntegrationTest`, `PostgresEnumRepositoryIntegrationTest` |
| Vincoli e concorrenza | `PostgresReviewConstraintIntegrationTest`, `PostgresTransactionAndConcurrencyIntegrationTest` |

Con PostgreSQL spento vengono eseguiti 206 test isolati. Con PostgreSQL disponibile e migration applicate vengono eseguiti anche i 15 test d'integrazione, per un totale atteso di 221.

## 13. Diagrammi da derivare

### Persona 1 - Requisiti

- diagrammi dei casi d'uso separati per attore;
- template completi dei casi d'uso selezionati;
- collegamenti tra flussi alternativi, business rule e test.

### Persona 2 - Dominio

- class diagram per utenti;
- class diagram per venue e risorse;
- class diagram per richieste ed eventi;
- class diagram per booking, feedback e moderazione;
- macchine a stati di Event, Booking, EventRequest, EventGuest e User.

### Persona 3 - Architettura e persistenza

- package diagram;
- modello ER e modello relazionale;
- sequence diagram della prenotazione concorrente;
- sequence diagram della gestione di una richiesta;
- sequence diagram di registrazione/cambio password;
- eventuale activity diagram della prenotazione.

## 14. Convenzioni per gli artefatti

- usare gli ID `UC-xx` definiti qui;
- usare i nomi degli stati esattamente come negli enum Java;
- distinguere relazione concettuale, classe Java e tabella SQL;
- evitare diagrammi generati automaticamente dal codice;
- mostrare soltanto attributi e metodi significativi nei class diagram;
- salvare anche i sorgenti modificabili (`.puml` o `.drawio`), non solo PNG/PDF;
- esportare preferibilmente in SVG per mantenere nitidezza nella relazione;
- accompagnare ogni diagramma con osservazioni interpretative, senza riscriverlo integralmente in prosa.

## 15. Uso dell'intelligenza artificiale

La relazione deve dichiarare in modo trasparente che strumenti di IA generativa, in particolare ChatGPT e Codex, sono stati usati come supporto per:

- audit della struttura e della logica del progetto;
- individuazione di vulnerabilità, TODO e regole mancanti;
- confronto di alternative progettuali;
- supporto alla correzione di transazioni, stati e validazioni;
- introduzione e verifica dell'hashing PBKDF2;
- generazione e revisione della suite di test;
- analisi della coverage JaCoCo;
- revisione di JavaDoc, README e documentazione;
- preparazione iniziale di questa specifica e supporto alla stesura dei diagrammi.

Gli output dell'IA non sono stati accettati automaticamente: sono stati confrontati con requisiti, codice, migration e test; le modifiche sono state versionate in commit e verificate tramite Maven, PostgreSQL e JaCoCo. Le decisioni finali e la responsabilità del risultato restano del gruppo.

## 16. Decisioni da approvare nella prima riunione

1. Confermare il nome concettuale `Organiser` e la sua rappresentazione come specializzazione di `User` nei casi d'uso.
2. Confermare la numerazione proposta degli use case.
3. Descrivere l'autenticazione applicativa distinguendola esplicitamente dalla futura gestione di sessioni o token.
4. Confermare che i report siano descritti secondo l'implementazione corrente: admin verso utente ordinario.
5. Decidere se produrre mockup pur in assenza di interfaccia; non sono necessari per rappresentare il prodotto corrente.
6. Scegliere una lingua unica per titoli, attori e diagrammi; i nomi tecnici del codice restano in inglese.
7. Assegnare responsabile e revisore per ciascun gruppo di artefatti.

## 17. Regola di aggiornamento

Ogni modifica concettuale deve indicare:

- decisione cambiata;
- motivo;
- diagrammi o template interessati;
- persona incaricata di riallinearli.

Questo documento deve restare sintetico e operativo. Le motivazioni estese, gli snippet e la discussione critica verranno trasferiti nella relazione finale.
