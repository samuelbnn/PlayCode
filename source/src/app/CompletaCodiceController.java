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

public class CompletaCodiceController {

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

    private ToggleGroup rispostaGroup = new ToggleGroup();
    private int punteggio = 0;
    private String livelloCorrente = "Principiante";
    private final Map<String, List<Esercizio>> eserciziPerLivello = new LinkedHashMap<>();
    private final Map<String, List<Esercizio>> mostratiPerLivello = new HashMap<>();
    private Esercizio esercizioCorrente;

    public void initialize() {
        risposta1.setToggleGroup(rispostaGroup);
        risposta2.setToggleGroup(rispostaGroup);
        risposta3.setToggleGroup(rispostaGroup);
        feedbackLabel.setVisible(false);
        tornaMenuButton.setVisible(false);
        caricaEsercizi();
        mostraDomandaCasuale();
    }

    private void caricaEsercizi() {
        eserciziPerLivello.put("Principiante", new ArrayList<>(List.of(
            new Esercizio("Completa il codice", "Principiante", "public int somma(int a, int b) {\n    // manca il return\n}",
                "Completa la funzione per restituire la somma di a e b",
                new String[]{"return a + b;", "System.out.println(a + b);", "a + b;"}, 0),
            new Esercizio("Completa il codice", "Principiante", "for(int i = 0; i < 5; i++) {\n    // manca la stampa\n}",
                "Completa il ciclo per stampare i",
                new String[]{"System.out.println(i);", "return i;", "i + 1;"}, 0),
            new Esercizio("Completa il codice", "Principiante", "System.out.println(_____);",
                "Completa la stampa del messaggio 'Ciao mondo'",
                new String[]{"\"Ciao mondo\"", "System.out", "print(\"Ciao mondo\")"}, 0),
            new Esercizio("Completa il codice", "Principiante", "if (x > 0) {\n    _____\n}",
                "Stampa 'positivo' se x è maggiore di 0",
                new String[]{"System.out.println(\"positivo\");", "return x;", "continue;"}, 0),
            new Esercizio("Completa il codice", "Principiante", "int numero;\n_____\n",
                "Assegna 10 alla variabile numero",
                new String[]{"numero = 10;", "numero == 10;", "10 -> numero;"}, 0)
        )));

        eserciziPerLivello.put("Intermedio", new ArrayList<>(List.of(
            new Esercizio("Completa il codice", "Intermedio", "if(nome.equals(\"Mario\")) {\n    // manca azione\n}",
                "Aggiungi il messaggio di benvenuto",
                new String[]{"System.out.println(\"Benvenuto Mario\");", "break;", "continue;"}, 0),
            new Esercizio("Completa il codice", "Intermedio", "int somma = 0;\nfor (int i = 0; i < 5; i++) {\n    _____\n}",
                "Aggiungi i alla somma",
                new String[]{"somma += i;", "i += somma;", "return i;"}, 0),
            new Esercizio("Completa il codice", "Intermedio", "String parola = \"ciao\";\nif (_____) {\n    System.out.println(\"ok\");\n}",
                "Controlla che parola sia uguale a 'ciao'",
                new String[]{"parola.equals(\"ciao\")", "parola == \"ciao\"", "parola = \"ciao\""}, 0)
        )));

        eserciziPerLivello.put("Avanzato", new ArrayList<>(List.of(
            new Esercizio("Completa il codice", "Avanzato", "int[] numeri = {1, 2, 3};\nfor(int i = 0; i < numeri.length; i++) {\n    // manca il controllo\n}",
                "Mostra solo i numeri maggiori di 1",
                new String[]{"if(numeri[i] > 1) System.out.println(numeri[i]);", "numeri[i]++;", "continue;"}, 0),
            new Esercizio("Completa il codice", "Avanzato", "public int fattoriale(int n) {\n    if (n == 0) return 1;\n    else _____\n}",
                "Completa la ricorsione per il fattoriale",
                new String[]{"return n * fattoriale(n - 1);", "return n + fattoriale(n);", "n--;"}, 0),
            new Esercizio("Completa il codice", "Avanzato", "int[] numeri = {1,2,3,4};\nfor (int n : numeri) {\n    if (n % 2 == 0) {\n        _____\n    }\n}",
                "Stampa solo i numeri pari",
                new String[]{"System.out.println(n);", "return n;", "continue;"}, 0)
        )));

        eserciziPerLivello.forEach((livello, lista) -> mostratiPerLivello.put(livello, new ArrayList<>()));
    }

    private void mostraDomandaCasuale() {
        List<Esercizio> disponibili = new ArrayList<>(eserciziPerLivello.get(livelloCorrente));
        disponibili.removeAll(mostratiPerLivello.get(livelloCorrente));

        if (disponibili.isEmpty()) {
            avanzaLivello();
            return;
        }

        Collections.shuffle(disponibili);
        esercizioCorrente = disponibili.get(0);
        mostratiPerLivello.get(livelloCorrente).add(esercizioCorrente);

        titoloLabel.setText(esercizioCorrente.titolo);
        livelloLabel.setText("Livello: " + esercizioCorrente.livello);
        codiceArea.setText(esercizioCorrente.codice);
        consegnaLabel.setText(esercizioCorrente.domanda);
        risposta1.setText(esercizioCorrente.risposte[0]);
        risposta2.setText(esercizioCorrente.risposte[1]);
        risposta3.setText(esercizioCorrente.risposte[2]);
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
        else if (livelloCorrente.equals("Intermedio")) livelloCorrente = "Avanzato";
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
            writer.printf("%s,%s,%d,%s\n", utente, "Completa il Codice", punteggio, data);
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
