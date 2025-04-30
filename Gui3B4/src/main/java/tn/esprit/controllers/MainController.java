package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import tn.esprit.entities.Lieu;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MainController {
    // Références aux éléments FXML
    @FXML private TableView<Lieu> tableViewLieux;
    @FXML private TableColumn<Lieu, Integer> colId;
    @FXML private TableColumn<Lieu, String> colNom;
    @FXML private TableColumn<Lieu, String> colAdresse;
    @FXML private TableColumn<Lieu, Integer> colCapacite;

    // Champs texte
    @FXML private TextField txtNom;
    @FXML private TextField txtAdresse;
    @FXML private TextField txtCapacite;

    // Label de statut
    @FXML private Label lblStatus;

    // Création d'une liste observable pour les données (au lieu d'utiliser ServiceLieu)
    private final ObservableList<Lieu> lieuxData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        System.out.println("Initialisation du contrôleur avec données simulées...");

        // Configuration des colonnes
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colAdresse.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        colCapacite.setCellValueFactory(new PropertyValueFactory<>("capacite"));

        // Ajouter des données simulées
        addMockData();

        // Configurer la table
        tableViewLieux.setItems(lieuxData);

        // Configurer le listener de sélection
        tableViewLieux.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtNom.setText(newSelection.getNom());
                txtAdresse.setText(newSelection.getAdresse());
                txtCapacite.setText(String.valueOf(newSelection.getCapacite()));
            }
        });

        lblStatus.setText("Interface chargée avec données simulées");
        System.out.println("Interface initialisée avec " + lieuxData.size() + " lieux simulés");
    }

    private void addMockData() {
        // Ajouter quelques lieux simulés pour tester l'interface
        lieuxData.add(new Lieu(1, "Hôpital Central", "123 Rue Principale, Tunis", 500));
        lieuxData.add(new Lieu(2, "Centre de Conférence", "45 Avenue des Sciences, Sfax", 300));
        lieuxData.add(new Lieu(3, "Salle de Formation Médicale", "78 Boulevard Santé, Sousse", 100));
        lieuxData.add(new Lieu(4, "Clinique Universitaire", "15 Rue des Médecins, Monastir", 200));
        lieuxData.add(new Lieu(5, "Centre de Recherche", "31 Avenue Innovation, Hammamet", 150));
    }

    @FXML
    private void handleAjouter() {
        try {
            String nom = txtNom.getText();
            String adresse = txtAdresse.getText();
            int capacite = Integer.parseInt(txtCapacite.getText());

            // Générer un ID simple pour simuler
            int newId = lieuxData.size() + 1;
            Lieu nouveauLieu = new Lieu(newId, nom, adresse, capacite);
            lieuxData.add(nouveauLieu);

            lblStatus.setText("Lieu ajouté avec succès (simulation)");
            clearFields();
        } catch (NumberFormatException e) {
            lblStatus.setText("Erreur: Capacité doit être un nombre");
        }
    }

    @FXML
    private void handleModifier() {
        Lieu selected = tableViewLieux.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                selected.setNom(txtNom.getText());
                selected.setAdresse(txtAdresse.getText());
                selected.setCapacite(Integer.parseInt(txtCapacite.getText()));

                // Rafraîchir la table pour voir les modifications
                tableViewLieux.refresh();

                lblStatus.setText("Lieu modifié avec succès (simulation)");
                clearFields();
            } catch (NumberFormatException e) {
                lblStatus.setText("Erreur: Capacité doit être un nombre");
            }
        } else {
            lblStatus.setText("Veuillez sélectionner un lieu à modifier");
        }
    }

    @FXML
    private void handleSupprimer() {
        Lieu selected = tableViewLieux.getSelectionModel().getSelectedItem();
        if (selected != null) {
            lieuxData.remove(selected);
            lblStatus.setText("Lieu supprimé avec succès (simulation)");
            clearFields();
        } else {
            lblStatus.setText("Veuillez sélectionner un lieu à supprimer");
        }
    }

    @FXML
    private void handleAfficher() {
        lblStatus.setText("Liste des lieux actualisée (simulation)");
        System.out.println("Affichage des lieux: " + lieuxData.size() + " lieux");
    }

    private void clearFields() {
        txtNom.clear();
        txtAdresse.clear();
        txtCapacite.clear();
    }
}