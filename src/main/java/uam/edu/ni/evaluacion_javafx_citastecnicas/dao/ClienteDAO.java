package uam.edu.ni.evaluacion_javafx_citastecnicas.dao;

import uam.edu.ni.evaluacion_javafx_citastecnicas.Interfaces.CRUD;
import uam.edu.ni.evaluacion_javafx_citastecnicas.model.Cliente;
import uam.edu.ni.evaluacion_javafx_citastecnicas.util.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO implements CRUD<Cliente> {
    private static final String SELECT_CLIENTES =
            "SELECT id, nombres, apellidos, telefono, correo, activo FROM cliente ";

    @Override
    public void guardar(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO cliente (nombres, apellidos, telefono, correo, activo) "
                + "VALUES (?, ?, ?, ?, ?) RETURNING id";
        try (Connection conexion = ConexionDB.getConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            asignarDatos(sentencia, cliente);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (!resultado.next()) {
                    throw new SQLException("No se recibió el ID del cliente guardado.");
                }
                cliente.setId(resultado.getInt("id"));
            }
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                throw new SQLException("Ya existe un cliente registrado con ese correo electrónico.", e);
            }
            throw e;
        }
    }

    @Override
    public List<Cliente> listar() throws SQLException {
        try (Connection conexion = ConexionDB.getConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SELECT_CLIENTES + "ORDER BY id");
             ResultSet resultado = sentencia.executeQuery()) {
            return mapearClientes(resultado);
        }
    }

    @Override
    public void eliminar(Cliente cliente) throws SQLException {
        String sql = "DELETE FROM cliente WHERE id = ?";
        try (Connection conexion = ConexionDB.getConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, cliente.getId());
            verificarCambio(sentencia.executeUpdate(), cliente.getId(), "eliminar");
        } catch (SQLException e) {
            if ("23503".equals(e.getSQLState())) {
                throw new SQLException(
                        "No se puede eliminar este cliente porque tiene citas técnicas asociadas.", e);
            }
            throw e;
        }
    }

    @Override
    public void actualizar(Cliente cliente) throws SQLException {
        String sql = "UPDATE cliente SET nombres = ?, apellidos = ?, telefono = ?, correo = ?, activo = ? "
                + "WHERE id = ?";
        try (Connection conexion = ConexionDB.getConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            asignarDatos(sentencia, cliente);
            sentencia.setInt(6, cliente.getId());
            verificarCambio(sentencia.executeUpdate(), cliente.getId(), "actualizar");
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                throw new SQLException("Ya existe otro cliente registrado con ese correo electrónico.", e);
            }
            throw e;
        }
    }

    @Override
    public List<Cliente> buscar(String termino) throws SQLException {
        String sql = SELECT_CLIENTES
                + "WHERE CAST(id AS TEXT) ILIKE ? OR nombres ILIKE ? OR apellidos ILIKE ? "
                + "OR telefono ILIKE ? OR correo ILIKE ? ORDER BY id";
        String patron = "%" + termino.trim() + "%";
        try (Connection conexion = ConexionDB.getConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            for (int i = 1; i <= 5; i++) {
                sentencia.setString(i, patron);
            }
            try (ResultSet resultado = sentencia.executeQuery()) {
                return mapearClientes(resultado);
            }
        }
    }

    private void asignarDatos(PreparedStatement sentencia, Cliente cliente) throws SQLException {
        sentencia.setString(1, cliente.getNombres());
        sentencia.setString(2, cliente.getApellidos());
        sentencia.setString(3, cliente.getTelefono());
        sentencia.setString(4, cliente.getCorreo());
        sentencia.setBoolean(5, cliente.isActivo());
    }

    private List<Cliente> mapearClientes(ResultSet resultado) throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        while (resultado.next()) {
            clientes.add(new Cliente(
                    resultado.getInt("id"),
                    resultado.getString("nombres"),
                    resultado.getString("apellidos"),
                    resultado.getString("telefono"),
                    resultado.getString("correo"),
                    resultado.getBoolean("activo")
            ));
        }
        return clientes;
    }

    private void verificarCambio(int filas, int id, String accion) throws SQLException {
        if (filas == 0) {
            throw new SQLException("No se pudo " + accion + " el cliente " + id + ": no existe.");
        }
    }
}
