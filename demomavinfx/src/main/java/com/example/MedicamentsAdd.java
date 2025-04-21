package com.example;

import java.io.InputStream;
import java.net.URL;
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
import javafx.stage.Stage;

public class MedicamentsAdd {

    

    private Image icon;

    private Medicament m;

    private Medicaments ms;

    private String urldb = "jdbc:sqlite:src/main/java/com/example/DB/pharmacy.db";
    private String nom;
    private int quantite;
    private int prix;
    private String fourniceur;
    private String dateExpiraiton;
    private String type;

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

    public MedicamentsAdd(Medicaments ms){this.ms=ms;}
    
    public void openpageMO(ActionEvent event){
        try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/medicamentsAdd.fxml"));
                loader.setController(this);  // Ensure FXML elements are linked
                Parent root = loader.load();

                initialize(null, null);
                InputStream iconStream = getClass().getResourceAsStream("/com/example/icons/add2.png");

                icon = new Image(iconStream);

                Stage stage = new Stage();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Ajouter un médicament");
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
        typeComboBox.setItems(FXCollections.observableArrayList("Avec ordonnance", "Sans ordonnance"));
    }


    public void ajouterM(ActionEvent event){

        if(nomMed.getText().isEmpty() || quantiteMed.getText().isEmpty() || prixMed.getText().isEmpty() || fourniceurMed.getText().isEmpty() || dateExpiraitonMed.getValue() == null || typeComboBox.getValue() == null) {
            showAlert(AlertType.WARNING, "Champs vides", "Veuillez remplir tous les champs", "Tous les champs doivent être remplis.");
            return;
        }

        Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation");
        confirmAlert.setHeaderText("Confirmer l'ajout du medicament");
        confirmAlert.setContentText("Êtes-vous sûr de vouloir ajouter ce medicament?");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                try{
                    nom=nomMed.getText();
                    quantite=Integer.parseInt(quantiteMed.getText());
                    prix=Integer.parseInt(prixMed.getText());
                    fourniceur=fourniceurMed.getText();
                    dateExpiraiton=dateExpiraitonMed.getValue().toString();
                    type=typeComboBox.getValue();

                    m= new Medicament(nom,quantite,prix,dateExpiraiton,fourniceur,type);

                    m.insert(urldb);
                    System.out.println(m.toString());
                    
                    showAlert(AlertType.INFORMATION, "Inscription réussie", "le medicament ajouter avec succès", "vous devez maintenant se conecter avec ces information");

                    ms.refreshNbrMedLabel();
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

    
    private void showAlert(AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

