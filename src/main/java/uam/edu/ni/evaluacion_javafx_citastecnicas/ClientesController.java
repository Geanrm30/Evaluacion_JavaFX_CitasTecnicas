
package uam.edu.ni.evaluacion_javafx_citastecnicas;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import uam.edu.ni.evaluacion_javafx_citastecnicas.dao.ClienteDAO;
import uam.edu.ni.evaluacion_javafx_citastecnicas.model.Cliente;
import uam.edu.ni.evaluacion_javafx_citastecnicas.util.AlertUtil;
import uam.edu.ni.evaluacion_javafx_citastecnicas.util.SqlErrorMessages;
import uam.edu.ni.evaluacion_javafx_citastecnicas.validation.ClienteValidator;

import java.sql.SQLException;
import java.util.List;

public class ClientesController {

    @FXML private TextField nombresField;
    @FXML private TextField apellidosField;
    @FXML private TextField telefonoField;
    @FXML private TextField correoField;
    @FXML private TextField busquedaField;
    @FXML private CheckBox activoCheckBox;

    @FXML private TableView<Cliente> clientesTable;
    @FXML private TableColumn<Cliente, String> idColumn;
    @FXML private TableColumn<Cliente, String> nombresColumn;
    @FXML private TableColumn<Cliente, String> apellidosColumn;
    @FXML private TableColumn<Cliente, String> telefonoColumn;
    @FXML private TableColumn<Cliente, String> correoColumn;
    @FXML private TableColumn<Cliente, String> activoColumn;

    private final ClienteDAO clienteDAO = new ClienteDAO();

    @FXML
    private void initialize() {

        idColumn.setCellValueFactory(celda ->
                new ReadOnlyStringWrapper(
                        Integer.toString(celda.getValue().getId())
                ));

        nombresColumn.setCellValueFactory(celda ->
                new ReadOnlyStringWrapper(
                        celda.getValue().getNombres()
                ));

        apellidosColumn.setCellValueFactory(celda ->
                new ReadOnlyStringWrapper(
                        celda.getValue().getApellidos()
                ));

        telefonoColumn.setCellValueFactory(celda ->
                new ReadOnlyStringWrapper(
                        celda.getValue().getTelefono()
                ));

        correoColumn.setCellValueFactory(celda ->
                new ReadOnlyStringWrapper(
                        celda.getValue().getCorreo()
                ));

        activoColumn.setCellValueFactory(celda ->
                new ReadOnlyStringWrapper(
                        celda.getValue().isActivo() ? "Sí" : "No"
                ));

        clientesTable.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, anterior, seleccionado) ->
                        mostrarSeleccion(seleccionado));

        activoCheckBox.setSelected(true);
        cargarClientes();
    }

    @FXML
    private void guardarCliente() {

        Cliente cliente = leerFormulario();

        if (cliente == null) {
            return;
        }

        ejecutarOperacion(
                () -> clienteDAO.guardar(cliente),
                "Cliente guardado correctamente."
        );
    }

    @FXML
    private void actualizarCliente() {

        Cliente seleccionado =
                clientesTable.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            AlertUtil.mostrarAdvertencia(
                    "Seleccioná un cliente de la tabla para actualizarlo."
            );
            return;
        }

        Cliente datos = leerFormulario();

        if (datos == null) {
            return;
        }

        datos.setId(seleccionado.getId());

        ejecutarOperacion(
                () -> clienteDAO.actualizar(datos),
                "Cliente actualizado correctamente."
        );
    }

    @FXML
    private void eliminarCliente() {

        Cliente seleccionado =
                clientesTable.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            AlertUtil.mostrarAdvertencia(
                    "Seleccioná un cliente para eliminarlo."
            );
            return;
        }

        String nombreCompleto =
                seleccionado.getNombres() + " " +
                        seleccionado.getApellidos();

        boolean confirmado =
                AlertUtil.confirmarEliminacion(nombreCompleto);

        if (!confirmado) {
            return;
        }

        ejecutarOperacion(
                () -> clienteDAO.eliminar(seleccionado),
                "Cliente eliminado correctamente."
        );
    }

    @FXML
    private void buscarClientes() {

        String termino = busquedaField.getText().trim();

        try {
            List<Cliente> resultados = termino.isEmpty()
                    ? clienteDAO.listar()
                    : clienteDAO.buscar(termino);

            clientesTable.setItems(
                    FXCollections.observableArrayList(resultados)
            );

        } catch (SQLException e) {
            mostrarErrorSQL(e);
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
        boolean activo = activoCheckBox.isSelected();

        // Validación centralizada mediante nuestra clase
        List<String> errores = ClienteValidator.validar(
                nombres,
                apellidos,
                telefono,
                correo,
                activo
        );

        if (!errores.isEmpty()) {
            AlertUtil.mostrarValidaciones(errores);
            return null;
        }

        return new Cliente(
                0,
                nombres,
                apellidos,
                telefono,
                correo,
                activo
        );
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
            clientesTable.setItems(
                    FXCollections.observableArrayList(
                            clienteDAO.listar()
                    )
            );

        } catch (SQLException e) {
            mostrarErrorSQL(e);
        }
    }

    private void ejecutarOperacion(
            OperacionBD operacion,
            String mensajeExito
    ) {

        try {
            operacion.ejecutar();

        } catch (SQLException e) {
            mostrarErrorSQL(e);
            return;
        }

        limpiarFormulario();

        // La operación en la base de datos tuvo éxito.
        AlertUtil.mostrarExito(mensajeExito);

        // Actualizar la tabla sin reiniciar el programa.
        buscarClientes();
    }

    private void mostrarErrorSQL(SQLException error) {

        AlertUtil.mostrarError(
                "Error de clientes",
                SqlErrorMessages.traducir(error)
        );
    }

    @FunctionalInterface
    private interface OperacionBD {
        void ejecutar() throws SQLException;
    }
}
