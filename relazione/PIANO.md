# Prima bozza dell'indice e ripartizione

Il piano riguarda la relazione, non assegna nuovi lavori ai colleghi senza accordo.
Non occorre attendere il completamento di tutti i diagrammi per scrivere i capitoli testuali.

L'indice è stato confrontato con le note del corso, le due relazioni di esempio,
i diagrammi e alcuni flussi del codice e copre i blocchi principali.
La verifica del testo e delle figure finali è ancora da completare.

| Capitolo | Contenuto e risultato atteso | Fonti del progetto | Referente proposto |
|---|---|---|---|
| 1. Contesto, obiettivi e strumenti | Statement sintetico, perimetro del nucleo applicativo, tecnologie e organizzazione del documento | README, pom.xml | Tu, con supporto nella stesura |
| 2. Requisiti e casi d'uso | Attori, catalogo UC, template dei flussi significativi, regole e tracciabilità | docs/USE-CASES.md, docs/use-cases/, diagrammi UC | Da concordare con il collega dei diagrammi |
| 3. Modello del dominio e cicli di vita | Concetti, ruoli, diagrammi di dominio, stati e invarianti | docs/diagrams/domain/, docs/diagrams/states/ | Da concordare con il collega dei diagrammi |
| 4. Architettura e responsabilità | Package e dipendenze, service/repository/JDBC, pattern effettivamente presenti e compromessi | docs/diagrams/architecture/, sorgenti Java | Tu, revisione incrociata |
| 5. Progettazione della base di dati | E-R, dizionario essenziale, modello relazionale, PK/FK, vincoli e regole applicative, migration | E-R del collega, migration Flyway, repository JDBC | Collega E-R per la parte concettuale; parte relazionale da assegnare |
| 6. Flussi implementativi significativi | Prenotazione e concorrenza, autenticazione/password, autorizzazioni, atomicità; sequence diagram pertinenti | service, TransactionManager, PasswordHasher, test e sequence diagram quando disponibili | Tu per il testo; diagrammi da coordinare |
| 7. Strategia di test e risultati | Test funzionali e strutturali, legame con UC, integrazione PostgreSQL, concorrenza, coverage e limiti | src/test/, risultati di una nuova esecuzione, JaCoCo | Tu, con verifica del gruppo |
| 8. Processo di sviluppo e uso dell'IA | Git e revisione, ripartizione effettiva del lavoro, strumenti IA usati, verifica umana e limiti | Cronologia Git e ricostruzione condivisa delle attività | Tutti e tre |
| 9. Conclusioni e sviluppi futuri | Risultati rispetto agli obiettivi, limiti reali, estensioni motivate | Capitoli precedenti e stato finale verificato | Tutti e tre |
| Appendice A | Matrice UC-regola-test | UC e test verificati | Integrazione finale |
| Appendice B | Mockup visivi e possibile navigazione, come complemento all'analisi | Futuri mockup collegati a UC e template | Strumento e referente da concordare |

## Ordine pratico di stesura

1. Completare metadati e approvare l'indice; verificare la prima compilazione.
2. Scrivere statement, perimetro e architettura mentre i colleghi completano i diagrammi.
3. Integrare UC, dominio, stati ed E-R senza duplicare integralmente tutti i documenti tecnici.
4. Completare il modello relazionale e due flussi significativi, verificati sui sorgenti.
5. Eseguire e documentare i test, poi scrivere risultati e limiti.
6. Completare uso dell'IA, conclusioni, bibliografia e revisione incrociata testo-codice-figure.

## Criteri editoriali

- Nel capitolo 1 anticipare brevemente l'architettura generale; approfondire package,
  classi significative e collaborazioni nei capitoli 4 e 6.
- Nel capitolo 3 usare esplicitamente le tre viste del dominio e i cinque cicli di vita;
  nel capitolo 5 distinguere E-R, dizionario dati, modello relazionale e regole.
- Mantenere separati il flusso delle prenotazioni per eventi pubblici e quello della
  lista invitati privata: il codice corrente non consente prenotazioni di eventi privati.

- Come ordine di grandezza iniziale: circa 30-45 pagine di corpo, da adattare alla leggibilità
  e alle indicazioni del docente; non è una prescrizione né un obiettivo da raggiungere artificialmente.
- Selezionare UC che mostrino regole e alternative significative, ad esempio UC-01,
  UC-07/08, UC-09.3, UC-11, UC-13. Il catalogo completo resta riconoscibile e tracciabile.
- È prevista un'appendice finale di mockup puramente visivi, con riferimenti ai
  casi d'uso e ai passi dei template. Lo strumento per realizzarli verrà scelto successivamente.
  Una navigazione essenziale potrà accompagnare le schermate come proposta progettuale.
  Non presentare mockup e navigazione come un'interfaccia implementata.
  L'appendice integra i requisiti senza diventare un capitolo centrale; se utile,
  i template del capitolo 2 possono rimandare alle schermate tramite i loro identificatori.
- Il riferimento SunSpot è utile per tracciabilità UC-regole-test, spiegazioni dei flussi,
  dizionario dati e discussione delle decisioni; la sua architettura non va attribuita al nostro codice.
- Le note del project work del gennaio 2025 guidano i contenuti: statement, analisi,
  progettazione, dettagli implementativi significativi, test, E-R e modello relazionale,
  uso documentato dell'IA. Gli esempi di tesi guidano l'impaginazione, non l'indice del project work.

## Punti da confermare

- Nomi, matricole, corso di laurea/scuola e anno accademico.
- Compiti attuali del secondo collega e responsabilità del modello relazionale.
- Quali figure sono già revisionate dal gruppo e quali sono ancora provvisorie.
- Allineamento degli ambienti dei colleghi alla configurazione verificata nel README
  e commit finale del codice oggetto della relazione.
