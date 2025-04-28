package tn.esprit.services;

import tn.esprit.entities.Lieu;
import tn.esprit.utils.MyDataBase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceLieu implements IService {
    private Connection cnx;

    public ServiceLieu() {
        this.cnx = MyDataBase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Lieu l) {
        String req = "INSERT INTO lieux(nom, adresse, capacite) VALUES(?,?,?)";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setString(1, l.getNom());
            ps.setString(2, l.getAdresse());
            ps.setInt(3, l.getCapacite());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void modifier(Lieu l) {
        String req = "UPDATE lieux SET nom=?, adresse=?, capacite=? WHERE IdLieu=?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setString(1, l.getNom());
            ps.setString(2, l.getAdresse());
            ps.setInt(3, l.getCapacite());
            ps.setInt(4, l.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void supprimer(int IdLieu) {
        String req = "DELETE FROM lieux WHERE IdLieu=?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, IdLieu);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public List<Lieu> afficher() {
        List<Lieu> lieux = new ArrayList<>();
        String req = "SELECT * FROM lieux";
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(req)) {
            while (rs.next()) {
                Lieu l = new Lieu(
                        rs.getInt("IdLieu"),
                        rs.getString("nom"),
                        rs.getString("adresse"),
                        rs.getInt("capacite")
                );
                lieux.add(l);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return lieux;
    }
}