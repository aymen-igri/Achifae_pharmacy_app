package com.example;

import java.io.InputStream;
import com.example.DB.models.Vente;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader; 
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class VentesAdd {
    private Image icon;

    private Vente ven;

    private int phID;

    private Ventes vens;

    private String urldb = "jdbc:sqlite:src/main/java/com/example/DB/pharmacy.db";
    private int idc;
    private int idm;
    private int quan;
    private int pri;
    private String datev;

    @FXML
    private TextField idCli;

    @FXML
    private TextField idMed;

    @FXML
    private TextField quantité;

    @FXML
    private TextField prix;

    @FXML
    private DatePicker dateVen;

    public VentesAdd(Ventes vens,int phID){this.vens=vens;this.phID=phID;}

    public void openpageVen(ActionEvent event){
        try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ventesAdd.fxml"));
                loader.setController(this);  // Ensure FXML elements are linked
                Parent root = loader.load();

                InputStream iconStream = getClass().getResourceAsStream("/com/example/icons/add2.png");

                icon = new Image(iconStream);

                Stage stage = new Stage();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Ajouter une vente");
                stage.setFullScreen(false);
                stage.getIcons().add(icon);
                stage.centerOnScreen();
                stage.show();
        }catch(Exception e){
            showAlert(AlertType.ERROR, "Erreur", "Une erreur s'est produite", "Détails: " + e.getMessage());
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void ajouterVen(ActionEvent event){

        if(idCli.getText().isEmpty() || idMed.getText().isEmpty() || quantité.getText().isEmpty() || prix.getText().isEmpty() || dateVen.getValue() == null ) {
            showAlert(AlertType.WARNING, "Champs vides", "Veuillez remplir tous les champs", "Tous les champs doivent être remplis.");
            return;
        }
        try{
            idc=Integer.parseInt(idCli.getText());
            idm=Integer.parseInt(idMed.getText());
            quan=Integer.parseInt(quantité.getText());
            pri=Integer.parseInt(prix.getText());
            datev=dateVen.getValue().toString();

            this.ven = new Vente();
        
            ven.setClientId(idc);
            ven.setMedicamentId(idm);
            ven.setPharmacienId(phID);
            ven.setQuantity(quan);
            ven.setTotalPrice(pri);
            ven.setDate(datev);

            try {
                ven.insert(urldb);
                
                // If we get here, the insertion was successful
                System.out.println(ven.toString());
                
                showAlert(AlertType.INFORMATION, "Vente réussie", "La vente a été ajoutée avec succès", 
                          "");
    
                vens.refreshNbrVenLabel();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.close();
                
            } catch (RuntimeException e) {
                // Check if it was due to insufficient quantity
                if (ven.hasInsufficientQuantity()) {
                    showAlert(AlertType.ERROR, "Quantité insuffisante", 
                             "Impossible d'ajouter' la vente - Quantité insuffisante", 
                             "Quantité disponible: " + ven.getAvailableQuantity() + 
                             "\nQuantité demandée: " + ven.getRequestedQuantity());
                } else {
                    showAlert(AlertType.ERROR, "Erreur", "Une erreur s'est produite", "Détails: " + e.getMessage());
                }
            }
        } catch(NumberFormatException e) {
            showAlert(AlertType.ERROR, "Format invalide", "Veuillez entrer des valeurs numériques valides", 
                     "Les champs ID client, ID médicament, quantité et prix doivent être des nombres.");
        } catch(Exception e){
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
