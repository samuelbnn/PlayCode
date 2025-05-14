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

public class LeggiCodiceController 
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

    private static final String titolo = "Leggi il codice";
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
                titolo, 
                Grado.PRINCIPIANTE, 
                "int x = 5;\nint y = 3;\nint z = x + y;", 
                "Qual è il valore di z?", 
                new String[]
                {
                    "8",
                     "53", 
                     "x + y", 
                     "35"
                }, 
                0),

            new Esercizio
            (
                titolo, 
                Grado.PRINCIPIANTE, 
                "for(int i = 0; i < 3; i++) {\n  System.out.println(i);\n}", 
                "Quante volte viene eseguito il ciclo?", 
                new String[]
                {
                    "3", 
                    "2", 
                    "4", 
                    "1"
                },
                0
            ),
            
            new Esercizio
            (
                titolo, 
                Grado.PRINCIPIANTE, 
                "String s = \"Java\";\nSystem.out.println(s.length());", 
                "Cosa viene stampato?", 
                new String[]
                {
                    "4",
                    "Java",
                    "3",
                    "Errore"
                }, 
                0
            ),
            
            new Esercizio
            (
                titolo,
                Grado.PRINCIPIANTE, 
                "boolean b = true;\nif(!b) {\n  System.out.println(\"Vero\");\n} else {\n  System.out.println(\"Falso\");\n}", 
                "Cosa viene stampato?", 
                new String[]
                {
                    "Falso", 
                    "Vero", 
                    "Niente", 
                    "Errore"
                }, 
                0
            ),
            
            new Esercizio
            (
                titolo,
                Grado.PRINCIPIANTE, 
                "int[] arr = {1,2,3};\nSystem.out.println(arr[1]);", 
                "Cosa viene stampato?", 
                new String[]
                {
                    "2", 
                    "1", 
                    "3", 
                    "Errore"
                }, 
                0
            )
        ));

        eserciziPerLivello.put("Intermedio", List.of(
            new Esercizio
            (
                titolo, 
                Grado.INTERMEDIO, 
                "int x = 10;\nint y = x++;\nSystem.out.println(y);", 
                "Cosa viene stampato?", 
                new String[]
                {
                    "10",
                    "11", 
                    "12", 
                    "Errore"
                }, 
                0
            ),

            new Esercizio
            (
                titolo, 
                Grado.INTERMEDIO, 
                "String s1 = \"Ciao\";\nString s2 = new String(\"Ciao\");\nSystem.out.println(s1 == s2);", 
                "Cosa viene stampato?", 
                new String[]
                {
                    "false", 
                    "true", 
                    "Ciao", 
                    "Errore"
                }, 
                0
            ),

            new Esercizio
            (
                titolo, 
                Grado.INTERMEDIO, 
                "List<Integer> list = new ArrayList<>();\nlist.add(1);\nlist.add(2);\nlist.remove(1);\nSystem.out.println(list.size());", 
                "Cosa viene stampato?",
                new String[]
                {
                    "1", 
                    "2", 
                    "0", 
                    "Errore"
                }, 
                0
            ),

            new Esercizio
            (
                titolo, 
                Grado.INTERMEDIO, 
                "int x = 5;\nint y = x > 0 ? 1 : -1;\nSystem.out.println(y);", 
                "Cosa viene stampato?", 
                new String[]
                {
                    "1", 
                    "-1", 
                    "5", 
                    "0"
                }, 
                0
            ),

            new Esercizio
            (
                titolo, 
                Grado.INTERMEDIO, 
                "try {\n  int x = 5 / 0;\n} catch (Exception e) {\n  System.out.println(\"Errore\");\n}", 
                "Cosa viene stampato?", 
                new String[]
                {
                    "Errore",
                    "Niente", 
                    "0", 
                    "Infinity"
                },
                0
            )
        ));

        eserciziPerLivello.put("Avanzato", List.of(
            new Esercizio
            (
                titolo, 
                Grado.AVANZATO, 
                "Map<String, Integer> map = new HashMap<>();\nmap.put(\"A\", 1);\nmap.put(\"B\", 2);\nSystem.out.println(map.get(\"C\"));", 
                "Cosa viene stampato?", 
                new String[]
                {
                    "null", 
                    "0", 
                    "Errore", 
                    "Niente"
                }, 
                0
            ),
            
            new Esercizio
            (
                titolo, 
                Grado.AVANZATO, 
                "Supplier<Integer> supplier = () -> 5;\nSystem.out.println(supplier.get());", 
                "Cosa viene stampato?", 
                new String[]
                {
                    "5", 
                    "supplier.get()", 
                    "Errore", 
                    "Niente"
                }, 
                0
            ),
            
            new Esercizio
            (
                titolo, 
                Grado.AVANZATO, 
                "Stream<Integer> stream = Stream.of(1,2,3);\nlong count = stream.filter(n -> n > 1).count();\nSystem.out.println(count);", 
                "Cosa viene stampato?", 
                new String[]
                {
                    "2", 
                    "3", 
                    "1", 
                    "0"
                }, 
                0
            ),
            
            new Esercizio
            (
                titolo, 
                Grado.AVANZATO, 
                "Optional<String> opt = Optional.ofNullable(null);\nSystem.out.println(opt.orElse(\"Default\"));", 
                "Cosa viene stampato?", 
                new String[]
                {
                    "Default", 
                    "null", 
                    "Niente", 
                    "Errore"
                }, 
                0
            ),
            
            new Esercizio
            (
                titolo, 
                Grado.AVANZATO, 
                "List<String> list = Arrays.asList(\"A\",\"B\",\"C\");\nlist.forEach(System.out::print);", 
                "Cosa viene stampato?", 
                new String[]
                {
                    "ABC", 
                    "A B C", 
                    "List[A,B,C]", 
                    "Errore"
                }, 
                0
            )
        ));

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