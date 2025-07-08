package application;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import java.util.List;

public class MenuController 
{
    @FXML
    private Label userNameLabel;
    @FXML
    private ProgressBar progressBarTrovaErrore;
    @FXML
    private ProgressBar progressBarCompletaCodice;
    @FXML
    private ProgressBar progressBarLinkedList;
    @FXML
    private ProgressBar progressBarStaticCode;
    @FXML
    private ProgressBar progressBarLeggiCodice;
    @FXML
    private ProgressBar progressBarStampaOutput;
        
    /**
     * Inizializza la schermata del menu e aggiorna le progress bar in base all'utente.
     */
    @FXML
    public void initialize() 
    {
        String username = Session.getCurrentUser();

        if (username != null && !username.isEmpty()) 
        {
            userNameLabel.setText("Benvenuto, " + username + "!");
        } 
        else 
        {
            userNameLabel.setText("Benvenuto!");
        }

        // Imposta le progress bar
        if (progressBarLinkedList != null)
            progressBarLinkedList.setProgress(calcolaAvanzamentoProgressBar(username, Costanti.ES_LINKED_LIST));
        if (progressBarCompletaCodice != null)
            progressBarCompletaCodice.setProgress(calcolaAvanzamentoProgressBar(username, Costanti.ES_COMPLETA_CODICE));
        if (progressBarLeggiCodice != null)
            progressBarLeggiCodice.setProgress(calcolaAvanzamentoProgressBar(username, Costanti.ES_LEGGI_CODICE));
        if (progressBarStampaOutput != null)
            progressBarStampaOutput.setProgress(calcolaAvanzamentoProgressBar(username, Costanti.ES_STAMPA_OUTPUT));
        if (progressBarTrovaErrore != null)
            progressBarTrovaErrore.setProgress(calcolaAvanzamentoProgressBar(username, Costanti.ES_TROVA_ERRORE));
        if (progressBarStaticCode != null)
            progressBarStaticCode.setProgress(calcolaAvanzamentoProgressBar(username, Costanti.ES_STATIC_CODE));
    }

    /**
     * Calcola il valore di avanzamento della progress bar.
     */
    private double calcolaAvanzamentoProgressBar(String utente, String titoloEsercizio) 
    {
        String grado = leggiGradoPerUtenteEsercizio(utente, titoloEsercizio);

        if (grado == null) return 0.0;

        return switch (grado) 
        {
            case "Principiante" -> 1.0 / 3;
            case "Intermedio"   -> 2.0 / 3;
            case "Avanzato"     -> 1.0;
            default             -> 0.0;
        };
    }

    /**
     * Lettura grado raggiunto dall'utente per uno specifico esercizio.
     */
    private String leggiGradoPerUtenteEsercizio(String utente, String titoloEsercizio) 
    {
        Path path = Paths.get(Costanti.PATH_FILE_STATO);

        if (!Files.exists(path)) 
        {
            return null;
        }

        try 
        {
            List<String> righe = Files.readAllLines(path, StandardCharsets.UTF_8);
            for (String riga : righe) 
            {
                if (!riga.startsWith(utente + ",")) 
                    continue;

                String[] parti = riga.split(",", 2);

                if (parti.length < 2) 
                    return null;

                String[] coppie = parti[1].split(",");

                for (String coppia : coppie) 
                {
                    String[] split = coppia.trim().split(" - ");

                    if (split.length == 2 && split[0].equals(titoloEsercizio)) 
                    {
                        return split[1];
                    }
                }
            }
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }

        return null;
    }
    
    //#region Esercizi

