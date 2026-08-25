# UC-03.2 - Riattivare un account

| Campo | Contenuto |
|---|---|
| **ID** | UC-03.2 |
| **Titolo** | Riattivare un account |
| **Livello** | User goal |
| **Attore principale** | Administrator |
| **Attori secondari** | User |
| **Descrizione** | Un Administrator riattiva un account ordinario precedentemente bannato. |
| **Trigger** | L'Administrator richiede di riattivare uno User bannato. |

## Precondizioni

1. Esiste un account Administrator identificabile dal sistema.
2. Lo User da moderare esiste.

## Postcondizioni di successo

1. Lo User si trova nello stato `ACTIVE`.
2. Gli altri dati dello User rimangono invariati.

## Garanzia minima

Se autenticazione, autorizzazione o transizione falliscono, lo stato dello User rimane invariato.

## Flusso principale

1. L'Administrator seleziona uno User bannato e richiede di riattivarlo.
2. Il sistema verifica identificativo, password e ruolo dell'Administrator.
3. Il sistema carica lo User indicato.
4. Il sistema verifica che il bersaglio sia uno User ordinario diverso dall'attore.
5. Il sistema verifica che lo User sia `BANNED`.
6. Il sistema aggiorna atomicamente lo stato a `ACTIVE`.
7. Il sistema conferma l'operazione.

## Flussi alternativi ed eccezioni

### 2a. Credenziali amministrative non valide

1. Il sistema nega l'operazione.
2. Il caso d'uso termina senza modifiche.

### 3a. User inesistente

1. Il sistema segnala che lo User non esiste.
2. Il caso d'uso termina senza modifiche.

### 4a. Auto-moderazione o bersaglio Administrator

1. Il sistema rileva che l'attore coincide con il bersaglio oppure che il bersaglio è Administrator.
2. Il sistema nega l'operazione.

### 5a. User già attivo

1. Il sistema rileva che lo User è già `ACTIVE`.
2. Il sistema rifiuta la transizione come conflitto.

## Regole di business correlate

- **BR-05:** le operazioni amministrative richiedono identificativo e password validi di un Administrator.
- **BR-06:** un Administrator non può moderare se stesso né un altro Administrator.

## Test correlati

- `UserServiceWorkflowTest.adminShouldBanAndUnbanOrdinaryUser`.
- `UserServiceWorkflowTest.adminCannotBanSelfOrAnotherAdmin`.
- `UserServiceWorkflowTest.adminCannotUnbanAnAlreadyActiveUser`.

