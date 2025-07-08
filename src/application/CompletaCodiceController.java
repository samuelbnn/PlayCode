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
import java.util.*;

public class CompletaCodiceController 
{
    //region FXML Variabili
    @FXML private Label titoloLabel;
    @FXML private Label livelloLabel;
    @FXML private TextArea codiceArea;
    @FXML private Label consegnaLabel;
    @FXML private Label feedbackLabel;
    @FXML private Button btnConferma;
    @FXML private Button btnEsci;
    @FXML private HBox tacchePrincipiante;
    @FXML private HBox taccheIntermedio;
    @FXML private HBox taccheAvanzato;
    @FXML private Button btnPrincipiante;
    @FXML private Button btnIntermedio;
    @FXML private Button btnAvanzato;
    @FXML private TextField rispostaTextField; // Rinominato da campoRisposta

    //endregion

    //region Variabili    
    private String livelloCorrente = "Principiante";
    private Esercizio esercizioCorrente;
    private final Map<String, List<Esercizio>> eserciziPerLivello = new LinkedHashMap<>();
    private final Map<String, List<Esercizio>> mostratiPerLivello = new HashMap<>();
    private final Map<String, List<String>> statoTacche = new HashMap<>();
    private final Set<String> livelliCompletati = new HashSet<>();

    private static final String titolo = Costanti.ES_COMPLETA_CODICE;
    private enum Grado { PRINCIPIANTE, INTERMEDIO, AVANZATO }
    private String timestampInizioLivello = null;
    //endregion

    //region Inizializzazione
    @FXML
    public void initialize() 
    {        
        rispostaTextField.setVisible(false); // Nasconde il campo di testo inizialmente

        caricaDomande();
        caricaProgresso(); 
        inizializzaStatoTacche();
        mostraDomandaCasuale();
        aggiornaStileLivelli();
        aggiornaTacche();
        setTimestampInizioLivello();
        }
    //endregion

    //region Timer Livello
    private void setTimestampInizioLivello() 
    {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        timestampInizioLivello = now.format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
    } 

    private String getTimestampInizioLivello()
    {
        return timestampInizioLivello;
    }
    //endregion

