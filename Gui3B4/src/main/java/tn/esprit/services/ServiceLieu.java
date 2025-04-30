package tn.esprit.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import tn.esprit.entities.Lieu;
import tn.esprit.utils.MyDataBase;

public class ServiceLieu {

    private Connection connection;

    public ServiceLieu() {
        connection = MyDataBase.getInstance().getConnection();
    }

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
}