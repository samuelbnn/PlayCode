package app;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class Controller
{
    private Stage stage;
    private Scene scene;
    
    @FXML
    private Button login;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password; 
    
    @FXML
    public void goto_login(ActionEvent event) throws IOException {
        System.out.println("CLICK SU PLAY RICEVUTO!");
        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}