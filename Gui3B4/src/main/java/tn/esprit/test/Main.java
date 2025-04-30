package tn.esprit.test;

import tn.esprit.entities.Lieu;
import tn.esprit.services.ServiceLieu;
import tn.esprit.utils.MyDataBase;

public class Main {
    public static void main(String[] args) {
        ServiceLieu sl = new ServiceLieu();

        //Ajouter un nouveau lieu
        //sl.ajouter(new Lieu("SSSSSSSSSSSSSSSS", "xxxxxddddxxxxxxx", 40));
        //System.out.println("Lieu ajouté !");

        // Modifier un lieu existant (décommenter pour utiliser)
        //sl.modifier(new Lieu(6, "Gassrine", "groupeeeeeeeeee", 999));
        //System.out.println("Lieu modifié !");

        //Supprimer un lieu (décommenter pour utiliser)
        //sl.supprimer(5);
        //System.out.println("Lieu supprimé !");

        // Afficher tous les lieux
        //System.out.println("Liste des lieux :");
        //System.out.println(sl.afficher());
    }
}