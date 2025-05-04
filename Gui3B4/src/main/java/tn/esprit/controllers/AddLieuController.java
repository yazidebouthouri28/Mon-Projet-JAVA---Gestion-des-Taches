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

public class AddLieuController {
    @FXML private TextField tfNom;
    @FXML private TextField tfAdresse;
    @FXML private TextField tfCapacite;
    @FXML private TextField tfImageUrl;
    @FXML private Button btnBrowse;
    @FXML private Label lblError;

    private MainController mainController;
    private final ServiceLieu serviceLieu = new ServiceLieu();

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void handleSave() {
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

        // Create new Lieu object
        Lieu nouveauLieu = new Lieu(tfNom.getText(), tfAdresse.getText(), capacite);

        // Save to database
        try {
            serviceLieu.ajouter(nouveauLieu);
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