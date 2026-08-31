# Guida ai diagrammi architetturali

## 1. Obiettivo e differenze tra le figure

Le tre figure descrivono la stessa applicazione con livelli di dettaglio diversi: non rappresentano architetture alternative.

- `layered-architecture` è la vista introduttiva, organizzata per livelli logici. Mostra come il chiamante accede ai service e come questi utilizzano dominio, contratti e infrastruttura.
- `package-dependencies` è la vista compatta delle dipendenze tra package Java reali. Serve a discutere la separazione delle responsabilità e la direzione delle dipendenze.
- `package-architecture` approfondisce i componenti interni, le transazioni e l'accesso a PostgreSQL.

## 2. Come leggere frecce e contenimento

Una freccia continua `A → B` nelle viste architetturali significa che **A usa B**. Non è una sequenza temporale: le frecce non indicano l'ordine completo delle chiamate o il percorso di ogni risultato restituito.

Il triangolo vuoto su linea tratteggiata indica **realizzazione**: una classe concreta implementa un'interfaccia. Nel package diagram compatto una dipendenza tratteggiata tra package sintetizza l'implementazione dei contratti contenuti in `repository` da parte delle classi in `repository.jdbc`; non significa che un package Java implementi letteralmente un altro package.

Il contenimento indica appartenenza. Una freccia diretta al bordo del `DOMAIN LAYER` o di `domain.model` riguarda l'insieme dei modelli contenuti, non soltanto la categoria più vicina al punto di arrivo. I sottogruppi interni non sono quindi componenti isolati.

Le figure sono viste selettive: non elencano ogni `import`, metodo o classe. L'assenza di una freccia secondaria non prova l'assenza di ogni possibile dipendenza nel codice.

## 3. Lettura di `layered-architecture`, freccia per freccia

### 3.1 Chiamante e orchestrazione

**Client applicativo → Application services.** Il client richiede un caso applicativo a `UserService`, `BookingService`, `EventService` o agli altri service. Il termine è volutamente generico perché non esiste una UI, CLI o API definitiva: rappresenta il punto d'ingresso esterno senza imporne la tecnologia. Attualmente sono soprattutto i test a esercitare direttamente i service, ma non vengono presentati come componenti dell'architettura produttiva.

**Application services → DOMAIN LAYER.** I service lavorano con tutti i sottodomini pertinenti: utenti, venue, risorse, richieste, eventi, prenotazioni e feedback. Creano o leggono oggetti immutabili, controllano regole applicative e producono copie aggiornate attraverso i metodi `with...`. La freccia è diretta all'intero livello proprio per non suggerire che utenti o feedback siano esclusi.

**Application services → Repository interfaces.** I service utilizzano contratti come `UserRepository`, `EventRepository` e `BookingRepository`. Le dipendenze vengono fornite tramite costruttore; il service non deve conoscere la classe concreta PostgreSQL e non contiene direttamente SQL.

**Application services → TransactionManager.** I service stabiliscono quali operazioni devono appartenere alla stessa transazione. Forniscono una funzione a `inTransaction` o `inReadOnly`; il manager gestisce apertura della connessione, completamento, rollback in caso di errore e ripristino dello stato della connessione. Nella vista introduttiva questa singola freccia riassume anche l'uso del manager da parte di `AuthService`, mostrato separatamente nel diagramma tecnico completo.

**Application services → AuthService.** Alcuni service delegano le responsabilità relative alle credenziali: in particolare `UserService` usa `AuthService`. Non significa che ogni service invochi l'autenticazione in ogni operazione.

### 3.2 Credenziali

**AuthService → PasswordHasher.** Il primo coordina autenticazione e cambio password, mentre il secondo esegue hashing e verifica PBKDF2 con salt. Il componente crittografico non accede al database.

**AuthService → Repository interfaces.** L'autenticazione utilizza `UserRepository` per recuperare utente e hash; il cambio password aggiorna l'hash persistito. La password non è un campo del modello di dominio `User`.

### 3.3 Contratti e implementazioni

**Repository interfaces → DOMAIN LAYER.** Le firme repository scambiano oggetti del dominio: per esempio restituiscono `Optional<Event>` o ricevono una `Booking` da salvare. I tipi persistiti non appartengono al package JDBC.

**Pg...Repository ⇢ Repository interfaces.** È una relazione di realizzazione: per esempio `PgBookingRepository implements BookingRepository`. La punta è rivolta verso l'interfaccia, non verso PostgreSQL.

La struttura fondamentale è:

```text
Service → Repository interface ← PostgreSQL implementation
```

Il service dipende dall'astrazione; a runtime la chiamata viene eseguita dall'implementazione concreta fornita. Nei test questa dipendenza può essere sostituita senza riscrivere la logica del service. Questa è la ragione della direzione apparentemente "verso l'alto" della realizzazione.

### 3.4 Connessioni e database

**TransactionManager → DataSourceSingleton / HikariCP.** Nel percorso predefinito il manager usa il pool configurato da `DataSourceSingleton`. Più precisamente il suo campo è di tipo `DataSource`, ricevuto dal costruttore: nei test può essere fornita un'implementazione alternativa. La figura raggruppa il percorso di configurazione ordinario senza annullare questa possibilità di injection.

