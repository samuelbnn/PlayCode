package application;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class ScriviCodiceController {

    @FXML private Label titoloLabel, livelloLabel, consegnaLabel, feedbackLabel;
    @FXML private TextArea codiceTextArea;
    @FXML private HBox tacchePrincipiante, taccheIntermedio, taccheAvanzato;
    @FXML private Button btnConferma;

    private String livelloCorrente = "Principiante";
    private int indiceEsercizio = 0;

    private final String[][] consegne = {
        {
            "Dichiara una variabile intera 'x' inizializzata a 10."
        },
        {
            "Scrivi una funzione che restituisce il doppio di un numero."
        },
        {
            "Scrivi una classe 'Auto' con attributo 'modello' e un costruttore."
        }
    };

    private final String[][] soluzioniAttese = {
        {
            "int x = 10;"
        },
        {
            "int doppio(int n) {\n    return n * 2;\n}"
        },
        {
            "class Auto {\n    String modello;\n\n    Auto(String modello) {\n        this.modello = modello;\n    }\n}"
        }
    };

    @FXML
    public void initialize() {
        mostraEsercizio();
    }

    private void mostraEsercizio() {
        titoloLabel.setText("Scrivi il codice corretto");
        livelloLabel.setText("Livello: " + livelloCorrente);
        consegnaLabel.setText(consegne[getIndiceLivello()][indiceEsercizio]);
        codiceTextArea.clear();
        feedbackLabel.setText("");
    }

    @FXML
    private void confermaRisposta() {
        String rispostaUtente = codiceTextArea.getText().trim();
        String soluzioneCorretta = soluzioniAttese[getIndiceLivello()][indiceEsercizio].trim();

        if (ripulisci(rispostaUtente).equals(ripulisci(soluzioneCorretta))) {
            feedbackLabel.setText("✅ Risposta corretta!");
            aggiornaTacche(livelloCorrente);
        } else {
            feedbackLabel.setText("❌ Risposta errata. Prova ancora.");
        }
    }

    private String ripulisci(String codice) {
        return codice.replaceAll("\\s+", "");
    }

    private int getIndiceLivello() {
        return switch (livelloCorrente) {
            case "Intermedio" -> 1;
            case "Avanzato" -> 2;
            default -> 0;
        };
    }

    private void aggiornaTacche(String livello) {
        HBox taccheBox = switch (livello) {
            case "Intermedio" -> taccheIntermedio;
            case "Avanzato" -> taccheAvanzato;
            default -> tacchePrincipiante;
        };

        for (int i = 0; i < taccheBox.getChildren().size(); i++) {
            Circle tacca = (Circle) taccheBox.getChildren().get(i);
            if (tacca.getFill().equals(Color.GREY)) {
                tacca.setFill(Color.GREEN);
                break;
            }
        }
    }

    public void setLivello(String livello) {
        this.livelloCorrente = livello;
        this.indiceEsercizio = 0;
        mostraEsercizio();
    }
}
