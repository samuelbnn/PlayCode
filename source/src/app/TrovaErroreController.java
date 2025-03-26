
package app;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.io.IOException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TrovaErroreController {

    @FXML private Label titoloLabel;
    @FXML private Label livelloLabel;
    @FXML private TextArea codiceArea;
    @FXML private Label consegnaLabel;
    @FXML private RadioButton risposta1;
    @FXML private RadioButton risposta2;
    @FXML private RadioButton risposta3;
    @FXML private Button confermaButton;
    @FXML private Button tornaMenuButton;
    @FXML private Label feedbackLabel;
    @FXML private ChoiceBox<String> livelloChoiceBox;

    private ToggleGroup rispostaGroup = new ToggleGroup();
    private int punteggio = 0;

    private final Map<String, List<Esercizio>> eserciziPerLivello = new LinkedHashMap<>();
    private final Map<String, List<Esercizio>> mostratiPerLivello = new HashMap<>();
    private String livelloCorrente = "Principiante";
    private Esercizio esercizioCorrente;

    public void initialize() {
        risposta1.setToggleGroup(rispostaGroup);
        risposta2.setToggleGroup(rispostaGroup);
        risposta3.setToggleGroup(rispostaGroup);
        feedbackLabel.setVisible(false);
        tornaMenuButton.setVisible(false);
        livelloChoiceBox.setVisible(false);
        livelloChoiceBox.setManaged(false);

        caricaDomande();
        mostraDomandaCasuale();
    }

    private void caricaDomande() {
        eserciziPerLivello.put("Principiante", new ArrayList<>(List.of(
            new Esercizio("Trova l'errore", "Principiante", "System.out.println(\"Hello\")", "Cosa manca?", new String[]{"Punto e virgola", "Parentesi graffa", "Dichiarazione variabile"}, 0),
            new Esercizio("Trova l'errore", "Principiante", "if (x > 5)\n    System.out.println(\"Grande\")\nelse\n    System.out.println(\"Piccolo\");", "Individua l'errore sintattico", new String[]{"Manca una graffa", "Errore di tipo", "Variabile non inizializzata"}, 0),
            new Esercizio("Trova l'errore", "Principiante", "System.ou.println(\"Errore\");", "Cosa c'è che non va?", new String[]{"Errore di battitura: 'ou'", "System non definito", "Manca il punto e virgola"}, 0),
            new Esercizio("Trova l'errore", "Principiante", "public static void main {\n    System.out.println(\"Ciao\");\n}", "Qual è l'errore?", new String[]{"Mancano le parentesi tonde", "Manca il return", "main non è static"}, 0),
            new Esercizio("Trova l'errore", "Principiante", "int numero = \"dieci\";", "Dove sta l'errore?", new String[]{"Tipo incompatibile", "String non definita", "Uso scorretto di = "}, 0),
            new Esercizio("Trova l'errore", "Principiante", "String nome;\nSystem.out.println(nome);", "Cosa succede?", new String[]{"Variabile non inizializzata", "String non importato", "System.err non usato"}, 0)
        )));

        eserciziPerLivello.put("Intermedio", new ArrayList<>(List.of(
            new Esercizio("Trova l'errore", "Intermedio", "if(x = 10) {\n  System.out.println(\"x\");\n}", "Errore logico?", new String[]{"Uso di '=' invece di '=='", "x è già definito", "System non importato"}, 0),
            new Esercizio("Trova l'errore", "Intermedio", "boolean valido = true;\nif(valido == false);\n  System.out.println(\"Non valido\");", "Cosa c'è che non va?", new String[]{"If con ; inutile", "La variabile non esiste", "Manca else"}, 0),
            new Esercizio("Trova l'errore", "Intermedio", "for(int i = 0; i > 10; i++) {\n  System.out.println(i);\n}", "Ciclo non entra mai, perché?", new String[]{"Condizione errata", "Inizializzazione sbagliata", "i non definito"}, 0),
            new Esercizio("Trova l'errore", "Intermedio", "int[] nums = new int[3];\nnums[3] = 5;", "Che problema c'è?", new String[]{"IndexOutOfBounds", "Errore di sintassi", "Array nullo"}, 0),
            new Esercizio("Trova l'errore", "Intermedio", "String s = null;\nSystem.out.println(s.length());", "Cosa succede?", new String[]{"NullPointerException", "String non importata", "Metodo sbagliato"}, 0)
        )));
    }

    private void mostraDomandaCasuale() {
        List<Esercizio> tutti = eserciziPerLivello.get(livelloCorrente);
        List<Esercizio> mostrati = mostratiPerLivello.getOrDefault(livelloCorrente, new ArrayList<>());

        List<Esercizio> disponibili = new ArrayList<>(tutti);
        disponibili.removeAll(mostrati);

        if (disponibili.isEmpty()) {
            avanzaLivello();
            return;
        }

        Collections.shuffle(disponibili);
        esercizioCorrente = disponibili.get(0);
        mostrati.add(esercizioCorrente);
        mostratiPerLivello.put(livelloCorrente, mostrati);

        aggiornaUI(esercizioCorrente);
    }

    private void aggiornaUI(Esercizio es) {
        titoloLabel.setText(es.titolo);
        livelloLabel.setText("Livello: " + es.livello);
        codiceArea.setText(es.codice);
        consegnaLabel.setText(es.domanda);
        risposta1.setText(es.risposte[0]);
        risposta2.setText(es.risposte[1]);
        risposta3.setText(es.risposte[2]);
        rispostaGroup.selectToggle(null);
        feedbackLabel.setVisible(false);
    }

    @FXML
    private void confermaRisposta(ActionEvent event) {
        RadioButton selezionata = (RadioButton) rispostaGroup.getSelectedToggle();
        if (selezionata == null) return;

        int scelta = selezionata == risposta1 ? 0 : selezionata == risposta2 ? 1 : 2;

        if (scelta == esercizioCorrente.indiceCorretta) {
            feedbackLabel.setText("Corretto!");
            feedbackLabel.setStyle("-fx-text-fill: green;");
            punteggio++;
        } else {
            feedbackLabel.setText("Sbagliato!");
            feedbackLabel.setStyle("-fx-text-fill: red;");
        }

        feedbackLabel.setVisible(true);
        confermaButton.setText("Avanti");
        confermaButton.setOnAction(e -> {
            confermaButton.setText("Conferma");
            confermaButton.setOnAction(this::confermaRisposta);
            mostraDomandaCasuale();
        });
    }

    private void avanzaLivello() {
        if (livelloCorrente.equals("Principiante")) livelloCorrente = "Intermedio";
        else {
            feedbackLabel.setText("Hai completato tutti i livelli! Punteggio: " + punteggio);
            feedbackLabel.setStyle("-fx-text-fill: blue;");
            feedbackLabel.setVisible(true);
            confermaButton.setDisable(true);
            tornaMenuButton.setVisible(true);
            salvaRisultato();
            return;
        }

        mostraDomandaCasuale();
    }

    private void salvaRisultato() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("risultati.csv", true))) {
            String utente = Session.getCurrentUser();
            String data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            writer.printf("%s,%s,%d,%s\n", utente, "Trova l'errore", punteggio, data);
        } catch (IOException e) {
            System.err.println("Errore nel salvataggio: " + e.getMessage());
        }
    }

    @FXML
    private void tornaAlMenu(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("menu.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    static class Esercizio {
        String titolo;
        String livello;
        String codice;
        String domanda;
        String[] risposte;
        int indiceCorretta;

        public Esercizio(String titolo, String livello, String codice, String domanda, String[] risposte, int indiceCorretta) {
            this.titolo = titolo;
            this.livello = livello;
            this.codice = codice;
            this.domanda = domanda;
            this.risposte = risposte;
            this.indiceCorretta = indiceCorretta;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Esercizio esercizio = (Esercizio) o;
            return Objects.equals(codice, esercizio.codice);
        }

        @Override
        public int hashCode() {
            return Objects.hash(codice);
        }
    }
}
