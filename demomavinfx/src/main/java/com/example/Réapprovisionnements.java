package com.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class Réapprovisionnements {

    private String Nom ;
    private String Role ;

    @FXML
    private Label name;
    
    @FXML
    private Label role;

    public Réapprovisionnements(String Nom ,String Role){this.Nom=Nom;this.Role=Role;}

    public void openpageR(ActionEvent event){
        try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/réapprovisionnements.fxml"));
                loader.setController(this);  // Ensure FXML elements are linked
                Parent root = loader.load();

                name.setText("Nom: " + Nom);
                role.setText("Rôle: " + Role);

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setFullScreenExitHint("");
                stage.setTitle("Gestion des réapprovisionnements");
                stage.setFullScreen(false);
                stage.setMaximized(true);
                stage.show();
        }catch(Exception e){
            showAlert(AlertType.ERROR, "Erreur", "Une erreur s'est produite", "Détails: " + e.getMessage());
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void openTab(ActionEvent event){
        Tableaudeboard t = new Tableaudeboard(Nom,Role);
        t.openpageT(event);
    }

    public void openMed(ActionEvent event){
        Medicaments m = new Medicaments(Nom,Role);
        m.openpageM(event);
    }

    public void openCli(ActionEvent event){
        Clients c = new Clients(Nom,Role);
        c.openpageC(event);
    }

    public void openOrd(ActionEvent event){
        Ordonnances o = new Ordonnances(Nom,Role);
        o.openpageO(event);
    }
    
    public void openVen(ActionEvent event){
        Ventes v = new Ventes(Nom,Role);
        v.openpageV(event);
    }

    public void openPar(ActionEvent event){
        Parametres p = new Parametres(Nom,Role);
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
