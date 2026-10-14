
package uam.edu.ni.evaluacion_javafx_citastecnicas.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import java.util.List;

public final class AlertUtil {

    private AlertUtil() {
    }

    public static void mostrarExito(String mensaje) {
        mostrar(
                Alert.AlertType.INFORMATION,
                "Operación exitosa",
                mensaje
        );
    }

    public static void mostrarError(String titulo, String mensaje) {
        mostrar(
                Alert.AlertType.ERROR,
                titulo,
                mensaje
        );
    }

    public static void mostrarAdvertencia(String mensaje) {
        mostrar(
                Alert.AlertType.WARNING,
                "Advertencia",
                mensaje
        );
    }

    public static void mostrarValidaciones(List<String> errores) {
        if (errores == null || errores.isEmpty()) {
            return;
        }

        String mensaje = "• " + String.join("\n• ", errores);

        mostrar(
                Alert.AlertType.WARNING,
                "Datos inválidos",
                mensaje
        );
    }

    public static boolean confirmarEliminacion(String registro) {
        ButtonType eliminar = new ButtonType(
                "Eliminar",
                ButtonBar.ButtonData.OK_DONE
        );

        ButtonType cancelar = new ButtonType(
                "Cancelar",
                ButtonBar.ButtonData.CANCEL_CLOSE
        );

        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);

        alerta.setTitle("Confirmar eliminación");
        alerta.setHeaderText("¿Deseás eliminar este registro?");
        alerta.setContentText(
                "Registro: " + registro
                        + "\n\nEsta acción no se puede deshacer."
        );

        alerta.getButtonTypes().setAll(eliminar, cancelar);

        return alerta.showAndWait()
                .filter(eliminar::equals)
                .isPresent();
    }

    private static void mostrar(Alert.AlertType tipo,
                                String titulo,
                                String mensaje) {
        Alert alerta = new Alert(tipo);

        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);

        alerta.showAndWait();
    }
}