    //region Domande
    private void caricaDomande() 
    {
        eserciziPerLivello.put(Costanti.LIVELLO_PRINCIPIANTE, List.of(
            new Esercizio(
                titolo, 
                Grado.PRINCIPIANTE, 
                "public int somma(int a, int b) {\n    // manca il return\n}", 
                "Completa la funzione per restituire la somma di a e b", 
                new String[]{"return a + b;"}, 
                0),
            new Esercizio(
                titolo, 
                Grado.PRINCIPIANTE, 
                "for(int i = 0; i < 5; i++) {\n    // manca la stampa\n}", 
                "Completa il ciclo per stampare i", 
                new String[]{"System.out.println(i);"}, 
                0),
            new Esercizio(
                titolo, 
                Grado.PRINCIPIANTE, 
                "String nome = \"Mario\";\nSystem.out.println(_____);", 
                "Completa la stampa per ottenere 'Ciao Mario'", 
                new String[]{"\"Ciao \" + nome"}, 
                0),
            new Esercizio(
                titolo, 
                Grado.PRINCIPIANTE, 
                "if (x > 0) {\n    _____\n}", 
                "Stampa 'positivo' se x è maggiore di 0", 
                new String[]{"System.out.println(\"positivo\");"}, 
                0),
            new Esercizio(
                titolo, 
                Grado.PRINCIPIANTE, 
                "int numero;\n_____", 
                "Assegna 10 alla variabile numero", 
                new String[]{"numero = 10;"}, 
                0)
        ));

        eserciziPerLivello.put(Costanti.LIVELLO_INTERMEDIO, List.of(
            new Esercizio(
                titolo, 
                Grado.INTERMEDIO, 
                "if(nome.equals(\"Mario\")) {\n    // manca azione \n}", 
                "Scrivi il messaggio: Benvenuto Mario", 
                new String[]{"System.out.println(\"Benvenuto Mario\");"}, 
                0),
            new Esercizio(
                titolo, 
                Grado.INTERMEDIO, 
                "int somma = 0;\nfor (int i = 0; i < 5; i++) {\n    _____\n}", 
                "Aggiungi i alla somma", 
                new String[]{"somma += i;"}, 
                0),
            new Esercizio(
                titolo, 
                Grado.INTERMEDIO, 
                "String parola = \"ciao\";\nif (_____) {\n    System.out.println(\"ok\");\n}", 
                "Controlla che parola sia uguale a 'ciao'", 
                new String[]{"parola.equals(\"ciao\")"}, 
                0),
            new Esercizio(
                titolo, 
                Grado.INTERMEDIO, 
                "String[] frutti = {\"mela\", \"banana\", \"kiwi\"};\nfor(String frutto : frutti) {\n    // manca la stampa\n}", 
                "Stampa ogni frutto dell'array", 
                new String[]{"System.out.println(frutto);"}, 
                0),
            new Esercizio(
                titolo, 
                Grado.INTERMEDIO, 
                "String numero = \"5\";\nint x = _____;", 
                "Converte la stringa in numero intero", 
                new String[]{"Integer.parseInt(numero)"}, 
                0)
        ));

        eserciziPerLivello.put(Costanti.LIVELLO_AVANZATO, List.of(
            new Esercizio(
                titolo, 
                Grado.AVANZATO, 
                "int[] numeri = {1, 2, 3, 4};\nfor(int i = 0; i < numeri.length; i++) {\n    if(__________) {\n       System.out.println(numeri[i]);\n    }\n}", 
                "Stampa solo i numeri maggiori di 1 e pari", 
                new String[]{"numeri[i] > 1 && numeri[i] % 2 == 0"}, 
                0),
            new Esercizio(
                titolo, 
                Grado.AVANZATO, 
                "public int fattoriale(int n) {\n    if (n == 0) return 1;\n    else _____\n}", 
                "Completa la ricorsione per il fattoriale", 
                new String[]{"return n * fattoriale(n - 1);"}, 
                0),
            new Esercizio(
                titolo, 
                Grado.AVANZATO, 
                "int[] numeri = {1, 2, 3, 4};\nfor (int n : numeri) {\n    if (_______) {\n        System.out.println(n);\n    }\n}", 
                "Stampa solo i numeri pari", 
                new String[]{"n % 2 == 0"}, 
                0),
            new Esercizio(
                titolo, 
                Grado.AVANZATO, 
                "Map<String, Integer> punteggi = new HashMap<>();\npunteggi.put(\"Alice\", 10);\npunteggi.put(\"Bob\", 8);\n// stampa punteggio di Alice", 
                "Accedi al valore associato ad 'Alice'", 
                new String[]{"System.out.println(punteggi.get(\"Alice\"));"}, 
                0),
            new Esercizio(
                titolo, 
                Grado.AVANZATO, 
                "List<String> nomi = Arrays.asList(\"Luca\", \"Marco\", \"Anna\");\nfor(String nome : nomi) {\n    // manca una condizione\n    System.out.println(nome);\n}", 
                "Scrivi la condizione per stampare solo i nomi che iniziano con la lettera 'M'.", 
                new String[]{"if(nome.startsWith(\"M\"))"}, 
                0)
        ));

        eserciziPerLivello.forEach((livello, lista) -> mostratiPerLivello.put(livello, new ArrayList<>()));
    }

    private void mostraDomandaCasuale() 
    {
        if (livelloCorrente == null) 
            return;

        if (livelliCompletati.contains(livelloCorrente)) 
        {
            feedbackLabel.setText("Hai completato questo livello!");
            feedbackLabel.setStyle("-fx-text-fill: " + Costanti.BLU + ";");
            feedbackLabel.setVisible(true);
            return;
        }

        List<String> tacchette = statoTacche.getOrDefault(livelloCorrente, new ArrayList<>());
        if (tacchette.stream().allMatch(t -> t.equals("G") || t.equals("R"))) 
        {
            feedbackLabel.setText("Hai già completato il livello!");
            feedbackLabel.setStyle("-fx-text-fill: " + Costanti.BLU + ";");
            feedbackLabel.setVisible(true);
            livelliCompletati.add(livelloCorrente);
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

            rispostaTextField.setVisible(false);            
        }

        feedbackLabel.setVisible(false);
    }

