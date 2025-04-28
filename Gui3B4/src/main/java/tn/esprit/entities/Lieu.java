package tn.esprit.entities;

public class Lieu {
    private int IdLieu;
    private String nom;
    private String adresse;
    private int capacite;

    public Lieu() {
    }

    public Lieu(int IdLieu, String nom, String adresse, int capacite) {
        this.IdLieu = IdLieu;
        this.nom = nom;
        this.adresse = adresse;
        this.capacite = capacite;
    }

    public Lieu(String nom, String adresse, int capacite) {
        this.nom = nom;
        this.adresse = adresse;
        this.capacite = capacite;
    }

    public int getId() {
        return IdLieu;
    }

    public void setId(int IdLieu) {
        this.IdLieu = IdLieu;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public int getCapacite() {
        return capacite;
    }

    public void setCapacite(int capacite) {
        this.capacite = capacite;
    }

    @Override
    public String toString() {
        return "Lieu{" +
                "id=" + IdLieu +
                ", nom='" + nom + '\'' +
                ", adresse='" + adresse + '\'' +
                ", capacite=" + capacite +
                '}';
    }
}