package tn.esprit.entities;

public class InfoLieu {
    private final int idLieu;
    private final String nomLieu;
    private final String nomEvenement;
    private final int idEvenement;
    private final int idReservation;
    private final String commentaires;

    public InfoLieu(int idLieu, String nomLieu, String nomEvenement,
                    int idEvenement, int idReservation, String commentaires) {
        this.idLieu = idLieu;
        this.nomLieu = nomLieu;
        this.nomEvenement = nomEvenement;
        this.idEvenement = idEvenement;
        this.idReservation = idReservation;
        this.commentaires = commentaires;
    }

    // Getters
    public int getIdLieu() { return idLieu; }
    public String getNomLieu() { return nomLieu; }
    public String getNomEvenement() { return nomEvenement; }
    public int getIdEvenement() { return idEvenement; }
    public int getIdReservation() { return idReservation; }
    public String getCommentaires() { return commentaires; }
}