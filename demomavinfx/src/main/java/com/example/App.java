package com.example;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        SignIn signIn = new SignIn();
        signIn.firstpage(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}