**DataSourceSingleton / HikariCP → PostgreSQL.** Il pool gestisce le connessioni verso il database e le rende riutilizzabili. Ottenere una connessione dal pool non significa necessariamente aprire ogni volta una nuova connessione fisica.

**Pg...Repository → PostgreSQL.** Le repository eseguono istruzioni SQL attraverso la `Connection` ricevuta. Quando più repository operano nello stesso caso d'uso, la connessione comune consente di condividere commit o rollback. Il controllo della transazione resta nel manager.

**Flyway migrations → PostgreSQL.** Flyway applica gli script versionati per costruire ed evolvere lo schema. Non è una chiamata eseguita per ogni prenotazione o autenticazione: riguarda la preparazione e l'aggiornamento del database.

## 4. Come leggere `package-dependencies`

Questa figura passa dalla vista logica ai package Java, sotto il prefisso `venue.event.manager`.

| Dipendenza | Significato |
|---|---|
| `service → domain.model` | Uso dei modelli, degli enum e dei value object |
| `service → repository` | Accesso alla persistenza attraverso contratti |
| `service → config` | Uso di `TransactionManager` e della configurazione predefinita |
| `service → exception` | Uso di eccezioni applicative per validazioni, conflitti, assenza di dati e autorizzazioni |
| `repository → domain.model` | Uso dei modelli nelle firme delle interfacce |
| `repository.jdbc → repository` | Implementazione delle interfacce da parte delle classi PostgreSQL |

La dipendenza diretta delle implementazioni JDBC dai modelli del dominio viene omessa in questa vista sintetica per non ripetere un collegamento secondario. Esiste comunque nel codice: il mapping JDBC costruisce gli oggetti restituiti dai contratti repository.

Il dominio non dipende dai service, dalle repository o da PostgreSQL. I suoi sottopackage sono mostrati per rendere visibile la suddivisione funzionale, non per descrivere le relazioni tra singole entità: queste sono documentate nei class diagram del blocco B.

Il nome `repository.jdbc` è mostrato separatamente per evidenziarne la responsabilità concreta. Si tratta del package Java omonimo, non di una seconda directory repository indipendente.

## 5. Come leggere `package-architecture`

La figura dettagliata aggiunge elementi che la vista compatta raggruppa.

Il collegamento dei service alle **Application exceptions** mostra l'uso di `ValidationException`, `ConflictException`, `ForbiddenException` e `NotFoundException`. Non rappresenta tutte le eccezioni del programma: gli errori transazionali e JDBC hanno anche classi nei rispettivi package infrastrutturali.

La dipendenza **Pg...Repository → JDBC mapping utilities** indica l'uso di `RowMapper<T>` e delle utility di binding, conversione e verifica delle righe aggiornate. Le lambda mapper trasformano un `ResultSet` nei modelli Java.

La catena **TransactionManager → DataSourceSingleton → HikariCP → PostgreSQL** descrive l'accesso predefinito al pool. Il collegamento **DataSourceSingleton → DbConfig** indica il caricamento delle impostazioni del database prima della configurazione del pool. La possibilità di fornire un diverso `DataSource` a `TransactionManager` resta valida.

**AuthService → TransactionManager** rende esplicito che anche autenticazione e cambio password usano transazioni gestite, non soltanto gli altri service.

HikariCP, PostgreSQL e Flyway sono esterni al contenitore `venue.event.manager`: sono rispettivamente una libreria di pooling, il DBMS e lo strumento con gli script di evoluzione dello schema, non package applicativi Java.

## 6. Un esempio concreto che collega le tre viste

Durante la creazione di una prenotazione:

1. un chiamante invoca `BookingService`;
2. il service avvia il lavoro tramite `TransactionManager`;
3. il manager ottiene una connessione dal `DataSource`;
4. il service usa i contratti repository per verificare utente, evento e disponibilità;
5. le implementazioni PostgreSQL eseguono le query sulla stessa connessione;
6. il service costruisce `Booking` e `Ticket` e ne richiede il salvataggio;
7. il manager esegue il commit se il lavoro termina correttamente, oppure il rollback in caso di errore.

Questo elenco illustra una possibile esecuzione; le figure architetturali, invece, mostrano responsabilità e dipendenze. L'ordine preciso delle chiamate e i flussi alternativi appartengono al sequence diagram.

## 7. Limiti e uso nella relazione

L'organizzazione è descritta come **layered architecture con Service Layer, Repository e dependency injection**. Non viene presentata come architettura esagonale formale. Le classi concrete dei service e `java.sql.Connection` rimangono visibili ai relativi chiamanti e contratti: non viene sostenuta un'indipendenza assoluta da ogni API infrastrutturale.

Le figure non includono tutti gli strumenti di sviluppo: Maven, JUnit, Mockito e JaCoCo appartengono alla sezione su ambiente e testing. PBKDF2 è spiegato nella sicurezza, non come livello architetturale.

Per la relazione è consigliabile introdurre l'architettura con `layered-architecture` e usare `package-dependencies` per discutere le dipendenze statiche. La vista dettagliata può essere inserita nell'approfondimento sulla persistenza o in appendice: non è necessario affiancare tre figure simili senza una motivazione distinta.
