# UC-03 - Moderare un account

| Campo | Contenuto |
|---|---|
| **ID** | UC-03 |
| **Titolo** | Moderare un account |
| **Livello** | Summary |
| **Attore principale** | Administrator |
| **Attori secondari** | User |
| **Descrizione** | Un Administrator banna un account ordinario attivo oppure riattiva un account ordinario bannato. |
| **Trigger** | L'Administrator richiede di modificare lo stato di un account. |

## Precondizioni

1. Esiste un account Administrator identificabile dal sistema.
2. L'account da moderare esiste.

## Postcondizioni di successo

1. Lo stato dell'account è `BANNED` dopo UC-03.1 oppure `ACTIVE` dopo UC-03.2.
2. Nessun altro dato dello User viene modificato.

## Garanzia minima

Se l'operazione fallisce, lo stato dell'account rimane invariato.

## Flusso principale

1. L'Administrator identifica l'account e sceglie se bannarlo o riattivarlo.
2. Il sistema verifica identità, password e ruolo dell'Administrator.
3. Il sistema verifica che il bersaglio sia uno User ordinario diverso dall'attore.
4. Il sistema controlla che la transizione richiesta sia compatibile con lo stato corrente.
5. Il sistema aggiorna lo stato dell'account nella stessa transazione.
6. Il sistema conferma l'operazione.

## Flussi alternativi ed eccezioni

### 2a. Credenziali amministrative non valide

1. Il sistema nega l'operazione.
2. Lo stato del bersaglio non cambia.

### 3a. Account inesistente

1. Il sistema segnala che l'account non esiste.
2. Il caso d'uso termina senza modifiche.

### 3b. Auto-moderazione o bersaglio Administrator

1. Il sistema rileva che l'attore coincide con il bersaglio oppure che il bersaglio è Administrator.
2. Il sistema nega l'operazione.

### 4a. Stato già raggiunto

1. Il sistema rileva un tentativo di bannare uno User già `BANNED` o riattivare uno User già `ACTIVE`.
2. Il sistema rifiuta la transizione come conflitto.

## Regole di business correlate

- **BR-05:** le operazioni amministrative richiedono identificativo e password validi di un Administrator.
- **BR-06:** un Administrator non può moderare se stesso né un altro Administrator.

## Test correlati

- `UserServiceWorkflowTest.adminShouldBanAndUnbanOrdinaryUser`.
- `UserServiceWorkflowTest.adminCannotBanSelfOrAnotherAdmin`.
- `UserServiceWorkflowTest.adminCannotUnbanAnAlreadyActiveUser`.
