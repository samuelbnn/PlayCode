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

public class LinkedListController 
{
    //region FXML Variabili
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
    //endregion

    //region Variabili
    private ToggleGroup gruppoRisposte;
    private String livelloCorrente = "Principiante";
    private Esercizio esercizioCorrente;
    private final Map<String, List<Esercizio>> eserciziPerLivello = new LinkedHashMap<>();
    private final Map<String, List<Esercizio>> mostratiPerLivello = new HashMap<>();
    private final Map<String, List<String>> statoTacche = new HashMap<>();
    private final Set<String> livelliCompletati = new HashSet<>();
    private static final String titolo= Costanti.ES_LINKED_LIST;
    private enum Grado { PRINCIPIANTE, INTERMEDIO, AVANZATO }
    private String timestampInizioLivello = null;
    //endregion

    //region Inizializzazione
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
        eserciziPerLivello.put("Principiante", List.of(
            new Esercizio(
                titolo,
                Grado.PRINCIPIANTE,
                "class Node {\n    int data;\n    Node next;\n    Node(int data) {\n        this.data = data;\n        this.next = null;\n    }\n}\npublic class Main {\n    public static void main(String[] args) {\n        Node node = new Node(10);\n        System.out.println(node.data);\n    }\n}",
                "Cosa verrà stampato a schermo?",
                new String[]{"10", "null", "Errore di compilazione", "0"},
                0
            ),
            new Esercizio(
                titolo,
                Grado.PRINCIPIANTE,
                "Node node = new Node(5);",
                "Qual è il valore iniziale del campo 'next' in un nuovo nodo?",
                new String[]{"null", "0", "node", "true"},
                0
            ),
            new Esercizio(
                titolo,
                Grado.PRINCIPIANTE,
                "Node first = new Node(1);\nNode second = new Node(2);",
                "Come si collega il nodo 'first' al nodo 'second'?",
                new String[]{"first.next = second;", "first.data = second;", "second = first.next;", "first = next.second;"},
                0
            ),
            new Esercizio(
                titolo,
                Grado.PRINCIPIANTE,
                "class Node {\n    int data;\n    Node next;\n}",
                "Qual è la funzione del campo 'next' in una Linked List?",
                new String[]{"Puntare al nodo successivo", "Conservare il valore del nodo", "Contare il numero di nodi", "Stampare i nodi"},
                0
            ),
            new Esercizio(
                titolo,
                Grado.PRINCIPIANTE,
                "Node node = new Node(7);\nSystem.out.println(node.next);",
                "Cosa verrà stampato a schermo?",
                new String[]{"null", "7", "0", "Errore di compilazione"},
                0
            )
        ));

        eserciziPerLivello.put("Intermedio", List.of(
            new Esercizio(
                titolo,
                Grado.INTERMEDIO,
                "Node a = new Node(1);\nNode b = new Node(2);\na.next = b;\nSystem.out.println(a.next.data);",
                "Cosa verrà stampato a schermo?",
                new String[]{"2", "1", "null", "Errore"},
                0
            ),
            new Esercizio(
                titolo,
                Grado.INTERMEDIO,
                "void printList(Node head) {\n    while (head != null) {\n        System.out.print(head.data + \" \" );\n        head = head.next;\n    }\n}",
                "Cosa fa questo metodo?",
                new String[]{"Stampa tutti gli elementi della lista", "Crea una nuova lista", "Rimuove il primo nodo", "Conta i nodi"},
                0
            ),
            new Esercizio(
                titolo,
                Grado.INTERMEDIO,
                "Node head = new Node(1);\nhead.next = new Node(2);\nhead.next.next = new Node(3);\nSystem.out.println(head.next.next.data);",
                "Cosa verrà stampato a schermo?",
                new String[]{"3", "2", "1", "null"},
                0
            ),
            new Esercizio(
                titolo,
                Grado.INTERMEDIO,
                "Node head = new Node(1);\nhead.next.data = 2;",
                "Qual è il problema in questo codice?",
                new String[]{"head.next è null", "Il nodo è statico", "Mancano i getter", "data non è accessibile"},
                0
            ),
            new Esercizio(
                titolo,
                Grado.INTERMEDIO,
                "int countNodes(Node head) {\n    int count = 0;\n    while (head != null) {\n        count++;\n        head = head.next;\n    }\n    return count;\n}",
                "Cosa restituisce questo metodo?",
                new String[]{"Conta il numero di nodi", "Ritorna l'ultimo nodo", "Stampa tutti i nodi", "Crea una nuova lista"},
                0
            )
        ));

