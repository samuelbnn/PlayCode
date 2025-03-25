package app;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

public class TrovaErroreController {

    @FXML
    private RadioButton opt1;

    @FXML
    private RadioButton opt2;

    @FXML
    private RadioButton opt3;

    @FXML
    private Label resultLabel;

    private final ToggleGroup toggleGroup = new ToggleGroup();

    @FXML
    public void initialize() {
        opt1.setToggleGroup(toggleGroup);
        opt2.setToggleGroup(toggleGroup);
        opt3.setToggleGroup(toggleGroup);
    }

    @FXML
    public void verificaRisposta() {
        RadioButton selected = (RadioButton) toggleGroup.getSelectedToggle();
        if (selected == null) {
            resultLabel.setText("Seleziona una risposta.");
            resultLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        if (selected == opt1) {
            resultLabel.setText("✅ Corretto! Manca una parentesi graffa.");
            resultLabel.setStyle("-fx-text-fill: green;");
        } else {
            resultLabel.setText("❌ Risposta errata.");
            resultLabel.setStyle("-fx-text-fill: red;");
        }
    }
}
