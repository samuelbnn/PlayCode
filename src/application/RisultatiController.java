package application;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RisultatiController 
{
    @FXML
    private ListView<String> risultatiListView;

    @FXML
    private Button btnChiudi;

    @FXML
    private Label titoloLabel;

    public void initialize() 
    {
        String utente = Session.getCurrentUser();
        titoloLabel.setText("Riassunto della partita - " + utente);

        // Imposta titolo e icona della finestra
        javafx.application.Platform.runLater(() -> {
            Stage stage = (Stage) titoloLabel.getScene().getWindow();
            stage.setTitle(Costanti.APP_NAME);
            stage.getIcons().clear();
            stage.getIcons().add(new Image(Costanti.LOGO));
        });

        // Retrieve results for the current user
        List<RisultatiController.Risultato> lista = null;
        try 
        {
            lista = RisultatiController.leggiRisultatiPerUtente(utente);
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }

        if (lista == null || lista.isEmpty()) 
        {
            risultatiListView.getItems().add("Nessun risultato trovato.");
            return;
        }

        // Impostazione colorazione delle righe in base al punteggio
        risultatiListView.setCellFactory(lv -> new javafx.scene.control.ListCell<>() 
        {
            @Override
            protected void updateItem(String item, boolean empty) 
            {
                super.updateItem(item, empty);
                if (empty || item == null) 
                {
                    setText(null);
                    setStyle("");
                } 
                else 
                {
                    setText(item);
                    int risposte = 0;
                    try 
                    {
                        int idx = item.indexOf(":");
                        int idxSlash = item.indexOf("/", idx);
                        if (idx != -1 && idxSlash != -1) 
                        {
                            String num = item.substring(idx + 1, idxSlash).replaceAll("[^0-9]", "").trim();
                            risposte = Integer.parseInt(num);
                        }
                    } 
                    catch (Exception e) 
                    {
                        risposte = 0;
                    }
                    String baseBg, selectedBg;
                    if (risposte < 3) 
                    {
                        baseBg = "#ee6363";  // rosso
                        selectedBg = "#e93a3a"; // rosso selezionato
                    } 
                    else 
                    {
                        baseBg = "#77f877"; // verde 
                        selectedBg = "#85ea85"; // verde selezionato
                    }
                    if (isSelected()) 
                    {
                        setStyle("-fx-control-inner-background: " + selectedBg + "; -fx-background-color: " + selectedBg + "; -fx-text-fill: black;");
                    } 
                    else 
                    {
                        setStyle("-fx-control-inner-background: " + baseBg + "; -fx-background-color: " + baseBg + "; -fx-text-fill: black;");
                    }
                }
            }
        });

        // Aggiungo i risultati alla ListView
        for (RisultatiController.Risultato risultato : lista) 
        {
            risultatiListView.getItems().add(risultato.toString());
        }
    }

    /**
     * Chiude la finestra dei risultati.
     */
    @FXML
    private void chiudiSchermata() 
    {
        Stage stage = (Stage) btnChiudi.getScene().getWindow();
        stage.close();
    }

    /**
     * Classe che definisce il singolo risultato di un esercizio.
     */
    public static class Risultato 
    {
        private final String esercizio;
        private final String livello;
        private final int risposteCorrette;
        private final String durata;
        private final LocalDateTime data;

        public Risultato(String esercizio, String livello, int risposteCorrette, String durata, LocalDateTime data) 
        {
            this.esercizio = esercizio;
            this.livello = livello;
            this.risposteCorrette = risposteCorrette;
            this.durata = durata;
            this.data = data;
        }

        public String getEsercizio()        { return esercizio; }
        public String getLivello()          { return livello; }
        public int getRisposteCorrette()    { return risposteCorrette; }
        public String getDurata()           { return durata; }
        public LocalDateTime getData()      { return data; }

        /**
         * Stampa dei risultati ottenuti dall'utente.
         */
        @Override
        public String toString() 
        { 
            String durataStr = (durata != null && !durata.isEmpty()) ? " - Durata: " + durata : "";
            return String.format("%s - %s: %d/5 risposte corrette - Completato in %s il: %s",
                esercizio, livello, risposteCorrette, durataStr,
                data.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
    }

    /**
     * Legge i risultati del csv e restituisce la lista del risultato per l'utente.
     */
    public static List<Risultato> leggiRisultatiPerUtente(String utente) throws IOException 
    {
        Path path = Paths.get(Costanti.PATH_FILE_RISULTATI);
        if (!Files.exists(path)) 
        {
            return Collections.emptyList();
        }

        // Ricerca riga dell'utente
        String lineaUtente = Files.readAllLines(path, StandardCharsets.UTF_8).stream()
            .filter(l -> l.startsWith(utente + ","))
            .findFirst()
            .orElse(null);

        if (lineaUtente == null) 
        {
            return Collections.emptyList();
        }

        List<Risultato> risultati = new ArrayList<>();

        // Pattern per ciascun blocco [Esercizio  (...)(...)(...)]
        Pattern bloccoPattern = Pattern.compile("\\[([^\\]]+)\\]");
        Matcher bloccoMatcher = bloccoPattern.matcher(lineaUtente);

        // Pattern per le tuple (Livello;count;durata;timestamp)
        Pattern tuplaPattern = Pattern.compile("\\(([^;]+);(\\d+);(\\d{2}:\\d{2}:\\d{2});([^\\)]+)\\)");

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        while (bloccoMatcher.find()) 
        {
            String contenuto = bloccoMatcher.group(1).trim();
            // separo nome esercizio da tutte le tuple
            int idxPar = contenuto.indexOf('(');
            String nomeEsercizio = contenuto.substring(0, idxPar).trim();

            Matcher tuplaMatcher = tuplaPattern.matcher(contenuto);
            while (tuplaMatcher.find()) 
            {
                String livello = tuplaMatcher.group(1);
                int count    = Integer.parseInt(tuplaMatcher.group(2));
                String durata = tuplaMatcher.group(3);
                String timestampStr = tuplaMatcher.group(4);
                LocalDateTime data = LocalDateTime.parse(timestampStr, fmt);

                risultati.add(new Risultato(nomeEsercizio, livello, count, durata, data));
            }
        }

        return risultati;
    }
}