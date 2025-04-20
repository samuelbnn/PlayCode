package application;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.scene.control.Button;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class MenuController {

    @FXML
    private Label userNameLabel;
    
    @FXML
    public void initialize() {
        String username = Session.getCurrentUser(); // otteniamo l'utente loggato
        if (username != null && !username.isEmpty()) {
            userNameLabel.setText("Benvenuto, " + username + "!");
        } else {
            userNameLabel.setText("Benvenuto!");
        }
    }
    
    // Metodi per le finestre cliccabili
    @FXML
    private void apriLeggiIlCodice(ActionEvent event) {
        System.out.println("Apertura esercizio: Leggi il Codice");
        // carica esercizio.fxml o una schermata dedicata
    }

    @FXML
    private void apriScritturaOutput(ActionEvent event) {
        System.out.println("Apertura esercizio: Scrittura con Output");
    }

    @FXML
    private void apriTrovaErrore(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("fxml/esercizio.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void apriCompletaCodice(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("fxml/completa.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
    

    @FXML
    private void apriLinkedList(ActionEvent event) {
        System.out.println("Apertura esercizio: Linked List");
    }

    @FXML
    private void apriStatic(ActionEvent event) {
        System.out.println("Apertura esercizio: Static or Not");
    }

    @FXML
    private void logout(ActionEvent event) {
        try {
            Session.setCurrentUser(null); 
            Parent root = FXMLLoader.load(getClass().getResource("fxml/homepage.fxml")); // oppure "login.fxml"
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}