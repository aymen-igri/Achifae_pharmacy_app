package com.example;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import com.example.DB.models.Pharmacien;
import com.example.DB.models.Réapprovisionnement;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class Réapprovisionnements {

    private Pharmacien ph ;
    private String urldb = "jdbc:sqlite:src/main/java/com/example/DB/pharmacy.db";
    private Réapprovisionnement rea;

    @FXML
    private Label name;
    
    @FXML
    private Label role;

    @FXML
    private Label nbrRea;

    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    private TableView<Réapprovisionnement> reaTable;

    @FXML
    private TableColumn<Réapprovisionnement, Integer> id_r;

    @FXML
    private TableColumn<Réapprovisionnement, String> nom_med_r;

    @FXML
    private TableColumn<Réapprovisionnement, String> nom_pha_r;

    @FXML
    private TableColumn<Réapprovisionnement, Integer> quantite_r;

    @FXML
    private TableColumn<Réapprovisionnement, String> date_r;

    @FXML
    private TableColumn<Réapprovisionnement, String> status_r;

    @FXML
    private TextField searchNomMedField;

    @FXML
    private TextField searchNomParField;

    @FXML
    private TextField searchQuanField;
    
    @FXML
    private DatePicker searchDateField;

    @FXML
    private FilteredList<Réapprovisionnement> filteredRéapprovisionnements;

    public Réapprovisionnements(Pharmacien ph){this.ph=ph;}

    public void openpageR(ActionEvent event){
        try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/réapprovisionnements.fxml"));
                loader.setController(this);  // Ensure FXML elements are linked
                Parent root = loader.load();

                name.setText("Nom: " + ph.getLastN());
                role.setText("Poste: " + ph.getRole());
                this.rea=new Réapprovisionnement();
                System.out.println(rea.count(urldb)); // Ensure parentheses are balanced
                nbrRea.setText("LISTE DES REAPPRO.("+String.valueOf(rea.count(urldb))+")");
                
                initialize(null, null);

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

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        typeComboBox.setItems(FXCollections.observableArrayList("","En attente", "Annulé", "Terminé"));

        //this is for the table
        initializeTable();
        loadRéapprovisionementData();

        //this is for the search action
        setupRealTimeFiltering();

        //this for the update action
        reaTable.setRowFactory(tv -> {
            TableRow<Réapprovisionnement> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Réapprovisionnement selectedMed = row.getItem();
                    RéapprovisionnementsUpdate updateController = new RéapprovisionnementsUpdate(this,ph);
                    updateController.openpageReaU(null, selectedMed);
                }
            });
            return row;
        });
    }

    private void initializeTable() {
        id_r.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        nom_med_r.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMedicamentN()));
        nom_pha_r.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPharmacienN()));
        quantite_r.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());
        date_r.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate()));
        status_r.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));
    }

    private void loadRéapprovisionementData() {
        ObservableList<Réapprovisionnement> réapprovisionnements = rea.getAll(urldb);
        reaTable.setItems(réapprovisionnements);
        setupRealTimeFiltering();
    }

    private void setupRealTimeFiltering() {
        filteredRéapprovisionnements = new FilteredList<>(reaTable.getItems(), p -> true);

        SortedList<Réapprovisionnement> sortedList = new SortedList<>(filteredRéapprovisionnements);
        sortedList.comparatorProperty().bind(reaTable.comparatorProperty());
        reaTable.setItems(sortedList);
        
        // Add listeners to all search fields
        searchNomMedField.textProperty().addListener((obs, oldVal, newVal) -> updateFilter());
        searchNomParField.textProperty().addListener((obs, oldVal, newVal) -> updateFilter());
        searchQuanField.textProperty().addListener((obs, oldVal, newVal) -> updateFilter());
        searchDateField.valueProperty().addListener((obs, oldVal, newVal) -> updateFilter());
        typeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> updateFilter());
    }

    private void updateFilter() {
        filteredRéapprovisionnements.setPredicate(réapprovisionnement -> {
            // If all search fields are empty, show all medicaments
            if (allFieldsEmpty()) {
                return true;
            }
            
            // Check each field
            boolean matchesNomMed = matchesString(réapprovisionnement.getMedicamentN(), searchNomMedField.getText());
            boolean matchesNomPha = matchesString(réapprovisionnement.getPharmacienN(), searchNomParField.getText());
            boolean matchesQuantite = matchesNumber(réapprovisionnement.getQuantity(), searchQuanField.getText());
            boolean matchesDate = matchesDate(réapprovisionnement.getDate(), searchDateField.getValue());
            boolean matchesStatus = matchesType(réapprovisionnement.getStatus(), typeComboBox.getValue());
            
            return matchesNomMed && matchesNomPha && matchesQuantite && 
                   matchesDate && matchesStatus;
        });
    }

    private boolean allFieldsEmpty() {
        return (searchNomMedField.getText() == null || searchNomMedField.getText().isEmpty()) &&
               (searchNomParField.getText() == null || searchNomParField.getText().isEmpty()) &&
               (searchQuanField.getText() == null || searchQuanField.getText().isEmpty()) &&
               (searchDateField.getValue() == null) &&
               (typeComboBox.getValue() == null || typeComboBox.getValue().isEmpty());
    }

    private boolean matchesString(String fieldValue, String searchValue) {
        if (searchValue == null || searchValue.isEmpty()) {
            return true;
        }
        String[] words = fieldValue.toLowerCase().split("\\s+");
        for (String word : words) {
            if (word.startsWith(searchValue.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesNumber(Number fieldValue, String searchValue) {
        if (searchValue == null || searchValue.isEmpty()) {
            return true;
        }else if (fieldValue instanceof Integer) {
            int searchNum = Integer.parseInt(searchValue);
            return fieldValue.intValue() == searchNum;
        }
        // For double prices
        else if (fieldValue instanceof Double) {
            double searchNum = Double.parseDouble(searchValue);
            return fieldValue.doubleValue() == searchNum;
        }
        return String.valueOf(fieldValue).equals(searchValue);
    }

    private boolean matchesDate(String fieldValue, LocalDate searchValue) {
        if (searchValue == null) {
            return true;
        }
        String formattedDate = searchValue.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return fieldValue.contains(formattedDate);
    }

    private boolean matchesType(String fieldValue, String searchValue) {
        if (searchValue == null || searchValue.isEmpty()) {
            return true;
        }
        return fieldValue.equals(searchValue);
    }

    public void ajouterRea(ActionEvent event) {
        try {
            // Refresh the label to ensure the count operation is completed
            refreshNbrReaLabel();
    
            // Open the insert window
            RéapprovisionnementsAdd m = new RéapprovisionnementsAdd(this,ph.getId());
            m.openpageRea(event);
        } catch (Exception e) {
            System.out.println("Error in ajouterM: " + e.getMessage());
        }
    }

    public void refreshNbrReaLabel() {
        try {
            int count = rea.count(urldb);
            nbrRea.setText("LISTE DES REAPPRO.(" + count + ")");
            loadRéapprovisionementData();
        } catch (Exception e) {
            System.out.println("Error refreshing label: " + e.getMessage());
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

    public void openPar(ActionEvent event){
        Parametres p = new Parametres(ph);
        p.openpageP(event);
    }

    public void logOut(ActionEvent event)throws Exception{
        Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation");
        confirmAlert.setHeaderText("Confirmer la deconnexion");
        confirmAlert.setContentText("Êtes-vous sûr de vouloir deconnecter?");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
               try{
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/signin.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Connection");
            stage.setMaximized(false);
            stage.setResizable(false);
            stage.sizeToScene();
            stage.centerOnScreen();
            stage.show();
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
