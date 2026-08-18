# Istruzioni operative per la call di progettazione

Questa guida descrive come organizzare la call dedicata a requisiti, diagrammi e divisione del lavoro. Deve essere letta insieme a [SPECIFICHE_CONDIVISE.md](SPECIFICHE_CONDIVISE.md), che rappresenta la base comune da approvare.

## 1. Obiettivo della call

La call non deve servire a disegnare tutti i diagrammi in diretta. L'obiettivo è uscire dalla riunione con:

1. terminologia e scope approvati;
2. attori definitivi;
3. catalogo e numerazione dei casi d'uso approvati;
4. stati e transizioni confermati;
5. tre blocchi di lavoro assegnati;
6. strumenti, formato dei file e stile condivisi;
7. responsabile e revisore di ogni artefatto;
8. una scadenza per la prima versione.

Al termine, ogni componente deve sapere esattamente cosa produrre senza dover reinterpretare autonomamente il progetto.

## 2. Preparazione prima della call

Ogni componente deve dedicare circa 20-30 minuti alla preparazione.

### Tutti

- aggiornare la repository locale;
- leggere almeno le sezioni 1-8 e 16 di `SPECIFICHE_CONDIVISE.md`;
- annotare dubbi o correzioni senza modificare subito la numerazione;
- verificare di distinguere `User`, `Administrator`, `Organiser` e `EventGuest`;
- controllare le macchine a stati proposte;
- scegliere eventuali preferenze tra i tre blocchi di lavoro.

### Componente più vicino ai requisiti

- controllare se mancano azioni importanti nel catalogo degli use case;
- segnalare titoli troppo tecnici o troppo generici;
- individuare i casi d'uso che meritano un template completo.

### Componente più vicino al dominio

- controllare entità, enum e transizioni;
- verificare che `Organiser` sia trattato come ruolo contestuale;
- segnalare differenze tra concetti del dominio e tabelle SQL.

### Componente più vicino a database e architettura

- controllare cardinalità, mapping e cancellazioni in cascade;
- verificare i package e le dipendenze;
- segnalare vincoli importanti da mostrare nel modello ER.

## 3. Materiale da avere aperto

Durante la call tenere disponibili:

- `docs/SPECIFICHE_CONDIVISE.md`;
- codice del package `domain.model`;
- enum di stato;
- service principali;
- migration Flyway;
- test di workflow e concorrenza;
- un documento condiviso per annotare decisioni e assegnazioni.

Non è necessario aprire contemporaneamente tutte le classi. Il codice serve soltanto quando una decisione è dubbia.

## 4. Ruoli durante la call

Assegnare temporaneamente tre ruoli, indipendenti dalla successiva divisione del lavoro.

### Facilitatore

- segue l'ordine del giorno;
- impedisce che una singola discussione assorba tutta la call;
- distingue decisioni necessarie da miglioramenti rinviabili;
- formula chiaramente la decisione prima di passare oltre.

### Segretario

- aggiorna il documento condiviso o prende note strutturate;
- registra decisioni, motivazioni e punti aperti;
- annota responsabili, revisori e scadenze;
- non deve affidarsi alla memoria o alla registrazione della chiamata.

### Verificatore

- controlla nel codice i punti controversi;
- segnala quando si sta descrivendo un requisito futuro come già implementato;
- controlla la coerenza tra use case, stati, database e test.

I ruoli valgono soltanto per la riunione e possono ruotare nelle call successive.

## 5. Agenda consigliata

Durata prevista: 75-90 minuti.

### Fase 1 - Allineamento sullo scope (10 minuti)

Leggere e approvare la descrizione sintetica del sistema.

Domande da chiudere:

- confermiamo che la consegna si concentra su dominio, service, JDBC, test e documentazione?
- confermiamo che GUI e API non appartengono alla versione corrente?
- eventuali mockup saranno dichiarati come proposta e non come interfaccia implementata?
- quali funzionalità non devono essere promesse nella relazione?

