package application;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import java.util.HashMap;
import java.util.Map;

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

    private Map<String, ProgressData> progressDataMap = new HashMap<>();
    
    @FXML
    public void initialize() 
    {
        String username = Session.getCurrentUser(); //otteniamo l'utente loggato

        if (username != null && !username.isEmpty()) 
        {
            userNameLabel.setText("Benvenuto, " + username + "!");
        } 
        else 
        {
            userNameLabel.setText("Benvenuto!");
        }

        // Initialize progress data for each exercise
        progressDataMap.put("TrovaErrore", new ProgressData(0, 0, 5));
        progressDataMap.put("CompletaCodice", new ProgressData(0, 0, 5));
        progressDataMap.put("LinkedList", new ProgressData(0, 0, 5));

        // Update progress bars with initial data
        updateProgressBar(progressBarTrovaErrore, progressDataMap.get("TrovaErrore"));
        updateProgressBar(progressBarCompletaCodice, progressDataMap.get("CompletaCodice"));
        updateProgressBar(progressBarLinkedList, progressDataMap.get("LinkedList"));
    }

    // Add methods to update progress bars
    private void updateProgressBar(ProgressBar progressBar, ProgressData data) 
    {
        if (data.getTotal() > 0) 
        {
            double progress = (double) data.getCorrect() / data.getTotal();
            progressBar.setStyle("-fx-accent: green;");
            progressBar.setProgress(progress);
        } 
        else
        {
            progressBar.setStyle("-fx-accent: white;");
            progressBar.setProgress(0);
        }
    }

    void updateProgress(String exerciseKey, int correctIncrement, int incorrectIncrement) 
    {
        ProgressData data = progressDataMap.get(exerciseKey);
        if (data != null) 
        {
            data.incrementCorrect(correctIncrement);
            data.incrementIncorrect(incorrectIncrement);
            updateProgressBar(getProgressBarForExercise(exerciseKey), data);
        }
    }

    private ProgressBar getProgressBarForExercise(String exerciseKey) 
    {
        switch (exerciseKey) 
        {
            case "TrovaErrore":
                return progressBarTrovaErrore;
            case "CompletaCodice":
                return progressBarCompletaCodice;
            case "LinkedList":
                return progressBarLinkedList;
            default:
                return null;
        }
    }
    
    //#region Esercizi

    @FXML
    private void apriLeggiCodice(ActionEvent event) throws IOException {
        FXMLLoader popupLoader = new FXMLLoader(App.class.getResource("fxml/descrizione.fxml"));
        Parent popupRoot = popupLoader.load();
        DescrizioneEsercizioController descrController = popupLoader.getController();

        descrController.setTitolo("Leggi il Codice");
        descrController.setDescrizione("Leggi attentamente il codice fornito e comprendi il suo funzionamento." +
                                        "Rispondi alle domande per verificare la tua comprensione.");
        descrController.setAzioneInizia(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(App.class.getResource(Costanti.PATH_FXML_LEGGICODICE));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
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

    @FXML
    private void apriStampaOutput(ActionEvent event) throws IOException {
        FXMLLoader popupLoader = new FXMLLoader(App.class.getResource("fxml/descrizione.fxml"));
        Parent popupRoot = popupLoader.load();
        DescrizioneEsercizioController descrController = popupLoader.getController();

        descrController.setTitolo("Stampa l'Output");
        descrController.setDescrizione("Digita il codice corretto che genera l’output indicato.");
        descrController.setAzioneInizia(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(App.class.getResource(Costanti.PATH_FXML_STAMPAOUTPUT));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
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

    @FXML
    private void apriTrovaErrore(ActionEvent event) throws IOException {
        FXMLLoader popupLoader = new FXMLLoader(App.class.getResource("fxml/descrizione.fxml"));
        Parent popupRoot = popupLoader.load();
        DescrizioneEsercizioController descrController = popupLoader.getController();

        descrController.setTitolo("Trova l'Errore");
        descrController.setDescrizione("Analizza il codice proposto e individua gli errori di logica o sintassi." +
                                        "Scegli tra le opzioni quella che corregge correttamente il problema.");
        descrController.setAzioneInizia(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(App.class.getResource(Costanti.PATH_FXML_TROVAERRORE));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
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

    @FXML
    private void apriCompletaCodice(ActionEvent event) throws IOException {
        FXMLLoader popupLoader = new FXMLLoader(App.class.getResource("fxml/descrizione.fxml"));
        Parent popupRoot = popupLoader.load();
        DescrizioneEsercizioController descrController = popupLoader.getController();

        descrController.setTitolo("Completa il Codice");
        descrController.setDescrizione("Osserva il frammento di codice incompleto e riempi gli spazi vuoti." +
                                        "Inserisci le istruzioni corrette per far funzionare il programma.");
        descrController.setAzioneInizia(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(App.class.getResource(Costanti.PATH_FXML_COMPLETACODICE));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
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
    
    @FXML
    private void apriLinkedList(ActionEvent event) throws IOException {
        FXMLLoader popupLoader = new FXMLLoader(App.class.getResource("fxml/descrizione.fxml"));
        Parent popupRoot = popupLoader.load();
        DescrizioneEsercizioController descrController = popupLoader.getController();

        descrController.setTitolo("Linked List");
        descrController.setDescrizione("Lavora con strutture dati dinamiche come le liste collegate." +
                                        "Completa o modifica il codice per gestire correttamente nodi e collegamenti.");
        descrController.setAzioneInizia(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(App.class.getResource(Costanti.PATH_FXML_LINKEDLIST));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
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

    @FXML
    private void apriStaticCode(ActionEvent event) throws IOException {
        FXMLLoader popupLoader = new FXMLLoader(App.class.getResource("fxml/descrizione.fxml"));
        Parent popupRoot = popupLoader.load();
        DescrizioneEsercizioController descrController = popupLoader.getController();

        descrController.setTitolo("Static Code");
        descrController.setDescrizione("Analizza blocchi di codice statici, comprendendo classi e metodi." +
                                        "Risolvi quesiti focalizzati su struttura e comportamento statico in Java.");
        descrController.setAzioneInizia(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(App.class.getResource(Costanti.PATH_FXML_LINKEDLIST));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
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

    @FXML
    private void logout(ActionEvent event) 
    {
        try 
        {
            Session.setCurrentUser(null); 
            Parent root = FXMLLoader.load(App.class.getResource(Costanti.PATH_FXML_HOMEPAGE)); // oppure "login.fxml"
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }

    // Inner class to track progress data
    private static class ProgressData 
    {
        private int correct;
        private int incorrect;
        private int total;

        public ProgressData(int correct, int incorrect, int total) 
        {
            this.correct = correct;
            this.incorrect = incorrect;
            this.total = total;
        }

        public int getCorrect() 
        {
            return correct;
        }

        public int getIncorrect() 
        {
            return incorrect;
        }

        public int getTotal() 
        {
            return total;
        }

        public void incrementCorrect(int value) 
        {
            this.correct += value;
        }

        public void incrementIncorrect(int value)
        {
            this.incorrect += value;
        }
    }
}