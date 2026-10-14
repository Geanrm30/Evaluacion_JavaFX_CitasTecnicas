package uam.edu.ni.evaluacion_javafx_citastecnicas;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import uam.edu.ni.evaluacion_javafx_citastecnicas.util.AlertUtil;
import uam.edu.ni.evaluacion_javafx_citastecnicas.util.ConexionDB;

import java.io.IOException;

public class MenuPrincipalApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        if (!ConexionDB.probarConexion()) {
            AlertUtil.mostrarError("Error de conexión",
                    "No se pudo conectar con PostgreSQL. Revisa db.properties y que el servicio esté activo.");
            Platform.exit();
            return;
        }

        FXMLLoader cargador = new FXMLLoader(
                MenuPrincipalApplication.class.getResource("menu-principal-view.fxml"));
        Scene escena = new Scene(cargador.load(), 1000, 650);
        stage.setTitle("TecnoAsistencia Nicaragua - Menú principal");
        stage.setScene(escena);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}