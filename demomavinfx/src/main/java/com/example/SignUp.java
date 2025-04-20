package com.example;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


import com.example.DB.models.Pharmacien;

public class SignUp {
    
    private Stage stage;
    private Scene scene;
    private Parent root;
    private SignIn signIn;

    private String nameValue;
    private String lastnValue;
    private String genderValue;
    private String contValue;
    private String roleValue;
    private String usernameValue;
    private String passwordValue;

    private Pharmacien pharmacien;

    private String URL = "/com/example/DB/pharmacy.db";

    @FXML
    private TextField nomph;
    @FXML
    private TextField prenph;
    @FXML
    private ComboBox<String> gender;
    @FXML
    private TextField tele;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Button create;
    @FXML
    private Button retour;


    public void connection(ActionEvent e)throws IOException{
        root = FXMLLoader.load(getClass().getResource("/com/example/signin.fxml"));
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();
    }

    public void initialize(){
        gender.setItems(FXCollections.observableArrayList("Homme", "Femme"));
    }

    public void inscrire(ActionEvent event) {
    try {
        // Check for empty fields
        if (nomph.getText().isEmpty() || prenph.getText().isEmpty() || 
            gender.getValue() == null || tele.getText().isEmpty() || 
            username.getText().isEmpty() || password.getText().isEmpty()) {
            showAlert(AlertType.ERROR, "Erreur de saisie", "Champs vides", "Veuillez remplir tous les champs.");
            return;
        }

        nameValue = nomph.getText();
        lastnValue = prenph.getText();
        genderValue = gender.getValue();
        roleValue = "Stagiere";
        contValue = tele.getText();
        usernameValue = username.getText();
        passwordValue = password.getText();

        // Check if username already exists
        if (usernameExists(usernameValue)) {
            showAlert(AlertType.ERROR, "Erreur", "Nom d'utilisateur déjà utilisé", 
                     "Veuillez choisir un autre nom d'utilisateur.");
            return;
        }

        // If username doesn't exist, proceed with insertion
        pharmacien = new Pharmacien(nameValue, lastnValue, genderValue, contValue, usernameValue, passwordValue, roleValue);
        pharmacien.insert("jdbc:sqlite:src/main/java/com/example/DB/pharmacy.db");

        showAlert(AlertType.INFORMATION, "Inscription réussie", "Utilisateur créé avec succès", 
                 "Vous devez maintenant vous connecter avec ces informations.");
        redirectToSignIn(event);
    } catch (Exception e) {
        showAlert(AlertType.ERROR, "Erreur", "Une erreur s'est produite", "Détails: " + e.getMessage());
        e.printStackTrace();
    }
}

    // Method to check if username already exists
    private boolean usernameExists(String username) {
        String url = "jdbc:sqlite:src/main/java/com/example/DB/pharmacy.db";
        String sql = "SELECT COUNT(*) FROM Pharmacien WHERE username_ph = ?";
        
        try (Connection conn = DriverManager.getConnection(url);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }

    private void redirectToSignIn(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/signin.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Connection");
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            showAlert(AlertType.ERROR, "Erreur", "Impossible de charger la page de connexion", 
                     "Détails: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
