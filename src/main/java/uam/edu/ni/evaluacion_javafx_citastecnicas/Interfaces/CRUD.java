package uam.edu.ni.evaluacion_javafx_citastecnicas.Interfaces;

import java.sql.SQLException;
import java.util.List;

public interface CRUD<T> {
    void guardar(T entidad) throws SQLException;

    List<T> listar() throws SQLException;

    void eliminar(T entidad) throws SQLException;

    void actualizar(T entidad) throws SQLException;

    List<T> buscar(String termino) throws SQLException;
}
