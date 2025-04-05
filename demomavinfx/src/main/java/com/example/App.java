package com.example;

import javafx.application.Application;
import javafx.stage.Stage;
import com.example.DB.models.DatabaseManager;
import java.sql.SQLException;

/**
 * JavaFX App
 */
public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        SignIn signIn = new SignIn();
        signIn.firstpage(primaryStage);
    }

    @Override
    public void stop() {
        // No need to close connection here anymore
        System.out.println("Application is shutting down");
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}