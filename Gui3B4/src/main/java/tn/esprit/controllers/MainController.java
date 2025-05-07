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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
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
import javafx.geometry.Pos;
import javafx.geometry.Insets;


public class MainController {
    @FXML private FlowPane cardsContainer;
    @FXML private VBox sidebar;
    @FXML private BorderPane mainBorderPane;

    // Les nouveaux boutons du sidebar
    @FXML private Button btnUserManagement;
    @FXML private Button btnEventManagement;
    @FXML private Button btnWorkshopManagement;
    @FXML private Button btnStockManagement;
    @FXML private Button btnLocationManagement;
    @FXML private Button btnSubscription;
    @FXML private Button btnExit;

    private final ServiceLieu serviceLieu = new ServiceLieu();

    @FXML
    public void initialize() {
        setupToolbar();
        loadLieuxCards();
    }

    private void setupToolbar() {
        // Créer la toolbar
        ToolBar toolbar = new ToolBar();
        toolbar.getStyleClass().add("admin-toolbar");

        // Titre dashboard à gauche
        Label dashboardTitle = new Label("Admin Dashboard");
        dashboardTitle.getStyleClass().add("dashboard-title");

        // Espace flexible au milieu
        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Profil admin à droite
        HBox profileBox = new HBox(5);
        profileBox.setAlignment(Pos.CENTER);

        // Icône de profil
        ImageView profileIcon = new ImageView();
        try {
            Image profileImage = new Image(getClass().getResourceAsStream("/tn/esprit/views/images/admin_profile.png"));
            profileIcon.setImage(profileImage);
        } catch (Exception e) {
            System.err.println("Profile image not found: " + e.getMessage());
        }
        profileIcon.setFitHeight(24);
        profileIcon.setFitWidth(24);

        // Texte "Admin"
        Label adminLabel = new Label("Admin");
        adminLabel.getStyleClass().add("admin-label");

        // Bouton de déconnexion
        Button logoutBtn = new Button("Logout");
        logoutBtn.getStyleClass().add("logout-btn");
        logoutBtn.setOnAction(e -> handleLogout());

        profileBox.getChildren().addAll(profileIcon, adminLabel, logoutBtn);

        // Ajouter tous les éléments à la toolbar
        toolbar.getItems().addAll(dashboardTitle, spacer, profileBox);

        // Ajouter la toolbar au haut du BorderPane
        mainBorderPane.setTop(toolbar);
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

        // Container pour les boutons d'action
        HBox buttonsContainer = new HBox(10);
        buttonsContainer.setAlignment(Pos.CENTER);

        // Bouton Update
        Button btnUpdate = new Button("Update");
        btnUpdate.getStyleClass().add("card-button");
        btnUpdate.setOnAction(e -> {
            openModifyDialog(lieu);
        });

        // Bouton Delete
        Button btnDelete = new Button("Delete");
        btnDelete.getStyleClass().addAll("card-button", "delete-button");
        btnDelete.setOnAction(e -> {
            handleDeleteLieu(lieu);
        });

        buttonsContainer.getChildren().addAll(btnUpdate, btnDelete);
        card.getChildren().addAll(imageView, title, adresse, capacite, buttonsContainer);

        return card;
    }

    private void openModifyDialog(Lieu lieu) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/views/ModifyLieu.fxml"));
            Parent root = loader.load();

            ModifyLieuController controller = loader.getController();
            controller.setLieu(lieu);
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

    private void handleDeleteLieu(Lieu lieu) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Delete Venue");
        confirmation.setContentText("Are you sure you want to delete the venue: " + lieu.getNom() + "?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                serviceLieu.supprimer(lieu.getId());
                showAlert(Alert.AlertType.INFORMATION, "Success", "Venue successfully deleted!");
                loadLieuxCards();
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete venue: " + e.getMessage());
            }
        }
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
    private void handleActualiser() {
        loadLieuxCards();
        showAlert(Alert.AlertType.INFORMATION, "Refresh", "Venue list has been refreshed!");
    }

    @FXML
    private void handleUserManagement() {
        // À implémenter
    }

    @FXML
    private void handleEventManagement() {
        // À implémenter
    }

    @FXML
    private void handleWorkshopManagement() {
        // À implémenter
    }

    @FXML
    private void handleStockManagement() {
        // À implémenter
    }

    @FXML
    private void handleLocationManagement() {
        // À implémenter
    }

    @FXML
    private void handleSubscription() {
        // À implémenter
    }

    @FXML
    private void handleExit() {
        Stage stage = (Stage) sidebar.getScene().getWindow();
        stage.close();
    }

    private void handleLogout() {
        // Logique de déconnexion à implémenter
        showAlert(Alert.AlertType.INFORMATION, "Logout", "Logging out...");

        // Fermer l'application ou rediriger vers la page de connexion
        // À implémenter selon vos besoins
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