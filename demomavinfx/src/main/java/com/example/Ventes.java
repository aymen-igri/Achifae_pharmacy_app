package com.example;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import com.example.DB.models.Pharmacien;
import com.example.DB.models.Vente;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class Ventes {

    private Pharmacien ph;
    private String urldb = "jdbc:sqlite:src/main/java/com/example/DB/pharmacy.db";
    private Vente ven;

    @FXML
    private Label name;
    
    @FXML
    private Label role;

    @FXML
    private Label nbrVen;

    @FXML
    private TableView<Vente> venTable;

    @FXML
    private TableColumn<Vente, Integer> id_ven;

    @FXML
    private TableColumn<Vente, String> nom_cli_ven;

    @FXML
    private TableColumn<Vente, String> nom_med_ven;

    @FXML
    private TableColumn<Vente, String> nom_ph_ven;

    @FXML
    private TableColumn<Vente, Integer> quantité_ven;

    @FXML
    private TableColumn<Vente, Double> prix_ven;

    @FXML
    private TableColumn<Vente, String> date_ven;

    @FXML
    private TextField searchNomCliField;

    @FXML
    private TextField searchNomMedField;

    @FXML
    private TextField searchNomParField;

    @FXML
    private TextField searchQuanField;

    @FXML
    private TextField searchPrixField;

    @FXML
    private DatePicker searchDateField;

    @FXML
    private FilteredList<Vente> filteredVentes;


    public Ventes(Pharmacien ph){this.ph=ph;}

    public void openpageV(ActionEvent event){
        try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ventes.fxml"));
                loader.setController(this);  // Ensure FXML elements are linked
                Parent root = loader.load();

                name.setText("Nom: " + ph.getLastN());
                role.setText("Poste: " + ph.getRole());
                this.ven=new Vente();
                nbrVen.setText("LISTE DES VENTES("+String.valueOf(ven.count(urldb))+")");
                initialize(null, null);

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);

                stage.setScene(scene);
                stage.setFullScreenExitHint("");
                stage.setTitle("Gestion des ventes");
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

        //this is for the table
        initializeTable();
        loadMedicamentsData();

        //this is for the search action
        setupRealTimeFiltering();

        //this for the update action
        venTable.setRowFactory(tv -> {
            TableRow<Vente> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Vente selectedOrd = row.getItem();
                    VentesUpdate updateController = new VentesUpdate(this,ph);
                    updateController.openpageVen(null, selectedOrd);
                }
            });
            return row;
        });
    }

    private void initializeTable() {
        id_ven.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        nom_cli_ven.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClientN()));
        nom_med_ven.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMedicamentN()));
        nom_ph_ven.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPharmacienN()));
        quantité_ven.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());
        prix_ven.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getTotalPrice()).asObject());
        date_ven.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate()));
    }

    private void loadMedicamentsData() {
        ObservableList<Vente> ventes = ven.getAll(urldb);
        venTable.setItems(ventes);
        setupRealTimeFiltering();
    }

    private void setupRealTimeFiltering() {
        filteredVentes = new FilteredList<>(venTable.getItems(), p -> true);

        SortedList<Vente> sortedList = new SortedList<>(filteredVentes);
        sortedList.comparatorProperty().bind(venTable.comparatorProperty());
        venTable.setItems(sortedList);
        
        // Add listeners to all search fields
        searchNomCliField.textProperty().addListener((obs, oldVal, newVal) -> updateFilter());
        searchNomMedField.textProperty().addListener((obs, oldVal, newVal) -> updateFilter());
        searchNomParField.textProperty().addListener((obs, oldVal, newVal) -> updateFilter());
        searchQuanField.textProperty().addListener((obs, oldVal, newVal) -> updateFilter());
        searchPrixField.textProperty().addListener((obs, oldVal, newVal) -> updateFilter());
        searchDateField.valueProperty().addListener((obs, oldVal, newVal) -> updateFilter());
    }

    private void updateFilter() {
        filteredVentes.setPredicate(vente -> {
            // If all search fields are empty, show all medicaments
            if (allFieldsEmpty()) {
                return true;
            }
            
            // Check each field
            boolean matchesNomCli = matchesString(vente.getClientN(), searchNomCliField.getText());
            boolean matchesNomMed = matchesString(vente.getMedicamentN(), searchNomMedField.getText());
            boolean matchesNomPh = matchesString(vente.getPharmacienN(), searchNomParField.getText());
            boolean matchesQuan = matchesNumber(vente.getQuantity(), searchQuanField.getText());
            boolean matchesPrix = matchesNumber(vente.getTotalPrice(), searchPrixField.getText());
            boolean matchesDate = matchesDate(vente.getDate(), searchDateField.getValue());

            
            return matchesNomCli && matchesNomMed && matchesNomPh && 
                   matchesDate && matchesQuan && matchesPrix;
        });
    }

    private boolean allFieldsEmpty() {
        return (searchNomCliField.getText() == null || searchNomCliField.getText().isEmpty()) &&
               (searchNomMedField.getText() == null || searchNomMedField.getText().isEmpty()) &&
               (searchNomParField.getText() == null || searchNomParField.getText().isEmpty()) &&
               (searchQuanField.getText() == null || searchQuanField.getText().isEmpty()) &&
               (searchPrixField.getText() == null) && (searchPrixField.getText() == null)&&
               (searchDateField.getValue() == null);

               
    }

    private boolean matchesNumber(Number fieldValue, String searchValue) {
        if (searchValue == null || searchValue.isEmpty()) {
            return true;
        }
        
        try {
            if (fieldValue instanceof Integer) {
                int searchNum = Integer.parseInt(searchValue);
                return fieldValue.intValue() == searchNum;
            }
            // For double prices
            else if (fieldValue instanceof Double) {
                double searchNum = Double.parseDouble(searchValue);
                return fieldValue.doubleValue() == searchNum;
            }
            return String.valueOf(fieldValue).equals(searchValue);
        } catch (NumberFormatException e) {
            // Return false if the input isn't a valid number
            return false;
        }
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

    private boolean matchesDate(String fieldValue, LocalDate searchValue) {
        if (searchValue == null) {
            return true;
        }
        String formattedDate = searchValue.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return fieldValue.contains(formattedDate);
    }

    public void ajouterVen(ActionEvent event) {
        try {
            // Refresh the label to ensure the count operation is completed
            refreshNbrVenLabel();
    
            // Open the insert window
            VentesAdd ven = new VentesAdd(this,this.ph.getId());
            ven.openpageVen(event);
        } catch (Exception e) {
            System.out.println("Error in ajouterM: " + e.getMessage());
        }
    }

    public void refreshNbrVenLabel() {
        try {
            int count = ven.count(urldb);
            nbrVen.setText("LISTE DES VENTES(" + count + ")");
            loadMedicamentsData();
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
