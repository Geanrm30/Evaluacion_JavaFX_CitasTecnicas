module uam.edu.ni.evaluacion_javafx_citastecnicas {
    requires javafx.controls;
    requires javafx.fxml;


    opens uam.edu.ni.evaluacion_javafx_citastecnicas to javafx.fxml;
    exports uam.edu.ni.evaluacion_javafx_citastecnicas;
}