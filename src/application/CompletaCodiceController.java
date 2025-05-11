package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
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
    @FXML private Label feedbackLabel;
    @FXML private Button btnConferma;
    @FXML private Button btnEsci;
    @FXML private RadioButton risposta1;
    @FXML private RadioButton risposta2;
    @FXML private RadioButton risposta3;
    @FXML private RadioButton risposta4;
    @FXML private HBox tacchePrincipiante;
    @FXML private HBox taccheIntermedio;
    @FXML private HBox taccheAvanzato;
    @FXML private Button btnPrincipiante;
    @FXML private Button btnIntermedio;
    @FXML private Button btnAvanzato;
    @FXML private TextField rispostaTextField; // Rinominato da campoRisposta

    private ToggleGroup gruppoRisposte;
    private String livelloCorrente = "Principiante";
    private int punteggio = 0;
    private Esercizio esercizioCorrente;
    private final Map<String, List<Esercizio>> eserciziPerLivello = new LinkedHashMap<>();
    private final Map<String, List<Esercizio>> mostratiPerLivello = new HashMap<>();
    private int correctAnswers = 0;
    private int incorrectAnswers = 0;
    private final Map<String, List<String>> statoTacche = new HashMap<>(); // Mappa per memorizzare lo stato delle tacche
    private final Set<String> livelliCompletati = new HashSet<>(); // Traccia i livelli completati

    private static final String titolo = "Completa Codice";
    private enum Grado { PRINCIPIANTE, INTERMEDIO, AVANZATO }

    @FXML
    public void initialize() 
    {
        // Ensure all FXML elements are properly initialized
        assert risposta1 != null : "fx:id 'risposta1' was not injected: check your FXML file.";
        assert risposta2 != null : "fx:id 'risposta2' was not injected: check your FXML file.";
        assert risposta3 != null : "fx:id 'risposta3' was not injected: check your FXML file.";
        assert risposta4 != null : "fx:id 'risposta4' was not injected: check your FXML file.";
        assert rispostaTextField != null : "fx:id 'rispostaTextField' was not injected: check your FXML file.";

        gruppoRisposte = new ToggleGroup();
        risposta1.setToggleGroup(gruppoRisposte);
        risposta2.setToggleGroup(gruppoRisposte);
        risposta3.setToggleGroup(gruppoRisposte);
        risposta4.setToggleGroup(gruppoRisposte);

        rispostaTextField.setVisible(false); // Nasconde il campo di testo inizialmente

        caricaDomande();
        caricaProgresso(); 
        inizializzaStatoTacche(); // Inizializza lo stato delle tacche
        mostraDomandaCasuale();
        aggiornaStileLivelli();
        aggiornaTacche(); // Aggiorna la visualizzazione delle tacche per tutti i livelli
    }

    private void inizializzaStatoTacche() 
    {
        if (statoTacche.isEmpty()) 
        {
            statoTacche.put("Principiante", new ArrayList<>(Collections.nCopies(5, "")));
            statoTacche.put("Intermedio", new ArrayList<>(Collections.nCopies(5, "")));
            statoTacche.put("Avanzato", new ArrayList<>(Collections.nCopies(5, "")));
        }

        String utente = Session.getCurrentUser();
        try (Scanner scanner = new Scanner(new File(Costanti.PATH_FILE_PROGRESSI))) 
        {
            while (scanner.hasNextLine()) 
            {
                String[] parts = scanner.nextLine().split(",");
                if (isProgressoValido(parts, utente)) 
                {
                    statoTacche.put("Principiante", normalizeTacche(parts[6], 5));
                    statoTacche.put("Intermedio", normalizeTacche(parts[7], 5));
                    statoTacche.put("Avanzato", normalizeTacche(parts[8], 5));
                    return;
                }
            }
        } 
        catch (IOException e) 
        {
            System.out.println("Nessun progresso precedente trovato per l'utente " + utente);
        }
    }

    private void caricaDomande() 
    {
        eserciziPerLivello.put("Principiante", new ArrayList<>(List.of(
            new Esercizio(titolo, Grado.PRINCIPIANTE, "public int somma(int a, int b) {\\n    // manca il return\\n}", "Completa la funzione per restituire la somma di a e b", new String[]{"return a + b;"}, 0),
            new Esercizio(titolo, Grado.PRINCIPIANTE, "for(int i = 0; i < 5; i++) {\\n    // manca la stampa\\n}", "Completa il ciclo per stampare i", new String[]{"System.out.println(i);"}, 0),
            new Esercizio(titolo, Grado.PRINCIPIANTE, "System.out.println(_____);", "Completa la stampa del messaggio 'Ciao mondo'", new String[]{"\"Ciao mondo\""}, 0),
            new Esercizio(titolo, Grado.PRINCIPIANTE, "if (x > 0) {\\n    _____\\n}", "Stampa 'positivo' se x è maggiore di 0", new String[]{"System.out.println(\"positivo\");"}, 0),
            new Esercizio(titolo, Grado.PRINCIPIANTE, "int numero;\\n_____", "Assegna 10 alla variabile numero", new String[]{"numero = 10;"}, 0)
        )));

        eserciziPerLivello.put("Intermedio", new ArrayList<>(List.of(
            new Esercizio(titolo, Grado.INTERMEDIO, "if(nome.equals(\\\"Mario\\\")) {\\n    // manca azione\\n}", "Aggiungi il messaggio di benvenuto", new String[]{"System.out.println(\"Benvenuto Mario\");"}, 0),
            new Esercizio(titolo, Grado.INTERMEDIO, "int somma = 0;\\nfor (int i = 0; i < 5; i++) {\\n    _____\\n}", "Aggiungi i alla somma", new String[]{"somma += i;"}, 0),
            new Esercizio(titolo, Grado.INTERMEDIO, "String parola = \\\"ciao\\\";\\nif (_____) {\\n    System.out.println(\\\"ok\\\");\\n}", "Controlla che parola sia uguale a 'ciao'", new String[]{"parola.equals(\"ciao\")"}, 0)
        )));

        eserciziPerLivello.put("Avanzato", new ArrayList<>(List.of(
            new Esercizio(titolo, Grado.AVANZATO, "int[] numeri = {1, 2, 3};\\nfor(int i = 0; i < numeri.length; i++) {\\n    // manca il controllo\\n}", "Mostra solo i numeri maggiori di 1", new String[]{"if(numeri[i] > 1) System.out.println(numeri[i]);"},0),
            new Esercizio(titolo, Grado.AVANZATO, "public int fattoriale(int n) {\\n    if (n == 0) return 1;\\n    else _____\\n}", "Completa la ricorsione per il fattoriale", new String[]{"return n * fattoriale(n - 1);"}, 0),
            new Esercizio(titolo, Grado.AVANZATO, "int[] numeri = {1,2,3,4};\\nfor (int n : numeri) {\\n    if (n % 2 == 0) {\\n        _____\\n    }\\n}", "Stampa solo i numeri pari", new String[]{"System.out.println(n);"}, 0)
        )));

        eserciziPerLivello.forEach((livello, lista) -> mostratiPerLivello.put(livello, new ArrayList<>()));
    }

    private void mostraDomandaCasuale() 
    {
        if (livelliCompletati.contains(livelloCorrente)) 
        {
            feedbackLabel.setText("Hai completato questo livello!");
            feedbackLabel.setStyle("-fx-text-fill: blue;");
            feedbackLabel.setVisible(true);
            return;
        }

        // Check if all tacchette are filled with "G" or "R"
        List<String> tacchette = statoTacche.getOrDefault(livelloCorrente, new ArrayList<>());
        if (tacchette.stream().allMatch(t -> t.equals("G") || t.equals("R"))) 
        {
            feedbackLabel.setText("Hai già completato il livello!");
            feedbackLabel.setStyle("-fx-text-fill: blue;");
            feedbackLabel.setVisible(true);
            livelliCompletati.add(livelloCorrente); // Mark the level as completed
            return;
        }

        List<Esercizio> disponibili = new ArrayList<>(eserciziPerLivello.get(livelloCorrente));
        disponibili.removeAll(mostratiPerLivello.get(livelloCorrente));
        
        if (disponibili.isEmpty()) 
        {
            completaLivello();
            return;
        }
        
        Collections.shuffle(disponibili);
        esercizioCorrente = disponibili.get(0);
        mostratiPerLivello.get(livelloCorrente).add(esercizioCorrente);
        
        titoloLabel.setText(esercizioCorrente.titolo);
        codiceArea.setText(esercizioCorrente.codice);
        consegnaLabel.setText(esercizioCorrente.domanda);
        
        if (esercizioCorrente.risposte.length == 1) 
        {
            // Se l'esercizio richiede una risposta scritta
            risposta1.setVisible(false);
            risposta2.setVisible(false);
            risposta3.setVisible(false);
            risposta4.setVisible(false);
            rispostaTextField.setVisible(true);
            rispostaTextField.clear();
        } 
        else 
        {
            // Se l'esercizio richiede una risposta selezionabile
            if (esercizioCorrente.risposte.length < 4) 
            {
                throw new IllegalStateException("L'esercizio richiede almeno 4 risposte per i RadioButton.");
            }

            risposta1.setVisible(true);
            risposta2.setVisible(true);
            risposta3.setVisible(true);
            risposta4.setVisible(true);
            rispostaTextField.setVisible(false);

            // Mescola le risposte
            List<String> risposteMischiate = new ArrayList<>(List.of(esercizioCorrente.risposte));
            Collections.shuffle(risposteMischiate);
            
            // Imposta le risposte mescolate sui RadioButton
            risposta1.setText(risposteMischiate.get(0));
            risposta2.setText(risposteMischiate.get(1));
            risposta3.setText(risposteMischiate.get(2));
            risposta4.setText(risposteMischiate.get(3));
        }

        gruppoRisposte.selectToggle(null);
        feedbackLabel.setVisible(false);
    }

    private void completaLivello() 
    {
        livelliCompletati.add(livelloCorrente);
        feedbackLabel.setText("Hai completato il livello " + livelloCorrente + "!");
        feedbackLabel.setStyle("-fx-text-fill: blue;");
        feedbackLabel.setVisible(true);

        //Salvataggio del progresso alla chiusura del livello
        ProgressManager.saveProgress(titolo, statoTacche);

        switch (livelloCorrente) 
        {
            case "Principiante" -> livelloCorrente = "Intermedio";
            case "Intermedio" -> livelloCorrente = "Avanzato";
            case "Avanzato" -> {
                feedbackLabel.setText("Hai completato tutti i livelli! Complimenti!");
                feedbackLabel.setStyle("-fx-text-fill: green;");
                feedbackLabel.setVisible(true);
                btnConferma.setDisable(true);
                salvaRisultato();
                return;
            }
        }

        aggiornaStileLivelli();
        mostratiPerLivello.get(livelloCorrente).clear();
        mostraDomandaCasuale();
    }

    private void aggiornaColoreTacca(boolean rispostaCorretta) 
    {
        String colore = rispostaCorretta ? "green" : "red";

        switch (livelloCorrente) 
        {
            case "Principiante" -> coloraTacca(tacchePrincipiante, colore);
            case "Intermedio" -> coloraTacca(taccheIntermedio, colore);
            case "Avanzato" -> coloraTacca(taccheAvanzato, colore);
        }
    }

    private void aggiornaTacche() 
    {
        aggiornaVisualizzazioneTacche(tacchePrincipiante, statoTacche.get("Principiante"));
        aggiornaVisualizzazioneTacche(taccheIntermedio, statoTacche.get("Intermedio"));
        aggiornaVisualizzazioneTacche(taccheAvanzato, statoTacche.get("Avanzato"));
    }

    private void aggiornaVisualizzazioneTacche(HBox tacche, List<String> stato) 
    {
        for (int i = 0; i < tacche.getChildren().size(); i++) 
        {
            Node tacca = tacche.getChildren().get(i);
            tacca.setStyle(stato.get(i));
        }
    }

    private void coloraTacca(HBox tacche, String colore) 
    {
        List<String> statoCorrente = statoTacche.get(livelloCorrente);

        for (int i = 0; i < tacche.getChildren().size(); i++) 
        {
            if (statoCorrente.get(i).isEmpty()) 
            {
                statoCorrente.set(i, "-fx-background-color: " + colore + ";");
                tacche.getChildren().get(i).setStyle(statoCorrente.get(i));
                break;
            }
        }
    }

    @FXML
    private void confermaRisposta(ActionEvent event) 
    {
        btnConferma.setDisable(true); // Disabilita il pulsante per evitare clic multipli

        if (esercizioCorrente.risposte.length == 1) 
        {
            // Gestione per risposte scritte
            String rispostaUtente = rispostaTextField.getText().trim();
            if (rispostaUtente.isEmpty()) 
            {
                feedbackLabel.setText("Inserisci una risposta!");
                feedbackLabel.setStyle("-fx-text-fill: red;");
                feedbackLabel.setVisible(true);
                btnConferma.setDisable(false);
                return;
            }

            if (rispostaUtente.equals(esercizioCorrente.risposte[0])) 
            {
                feedbackLabel.setText("Corretto!");
                feedbackLabel.setStyle("-fx-text-fill: green;");
                feedbackLabel.setVisible(true);
                correctAnswers++;
                aggiornaColoreTacca(true);
            } 
            else 
            {
                feedbackLabel.setText("Sbagliato! Riprova.");
                feedbackLabel.setStyle("-fx-text-fill: red;");
                feedbackLabel.setVisible(true);
                incorrectAnswers++;
                aggiornaColoreTacca(false);
            }
        } 
        else 
        {
            RadioButton selezionata = (RadioButton) gruppoRisposte.getSelectedToggle();

            if (selezionata == null) 
            {
                feedbackLabel.setText("Seleziona una risposta!");
                feedbackLabel.setStyle("-fx-text-fill: red;");
                feedbackLabel.setVisible(true);
                btnConferma.setDisable(false); // Riabilita il pulsante
                return;
            }

            String rispostaSelezionata = selezionata.getText();
            Esercizio domanda = esercizioCorrente;

            if (rispostaSelezionata.equals(domanda.risposte[domanda.indiceCorretta])) 
            {
                feedbackLabel.setText("Corretto!");
                feedbackLabel.setStyle("-fx-text-fill: green;");
                feedbackLabel.setVisible(true);

                if (!domanda.isAnswered) 
                {
                    correctAnswers++;
                    aggiornaColoreTacca(true); // Colora tacca verde
                    domanda.isAnswered = true; // Segna la domanda come già risolta
                }

                codiceArea.setStyle("-fx-border-color: green; -fx-border-width: 2;");

                new Thread(() -> {
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    javafx.application.Platform.runLater(() -> {
                        codiceArea.setStyle("");
                        mostraDomandaCasuale();
                        btnConferma.setDisable(false); // Riabilita il pulsante
                    });
                }).start();
            } 
            else 
            {
                feedbackLabel.setText("Sbagliato! Riprova.");
                feedbackLabel.setStyle("-fx-text-fill: red;");
                feedbackLabel.setVisible(true);

                if (!domanda.isAnswered) // Colora la tacca solo alla prima risposta
                {
                    incorrectAnswers++;
                    aggiornaColoreTacca(false); // Colora tacca rossa
                    domanda.isAnswered = true; // Segna la domanda come già risolta
                }

                codiceArea.setStyle("-fx-border-color: red; -fx-border-width: 2;");
                btnConferma.setDisable(false); // Riabilita il pulsante
            }
        }
    }

    private void salvaRisultato() 
    {
        String utente = Session.getCurrentUser();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // Build the result entry for all levels
        StringBuilder resultEntry = new StringBuilder(utente);
        resultEntry.append(",["+ titolo + " ");

        for (String livello : List.of("Principiante", "Intermedio", "Avanzato")) 
        {
            List<String> tacche = statoTacche.getOrDefault(livello, new ArrayList<>());
            long correctAnswers = tacche.stream().filter(t -> t.equals("G")).count();
            resultEntry.append(String.format(" (%s; %d;%s)", livello, correctAnswers, timestamp));
        }

        resultEntry.append("]");

        // Append the result to the risultati.csv file
        try (PrintWriter writer = new PrintWriter(new FileWriter(Costanti.PATH_FILE_RISULTATI, true))) 
        {
            writer.println(resultEntry.toString());
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }

    private void caricaProgresso() 
    {
        String utente = Session.getCurrentUser();
        Map<String, List<String>> loadedProgress = ProgressManager.loadProgress(utente, titolo);

        // Convert R (red) or G (green) back to styles
        statoTacche.put("Principiante", translateTacche(loadedProgress.getOrDefault("Principiante", new ArrayList<>()), 5));
        statoTacche.put("Intermedio", translateTacche(loadedProgress.getOrDefault("Intermedio", new ArrayList<>()), 5));
        statoTacche.put("Avanzato", translateTacche(loadedProgress.getOrDefault("Avanzato", new ArrayList<>()), 5));

        if (loadedProgress.getOrDefault("Principiante", new ArrayList<>()).stream().anyMatch(t -> t.equals("G") || t.equals("R"))) 
        {
            livelliCompletati.add("Principiante");
            livelloCorrente = "Intermedio";
        }
        if (loadedProgress.getOrDefault("Intermedio", new ArrayList<>()).stream().anyMatch(t -> t.equals("G") || t.equals("R"))) 
        {
            livelliCompletati.add("Intermedio");
            livelloCorrente = "Avanzato";
        }
        if (loadedProgress.getOrDefault("Avanzato", new ArrayList<>()).stream().anyMatch(t -> t.equals("G") || t.equals("R"))) 
        {
            livelliCompletati.add("Avanzato");

            // Show pop-up for completion
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Congratulazioni");
            alert.setHeaderText(null);
            alert.setContentText("Hai completato tutti i livelli!");
            alert.showAndWait();

            // Prevent entry into the "Avanzato" level
            livelloCorrente = null;
            feedbackLabel.setText("Hai completato tutti i livelli! Complimenti!");
            feedbackLabel.setStyle("-fx-text-fill: green;");
            feedbackLabel.setVisible(true);
            btnConferma.setDisable(true);
        }

        // Debug: Print loaded progress for verification
        System.out.println("Progress loaded for user: " + utente);
        System.out.println("Principiante: " + statoTacche.get("Principiante"));
        System.out.println("Intermedio: " + statoTacche.get("Intermedio"));
        System.out.println("Avanzato: " + statoTacche.get("Avanzato"));
    }

    private List<String> translateTacche(List<String> tacche, int expectedSize) 
    {
        List<String> translatedTacche = new ArrayList<>();
        for (String tacca : tacche) 
        {
            if ("G".equals(tacca)) 
            {
                translatedTacche.add("-fx-background-color: green;");
            } 
            else if ("R".equals(tacca)) 
            {
                translatedTacche.add("-fx-background-color: red;");
            } 
            else 
            {
                translatedTacche.add("");
            }
        }
        while (translatedTacche.size() < expectedSize) 
        {
            translatedTacche.add(""); // Add empty entries if missing
        }
        return translatedTacche.subList(0, expectedSize); // Ensure the list is trimmed to the expected size
    }

    private boolean isProgressoValido(String[] parts, String utente) 
    {
        return parts.length >= 9 && parts[0].equals(utente) && parts[1].equals(titolo);
    }

    private List<String> normalizeTacche(String taccheString, int expectedSize) 
    {
        List<String> tacche = new ArrayList<>(Arrays.asList(taccheString.split(";")));
        while (tacche.size() < expectedSize) 
        {
            tacche.add(""); // Aggiungi tacche vuote se mancano
        }
        return tacche.subList(0, expectedSize); // Troncamento se ci sono più tacche del previsto
    }

    @FXML
    private void tornaAlMenu(ActionEvent event) throws IOException 
    {
        FXMLLoader loader = new FXMLLoader(App.class.getResource(Costanti.PATH_FXML_MENU));
        Parent root = loader.load();
        MenuController menuController = loader.getController();
        menuController.updateProgress(titolo, correctAnswers, incorrectAnswers);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    private void vaiALivelloPrincipiante(ActionEvent event) 
    {
        if (livelliCompletati.contains("Principiante")) 
        {
            feedbackLabel.setText("Hai già completato il livello Principiante!");
            feedbackLabel.setStyle("-fx-text-fill: blue;");
            feedbackLabel.setVisible(true);
            return;
        }

        livelloCorrente = "Principiante";
        aggiornaStileLivelli();
    }

    @FXML
    private void vaiALivelloIntermedio(ActionEvent event) 
    {
        if (!livelliCompletati.contains("Principiante")) 
        {
            feedbackLabel.setText("Completa il livello Principiante prima di accedere a Intermedio!");
            feedbackLabel.setStyle("-fx-text-fill: red;");
            feedbackLabel.setVisible(true);
            return;
        }

        if (livelliCompletati.contains("Intermedio")) 
        {
            feedbackLabel.setText("Hai già completato il livello Intermedio!");
            feedbackLabel.setStyle("-fx-text-fill: blue;");
            feedbackLabel.setVisible(true);
            return;
        }

        livelloCorrente = "Intermedio";
        aggiornaStileLivelli();
    }

    @FXML
    private void vaiALivelloAvanzato(ActionEvent event) 
    {
        if (!livelliCompletati.contains("Intermedio")) 
        {
            feedbackLabel.setText("Completa il livello Intermedio prima di accedere a Avanzato!");
            feedbackLabel.setStyle("-fx-text-fill: red;");
            feedbackLabel.setVisible(true);
            return;
        }

        if (livelliCompletati.contains("Avanzato")) 
        {
            feedbackLabel.setText("Hai già completato il livello Avanzato!");
            feedbackLabel.setStyle("-fx-text-fill: blue;");
            feedbackLabel.setVisible(true);
            return;
        }

        livelloCorrente = "Avanzato";
        aggiornaStileLivelli();
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
}