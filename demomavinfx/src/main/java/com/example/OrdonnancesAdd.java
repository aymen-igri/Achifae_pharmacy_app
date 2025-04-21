package com.example;

import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

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
import javafx.stage.Stage;

public class OrdonnancesAdd {
    private Image icon;

    private Ordonnance ord;

    private Ordonnances ords;

    private String urldb = "jdbc:sqlite:src/main/java/com/example/DB/pharmacy.db";
    private int idc;
    private int idm;
    private String nomd;
    private String ctd;
    private String dateo;
    private String datee;
    private String etat;


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

    public OrdonnancesAdd(Ordonnances ords){this.ords=ords;}
    
    public void openpageOrd(ActionEvent event){
        try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ordonnancesAdd.fxml"));
                loader.setController(this);  // Ensure FXML elements are linked
                Parent root = loader.load();

                initialize(null, null);
                InputStream iconStream = getClass().getResourceAsStream("/com/example/icons/add2.png");

                icon = new Image(iconStream);

                Stage stage = new Stage();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Ajouter une ordonnances");
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
        typeComboBox.setItems(FXCollections.observableArrayList("Expirée", "Validée"));
    }


    public void ajouterOrd(ActionEvent event){

        if(idCli.getText().isEmpty() || idMed.getText().isEmpty() || nomDoc.getText().isEmpty() || contDoc.getText().isEmpty() || dateOrd.getValue() == null || dateExp.getValue() == null ||typeComboBox.getValue() == null) {
            showAlert(AlertType.WARNING, "Champs vides", "Veuillez remplir tous les champs", "Tous les champs doivent être remplis.");
            return;
        }
        try{
            idc=Integer.parseInt(idCli.getText());
            idm=Integer.parseInt(idMed.getText());
            nomd=nomDoc.getText();
            ctd=contDoc.getText();
            datee=dateExp.getValue().toString();
            dateo=dateOrd.getValue().toString();
            etat=typeComboBox.getValue();

            this.ord = new Ordonnance();
            
            ord.setClientId(idc);
            ord.setMedicamentId(idm);
            ord.setDoctorName(nomd);
            ord.setDoctorContact(ctd);
            ord.setDate(dateo);
            ord.setExpirationDate(datee);
            ord.setStatus(etat);
            
            ord.insert(urldb);
            System.out.println(ord.toString());
            
            showAlert(AlertType.INFORMATION, "Inscription réussie", "le ordonnance ajouté avec succès", "");

            ords.refreshNbrOrdLabel();
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
