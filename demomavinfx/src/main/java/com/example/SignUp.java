package com.example;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
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
    private TextField gender;
    @FXML
    private TextField tele;
    @FXML
    private TextField role;
    @FXML
    private TextField username;
    @FXML
    private TextField password;
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


    public void inscrire(){
        try{
            if(nomph.getText().isEmpty()||prenph.getText().isEmpty()||gender.getText().isEmpty()||tele.getText().isEmpty()||role.getText().isEmpty()||username.getText().isEmpty()||password.getText().isEmpty()){
                showAlert(AlertType.ERROR, "Erreur de saisie","Champs vides","Veuillez remplir tous les champs.");
                return;
            }
            nameValue=nomph.getText();
            lastnValue=prenph.getText();
            genderValue=gender.getText();
            contValue=tele.getText();
            roleValue=role.getText();
            usernameValue=username.getText();
            passwordValue=password.getText();

            pharmacien = new Pharmacien(nameValue,lastnValue, genderValue, contValue, usernameValue, passwordValue, roleValue);
            pharmacien.insert("jdbc:sqlite:src/main/java/com/example/DB/pharmacy.db");

            showAlert(AlertType.INFORMATION, "Inscription réussie", "Utilisateur créé avec succès", "vous devez maintenant se conecter avec ces information");


        }catch(Exception e){
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