Output atteso: un paragrafo di scope approvato e una lista `fuori scope` stabile.

### Fase 2 - Terminologia e attori (15 minuti)

Esaminare glossario e attori.

Decisioni obbligatorie:

- usare `Organiser` o la traduzione italiana `Organizzatore`;
- rappresentare `Organiser` come specializzazione concettuale di `User` oppure come ruolo associato a un singolo evento;
- usare `Administrator` o `Admin` in tutti i diagrammi;
- distinguere `EventGuest` da un account registrato;
- decidere la lingua dei diagrammi.

Regola consigliata: descrizioni in italiano, nomi tecnici di classi, enum e stati in inglese.

Output atteso: quattro attori definitivi con una descrizione di una o due righe ciascuno.

### Fase 3 - Catalogo dei casi d'uso (20 minuti)

Esaminare da `UC-01` a `UC-15` senza scrivere ancora tutti i flussi.

Per ogni voce verificare:

- esiste un obiettivo riconoscibile dell'attore?
- il titolo descrive un risultato, non un dettaglio tecnico?
- l'attore principale è corretto?
- il livello `Summary`, `User goal` o `Function` è sensato?
- il caso è implementato, parzialmente implementato o futuro?
- può essere unito a un caso più generale senza perdere chiarezza?

Decisione importante sul login:

- il codice verifica password per operazioni sensibili;
- non implementa una sessione o un endpoint pubblico di login;
- un eventuale use case `Autenticarsi` deve essere marcato come requisito concettuale/futuro oppure escluso dai casi implementati.

Non rinumerare più volte durante la discussione. Annotare prima tutte le modifiche, poi applicare una sola numerazione definitiva.

Output atteso: catalogo approvato con ID stabili.

### Fase 4 - Regole e macchine a stati (15 minuti)

Controllare insieme le cinque macchine a stati:

- `EventStatus`;
- `BookingStatus`;
- `EventRequestStatus`;
- `EventGuestStatus`;
- `AccountStatus`.

Per ogni macchina verificare:

- stato iniziale;
- transizioni consentite;
- stati terminali;
- transizioni vietate;
- effetti collaterali atomici;
- attore che provoca la transizione.

Punti da ricordare:

- un evento non passa direttamente da `DRAFT` a `PUBLISHED`;
- `CANCELLED` non viene riattivato;
- accettare una richiesta richiede handler e preventivo;
- cancellare un evento annulla booking e inviti attivi;
- `PENDING_PAYMENT` non corrisponde a un pagamento realmente integrato.

Output atteso: tabelle di transizione approvate, utilizzabili direttamente per gli state diagram.

### Fase 5 - Assegnazione del lavoro (15 minuti)

Assegnare i tre blocchi in base a competenze e dipendenze, non al numero grezzo di figure.

#### Blocco A - Requisiti e use case

Consegne:

- diagrammi dei casi d'uso suddivisi per attore;
- template completi dei casi selezionati;
- riferimenti da flussi alternativi a business rule e test.

Competenze utili: sintesi, scrittura, ragionamento sui requisiti.

#### Blocco B - Dominio e stati

Consegne:

- class diagram suddivisi per sottodominio;
- cinque state diagram;
- note interpretative su entità, value object, enum e immutabilità.

Competenze utili: conoscenza del codice Java e modellazione OO.

#### Blocco C - Architettura e persistenza

Consegne:

- package diagram;
- modello ER e modello relazionale;
- sequence diagram di prenotazione, richiesta e password;
- eventuale activity diagram della prenotazione.

Competenze utili: database, JDBC, transazioni e dipendenze architetturali.

Per ogni blocco nominare:

- un responsabile, che produce la prima versione;
- un revisore, che controlla coerenza e leggibilità;
- una data per la prima bozza;
- una data per la revisione.

### Fase 6 - Strumenti e convenzioni (10 minuti)

Scelta consigliata:

- PlantUML per use case, package, class, state e sequence diagram;
- diagrams.net per il modello ER, se il posizionamento manuale risulta più leggibile;
- Markdown o Google Docs per i template degli use case;
- esportazione SVG per la relazione.

