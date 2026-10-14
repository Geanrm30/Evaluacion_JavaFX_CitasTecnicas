package uam.edu.ni.evaluacion_javafx_citastecnicas;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                HelloApplication.class.getResource("clientes-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);
        stage.setTitle("Gestión de clientes");
        stage.setScene(scene);
        stage.show();
    }
}
