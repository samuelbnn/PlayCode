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
    @FXML private HBox tacchePrincipiante;
    @FXML private HBox taccheIntermedio;
    @FXML private HBox taccheAvanzato;
    @FXML private Button btnPrincipiante;
    @FXML private Button btnIntermedio;
    @FXML private Button btnAvanzato;

    private ToggleGroup gruppoRisposte;
    private String livelloCorrente = "Principiante";
    private int punteggio = 0;
    private int successiConsecutivi = 0;
    private final int nSuccessiPerLivello = 3;
    private Esercizio esercizioCorrente;
    private final Map<String, List<Esercizio>> eserciziPerLivello = new LinkedHashMap<>();
    private final Map<String, List<Esercizio>> mostratiPerLivello = new HashMap<>();
    private int correctAnswers = 0;
    private int incorrectAnswers = 0;
    private final Map<String, List<String>> statoTacche = new HashMap<>(); // Mappa per memorizzare lo stato delle tacche
    private final Set<String> livelliCompletati = new HashSet<>(); // Traccia i livelli completati

    @FXML
    public void initialize() 
    {
        gruppoRisposte = new ToggleGroup();
        risposta1.setToggleGroup(gruppoRisposte);
        risposta2.setToggleGroup(gruppoRisposte);
        risposta3.setToggleGroup(gruppoRisposte);

        caricaDomande();
        caricaProgresso(); 
        inizializzaStatoTacche(); // Inizializza lo stato delle tacche
        mostraDomandaCasuale();
        aggiornaStileLivelli();
        aggiornaTacche(); // Aggiorna la visualizzazione delle tacche per tutti i livelli
    }

    private void inizializzaStatoTacche() 
    {
        statoTacche.put("Principiante", new ArrayList<>(Collections.nCopies(6, "")));
        statoTacche.put("Intermedio", new ArrayList<>(Collections.nCopies(6, "")));
        statoTacche.put("Avanzato", new ArrayList<>(Collections.nCopies(6, "")));
    }

    private void caricaDomande() 
    {
        eserciziPerLivello.put("Principiante", List.of(
            new Esercizio("Trova l'errore", "Principiante", "System.out.println(\"Hello\")", "Cosa manca?", new String[]{"Punto e virgola", "Parentesi graffa", "Dichiarazione variabile"}, 0),
            new Esercizio("Trova l'errore", "Principiante", "if (x > 5)\n    System.out.println(\"Grande\")\nelse\n    System.out.println(\"Piccolo\");", "Individua l'errore sintattico", new String[]{"Manca una graffa", "Errore di tipo", "Variabile non inizializzata"}, 0),
            new Esercizio("Trova l'errore", "Principiante", "System.ou.println(\"Errore\");", "Cosa c'è che non va?", new String[]{"Errore di battitura: 'ou'", "System non definito", "Manca il punto e virgola"}, 0),
            new Esercizio("Trova l'errore", "Principiante", "public static void main {\n    System.out.println(\"Ciao\");\n}", "Qual è l'errore?", new String[]{"Mancano le parentesi tonde", "Manca il return", "main non è static"}, 0),
            new Esercizio("Trova l'errore", "Principiante", "int numero = \"dieci\";", "Dove sta l'errore?", new String[]{"Tipo incompatibile", "String non definita", "Uso scorretto di = "}, 0),
            new Esercizio("Trova l'errore", "Principiante", "String nome;\nSystem.out.println(nome);", "Cosa succede?", new String[]{"Variabile non inizializzata", "String non importato", "System.err non usato"}, 0)
        ));

        eserciziPerLivello.put("Intermedio", new ArrayList<>(List.of(
            new Esercizio("Trova l'errore", "Intermedio", "if(x = 10) {\n  System.out.println(\"x\");\n}", "Errore logico?", new String[]{"Uso di '=' invece di '=='", "x è già definito", "System non importato"}, 0),
            new Esercizio("Trova l'errore", "Intermedio", "boolean valido = true;\nif(valido == false);\n  System.out.println(\"Non valido\");", "Cosa c'è che non va?", new String[]{"If con ; inutile", "La variabile non esiste", "Manca else"}, 0),
            new Esercizio("Trova l'errore", "Intermedio", "for(int i = 0; i > 10; i++) {\n  System.out.println(i);\n}", "Ciclo non entra mai, perché?", new String[]{"Condizione errata", "Inizializzazione sbagliata", "i non definito"}, 0),
            new Esercizio("Trova l'errore", "Intermedio", "int[] nums = new int[3];\nnums[3] = 5;", "Che problema c'è?", new String[]{"IndexOutOfBounds", "Errore di sintassi", "Array nullo"}, 0),
            new Esercizio("Trova l'errore", "Intermedio", "String s = null;\nSystem.out.println(s.length());", "Cosa succede?", new String[]{"NullPointerException", "String non importata", "Metodo sbagliato"}, 0)        
        )));

        eserciziPerLivello.put("Avanzato", new ArrayList<>(List.of(
            new Esercizio("Trova l'errore", "Avanzato", "int[] arr = {1,2,3};\nfor(int i = 0; i <= arr.length; i++) {\n  System.out.println(arr[i]);\n}", "Cosa succede?", new String[]{"IndexOutOfBounds", "Errore di compilazione", "StackOverflow"}, 0),
            new Esercizio("Trova l'errore", "Avanzato", "while(true) {\n  int x = 5;\n  x++;\n}", "Qual è il problema?", new String[]{"Loop infinito", "x non definito", "Manca il break"}, 0),
            new Esercizio("Trova l'errore", "Avanzato", "for(int i = 0; i < 5; i++)\n  break\n  System.out.println(i);", "Errore combinato?", new String[]{"Manca punto e virgola dopo break", "Ciclo sbagliato", "Variabile duplicata"}, 0),
            new Esercizio("Trova l'errore", "Avanzato", "int x;\nif(x > 0) {\n  System.out.println(\"Positivo\");\n}", "Cosa accade?", new String[]{"Variabile non inizializzata", "Errore di logica", "Loop non chiuso"}, 0),
            new Esercizio("Trova l'errore", "Avanzato", "String[] parole = {\"ciao\", null, \"mondo\"};\nfor(String p : parole) {\n  System.out.println(p.toUpperCase());\n}", "Cosa può succedere?", new String[]{"NullPointerException", "IndexError", "ArrayIndexOutOfBounds"}, 0)
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
        
        gruppoRisposte.selectToggle(null);
        feedbackLabel.setVisible(false);
    }

    private void completaLivello() 
    {
        livelliCompletati.add(livelloCorrente);
        feedbackLabel.setText("Hai completato il livello " + livelloCorrente + "!");
        feedbackLabel.setStyle("-fx-text-fill: blue;");
        feedbackLabel.setVisible(true);

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

    private void resettaTacche() 
    {
        tacchePrincipiante.getChildren().forEach(tacca -> tacca.setStyle(""));
        taccheIntermedio.getChildren().forEach(tacca -> tacca.setStyle(""));
        taccheAvanzato.getChildren().forEach(tacca -> tacca.setStyle(""));
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

            if (!domanda.isAnswered) // Colora la tacca solo alla prima risposta
            {
                correctAnswers++;
                successiConsecutivi++;
                aggiornaColoreTacca(true); // Colora tacca verde
                domanda.isAnswered = true; // Segna la domanda come già risolta
            }

            codiceArea.setStyle("-fx-border-color: green; -fx-border-width: 2;");

            new Thread(() -> {
                try {
                    Thread.sleep(2000);
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

    private void avanzaLivello() 
    {
        successiConsecutivi = 0;
        resettaTacche(); // Modificato per resettare le tacche

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
        File file = new File(Costanti.PATH_FILE_PROGRESSI);
        List<String> righeAggiornate = new ArrayList<>();
    
        // Leggi tutte le righe esistenti e tieni solo quelle NON dell'utente corrente per questo esercizio
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) 
        {
            String riga;
            while ((riga = reader.readLine()) != null) 
            {
                if (!riga.startsWith(utente + ",Trova l'errore")) 
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
        righeAggiornate.add(String.format("%s,%s,%s,%d", utente, "Trova l'errore", livelloCorrente, successiConsecutivi));
    
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
                if (parts.length == 4 && parts[0].equals(utente) && parts[1].equals("Trova l'errore")) 
                {
                    livelloCorrente = parts[2];
                    successiConsecutivi = Integer.parseInt(parts[3]);
                    resettaTacche(); // Modificato per resettare le tacche
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
        mostraDomandaCasuale();
    }

    @FXML
    private void vaiALivelloIntermedio(ActionEvent event) 
    {
        if (livelliCompletati.contains("Intermedio")) 
        {
            feedbackLabel.setText("Hai già completato il livello Intermedio!");
            feedbackLabel.setStyle("-fx-text-fill: blue;");
            feedbackLabel.setVisible(true);
            return;
        }

        livelloCorrente = "Intermedio";
        aggiornaStileLivelli();
        mostraDomandaCasuale();
    }

    @FXML
    private void vaiALivelloAvanzato(ActionEvent event) 
    {
        if (livelliCompletati.contains("Avanzato")) 
        {
            feedbackLabel.setText("Hai già completato il livello Avanzato!");
            feedbackLabel.setStyle("-fx-text-fill: blue;");
            feedbackLabel.setVisible(true);
            return;
        }

        livelloCorrente = "Avanzato";
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
}