import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.event.ActionEvent;

public class MenuController {

    @FXML
    private Text userNameLabel;

    private String currentUserName;

    public void initialize() {
        // Ottieni il nome dell'utente dalla sessione di login, qui lo settiamo come esempio
        currentUserName = "NomeUtente";  // Puoi settarlo tramite la sessione o passarlo da un'altra schermata
        userNameLabel.setText("Benvenuto, " + currentUserName + "!");
    }

    // Metodi per le finestre cliccabili
    @FXML
    private void openWindow1(ActionEvent event) {
        System.out.println("Finestra 1 cliccata");
        // Aggiungi la logica per aprire la finestra 1
    }

    @FXML
    private void openWindow2(ActionEvent event) {
        System.out.println("Finestra 2 cliccata");
        // Aggiungi la logica per aprire la finestra 2
    }

    @FXML
    private void openWindow3(ActionEvent event) {
        System.out.println("Finestra 3 cliccata");
        // Aggiungi la logica per aprire la finestra 3
    }

    @FXML
    private void openWindow4(ActionEvent event) {
        System.out.println("Finestra 4 cliccata");
        // Aggiungi la logica per aprire la finestra 4
    }
}
