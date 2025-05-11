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
    private void apriLeggiCodice(ActionEvent event) 
    {
        System.out.println("Apertura esercizio: Leggi il Codice");
        try 
        {
            Parent root = FXMLLoader.load(App.class.getResource(Costanti.PATH_FXML_LEGGICODICE));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }

    @FXML
    private void apriScritturaOutput(ActionEvent event) 
    {
        System.out.println("Apertura esercizio: Scrittura con Output");
        try 
        {
            Parent root = FXMLLoader.load(App.class.getResource(Costanti.PATH_FXML_SCRITTURAOUTPUT));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }


    @FXML
    private void apriTrovaErrore(ActionEvent event) 
    {
        System.out.println("Apertura esercizio: Trova l'Errore");
        try 
        {
            Parent root = FXMLLoader.load(App.class.getResource(Costanti.PATH_FXML_TROVAERRORE));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }

    @FXML
    private void apriCompletaCodice(ActionEvent event) throws IOException 
    {
        System.out.println("Apertura esercizio: Completa il Codice");
        try 
        {
            Parent root = FXMLLoader.load(App.class.getResource(Costanti.PATH_FXML_COMPLETACODICE));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void apriLinkedList(ActionEvent event) 
    {
        System.out.println("Apertura esercizio: Linked List");
        try 
        {
            Parent root = FXMLLoader.load(App.class.getResource(Costanti.PATH_FXML_LINKEDLIST));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }

    @FXML
    private void apriStaticCode(ActionEvent event) 
    {
        System.out.println("Apertura esercizio: Static Code");
        try 
        {
            Parent root = FXMLLoader.load(App.class.getResource(Costanti.PATH_FXML_STATICCODE));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
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