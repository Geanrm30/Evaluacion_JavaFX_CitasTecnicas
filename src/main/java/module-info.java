module uam.edu.ni.evaluacion_javafx_citastecnicas {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;
    requires static lombok;


    opens uam.edu.ni.evaluacion_javafx_citastecnicas to javafx.fxml;
    opens uam.edu.ni.evaluacion_javafx_citastecnicas.controller to javafx.fxml;
    opens uam.edu.ni.evaluacion_javafx_citastecnicas.model to javafx.base;

    exports uam.edu.ni.evaluacion_javafx_citastecnicas;
    exports uam.edu.ni.evaluacion_javafx_citastecnicas.util;
    exports uam.edu.ni.evaluacion_javafx_citastecnicas.model;
    exports uam.edu.ni.evaluacion_javafx_citastecnicas.dao;
    exports uam.edu.ni.evaluacion_javafx_citastecnicas.controller;
    exports uam.edu.ni.evaluacion_javafx_citastecnicas.validation;
    exports uam.edu.ni.evaluacion_javafx_citastecnicas.Interfaces;
}