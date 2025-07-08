package application;

import javafx.application.Platform;
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
    private PasswordField hiddenPassword;
    @FXML
    private TextField password;
    @FXML
    private CheckBox showPassword;
    @FXML
    private TextField messageField;

    private final File file = new File(Costanti.PATH_FILE_ACCOUNTS);
    private final HashMap<String, String> loginInfo = new HashMap<>();
    private final Encryptor encryptor = new Encryptor();

    /**
     * Carica le informazioni di login dal file.
     */
    @FXML
    void initialize()  
    {
        try 
        {
            updateLoginInfo();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }

    /**
     * Gestione visibilità password.
     */
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

    /**
     * Verifica le credenziali dell'utente.
     */
    @FXML
    void loginHandler(ActionEvent event) 
    {
        String user = username.getText();
        String psw = getPassword();

        if (user.isEmpty() || psw.isEmpty()) 
        {
            showMessage("Username e Password sono obbligatori.", false);
            return;
        }

        try 
        {
            updateLoginInfo();
            String encryptedPassword = loginInfo.get(user);

            if (encryptedPassword != null && encryptor.encryptString(psw).equals(encryptedPassword)) 
            {
                System.out.println("Login effettuato con successo!");
                showMessage("Login riuscito! Benvenuto, " + user + "!", true);
                Session.setCurrentUser(user);

                new Thread(() -> {
                    try 
                    {
                        Thread.sleep(2000);  // 2.0 secondi di attesa
                        Platform.runLater(() -> {
                            try 
                            {
                                goto_menu(event);
                            } 
                            catch (IOException e) 
                            {
                                e.printStackTrace();
                            }
                        });
                    } 
                    catch (InterruptedException e) 
                    {
                        e.printStackTrace();
                    }
                }).start();
            } 
            else 
            {
                showMessage("Credenziali errate.", false);
            }
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            showMessage("Errore durante il login.", false);
        }
    }

    /**
     * Gestisce la creazione di un nuovo account.
     */
    @FXML
    void createAccount(ActionEvent event) 
    {
        String user = username.getText();
        String psw = getPassword();

        if (user.isEmpty() || psw.isEmpty()) 
        {
            showMessage("Username e Password sono obbligatori.", false);
            return;
        }

        try 
        {
            updateLoginInfo();
            if (loginInfo.containsKey(user)) 
            {
                showMessage("L'username è già in uso.", false);
                return;
            }

            writeToFile(user, psw);
            System.out.println("Account creato con successo!");
            showMessage("Account creato con successo!", true);

            new Thread(() -> {
                try 
                {
                    Thread.sleep(2000);  // 2.0 secondi di attesa
                    Platform.runLater(() -> {
                        try 
                        {
                            Session.setCurrentUser(user); // salva l'utente
                            goto_menu(event);
                        } 
                        catch (IOException e) 
                        {
                            e.printStackTrace();
                        }
                    });
                } 
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }).start();
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            showMessage("Errore durante la creazione dell'account.", false);
        }
    }

    /**
     * Aggiorna login file.
     */
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

    /**
     * Registrazione nuovo account criptato nel file.
     */
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

    /**
     * Messaggi di feedback all'utente.
     */
    private void showMessage(String message, boolean isSuccess) 
    {
        Platform.runLater(() -> {
            messageField.setText(message);
            messageField.setVisible(true);

            if (isSuccess) 
            {
                messageField.setStyle("-fx-background-color: #d4edda; -fx-text-fill: #155724; -fx-border-color: #c3e6cb; -fx-border-radius: 4;");
            } 
            else 
            {
                messageField.setStyle("-fx-background-color: #f8d7da; -fx-text-fill: #721c24; -fx-border-color: #f5c6cb; -fx-border-radius: 4;");
            }

            new Thread(() -> 
            {
                try 
                {
                    Thread.sleep(2000);  // 2.5 secondi di attesa
                    Platform.runLater(() -> messageField.setVisible(false)); // Nascondi il messaggio
                } 
                catch (InterruptedException e) 
                {
                    e.printStackTrace();
                }
            }).start();
        });
    }

    /**
     * Reindirizzamento al login.
     */
    public void goto_login(ActionEvent event) throws IOException 
    {
        navigateTo(Costanti.PATH_FXML_LOGIN, event);
    }

    /**
     * Reindirizzamento al menu principale.
     */
    public void goto_menu(ActionEvent event) throws IOException 
    {
        navigateTo(Costanti.PATH_FXML_MENU, event);
    }

    /**
     * Naviga a una schermata specificata dal file FXML.
     */
    private void navigateTo(String fxmlFile, ActionEvent event) throws IOException 
    {
        Parent root = FXMLLoader.load(App.class.getResource(fxmlFile));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}