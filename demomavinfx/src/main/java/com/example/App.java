package com.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;



import java.io.IOException;

import com.example.DB.models.Client;
import com.example.DB.models.Medicaments;
import com.example.DB.models.Ordonnances;


/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("primary"), 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
         Ordonnances o = new Ordonnances(1, 1, 1, "STYLESHEET_CASPIAN", "STYLESHEET_CASPIAN", "STYLESHEET_CASPIAN", "STYLESHEET_MODENA", "STYLESHEET_CASPIAN");
         String URL = "jdbc:sqlite:src/main/java/com/example/DB/pharmacy.db";
         o.insert(URL);
    }

}