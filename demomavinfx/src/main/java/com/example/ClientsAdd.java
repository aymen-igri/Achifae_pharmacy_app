package com.example;

import java.io.InputStream;
import javafx.scene.Node;
import java.net.URL;
import java.util.ResourceBundle;

import com.example.DB.models.Client;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class ClientsAdd {
    private Image icon;

    private Client cli;

    private Clients clients;

    private String urldb = "jdbc:sqlite:src/main/java/com/example/DB/pharmacy.db";
    private String nom;
    private String prenom;
    private String sexe;
    private String tele;
    private String email;
    private String adresse;

    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    private TextField nomcli;

    @FXML
    private TextField prencli;

    @FXML
    private TextField telecli;

    @FXML
    private TextField emailcli;

    @FXML
    private TextField adressecli;

    public ClientsAdd(Clients clients){this.clients=clients;}
    
    public void openpagecli(ActionEvent event){
        try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/clientsAdd.fxml"));
                loader.setController(this);  // Ensure FXML elements are linked
                Parent root = loader.load();

                initialize(null, null);
                InputStream iconStream = getClass().getResourceAsStream("/com/example/icons/add2.png");

                icon = new Image(iconStream);

                Stage stage = new Stage();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Ajouter un client");
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
        typeComboBox.setItems(FXCollections.observableArrayList("Homme", "Femme"));
    }


    public void ajoutercli(ActionEvent event){

        if(nomcli.getText().isEmpty() || prencli.getText().isEmpty() || telecli.getText().isEmpty() || emailcli.getText().isEmpty() || adressecli.getText().isEmpty() || typeComboBox.getValue() == null) {
            showAlert(AlertType.WARNING, "Champs vides", "Veuillez remplir tous les champs", "Tous les champs doivent être remplis.");
            return;
        }

        Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation");
        confirmAlert.setHeaderText("Confirmer l'ajout du client");
        confirmAlert.setContentText("Êtes-vous sûr de vouloir ajouter ce client?");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                // User clicked OK, proceed with the operation
                try{
                    nom = nomcli.getText();
                    prenom = prencli.getText();
                    sexe = typeComboBox.getValue();
                    tele = telecli.getText();
                    email = emailcli.getText();
                    adresse = adressecli.getText();
    
                    cli = new Client(nom, prenom, sexe, tele, email, adresse); 
    
                    cli.insert(urldb);
                    System.out.println(cli.toString());
                    
                    showAlert(AlertType.INFORMATION, "Inscription réussie", "Le client a été ajouté avec succès", "");
    
                    clients.refreshNbrMedLabel();
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.close();
                    
                } catch(Exception e){
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
