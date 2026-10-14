module uam.edu.ni.evaluacion_javafx_citastecnicas {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;
    requires static lombok;


    opens uam.edu.ni.evaluacion_javafx_citastecnicas to javafx.fxml;
    exports uam.edu.ni.evaluacion_javafx_citastecnicas;
    exports uam.edu.ni.evaluacion_javafx_citastecnicas.util;
    exports uam.edu.ni.evaluacion_javafx_citastecnicas.controller;
    opens uam.edu.ni.evaluacion_javafx_citastecnicas.controller to javafx.fxml;

}