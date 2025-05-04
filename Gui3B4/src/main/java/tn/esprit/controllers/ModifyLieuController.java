package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.esprit.entities.Lieu;
import tn.esprit.services.ServiceLieu;

import java.io.File;
import java.sql.SQLException;

public class ModifyLieuController {
    @FXML private TextField tfId;
    @FXML private TextField tfNom;
    @FXML private TextField tfAdresse;
    @FXML private TextField tfCapacite;
    @FXML private TextField tfImageUrl;
    @FXML private Button btnBrowse;
    @FXML private Label lblError;

    private MainController mainController;
    private final ServiceLieu serviceLieu = new ServiceLieu();
    private Lieu lieuToModify;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setLieu(Lieu lieu) {
        this.lieuToModify = lieu;

        // Populate fields with existing data
        tfId.setText(String.valueOf(lieu.getId()));
        tfNom.setText(lieu.getNom());
        tfAdresse.setText(lieu.getAdresse());
        tfCapacite.setText(String.valueOf(lieu.getCapacite()));

        // If you have image URL in your Lieu entity, you can set it here
        // tfImageUrl.setText(lieu.getImageUrl());
    }

    @FXML
    private void handleUpdate() {
        // Clear previous error messages
        lblError.setVisible(false);

        // Validate inputs
        if (tfNom.getText().isEmpty() || tfAdresse.getText().isEmpty() || tfCapacite.getText().isEmpty()) {
            showError("All fields are required!");
            return;
        }

        int capacite;
        try {
            capacite = Integer.parseInt(tfCapacite.getText());
            if (capacite < 50) {
                showError("Capacity must be at least 50!");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Capacity must be a valid number!");
            return;
        }

        // Update Lieu object
        Lieu updatedLieu = new Lieu(
                lieuToModify.getId(),
                tfNom.getText(),
                tfAdresse.getText(),
                capacite
        );

        // Save to database
        try {
            serviceLieu.modifier(updatedLieu);
            closeWindow();
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
        }
    }

    @FXML
    private void handleBrowse() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Venue Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(btnBrowse.getScene().getWindow());
        if (selectedFile != null) {
            tfImageUrl.setText(selectedFile.getAbsolutePath());
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void showError(String message) {
        lblError.setText(message);
        lblError.setVisible(true);
    }

    private void closeWindow() {
        Stage stage = (Stage) tfNom.getScene().getWindow();
        stage.close();
    }
}