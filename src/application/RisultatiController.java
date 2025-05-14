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

        if (lista == null || lista.isEmpty()) {
            risultatiListView.getItems().add("Nessun risultato trovato.");
            return;
        }

        // Populate the ListView with results
        for (RisultatiController.Risultato risultato : lista) {
            risultatiListView.getItems().add(risultato.toString());
        }
    }

    @FXML
    private void chiudiSchermata() {
        Stage stage = (Stage) btnChiudi.getScene().getWindow();
        stage.close();
    }

    // Oggetto che contiene i dati di un singolo risultato
    public static class Risultato {
        private final String esercizio;
        private final String livello;
        private final int risposteCorrette;
        private final LocalDateTime data;

        public Risultato(String esercizio, String livello, int risposteCorrette, LocalDateTime data) {
            this.esercizio = esercizio;
            this.livello = livello;
            this.risposteCorrette = risposteCorrette;
            this.data = data;
        }

        public String getEsercizio()        { return esercizio; }
        public String getLivello()          { return livello; }
        public int getRisposteCorrette()    { return risposteCorrette; }
        public LocalDateTime getData()      { return data; }

        @Override
        public String toString() {
            return String.format("%s - %s: %d risposte corrette il %s",
                esercizio, livello, risposteCorrette,
                data.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
    }

    /**
     * Legge risultati.csv e restituisce la lista di Risultato per lo user specificato.
     */
    public static List<Risultato> leggiRisultatiPerUtente(String utente) throws IOException {
        Path path = Paths.get(Costanti.PATH_FILE_RISULTATI);
        if (!Files.exists(path)) {
            return Collections.emptyList();
        }

        // Trovo la riga dell'utente (assumo una sola riga per utente)
        String lineaUtente = Files.readAllLines(path, StandardCharsets.UTF_8).stream()
            .filter(l -> l.startsWith(utente + ","))
            .findFirst()
            .orElse(null);

        if (lineaUtente == null) {
            return Collections.emptyList();
        }

        List<Risultato> risultati = new ArrayList<>();

        // Pattern per ciascun blocco [Esercizio  (...)(...)(...)]
        Pattern bloccoPattern = Pattern.compile("\\[([^\\]]+)\\]");
        Matcher bloccoMatcher = bloccoPattern.matcher(lineaUtente);

        // Pattern per le tuple (Livello;count;timestamp)
        Pattern tuplaPattern = Pattern.compile("\\(([^;]+);(\\d+);([^\\)]+)\\)");

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        while (bloccoMatcher.find()) {
            String contenuto = bloccoMatcher.group(1).trim();
            // separo nome esercizio da tutte le tuple
            int idxPar = contenuto.indexOf('(');
            String nomeEsercizio = contenuto.substring(0, idxPar).trim();

            Matcher tuplaMatcher = tuplaPattern.matcher(contenuto);
            while (tuplaMatcher.find()) {
                String livello = tuplaMatcher.group(1);
                int count    = Integer.parseInt(tuplaMatcher.group(2));
                LocalDateTime data = LocalDateTime.parse(tuplaMatcher.group(3), fmt);

                risultati.add(new Risultato(nomeEsercizio, livello, count, data));
            }
        }

        return risultati;
    }
}
