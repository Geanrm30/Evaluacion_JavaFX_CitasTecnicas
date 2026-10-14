package uam.edu.ni.evaluacion_javafx_citastecnicas.dao;

import java.sql.SQLException;
import java.util.List;

public interface CRUD<T> {
    void guardar(T t) throws SQLException;
    void actualizar(T t) throws SQLException;
    void eliminar(int id) throws SQLException;
    List<T> listar() throws SQLException;
}