    private void completaLivello() 
    {
        livelliCompletati.add(livelloCorrente);
        feedbackLabel.setText("Hai completato il livello " + livelloCorrente + "!");
        feedbackLabel.setStyle("-fx-text-fill: " + Costanti.BLU + ";");
        feedbackLabel.setVisible(true);

        salvaRisultato();

        switch (livelloCorrente) 
        {
            case Costanti.LIVELLO_PRINCIPIANTE -> {
                livelloCorrente = "Intermedio";
                setTimestampInizioLivello();    //Reset timestamp per nuovo livello
            }
            case Costanti.LIVELLO_INTERMEDIO -> {
                livelloCorrente = Costanti.LIVELLO_AVANZATO;
                setTimestampInizioLivello();    //Reset timestamp per nuovo livello
            }
            case Costanti.LIVELLO_AVANZATO -> {
                feedbackLabel.setText("Hai completato tutti i livelli! Complimenti!");
                feedbackLabel.setStyle("-fx-text-fill: " + Costanti.VERDE + ";");
                feedbackLabel.setVisible(true);
                btnConferma.setDisable(true);
                return;
            }
        }

        aggiornaStileLivelli();
        mostratiPerLivello.get(livelloCorrente).clear();
        mostraDomandaCasuale();
    }
    //endregion

    //region Gestione Tacche
    private void inizializzaStatoTacche() 
    {
        ProgressManager progressManager = new ProgressManager();

        if (statoTacche.isEmpty()) 
        {
            statoTacche.put(Costanti.LIVELLO_PRINCIPIANTE, new ArrayList<>(Collections.nCopies(5, "")));
            statoTacche.put(Costanti.LIVELLO_INTERMEDIO, new ArrayList<>(Collections.nCopies(5, "")));
            statoTacche.put(Costanti.LIVELLO_AVANZATO, new ArrayList<>(Collections.nCopies(5, "")));
        }

        String utente = Session.getCurrentUser();
        try (Scanner scanner = new Scanner(new File(Costanti.PATH_FILE_PROGRESSI))) 
        {
            while (scanner.hasNextLine()) 
            {
                String[] parts = scanner.nextLine().split(",");
                if (progressManager.isProgressoValido(parts, utente, titolo)) 
                {
                    statoTacche.put(Costanti.LIVELLO_PRINCIPIANTE, progressManager.normalizeTacche(parts[6], 5));
                    statoTacche.put(Costanti.LIVELLO_INTERMEDIO, progressManager.normalizeTacche(parts[7], 5));
                    statoTacche.put(Costanti.LIVELLO_AVANZATO, progressManager.normalizeTacche(parts[8], 5));
                    return;
                }
            }
        } 
        catch (IOException e) 
        {
            System.out.println("Nessun progresso precedente trovato per l'utente " + utente);
        }
    }

    private void aggiornaColoreTacca(boolean rispostaCorretta) 
    {
        if (livelloCorrente == null) 
            return;

        String colore = rispostaCorretta ?  Costanti.VERDE : Costanti.ROSSO;

        switch (livelloCorrente) 
        {
            case Costanti.LIVELLO_PRINCIPIANTE -> coloraTacca(tacchePrincipiante, colore);
            case Costanti.LIVELLO_INTERMEDIO -> coloraTacca(taccheIntermedio, colore);
            case Costanti.LIVELLO_AVANZATO -> coloraTacca(taccheAvanzato, colore);
        }
    }

