package uam.edu.ni.evaluacion_javafx_citastecnicas.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import uam.edu.ni.evaluacion_javafx_citastecnicas.dao.CitaTecnicaDao;
import uam.edu.ni.evaluacion_javafx_citastecnicas.model.CitaTecnica;
import uam.edu.ni.evaluacion_javafx_citastecnicas.model.Cliente;
import uam.edu.ni.evaluacion_javafx_citastecnicas.util.AlertUtil;
import uam.edu.ni.evaluacion_javafx_citastecnicas.util.SqlErrorMessages;
import uam.edu.ni.evaluacion_javafx_citastecnicas.validation.CitaValidator;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class CitaController {

    @FXML private ComboBox<Cliente> cmbCliente;
    @FXML private DatePicker dpFecha;
    @FXML private TextField txtHora;
    @FXML private TextField txtTipoServicio;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private TextField txtDescripcion;

    @FXML private Button btnGuardar;
    @FXML private Button btnActualizar;
    @FXML private Button btnEliminar;
    @FXML private Button btnLimpiar;

    @FXML private ComboBox<String> cmbFiltroEstado;
    @FXML private DatePicker dpFiltroFecha;
    @FXML private Button btnFiltrar;
    @FXML private Button btnLimpiarFiltros;

    @FXML private TableView<CitaTecnica> tblCitas;
    @FXML private TableColumn<CitaTecnica, Integer> colId;
    @FXML private TableColumn<CitaTecnica, String> colCliente;
    @FXML private TableColumn<CitaTecnica, LocalDate> colFecha;
    @FXML private TableColumn<CitaTecnica, LocalTime> colHora;
    @FXML private TableColumn<CitaTecnica, String> colServicio;
    @FXML private TableColumn<CitaTecnica, String> colEstado;

    @FXML private Label lblTotalRegistros;

    private final CitaTecnicaDao citaDao = new CitaTecnicaDao();
    private final ObservableList<CitaTecnica> listaCitas = FXCollections.observableArrayList();
    private final ObservableList<Cliente> listaClientes = FXCollections.observableArrayList();

    private CitaTecnica citaSeleccionada;

    @FXML
    public void initialize() {
        configurarTabla();
        configurarCombos();
        configurarContextMenu();
        configurarListenerSeleccion();

        recargarClientes();
        cargarCitas();
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCliente.setCellValueFactory(new PropertyValueFactory<>("clienteNombreCompleto"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colHora.setCellValueFactory(new PropertyValueFactory<>("hora"));
        colServicio.setCellValueFactory(new PropertyValueFactory<>("tipoServicio"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        tblCitas.setItems(listaCitas);
    }

    private void configurarCombos() {
        cmbEstado.setItems(FXCollections.observableArrayList(CitaValidator.ESTADOS_VALIDOS));

        List<String> opcionesFiltro = new ArrayList<>();
        opcionesFiltro.add("Todos");
        opcionesFiltro.addAll(CitaValidator.ESTADOS_VALIDOS);
        cmbFiltroEstado.setItems(FXCollections.observableArrayList(opcionesFiltro));
        cmbFiltroEstado.setValue("Todos");

        cmbCliente.setItems(listaClientes);
    }

    private void configurarListenerSeleccion() {
        tblCitas.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                cargarCitaEnFormulario(newVal);
            }
        });
    }

    private void configurarContextMenu() {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem itemEditar = new MenuItem("Cargar en formulario para editar");
        itemEditar.setOnAction(e -> {
            CitaTecnica seleccion = tblCitas.getSelectionModel().getSelectedItem();
            if (seleccion != null) {
                cargarCitaEnFormulario(seleccion);
            }
        });

        MenuItem itemEliminar = new MenuItem("Eliminar cita");
        itemEliminar.setOnAction(e -> eliminarCita());

        contextMenu.getItems().addAll(itemEditar, itemEliminar);
        tblCitas.setContextMenu(contextMenu);
    }

    public void recargarClientes() {
        try {
            listaClientes.setAll(citaDao.listarClientesActivos());
        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al cargar clientes", SqlErrorMessages.traducir(e));
        }
    }

    private void cargarCitas() {
        try {
            listaCitas.setAll(citaDao.listar());
            actualizarContador();
        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al cargar citas", SqlErrorMessages.traducir(e));
        }
    }

    private void cargarCitaEnFormulario(CitaTecnica cita) {
        this.citaSeleccionada = cita;

        if (cita.getCliente() != null) {
            for (Cliente cl : listaClientes) {
                if (cl.getId().equals(cita.getCliente().getId())) {
                    cmbCliente.setValue(cl);
                    break;
                }
            }
        }

        dpFecha.setValue(cita.getFecha());
        txtHora.setText(cita.getHora() != null ? cita.getHora().toString() : "");
        txtTipoServicio.setText(cita.getTipoServicio());
        cmbEstado.setValue(cita.getEstado());
        txtDescripcion.setText(cita.getDescripcion());

        btnGuardar.setDisable(true);
        btnActualizar.setDisable(false);
        btnEliminar.setDisable(false);
    }

    @FXML
    private void guardarCita() {
        try {
            CitaTecnica nuevaCita = obtenerCitaDesdeFormulario();
            if (nuevaCita == null) {
                return;
            }

            citaDao.guardar(nuevaCita);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Operación exitosa", "La cita técnica ha sido registrada exitosamente.");
            cargarCitas();
            limpiarFormulario();

        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al guardar cita", SqlErrorMessages.traducir(e));
        }
    }

    @FXML
    private void actualizarCita() {
        if (citaSeleccionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Debe seleccionar una cita para actualizar.");
            return;
        }

        try {
            CitaTecnica datosFormulario = obtenerCitaDesdeFormulario();
            if (datosFormulario == null) {
                return;
            }

            citaSeleccionada.setCliente(datosFormulario.getCliente());
            citaSeleccionada.setFecha(datosFormulario.getFecha());
            citaSeleccionada.setHora(datosFormulario.getHora());
            citaSeleccionada.setTipoServicio(datosFormulario.getTipoServicio());
            citaSeleccionada.setDescripcion(datosFormulario.getDescripcion());
            citaSeleccionada.setEstado(datosFormulario.getEstado());

            citaDao.actualizar(citaSeleccionada);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Operación exitosa", "La cita técnica ha sido actualizada exitosamente.");
            cargarCitas();
            limpiarFormulario();

        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al actualizar cita", SqlErrorMessages.traducir(e));
        }
    }

    @FXML
    private void eliminarCita() {
        CitaTecnica citaAEliminar = citaSeleccionada != null ? citaSeleccionada : tblCitas.getSelectionModel().getSelectedItem();

        if (citaAEliminar == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Debe seleccionar una cita de la tabla para eliminar.");
            return;
        }

        String detalle = "Cita #" + citaAEliminar.getId() + " - " + citaAEliminar.getClienteNombreCompleto()
                + " (" + citaAEliminar.getFecha() + " " + citaAEliminar.getHora() + ")";

        if (!AlertUtil.confirmarEliminacion(detalle)) {
            return;
        }

        try {
            citaDao.eliminar(citaAEliminar.getId());
            mostrarAlerta(Alert.AlertType.INFORMATION, "Operación exitosa", "La cita ha sido eliminada exitosamente.");
            cargarCitas();
            limpiarFormulario();

        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al eliminar cita", SqlErrorMessages.traducir(e));
        }
    }

    @FXML
    private void limpiarFormulario() {
        citaSeleccionada = null;
        cmbCliente.setValue(null);
        dpFecha.setValue(null);
        txtHora.clear();
        txtTipoServicio.clear();
        cmbEstado.setValue(null);
        txtDescripcion.clear();

        tblCitas.getSelectionModel().clearSelection();

        btnGuardar.setDisable(false);
        btnActualizar.setDisable(true);
        btnEliminar.setDisable(true);
    }

    @FXML
    private void aplicarFiltros() {
        String estado = cmbFiltroEstado.getValue();
        LocalDate fecha = dpFiltroFecha.getValue();

        try {
            listaCitas.setAll(citaDao.filtrar(estado, fecha));
            actualizarContador();
        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al aplicar filtros", SqlErrorMessages.traducir(e));
        }
    }

    @FXML
    private void limpiarFiltros() {
        cmbFiltroEstado.setValue("Todos");
        dpFiltroFecha.setValue(null);
        cargarCitas();
    }

    private CitaTecnica obtenerCitaDesdeFormulario() {
        Cliente cliente = cmbCliente.getValue();
        Integer clienteId = cliente != null ? cliente.getId() : null;
        LocalDate fecha = dpFecha.getValue();

        LocalTime hora = null;
        String textoHora = txtHora.getText() != null ? txtHora.getText().trim() : "";
        if (!textoHora.isEmpty()) {
            try {
                if (textoHora.length() == 4 && textoHora.charAt(1) == ':') {
                    textoHora = "0" + textoHora;
                }
                if (textoHora.length() == 5) {
                    hora = LocalTime.parse(textoHora);
                } else if (textoHora.length() == 8) {
                    hora = LocalTime.parse(textoHora);
                } else {
                    mostrarAlerta(Alert.AlertType.WARNING, "Formato inválido", "El formato de la hora debe ser HH:mm (ej. 09:30 o 14:00).");
                    txtHora.requestFocus();
                    return null;
                }
            } catch (DateTimeParseException ex) {
                mostrarAlerta(Alert.AlertType.WARNING, "Hora inválida", "Ingrese una hora válida en formato HH:mm (ej. 09:30 o 14:00).");
                txtHora.requestFocus();
                return null;
            }
        }

        String tipoServicio = txtTipoServicio.getText() != null ? txtTipoServicio.getText().trim() : "";
        String estado = cmbEstado.getValue();
        String descripcion = txtDescripcion.getText() != null ? txtDescripcion.getText().trim() : "";

        List<String> errores = CitaValidator.validar(clienteId, fecha, hora, tipoServicio, descripcion, estado);
        if (!errores.isEmpty()) {
            String mensaje = "• " + String.join("\n• ", errores);
            mostrarAlerta(Alert.AlertType.WARNING, "Datos inválidos", mensaje);
            return null;
        }

        return new CitaTecnica(cliente, fecha, hora, tipoServicio, descripcion, estado);
    }

    public void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setHeaderText(null);
        alerta.setTitle(titulo);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void actualizarContador() {
        lblTotalRegistros.setText("Total de citas: " + listaCitas.size());
    }
}
