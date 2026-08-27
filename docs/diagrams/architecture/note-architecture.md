# Nota al diagramma dei package

## Scopo

I diagrammi forniscono due livelli di lettura:

- `package-dependencies` è la vista UML compatta delle dipendenze tra i package Java ed è adatta come figura principale della relazione;
- `package-architecture` aggiunge i componenti infrastrutturali e mostra come HikariCP, Flyway e PostgreSQL partecipano al funzionamento dell'applicazione.
- `layered-architecture` è una vista introduttiva più visuale, organizzata per livelli applicativo, dominio, contratti di persistenza e infrastruttura.

I diagrammi non elencano ogni classe: mostrano le responsabilità architetturali necessarie a comprendere come un caso d'uso attraversa dominio, persistenza e gestione transazionale.

## Lettura del diagramma

- `domain.model` contiene modelli immutabili, value object ed enum e non dipende dai livelli applicativi o infrastrutturali.
- `service` espone e orchestra i casi applicativi, applica validazioni e autorizzazioni disponibili e stabilisce i confini transazionali.
- `repository` contiene i contratti di persistenza usati dai service. Le firme scambiano modelli del dominio e ricevono, quando necessario, la connessione della transazione corrente.
- `repository.jdbc` contiene gli adapter PostgreSQL che implementano le interfacce repository, eseguono SQL e convertono le righe nei modelli Java.
- `config` configura HikariCP e centralizza commit, rollback, transazioni read-only e ripristino dello stato delle connessioni tramite `TransactionManager`.
- `exception` raccoglie le eccezioni applicative comunicate dai service.
- le migration Flyway costituiscono la fonte versionata dello schema PostgreSQL.

## Scelte rappresentative

L'architettura è descritta come **layered architecture con Repository e dependency injection**. Non viene definita architettura esagonale, perché il progetto non possiede una distinzione formale tra port-in, port-out e adapter di ingresso. Le interfacce repository svolgono comunque il ruolo importante di separare i service dall'implementazione JDBC.

Il `Caller` è intenzionalmente generico: nella versione corrente non esiste una UI, CLI o API definitiva. I test invocano direttamente i service e rappresentano il principale punto di esercizio funzionale dell'applicazione.

Il collegamento tra `repository.jdbc` e PostgreSQL è concettuale: la `Connection` viene aperta dal `TransactionManager` attraverso il pool HikariCP e passata alle operazioni repository che partecipano alla stessa transazione.
