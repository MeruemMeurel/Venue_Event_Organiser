# Casi d'uso - Venue Event Manager

## 1. Scopo del documento

Questo documento descrive i requisiti funzionali di Venue Event Manager dal punto di vista degli attori che interagiscono con il sistema. Costituisce la fonte testuale dei diagrammi dei casi d'uso e mantiene una corrispondenza stabile tra:

- attori;
- obiettivi funzionali;
- casi d'uso e relativi sottocasi;
- regole di business;
- test che verificano il comportamento implementato.

Il documento non descrive classi, repository, query SQL o altri dettagli interni. Tali elementi appartengono alla progettazione e all'implementazione, non all'analisi dei casi d'uso.

Il sistema attuale è composto dal modello del dominio, dai service applicativi e dalla persistenza JDBC. Non sono presenti una GUI, una API web o una CLI definitiva. I casi d'uso descrivono pertanto le funzionalità offerte dal livello applicativo, senza inventare schermate o modalità di interazione non implementate.

## 2. Confine e limitazioni del sistema

Venue Event Manager permette di gestire utenti, venue, risorse, richieste di evento, eventi, invitati, prenotazioni, biglietti, recensioni e report amministrativi.

Restano fuori dal perimetro della versione corrente:

- la gestione di sessioni o token successiva all'autenticazione;
- una interfaccia grafica o web;
- un sistema reale di pagamento;
- l'invio effettivo di email e notifiche;
- l'integrazione con servizi esterni;
- il deployment in produzione.

Lo stato `PENDING_PAYMENT` rappresenta una fase del ciclo di vita della prenotazione, ma non implica l'esistenza di un gateway di pagamento. L'autenticazione verifica username, password e stato dell'account a livello applicativo, ma non crea una sessione né emette un token.

## 3. Stato di implementazione

Nel catalogo viene usata la seguente classificazione:

| Stato | Significato |
|---|---|
| **Implementato** | Il comportamento principale e le regole essenziali sono presenti nei service e verificabili tramite test. |
| **Futuro** | Il comportamento è soltanto ipotizzato e non deve essere presentato come funzionalità disponibile. |

## 4. Attori

### A1 - Visitor

Persona che non è ancora autenticata. Può registrare un nuovo account ordinario fornendo dati anagrafici e credenziali valide oppure autenticarsi con un account esistente e attivo.

### A2 - User

Utente ordinario registrato, caratterizzato da `is_admin = false`. Può:

- consultare venue, risorse ed eventi;
- modificare o eliminare il proprio account dopo la verifica della password;
- cambiare la propria password;
- presentare e gestire richieste per nuovi eventi;
- prenotare biglietti per eventi pubblici e pubblicati;
- gestire le proprie prenotazioni;
- pubblicare e gestire recensioni nel rispetto dei relativi vincoli;
- essere assegnato come organiser di uno specifico evento.

Un account nello stato `BANNED` resta memorizzato, ma non può effettuare nuove prenotazioni.

### A3 - Organiser

Ruolo assunto da uno `User` quando viene associato a uno specifico evento tramite `organiser_id`. Non rappresenta un tipo permanente di account, una sottoclasse Java o una tabella separata.

L'organiser può gestire esclusivamente gli eventi ai quali è stato assegnato. Per tali eventi può modificare i dati, la programmazione e le proprietà operative, nonché eseguire le transizioni consentite dal ciclo di vita. Non può creare eventi né assegnare o rimuovere autonomamente un organiser, operazioni riservate agli amministratori.

Nei diagrammi UML `Organiser` viene rappresentato come specializzazione concettuale di `User` per mostrare che conserva tutte le capacità dell'utente ordinario e acquisisce responsabilità aggiuntive soltanto nel contesto dell'evento assegnato.

### A4 - Administrator

Utente registrato caratterizzato da `is_admin = true`. Può:

- creare e gestire venue e relative risorse;
- creare eventi e risultarne `creator`;
- gestire qualsiasi evento;
- assegnare o rimuovere l'organiser di un evento;
- prendere in carico, accettare o rifiutare richieste di evento;
- moderare gli account ordinari;
- confermare, cancellare o eliminare qualsiasi prenotazione;
- creare e consultare report amministrativi.

### S1 - Invited guest

Persona inserita nella lista di un evento privato. Non coincide
necessariamente con uno User registrato e, nella versione corrente,
non interagisce direttamente con l'applicativo.

