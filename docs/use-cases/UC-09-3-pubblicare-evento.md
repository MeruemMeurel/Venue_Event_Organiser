# UC-09.3 - Pubblicare un evento

| Campo | Contenuto |
|---|---|
| **ID** | UC-09.3 |
| **Titolo** | Pubblicare un evento |
| **Livello** | User goal |
| **Attore principale** | Administrator / Organiser assegnato |
| **Attori secondari** | Nessuno |
| **Descrizione** | Un attore autorizzato rende visibile e prenotabile un evento confermato e valido. |
| **Trigger** | L'attore richiede la pubblicazione di un evento. |

## Precondizioni

1. L'evento esiste ed è `CONFIRMED`.
2. L'attore è Administrator oppure Organiser assegnato all'evento.

## Postcondizioni di successo

1. L'evento è `PUBLISHED`.
2. `publishedAt` contiene l'istante della pubblicazione.

## Garanzia minima

Se autorizzazione, stato o dati non sono validi, evento e data di pubblicazione rimangono invariati.

## Flusso principale

1. L'attore seleziona un evento confermato e ne richiede la pubblicazione.
2. Il sistema blocca l'evento e identifica l'attore.
3. Il sistema verifica che l'attore sia Administrator o Organiser assegnato.
4. Il sistema verifica la transizione `CONFIRMED` → `PUBLISHED`.
5. Il sistema verifica che l'inizio sia futuro, la capienza positiva e la venue esistente.
6. Il sistema imposta stato e istante di pubblicazione.
7. Il sistema conferma la transazione.

## Flussi alternativi ed eccezioni

### 2a. Evento o attore inesistente

1. Il sistema segnala l'elemento mancante.
2. Il caso d'uso termina senza modifiche.

### 3a. Attore non autorizzato

1. Il sistema rileva che lo User non è Administrator né Organiser assegnato.
2. La pubblicazione viene negata.

### 4a. Stato incompatibile

1. Il sistema rileva che l'evento non è `CONFIRMED`, oppure è già `PUBLISHED` o `CANCELLED`.
2. Il sistema rifiuta la transizione.

### 5a. Evento non pubblicabile

1. Il sistema rileva una data di inizio non futura, capienza non positiva o venue inesistente.
2. Il sistema rifiuta la pubblicazione.

## Regole di business correlate

- **BR-16:** un evento è pubblicabile solo se confermato, futuro, con capienza positiva e venue esistente.
- L'Administrator può gestire ogni evento; l'Organiser soltanto quelli a lui assegnati.

## Test correlati

- `EventServiceWorkflowTest.publishShouldSetStatusAndCurrentPublicationTime`.
- `EventServiceWorkflowTest.assignedOrganiserShouldBeAllowedToManageItsEvent`.
- `EventServiceWorkflowTest.unrelatedUserShouldNotBeAllowedToManageEvent`.
- `StatusTransitionTest.eventAllowsExpectedTransitions` e `eventRejectsBackwardAndDuplicateTransitions`.

