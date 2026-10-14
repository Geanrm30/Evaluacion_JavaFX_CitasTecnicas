package uam.edu.ni.evaluacion_javafx_citastecnicas.dao;

import uam.edu.ni.evaluacion_javafx_citastecnicas.model.CitaTecnica;
import uam.edu.ni.evaluacion_javafx_citastecnicas.model.Cliente;
import uam.edu.ni.evaluacion_javafx_citastecnicas.util.ConexionDB;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CitaTecnicaDao implements CRUD<CitaTecnica> {

    @Override
    public void guardar(CitaTecnica cita) throws SQLException {
        String sql = "INSERT INTO cita_tecnica (cliente_id, fecha, hora, tipo_servicio, descripcion, estado) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, cita.getCliente().getId());
            ps.setDate(2, Date.valueOf(cita.getFecha()));
            ps.setTime(3, Time.valueOf(cita.getHora()));
            ps.setString(4, cita.getTipoServicio());
            ps.setString(5, cita.getDescripcion());
            ps.setString(6, cita.getEstado());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    cita.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void actualizar(CitaTecnica cita) throws SQLException {
        String sql = "UPDATE cita_tecnica SET cliente_id = ?, fecha = ?, hora = ?, "
                   + "tipo_servicio = ?, descripcion = ?, estado = ? WHERE id = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, cita.getCliente().getId());
            ps.setDate(2, Date.valueOf(cita.getFecha()));
            ps.setTime(3, Time.valueOf(cita.getHora()));
            ps.setString(4, cita.getTipoServicio());
            ps.setString(5, cita.getDescripcion());
            ps.setString(6, cita.getEstado());
            ps.setInt(7, cita.getId());

            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM cita_tecnica WHERE id = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public List<CitaTecnica> listar() throws SQLException {
        List<CitaTecnica> lista = new ArrayList<>();
        String sql = "SELECT ct.id, ct.cliente_id, ct.fecha, ct.hora, ct.tipo_servicio, ct.descripcion, ct.estado, "
                   + "       c.nombres, c.apellidos, c.telefono, c.correo, c.activo "
                   + "FROM cita_tecnica ct "
                   + "LEFT JOIN cliente c ON ct.cliente_id = c.id "
                   + "ORDER BY ct.fecha DESC, ct.hora DESC";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearCita(rs));
            }
        }
        return lista;
    }

    public List<CitaTecnica> filtrar(String estado, LocalDate fecha) throws SQLException {
        List<CitaTecnica> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT ct.id, ct.cliente_id, ct.fecha, ct.hora, ct.tipo_servicio, ct.descripcion, ct.estado, "
              + "       c.nombres, c.apellidos, c.telefono, c.correo, c.activo "
              + "FROM cita_tecnica ct "
              + "LEFT JOIN cliente c ON ct.cliente_id = c.id "
              + "WHERE 1=1 "
        );

        boolean tieneEstado = estado != null && !estado.isBlank() && !estado.equalsIgnoreCase("Todos");
        boolean tieneFecha = fecha != null;

        if (tieneEstado) {
            sql.append("AND ct.estado = ? ");
        }
        if (tieneFecha) {
            sql.append("AND ct.fecha = ? ");
        }
        sql.append("ORDER BY ct.fecha DESC, ct.hora DESC");

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (tieneEstado) {
                ps.setString(paramIndex++, estado.trim());
            }
            if (tieneFecha) {
                ps.setDate(paramIndex++, Date.valueOf(fecha));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearCita(rs));
                }
            }
        }
        return lista;
    }

    public List<Cliente> listarClientesActivos() throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT id, nombres, apellidos, telefono, correo, activo "
                   + "FROM cliente WHERE activo = TRUE ORDER BY nombres, apellidos";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Cliente cliente = new Cliente(
                        rs.getInt("id"),
                        rs.getString("nombres"),
                        rs.getString("apellidos"),
                        rs.getString("telefono"),
                        rs.getString("correo"),
                        rs.getBoolean("activo")
                );
                clientes.add(cliente);
            }
        }
        return clientes;
    }

    private CitaTecnica mapearCita(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente(
                rs.getInt("cliente_id"),
                rs.getString("nombres"),
                rs.getString("apellidos"),
                rs.getString("telefono"),
                rs.getString("correo"),
                rs.getBoolean("activo")
        );

        return new CitaTecnica(
                rs.getInt("id"),
                cliente,
                rs.getDate("fecha").toLocalDate(),
                rs.getTime("hora").toLocalTime(),
                rs.getString("tipo_servicio"),
                rs.getString("descripcion"),
                rs.getString("estado")
        );
    }
}