        eserciziPerLivello.put("Avanzato", List.of(
            new Esercizio(
                titolo,
                Grado.AVANZATO,
                "Node reverse(Node head) {\n    Node prev = null;\n    Node curr = head;\n    while (curr != null) {\n        Node next = curr.next;\n        curr.next = prev;\n        prev = curr;\n        curr = next;\n    }\n    return prev;\n}",
                "Cosa fa questo metodo?",
                new String[]{"Inverte la lista", "Conta i nodi", "Elimina i nodi duplicati", "Crea una nuova lista"},
                0
            ),
            new Esercizio(
                titolo,
                Grado.AVANZATO,
                "Node reverse(Node head) { /* stessa funzione dell'esercizio precedente */ }",
                "Qual è la complessità temporale della funzione 'reverse'?",
                new String[]{"O(n)", "O(log n)", "O(n²)", "O(1)"},
                0
            ),
            new Esercizio(
                titolo,
                Grado.AVANZATO,
                "Node head = new Node(1);\nhead.next = new Node(2);\nhead.next.next = new Node(3);\nNode temp = head;\nwhile (temp.next != null) {\n    temp = temp.next;\n}\nSystem.out.println(temp.data);",
                "Cosa verrà stampato a schermo?",
                new String[]{"3", "2", "1", "null"},
                0
            ),
            new Esercizio(
                titolo,
                Grado.AVANZATO,
                "Node a = new Node(1);\nNode b = new Node(2);\na.next = b;\nb.next = a;",
                "Qual è il rischio di questo codice?",
                new String[]{"Crea un ciclo infinito nella lista", "Nessuno", "È più veloce", "Elimina il nodo precedente"},
                0
            ),
            new Esercizio(
                titolo,
                Grado.AVANZATO,
                "Node head = null;\nSystem.out.println(head.data);",
                "Cosa succede eseguendo questo codice?",
                new String[]{"NullPointerException", "0", "Errore di compilazione", "Stampa null"},
                0
            )
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
        
        // Mescola le risposte
        List<String> risposteMischiate = new ArrayList<>(List.of(esercizioCorrente.risposte));
        Collections.shuffle(risposteMischiate);
        
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
        feedbackLabel.setStyle("-fx-text-fill: " + Costanti.BLU + ";");
        feedbackLabel.setVisible(true);

        salvaRisultato();

        switch (livelloCorrente) 
        {
            case "Principiante" -> {
                livelloCorrente = "Intermedio";
                setTimestampInizioLivello();    //Reset timestamp per nuovo livello
            }
            case "Intermedio" -> {
                livelloCorrente = "Avanzato";
                setTimestampInizioLivello();    //Reset timestamp per nuovo livello
            }
            case "Avanzato" -> {
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
                if (progressManager.isProgressoValido(parts, utente, titolo)) 
                {
                    statoTacche.put("Principiante", progressManager.normalizeTacche(parts[6], 5));
                    statoTacche.put("Intermedio", progressManager.normalizeTacche(parts[7], 5));
                    statoTacche.put("Avanzato", progressManager.normalizeTacche(parts[8], 5));
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

        RadioButton selezionata = (RadioButton) gruppoRisposte.getSelectedToggle();

        if (selezionata == null) 
        {
            feedbackLabel.setText("Seleziona una risposta!");
            feedbackLabel.setStyle("-fx-text-fill: " + Costanti.ROSSO + ";");
            feedbackLabel.setVisible(true);
            btnConferma.setDisable(false);
            return;
        }

        String rispostaSelezionata = selezionata.getText();
        Esercizio domanda = esercizioCorrente;

        if (rispostaSelezionata.equals(domanda.risposte[domanda.indiceCorretta])) 
        {
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
        statoTacche.put("Principiante", ProgressManager.translateTacche(loadedProgress.getOrDefault("Principiante", new ArrayList<>()), 5));
        statoTacche.put("Intermedio", ProgressManager.translateTacche(loadedProgress.getOrDefault("Intermedio", new ArrayList<>()), 5));
        statoTacche.put("Avanzato", ProgressManager.translateTacche(loadedProgress.getOrDefault("Avanzato", new ArrayList<>()), 5));

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
        if (livelliCompletati.contains("Principiante")) 
        {
            feedbackLabel.setText("Hai già completato il livello Principiante!");
            feedbackLabel.setStyle("-fx-text-fill: " + Costanti.BLU + ";");
            feedbackLabel.setVisible(true);
            return;
        }

        livelloCorrente = "Principiante";
        aggiornaStileLivelli();
        setTimestampInizioLivello(); //Reset timestamp quando si entra nel livello
    }

    @FXML
    private void vaiALivelloIntermedio(ActionEvent event) 
    {
        if (!livelliCompletati.contains("Principiante")) 
        {
            feedbackLabel.setText("Completa il livello Principiante prima di accedere a Intermedio!");
            feedbackLabel.setStyle("-fx-text-fill: " + Costanti.ROSSO + ";");
            feedbackLabel.setVisible(true);
            return;
        }

        if (livelliCompletati.contains("Intermedio")) 
        {
            feedbackLabel.setText("Hai già completato il livello Intermedio!");
            feedbackLabel.setStyle("-fx-text-fill: " + Costanti.BLU + ";");
            feedbackLabel.setVisible(true);
            return;
        }

        livelloCorrente = "Intermedio";
        aggiornaStileLivelli();
        setTimestampInizioLivello(); //Reset timestamp quando si entra nel livello
    }

    @FXML
    private void vaiALivelloAvanzato(ActionEvent event) 
    {
        if (!livelliCompletati.contains("Intermedio")) 
        {
            feedbackLabel.setText("Completa il livello Intermedio prima di accedere a Avanzato!");
            feedbackLabel.setStyle("-fx-text-fill: " + Costanti.ROSSO + ";");
            feedbackLabel.setVisible(true);
            return;
        }

        if (livelliCompletati.contains("Avanzato")) 
        {
            feedbackLabel.setText("Hai già completato il livello Avanzato!");
            feedbackLabel.setStyle("-fx-text-fill: " + Costanti.BLU + ";");
            feedbackLabel.setVisible(true);
            return;
        }

        livelloCorrente = "Avanzato";
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
            case "Principiante" -> btnPrincipiante.getStyleClass().add("selected");
            case "Intermedio" -> btnIntermedio.getStyleClass().add("selected");
            case "Avanzato" -> btnAvanzato.getStyleClass().add("selected");
        }
    }
    //endregion
}