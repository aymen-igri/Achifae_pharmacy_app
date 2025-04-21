package com.example;

import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

import com.example.DB.models.Pharmacien;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ParametresUpdateInfo {

    private Image icon;
    
    private Pharmacien ph;
    private Parametres p;
    private String urldb = "jdbc:sqlite:src/main/java/com/example/DB/pharmacy.db";

    @FXML
    private TextField nom;
    
    @FXML
    private TextField prenom;

    @FXML
    private TextField contact;

    @FXML
    private ComboBox<String> sexe;

    public ParametresUpdateInfo(Pharmacien ph,Parametres p){this.ph=ph;this.p=p;}

    public void openpageUI(ActionEvent event){
        try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/parametresUpdateInfo.fxml"));
                loader.setController(this);  // Ensure FXML elements are linked
                Parent root = loader.load();

                InputStream iconStream = getClass().getResourceAsStream("/com/example/icons/icon3.png");

                icon = new Image(iconStream);

                initialize(null, null);

                Stage stage = new Stage();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Modifier les information personnelle");
                stage.setFullScreen(false);
                stage.getIcons().add(icon);
                stage.centerOnScreen();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.show();

                nom.setText(ph.getName());
                prenom.setText(ph.getLastN());
                contact.setText(ph.getContact());
                sexe.setValue(ph.getGender());

        }catch(Exception e){
            showAlert(AlertType.ERROR, "Erreur", "Une erreur s'est produite", "Détails: " + e.getMessage());
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        sexe.setItems(FXCollections.observableArrayList("Homme", "Femme"));
    }

    public void modifierPhP(ActionEvent event){

        if(nom.getText().isEmpty() || prenom.getText().isEmpty() || contact.getText().isEmpty() || sexe.getValue() == null) {
            showAlert(AlertType.WARNING, "Champs vides", "Veuillez remplir tous les champs", "Tous les champs doivent être remplis.");
            return;
        }
        try{
            ph.setName(nom.getText());
            ph.setLastN(prenom.getText());
            ph.setContact(contact.getText());
            ph.setGender(sexe.getValue());

            System.out.println(ph.toString());
            ph.updateIP(urldb);
            showAlert(AlertType.INFORMATION, "Mise à jour réussie","Vous avez changez vos information personelle", "");
            
            p.refreshNbrParLabel();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
            
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
