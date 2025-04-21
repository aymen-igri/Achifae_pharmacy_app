package com.example;

import java.net.URL;
import java.util.ResourceBundle;

import com.example.DB.models.Pharmacien;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class Parametres {

    private Pharmacien ph;
    private String urldb = "jdbc:sqlite:src/main/java/com/example/DB/pharmacy.db";

    @FXML
    private Label name;
    
    @FXML
    private Label role;

    @FXML
    private Label fullName;

    @FXML
    private Label gender; 

    @FXML
    private Label rolePh;

    @FXML 
    private Label id;

    @FXML
    private Label contact; 

    @FXML
    private Label nbrVen;

    @FXML
    private TableView<Pharmacien> parTable;

    @FXML
    private TableColumn<Pharmacien, Integer> id_ph;

    @FXML
    private TableColumn<Pharmacien, String> nom_ph;

    @FXML
    private TableColumn<Pharmacien, String> pre_ph;

    @FXML
    private TableColumn<Pharmacien, String> role_ph;

    @FXML
    private TableColumn<Pharmacien, String> sexe;

    @FXML
    private TableColumn<Pharmacien, String> contact_ph;


    public Parametres(Pharmacien ph){this.ph=ph;}

    public void openpageP(ActionEvent event){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/parametres.fxml"));
                loader.setController(this);  // Ensure FXML elements are linked
                Parent root = loader.load();

                name.setText("Nom: " + ph.getLastN());
                role.setText("Poste: " + ph.getRole());

                rolePh.setText("Poste : "+ph.getRole());
                fullName.setText(ph.getName()+" "+ph.getLastN());
                id.setText("ID: "+ph.getId());
                gender.setText("Genre: "+ph.getGender());
                contact.setText("Contact: "+ph.getContact());

                if(ph.getRole().equals("Admin")){
                    initialize(null, null);
                }

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setFullScreenExitHint("");
                stage.setTitle("Parametres");
                stage.setFullScreen(false);
                stage.setMaximized(true);
                stage.show();
        }catch(Exception e){
            showAlert(AlertType.ERROR, "Erreur", "Une erreur s'est produite", "Détails: " + e.getMessage());
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void initialize(URL location, ResourceBundle resources) {

        //this is for the table
        initializeTable();
        loadMedicamentsData();

        parTable.setRowFactory(tv -> {
            TableRow<Pharmacien> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Pharmacien selectedpha = row.getItem();
                    PharmacienUpdate updateController = new PharmacienUpdate(this);
                    updateController.openpagePhR(null, selectedpha);
                }
            });
            return row;
        });
    }

    private void initializeTable() {
        id_ph.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        nom_ph.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        pre_ph.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLastN()));
        role_ph.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRole()));
        contact_ph.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getContact()));
        sexe.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGender())); 
    }

    private void loadMedicamentsData() {
        ObservableList<Pharmacien> pharmaciens = ph.getAll(urldb);
        parTable.setItems(pharmaciens);
    }

    public void refreshNbrParLabel() {
        try {
            name.setText("Nom: " + ph.getLastN());
            role.setText("Poste: " + ph.getRole());
            rolePh.setText("Poste: "+ph.getRole());
            fullName.setText(ph.getName()+" "+ph.getLastN());
            id.setText("ID: "+ph.getId());
            gender.setText("Genre: "+ph.getGender());
            contact.setText("Contact: "+ph.getContact());
            loadMedicamentsData();
        } catch (Exception e) {
            System.out.println("Error refreshing label: " + e.getMessage());
        } 
    }

    public void update(ActionEvent event){
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            ParametresUpdate pu = new ParametresUpdate(ph, this, stage);
            pu.openpageU(event);
        } catch(Exception e){
            System.out.println("Error in update action: " + e.getMessage());
        }
    }

    public void openTab(ActionEvent event){
        Tableaudeboard t = new Tableaudeboard(ph);
        t.openpageT(event);
    }

    public void openMed(ActionEvent event){
        Medicaments m = new Medicaments(ph);
        m.openpageM(event);
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
