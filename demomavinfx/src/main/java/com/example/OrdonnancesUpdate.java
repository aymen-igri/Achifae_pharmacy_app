package com.example;

import java.io.InputStream;
import javafx.scene.control.Label;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import com.example.DB.models.Medicament;
import com.example.DB.models.Ordonnance;

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

public class OrdonnancesUpdate {
    
    @FXML
    private Label idOrd;

    private Image icon;

    private Ordonnance ord;

    private Ordonnances ords;

    private String urldb = "jdbc:sqlite:src/main/java/com/example/DB/pharmacy.db";

    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    private TextField idCli;

    @FXML
    private TextField idMed;

    @FXML
    private TextField nomDoc;

    @FXML
    private TextField contDoc;

    @FXML
    private DatePicker dateOrd;

    @FXML
    private DatePicker dateExp;

    public OrdonnancesUpdate(Ordonnances ords){this.ords=ords;}

    public void openpageOrd(ActionEvent event,Ordonnance ord){
        try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ordonnancesUpdate.fxml"));
                loader.setController(this);  // Ensure FXML elements are linked
                Parent root = loader.load();

                idOrd.setText("Modifier l'ordonnance N:" + ord.getId());

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
                this.ord = ord;

                idCli.setText(String.valueOf(ord.getClientId()));
                idMed.setText(String.valueOf(ord.getMedicamentId()));
                nomDoc.setText(ord.getDoctorName());
                contDoc.setText(ord.getDoctorContact());
                dateOrd.setValue(LocalDate.parse(ord.getDate()));
                dateExp.setValue(LocalDate.parse(ord.getExpirationDate()));
                typeComboBox.setValue(ord.getStatus());

        }catch(Exception e){
            showAlert(AlertType.ERROR, "Erreur", "Une erreur s'est produite", "Détails: " + e.getMessage());
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        typeComboBox.setItems(FXCollections.observableArrayList("Validée", "Expirée"));
    }

    public void modifierOrd(ActionEvent event){

        if(idCli.getText().isEmpty() || idMed.getText().isEmpty() || nomDoc.getText().isEmpty() || contDoc.getText().isEmpty() || dateOrd.getValue() == null || dateExp.getValue() == null || typeComboBox.getValue() == null) {
            showAlert(AlertType.WARNING, "Champs vides", "Veuillez remplir tous les champs", "Tous les champs doivent être remplis.");
            return;
        }
        try{
            ord.setClientId(Integer.parseInt(idCli.getText()));
            ord.setMedicamentId(Integer.parseInt(idMed.getText()));
            ord.setDoctorName(nomDoc.getText());
            ord.setDoctorContact(contDoc.getText());
            ord.setDate(dateOrd.getValue().toString());
            ord.setExpirationDate(dateExp.getValue().toString());
            ord.setStatus(typeComboBox.getValue());

            System.out.println(ord.toString());
            ord.update(urldb);
            showAlert(AlertType.INFORMATION, "Mise à jour réussie","Le médicament " + ord.getId() + " a été mis à jour", "");
            
            ords.refreshNbrOrdLabel();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
            
            ((Stage) idCli.getScene().getWindow()).close();
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
