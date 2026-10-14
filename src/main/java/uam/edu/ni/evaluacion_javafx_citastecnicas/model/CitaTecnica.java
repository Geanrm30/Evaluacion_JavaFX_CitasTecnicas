package uam.edu.ni.evaluacion_javafx_citastecnicas.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter@AllArgsConstructor
@Setter@NoArgsConstructor
public class CitaTecnica {

    private Integer id;
    private Cliente cliente;
    private LocalDate fecha;
    private LocalTime hora;
    private String tipoServicio;
    private String descripcion;
    private String estado;

    public CitaTecnica(Cliente cliente, LocalDate fecha, LocalTime hora, String tipoServicio, String descripcion, String estado) {
        this.cliente = cliente;
        this.fecha = fecha;
        this.hora = hora;
        this.tipoServicio = tipoServicio;
        this.descripcion = descripcion;
        this.estado = estado;
    }

    public String getClienteNombreCompleto() {
        return cliente != null ? cliente.getNombreCompleto() : "";
    }

    public Integer getClienteId() {
        return cliente != null ? cliente.getId() : null;
    }
}
