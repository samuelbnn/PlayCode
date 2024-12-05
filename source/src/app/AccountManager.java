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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class AccountManager 
{
    @FXML
    private TextField username;
    @FXML
    private TextField password;
    @FXML
    private PasswordField hiddenPassword;
    @FXML
    private CheckBox showPassword;
    @FXML
    private TextField errorField;

    private final File file = new File("accounts.csv");
    private final HashMap<String, String> loginInfo = new HashMap<>();
    private final Encryptor encryptor = new Encryptor();

    @FXML
    void initialize() 
    {
        // Load accounts from file at the start
        try 
        {
            updateLoginInfo();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }

    @FXML
    void changeVisibility(ActionEvent event) 
    {
        if (showPassword.isSelected()) 
        {
            password.setText(hiddenPassword.getText());
            password.setVisible(true);
            hiddenPassword.setVisible(false);
        } 
        else 
        {
            hiddenPassword.setText(password.getText());
            hiddenPassword.setVisible(true);
            password.setVisible(false);
        }
    }

    @FXML
    void loginHandler(ActionEvent event) 
    {
        String user = username.getText();
        String psw = getPassword();

        if (user.isEmpty() || psw.isEmpty()) 
        {
            showError("Username e Password sono obbligatori.");
            return;
        }

        try 
        {
            updateLoginInfo();
            String encryptedPassword = loginInfo.get(user);

            if (encryptedPassword != null && encryptor.encryptString(psw).equals(encryptedPassword)) 
            {
                System.out.println("Login effettuato con successo!");
                errorField.setVisible(false);
               
                //Naviga nel muenu
                goto_menu(event);
            } 
            else 
            {
                showError("Credenziali errate.");
            }
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            showError("Errore durante il login.");
        }
    }

    @FXML
    void createAccount(ActionEvent event) 
    {
        String user = username.getText();
        String psw = getPassword();

        if (user.isEmpty() || psw.isEmpty()) 
        {
            showError("Username e Password sono obbligatori.");
            return;
        }

        try 
        {
            updateLoginInfo();
            if (loginInfo.containsKey(user))
            {
                showError("L'username è già in uso.");
                return;
            }

            writeToFile(user, psw);
            System.out.println("Account creato con successo!");
            errorField.setVisible(false);
            
            // Naviga al login
            goto_login(event);
        }
        catch (Exception e) 
        {
            e.printStackTrace();
            showError("Errore durante la creazione dell'account.");
        }
    }

    private void updateLoginInfo() throws IOException 
    {
        loginInfo.clear();
        if (!file.exists()) 
        {
            file.createNewFile();
        }

        try (Scanner scanner = new Scanner(file)) 
        {
            while (scanner.hasNextLine()) 
            {
                String[] usernameAndPassword = scanner.nextLine().split(",");
                if (usernameAndPassword.length == 2) 
                {
                    loginInfo.put(usernameAndPassword[0], usernameAndPassword[1]);
                }
            }
        }
    }

    private void writeToFile(String user, String psw) throws Exception 
    {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) 
        {
            writer.write(user + "," + encryptor.encryptString(psw) + "\n");
        }
    }

    private String getPassword() 
    {
        return password.isVisible() ? password.getText() : hiddenPassword.getText();
    }

    private void showError(String message) 
    {
        errorField.setText(message);
        errorField.setVisible(true);
    }

    public void goto_login(ActionEvent event) throws IOException 
    {
        navigateTo("login.fxml", event);
    }

    public void goto_register(ActionEvent event) throws IOException {
        navigateTo("register.fxml", event);
    }

    public void goto_menu(ActionEvent event) throws IOException {
        navigateTo("menu.fxml", event);
    }

    private void navigateTo(String fxmlFile, ActionEvent event) throws IOException 
    {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}