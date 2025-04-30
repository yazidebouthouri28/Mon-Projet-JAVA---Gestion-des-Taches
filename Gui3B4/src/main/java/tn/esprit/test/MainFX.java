package tn.esprit.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainFX extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Chemin correct vers le fichier FXML (place dans le répertoire resources)
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Main.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setTitle("Gestion des lieux");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Ajout de logs pour débogage
        System.out.println("Application démarrée avec succès");
    }

    public static void main(String[] args) {
        launch(args);
    }
}