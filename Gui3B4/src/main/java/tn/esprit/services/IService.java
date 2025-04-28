package tn.esprit.services;

import tn.esprit.entities.Lieu;

import java.sql.SQLException;
import java.util.List;

public interface IService {

    void ajouter(Lieu l);
    void modifier(Lieu l);
    void supprimer(int IdLieu);
    List<Lieu> afficher ();
}
