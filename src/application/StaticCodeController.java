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

public class StaticCodeController 
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
    private static final String titolo= Costanti.ES_STATIC_CODE;
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
        eserciziPerLivello.put(Costanti.LIVELLO_PRINCIPIANTE, List.of(
            new Esercizio(
                titolo,
                Grado.PRINCIPIANTE,
                "public class Test {\n    static int x = 5;\n    public static void main(String[] args) {\n        System.out.println(x);\n    }\n}",
                "Cosa verrà stampato a schermo?",
                new String[]{
                    "5", 
                    "null", 
                    "0", 
                    "Errore di compilazione"},
                0
            ),
            new Esercizio(
                titolo,
                Grado.PRINCIPIANTE,
                "",
                "Cosa significa che una variabile è dichiarata static?",
                new String[]{
                    "Appartiene alla classe e non all'istanza", 
                    "Appartiene all'oggetto", 
                    "Può essere usata solo nel main", 
                    "Non può cambiare valore"},
                0
            ),
            new Esercizio(
                titolo,
                Grado.PRINCIPIANTE,
                "public class Test {\n    static int x = 10;\n    public static void main(String[] args) {\n        Test a = new Test();\n        Test b = new Test();\n        a.x = 20;\n        System.out.println(b.x);\n    }\n}",
                "Cosa verrà stampato a schermo?",
                new String[]{
                    "20", 
                    "10", 
                    "Errore", 
                    "null"},
                0
            ),
            new Esercizio(
                titolo,
                Grado.PRINCIPIANTE,
                "public class Utility {\n    static void greet() {\n        System.out.println(\"Ciao!\");\n    }\n}",
                "Come si può chiamare il metodo greet da un'altra classe?",
                new String[]{
                    "Utility.greet();", 
                    "new Utility().greet();", 
                    "Utility->greet();", 
                    "greet();"},
                0
            ),
            new Esercizio(
                titolo,
                Grado.PRINCIPIANTE,
                "-",
                "Quale tra le seguenti affermazioni è corretta riguardo ai metodi statici?",
                new String[]{
                    "Un metodo statico può essere chiamato senza creare un oggetto", 
                    "Un metodo statico può accedere direttamente a campi non statici", 
                    "I metodi statici devono restituire un valore", 
                    "Un metodo statico può essere sovrascritto nelle sottoclassi"},
                0
            )
        ));

        eserciziPerLivello.put(Costanti.LIVELLO_INTERMEDIO, List.of(
            new Esercizio(
                titolo,
                Grado.INTERMEDIO,
                "public class Contatore {\n    static int count = 0;\n    public Contatore() {\n        count++;\n    }\n    public static void main(String[] args) {\n        new Contatore();\n        new Contatore();\n        System.out.println(count);\n    }\n}",
                "Cosa verrà stampato a schermo?",
                new String[]{
                    "2", 
                    "0", 
                    "1", 
                    "Errore"},
                0
            ),
            new Esercizio(
                titolo,
                Grado.INTERMEDIO,
                "-",
                "Perché un metodo statico non può accedere direttamente a variabili d'istanza?",
                new String[]{
                    "Perché non esistono nel contesto statico", 
                    "Perché sono sempre private", 
                    "Perché non sono inizializzate", 
                    "Perché Java lo impedisce per motivi di sicurezza"},
                0
            ),
            new Esercizio(
                titolo,
                Grado.INTERMEDIO,
                "public class Test {\n    static {\n        System.out.println(\"Inizializzazione\");\n    }\n    public static void main(String[] args) {\n        System.out.println(\"Main\");\n    }\n}",
                "Cosa verrà stampato a schermo?",
                new String[]{
                    "Inizializzazione\nMain", 
                    "Main\nInizializzazione", 
                    "Solo Main", 
                    "Errore"},
                0
            ),
            new Esercizio(
                titolo,
                Grado.INTERMEDIO,
                "class Esempio {\n    static int x;\n    static {\n        x = 10;\n    }\n}",
                "Quando viene eseguito il blocco statico?",
                new String[]{
                    "Al caricamento della classe", 
                    "Ogni volta che si crea un oggetto", 
                    "Alla chiamata del costruttore", 
                    "Alla fine del programma"},
                0
            ),
            new Esercizio(
                titolo,
                Grado.INTERMEDIO,
                "--",
                "Cosa succede se si usa `this` in un metodo statico?",
                new String[]{
                    "Errore di compilazione: `this` non può essere usato in contesto statico", 
                    "Compila ma non ha effetto", 
                    "Richiama il costruttore", 
                    "Compila ma genera un warning"},
                0
            )
        ));

        eserciziPerLivello.put(Costanti.LIVELLO_AVANZATO, List.of(
            new Esercizio(
                titolo,
                Grado.AVANZATO,
                "class A {\n    static int x = 10;\n}\nclass B extends A {\n    static int x = 20;\n}\npublic class Main {\n    public static void main(String[] args) {\n        System.out.println(B.x);\n        System.out.println(A.x);\n    }\n}",
                "Cosa verrà stampato a schermo?",
                new String[]{
                    "20 e 10", 
                    "10 e 10", 
                    "20 e 20", 
                    "Errore di compilazione"},
                0
            ),
            new Esercizio(
                titolo,
                Grado.AVANZATO,
                "class Base {\n    static void print() {\n        System.out.println(\"Base\");\n    }\n}\nclass Derivata extends Base {\n    static void print() {\n        System.out.println(\"Derivata\");\n    }\n}",
                "Cosa succede se chiami `Base.print()` e `Derivata.print()`?",
                new String[]{
                    "Ogni classe stampa il proprio metodo: Base e poi Derivata", 
                    "Entrambe stampano Derivata", 
                    "Stampa Base due volte", 
                    "Errore di override"},
                0
            ),
            new Esercizio(
                titolo,
                Grado.AVANZATO,
                "public class Costanti {\n    static final double PI = 3.1415;\n}",
                "Qual è il vantaggio di usare `static final`?",
                new String[]{
                    "Il valore è costante e condiviso tra tutte le istanze", 
                    "Si può modificare solo nel costruttore", 
                    "Serve solo per le variabili di istanza", 
                    "Non occupa memoria"},
                0
            ),
            new Esercizio(
                titolo,
                Grado.AVANZATO,
                "class Genitore {\n    static int x = 5;\n}\nclass Figlio extends Genitore {\n    static int y = x * 2;\n}\nSystem.out.println(Figlio.y);",
                "Cosa verrà stampato a schermo?",
                new String[]{
                    "10", 
                    "5", 
                    "0", 
                    "Errore"},
                0
            ),
            new Esercizio(
                titolo,
                Grado.AVANZATO,
                "class Esempio {\n    static void stampa() {\n        System.out.println(\"Statica\");\n    }\n}\npublic class Main {\n    public static void main(String[] args) {\n        Esempio e = new Esempio();\n        e.stampa();\n    }\n}",
                "Cosa succede eseguendo questo codice?",
                new String[]{
                    "Chiama il metodo ma non è buona pratica accedere static tramite istanza", 
                    "Errore di compilazione", 
                    "Nessun output", 
                    "Chiama il costruttore due volte"},
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
 		consegnaLabel.setText(esercizioCorrente.domanda);
        // Mostra codice se presente
        if (codiceArea != null) 
        {
            if (esercizioCorrente.codice != null && !esercizioCorrente.codice.isEmpty() && !esercizioCorrente.codice.contains("-")) 
            {
        codiceArea.setText(esercizioCorrente.codice);
            codiceArea.setVisible(true);
            codiceArea.setManaged(true);
            } 
            else 
            {
            codiceArea.clear();
            codiceArea.setVisible(false);
            codiceArea.setManaged(false);
        }
        }
        
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
            case Costanti.LIVELLO_PRINCIPIANTE -> {
                livelloCorrente = "Intermedio";
                setTimestampInizioLivello();    //Reset timestamp per nuovo livello
            }
            case Costanti.LIVELLO_INTERMEDIO -> {
                livelloCorrente = Costanti.LIVELLO_AVANZATO;
                setTimestampInizioLivello();    //Reset timestamp per nuovo livello
            }
            case Costanti.LIVELLO_AVANZATO -> {
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
            statoTacche.put(Costanti.LIVELLO_PRINCIPIANTE, new ArrayList<>(Collections.nCopies(5, "")));
            statoTacche.put(Costanti.LIVELLO_INTERMEDIO, new ArrayList<>(Collections.nCopies(5, "")));
            statoTacche.put(Costanti.LIVELLO_AVANZATO, new ArrayList<>(Collections.nCopies(5, "")));
        }

        String utente = Session.getCurrentUser();
        try (Scanner scanner = new Scanner(new File(Costanti.PATH_FILE_PROGRESSI))) 
        {
            while (scanner.hasNextLine()) 
            {
                String[] parts = scanner.nextLine().split(",");
                if (progressManager.isProgressoValido(parts, utente, titolo)) 
                {
                    statoTacche.put(Costanti.LIVELLO_PRINCIPIANTE, progressManager.normalizeTacche(parts[6], 5));
                    statoTacche.put(Costanti.LIVELLO_INTERMEDIO, progressManager.normalizeTacche(parts[7], 5));
                    statoTacche.put(Costanti.LIVELLO_AVANZATO, progressManager.normalizeTacche(parts[8], 5));
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
            case Costanti.LIVELLO_PRINCIPIANTE -> coloraTacca(tacchePrincipiante, colore);
            case Costanti.LIVELLO_INTERMEDIO -> coloraTacca(taccheIntermedio, colore);
            case Costanti.LIVELLO_AVANZATO -> coloraTacca(taccheAvanzato, colore);
        }
    }

    private void aggiornaTacche() 
    {
        aggiornaVisualizzazioneTacche(tacchePrincipiante, statoTacche.get(Costanti.LIVELLO_PRINCIPIANTE));
        aggiornaVisualizzazioneTacche(taccheIntermedio, statoTacche.get(Costanti.LIVELLO_INTERMEDIO));
        aggiornaVisualizzazioneTacche(taccheAvanzato, statoTacche.get(Costanti.LIVELLO_AVANZATO));
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
        statoTacche.put(Costanti.LIVELLO_PRINCIPIANTE, ProgressManager.translateTacche(loadedProgress.getOrDefault(Costanti.LIVELLO_PRINCIPIANTE, new ArrayList<>()), 5));
        statoTacche.put(Costanti.LIVELLO_INTERMEDIO, ProgressManager.translateTacche(loadedProgress.getOrDefault(Costanti.LIVELLO_INTERMEDIO, new ArrayList<>()), 5));
        statoTacche.put(Costanti.LIVELLO_AVANZATO, ProgressManager.translateTacche(loadedProgress.getOrDefault(Costanti.LIVELLO_AVANZATO, new ArrayList<>()), 5));

        if (loadedProgress.getOrDefault(Costanti.LIVELLO_PRINCIPIANTE, new ArrayList<>()).stream().anyMatch(t -> t.equals("G") || t.equals("R"))) 
        {
            livelliCompletati.add(Costanti.LIVELLO_PRINCIPIANTE);
            livelloCorrente = Costanti.LIVELLO_INTERMEDIO;
        }
        if (loadedProgress.getOrDefault(Costanti.LIVELLO_INTERMEDIO, new ArrayList<>()).stream().anyMatch(t -> t.equals("G") || t.equals("R"))) 
        {
            livelliCompletati.add(Costanti.LIVELLO_INTERMEDIO);
            livelloCorrente = Costanti.LIVELLO_AVANZATO;
        }
        if (loadedProgress.getOrDefault(Costanti.LIVELLO_AVANZATO, new ArrayList<>()).stream().anyMatch(t -> t.equals("G") || t.equals("R"))) 
        {
            livelliCompletati.add(Costanti.LIVELLO_AVANZATO);

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
        if (livelliCompletati.contains(Costanti.LIVELLO_PRINCIPIANTE)) 
        {
            feedbackLabel.setText("Hai già completato il livello " + Costanti.LIVELLO_PRINCIPIANTE + "!");
            feedbackLabel.setStyle("-fx-text-fill: " + Costanti.BLU + ";");
            feedbackLabel.setVisible(true);
            return;
        }

        livelloCorrente = Costanti.LIVELLO_PRINCIPIANTE;
        aggiornaStileLivelli();
        setTimestampInizioLivello(); //Reset timestamp quando si entra nel livello
    }

    @FXML
    private void vaiALivelloIntermedio(ActionEvent event) 
    {
        if (!livelliCompletati.contains(Costanti.LIVELLO_PRINCIPIANTE)) 
        {
            feedbackLabel.setText("Completa il livello " + Costanti.LIVELLO_PRINCIPIANTE + " prima di accedere a " + Costanti.LIVELLO_INTERMEDIO + "!");
            feedbackLabel.setStyle("-fx-text-fill: " + Costanti.ROSSO + ";");
            feedbackLabel.setVisible(true);
            return;
        }

        if (livelliCompletati.contains(Costanti.LIVELLO_INTERMEDIO)) 
        {
            feedbackLabel.setText("Hai già completato il livello " + Costanti.LIVELLO_INTERMEDIO+ "!");
            feedbackLabel.setStyle("-fx-text-fill: " + Costanti.BLU + ";");
            feedbackLabel.setVisible(true);
            return;
        }

        livelloCorrente = Costanti.LIVELLO_INTERMEDIO;
        aggiornaStileLivelli();
        setTimestampInizioLivello(); //Reset timestamp quando si entra nel livello
    }

    @FXML
    private void vaiALivelloAvanzato(ActionEvent event) 
    {
        if (!livelliCompletati.contains(Costanti.LIVELLO_INTERMEDIO)) 
        {
            feedbackLabel.setText("Completa il livello " + Costanti.LIVELLO_INTERMEDIO + " prima di accedere a " + Costanti.LIVELLO_AVANZATO + "!");
            feedbackLabel.setStyle("-fx-text-fill: " + Costanti.ROSSO + ";");
            feedbackLabel.setVisible(true);
            return;
        }

        if (livelliCompletati.contains(Costanti.LIVELLO_AVANZATO)) 
        {
            feedbackLabel.setText("Hai già completato il livello " + Costanti.LIVELLO_AVANZATO + "!");
            feedbackLabel.setStyle("-fx-text-fill: " + Costanti.BLU + ";");
            feedbackLabel.setVisible(true);
            return;
        }

        livelloCorrente = Costanti.LIVELLO_AVANZATO;
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
            case Costanti.LIVELLO_PRINCIPIANTE -> btnPrincipiante.getStyleClass().add("selected");
            case Costanti.LIVELLO_INTERMEDIO -> btnIntermedio.getStyleClass().add("selected");
            case Costanti.LIVELLO_AVANZATO -> btnAvanzato.getStyleClass().add("selected");
        }
    }
    //endregion
}