    private void aggiornaTacche() 
    {
        aggiornaVisualizzazioneTacche(tacchePrincipiante, statoTacche.get(Costanti.LIVELLO_PRINCIPIANTE));
        aggiornaVisualizzazioneTacche(taccheIntermedio, statoTacche.get(Costanti.LIVELLO_INTERMEDIO));
        aggiornaVisualizzazioneTacche(taccheAvanzato, statoTacche.get(Costanti.LIVELLO_AVANZATO));
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
        if (livelloCorrente == null) 
            return;

        List<String> statoCorrente = statoTacche.get(livelloCorrente);
        if (statoCorrente == null) 
            return;

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
    //endregion

    //region Risposte
    @FXML
    private void confermaRisposta(ActionEvent event) 
    {
        btnConferma.setDisable(true); // Disabilita il pulsante per evitare clic multipli

        String rispostaUtente = rispostaTextField.getText().replaceAll("\\s+", "");
        String rispostaCorretta = esercizioCorrente.risposte[esercizioCorrente.indiceCorretta].replaceAll("\\s+", "");

        if (rispostaUtente.isEmpty()) {
            feedbackLabel.setText("Inserisci una risposta!");
            feedbackLabel.setStyle("-fx-text-fill: " + Costanti.ROSSO + ";");
            feedbackLabel.setVisible(true);
            btnConferma.setDisable(false);
            return;
        }

        Esercizio domanda = esercizioCorrente;

        if (rispostaUtente.equalsIgnoreCase(rispostaCorretta)) {
            feedbackLabel.setText("Corretto!");
            feedbackLabel.setStyle("-fx-text-fill: " + Costanti.VERDE + ";");
            feedbackLabel.setVisible(true);

            if (!domanda.isAnswered) 
            {
                aggiornaColoreTacca(true); // Colora tacca verde
                domanda.isAnswered = true;
            }

            codiceArea.setStyle("-fx-border-color: " + Costanti.VERDE + "; -fx-border-width: 2;");

            new Thread(() -> {
                try 
                {
                    Thread.sleep(1500);
                } 
                catch (InterruptedException e) 
                {
                    e.printStackTrace();
                }
                javafx.application.Platform.runLater(() -> {
                    codiceArea.setStyle("");
                    rispostaTextField.clear();
                    mostraDomandaCasuale();
                    btnConferma.setDisable(false);
                });
            }).start();
        } 
        else 
        {
            feedbackLabel.setText("Sbagliato! Riprova.");
            feedbackLabel.setStyle("-fx-text-fill: " + Costanti.ROSSO + ";");
            feedbackLabel.setVisible(true);

            if (!domanda.isAnswered)
            {
                aggiornaColoreTacca(false); // Colora tacca rossa
                domanda.isAnswered = true;
            }

            codiceArea.setStyle("-fx-border-color: "+ Costanti.ROSSO + "; -fx-border-width: 2;");
            btnConferma.setDisable(false);
        }
    }
    //endregion

    //region Gestione Progresso
    private void salvaRisultato() 
    {
        ProgressManager.saveProgress(titolo, statoTacche);
        ProgressManager.salvaRisultati(titolo, livelloCorrente, getTimestampInizioLivello());
        ProgressManager.updateProgressBar(titolo, livelloCorrente);
    }

    private void caricaProgresso() 
    {
        String utente = Session.getCurrentUser();
        Map<String, List<String>> loadedProgress = ProgressManager.loadProgress(utente, titolo);

        //Conversione R e G in rosso e verde
        statoTacche.put(Costanti.LIVELLO_PRINCIPIANTE, ProgressManager.translateTacche(loadedProgress.getOrDefault(Costanti.LIVELLO_PRINCIPIANTE, new ArrayList<>()), 5));
        statoTacche.put(Costanti.LIVELLO_INTERMEDIO, ProgressManager.translateTacche(loadedProgress.getOrDefault(Costanti.LIVELLO_INTERMEDIO, new ArrayList<>()), 5));
        statoTacche.put(Costanti.LIVELLO_AVANZATO, ProgressManager.translateTacche(loadedProgress.getOrDefault(Costanti.LIVELLO_AVANZATO, new ArrayList<>()), 5));

        if (loadedProgress.getOrDefault(Costanti.LIVELLO_PRINCIPIANTE, new ArrayList<>()).stream().anyMatch(t -> t.equals("G") || t.equals("R"))) 
        {
            livelliCompletati.add(Costanti.LIVELLO_PRINCIPIANTE);
            livelloCorrente = Costanti.LIVELLO_INTERMEDIO;
        }
        if (loadedProgress.getOrDefault(Costanti.LIVELLO_INTERMEDIO, new ArrayList<>()).stream().anyMatch(t -> t.equals("G") || t.equals("R"))) 
        {
            livelliCompletati.add(Costanti.LIVELLO_INTERMEDIO);
            livelloCorrente = Costanti.LIVELLO_AVANZATO;
        }
        if (loadedProgress.getOrDefault(Costanti.LIVELLO_AVANZATO, new ArrayList<>()).stream().anyMatch(t -> t.equals("G") || t.equals("R"))) 
        {
            livelliCompletati.add(Costanti.LIVELLO_AVANZATO);

            //Pop-up per completamento dell'esercizio
            ButtonType btnRisultati = new ButtonType("Visualizza i risultati", ButtonBar.ButtonData.OK_DONE);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Hai completato tutti i livelli!", btnRisultati);
            alert.setTitle("Congratulazioni");
            alert.setHeaderText(null);
            alert.getButtonTypes().setAll(btnRisultati);
            alert.showAndWait();

            //Visualizzo risultati.fxml
            try 
            {
                FXMLLoader loader = new FXMLLoader(App.class.getResource(Costanti.PATH_FXML_RISULTATI));
                Parent root = loader.load();
                Stage stage;
                if (feedbackLabel != null && feedbackLabel.getScene() != null && feedbackLabel.getScene().getWindow() != null) {
                    stage = (Stage) feedbackLabel.getScene().getWindow();
                } else if (btnConferma != null && btnConferma.getScene() != null && btnConferma.getScene().getWindow() != null) {
                    stage = (Stage) btnConferma.getScene().getWindow();
                } else {
                    stage = new Stage();
                }
                stage.setScene(new Scene(root));
                stage.show();
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }

            livelloCorrente = null;
            feedbackLabel.setText("Hai completato tutti i livelli! Complimenti!");
            feedbackLabel.setStyle("-fx-text-fill: " + Costanti.VERDE + ";");
            feedbackLabel.setVisible(true);
            btnConferma.setDisable(true);
        }
    }
    //endregion

    //region Eventi FXML

    @FXML
    private void tornaAlMenu(ActionEvent event) throws IOException 
    {
        FXMLLoader loader = new FXMLLoader(App.class.getResource(Costanti.PATH_FXML_MENU));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    private void vaiALivelloPrincipiante(ActionEvent event) 
    {
        if (livelliCompletati.contains(Costanti.LIVELLO_PRINCIPIANTE)) 
        {
            feedbackLabel.setText("Hai già completato il livello " + Costanti.LIVELLO_PRINCIPIANTE + "!");
            feedbackLabel.setStyle("-fx-text-fill: " + Costanti.BLU + ";");
            feedbackLabel.setVisible(true);
            return;
        }

        livelloCorrente = Costanti.LIVELLO_PRINCIPIANTE;
        aggiornaStileLivelli();
        setTimestampInizioLivello(); //Reset timestamp quando si entra nel livello
    }

    @FXML
    private void vaiALivelloIntermedio(ActionEvent event) 
    {
        if (!livelliCompletati.contains(Costanti.LIVELLO_PRINCIPIANTE)) 
        {
            feedbackLabel.setText("Completa il livello " + Costanti.LIVELLO_PRINCIPIANTE + " prima di accedere a " + Costanti.LIVELLO_INTERMEDIO + "!");
            feedbackLabel.setStyle("-fx-text-fill: " + Costanti.ROSSO + ";");
            feedbackLabel.setVisible(true);
            return;
        }

        if (livelliCompletati.contains(Costanti.LIVELLO_INTERMEDIO)) 
        {
            feedbackLabel.setText("Hai già completato il livello " + Costanti.LIVELLO_INTERMEDIO+ "!");
            feedbackLabel.setStyle("-fx-text-fill: " + Costanti.BLU + ";");
            feedbackLabel.setVisible(true);
            return;
        }

        livelloCorrente = Costanti.LIVELLO_INTERMEDIO;
        aggiornaStileLivelli();
        setTimestampInizioLivello(); //Reset timestamp quando si entra nel livello
    }

    @FXML
    private void vaiALivelloAvanzato(ActionEvent event) 
    {
        if (!livelliCompletati.contains(Costanti.LIVELLO_INTERMEDIO)) 
        {
            feedbackLabel.setText("Completa il livello " + Costanti.LIVELLO_INTERMEDIO + " prima di accedere a " + Costanti.LIVELLO_AVANZATO + "!");
            feedbackLabel.setStyle("-fx-text-fill: " + Costanti.ROSSO + ";");
            feedbackLabel.setVisible(true);
            return;
        }

        if (livelliCompletati.contains(Costanti.LIVELLO_AVANZATO)) 
        {
            feedbackLabel.setText("Hai già completato il livello " + Costanti.LIVELLO_AVANZATO + "!");
            feedbackLabel.setStyle("-fx-text-fill: " + Costanti.BLU + ";");
            feedbackLabel.setVisible(true);
            return;
        }

        livelloCorrente = Costanti.LIVELLO_AVANZATO;
        aggiornaStileLivelli();
        setTimestampInizioLivello(); //Reset timestamp quando si entra nel livello
    }

    private void aggiornaStileLivelli() 
    {
        btnPrincipiante.getStyleClass().remove("selected");
        btnIntermedio.getStyleClass().remove("selected");
        btnAvanzato.getStyleClass().remove("selected");

        switch (livelloCorrente) 
        {
            case Costanti.LIVELLO_PRINCIPIANTE -> btnPrincipiante.getStyleClass().add("selected");
            case Costanti.LIVELLO_INTERMEDIO -> btnIntermedio.getStyleClass().add("selected");
            case Costanti.LIVELLO_AVANZATO -> btnAvanzato.getStyleClass().add("selected");
        }
    }
    //endregion
}