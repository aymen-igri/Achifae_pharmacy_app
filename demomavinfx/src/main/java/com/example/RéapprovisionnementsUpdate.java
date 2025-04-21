package com.example;

import java.io.InputStream;
import javafx.scene.control.Label;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import com.example.DB.models.Pharmacien;
import com.example.DB.models.Réapprovisionnement;

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
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class RéapprovisionnementsUpdate {

    @FXML
    private Label idRea;

    @FXML
    private Label nomMed;

    private Image icon;

    private Réapprovisionnement rea;

    private Réapprovisionnements reas;

    private Pharmacien ph;

    private String urldb = "jdbc:sqlite:src/main/java/com/example/DB/pharmacy.db";

    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    private TextField quantité;

    @FXML
    private DatePicker date;

    public RéapprovisionnementsUpdate(Réapprovisionnements reas, Pharmacien ph){this.reas=reas;this.ph=ph;}

    public void openpageReaU(ActionEvent event,Réapprovisionnement rea){
        try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/réapprovisionnementsUpdate.fxml"));
                loader.setController(this);  // Ensure FXML elements are linked
                Parent root = loader.load();

                idRea.setText("Modifier la réapprovisionnement N:" + rea.getId());

                initialize(null, null);
                InputStream iconStream = getClass().getResourceAsStream("/com/example/icons/icon3.png");

                icon = new Image(iconStream);

                Stage stage = new Stage();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Modifier une réapprovisionnement");
                stage.setFullScreen(false);
                stage.getIcons().add(icon);
                stage.centerOnScreen();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.show();

                // like a controller hhhhhhhhhhhh 
                this.rea=rea;
                nomMed.setText("Medicament: "+rea.getMedicamentN());
                quantité.setText(String.valueOf(rea.getQuantity()));
                date.setValue(LocalDate.parse(rea.getDate()));
                typeComboBox.setValue(rea.getStatus());

        }catch(Exception e){
            showAlert(AlertType.ERROR, "Erreur", "Une erreur s'est produite", "Détails: " + e.getMessage());
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        typeComboBox.setItems(FXCollections.observableArrayList("En attente", "Annulé", "Terminé"));
    }

    public void modifierRea(ActionEvent event){

        if( quantité.getText().isEmpty() || date.getValue() == null || typeComboBox.getValue() == null) {
            showAlert(AlertType.WARNING, "Champs vides", "Veuillez remplir tous les champs", "Tous les champs doivent être remplis.");
            return;
        }
        try{
            rea.setQuantity(Integer.parseInt(quantité.getText()));
            rea.setDate(date.getValue().toString());
            rea.setStatus(typeComboBox.getValue());
            rea.setPharmacienId(ph.getId());

            if (!isNumeric(quantité.getText())) {
                showAlert(AlertType.WARNING, "Valeurs invalides", 
                         "Quantité et Prix doivent être des nombres", "");
                return;
            }

            System.out.println(rea.toString());
            rea.update(urldb);
            showAlert(AlertType.INFORMATION, "Mise à jour réussie","La réapprovisionnement N:" + rea.getId() + " a été mis à jour", "");
            
            reas.refreshNbrReaLabel();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
            
        }catch(Exception e){
            showAlert(AlertType.ERROR, "Erreur", "Une erreur s'est produite", "Détails: " + e.getMessage());
            System.out.println(e.getMessage());
            e.printStackTrace();
        } 
    }

    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e) {
            return false;
        }
    }

    public void supprimerRea(ActionEvent event){
        try{
           
            rea.delete(urldb);
            showAlert(AlertType.INFORMATION, "La suppresion se fait avec succes","La réapprovisionnement N:" + rea.getId() + " a été supprimée", "");
            
            reas.refreshNbrReaLabel();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
            
            ((Stage) nomMed.getScene().getWindow()).close();
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
