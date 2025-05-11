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

public class LinkedListController 
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

    private static final String titolo= "Linked List";
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
            new Esercizio(
                titolo,
                Grado.PRINCIPIANTE,
                "class Node {\n    int data;\n    Node next;\n    Node(int data) {\n        this.data = data;\n        this.next = null;\n    }\n}\n\npublic class Main {\n    public static void main(String[] args) {\n        Node node = new Node(10);\n        System.out.println(node.data);\n    }\n}",
                "Qual è l'output?",
                new String[]{"Errore di compilazione", "null", "10", "0"},
                2
            ),
            
            new Esercizio(
                titolo,
                Grado.PRINCIPIANTE,
                "Node node = new Node(5);",
                "Qual è il valore iniziale del puntatore 'next' in un nuovo nodo?",
                new String[]{"node", "0", "null", "true"},
                2
            ),
            
            new Esercizio(
                titolo,
                Grado.PRINCIPIANTE,
                "Node first = new Node(1);\nNode second = new Node(2);",
                "Come si collega un nodo a un altro?",
                new String[]{
                    "first.data = second;",
                    "first.next = second;",
                    "second = first.next;",
                    "first = next.second;"
                },
                1
            ),
            
            new Esercizio(
                titolo,
                Grado.PRINCIPIANTE,
                "class Node {\n    int data;\n    Node next;\n}",
                "Qual è la funzione del campo 'next' in un nodo di una Linked List?",
                new String[]{
                    "Conservare il valore del nodo",
                    "Puntare al nodo successivo",
                    "Contare il numero di nodi",
                    "Stampare i nodi"
                },
                1
            ),
            
            new Esercizio(
                titolo,
                Grado.PRINCIPIANTE,
                "Node node = new Node(7);\nSystem.out.println(node.next);",
                "Cosa stampa il seguente codice?",
                new String[]{"0", "7", "null", "Errore di compilazione"},
                2
            )
        ));

        eserciziPerLivello.put("Intermedio", List.of(
            new Esercizio(
                titolo,
                Grado.INTERMEDIO,
                "Node a = new Node(1);\nNode b = new Node(2);\na.next = b;\nSystem.out.println(a.next.data);",
                "Cosa stampa il codice?",
                new String[]{"1", "2", "null", "Errore"},
                1
            ),
            
            new Esercizio(
                titolo,
                Grado.INTERMEDIO,
                "void printList(Node head) {\n    while (head != null) {\n        System.out.print(head.data + \" \");\n        head = head.next;\n    }\n}",
                "Cosa fa questo metodo?",
                new String[]{
                    "Crea una nuova lista",
                    "Stampa tutti gli elementi della lista",
                    "Rimuove il primo nodo",
                    "Conta i nodi"
                },
                1
            ),
            
            new Esercizio(
                titolo,
                Grado.INTERMEDIO,
                "Node head = new Node(1);\nhead.next = new Node(2);\nhead.next.next = new Node(3);\nSystem.out.println(head.next.next.data);",
                "Qual è l'output?",
                new String[]{"1", "2", "3", "null"},
                2
            ),
            
            new Esercizio(
                titolo,
                Grado.INTERMEDIO,
                "Node head = new Node(1);\nhead.next.data = 2;",
                "Qual è il problema in questo codice?",
                new String[]{
                    "Il nodo è statico",
                    "head.next è null",
                    "Mancano i getter",
                    "data non è accessibile"
                },
                1
            ),
            
            new Esercizio(
                titolo,
                Grado.INTERMEDIO,
                "int countNodes(Node head) {\n    int count = 0;\n    while (head != null) {\n        count++;\n        head = head.next;\n    }\n    return count;\n}",
                "Cosa fa questo metodo?",
                new String[]{
                    "Ritorna l’ultimo nodo",
                    "Conta il numero di nodi",
                    "Stampa tutti i nodi",
                    "Crea una nuova lista"
                },
                1
            )
        ));

        eserciziPerLivello.put("Avanzato", List.of(
            new Esercizio(
                titolo,
                Grado.AVANZATO,
                "Node reverse(Node head) {\n    Node prev = null;\n    Node curr = head;\n    while (curr != null) {\n        Node next = curr.next;\n        curr.next = prev;\n        prev = curr;\n        curr = next;\n    }\n    return prev;\n}",
                "Cosa fa questo metodo?",
                new String[]{
                    "Conta i nodi",
                    "Elimina i nodi duplicati",
                    "Inverte la lista",
                    "Crea una nuova lista"
                },
                2
            ),
            
            new Esercizio(
                titolo,
                Grado.AVANZATO,
                "Node reverse(Node head) { /* stessa funzione dell'esercizio precedente */ }",
                "Qual è la complessità temporale della funzione 'reverse'?",
                new String[]{"O(log n)", "O(n²)", "O(n)", "O(1)"},
                2
            ),
            
            new Esercizio(
                titolo,
                Grado.AVANZATO,
                "Node head = new Node(1);\nhead.next = new Node(2);\nhead.next.next = new Node(3);\nNode temp = head;\nwhile (temp.next != null) {\n    temp = temp.next;\n}\nSystem.out.println(temp.data);",
                "Cosa stampa questo codice?",
                new String[]{"1", "2", "3", "null"},
                2
            ),
            
            new Esercizio(
                titolo,
                Grado.AVANZATO,
                "Node a = new Node(1);\nNode b = new Node(2);\na.next = b;\nb.next = a;",
                "Qual è il rischio di questo codice?",
                new String[]{
                    "Nessuno",
                    "È più veloce",
                    "Crea un ciclo infinito nella lista",
                    "Elimina il nodo precedente"
                },
                2
            ),
            
            new Esercizio(
                titolo,
                Grado.AVANZATO,
                "boolean hasCycle(Node head) {\n    Node slow = head;\n    Node fast = head;\n    while (fast != null && fast.next != null) {\n        slow = slow.next;\n        fast = fast.next.next;\n        if (slow == fast) return true;\n    }\n    return false;\n}",
                "Come si rileva un ciclo in una Linked List?",
                new String[]{
                    "Con un contatore",
                    "Usando un array",
                    "Con due puntatori (slow e fast)",
                    "Ordinando i nodi"
                },
                2
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