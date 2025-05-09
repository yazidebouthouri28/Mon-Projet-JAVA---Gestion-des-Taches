package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tn.esprit.entities.Lieu;
import tn.esprit.services.ServiceLieu;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;
import javafx.geometry.Pos;


public class MainController {
    @FXML private FlowPane cardsContainer;
    @FXML private VBox sidebarMenu;
    @FXML private BorderPane mainBorderPane;
    @FXML private TextField searchField;

    @FXML private ImageView profileImage;
    @FXML private Label userNameLabel;
    @FXML private Label statusLabel;

    @FXML private Button btnUserManagement;
    @FXML private Button btnEventManagement;
    @FXML private Button btnWorkshopManagement;
    @FXML private Button btnStockManagement;
    @FXML private Button btnLocationManagement;
    @FXML private Button btnSubscription;
    @FXML private Button btnLogout;
    @FXML private Button btnSearch;

    private final ServiceLieu serviceLieu = new ServiceLieu();
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
        if (stage != null) {
            stage.setMaximized(true);
        }
    }

    @FXML
    public void initialize() {
        setupUIComponents();
        loadLieuxCards();
        setupSearchFieldListener();
    }

    private void setupUIComponents() {
        try {
            URL profileImageUrl = getClass().getResource("/tn/esprit/views/images/admin_profile.png");
            if (profileImageUrl != null) {
                profileImage.setImage(new Image(profileImageUrl.toString()));
            }
            profileImage.setFitWidth(40);
            profileImage.setFitHeight(40);
            profileImage.setStyle("-fx-background-radius: 50%; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0.2, 0, 1);");
        } catch (Exception e) {
            System.err.println("Failed to load profile image: " + e.getMessage());
        }

        userNameLabel.setText("Admin User");
        applyStylesButtons();
    }

    // Configuration du listener pour le champ de recherche (recherche pendant la saisie)
    private void setupSearchFieldListener() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() >= 2) {
                // Déclencher la recherche après 2 caractères
                performSearch(newValue);
            } else if (newValue.isEmpty()) {
                // Recharger tous les lieux si le champ est vide
                loadLieuxCards();
            }
        });
    }

    private void applyStylesButtons() {
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
            loadCardsFromList(lieux);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    // Nouvelle méthode pour charger les cartes à partir d'une liste de lieux
    private void loadCardsFromList(List<Lieu> lieux) {
        cardsContainer.getChildren().clear();
        int index = 0;
        for (Lieu lieu : lieux) {
            Node card = createLieuCard(lieu);
            cardsContainer.getChildren().add(card);
            playCardEntryAnimation(card, index++);
        }
    }

    // Nouvelle méthode pour gérer le bouton de recherche
    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().trim();
        performSearch(searchTerm);
    }

    // Méthode qui effectue la recherche
    private void performSearch(String searchTerm) {
        if (searchTerm.isEmpty()) {
            loadLieuxCards();
            return;
        }

        try {
            List<Lieu> searchResults = serviceLieu.rechercherLieux(searchTerm);
            loadCardsFromList(searchResults);

            if (searchResults.isEmpty()) {
                statusLabel.setText("Aucun lieu trouvé pour: " + searchTerm);
            } else {
                statusLabel.setText(searchResults.size() + " lieu(x) trouvé(s) pour: " + searchTerm);
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de recherche", e.getMessage());
        }
    }

    private Node createLieuCard(Lieu lieu) {
        VBox card = new VBox(10);
        card.getStyleClass().add("lieu-card");

        ImageView imageView = new ImageView();
        try {
            URL imageUrl = getClass().getResource("/tn/esprit/views/images/Hbelt.png");
            if (imageUrl != null) {
                Image image = new Image(imageUrl.toString());
                imageView.setImage(image);
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
                imageView.setPreserveRatio(true);
                imageView.getStyleClass().add("card-image");
                card.getChildren().add(imageView);
            } else {
                throw new Exception("Image URL is null");
            }
        } catch (Exception e) {
            System.err.println("Could not load image: " + e.getMessage());
            Label placeholder = new Label("Map");
            placeholder.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 15px; -fx-text-fill: #555; -fx-border-color: #ddd;");
            placeholder.setPrefWidth(75);
            placeholder.setPrefHeight(75);
            placeholder.setAlignment(Pos.CENTER);
            card.getChildren().add(placeholder);
        }

        Label title = new Label(lieu.getNom());
        title.getStyleClass().add("card-title");

        Label adresse = new Label(lieu.getAdresse());
        adresse.getStyleClass().add("card-detail");

        Label capacite = new Label("Capacity: " + lieu.getCapacite());
        capacite.getStyleClass().add("card-detail");

        HBox buttonsContainer = new HBox(5);
        buttonsContainer.setAlignment(Pos.CENTER);

        Button btnUpdate = new Button("Update");
        btnUpdate.getStyleClass().add("card-button");
        btnUpdate.setOnAction(e -> openModifyDialog(lieu));

        Button btnDelete = new Button("Delete");
        btnDelete.getStyleClass().addAll("card-button", "delete-button");
        btnDelete.setOnAction(e -> handleDeleteLieu(lieu));

        Button btnShowEvents = new Button("Events");
        btnShowEvents.getStyleClass().addAll("card-button", "events-button");
        btnShowEvents.setOnAction(e -> showEventsForLieu(lieu));

        buttonsContainer.getChildren().addAll(btnUpdate, btnDelete, btnShowEvents);
        card.getChildren().addAll(title, adresse, capacite, buttonsContainer);

        return card;
    }

    private void openModifyDialog(Lieu lieu) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/views/ModifyLieu.fxml"));
            Parent root = loader.load();
            ModifyLieuController controller = loader.getController();
            controller.setLieu(lieu);
            controller.setMainController(this);

            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setTitle("Modify Venue");
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();

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

    private void showEventsForLieu(Lieu lieu) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/views/EventsParLieu.fxml"));
            Parent root = loader.load();

            EventsParLieuController controller = loader.getController();
            controller.setLieuId(lieu.getId());

            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setTitle("Events for " + lieu.getNom());
            dialogStage.setScene(new Scene(root));
            dialogStage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot open events window: " + e.getMessage());
        }
    }

    @FXML
    private void handleAjouter() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/views/AddLieu.fxml"));
            Parent root = loader.load();

            AddLieuController controller = loader.getController();
            controller.setMainController(this);

            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setTitle("Add New Venue");
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();

            loadLieuxCards();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot open add venue window: " + e.getMessage());
        }
    }

    @FXML private void handleActualiser() {
        searchField.clear();  // Réinitialiser le champ de recherche
        loadLieuxCards();
        showAlert(Alert.AlertType.INFORMATION, "Refresh", "Venue list has been refreshed!");
    }

    @FXML private void handleUserManagement() { statusLabel.setText("User Management Selected"); }
    @FXML private void handleEventManagement() { statusLabel.setText("Event Management Selected"); }
    @FXML private void handleWorkshopManagement() { statusLabel.setText("Workshop Management Selected"); }
    @FXML private void handleStockManagement() { statusLabel.setText("Stock Management Selected"); }
    @FXML private void handleLocationManagement() { statusLabel.setText("Location Management Selected"); }
    @FXML private void showSubscriptionManagement() { statusLabel.setText("Subscription Management Selected"); }

    @FXML
    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout Confirmation");
        alert.setHeaderText("Confirm Logout");
        alert.setContentText("Are you sure you want to logout?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK && stage != null) {
            showAlert(Alert.AlertType.INFORMATION, "Logout", "Successfully logged out!");
            stage.close();
        }
    }

    @FXML private void handleProfileClick() {
        showAlert(Alert.AlertType.INFORMATION, "Profile", "Profile options will be available soon!");
    }

    @FXML private void handleExit() {
        if (stage != null) {
            stage.close();
        }
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

        SequentialTransition animation = new SequentialTransition(fade, slide);
        animation.setDelay(Duration.millis(index * 100));
        animation.play();
    }
}