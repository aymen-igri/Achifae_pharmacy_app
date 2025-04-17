package com.example;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import com.example.DB.models.Ordonnance;
import com.example.DB.models.Pharmacien;

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

public class Ordonnances {

    private Pharmacien ph;
    private String urldb = "jdbc:sqlite:src/main/java/com/example/DB/pharmacy.db";
    private Ordonnance ord;

    @FXML
    private Label name;
    
    @FXML
    private Label role;

    @FXML
    private Label nbrOrd;

    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    private TableView<Ordonnance> ordTable;

    @FXML
    private TableColumn<Ordonnance, Integer> id_ord;

    @FXML
    private TableColumn<Ordonnance, String> nom_cli_ord;

    @FXML
    private TableColumn<Ordonnance, String> nom_med_ord;

    @FXML
    private TableColumn<Ordonnance, String> nom_doc_ord;

    @FXML
    private TableColumn<Ordonnance, String> contact_doc_ord;

    @FXML
    private TableColumn<Ordonnance, String> date_ord;

    @FXML
    private TableColumn<Ordonnance, String> date_exp_ord;

    @FXML
    private TableColumn<Ordonnance, String> etat_ord;

    @FXML
    private TextField searchNomCliField;

    @FXML
    private TextField searchNomMedField;

    @FXML
    private TextField searchNomDocField;

    @FXML
    private TextField searchContDocField;

    @FXML
    private DatePicker searchDateField;

    @FXML
    private DatePicker searchDateExpField;

    @FXML
    private FilteredList<Ordonnance> filteredOrdonnances;

    

    public Ordonnances(Pharmacien ph){this.ph=ph;}

    public void openpageO(ActionEvent event){
        try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ordonnances.fxml"));
                loader.setController(this);  // Ensure FXML elements are linked
                Parent root = loader.load();

                name.setText("Nom: " + ph.getLastN());
                role.setText("Rôle: " + ph.getRole());
                this.ord = new Ordonnance();
                nbrOrd.setText("LISTE DES ORDONNANCES("+String.valueOf(ord.count(urldb))+")");
                initialize(null, null);

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setFullScreenExitHint("");
                stage.setTitle("Gestion des ordonnances");
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
        typeComboBox.setItems(FXCollections.observableArrayList("","Expirée", "Validée"));

        //this is for the table
        initializeTable();
        loadMedicamentsData();

        //this is for the search action
        setupRealTimeFiltering();

        //this for the update action
        ordTable.setRowFactory(tv -> {
            TableRow<Ordonnance> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Ordonnance selectedOrd = row.getItem();
                    OrdonnancesUpdate updateController = new OrdonnancesUpdate(this);
                    updateController.openpageOrd(null, selectedOrd);
                }
            });
            return row;
        });
    }

    private void initializeTable() {
        id_ord.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        nom_cli_ord.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClientN()));
        nom_med_ord.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMedicamentN()));
        nom_doc_ord.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDoctorName()));
        contact_doc_ord.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDoctorContact()));
        date_ord.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate()));
        date_exp_ord.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getExpirationDate()));
        etat_ord.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));
    }

    private void loadMedicamentsData() {
        ObservableList<Ordonnance> ordonnances = ord.getAll(urldb);
        ordTable.setItems(ordonnances);
        setupRealTimeFiltering();
    }

    private void setupRealTimeFiltering() {
        filteredOrdonnances = new FilteredList<>(ordTable.getItems(), p -> true);
    
        SortedList<Ordonnance> sortedList = new SortedList<>(filteredOrdonnances);
        sortedList.comparatorProperty().bind(ordTable.comparatorProperty());
        ordTable.setItems(sortedList);
        
        // Add listeners to all search fields
        searchNomCliField.textProperty().addListener((obs, oldVal, newVal) -> updateFilter());
        searchNomMedField.textProperty().addListener((obs, oldVal, newVal) -> updateFilter());
        searchNomDocField.textProperty().addListener((obs, oldVal, newVal) -> updateFilter());
        searchContDocField.textProperty().addListener((obs, oldVal, newVal) -> updateFilter());
        searchDateField.valueProperty().addListener((obs, oldVal, newVal) -> updateFilter());
        searchDateExpField.valueProperty().addListener((obs, oldVal, newVal) -> updateFilter());
        typeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> updateFilter());
    }

    private void updateFilter() {
        filteredOrdonnances.setPredicate(ordonnance -> {
            // If all search fields are empty, show all medicaments
            if (allFieldsEmpty()) {
                return true;
            }
            
            // Check each field
            boolean matchesNomCli = matchesString(ordonnance.getClientN(), searchNomCliField.getText());
            boolean matchesNomMed = matchesString(ordonnance.getMedicamentN(), searchNomMedField.getText());
            boolean matchesNomDoc = matchesString(ordonnance.getDoctorName(), searchNomDocField.getText());
            boolean matchesConDoc = matchesString(ordonnance.getDoctorContact(), searchContDocField.getText());
            boolean matchesDate = matchesDate(ordonnance.getDate(), searchDateField.getValue());
            boolean matchesDateExp = matchesDate(ordonnance.getExpirationDate(), searchDateExpField.getValue());
            boolean matchesStatus = matchesType(ordonnance.getStatus(), typeComboBox.getValue());

            
            return matchesNomCli && matchesNomMed && matchesNomDoc && 
                   matchesDate && matchesDateExp && matchesStatus && matchesConDoc; 
        });
    }

    private boolean allFieldsEmpty() {
        return (searchNomCliField.getText() == null || searchNomCliField.getText().isEmpty()) &&
               (searchNomMedField.getText() == null || searchNomMedField.getText().isEmpty()) &&
               (searchNomDocField.getText() == null || searchNomDocField.getText().isEmpty()) &&
               (searchDateField.getValue() == null) && (searchDateExpField.getValue() == null)&&
               (searchContDocField.getText() == null || searchContDocField.getText().isEmpty()) &&
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

    public void ajouterOrd(ActionEvent event) {
        try {
            // Refresh the label to ensure the count operation is completed
            refreshNbrOrdLabel();
    
            // Open the insert window
            OrdonnancesAdd ord = new OrdonnancesAdd(this);
            ord.openpageOrd(event);
        } catch (Exception e) {
            System.out.println("Error in ajouterM: " + e.getMessage());
        }
    }

    public void refreshNbrOrdLabel() {
        try {
            int count = ord.count(urldb);
            nbrOrd.setText("LISTE DES MÉDICAMENTS(" + count + ")");
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
