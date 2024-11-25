

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
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
    
    //Pagina per accedere ad un account
    public void goto_login(ActionEvent event) throws IOException 
    {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    //Pagina per la creazione di un'account
    public void goto_register(ActionEvent event) throws IOException 
    {
        Parent root = FXMLLoader.load(getClass().getResource("register.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

  // //region AccountManager
  // private AccountManager accountManager = new AccountManager();

  // @FXML
  // private void handleCreateAccount() 
  // {
  //     String username = "";
  //     String password = "";        
  //     username = username.toString();
  //     password = password.toString();
  //     if (accountManager.createAccount(username, password)) 
  //     {
  //         showAlert(Alert.AlertType.INFORMATION, "Account creato con successo!");
  //     } 
  //     else 
  //     {
  //         showAlert(Alert.AlertType.ERROR, "L'username esiste già.");
  //     }
  // }

  // @FXML
  // private void handleLogin() 
  // {
  //     String username = "";
  //     String password = "";  
  //     username = username.toString();
  //     password = password.toString();
  //     if (accountManager.login(username, password)) 
  //     {
  //         showAlert(Alert.AlertType.INFORMATION, "Login avvenuto con successo!");
  //     } 
  //     else 
  //     {
  //         showAlert(Alert.AlertType.ERROR, "Credenziali errate.");
  //     }
  // }

  // private void showAlert(Alert.AlertType alertType, String message) 
  // {
  //     Alert alert = new Alert(alertType);
  //     alert.setContentText(message);
  //     alert.showAndWait();
  // }
  // //endregion
}