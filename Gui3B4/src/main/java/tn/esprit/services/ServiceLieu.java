package tn.esprit.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import tn.esprit.entities.Lieu;
import tn.esprit.entities.InfoLieu;
import tn.esprit.utils.MyDataBase;

public class ServiceLieu {

    private Connection connection;

    public ServiceLieu() {
        connection = MyDataBase.getInstance().getConnection();
    }

    // Méthodes existantes pour la gestion des lieux (inchangées)
    public void ajouter(Lieu lieu) throws SQLException {
        String query = "INSERT INTO lieux (nom, adresse, capacite) VALUES (?, ?, ?)";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, lieu.getNom());
            pst.setString(2, lieu.getAdresse());
            pst.setInt(3, lieu.getCapacite());
            pst.executeUpdate();
        }
    }

    public void modifier(Lieu lieu) throws SQLException {
        String query = "UPDATE lieux SET nom = ?, adresse = ?, capacite = ? WHERE IdLieu = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, lieu.getNom());
            pst.setString(2, lieu.getAdresse());
            pst.setInt(3, lieu.getCapacite());
            pst.setInt(4, lieu.getId());
            pst.executeUpdate();
        }
    }

    public void supprimer(int IdLieu) throws SQLException {
        String query = "DELETE FROM lieux WHERE IdLieu = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, IdLieu);
            pst.executeUpdate();
        }
    }

    public List<Lieu> afficher() throws SQLException {
        List<Lieu> lieux = new ArrayList<>();
        String query = "SELECT * FROM lieux";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                Lieu lieu = new Lieu(
                        rs.getInt("IdLieu"),
                        rs.getString("nom"),
                        rs.getString("adresse"),
                        rs.getInt("capacite")
                );
                lieux.add(lieu);
            }
        }
        return lieux;
    }

    // Nouvelle méthode de recherche de lieux
    public List<Lieu> rechercherLieux(String searchTerm) throws SQLException {
        List<Lieu> lieux = new ArrayList<>();
        String query = "SELECT * FROM lieux WHERE nom LIKE ? OR adresse LIKE ?";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            String searchPattern = "%" + searchTerm + "%";
            pst.setString(1, searchPattern);
            pst.setString(2, searchPattern);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Lieu lieu = new Lieu(
                            rs.getInt("IdLieu"),
                            rs.getString("nom"),
                            rs.getString("adresse"),
                            rs.getInt("capacite")
                    );
                    lieux.add(lieu);
                }
            }
        }
        return lieux;
    }

    // Seule méthode ajoutée pour les événements associés
    public ObservableList<InfoLieu> getEvenementsParLieu(int lieuId) {
        ObservableList<InfoLieu> data = FXCollections.observableArrayList();
        String query = "SELECT * FROM infos_lieux WHERE IdLieu = ?";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, lieuId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                InfoLieu info = new InfoLieu(
                        rs.getInt("IdLieu"),
                        rs.getString("nom_lieu"),
                        rs.getString("nom_evenement"),
                        rs.getInt("IdEvenement"),
                        rs.getInt("IdReservation"),
                        rs.getString("commentaires")
                );
                data.add(info); // Correction ici
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL: " + e.getMessage());
        }
        return data;
    }
}