package uam.edu.ni.evaluacion_javafx_citastecnicas.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import uam.edu.ni.evaluacion_javafx_citastecnicas.MenuPrincipalApplication;
import uam.edu.ni.evaluacion_javafx_citastecnicas.util.AlertUtil;

import java.io.IOException;
import java.net.URL;

public class MenuPrincipalController {

    @FXML
    private BorderPane raiz;

    @FXML
    private void initialize() {
        mostrarClientes();
    }

    @FXML
    private void mostrarClientes() {
        cargarVista("clientes-view.fxml", "Clientes");
    }

    @FXML
    private void mostrarCitas() {
        cargarVista("citas-view.fxml", "Citas técnicas");
    }

    private void cargarVista(String archivo, String nombre) {
        // Se busca desde MenuPrincipalApplication porque los FXML están en el paquete raíz.
        URL recurso = MenuPrincipalApplication.class.getResource(archivo);
        if (recurso == null) {
            AlertUtil.mostrarAdvertencia("El módulo de " + nombre + " todavía no está disponible.");
            return;
        }
        try {
            Node vista = new FXMLLoader(recurso).load();
            raiz.setCenter(vista);
        } catch (IOException e) {
            AlertUtil.mostrarError("Error de navegación",
                    "No se pudo abrir el módulo de " + nombre + ".");
        }
    }
}