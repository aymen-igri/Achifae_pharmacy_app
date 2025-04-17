package com.example;


import java.io.InputStream;
import javafx.scene.control.Label;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import com.example.DB.models.Ordonnance;
import com.example.DB.models.Pharmacien;
import com.example.DB.models.Vente;

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

public class VentesUpdate {

    @FXML
    private Label idVen;

    @FXML
    private Label nomCli;

    @FXML
    private Label nomMed;

    private Image icon;

    private Vente ven;
    private Ventes vens;

    private Pharmacien ph;

    private String urldb = "jdbc:sqlite:src/main/java/com/example/DB/pharmacy.db";

    @FXML
    private TextField quantité;

    @FXML
    private TextField prix;

    @FXML
    private DatePicker dateVen;

    public VentesUpdate(Ventes vens,Pharmacien ph){this.vens=vens;this.ph=ph;}

    public void openpageVen(ActionEvent event,Vente ven){
        try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ventesUpdate.fxml"));
                loader.setController(this);  // Ensure FXML elements are linked
                Parent root = loader.load();

                idVen.setText("Modifier le vente N:" + idVen.getId());

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
                this.ven = ven;

                nomCli.setText("Client:"+ven.getClientN());
                nomMed.setText("Med:"+ven.getMedicamentN());
                quantité.setText(String.valueOf(ven.getQuantity()));
                prix.setText(String.valueOf(ven.getTotalPrice()));
                dateVen.setValue(LocalDate.parse(ven.getDate()));


        }catch(Exception e){
            showAlert(AlertType.ERROR, "Erreur", "Une erreur s'est produite", "Détails: " + e.getMessage());
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void modifierVen(ActionEvent event){

        if(nomMed.getText().isEmpty() || nomCli.getText().isEmpty() || quantité.getText().isEmpty() || prix.getText().isEmpty() || dateVen.getValue() == null) {
            showAlert(AlertType.WARNING, "Champs vides", "Veuillez remplir tous les champs", "Tous les champs doivent être remplis.");
            return;
        }
        try{
            Vente v = new Vente();
            v.setPharmacienId(ph.getId());
            v.setQuantity(Integer.parseInt(quantité.getText()));
            v.setTotalPrice(Double.parseDouble(prix.getText()));
            v.setDate(dateVen.getValue().toString());

            if (!isNumeric(quantité.getText()) || !isNumeric(prix.getText())) {
                showAlert(AlertType.WARNING, "Valeurs invalides", 
                         "Quantité et Prix doivent être des nombres", "");
                return;
            }

            System.out.println(v.toString());
            ven.update(urldb,v);
            showAlert(AlertType.INFORMATION, "Mise à jour réussie","Le vente " + ven.getId() + " a été mis à jour", "");
            
            vens.refreshNbrVenLabel();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
            
            ((Stage) nomMed.getScene().getWindow()).close();
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

    public void supprimerVen(ActionEvent event){
        try{
           
            ven.delete(urldb);
            showAlert(AlertType.INFORMATION, "La suppresion se fait avec succes","Le vente " + ven.getId() + " a été supprimée", "");
            
            vens.refreshNbrVenLabel();

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
