package application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CompletaCodiceController 
{
    @FXML private Label titoloLabel;
    @FXML private Label livelloLabel;
    @FXML private TextArea codiceArea;
    @FXML private Label consegnaLabel;
    @FXML private TextField campoRisposta;
    @FXML private Button btnConferma;
    @FXML private Button btnEsci;
    @FXML private Label feedbackLabel;
    @FXML private ProgressBar progressBar;
    @FXML private Button btnPrincipiante;
    @FXML private Button btnIntermedio;
    @FXML private Button btnAvanzato;

    private int successiConsecutivi = 0;
    private int punteggio = 0;
    private final int nSuccessiPerLivello = 3;
    private String livelloCorrente = "Principiante";
    private final Map<String, List<Esercizio>> eserciziPerLivello = new LinkedHashMap<>();
    private final Map<String, List<Esercizio>> mostratiPerLivello = new HashMap<>();
    private Esercizio esercizioCorrente;

    public void initialize() 
    {
        feedbackLabel.setVisible(false);
        campoRisposta.setDisable(false);
        btnConferma.setDisable(false);
        caricaEsercizi();
        caricaProgresso();
        aggiornaStileLivelli();
        progressBar.setProgress(0.0);
        mostraDomandaCasuale();
    }

    private void caricaEsercizi() 
    {
        eserciziPerLivello.put("Principiante", new ArrayList<>(List.of(
            new Esercizio("Completa il codice", "Principiante", "public int somma(int a, int b) {\\n    // manca il return\\n}", "Completa la funzione per restituire la somma di a e b", "return a + b;"),
            new Esercizio("Completa il codice", "Principiante", "for(int i = 0; i < 5; i++) {\\n    // manca la stampa\\n}", "Completa il ciclo per stampare i", "System.out.println(i);"),
            new Esercizio("Completa il codice", "Principiante", "System.out.println(_____);", "Completa la stampa del messaggio 'Ciao mondo'", "\"Ciao mondo\""),
            new Esercizio("Completa il codice", "Principiante", "if (x > 0) {\\n    _____\\n}", "Stampa 'positivo' se x è maggiore di 0", "System.out.println(\"positivo\");"),
            new Esercizio("Completa il codice", "Principiante", "int numero;\\n_____", "Assegna 10 alla variabile numero", "numero = 10;")
        )));

        eserciziPerLivello.put("Intermedio", new ArrayList<>(List.of(
            new Esercizio("Completa il codice", "Intermedio", "if(nome.equals(\\\"Mario\\\")) {\\n    // manca azione\\n}", "Aggiungi il messaggio di benvenuto", "System.out.println(\"Benvenuto Mario\");"),
            new Esercizio("Completa il codice", "Intermedio", "int somma = 0;\\nfor (int i = 0; i < 5; i++) {\\n    _____\\n}", "Aggiungi i alla somma", "somma += i;"),
            new Esercizio("Completa il codice", "Intermedio", "String parola = \\\"ciao\\\";\\nif (_____) {\\n    System.out.println(\\\"ok\\\");\\n}", "Controlla che parola sia uguale a 'ciao'", "parola.equals(\"ciao\")")
        )));

        eserciziPerLivello.put("Avanzato", new ArrayList<>(List.of(
            new Esercizio("Completa il codice", "Avanzato", "int[] numeri = {1, 2, 3};\\nfor(int i = 0; i < numeri.length; i++) {\\n    // manca il controllo\\n}", "Mostra solo i numeri maggiori di 1", "if(numeri[i] > 1) System.out.println(numeri[i]);"),
            new Esercizio("Completa il codice", "Avanzato", "public int fattoriale(int n) {\\n    if (n == 0) return 1;\\n    else _____\\n}", "Completa la ricorsione per il fattoriale", "return n * fattoriale(n - 1);"),
            new Esercizio("Completa il codice", "Avanzato", "int[] numeri = {1,2,3,4};\\nfor (int n : numeri) {\\n    if (n % 2 == 0) {\\n        _____\\n    }\\n}", "Stampa solo i numeri pari", "System.out.println(n);")
        )));

        eserciziPerLivello.forEach((livello, lista) -> mostratiPerLivello.put(livello, new ArrayList<>()));
    }

    private void mostraDomandaCasuale() 
    {
        List<Esercizio> disponibili = new ArrayList<>(eserciziPerLivello.get(livelloCorrente));
        disponibili.removeAll(mostratiPerLivello.get(livelloCorrente));

        if (disponibili.isEmpty()) 
        {
            avanzaLivello();
            return;
        }

        Collections.shuffle(disponibili);
        esercizioCorrente = disponibili.get(0);
        mostratiPerLivello.get(livelloCorrente).add(esercizioCorrente);

        titoloLabel.setText(esercizioCorrente.titolo);
        //livelloLabel.setText("Livello: " + esercizioCorrente.livello);
        codiceArea.setText(esercizioCorrente.codice.replace("\\n", "\n"));
        consegnaLabel.setText(esercizioCorrente.domanda);
        campoRisposta.setText("");
        feedbackLabel.setVisible(false);
    }

    @FXML
    private void confermaRisposta(ActionEvent event) 
    {
        String rispostaUtente = campoRisposta.getText().replaceAll("\\s+", "").trim();
        String rispostaCorretta = esercizioCorrente.rispostaCorretta.replaceAll("\\s+", "").trim();

        if (rispostaUtente.isEmpty()) 
        {
            feedbackLabel.setText("Inserisci una risposta prima di confermare.");
            feedbackLabel.setStyle("-fx-text-fill: orange;");
            feedbackLabel.setVisible(true);
            return;
        }

        if (rispostaUtente.equalsIgnoreCase(rispostaCorretta)) 
        {
            feedbackLabel.setText("Corretto!");
            feedbackLabel.setStyle("-fx-text-fill: green;");
            successiConsecutivi++;
            punteggio++;
            mostraDomandaCasuale();
        } 
        else 
        {
            feedbackLabel.setText("Sbagliato! Riprova.");
            feedbackLabel.setStyle("-fx-text-fill: red;");
            successiConsecutivi = 0;
        }

        feedbackLabel.setVisible(true);
        aggiornaProgressBar();

        if(successiConsecutivi >= nSuccessiPerLivello) 
        {
            avanzaLivello();
        }
    }

    private void aggiornaProgressBar() 
    {
        double progress = (double) successiConsecutivi / nSuccessiPerLivello;
        progressBar.setProgress(progress);
    }

    private void avanzaLivello() 
    {
        successiConsecutivi = 0;
        aggiornaProgressBar();

        switch (livelloCorrente) 
        {
            case "Principiante" -> livelloCorrente = "Intermedio";
            case "Intermedio" -> livelloCorrente = "Avanzato";
            case "Avanzato" -> {
                feedbackLabel.setText("Hai completato tutti i livelli! Punteggio: " + punteggio);
                feedbackLabel.setStyle("-fx-text-fill: blue;");
                feedbackLabel.setVisible(true);
                btnConferma.setDisable(true);
                salvaRisultato();
                aggiornaStileLivelli();
                return;
            }
        }
        aggiornaStileLivelli();
        mostratiPerLivello.get(livelloCorrente).clear();
        mostraDomandaCasuale();
    }

    private void salvaRisultato() 
    {
        try (PrintWriter writer = new PrintWriter(new FileWriter(Costanti.PATH_FILE_RISULTATI, true))) 
        {
            String utente = Session.getCurrentUser();
            String data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            writer.printf("%s,%s,%d,%s\n", utente, "Completa il Codice", punteggio, data);
        } 
        catch (IOException e) 
        {
            System.err.println("Errore nel salvataggio: " + e.getMessage());
        }
    }

    private void salvaProgresso() 
    {
        String utente = Session.getCurrentUser();
        File file = new File(Costanti.PATH_FILE_PROGRESSI);
        List<String> righeAggiornate = new ArrayList<>();
    
        // Leggi tutte le righe esistenti e tieni solo quelle NON dell'utente corrente per questo esercizio
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) 
        {
            String riga;
            while ((riga = reader.readLine()) != null) 
            {
                if (!riga.startsWith(utente + ",Completa il Codice")) 
                {
                    righeAggiornate.add(riga);
                }
            }
        } 
        catch (IOException e) 
        {
            // Il file potrebbe non esistere ancora: lo creeremo sotto
        }
    
        // Aggiungi la nuova riga aggiornata
        righeAggiornate.add(String.format("%s,%s,%s,%d", utente, "Completa il Codice", livelloCorrente, successiConsecutivi));
    
        // Sovrascrivi il file
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) 
        {
            for (String r : righeAggiornate) 
            {
                writer.println(r);
            }
        } 
        catch (IOException e) 
        {
            System.err.println("Errore nel salvataggio del progresso: " + e.getMessage());
        }
    }
    
    private void caricaProgresso() 
    {
        String utente = Session.getCurrentUser();
        try (Scanner scanner = new Scanner(new File(Costanti.PATH_FILE_PROGRESSI))) 
        {
            while (scanner.hasNextLine()) 
            {
                String[] parts = scanner.nextLine().split(",");
                if (parts.length == 4 && parts[0].equals(utente) && parts[1].equals("Completa il Codice")) 
                {
                    livelloCorrente = parts[2];
                    successiConsecutivi = Integer.parseInt(parts[3]);
                    aggiornaProgressBar();
                    return;
                }
            }
        } 
        catch (IOException e) 
        {
            System.out.println("Nessun progresso precedente trovato per l'utente " + utente);
        }
    }

    @FXML
    private void tornaAlMenu(ActionEvent event) throws IOException 
    {
        salvaProgresso();
        Parent root = FXMLLoader.load(getClass().getResource(Costanti.PATH_FXML_MENU));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    private void vaiALivelloPrincipiante(ActionEvent event) 
    {
        livelloCorrente = "Principiante";
        successiConsecutivi = 0;
        aggiornaStileLivelli();
        mostraDomandaCasuale();
    }

    @FXML
    private void vaiALivelloIntermedio(ActionEvent event) 
    {
        livelloCorrente = "Intermedio";
        successiConsecutivi = 0;
        aggiornaStileLivelli();
        mostraDomandaCasuale();
    }

    @FXML
    private void vaiALivelloAvanzato(ActionEvent event) 
    {
        livelloCorrente = "Avanzato";
        successiConsecutivi = 0;
        aggiornaStileLivelli();
        mostraDomandaCasuale();
    }

    private void aggiornaStileLivelli() 
    {
        btnPrincipiante.getStyleClass().remove("selected");
        btnIntermedio.getStyleClass().remove("selected");
        btnAvanzato.getStyleClass().remove("selected");

        switch (livelloCorrente) 
        {
            case "Principiante" -> btnPrincipiante.getStyleClass().add("selected");
            case "Intermedio" -> btnIntermedio.getStyleClass().add("selected");
            case "Avanzato" -> btnAvanzato.getStyleClass().add("selected");
        }
    }

    static class Esercizio 
    {
        String titolo;
        String livello;
        String codice;
        String domanda;
        String rispostaCorretta;

        public Esercizio(String titolo, String livello, String codice, String domanda, String rispostaCorretta) 
        {
            this.titolo = titolo;
            this.livello = livello;
            this.codice = codice;
            this.domanda = domanda;
            this.rispostaCorretta = rispostaCorretta;
        }

        @Override
        public boolean equals(Object o) 
        {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) 
                return false;
            Esercizio esercizio = (Esercizio) o;
            
            return Objects.equals(codice, esercizio.codice);
        }

        @Override
        public int hashCode() 
        {
            return Objects.hash(codice);
        }
    }
}