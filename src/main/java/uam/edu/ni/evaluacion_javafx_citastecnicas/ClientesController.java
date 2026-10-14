package uam.edu.ni.evaluacion_javafx_citastecnicas;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import uam.edu.ni.evaluacion_javafx_citastecnicas.dao.ClienteDAO;
import uam.edu.ni.evaluacion_javafx_citastecnicas.model.Cliente;

import java.sql.SQLException;

public class ClientesController {
    @FXML
    private TextField nombresField;
    @FXML
    private TextField apellidosField;
    @FXML
    private TextField telefonoField;
    @FXML
    private TextField correoField;
    @FXML
    private TextField busquedaField;
    @FXML
    private CheckBox activoCheckBox;
    @FXML
    private TableView<Cliente> clientesTable;
    @FXML
    private TableColumn<Cliente, String> idColumn;
    @FXML
    private TableColumn<Cliente, String> nombresColumn;
    @FXML
    private TableColumn<Cliente, String> apellidosColumn;
    @FXML
    private TableColumn<Cliente, String> telefonoColumn;
    @FXML
    private TableColumn<Cliente, String> correoColumn;
    @FXML
    private TableColumn<Cliente, String> activoColumn;

    private final ClienteDAO clienteDAO = new ClienteDAO();

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(celda ->
                new ReadOnlyStringWrapper(Integer.toString(celda.getValue().getId())));
        nombresColumn.setCellValueFactory(celda ->
                new ReadOnlyStringWrapper(celda.getValue().getNombres()));
        apellidosColumn.setCellValueFactory(celda ->
                new ReadOnlyStringWrapper(celda.getValue().getApellidos()));
        telefonoColumn.setCellValueFactory(celda ->
                new ReadOnlyStringWrapper(celda.getValue().getTelefono()));
        correoColumn.setCellValueFactory(celda ->
                new ReadOnlyStringWrapper(celda.getValue().getCorreo()));
        activoColumn.setCellValueFactory(celda ->
                new ReadOnlyStringWrapper(celda.getValue().isActivo() ? "Sí" : "No"));
        clientesTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, anterior, seleccionado) -> mostrarSeleccion(seleccionado));
        activoCheckBox.setSelected(true);
        cargarClientes();
    }

    @FXML
    private void guardarCliente() {
        Cliente cliente = leerFormulario();
        if (cliente == null) {
            return;
        }
        ejecutarOperacion(() -> clienteDAO.guardar(cliente), "Cliente guardado.");
    }

    @FXML
    private void actualizarCliente() {
        Cliente seleccionado = clientesTable.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Selecciona un cliente de la tabla para actualizarlo.");
            return;
        }
        Cliente datos = leerFormulario();
        if (datos == null) {
            return;
        }
        datos.setId(seleccionado.getId());
        ejecutarOperacion(() -> clienteDAO.actualizar(datos), "Cliente actualizado.");
    }

    @FXML
    private void eliminarCliente() {
        Cliente seleccionado = clientesTable.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Selecciona un cliente de la tabla para eliminarlo.");
            return;
        }
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Eliminar al cliente seleccionado?");
        confirmacion.setContentText(seleccionado.getNombres() + " " + seleccionado.getApellidos()
                + "\nEsta acción no se puede deshacer.");
        confirmacion.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        if (confirmacion.showAndWait().orElse(ButtonType.NO) != ButtonType.YES) {
            return;
        }
        ejecutarOperacion(() -> clienteDAO.eliminar(seleccionado), "Cliente eliminado.");
    }

    @FXML
    private void buscarClientes() {
        String termino = busquedaField.getText().trim();
        try {
            clientesTable.setItems(FXCollections.observableArrayList(
                    termino.isEmpty() ? clienteDAO.listar() : clienteDAO.buscar(termino)));
        } catch (SQLException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void mostrarTodos() {
        busquedaField.clear();
        cargarClientes();
    }

    @FXML
    private void limpiarFormulario() {
        nombresField.clear();
        apellidosField.clear();
        telefonoField.clear();
        correoField.clear();
        activoCheckBox.setSelected(true);
        clientesTable.getSelectionModel().clearSelection();
    }

    private Cliente leerFormulario() {
        String nombres = nombresField.getText().trim();
        String apellidos = apellidosField.getText().trim();
        String telefono = telefonoField.getText().trim();
        String correo = correoField.getText().trim();
        if (nombres.isEmpty() || apellidos.isEmpty() || telefono.isEmpty() || correo.isEmpty()) {
            mostrarError("Completa nombres, apellidos, teléfono y correo.");
            return null;
        }
        if (!correo.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            mostrarError("Ingresa un correo válido.");
            return null;
        }
        return new Cliente(0, nombres, apellidos, telefono, correo, activoCheckBox.isSelected());
    }

    private void mostrarSeleccion(Cliente cliente) {
        if (cliente == null) {
            return;
        }
        nombresField.setText(cliente.getNombres());
        apellidosField.setText(cliente.getApellidos());
        telefonoField.setText(cliente.getTelefono());
        correoField.setText(cliente.getCorreo());
        activoCheckBox.setSelected(cliente.isActivo());
    }

    private void cargarClientes() {
        try {
            clientesTable.setItems(FXCollections.observableArrayList(clienteDAO.listar()));
        } catch (SQLException e) {
            mostrarError(e.getMessage());
        }
    }

    private void ejecutarOperacion(OperacionBD operacion, String mensajeExito) {
        try {
            operacion.ejecutar();
            limpiarFormulario();
            buscarClientes();
            mostrarMensaje(mensajeExito);
        } catch (SQLException e) {
            mostrarError(e.getMessage());
        }
    }

    private void mostrarMensaje(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Clientes");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error de clientes");
        alerta.setHeaderText("No se pudo completar la operación");
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    @FunctionalInterface
    private interface OperacionBD {
        void ejecutar() throws SQLException;
    }
}
