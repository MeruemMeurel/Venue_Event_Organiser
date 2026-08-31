# Pattern architetturali e progettuali

## Pattern effettivamente presenti

| Pattern | Applicazione nel progetto | Beneficio principale |
|---|---|---|
| Layered Architecture | Separazione tra `domain.model`, `service`, `repository`, `repository.jdbc` e `config` | Responsabilità distinte e dipendenze controllabili |
| Service Layer | I service espongono i casi applicativi e coordinano validazioni, autorizzazioni, repository e transazioni | Un solo punto di orchestrazione della logica applicativa |
| Repository | Le interfacce in `repository` astraggono le operazioni di persistenza; le classi `Pg...Repository` le implementano con JDBC | I service non dipendono direttamente da SQL o PostgreSQL |
| Dependency Injection | Repository, `TransactionManager`, `AuthService` e `PasswordHasher` vengono forniti tramite costruttore | Sostituibilità delle dipendenze e test isolati |
| Singleton | `DataSourceSingleton` e l'istanza predefinita di `TransactionManager` usano inizializzazione lazy thread-safe con `volatile` e double-checked locking | Condivisione controllata del pool e del gestore delle transazioni |
| Execute Around Method | `TransactionManager.inTransaction` e `inReadOnly` ricevono una funzione e racchiudono il lavoro tra apertura, commit o rollback e ripristino della connessione | Gestione transazionale uniforme senza duplicazione nei service |
| Row Mapper | L'interfaccia funzionale `RowMapper<T>` e le relative lambda convertono una riga JDBC in un modello del dominio | Separazione tra esecuzione delle query e costruzione degli oggetti |
| Immutable Object / copy-with | I modelli non espongono mutazioni dello stato; i metodi `with...` restituiscono nuove istanze modificate | Stato prevedibile e aggiornamenti espliciti |
| Value Object | `Address` rappresenta un indirizzo strutturato e immutabile all'interno del dominio venue | Raggruppamento e validazione coerente di valori correlati |

`Layered Architecture`, Service Layer, Repository e Dependency Injection descrivono soprattutto l'organizzazione complessiva. Singleton ed Execute Around Method sono invece chiaramente riconoscibili nell'implementazione concreta. Row Mapper e modelli immutabili sono idiomi progettuali applicati sistematicamente.

## Termini da non sovrastimare

- Non è implementato il **GoF State pattern**: gli stati sono enum e le transizioni sono controllate dai service e dal database, senza oggetti di stato polimorfici.
- Non è presente un **Builder pattern** dedicato: la costruzione avviene tramite costruttori e le modifiche mediante metodi `with...`.
- `TransactionManager` non è una **Unit of Work** completa, perché non mantiene un registro degli oggetti modificati né calcola automaticamente le operazioni da persistere.
- L'organizzazione non costituisce una **architettura esagonale formale**, perché non esistono port-in e adapter di ingresso espliciti. Le interfacce repository forniscono comunque un confine efficace verso la persistenza.
- Le macchine a stati documentano il ciclo di vita delle entità, ma non costituiscono da sole un design pattern.

## Aspetti tecnici correlati, ma non design pattern

PBKDF2, Flyway, HikariCP, transazioni read-only, lock `FOR UPDATE`, vincoli SQL e dependency injection nei test sono scelte tecniche o meccanismi infrastrutturali. Devono essere trattati nella relazione, ma non elencati impropriamente come design pattern.
