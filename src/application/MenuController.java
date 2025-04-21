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

public class MenuController 
{

    @FXML
    private Label userNameLabel;
    
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
    }
    
    //Metodi per le finestre cliccabili
    @FXML
    private void apriLeggiCodice(ActionEvent event) 
    {
        System.out.println("Apertura esercizio: Leggi il Codice");
        //carica leggiCodice.fxml
    }

    @FXML
    private void apriScritturaOutput(ActionEvent event) 
    {
        System.out.println("Apertura esercizio: Scrittura con Output");
        //carica scritturaOutput.fxml
    }


    @FXML
    private void apriTrovaErrore(ActionEvent event) 
    {
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
        Parent root = FXMLLoader.load(App.class.getResource(Costanti.PATH_FXML_COMPLETACODICE));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
    

    @FXML
    private void apriLinkedList(ActionEvent event) 
    {
        System.out.println("Apertura esercizio: Linked List");
        //carica linkedList.fxml
    }

    @FXML
    private void apriStaticCode(ActionEvent event) 
    {
        System.out.println("Apertura esercizio: Static Code");
        //carica staticCode.fxml
    }

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