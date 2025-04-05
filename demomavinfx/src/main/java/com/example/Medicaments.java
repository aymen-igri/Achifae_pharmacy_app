package com.example;

import java.net.URL;
import java.util.ResourceBundle;


import com.example.DB.models.Medicament;
import com.example.DB.models.Pharmacien;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.stage.Stage;

public class Medicaments {

    private Pharmacien ph ;
    private String urldb = "jdbc:sqlite:src/main/java/com/example/DB/pharmacy.db";
    private Medicament med;

    @FXML
    private Label name;
    
    @FXML
    private Label role;

    @FXML
    private Label nbrMed;

    @FXML
    private ComboBox<String> typeComboBox;

    public Medicaments(Pharmacien ph){
        this.ph = ph;
        this.med=new Medicament();
    }

    public void openpageM(ActionEvent event){
        try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/medicaments.fxml"));
                loader.setController(this);  // Ensure FXML elements are linked
                Parent root = loader.load();

                name.setText("Nom: " + ph.getLastN());
                role.setText("Rôle: " + ph.getRole());
                nbrMed.setText("LISTE DES MÉDICAMENTS("+String.valueOf(med.count(urldb))+")");

                initialize(null, null);

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setFullScreenExitHint("");
                stage.setTitle("Gestion des medicaments");
                stage.setFullScreen(false);
                stage.setMaximized(true);
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
        MedicamentsOperations m = new MedicamentsOperations(this);
        m.openpageMO(event);
    }

    public void refreshNbrMedLabel() {
        try {
            int count = med.count(urldb);
            nbrMed.setText("LISTE DES MÉDICAMENTS(" + count + ")");
        } catch (Exception e) {
            System.out.println("Error refreshing label: " + e.getMessage());
        }
    }
    
    public void openTab(ActionEvent event){
        Tableaudeboard t = new Tableaudeboard(ph);
        t.openpageT(event);
    }

    public void openCli(ActionEvent event){
        Clients c = new Clients(ph);
        c.openpageC(event);
    }

    public void openOrd(ActionEvent event){
        Ordonnances o = new Ordonnances(ph);
        o.openpageO(event);
    }

    public void openVen(ActionEvent event){
        Ventes v = new Ventes(ph);
        v.openpageV(event);
    }

    public void openRea(ActionEvent event){
        Réapprovisionnements r = new Réapprovisionnements(ph);
        r.openpageR(event);
    }

    public void openPar(ActionEvent event){
        Parametres p = new Parametres(ph);
        p.openpageP(event);
    }

    public void logOut(ActionEvent event)throws Exception{
        try{
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/signin.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setFullScreenExitHint("");
            stage.setTitle("Connection");
            stage.centerOnScreen();
            stage.show();
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
