package application;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application 
{
    @Override
    public void start(Stage primaryStage) throws Exception 
    {
        try
        {
            System.out.println("Carico schermata iniziale...");
            Parent root = FXMLLoader.load(getClass().getResource("fxml/homepage.fxml"));
            System.out.println("FXML caricato!");

            Scene scene = new Scene(root);
            primaryStage.setTitle("Play");
            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
            primaryStage.show();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) 
    {
        launch(args);
    }
}
