package application;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import java.util.ArrayList;
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
    @FXML
    private ProgressBar progressBarScritturaOutput;
    
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

        // Imposta le progress bar solo se non sono null per evitare NullPointerException
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
        System.out.println("Apertura esercizio: Stampa Output");
        try 
        {
            Parent root = FXMLLoader.load(App.class.getResource(Costanti.PATH_FXML_STAMPAOUTPUT));
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
}