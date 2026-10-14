
package uam.edu.ni.evaluacion_javafx_citastecnicas.validation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public final class CitaValidator {

    public static final List<String> ESTADOS_VALIDOS = List.of(
            "Pendiente",
            "En proceso",
            "Finalizada",
            "Cancelada"
    );

    private CitaValidator() {
    }

    public static List<String> validar(Integer clienteId, LocalDate fecha,
                                       LocalTime hora, String tipoServicio,
                                       String descripcion, String estado) {
        List<String> errores = new ArrayList<>();

        if (clienteId == null || clienteId <= 0) {
            errores.add("Debe seleccionar un cliente válido.");
        }

        if (fecha == null) {
            errores.add("La fecha es obligatoria.");
        }

        if (hora == null) {
            errores.add("La hora es obligatoria.");
        }

        validarTexto(errores, tipoServicio, "El tipo de servicio", 80);
        validarTexto(errores, descripcion, "La descripción", 255);

        if (estado == null || !ESTADOS_VALIDOS.contains(estado)) {
            errores.add(
                    "El estado debe ser Pendiente, En proceso, "
                            + "Finalizada o Cancelada."
            );
        }

        return errores;
    }

    private static void validarTexto(List<String> errores, String valor,
                                     String nombreCampo, int longitudMaxima) {
        if (valor == null || valor.isBlank()) {
            errores.add(nombreCampo + " es obligatorio.");
        } else if (valor.trim().length() > longitudMaxima) {
            errores.add(nombreCampo + " no puede superar los "
                    + longitudMaxima + " caracteres.");
        }
    }
}
