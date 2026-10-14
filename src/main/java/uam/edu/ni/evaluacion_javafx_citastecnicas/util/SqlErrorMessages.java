package uam.edu.ni.evaluacion_javafx_citastecnicas.util;

import java.sql.SQLException;

public final class SqlErrorMessages {

    private SqlErrorMessages() {
    }

    public static String traducir(SQLException error) {
        if (error == null) {
            return "Ocurrió un error desconocido en la base de datos.";
        }

        String estado = error.getSQLState();

        if (estado == null) {
            return "Ocurrió un error al acceder a la base de datos. Verifique la conexión y los datos.";
        }

        return switch (estado) {
            case "23505" ->
                    "Ya existe un registro con un valor único repetido (por ejemplo, el correo del cliente).";

            case "23503" ->
                    "No se puede completar la operación: el registro tiene datos relacionados o el cliente seleccionado no existe.";

            case "23502" ->
                    "Falta completar un campo obligatorio en la base de datos.";

            case "23514" ->
                    "Uno de los valores no cumple las reglas de la base de datos (por ejemplo, estado no permitido).";

            case "22001" ->
                    "Uno de los campos supera la cantidad máxima de caracteres permitidos.";

            case "22P02" ->
                    "Alguno de los datos ingresados tiene un formato incorrecto.";

            case "08001", "08003", "08006" ->
                    "No fue posible establecer o mantener la conexión con PostgreSQL.";

            case "28P01" ->
                    "Usuario o contraseña de PostgreSQL incorrectos.";

            case "3D000" ->
                    "La base de datos configurada no existe.";

            default ->
                    "Error de base de datos PostgreSQL (código: " + estado + "): " + error.getMessage();
        };
    }
}
