package uam.edu.ni.evaluacion_javafx_citastecnicas.util;

import java.sql.SQLException;

public final class SqlErrorMessages {

    private SqlErrorMessages() {
    }

    public static String traducir(SQLException error) {
        if (error == null) {
            return "Ocurrió un error desconocido en la base de datos.";
        }

        // Recuperar el SQLState incluso si una excepción fue envuelta.
        SQLException origen = error;

        for (int i = 0; i < 10 && origen.getSQLState() == null; i++) {
            if (origen.getCause() instanceof SQLException causa) {
                origen = causa;
            } else if (origen.getNextException() != null) {
                origen = origen.getNextException();
            } else {
                break;
            }
        }

        String estado = origen.getSQLState();

        if (estado == null) {
            String mensaje = error.getMessage();

            return mensaje != null && !mensaje.isBlank()
                    ? mensaje
                    : "No se pudo completar la operación en la base de datos.";
        }

        return switch (estado) {

            case "23505" ->
                    "El correo electrónico ya está registrado. Usá uno diferente.";

            case "23503", "23001" -> {
                if (error.getMessage() != null
                        && error.getMessage().contains("citas técnicas asociadas")) {
                    yield error.getMessage();
                }

                yield "La operación incumple una relación entre clientes y citas. "
                        + "Comprobá que el cliente exista y que no tenga citas "
                        + "asociadas antes de eliminarlo.";
            }

            case "23502" ->
                    "Falta completar un campo obligatorio.";

            case "23514" ->
                    "Uno de los datos no cumple las reglas permitidas.";

            case "22001" ->
                    "Uno de los campos supera la longitud máxima permitida.";

            case "22P02" ->
                    "Alguno de los datos tiene un formato incorrecto.";

            case "08001", "08003", "08006" ->
                    "No se pudo establecer o mantener la conexión con PostgreSQL.";

            case "28P01" ->
                    "El usuario o la contraseña de PostgreSQL son incorrectos.";

            case "3D000" ->
                    "La base de datos configurada no existe.";

            default ->
                    "No se pudo completar la operación en PostgreSQL "
                            + "(código: " + estado + ").";
        };
    }
}