    /**
     * Avvio dell'esercizio "Leggi il Codice".
     */
    @FXML
    private void apriLeggiCodice(ActionEvent event) throws IOException 
    {
        FXMLLoader popupLoader = new FXMLLoader(App.class.getResource(Costanti.PATH_FXML_DESCRIZIONE));
        Parent popupRoot = popupLoader.load();
        DescrizioneEsercizioController descrController = popupLoader.getController();

        descrController.setTitolo(Costanti.ES_LEGGI_CODICE);
        descrController.setDescrizione("Leggi attentamente il codice fornito e comprendi il suo funzionamento." +
                                        "Rispondi alle domande per verificare la tua comprensione.");
        descrController.setAzioneInizia(() -> {

            try 
            {
                FXMLLoader loader = new FXMLLoader(App.class.getResource(Costanti.PATH_FXML_LEGGICODICE));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        });

        Stage popupStage = new Stage();
        popupStage.setScene(new Scene(popupRoot, 500, 300));
        popupStage.centerOnScreen();
        popupStage.setResizable(false);
        popupStage.setTitle("Descrizione");
        popupStage.showAndWait();
    }

    /**
     * Avvio dell'esercizio "Stampa l'Output".
     */
    @FXML
    private void apriStampaOutput(ActionEvent event) throws IOException 
    {
        FXMLLoader popupLoader = new FXMLLoader(App.class.getResource(Costanti.PATH_FXML_DESCRIZIONE));
        Parent popupRoot = popupLoader.load();
        DescrizioneEsercizioController descrController = popupLoader.getController();

        descrController.setTitolo(Costanti.ES_STAMPA_OUTPUT);
        descrController.setDescrizione("Digita il codice corretto che genera l’output indicato.");
        descrController.setAzioneInizia(() -> 
        {
            try 
            {
                FXMLLoader loader = new FXMLLoader(App.class.getResource(Costanti.PATH_FXML_STAMPAOUTPUT));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        });

        Stage popupStage = new Stage();
        popupStage.setScene(new Scene(popupRoot, 500, 300));
        popupStage.centerOnScreen();
        popupStage.setResizable(false);
        popupStage.setTitle("Descrizione");
        popupStage.showAndWait();
    }

    /**
     * Avvio dell'esercizio "Trova l'Errore".
     */
    @FXML
    private void apriTrovaErrore(ActionEvent event) throws IOException 
    {
        FXMLLoader popupLoader = new FXMLLoader(App.class.getResource(Costanti.PATH_FXML_DESCRIZIONE));
        Parent popupRoot = popupLoader.load();
        DescrizioneEsercizioController descrController = popupLoader.getController();

        descrController.setTitolo(Costanti.ES_TROVA_ERRORE);
        descrController.setDescrizione("Analizza il codice proposto e individua gli errori di logica o sintassi." +
                                        "Scegli tra le opzioni quella che corregge correttamente il problema.");
        descrController.setAzioneInizia(() -> 
        {
            try 
            {
                FXMLLoader loader = new FXMLLoader(App.class.getResource(Costanti.PATH_FXML_TROVAERRORE));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        });

        Stage popupStage = new Stage();
        popupStage.setScene(new Scene(popupRoot, 500, 300));
        popupStage.centerOnScreen();
        popupStage.setResizable(false);
        popupStage.setTitle("Descrizione");
        popupStage.showAndWait();
    }

    /**
     * Avvio dell'esercizio "Completa il Codice".
     */
    @FXML
    private void apriCompletaCodice(ActionEvent event) throws IOException 
    {
        FXMLLoader popupLoader = new FXMLLoader(App.class.getResource(Costanti.PATH_FXML_DESCRIZIONE));
        Parent popupRoot = popupLoader.load();
        DescrizioneEsercizioController descrController = popupLoader.getController();

        descrController.setTitolo(Costanti.ES_COMPLETA_CODICE);
        descrController.setDescrizione("Osserva il frammento di codice incompleto e riempi gli spazi vuoti." +
                                        "Inserisci le istruzioni corrette per far funzionare il programma.");
        descrController.setAzioneInizia(() -> 
        {
            try 
            {
                FXMLLoader loader = new FXMLLoader(App.class.getResource(Costanti.PATH_FXML_COMPLETACODICE));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        });

        Stage popupStage = new Stage();
        popupStage.setScene(new Scene(popupRoot, 500, 300));
        popupStage.centerOnScreen();
        popupStage.setResizable(false);
        popupStage.setTitle("Descrizione");
        popupStage.showAndWait();
    }
    
    /**
     * Avvio dell'esercizio "Linked List".
     */
    @FXML
    private void apriLinkedList(ActionEvent event) throws IOException 
    {
        FXMLLoader popupLoader = new FXMLLoader(App.class.getResource(Costanti.PATH_FXML_DESCRIZIONE));
        Parent popupRoot = popupLoader.load();
        DescrizioneEsercizioController descrController = popupLoader.getController();

        descrController.setTitolo(Costanti.ES_LINKED_LIST);
        descrController.setDescrizione("Lavora con strutture dati dinamiche come le liste collegate." +
                                        "Completa o modifica il codice per gestire correttamente nodi e collegamenti.");
        descrController.setAzioneInizia(() -> 
        {
            try 
            {
                FXMLLoader loader = new FXMLLoader(App.class.getResource(Costanti.PATH_FXML_LINKEDLIST));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        });

        Stage popupStage = new Stage();
        popupStage.setScene(new Scene(popupRoot, 500, 300));
        popupStage.centerOnScreen();
        popupStage.setResizable(false);
        popupStage.setTitle("Descrizione");
        popupStage.showAndWait();
    }

    /**
     * Avvio dell'esercizio "Static Code".
     */
    @FXML
    private void apriStaticCode(ActionEvent event) throws IOException 
    {
        FXMLLoader popupLoader = new FXMLLoader(App.class.getResource(Costanti.PATH_FXML_DESCRIZIONE));
        Parent popupRoot = popupLoader.load();
        DescrizioneEsercizioController descrController = popupLoader.getController();

        descrController.setTitolo(Costanti.ES_STATIC_CODE);
        descrController.setDescrizione("Analizza blocchi di codice statici, comprendendo classi e metodi." +
                                        "Risolvi quesiti focalizzati su struttura e comportamento statico in Java.");
        descrController.setAzioneInizia(() -> 
        {
            try 
            {
                FXMLLoader loader = new FXMLLoader(App.class.getResource(Costanti.PATH_FXML_STATICCODE));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        });

        Stage popupStage = new Stage();
        popupStage.setScene(new Scene(popupRoot, 500, 300));
        popupStage.centerOnScreen();
        popupStage.setResizable(false);
        popupStage.setTitle("Descrizione");
        popupStage.showAndWait();
    }

    //#endregion

    /**
     * Esegue il logout e torna alla schermata principale.
     */
    @FXML
    private void logout(ActionEvent event) 
    {
        try 
        {
            Session.setCurrentUser(null); 
            Parent root = FXMLLoader.load(App.class.getResource(Costanti.PATH_FXML_HOMEPAGE));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
}