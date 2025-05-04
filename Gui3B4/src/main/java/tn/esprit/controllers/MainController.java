package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tn.esprit.entities.Lieu;
import tn.esprit.services.ServiceLieu;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;


public class MainController {
    @FXML private FlowPane cardsContainer;
    @FXML private VBox sidebar;
    @FXML private Button btnAjouter;
    @FXML private Button btnModifier;
    @FXML private Button btnSupprimer;
    @FXML private Button btnActualiser;
    @FXML private Button btnEvenements;
    @FXML private Button btnQuitter;

    private final ServiceLieu serviceLieu = new ServiceLieu();
    private Lieu selectedLieu = null;

    @FXML
    public void initialize() {
        loadLieuxCards();

        // Disable buttons that require selection
        btnModifier.setDisable(true);
        btnSupprimer.setDisable(true);
        btnEvenements.setDisable(true);
    }

    private void loadLieuxCards() {
        cardsContainer.getChildren().clear();

        try {
            List<Lieu> lieux = serviceLieu.afficher();
            int index = 0;
            for (Lieu lieu : lieux) {
                Node card = createLieuCard(lieu);
                cardsContainer.getChildren().add(card);
                playCardEntryAnimation(card, index++);
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }


    private Node createLieuCard(Lieu lieu) {
        VBox card = new VBox(10);
        card.getStyleClass().add("lieu-card");

        // Chargement de l'image location_image.png
        ImageView imageView = new ImageView();
        try {
            Image image = new Image(getClass().getResourceAsStream("/tn/esprit/views/images/location_image.png"));
            imageView.setImage(image);
        } catch (Exception e) {
            System.err.println("Image not found: " + e.getMessage());
        }

        imageView.setFitWidth(200);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);
        imageView.getStyleClass().add("card-image");

        Label title = new Label(lieu.getNom());
        title.getStyleClass().add("card-title");

        Label adresse = new Label(lieu.getAdresse());
        adresse.getStyleClass().add("card-detail");

        Label capacite = new Label("Capacity: " + lieu.getCapacite());
        capacite.getStyleClass().add("card-detail");

        Button btnSelect = new Button("Select");
        btnSelect.getStyleClass().add("card-button");
        btnSelect.setOnAction(e -> {
            selectedLieu = lieu;
            highlightSelectedCard(card);

            btnModifier.setDisable(false);
            btnSupprimer.setDisable(false);
            btnEvenements.setDisable(false);
        });

        card.getChildren().addAll(imageView, title, adresse, capacite, btnSelect);
        return card;
    }


    private void highlightSelectedCard(VBox selectedCard) {
        // Remove highlight from all cards
        for (Node node : cardsContainer.getChildren()) {
            if (node instanceof VBox) {
                node.getStyleClass().remove("selected-card");
            }
        }

        // Add highlight to selected card
        selectedCard.getStyleClass().add("selected-card");
    }

    @FXML
    private void handleAjouter() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/views/AddLieu.fxml"));
            Parent root = loader.load();

            AddLieuController controller = loader.getController();
            controller.setMainController(this);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add New Venue");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Refresh the list after adding
            loadLieuxCards();

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot open add venue window: " + e.getMessage());
        }
    }

    @FXML
    private void handleModifier() {
        if (selectedLieu == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a venue first");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/views/ModifyLieu.fxml"));
            Parent root = loader.load();

            ModifyLieuController controller = loader.getController();
            controller.setLieu(selectedLieu);
            controller.setMainController(this);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Modify Venue");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Refresh the list after modification
            loadLieuxCards();

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot open modify venue window: " + e.getMessage());
        }
    }

    @FXML
    private void handleSupprimer() {
        if (selectedLieu == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a venue first");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Delete Venue");
        confirmation.setContentText("Are you sure you want to delete the venue: " + selectedLieu.getNom() + "?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                serviceLieu.supprimer(selectedLieu.getId());
                showAlert(Alert.AlertType.INFORMATION, "Success", "Venue successfully deleted!");

                // Reset selection and refresh list
                selectedLieu = null;
                btnModifier.setDisable(true);
                btnSupprimer.setDisable(true);
                btnEvenements.setDisable(true);
                loadLieuxCards();

            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete venue: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleActualiser() {
        loadLieuxCards();
        selectedLieu = null;
        btnModifier.setDisable(true);
        btnSupprimer.setDisable(true);
        btnEvenements.setDisable(true);
        showAlert(Alert.AlertType.INFORMATION, "Refresh", "Venue list has been refreshed!");
    }

    @FXML
    private void handleEvenements() {
        if (selectedLieu == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a venue first");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/views/EventsParLieu.fxml"));
            Parent root = loader.load();

            EventsParLieuController controller = loader.getController();
            controller.setLieuId(selectedLieu.getId());

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Events for " + selectedLieu.getNom());
            stage.setScene(new Scene(root, 700, 400));
            stage.show();

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot open events window: " + e.getMessage());
        }
    }

    @FXML
    private void handleQuitter() {
        Stage stage = (Stage) btnQuitter.getScene().getWindow();
        stage.close();
    }

    public void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void playCardEntryAnimation(Node node, int index) {
        FadeTransition fade = new FadeTransition(Duration.millis(300), node);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(Duration.millis(300), node);
        slide.setFromY(20);
        slide.setToY(0);

        SequentialTransition animation = new SequentialTransition();
        animation.getChildren().addAll(fade, slide);
        animation.setDelay(Duration.millis(index * 100)); // delay based on index
        animation.play();
    }

}