package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import tn.esprit.entities.Lieu;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import tn.esprit.services.ServiceLieu;

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

    // Service pour accéder à la base de données
    private final ServiceLieu serviceLieu = new ServiceLieu();

    // Liste observable pour les données
    private final ObservableList<Lieu> lieuxData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Configuration des colonnes
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colAdresse.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        colCapacite.setCellValueFactory(new PropertyValueFactory<>("capacite"));

        // Charger les données depuis la base de données
        refreshTableData();

        // Configurer le listener de sélection
        tableViewLieux.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtNom.setText(newSelection.getNom());
                txtAdresse.setText(newSelection.getAdresse());
                txtCapacite.setText(String.valueOf(newSelection.getCapacite()));
            }
        });
    }

    private void refreshTableData() {
        try {
            lieuxData.clear();
            lieuxData.addAll(serviceLieu.afficher());
            tableViewLieux.setItems(lieuxData);
            lblStatus.setText("Données chargées avec succès");
        } catch (Exception e) {
            lblStatus.setText("Erreur: " + e.getMessage());
        }
    }

    @FXML
    private void handleAjouter() {
        try {
            String nom = txtNom.getText();
            String adresse = txtAdresse.getText();
            int capacite = Integer.parseInt(txtCapacite.getText());

            Lieu nouveauLieu = new Lieu(nom, adresse, capacite);
            serviceLieu.ajouter(nouveauLieu);
            refreshTableData();
            clearFields();
            lblStatus.setText("Lieu ajouté avec succès");
        } catch (NumberFormatException e) {
            lblStatus.setText("Erreur: Capacité doit être un nombre");
        } catch (Exception e) {
            lblStatus.setText("Erreur lors de l'ajout: " + e.getMessage());
        }
    }

    @FXML
    private void handleModifier() {
        Lieu selected = tableViewLieux.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                String nom = txtNom.getText();
                String adresse = txtAdresse.getText();
                int capacite = Integer.parseInt(txtCapacite.getText());

                Lieu lieuModifie = new Lieu(selected.getId(), nom, adresse, capacite);
                serviceLieu.modifier(lieuModifie);
                refreshTableData();
                clearFields();
                lblStatus.setText("Lieu modifié avec succès");
            } catch (NumberFormatException e) {
                lblStatus.setText("Erreur: Capacité doit être un nombre");
            } catch (Exception e) {
                lblStatus.setText("Erreur: " + e.getMessage());
            }
        } else {
            lblStatus.setText("Veuillez sélectionner un lieu à modifier");
        }
    }

    @FXML
    private void handleSupprimer() {
        Lieu selected = tableViewLieux.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                serviceLieu.supprimer(selected.getId());
                refreshTableData();
                clearFields();
                lblStatus.setText("Lieu supprimé avec succès");
            } catch (Exception e) {
                lblStatus.setText("Erreur: " + e.getMessage());
            }
        } else {
            lblStatus.setText("Veuillez sélectionner un lieu à supprimer");
        }
    }

    @FXML
    private void handleAfficher() {
        refreshTableData();
        lblStatus.setText("Liste des lieux actualisée");
    }

    private void clearFields() {
        txtNom.clear();
        txtAdresse.clear();
        txtCapacite.clear();
    }
}