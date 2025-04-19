package com.example;

import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

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
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class RéapprovisionnementsAdd {
    private Image icon;

    private Réapprovisionnement rea;

    private int phID;

    private Réapprovisionnements reas;

    private String urldb = "jdbc:sqlite:src/main/java/com/example/DB/pharmacy.db";
    private int idm;
    private int idp;
    private int quan;
    private String date;
    private String stat;

    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    private TextField idMed;

    @FXML
    private TextField quantité;

    public RéapprovisionnementsAdd(Réapprovisionnements reas, int phID){this.reas=reas;this.phID=phID;}

    public void openpageRea(ActionEvent event){
        try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/réapprovisionnementsAdd.fxml"));
                loader.setController(this);  // Ensure FXML elements are linked
                Parent root = loader.load();

                initialize(null, null);

                InputStream iconStream = getClass().getResourceAsStream("/com/example/icons/add2.png");

                icon = new Image(iconStream);

                Stage stage = new Stage();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Ajouter une réapprovisionnement");
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

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        typeComboBox.setItems(FXCollections.observableArrayList("En attente", "Annulé", "Terminé"));
    }

    public void ajouterRea(ActionEvent event){

        if(idMed.getText().isEmpty() || quantité.getText().isEmpty() || typeComboBox.getValue() == null || typeComboBox.getValue().isEmpty()) {
            showAlert(AlertType.WARNING, "Champs vides", "Veuillez remplir tous les champs", "Tous les champs doivent être remplis.");
            return;
        }
        try{
            idm=Integer.parseInt(idMed.getText());
            quan=Integer.parseInt(quantité.getText());
            stat=typeComboBox.getValue();

            date=LocalDate.now().toString();

            this.rea = new Réapprovisionnement();
        
            rea.setMedicamentId(idm);
            rea.setPharmacienId(phID);
            rea.setQuantity(quan);
            rea.setStatus(stat);
            rea.setDate(date);

            rea.insert(urldb);
                
                // If we get here, the insertion was successful
            System.out.println(rea.toString());
                
            showAlert(AlertType.INFORMATION, "Vente réussie", "La vente a été créée avec succès", 
                          "La vente a été ajoutée à la base de données.");
    
            reas.refreshNbrReaLabel();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
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
