package com.example;

import java.io.InputStream;


import com.example.DB.models.Pharmacien;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ParametresUpdate {

    private Image icon;

    private Pharmacien ph;
    private String urldb = "jdbc:sqlite:src/main/java/com/example/DB/pharmacy.db";

    private Parametres p;
    private Stage s;

    public ParametresUpdate(Pharmacien ph,Parametres p,Stage s){this.ph=ph;this.p=p;this.s=s;}


    public void openpageU(ActionEvent event){
        try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/parametresUpdate.fxml"));
                loader.setController(this);  // Ensure FXML elements are linked
                Parent root = loader.load();

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

        }catch(Exception e){
            showAlert(AlertType.ERROR, "Erreur", "Une erreur s'est produite", "Détails: " + e.getMessage());
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void updateinfo(ActionEvent event){
        ParametresUpdateInfo pui = new ParametresUpdateInfo(ph,p);
        pui.openpageUI(event);
    }

    public void updateCon(ActionEvent event){
        ParametresUpdateConn puc = new ParametresUpdateConn(ph);
        puc.openpageUC(event);
    }

    private void showAlert(AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }



}
