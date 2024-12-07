**PLAY**

Applicazione Java-FX

Architettura PLAY

Si avvia l’applicazione:

-l’utente deve far accesso all’applicazione con una
semplice autenticazione

-Si accede a un’interfaccia in cui si vedono i diversi tipi di
esercizi, volti all’insegnamento della programmazione,
raggruppati per tipologia e grado.

-l’applicazione visualizza una barra di avanzamento di ogni
esercizio a seconda del livello raggiunto.

-l’utente attiva un esercizio e gioca utilizzando il mouse o la
tastiera a seconda dell’esercizio scelto.

Workflow PLAY
1. L’utente si autentica (tramite username e password si
legge il file se ne è in possesso) o crea l’utenza con il
pulsante dedicato, si scrive il dato su file
2. L’utente accede alla videata iniziale, griglia divisa per
tipologia di esercizio con le immagini degli esercizi e
indicazione sul grado di apprendimento raggiunto.
3. L’utente clicca sull’icona o la descrizione e visualizza una
breve descrizione dell’esercizio, Quando ha letto se
preme ok accede all’esercizio, altrimenti torna al punto 2
con annulla.
4. L’utente preme il pulsante inizia e svolge l’esercizio. Alla
fine se supera l’esercizio, se ne mostra uno simile fino al
raggiungimento di n successi. Se supera gli n esercizi si
passa al grado di difficoltà successivo se esiste.
5. In ogni momento l’utente può sospendere l’esercizio in
esecuzione con il pulsante esci dall’esercizio, se
l’esercizio non è ancora stato concluso sarà considerato
come un fallimento, altrimenti sarà valutato. Si torna a 2.
6. A fine partita il risultato viene visualizzato su una
maschera e contestualmente salvato (su file). Sul file si
salvano nome utente, numero e nome esercizio, se
superato o fallito il tentativo, e grado raggiunto
7. L’utente può fare logout o chiudere l’applicazione in ogni
momento e il sistema dovrà salvare i dati relativi al login
su di un file.

Gli esercizi da presentare sono scelti dai gruppi, ma ci sono
alcuni vincoli OBBLIGATORI:

-Almeno un esercizio per ogni componente del gruppo

-Ogni esercizio deve considerare un ambito diverso di
comprensione del linguaggio di programmazione (es.
lettura del codice, l’ordine delle istruzioni, capacità di
individuare gli errori, scrittura del codice di esercizi, uso e
comprensione del polimorfismo e dell’ereditarietà,
valutazione del codice)

-In ogni esercizio è formato da almeno 3 esercizi diversi
per grado di difficoltà (gli esercizi per lo stesso grado
sono simili tra loro, es. stessa struttura ma valori diversi)
Gli esercizi possono essere progettati anche con Chatgpt, nella consegna va indicato il link della conversazione con chat.

PLAY memorizza esercizi,risultati parziali e utenti sul filesystem.

Scelta libera del formato dei dati e le librerie per fare il parsing.

Necessari controlli di consistenza sui dati.

Strategia libera per la gestione degli errori, purchè sia documentata.

[Opzionale] Uso del database.

