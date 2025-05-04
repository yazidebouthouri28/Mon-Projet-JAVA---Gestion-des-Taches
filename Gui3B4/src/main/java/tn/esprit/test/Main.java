package tn.esprit.test;

import java.sql.SQLException;
import tn.esprit.entities.Lieu;
import tn.esprit.services.ServiceLieu;
import tn.esprit.utils.MyDataBase;

public class Main {
    public static void main(String[] args) {
        ServiceLieu sl = new ServiceLieu();

        try {
            //Ajouter un nouveau lieu
            sl.ajouter(new Lieu("SSSSSSSSSSSSSSSS", "xxxxxddddxxxxxxx", 70));
            System.out.println("Lieu ajouté !");

             //Modifier un lieu existant (décommenter pour utiliser)
            sl.modifier(new Lieu(6, "Gassrine", "groupeeeeeeeeee", 999));
            System.out.println("Lieu modifié !");

            //supprimer un lieu (décommenter pour utiliser)
            sl.supprimer(5);
            System.out.println("Lieu supprimé !");

             //Afficher tous les lieux
            System.out.println("Liste des lieux :");
            System.out.println(sl.afficher());
        } catch (SQLException e) {
            System.err.println("Erreur SQL: " + e.getMessage());
            e.printStackTrace();
        }
    }
}