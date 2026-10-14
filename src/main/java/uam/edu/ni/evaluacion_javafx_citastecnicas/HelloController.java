package uam.edu.ni.evaluacion_javafx_citastecnicas;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    protected void abrirModuloCitas() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("citas-view.fxml"));
            javafx.scene.Scene scene = new javafx.scene.Scene(loader.load(), 980, 650);
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("TecnoAsistencia Nicaragua - Módulo de Citas Técnicas");
            stage.setScene(scene);
            stage.show();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}
