package application;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class DescrizioneEsercizioController 
{

    @FXML private Label titoloLabel;

    @FXML private Label descrizioneArea;

    private Runnable azioneInizia;

    @FXML
    private void chiudiPopup() 
    {
        Stage stage = (Stage) titoloLabel.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void avviaEsercizio() 
    {
        if (azioneInizia != null) azioneInizia.run();
        chiudiPopup();
    }

    public void setTitolo(String titolo) 
    {
        titoloLabel.setText(titolo);
    }

    public void setDescrizione(String descrizione) 
    {
        descrizioneArea.setText(descrizione);
    }

    public void setAzioneInizia(Runnable azione) 
    {
        this.azioneInizia = azione;
    }
}