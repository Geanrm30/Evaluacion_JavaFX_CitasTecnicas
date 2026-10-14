package uam.edu.ni.evaluacion_javafx_citastecnicas.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    private Integer id;
    private String nombres;
    private String apellidos;
    private String telefono;
    private String correo;
    private boolean activo;

    public Cliente(String nombres, String apellidos, String telefono, String correo, boolean activo) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.correo = correo;
        this.activo = activo;
    }

    public String getNombreCompleto() {
        String n = nombres != null ? nombres : "";
        String a = apellidos != null ? apellidos : "";
        return (n + " " + a).trim();
    }

    @Override
    public String toString() {
        return getNombreCompleto();
    }
}