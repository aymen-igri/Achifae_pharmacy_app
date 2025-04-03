package com.example;

import java.io.IOException;
import java.io.InputStream;

import com.example.DB.models.Pharmacien;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class SignIn {

    private Stage stage;
    private Image icon;

    private String usernameValue;
    private String passwordValue;

    Pharmacien ph = new Pharmacien("usernameValue","","passwordValue", "passwordValue", "usernameValue", "passwordValue", "passwordValue");

    @FXML
    private TextField username;

    @FXML
    private PasswordField passwordField;
    
    @FXML
    private TextField passwordVisible;
    
    @FXML
    private Button togglePassword;
    
    @FXML
    private Button contibutton;

    @FXML
    private Button signup;

    public SignIn() {
        InputStream iconStream = getClass().getResourceAsStream("/com/example/icons/icon4.png");
        if (iconStream == null) {
            throw new IllegalArgumentException("Icon image not found");
        }
        icon = new Image(iconStream);
    }

    public void firstpage(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/signin.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Connection");
        stage.getIcons().add(icon);
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();
    }
    
    public void inscription(ActionEvent e) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/signup.fxml"));
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Inscription");
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();
    }
    
    @FXML
    public void togglePasswordVisibility(ActionEvent event) {
        if (passwordField.isVisible()) {
            // Transfer text from password field to visible field
            passwordVisible.setText(passwordField.getText());
            passwordField.setVisible(false);
            passwordVisible.setVisible(true);
            togglePassword.setText("🔒");
        } else {
            // Transfer text from visible field to password field
            passwordField.setText(passwordVisible.getText());
            passwordField.setVisible(true);
            passwordVisible.setVisible(false);
            togglePassword.setText("👁");
        }
    }

    public void valider(ActionEvent event) {
        try {
            String passwordText = passwordField.isVisible() ? passwordField.getText() : passwordVisible.getText();
            
            if (username.getText().isEmpty() || passwordText.isEmpty()) {
                showAlert(AlertType.ERROR, "Erreur de saisie", "Champs vides", "Veuillez remplir tous les champs.");
                return;
            }
            
            usernameValue = username.getText();
            passwordValue = passwordText;

            Pharmacien ph = new Pharmacien("", "", "", "", usernameValue, passwordValue, "");

            if (ph.exist("jdbc:sqlite:src/main/java/com/example/DB/pharmacy.db")) {
                Pharmacien ph2;
                ph2 = ph.getByUserPass("jdbc:sqlite:src/main/java/com/example/DB/pharmacy.db");
                Tableaudeboard t = new Tableaudeboard(ph2);
                t.openpageT(event);
            } else {
                showAlert(AlertType.ERROR, "Erreur de saisie", "Champs vides", "nom d'utilisateur ou le mot de passe est faux.");
            }
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Erreur", "Une erreur s'est produite", "Détails: " + e.getMessage());
            System.out.println(e.getMessage());
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