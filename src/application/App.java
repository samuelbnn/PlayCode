package application;

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
            Parent root = FXMLLoader.load(App.class.getResource(Costanti.PATH_FXML_HOMEPAGE));
            Image image = new Image(Costanti.LOGO);
            primaryStage.getIcons().add(image);
            Scene scene = new Scene(root);
            primaryStage.setTitle(Costanti.APP_NAME);
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