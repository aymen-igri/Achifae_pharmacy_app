package com.example;

import java.io.InputStream;

import com.example.DB.models.Pharmacien;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ParametresUpdateConn {

    private Image icon;

    private Pharmacien ph;
    private String urldb = "jdbc:sqlite:src/main/java/com/example/DB/pharmacy.db";
    private SignIn s;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    public ParametresUpdateConn(Pharmacien ph){this.ph=ph;this.s=new SignIn();}

    public void openpageUC(ActionEvent event){
        try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/parametresUpdateCon.fxml"));
                loader.setController(this);  // Ensure FXML elements are linked
                Parent root = loader.load();

                InputStream iconStream = getClass().getResourceAsStream("/com/example/icons/icon3.png");

                icon = new Image(iconStream);

                Stage stage = new Stage();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Modifier les informations de connexion");
                stage.setFullScreen(false);
                stage.getIcons().add(icon);
                stage.centerOnScreen();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.show();


        }catch(Exception e){
            showAlert(AlertType.ERROR, "Erreur", "Une erreur s'est produite", "Détails: " + e.getMessage());
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void modifierPhC(ActionEvent event){

        if(username.getText().isEmpty() || password.getText().isEmpty()) {
            showAlert(AlertType.WARNING, "Champs vides", "Veuillez remplir tous les champs", "Tous les champs doivent être remplis.");
            return;
        }

        Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation");
        confirmAlert.setHeaderText("Confirmer la modification des informations de connexion");
        confirmAlert.setContentText("Êtes-vous sûr de vouloir modifier les informations de connexion?");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                try{
                    ph.setUsername(username.getText());
                    ph.setPassword(password.getText());

                    System.out.println(ph.toString());
                    ph.updateC(urldb);
                    showAlert(AlertType.INFORMATION, "Mise à jour réussie","Vous avez changé les inforamtion de connection de votre compte", "");

                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.close();
                    
                }catch(Exception e){
                    showAlert(AlertType.ERROR, "Erreur", "Une erreur s'est produite", "Détails: " + e.getMessage());
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                } 
            } else {
                // User clicked Cancel, do nothing
                System.out.println("Operation canceled by user");
            }
        });
        
    }

    public void supprimerPh(ActionEvent event){
        
    }

    private void showAlert(AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