Concordare:

- posizione dei sorgenti dei diagrammi nella repository;
- nomi dei file;
- palette e font;
- orientamento dei diagrammi;
- quantità di dettagli da mostrare;
- lingua di titoli e note.

Struttura proposta:

```text
docs/
└── diagrams/
    ├── use-cases/
    ├── domain/
    ├── states/
    ├── architecture/
    ├── database/
    └── sequences/
```

Per ciascun diagramma conservare:

```text
nome-diagramma.puml oppure nome-diagramma.drawio
nome-diagramma.svg
```

Output atteso: toolchain e struttura delle cartelle approvate.

## 6. Come realizzare gli artefatti dopo la call

### Diagrammi dei casi d'uso

- separare i diagrammi per attore o area per evitare una figura illeggibile;
- usare gli stessi ID e titoli dei template;
- utilizzare `include` solo per comportamento sempre riutilizzato;
- utilizzare `extend` solo per comportamento opzionale o alternativo;
- non rappresentare repository, database o classi tecniche;
- non trasformare ogni metodo pubblico in un caso d'uso.

### Template dei casi d'uso

Ogni template deve contenere:

```text
ID
Titolo
Livello
Attore principale
Attori secondari
Descrizione
Precondizioni
Postcondizioni di successo
Garanzia minima
Flusso principale numerato
Flussi alternativi collegati al passo di origine
Business rule correlate
Test correlati
```

Esempio di numerazione alternativa:

```text
4. Il sistema verifica la disponibilità.
4a. La capienza è insufficiente.
    4a.1 Il sistema annulla l'operazione.
    4a.2 Nessun booking o ticket viene persistito.
```

### Class diagram

- realizzare più diagrammi piccoli, uno per sottodominio;
- mostrare attributi che spiegano identità, stato e relazioni;
- mostrare soltanto metodi rilevanti per il comportamento;
- indicare composizioni, associazioni, generalizzazioni e cardinalità;
- rappresentare `Address` come value object;
- rappresentare `Resource` come classe astratta;
- non copiare automaticamente tutti i getter, costruttori e `toString`.

### State diagram

- usare lo stato iniziale e gli stati terminali;
- etichettare le transizioni con operazione e guardia significativa;
- annotare gli effetti importanti, come `published_at` o cancellazioni cascade;
- evitare transizioni non presenti nei service;
- indicare separatamente eventuali sviluppi futuri.

### Package diagram

- mostrare dipendenze, non soltanto contenimento;
- associare ogni package a una responsabilità;
- evidenziare che i service dipendono dalle interfacce repository;
- mantenere distinta la dipendenza dalle implementazioni JDBC;
- mostrare `TransactionManager` e configurazione come infrastruttura.

### Modello ER

- derivarlo dalle migration, non soltanto dalle classi Java;
- mostrare PK, FK, cardinalità e opzionalità;
- includere le tabelle associative;
- evidenziare gli attributi `quantity` nelle relazioni con risorse;
- riportare almeno i principali vincoli `UNIQUE` e `CHECK`;
- distinguere chiaramente modello ER e modello relazionale.

### Sequence diagram

Per la prenotazione mostrare almeno:

```text
Caller → BookingService
BookingService → TransactionManager
TransactionManager → Connection
BookingService → UserRepository
BookingService → EventRepository: findByIdForUpdate
BookingService → TicketRepository: countTicketsForEvent
BookingService → BookingRepository: insert
BookingService → TicketRepository: insertMany
TransactionManager → Connection: commit/rollback
```

Rappresentare anche il ramo alternativo di capienza insufficiente e il rollback.

## 7. Regole di collaborazione Git

Ogni responsabile dovrebbe lavorare su un branch dedicato, per esempio:

```text
docs/use-case-diagrams
docs/domain-state-diagrams
docs/architecture-database-diagrams
```

Linee guida:

