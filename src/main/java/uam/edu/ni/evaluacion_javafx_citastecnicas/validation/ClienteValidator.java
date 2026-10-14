
package uam.edu.ni.evaluacion_javafx_citastecnicas.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public final class ClienteValidator {

    private static final Pattern CORREO_PATTERN = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$",
            Pattern.CASE_INSENSITIVE
    );

    private ClienteValidator() {
    }

    public static List<String> validar(String nombres, String apellidos,
                                       String telefono, String correo, Boolean activo) {
        List<String> errores = new ArrayList<>();

        validarTexto(errores, nombres, "Los nombres", 80);
        validarTexto(errores, apellidos, "Los apellidos", 80);
        validarTexto(errores, telefono, "El teléfono", 20);

        if (correo == null || correo.isBlank()) {
            errores.add("El correo es obligatorio.");
        } else {
            String correoLimpio = correo.trim();

            if (correoLimpio.length() > 120) {
                errores.add("El correo no puede superar los 120 caracteres.");
            } else if (!CORREO_PATTERN.matcher(correoLimpio).matches()) {
                errores.add("El correo no tiene un formato válido.");
            }
        }

        if (activo == null) {
            errores.add("Debe indicarse si el cliente está activo.");
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
