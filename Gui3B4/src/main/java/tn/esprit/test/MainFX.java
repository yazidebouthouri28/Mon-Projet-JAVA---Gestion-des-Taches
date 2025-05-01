package tn.esprit.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class MainFX extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            // Méthode pour charger le FXML avec meilleure gestion d'erreurs
            URL fxmlUrl = getClass().getResource("/tn/esprit/views/Main.fxml");
            if (fxmlUrl == null) {
                System.err.println("ERREUR CRITIQUE: Fichier FXML introuvable");
                System.err.println("Chemin recherché: /tn/esprit/views/Main.fxml");
                System.err.println("Répertoire courant: " + System.getProperty("user.dir"));

                // Essayer de lister les ressources disponibles
                System.err.println("Essai de trouver des ressources similaires:");
                ClassLoader classLoader = getClass().getClassLoader();
                URL resourcesUrl = classLoader.getResource("tn/esprit/views");
                if (resourcesUrl != null) {
                    System.err.println("Dossier tn/esprit/views trouvé à: " + resourcesUrl);
                } else {
                    System.err.println("Dossier tn/esprit/views introuvable!");
                }

                throw new IOException("Fichier FXML introuvable. Vérifiez le chemin: /tn/esprit/views/Main.fxml");
            }

            Parent root = FXMLLoader.load(fxmlUrl);
            primaryStage.setTitle("Gestion des Lieux");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();

        } catch (IOException e) {
            System.err.println("Erreur lors du chargement du FXML: " + e.getMessage());
            e.printStackTrace();
            // Afficher une alerte à l'utilisateur
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur au démarrage");
            alert.setContentText("Impossible de charger l'interface utilisateur: " + e.getMessage());
            alert.showAndWait();
        }
    }

    public static void main(String[] args) {
        // Ajout de vérifications supplémentaires
        System.out.println("Démarrage de l'application...");
        System.out.println("Classe en cours d'exécution: " + MainFX.class.getName());
        System.out.println("Chemin FXML recherché: " + MainFX.class.getResource("/tn/esprit/views/Main.fxml"));
        launch(args);
    }
}