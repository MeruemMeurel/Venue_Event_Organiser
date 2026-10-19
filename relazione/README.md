# Relazione di Ingegneria del Software

## Scelta di lavoro

LaTeX nel repository del progetto, con Git per condividere e revisionare i contributi.
Editor consigliato: VS Code con **LaTeX Workshop**, più una distribuzione **TeX Live**.
L'estensione non contiene il compilatore: TeX Live deve essere installato separatamente.
È possibile usare un altro editor mantenendo gli stessi sorgenti e comando di compilazione.

Questa è una prima bozza di struttura, non la relazione completa. Le parti da completare
sono evidenziate nel documento. Non sono ancora stati inseriti risultati quantitativi dei test.

## Riferimenti UniFi verificati il 18 ottobre 2026

- [Identità visiva di Ateneo](https://www.unifi.it/it/ateneo/comunicazione/identita-visiva).
- [Frontespizio ufficiale aggiornato nel 2026, DOCX](https://www.unifi.it/sites/default/files/2026-02/facsimile_frontespiziotesi.docx).
- [Stile LaTeX pubblicato dal CdL in Informatica](https://www.informatica.unifi.it/vp-187-stile-latex-per-la-tesi.html).
- [Pacchetto LaTeX del CdL in Informatica, ZIP](https://www.informatica.unifi.it/upload/sub/documenti/ThesisGuide.zip).

Il pacchetto del CdL contiene una tesi di esempio basata su `scrbook` e ClassicThesis,
con capitoli separati, bibliografia e frontespizio per un candidato. Non è un template
universale imposto dall'Ateneo a tutti i corsi. La pagina del CdL riporta un aggiornamento
del 05/12/2023; il frontespizio centrale del 2026 è un riferimento distinto.

Lo scheletro presente qui è **una proposta originale per il project work**, basata sulla
classe standard `report`: non è una copia né una versione ufficiale del template UniFi.
Propone A4, 11 pt, stampa su un lato, margini di 25 mm, indice e capitoli separati.
Queste sono scelte editoriali del gruppo, non requisiti di Ateneo.
Il frontespizio va completato con corso di laurea/scuola, autori, matricole e anno accademico;
l'eventuale marchio va ricavato dal materiale ufficiale e verificato prima della consegna.
Non attribuire alla relazione la dicitura "Tesi di laurea".

## Compilazione

Aprire questa cartella in VS Code, installare l'estensione suggerita e compilare `main.tex`.
In alternativa, da questa cartella:

```powershell
latexmk -pdf -interaction=nonstopmode -halt-on-error -outdir=build main.tex
```

Il risultato è `build/main.pdf`. I file generati non vanno committati.

In VS Code, con `main.tex` aperto:

- `Ctrl+Maiusc+B`: compila con il task del progetto.
- `Ctrl+Alt+V`: apre l'anteprima PDF di LaTeX Workshop.
- Il salvataggio di un sorgente LaTeX avvia la compilazione automatica.

Aprire **la cartella `relazione`** come cartella di lavoro, così VS Code carica le
impostazioni e il task qui inclusi. Dopo l'installazione di TeX Live riavviare
l'editor e i terminali già aperti, perché rileggano il PATH aggiornato.

### Ambiente verificato il 18 ottobre 2026

Sul PC di configurazione sono stati installati e verificati:

- VS Code 1.141.2, installazione per utente;
- LaTeX Workshop 10.20.0;
- TeX Live 2026 con supporto per l'italiano;
- pdfTeX 1.40.29 e latexmk 4.88.

La compilazione dello scheletro con la ricetta sopra ha prodotto 13 pagine,
senza errori o avvisi LaTeX, con controllo visivo delle pagine renderizzate.
Il documento contiene ancora i segnaposto editoriali e i metadati da completare.

Per predisporre un altro PC Windows, installare VS Code, LaTeX Workshop e TeX Live
dal [sito ufficiale](https://tug.org/texlive/acquire-netinstall.html).
Se si sceglie un'installazione TeX Live ridotta, i pacchetti aggiuntivi usati qui
possono essere installati con:

```powershell
tlmgr install collection-langitalian collection-latexrecommended collection-fontsrecommended latexmk enumitem placeins
```

Non occorre installare Perl separatamente: TeX Live per Windows lo include.
Verificare dal terminale nuovo `pdflatex --version` e `latexmk -v` prima di compilare.

## Scrivere contemporaneamente

1. Concordare nel file `PIANO.md` un referente per ciascun capitolo. La suddivisione è
   provvisoria: va confermato chi sta producendo quali diagrammi.
2. Aggiornare `main` prima di iniziare; creare un branch breve `docs/relazione-<argomento>`
   oppure usare le convenzioni di branch già concordate dal gruppo.
3. Scrivere nel proprio file sotto `sezioni/`. Ogni capitolo è caricato da `main.tex` tramite
   `\input`: il file di capitolo non deve contenere `\documentclass` o `\begin{document}`.
4. Committare contributi piccoli e aprire una PR per capitolo o argomento. Un collega
   controlla correttezza e coerenza con codice, casi d'uso e figure prima del merge.
5. Un referente integra modifiche a `main.tex`, `config/`, `frontespizio.tex` e alla
   bibliografia, così i file condivisi cambiano raramente in parallelo.
6. Durante la revisione finale lavorare a turno sullo stesso capitolo, oppure suddividerlo
   ulteriormente in file. Git permette il lavoro parallelo, ma non è un editor in tempo reale.

Regole di scrittura:

- Usare UTF-8 e una frase per riga, con una riga vuota tra paragrafi: rende più leggibili i diff.
- Conservare gli ID dei casi d'uso già presenti in `docs/USE-CASES.md`.
- Usare etichette stabili: `cap:`, `sec:`, `fig:`, `tab:`, `lst:` seguite da un nome significativo.
  Richiamare le figure con `\ref`, senza scrivere manualmente il loro numero.
- Ogni figura deve avere una didascalia e una spiegazione nel testo.
- I sorgenti dei diagrammi rimangono in `docs/diagrams/`; le figure approvate per la
  relazione vengono esportate in PDF vettoriale sotto `figure/`, con nome riconoscibile.
  Non mantenere una seconda copia modificabile dei `.puml`.
- Per la compilazione base usare PDF/PNG/JPG; convertire gli SVG prima dell'inserimento.
  Non richiedere `shell-escape` per compilare il documento.
- Usare frammenti di codice brevi e motivati, preferibilmente inclusi dal sorgente tramite
  `\lstinputlisting`; registrare il commit del codice cui si riferisce la versione finale.
- Non riportare conteggi test, coverage, prestazioni o verifiche PostgreSQL senza risultati
  riproducibili; distinguere test eseguiti, falliti e saltati.
- `\bozza{...}` marca un punto ancora aperto: eliminare tutti questi richiami prima della consegna.

## Alternative valutate

Overleaf è utile per scrittura nel browser e revisione in tempo reale. Al controllo del
18/10/2026 il piano gratuito ammette un solo collaboratore invitato, quindi due persone
complessive. La sincronizzazione GitHub è premium e viene avviata esplicitamente,
non è una sincronizzazione automatica continua. Per questo gruppo è stata scelta
la soluzione gratuita locale con Git.

Fonti:
[piani Overleaf](https://www.overleaf.com/user/subscription/plans),
[sincronizzazione GitHub](https://docs.overleaf.com/integrations-and-add-ons/git-integration-and-github-synchronization/github-synchronization),
[installazione LaTeX Workshop](https://github.com/James-Yu/latex-workshop/wiki/Install).
