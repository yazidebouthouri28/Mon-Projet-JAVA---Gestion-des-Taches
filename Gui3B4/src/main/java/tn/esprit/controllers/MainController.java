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
    @FXML private VBox sidebarMenu;
    @FXML private BorderPane mainBorderPane;

    // Nouveaux composants pour le Toolbar
    @FXML private ImageView profileImage;
    @FXML private Label userNameLabel;
    @FXML private Label statusLabel;

    // Boutons du sidebar
    @FXML private Button btnUserManagement;
    @FXML private Button btnEventManagement;
    @FXML private Button btnWorkshopManagement;
    @FXML private Button btnStockManagement;
    @FXML private Button btnLocationManagement;
    @FXML private Button btnSubscription;
    @FXML private Button btnLogout;

    private final ServiceLieu serviceLieu = new ServiceLieu();

    @FXML
    public void initialize() {
        setupUIComponents();
        loadLieuxCards();

        // Ajuster la taille de la fenêtre pour occuper tout l'écran
        mainBorderPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                Stage stage = (Stage) newScene.getWindow();
                stage.setMaximized(true);
            }
        });
    }

    private void setupUIComponents() {
        // Configuration de l'image de profil
        try {
            Image image = new Image(getClass().getResourceAsStream("/tn/esprit/views/images/admin_profile.png"));
            profileImage.setImage(image);
            profileImage.setFitWidth(40);
            profileImage.setFitHeight(40);

            // Ajouter un effet de cercle pour l'image de profil
            profileImage.setStyle("-fx-background-radius: 50%; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0.2, 0, 1);");
        } catch (Exception e) {
            System.err.println("Profile image not found: " + e.getMessage());
        }

        // Configuration du nom d'utilisateur
        userNameLabel.setText("Admin User");

        // Appliquer les styles aux boutons du sidebar si nécessaire
        applyStylesButtons();
    }

    private void applyStylesButtons() {
        // Si vous souhaitez appliquer des styles via code plutôt que FXML
        // Vous pouvez également utiliser les classes CSS que nous avons définies
        btnUserManagement.getStyleClass().add("sidebar-btn");
        btnEventManagement.getStyleClass().add("sidebar-btn");
        btnWorkshopManagement.getStyleClass().add("sidebar-btn");
        btnStockManagement.getStyleClass().add("sidebar-btn");
        btnLocationManagement.getStyleClass().add("sidebar-btn");
        btnSubscription.getStyleClass().add("sidebar-btn");
        btnLogout.getStyleClass().add("logout-btn");
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
        statusLabel.setText("User Management Selected");
        // À implémenter la logique complète
    }

    @FXML
    private void handleEventManagement() {
        statusLabel.setText("Event Management Selected");
        // À implémenter la logique complète
    }

    @FXML
    private void handleWorkshopManagement() {
        statusLabel.setText("Workshop Management Selected");
        // À implémenter la logique complète
    }

    @FXML
    private void handleStockManagement() {
        statusLabel.setText("Stock Management Selected");
        // À implémenter la logique complète
    }

    @FXML
    private void handleLocationManagement() {
        statusLabel.setText("Location Management Selected");
        // À implémenter la logique complète
    }

    @FXML
    private void showSubscriptionManagement() {
        statusLabel.setText("Subscription Management Selected");
        // À implémenter la logique complète
    }

    @FXML
    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout Confirmation");
        alert.setHeaderText("Confirm Logout");
        alert.setContentText("Are you sure you want to logout?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            showAlert(Alert.AlertType.INFORMATION, "Logout", "Successfully logged out!");

            // Fermer l'application ou rediriger vers la page de connexion
            // Pour l'instant, on ferme simplement la fenêtre
            Stage stage = (Stage) sidebarMenu.getScene().getWindow();
            stage.close();
        }
    }

    @FXML
    private void handleProfileClick() {
        // Afficher un dialogue de profil ou un menu contextuel
        showAlert(Alert.AlertType.INFORMATION, "Profile", "Profile options will be available soon!");
    }

    @FXML
    private void handleExit() {
        Stage stage = (Stage) sidebarMenu.getScene().getWindow();
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