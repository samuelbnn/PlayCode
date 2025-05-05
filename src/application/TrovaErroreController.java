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

public class TrovaErroreController 
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

    private final String titolo= "Trova l'errore";
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
            new Esercizio(titolo, Grado.PRINCIPIANTE, "System.out.println(\"Hello\")", "Cosa manca?", new String[]{"Punto e virgola", "Parentesi graffa", "Dichiarazione variabile", "Parentesi quadra"}, 0),
            new Esercizio(titolo, Grado.PRINCIPIANTE, "if (x > 5)\n    System.out.println(\"Grande\")\nelse\n    System.out.println(\"Piccolo\");", "Individua l'errore sintattico", new String[]{"Manca una graffa", "Errore di tipo", "Variabile non inizializzata", "Manca il punto e virgola"}, 0),
            new Esercizio(titolo, Grado.PRINCIPIANTE, "System.ou.println(\"Errore\");", "Cosa c'è che non va?", new String[]{"Errore di battitura: 'ou'", "System non definito", "Manca il punto e virgola", "Errore di runtime"}, 0),
            new Esercizio(titolo, Grado.PRINCIPIANTE, "public static void main {\n    System.out.println(\"Ciao\");\n}", "Qual è l'errore?", new String[]{"Mancano le parentesi tonde", "Manca il return", "main non è static", "Errore di sintassi"}, 0),
            new Esercizio(titolo, Grado.PRINCIPIANTE, "int numero = \"dieci\";", "Dove sta l'errore?", new String[]{"Tipo incompatibile", "String non definita", "Uso scorretto di = ", "Errore di casting"}, 0)
        ));

        eserciziPerLivello.put("Intermedio", List.of(
            new Esercizio(titolo, Grado.INTERMEDIO, "if(x = 10) {\n  System.out.println(\"x\");\n}", "Errore logico?", new String[]{"Uso di '=' invece di '=='", "x è già definito", "System non importato", "Errore di runtime"}, 0),
            new Esercizio(titolo, Grado.INTERMEDIO, "boolean valido = true;\nif(valido == false);\n  System.out.println(\"Non valido\");", "Cosa c'è che non va?", new String[]{"If con ; inutile", "La variabile non esiste", "Manca else", "Errore di logica"}, 0),
            new Esercizio(titolo, Grado.INTERMEDIO, "for(int i = 0; i > 10; i++) {\n  System.out.println(i);\n}", "Ciclo non entra mai, perché?", new String[]{"Condizione errata", "Inizializzazione sbagliata", "i non definito", "Errore di sintassi"}, 0),
            new Esercizio(titolo, Grado.INTERMEDIO, "int[] nums = new int[3];\nnums[3] = 5;", "Che problema c'è?", new String[]{"IndexOutOfBounds", "Errore di sintassi", "Array nullo", "Errore di runtime"}, 0),
            new Esercizio(titolo, Grado.INTERMEDIO, "String s = null;\nSystem.out.println(s.length());", "Cosa succede?", new String[]{"NullPointerException", "String non importata", "Metodo sbagliato", "Errore di runtime"}, 0)
        ));

        eserciziPerLivello.put("Avanzato", List.of(
            new Esercizio(titolo, Grado.AVANZATO, "int[] arr = {1,2,3};\nfor(int i = 0; i <= arr.length; i++) {\n  System.out.println(arr[i]);\n}", "Cosa succede?", new String[]{"IndexOutOfBounds", "Errore di compilazione", "StackOverflow", "Errore di runtime"}, 0),
            new Esercizio(titolo, Grado.AVANZATO, "while(true) {\n  int x = 5;\n  x++;\n}", "Qual è il problema?", new String[]{"Loop infinito", "x non definito", "Manca il break", "Errore di logica"}, 0),
            new Esercizio(titolo, Grado.AVANZATO, "for(int i = 0; i < 5; i++)\n  break\n  System.out.println(i);", "Errore combinato?", new String[]{"Manca punto e virgola dopo break", "Ciclo sbagliato", "Variabile duplicata", "Errore di sintassi"}, 0),
            new Esercizio(titolo, Grado.AVANZATO, "int x;\nif(x > 0) {\n  System.out.println(\"Positivo\");\n}", "Cosa accade?", new String[]{"Variabile non inizializzata", "Errore di logica", "Loop non chiuso", "Errore di runtime"}, 0),
            new Esercizio(titolo, Grado.AVANZATO, "String[] parole = {\"ciao\", null, \"mondo\"};\nfor(String p : parole) {\n  System.out.println(p.toUpperCase());\n}", "Cosa può succedere?", new String[]{"NullPointerException", "IndexError", "ArrayIndexOutOfBounds", "Errore di runtime"}, 0)
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

        // Save progress after completing the level
        salvaProgresso();

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
        RadioButton selezionata = (RadioButton) gruppoRisposte.getSelectedToggle();

        if (selezionata == null) 
        {
            feedbackLabel.setText("Seleziona una risposta!");
            feedbackLabel.setStyle("-fx-text-fill: red;");
            feedbackLabel.setVisible(true);
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
        }
    }

    private void salvaRisultato() 
    {
        try (PrintWriter writer = new PrintWriter(new FileWriter(Costanti.PATH_FILE_RISULTATI, true))) 
        {
            String utente = Session.getCurrentUser();
            String data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            writer.printf("%s,%s,%d,%s\n", utente, "Trova l'errore", punteggio, data);
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }

    private void salvaProgresso() 
    {
        String utente = Session.getCurrentUser();
        StringBuilder progressData = new StringBuilder(utente);

        // Append progress for Trova l'errore without quotes around the exercise title
        progressData.append(", {Trova l'errore");
        progressData.append(" [Principiante (").append(String.join(";", convertToRG(statoTacche.getOrDefault("Principiante", new ArrayList<>())))).append(")]");
        progressData.append(" [Intermedio (").append(String.join(";", convertToRG(statoTacche.getOrDefault("Intermedio", new ArrayList<>())))).append(")]");
        progressData.append(" [Avanzato (").append(String.join(";", convertToRG(statoTacche.getOrDefault("Avanzato", new ArrayList<>())))).append(")]");
        progressData.append("}");

        List<String> updatedLines = new ArrayList<>();
        boolean userFound = false;

        // Read existing progress and update the current user's data
        try (BufferedReader reader = new BufferedReader(new FileReader(Costanti.PATH_FILE_PROGRESSI))) 
        {
            String line;
            while ((line = reader.readLine()) != null) 
            {
                if (line.startsWith(utente + ",")) 
                {
                    updatedLines.add(progressData.toString());
                    userFound = true;
                } 
                else 
                {
                    updatedLines.add(line);
                }
            }
        } 
        catch (IOException e) 
        {
            System.out.println("File non trovato, verrà creato un nuovo file.");
        }

        // If the user was not found, add their progress as a new entry
        if (!userFound) 
        {
            updatedLines.add(progressData.toString());
        }

        // Write back all progress data
        try (PrintWriter writer = new PrintWriter(new FileWriter(Costanti.PATH_FILE_PROGRESSI, false))) 
        {
            for (String line : updatedLines) 
            {
                writer.println(line);
            }
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }

    private List<String> convertToRG(List<String> tacche) 
    {
        List<String> result = new ArrayList<>();
        for (String tacca : tacche) 
        {
            if (tacca.contains("green")) 
            {
                result.add("G");
            } 
            else if (tacca.contains("red")) 
            {
                result.add("R");
            } 
            else 
            {
                result.add("");
            }
        }
        return result;
    }

    private void caricaProgresso() 
    {
        String utente = Session.getCurrentUser();
        Map<String, List<String>> loadedProgress = ProgressManager.loadProgress(utente, "Trova l'errore");

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
        return parts.length >= 9 && parts[0].equals(utente) && parts[1].equals("Trova l'errore");
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
        salvaProgresso();
        FXMLLoader loader = new FXMLLoader(App.class.getResource(Costanti.PATH_FXML_MENU));
        Parent root = loader.load();
        MenuController menuController = loader.getController();
        menuController.updateProgress("TrovaErrore", correctAnswers, incorrectAnswers);

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