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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PharmacienUpdate {

    private Image icon;

    private Pharmacien ph;

    private Parametres pr;

    private String urldb = "jdbc:sqlite:src/main/java/com/example/DB/pharmacy.db";

    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    private Label name;

    @FXML
    private Label lastN;

    @FXML
    private Label poste;

    public PharmacienUpdate(Parametres pr){this.pr=pr;}

    public void openpagePhR(ActionEvent event, Pharmacien ph){
        try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/pharmacienUpdate.fxml"));
                loader.setController(this);  // Ensure FXML elements are linked
                Parent root = loader.load();

                initialize(null, null);
                InputStream iconStream = getClass().getResourceAsStream("/com/example/icons/icon3.png");

                icon = new Image(iconStream);

                Stage stage = new Stage();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Modifier un médicament");
                stage.setFullScreen(false);
                stage.getIcons().add(icon);
                stage.centerOnScreen();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.show();

                // like a controller hhhhhhhhhhhh 
                this.ph=ph;
                poste.setText("Modifier le poste du pharmacien N: "+ph.getId());
                name.setText("Nom: "+ph.getName());
                lastN.setText("Prenom: "+ph.getLastN());
                typeComboBox.setValue(ph.getRole());

        }catch(Exception e){
            showAlert(AlertType.ERROR, "Erreur", "Une erreur s'est produite", "Détails: " + e.getMessage());
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void initialize(URL location, ResourceBundle resources) {
        typeComboBox.setItems(FXCollections.observableArrayList("Admin","CDI","Stagere"));
    }

    public void modifierPh(ActionEvent event){

        if( typeComboBox.getValue() == null) {
            showAlert(AlertType.WARNING, "Champs vides", "Veuillez remplir tous les champs", "Tous les champs doivent être remplis.");
            return;
        }
        try{
            ph.setRole(typeComboBox.getValue());

            System.out.println(ph.toString());
            ph.updateR(urldb);
            showAlert(AlertType.INFORMATION, "Mise à jour réussie","Le médicament " + ph.getId() + " a été mis à jour", "");
            
            pr.refreshNbrParLabel();

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
