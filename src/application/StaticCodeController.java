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

public class StaticCodeController 
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

    private static final String titolo= "Static Code";
    private enum Grado { PRINCIPIANTE, INTERMEDIO, AVANZATO }

    @FXML
    public void initialize() 
    {
        gruppoRisposte = new ToggleGroup();
        risposta1.setToggleGroup(gruppoRisposte);
        risposta2.setToggleGroup(gruppoRisposte);
        risposta3.setToggleGroup(gruppoRisposte);
        risposta4.setToggleGroup(gruppoRisposte);

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
        eserciziPerLivello.put("Principiante", List.of(
        new Esercizio
        (
            "Accesso a variabile statica",
            Grado.PRINCIPIANTE,
            "public class Test {\n    static int x = 5;\n    public static void main(String[] args) {\n        System.out.println(x);\n    }\n}",
            "Cosa stampa questo codice?",
            new String[]{"Errore di compilazione", "null", "5", "0"},
            2
        ),

        new Esercizio
        (
            "Definizione di static",
            Grado.PRINCIPIANTE,
            "",
            "Cosa significa che una variabile è dichiarata static?",
            new String[]
            {
                "Appartiene all'oggetto",
                "Appartiene alla classe e non all’istanza",
                "Può essere usata solo nel main",
                "Non può cambiare valore"
            },
            1
        ),
        
        new Esercizio
        (
            "Modifica di campo statico",
            Grado.PRINCIPIANTE,
            "public class Test {\n    static int x = 10;\n    public static void main(String[] args) {\n        Test a = new Test();\n        Test b = new Test();\n        a.x = 20;\n        System.out.println(b.x);\n    }\n}",
            "Cosa stampa questo codice?",
            new String[]{"10", "20", "Errore", "null"},
            1
        ),
        
        new Esercizio
        (
            "Metodo statico",
            Grado.PRINCIPIANTE,
            "public class Utility {\n    static void greet() {\n        System.out.println(\"Ciao!\");\n    }\n}",
            "Come si può chiamare il metodo greet da un’altra classe?",
            new String[]{"new Utility().greet();", "Utility->greet();", "greet();", "Utility.greet();"},
            3
        ),
        
        new Esercizio
        (
            "Metodo statico vs. istanza",
            Grado.PRINCIPIANTE,
            "",
            "Quale tra le seguenti affermazioni è corretta riguardo ai metodi statici?",
            new String[]
            {
                "Un metodo statico può accedere direttamente a campi non statici",
                "Un metodo statico può essere chiamato senza creare un oggetto",
                "I metodi statici devono restituire un valore",
                "Un metodo statico può essere sovrascritto nelle sottoclassi"
            },
            1
        )));

        eserciziPerLivello.put("Intermedio", List.of(
        new Esercizio
        (
            "Incremento di campo statico",
            Grado.INTERMEDIO,
            "public class Contatore {\n    static int count = 0;\n    public Contatore() {\n        count++;\n    }\n    public static void main(String[] args) {\n        new Contatore();\n        new Contatore();\n        System.out.println(count);\n    }\n}",
            "Cosa stampa questo codice?",
            new String[]{"0", "1", "2", "Errore"},
            2
        ),

        new Esercizio
        (
            "Accesso a variabili di istanza da metodo statico",
            Grado.INTERMEDIO,
            "",
            "Perché un metodo statico **non può accedere direttamente** a variabili d’istanza?",
            new String[]
            {
                "Perché non esistono nel contesto statico",
                "Perché sono sempre private",
                "Perché non sono inizializzate",
                "Perché Java lo impedisce per motivi di sicurezza"
            },
            0
        ),
        
        new Esercizio
        (
            "Blocco statico",
            Grado.INTERMEDIO,
            "public class Test {\n    static {\n        System.out.println(\"Inizializzazione\");\n    }\n    public static void main(String[] args) {\n        System.out.println(\"Main\");\n    }\n}",
            "Cosa stampa questo codice?",
            new String[]{"Main\nInizializzazione", "Solo Main", "Errore", "Inizializzazione\nMain"},
            3
        ),
        
        new Esercizio
        (
            "Ordinamento dei blocchi statici",
            Grado.INTERMEDIO,
            "class Esempio {\n    static int x;\n    static {\n        x = 10;\n    }\n}",
            "Quando viene eseguito il blocco statico?",
            new String[]{
                "Ogni volta che si crea un oggetto",
                "Al caricamento della classe",
                "Alla chiamata del costruttore",
                "Alla fine del programma"
            },
            1
        ),
        
        new Esercizio
        (
            "Parola chiave this in static",
            Grado.INTERMEDIO,
            "",
            "Cosa succede se si usa `this` in un metodo statico?",
            new String[]{
                "Compila ma non ha effetto",
                "Richiama il costruttore",
                "Compila ma genera un warning",
                "Errore di compilazione: `this` non può essere usato in contesto statico"
            },
            3
        )));

        eserciziPerLivello.put("Avanzato", List.of(
        new Esercizio
        (
            "Conflitto tra variabili statiche",
            Grado.AVANZATO,
            "class A {\n    static int x = 10;\n}\nclass B extends A {\n    static int x = 20;\n}\npublic class Main {\n    public static void main(String[] args) {\n        System.out.println(B.x);\n        System.out.println(A.x);\n    }\n}",
            "Cosa stampa il codice?",
            new String[]{"20 e 10", "10 e 10", "20 e 20", "Errore di compilazione"},
            0
        ),

        new Esercizio
        (
            "Sovrascrittura di metodo statico",
            Grado.AVANZATO,
            "class Base {\n    static void print() {\n        System.out.println(\"Base\");\n    }\n}\nclass Derivata extends Base {\n    static void print() {\n        System.out.println(\"Derivata\");\n    }\n}",
            "Cosa succede se chiami `Base.print()` e `Derivata.print()`?",
            new String[]
            {
                "Entrambe stampano Derivata",
                "Stampa Base due volte",
                "Ogni classe stampa il proprio metodo: Base e poi Derivata",
                "Errore di override"
            },
            2
        ),
        
        new Esercizio
        (
            "Uso corretto di campo static finale",
            Grado.AVANZATO,
            "public class Costanti {\n    static final double PI = 3.1415;\n}",
            "Qual è il vantaggio di usare `static final`?",
            new String[]
            {
                "Può essere modificato in runtime",
                "Non occupa memoria",
                "È condiviso da tutte le istanze ed è costante",
                "Viene inizializzato ogni volta che si crea un oggetto"
            },
            2
        ),
        
        new Esercizio
        (
            "Compilazione campo statico derivato",
            Grado.AVANZATO,
            "class Genitore {\n    static int x = 5;\n}\nclass Figlio extends Genitore {\n    static int y = x * 2;\n}",
            "Cosa stampa `System.out.println(Figlio.y);`?",
            new String[]{"5", "10", "0", "Errore"},
            1
        ),
        
        new Esercizio
        (
            "Accesso statico tramite oggetto",
            Grado.AVANZATO,
            "class Esempio {\n    static void stampa() {\n        System.out.println(\"Statica\");\n    }\n}\npublic class Main {\n    public static void main(String[] args) {\n        Esempio e = new Esempio();\n        e.stampa();\n    }\n}",
            "Qual è il risultato dell’esecuzione?",
            new String[]
            {
                "Errore di compilazione",
                "Nessun output",
                "Chiama il metodo ma non è buona pratica accedere static tramite istanza",
                "Chiama il costruttore due volte"
            },
            2
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

        // Mostra o nascondi il box codice a seconda della domanda
        if (esercizioCorrente.codice != null && !esercizioCorrente.codice.isEmpty()) {
        codiceArea.setText(esercizioCorrente.codice);
            codiceArea.setVisible(true);
            codiceArea.setManaged(true);
        } else {
            codiceArea.clear();
            codiceArea.setVisible(false);
            codiceArea.setManaged(false);
        }

        consegnaLabel.setText(esercizioCorrente.domanda);
        
        // Mescola le risposte
        List<String> risposteMischiate = new ArrayList<>(List.of(esercizioCorrente.risposte));
        Collections.shuffle(risposteMischiate);
        
        // Imposta le risposte mescolate sui RadioButton
        risposta1.setText(risposteMischiate.get(0));
        risposta2.setText(risposteMischiate.get(1));
        risposta3.setText(risposteMischiate.get(2));
        risposta4.setText(risposteMischiate.get(3));
        
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
        salvaRisultato();
        

        switch (livelloCorrente) 
        {
            case "Principiante" -> livelloCorrente = "Intermedio";
            case "Intermedio" -> livelloCorrente = "Avanzato";
            case "Avanzato" -> {
                feedbackLabel.setText("Hai completato tutti i livelli! Complimenti!");
                feedbackLabel.setStyle("-fx-text-fill: #2ECC71;");
                feedbackLabel.setVisible(true);
                btnConferma.setDisable(true);
                return;
            }
        }

        aggiornaStileLivelli();
        mostratiPerLivello.get(livelloCorrente).clear();
        mostraDomandaCasuale();
    }

    private void aggiornaColoreTacca(boolean rispostaCorretta) 
    {
        String colore = rispostaCorretta ? "#2ECC71" : "#E74C3C";

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

        RadioButton selezionata = (RadioButton) gruppoRisposte.getSelectedToggle();

        if (selezionata == null) 
        {
            feedbackLabel.setText("Seleziona una risposta!");
            feedbackLabel.setStyle("-fx-text-fill: #E74C3C;");
            feedbackLabel.setVisible(true);
            btnConferma.setDisable(false); // Riabilita il pulsante
            return;
        }

        String rispostaSelezionata = selezionata.getText();
        Esercizio domanda = esercizioCorrente;

        if (rispostaSelezionata.equals(domanda.risposte[domanda.indiceCorretta])) 
        {
            feedbackLabel.setText("Corretto!");
            feedbackLabel.setStyle("-fx-text-fill: #2ECC71;");
            feedbackLabel.setVisible(true);

            if (!domanda.isAnswered) 
            {
                correctAnswers++;
                aggiornaColoreTacca(true); // Colora tacca verde
                domanda.isAnswered = true; // Segna la domanda come già risolta
            }

            codiceArea.setStyle("-fx-border-color: #2ECC71; -fx-border-width: 2;");

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
            feedbackLabel.setStyle("-fx-text-fill: #E74C3C;");
            feedbackLabel.setVisible(true);

            if (!domanda.isAnswered) // Colora la tacca solo alla prima risposta
            {
                incorrectAnswers++;
                aggiornaColoreTacca(false); // Colora tacca rossa
                domanda.isAnswered = true; // Segna la domanda come già risolta
            }

            codiceArea.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
            btnConferma.setDisable(false); // Riabilita il pulsante
        }
    }

    private void salvaRisultato() 
    {
        ProgressManager.salvaRisultatoCSV(titolo, livelloCorrente);
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

            // Show pop-up for completion with custom button
            ButtonType btnRisultati = new ButtonType("Visualizza i risultati", ButtonBar.ButtonData.OK_DONE);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Hai completato tutti i livelli!", btnRisultati);
            alert.setTitle("Congratulazioni");
            alert.setHeaderText(null);
            alert.getButtonTypes().setAll(btnRisultati);
            alert.showAndWait();

            // Dopo il click su "Visualizza i risultati", vai a risultati.fxml usando una finestra alternativa se feedbackLabel non è in scena
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
            // Prevent entry into the "Avanzato" level
            livelloCorrente = null;
            feedbackLabel.setText("Hai completato tutti i livelli! Complimenti!");
            feedbackLabel.setStyle("-fx-text-fill: #2ECC71;");
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
                translatedTacche.add("-fx-background-color: #2ECC71;");
            } 
            else if ("R".equals(tacca)) 
            {
                translatedTacche.add("-fx-background-color: #E74C3C;");
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
            feedbackLabel.setStyle("-fx-text-fill: #E74C3C;");
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
            feedbackLabel.setStyle("-fx-text-fill: #E74C3C;");
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