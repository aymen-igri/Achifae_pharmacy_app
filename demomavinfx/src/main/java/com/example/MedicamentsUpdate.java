package com.example;

import java.io.InputStream;
import javafx.scene.control.Label;
import java.net.URL; 
import java.time.LocalDate;
import java.util.ResourceBundle;

import com.example.DB.models.Medicament;

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


public class MedicamentsUpdate {

    @FXML
    private Label idMed;

    private Image icon;

    private Medicament m;

    private Medicaments ms;

    private String urldb = "jdbc:sqlite:src/main/java/com/example/DB/pharmacy.db";

    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    private TextField nomMed;

    @FXML
    private TextField quantiteMed;

    @FXML
    private TextField prixMed;

    @FXML
    private TextField fourniceurMed;

    @FXML
    private DatePicker dateExpiraitonMed;


    public MedicamentsUpdate(Medicaments ms){this.ms=ms;}

    public void openpageMU(ActionEvent event,Medicament m){
        try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/medicamentsUpdate.fxml"));
                loader.setController(this);  // Ensure FXML elements are linked
                Parent root = loader.load();

                idMed.setText("Modifier le medicament N:" + m.getId());

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
                this.m = m;

                nomMed.setText(m.getName());
                quantiteMed.setText(String.valueOf(m.getQuantity()));
                prixMed.setText(String.valueOf(m.getPrice()));
                fourniceurMed.setText(m.getSupplier());
                dateExpiraitonMed.setValue(LocalDate.parse(m.getExpirationDate()));
                typeComboBox.setValue(m.getType());

        }catch(Exception e){
            showAlert(AlertType.ERROR, "Erreur", "Une erreur s'est produite", "Détails: " + e.getMessage());
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        typeComboBox.setItems(FXCollections.observableArrayList("Avec ordonnance", "Sans ordonnance"));
    }


    public void modifierM(ActionEvent event){

        if(nomMed.getText().isEmpty() || quantiteMed.getText().isEmpty() || prixMed.getText().isEmpty() || fourniceurMed.getText().isEmpty() || dateExpiraitonMed.getValue() == null || typeComboBox.getValue() == null) {
            showAlert(AlertType.WARNING, "Champs vides", "Veuillez remplir tous les champs", "Tous les champs doivent être remplis.");
            return;
        }

        Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation");
        confirmAlert.setHeaderText("Confirmer la modification du medicament");
        confirmAlert.setContentText("Êtes-vous sûr de vouloir modifier ce medicament?");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                try{
                    m.setName(nomMed.getText());
                    m.setQuantity(Integer.parseInt(quantiteMed.getText()));
                    m.setPrice(Double.parseDouble(prixMed.getText()));
                    m.setSupplier(fourniceurMed.getText());
                    m.setExpirationDate(dateExpiraitonMed.getValue().toString());
                    m.setType(typeComboBox.getValue());

                    if (!isNumeric(quantiteMed.getText()) || !isNumeric(prixMed.getText())) {
                        showAlert(AlertType.WARNING, "Valeurs invalides", 
                                "Quantité et Prix doivent être des nombres", "");
                        return;
                    }

                    System.out.println(m.toString());
                    m.update(urldb);
                    showAlert(AlertType.INFORMATION, "Mise à jour réussie","Le médicament " + m.getId() + " a été mis à jour", "");
                    
                    ms.refreshNbrMedLabel();

                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.close();
                    
                    ((Stage) nomMed.getScene().getWindow()).close();
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
 
    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e) {
            return false;
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
