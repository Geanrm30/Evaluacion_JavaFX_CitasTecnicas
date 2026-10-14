package uam.edu.ni.evaluacion_javafx_citastecnicas.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class ConexionDB {

    private static final String ARCHIVO_CONFIG = "db.properties";

    private ConexionDB() {
    }

    private static Properties cargarConfig() throws SQLException {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(ARCHIVO_CONFIG)) {
            props.load(fis);
        } catch (IOException e) {
            throw new SQLException("No se pudo leer " + ARCHIVO_CONFIG
                    + ". Copia db.properties.example a db.properties y configura tus credenciales.", e);
        }
        return props;
    }

    public static Connection getConexion() throws SQLException {
        Properties props = cargarConfig();
        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");

        if (url == null || user == null || password == null) {
            throw new SQLException("Faltan datos en db.properties (db.url, db.user, db.password).");
        }
        return DriverManager.getConnection(url, user, password);
    }

    // Usa Statement porque el SQL es fijo y no recibe datos del usuario
    public static boolean probarConexion() {
        try (Connection con = getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT 1")) {
            return rs.next() && rs.getInt(1) == 1;
        } catch (SQLException e) {
            System.err.println("Error de conexion: " + e.getMessage());
            return false;
        }
    }
}