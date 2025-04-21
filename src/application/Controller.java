package application;

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
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;

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
    public void goto_login(ActionEvent event) throws IOException 
    {
        System.out.println("CLICK SU PLAY RICEVUTO!");

        FXMLLoader loader = new FXMLLoader(getClass().getResource(Costanti.PATH_FXML_LOGIN));
        Parent root = loader.load();

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);

        // Ottieni la dimensione dello schermo
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX(screenBounds.getMinX());
        stage.setY(screenBounds.getMinY());
        stage.setWidth(screenBounds.getWidth());
        stage.setHeight(screenBounds.getHeight());

        // Imposta la scena DOPO aver settato dimensioni
        stage.setScene(scene);
        stage.setResizable(false); // per bloccare il ridimensionamento
        stage.show();
    }
}