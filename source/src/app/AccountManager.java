

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Scanner;

public class AccountManager 
{

    @FXML
    public TextField username;
    @FXML
    public TextField password;
    @FXML
    public TextField errorField;
    @FXML
    private PasswordField hiddenPassword;
    @FXML
    private CheckBox showPassword;

    File file = new File("accounts.csv");

    //Map containing <Username, Password>
    HashMap<String, String> loginInfo = new HashMap<>();

    Encryptor encryptor = new Encryptor();

    @FXML
    void changeVisibility(ActionEvent event) 
    {
        if (showPassword.isSelected()) 
        {
            password.setText(hiddenPassword.getText());
            password.setVisible(true);
            hiddenPassword.setVisible(false);
            return;
        }
        hiddenPassword.setText(password.getText());
        hiddenPassword.setVisible(true);
        password.setVisible(false);
    }

    @FXML
    void loginHandler(ActionEvent event) throws IOException, NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException 
    {
        String user = username.getText();
        String psw = getPassword();
        updateLoginUsernamesAndPasswords();

        String encryptedPassword = loginInfo.get(user);
        if(encryptor.encryptString(psw).equals(encryptedPassword))
        {
            System.out.println("successfully login!");
        } 
        else 
        {
            errorField.setVisible(true);
        }
    }

    private String getPassword()
    {
        if(password.isVisible())
        {
            return password.getText();
        } 
        else 
        {
            return hiddenPassword.getText();
        }
    }

    @FXML
    void createAccount(ActionEvent event) throws IOException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException
    {
        writeToFile();
    }

    private void updateLoginUsernamesAndPasswords() throws IOException 
    {
        Scanner scanner = new Scanner(file);
        loginInfo.clear();
        loginInfo = new HashMap<>();
        while (scanner.hasNext())
        {
            String[] usernameAndPassword = scanner.nextLine().split(",");
            loginInfo.put(usernameAndPassword[0],usernameAndPassword[1]);
        }
    }

    private void writeToFile() throws IOException, NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException 
    {
        String user = username.getText();
        String psw = getPassword();
        BufferedWriter writer = new BufferedWriter(new FileWriter(file,true));

        writer.write(user + "," + encryptor.encryptString(psw) + "\n");
        writer.close();
    }





    //Pagina per la creazione di un'account
    public void goto_register(ActionEvent event) throws IOException 
    {
        Parent root = FXMLLoader.load(getClass().getResource("register.fxml"));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

        //Pagina per accedere ad un account
        public void goto_login(ActionEvent event) throws IOException 
        {
            Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
}