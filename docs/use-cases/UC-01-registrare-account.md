# UC-01 - Registrare un account

| Campo | Contenuto |
|---|---|
| **ID** | UC-01 |
| **Titolo** | Registrare un account |
| **Livello** | User goal |
| **Attore principale** | Visitor |
| **Attori secondari** | Nessuno |
| **Descrizione** | Un Visitor richiede la registrazione, fornisce i propri dati e una password, e il sistema crea un nuovo account User ordinario. |
| **Trigger** | Il Visitor richiede di registrare un nuovo account. |

## Precondizioni

1. Il Visitor non è autenticato.
2. Il sistema è disponibile a ricevere la richiesta di registrazione.

L'unicità dell'email e la validità dei dati non sono considerate precondizioni: vengono verificate durante il caso d'uso e, se non soddisfatte, producono un flusso alternativo.

## Postcondizioni di successo

1. È persistito un nuovo account User associato ai dati forniti.
2. Il nuovo account non possiede privilegi da Administrator.
3. Il nuovo account si trova nello stato `ACTIVE`.
4. La password è persistita esclusivamente come hash PBKDF2 con salt casuale e non in chiaro.
5. Il sistema restituisce l'identificativo del nuovo User.

## Garanzia minima

Se la registrazione non può essere completata, non viene persistito alcun nuovo User e non viene memorizzata alcuna password. Eventuali valori amministrativi o di stato presenti nei dati ricevuti non permettono comunque di creare un Administrator o un account già `BANNED`.

## Flusso principale

1. Il Visitor richiede di registrare un nuovo account.
2. Il Visitor fornisce username, nome, cognome, data di nascita, email, eventuale numero di telefono e password.
3. Il sistema verifica la presenza e la validità formale dei dati forniti e della password.
4. Il sistema verifica che l'indirizzo email non sia già associato a un altro User.
5. Il sistema inizializza il nuovo account come User ordinario nello stato `ACTIVE`, ignorando eventuali valori amministrativi o di stato ricevuti.
6. Il sistema genera dalla password una rappresentazione sicura PBKDF2 con salt casuale.
7. Il sistema persiste atomicamente lo User e la credenziale codificata.
8. Il sistema conferma la transazione e restituisce l'identificativo del nuovo User.

## Flussi alternativi ed eccezioni

### 2a. Dati obbligatori mancanti

1. Il sistema rileva che lo User da registrare o uno dei dati obbligatori non è stato fornito.
2. Il sistema rifiuta la richiesta come non valida.
3. Il caso d'uso termina senza persistere alcun User o password.

### 3a. Dati personali non validi

1. Il sistema rileva almeno una delle seguenti condizioni:
   - username, nome o cognome vuoti oppure con lunghezza non valida;
   - email con formato non valido;
   - numero di telefono facoltativo presente ma con formato o lunghezza non validi;
   - data di nascita futura, assente o precedente al 1900.
2. Il sistema rifiuta la richiesta indicando che i dati non sono validi.
3. Il caso d'uso termina senza persistere alcun User o password.

### 3b. Password non valida

1. Il sistema rileva che la password è nulla, vuota oppure non contiene tra 8 e 30 caratteri.
2. Il sistema rifiuta la richiesta.
3. Il caso d'uso termina senza persistere alcun User o password.

### 4a. Email già utilizzata

1. Il sistema rileva che l'indirizzo email è già associato a un altro User.
2. Il sistema rifiuta la registrazione per evitare la duplicazione dell'account.
3. Il caso d'uso termina senza persistere alcun nuovo User o password.

### 7a. Conflitto o errore durante la persistenza

1. Il sistema rileva che i dati non possono essere persistiti, ad esempio per un username già utilizzato o per un errore del database.
2. Il sistema annulla la transazione.
3. Nessun User incompleto e nessuna credenziale rimangono persistiti.
4. Il caso d'uso termina con un errore.

## Regole di business correlate

- **BR-01:** la registrazione crea sempre un utente ordinario `ACTIVE`; valori admin o banned forniti in input vengono ignorati.
- **BR-02:** username, nome, cognome, email e data di nascita devono essere validi; l'email deve essere univoca.
- **BR-03:** una password deve contenere tra 8 e 30 caratteri.
- **BR-04:** le password vengono persistite esclusivamente come hash PBKDF2 con salt casuale.

## Test correlati

- `UserServiceWorkflowTest.registrationShouldStripAdminAndBannedFlagsAndHashPassword`: verifica che la registrazione rimuova privilegi e stato non ammessi, persista un account `ACTIVE` ordinario e codifichi la password.
- `UserServiceValidationTest.nullUserShouldBeRejected`: verifica il rifiuto di una richiesta senza User.
- `UserServiceValidationTest.shortUsernameShouldBeRejected`: verifica la lunghezza minima dello username.
- `UserServiceValidationTest.emptyFirstnameShouldBeRejected` e `emptyLastnameShouldBeRejected`: verificano i dati anagrafici obbligatori.
- `UserServiceValidationTest.malformedEmailShouldBeRejected` e `duplicateEmailShouldBeRejected`: verificano formato e unicità dell'email.
- `UserServiceValidationTest.malformedPhoneShouldBeRejected` e `shortPhoneShouldBeRejected`: verificano il numero di telefono facoltativo quando presente.
- `UserServiceValidationTest.missingBirthdayShouldBeRejected`, `futureBirthdayShouldBeRejected` e `implausiblyOldBirthdayShouldBeRejected`: verificano la data di nascita.
- `PasswordHasherTest.hashShouldCreateVerifiableEncodedPassword`: verifica la generazione di una credenziale PBKDF2 verificabile.
- `PasswordHasherTest.hashShouldUseADifferentSaltEachTime`: verifica l'impiego di un salt casuale.
- `PasswordCredentialIntegrationTest.registrationShouldPersistOnlyAnEncodedPassword`: verifica su PostgreSQL che la password registrata sia codificata e non coincida con quella in chiaro.

Il vincolo di lunghezza della password, pur essendo implementato in `AuthService`, non dispone ancora di un test unitario dedicato ai limiti di 8 e 30 caratteri.
