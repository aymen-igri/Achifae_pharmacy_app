package com.example;

import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

import com.example.DB.models.Client;

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
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ClientsUpdate {
    @FXML
    private Label idCli;

    private Image icon;

    private Client cli;

    private Clients clients;

    private String urldb = "jdbc:sqlite:src/main/java/com/example/DB/pharmacy.db";

    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    private TextField nomCli;

    @FXML
    private TextField prenCli;

    @FXML
    private TextField teleCli;

    @FXML
    private TextField emailCli;

    @FXML
    private TextField adresseCli;



    public ClientsUpdate(Clients clients){this.clients=clients;}

    public void openpagecliu(ActionEvent event,Client c){
        try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/clientsUpdate.fxml"));
                loader.setController(this);  // Ensure FXML elements are linked
                Parent root = loader.load();

                idCli.setText("Modifier le client N:" + c.getId());

                initialize(null, null);
                InputStream iconStream = getClass().getResourceAsStream("/com/example/icons/icon3.png");

                icon = new Image(iconStream);

                Stage stage = new Stage();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Modifier un client");
                stage.setFullScreen(false);
                stage.getIcons().add(icon);
                stage.centerOnScreen();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.show();

                // like a controller hhhhhhhhhhhh 
                this.cli = c;

                nomCli.setText(cli.getName());
                prenCli.setText(cli.getLastN());
                teleCli.setText(String.valueOf(cli.getPhoneNumber()));
                emailCli.setText(cli.getEmail());
                adresseCli.setText(cli.getAddress());
                typeComboBox.setValue(cli.getGender());
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


    public void modifiercli(ActionEvent event){

        if(nomCli.getText().isEmpty() || prenCli.getText().isEmpty() || teleCli.getText().isEmpty() || emailCli.getText().isEmpty() || adresseCli.getText().isEmpty() || typeComboBox.getValue() == null) {
            showAlert(AlertType.WARNING, "Champs vides", "Veuillez remplir tous les champs", "Tous les champs doivent être remplis.");
            return;
        }
        try{
            cli.setName(nomCli.getText());
            cli.setLastN(prenCli.getText());
            cli.setPhoneNumber(Integer.parseInt(teleCli.getText()));
            cli.setEmail(emailCli.getText());
            cli.setAddress(adresseCli.getText());
            cli.setGender(typeComboBox.getValue());

            if (!isNumeric(teleCli.getText()) || !isNumeric(teleCli.getText())) {
                showAlert(AlertType.WARNING, "Valeurs invalides", 
                         "Le tele doit être des nombres", ""); 
                return;
            }

            System.out.println(cli.toString());
            cli.update(urldb);
            showAlert(AlertType.INFORMATION, "Mise à jour réussie","Le client " + cli.getId() + " a été mis à jour", "");
            
            clients.refreshNbrMedLabel();
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
    
    private void showAlert(AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