- aggiornarsi da `main` prima di creare il branch;
- non modificare file assegnati a un altro responsabile senza accordo;
- creare commit per artefatti coerenti, non per ogni minimo spostamento grafico;
- includere sempre il sorgente modificabile del diagramma;
- aprire una PR e assegnare il revisore concordato;
- evitare force push su branch condivisi;
- risolvere conflitti concettuali aggiornando prima `SPECIFICHE_CONDIVISE.md`.

## 8. Checklist del responsabile

Prima di chiedere una review:

- [ ] ID e nomi coincidono con la specifica condivisa;
- [ ] non sono state inventate funzionalità mancanti;
- [ ] stati e transizioni coincidono con i service;
- [ ] cardinalità coincidono con le migration;
- [ ] il diagramma rimane leggibile alla dimensione della relazione;
- [ ] esiste il file sorgente modificabile;
- [ ] esiste l'esportazione SVG;
- [ ] nomi e stile sono coerenti con gli altri diagrammi;
- [ ] è presente una breve nota che spiega le scelte importanti;
- [ ] la PR descrive cosa è stato rappresentato e quali dubbi restano.

## 9. Checklist del revisore

Il revisore non deve limitarsi a controllare l'estetica.

- [ ] confronta il diagramma con `SPECIFICHE_CONDIVISE.md`;
- [ ] controlla almeno un riferimento nel codice o nelle migration;
- [ ] verifica precondizioni, alternative e stati terminali;
- [ ] cerca contraddizioni con gli artefatti degli altri blocchi;
- [ ] segnala dettagli inutili o mancanti;
- [ ] verifica che requisiti futuri siano etichettati come tali;
- [ ] approva soltanto quando il diagramma è comprensibile senza spiegazione orale indispensabile.

## 10. Criteri di completamento della prima fase

La fase diagrammi è pronta per alimentare la relazione quando sono disponibili:

- catalogo definitivo dei casi d'uso;
- almeno un diagramma use case per ciascuna area/attore rilevante;
- template completi dei casi principali;
- package diagram;
- class diagram di tutti i sottodomini;
- cinque macchine a stati;
- modello ER e modello relazionale;
- almeno tre sequence diagram significativi;
- sorgenti ed esportazioni versionati;
- review incrociata completata;
- assenza di contraddizioni note tra gli artefatti.

## 11. Cosa non fare durante la call

- non discutere colori e allineamenti prima di aver approvato i contenuti;
- non generare automaticamente UML dal codice e considerarlo finito;
- non trasformare ogni classe o metodo in un requisito;
- non promettere login, pagamento o interfaccia completa come implementati;
- non rinviare tutte le decisioni difficili alla scrittura della relazione;
- non assegnare un diagramma senza indicare anche chi lo revisiona;
- non iniziare tre versioni incompatibili dello stesso glossario.

## 12. Verbale minimo da compilare a fine call

```text
Data:   18/08
Partecipanti:

Decisioni:
- Lingua degli artefatti: Italiano/Inglese per nomi tecnici e di Attori
- Termine scelto per Organiser: Organiser
- Rappresentazione di Organiser: Specifica dello User (da fixare pero il codice)
- Trattamento del login: per ora si ignora dopo si può magari considerare metterllo fra le specifiche future
- Trattamento dei mockup: uguale
- Numerazione use case approvata: sì
- Macchine a stati approvate: si

Assegnazioni:
- Blocco A, responsabile/revisore/scadenza: manuel
- Blocco B, responsabile/revisore/scadenza: marco
- Blocco C, responsabile/revisore/scadenza: carma

Strumenti:
- UML: PlantUML
- ER: diagrams.net  
- Template: per ora come si vole dopo con LaTeX direttamente
- Formato esportazione: sorgente + svg. md per testo

Punti ancora aperti:
-

Prossima verifica comune:
-
```

Compilare questo verbale negli ultimi cinque minuti evita che le decisioni prese oralmente vadano perse o vengano reinterpretate in modo diverso il giorno successivo.
