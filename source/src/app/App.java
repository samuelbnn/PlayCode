

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class App extends Application 
{
    @Override
    public void start(Stage primaryStage) throws Exception 
    {
        try
        {
            //FXMLLoader loader = new FXMLLoader(getClass().getResource("homepage.fxml"));
            Parent root = FXMLLoader.load(getClass().getResource("homepage.fxml"));
            Scene scene = new Scene(root);

            primaryStage.setTitle("Play");
            //primaryStage.getIcons().add(new Image(getClass().getResource("img/favicon.png").toString()));
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