La conferma o cancellazione della sua partecipazione viene registrata
da un Administrator oppure dall'Organiser assegnato. Un'interazione
autonoma dell'invitato richiederebbe un futuro meccanismo basato su
token d'invito.

## 5. Catalogo dei casi d'uso

Gli identificatori seguenti devono rimanere invariati nei diagrammi, nei template dettagliati, nella relazione e nei riferimenti ai test.

| ID | Titolo | Attore principale | Livello   | Priorità | Stato        |
|---|---|---|-----------|---|--------------|
| UC-01 | Registrare un account | Visitor | User goal | Alta | Implementato |
| UC-02 | Gestire il proprio account | User | Summary   | Alta | Implementato |
| UC-02.1 | Modificare il profilo | User | User goal | Alta | Implementato |
| UC-02.2 | Cambiare password | User | User goal | Alta | Implementato |
| UC-02.3 | Eliminare l'account | User | User goal | Media | Implementato |
| UC-03 | Moderare un account | Administrator | Summary   | Alta | Implementato |
| UC-03.1 | Bannare un account | Administrator | User goal | Alta | Implementato |
| UC-03.2 | Riattivare un account | Administrator | User goal | Media | Implementato |
| UC-04 | Consultare venue, risorse ed eventi | User | Summary   | Media | Implementato |
| UC-05 | Gestire una venue | Administrator | Summary   | Alta | Implementato |
| UC-05.1 | Creare una venue | Administrator | User goal | Alta | Implementato |
| UC-05.2 | Modificare una venue | Administrator | User goal | Alta | Implementato |
| UC-05.3 | Eliminare una venue | Administrator | User goal | Media | Implementato |
| UC-06 | Gestire le risorse di una venue | Administrator | Summary   | Alta | Implementato |
| UC-06.1 | Aggiungere una risorsa | Administrator | User goal | Alta | Implementato |
| UC-06.2 | Modificare una risorsa | Administrator | User goal | Alta | Implementato |
| UC-06.3 | Rimuovere una risorsa | Administrator | User goal | Media | Implementato |
| UC-07 | Presentare una richiesta di evento | User | User goal | Alta | Implementato |
| UC-08 | Gestire una richiesta di evento | Administrator | User goal | Alta | Implementato |
| UC-09 | Gestire un evento | Administrator / Organiser | Summary   | Alta | Implementato |
| UC-09.1 | Creare un evento | Administrator | User goal | Alta | Implementato |
| UC-09.2 | Confermare un evento | Administrator / Organiser | User goal | Alta | Implementato |
| UC-09.3 | Pubblicare un evento | Administrator / Organiser | User goal | Alta | Implementato |
| UC-09.4 | Modificare o riprogrammare un evento | Administrator / Organiser | User goal | Media | Implementato |
| UC-09.5 | Cancellare un evento | Administrator / Organiser | User goal | Alta | Implementato |
| UC-09.6 | Assegnare o rimuovere un organiser | Administrator | User goal | Media | Implementato |
| UC-10 | Gestire la lista degli invitati | Administrator / Organiser | User goal | Media | Implementato |
| UC-11 | Prenotare biglietti | User | User goal | Alta | Implementato |
| UC-12 | Gestire una prenotazione | User / Administrator | Summary   | Alta | Implementato |
| UC-12.1 | Confermare una prenotazione | User / Administrator | User goal | Alta | Implementato |
| UC-12.2 | Cancellare una prenotazione | User / Administrator | User goal | Alta | Implementato |
| UC-12.3 | Eliminare una prenotazione | User / Administrator | User goal | Media | Implementato |
| UC-13 | Pubblicare una recensione | User | User goal | Media | Implementato |
| UC-14 | Gestire le proprie recensioni | User | User goal | Media | Implementato |
| UC-15 | Creare e consultare report | Administrator | User goal | Media | Implementato |
| UC-16 | Autenticarsi | Visitor | User goal | Alta | Implementato |

## 6. Casi esclusi dal catalogo corrente

### Effettuare un pagamento

Il sistema non integra un provider di pagamento. La conferma di una prenotazione è una transizione applicativa e non rappresenta l'esito verificato di una transazione economica.

### Ricevere notifiche

Il sistema non invia materialmente email, SMS o notifiche. Gli eventuali contatti presenti nel modello vengono soltanto memorizzati.
