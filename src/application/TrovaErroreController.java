package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.*;
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
    @FXML private ProgressBar progressBar;
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

    @FXML
    public void initialize() 
    {
        gruppoRisposte = new ToggleGroup();
        risposta1.setToggleGroup(gruppoRisposte);
        risposta2.setToggleGroup(gruppoRisposte);
        risposta3.setToggleGroup(gruppoRisposte);

        caricaDomande();
        caricaProgresso(); 
        mostraDomandaCasuale();
        aggiornaStileLivelli();
        aggiornaProgressBar();
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
        List<Esercizio> disponibili = new ArrayList<>(eserciziPerLivello.get(livelloCorrente));
        disponibili.removeAll(mostratiPerLivello.get(livelloCorrente));

        if (disponibili.isEmpty()) 
        {
            avanzaLivello();
            return;
        }

        Collections.shuffle(disponibili);
        esercizioCorrente = disponibili.get(0);
        mostratiPerLivello.get(livelloCorrente).add(esercizioCorrente);

        titoloLabel.setText(esercizioCorrente.titolo);
        //livelloLabel.setText("Livello: " + esercizioCorrente.livello);
        codiceArea.setText(esercizioCorrente.codice);
        consegnaLabel.setText(esercizioCorrente.domanda);
        risposta1.setText(esercizioCorrente.risposte[0]);
        risposta2.setText(esercizioCorrente.risposte[1]);
        risposta3.setText(esercizioCorrente.risposte[2]);
        gruppoRisposte.selectToggle(null);
        feedbackLabel.setVisible(false);
    }

    @FXML
    private void confermaRisposta(ActionEvent event) 
    {
        RadioButton selezionata = (RadioButton) gruppoRisposte.getSelectedToggle();

        if (selezionata == null) 
            return;

        int scelta = selezionata == risposta1 ? 0 : selezionata == risposta2 ? 1 : 2;

        if (scelta == esercizioCorrente.indiceCorretta) 
        {
            feedbackLabel.setText("Corretto!");
            feedbackLabel.setStyle("-fx-text-fill: green;");
            punteggio++;
            successiConsecutivi++;
            aggiornaProgressBar();
        
            if (successiConsecutivi >= nSuccessiPerLivello) 
            {
                avanzaLivello(); // 👉 passa al livello successivo!
            } 
            else 
            {
                mostraDomandaCasuale(); // 👉 rimani nello stesso livello
            }

        } 
        else 
        {
            feedbackLabel.setText("Sbagliato!");
            feedbackLabel.setStyle("-fx-text-fill: red;");
            successiConsecutivi = 0;
            aggiornaProgressBar();
        }

        feedbackLabel.setVisible(true);
    }

    private void aggiornaProgressBar() 
    {
        double progress = (double) successiConsecutivi / nSuccessiPerLivello;
        progressBar.setProgress(progress);
    }

    private void avanzaLivello() 
    {
        successiConsecutivi = 0;
        aggiornaProgressBar();

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
                    aggiornaProgressBar();
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
        Parent root = FXMLLoader.load(getClass().getResource(Costanti.PATH_FXML_MENU));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    private void vaiALivelloPrincipiante(ActionEvent event) 
    {
        livelloCorrente = "Principiante";
        successiConsecutivi = 0;
        aggiornaStileLivelli();
        mostraDomandaCasuale();
    }

    @FXML
    private void vaiALivelloIntermedio(ActionEvent event) 
    {
        livelloCorrente = "Intermedio";
        successiConsecutivi = 0;
        aggiornaStileLivelli();
        mostraDomandaCasuale();
    }

    @FXML
    private void vaiALivelloAvanzato(ActionEvent event) 
    {
        livelloCorrente = "Avanzato";
        successiConsecutivi = 0;
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

    static class Esercizio 
    {
        String titolo;
        String livello;
        String codice;
        String domanda;
        String[] risposte;
        int indiceCorretta;

        public Esercizio(String titolo, String livello, String codice, String domanda, String[] risposte, int indiceCorretta) 
        {
            this.titolo = titolo;
            this.livello = livello;
            this.codice = codice;
            this.domanda = domanda;
            this.risposte = risposte;
            this.indiceCorretta = indiceCorretta;
        }

        @Override
        public boolean equals(Object o) 
        {
            if (this == o) return true;
            if (!(o instanceof Esercizio)) 
                return false;
            Esercizio that = (Esercizio) o;

            return Objects.equals(codice, that.codice);
        }

        @Override
        public int hashCode() 
        {
            return Objects.hash(codice);
        }
    }
}