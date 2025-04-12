package com.example;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import com.example.DB.models.Medicament;
import com.example.DB.models.Pharmacien;

import javafx.beans.property.SimpleDoubleProperty;
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

    @FXML
    private TableView<Medicament> medTable;

    @FXML
    private TableColumn<Medicament, Integer> id_c;

    @FXML
    private TableColumn<Medicament, String> nom_c;

    @FXML
    private TableColumn<Medicament, Integer> quantite_c;

    @FXML
    private TableColumn<Medicament, Double> prix_c;

    @FXML
    private TableColumn<Medicament, String> dateexp_c;

    @FXML
    private TableColumn<Medicament, String> fourniceur_c;

    @FXML
    private TableColumn<Medicament, String> type_c;

    @FXML
    private TextField searchNomField;

    @FXML
    private TextField searchQuantiteField;

    @FXML
    private TextField searchPrixField;
    
    @FXML
    private DatePicker searchDateField;
    
    @FXML
    private TextField searchFournisseurField;

    @FXML
    private FilteredList<Medicament> filteredMedicaments;

    public Medicaments(Pharmacien ph){
        this.ph = ph;
    }

    public void openpageM(ActionEvent event){
        try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/medicaments.fxml"));
                loader.setController(this);  // Ensure FXML elements are linked
                Parent root = loader.load();

                name.setText("Nom: " + ph.getLastN());
                role.setText("Rôle: " + ph.getRole());
                this.med=new Medicament();
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
        typeComboBox.setItems(FXCollections.observableArrayList("","Avec ordonnance", "Sans ordonnance"));

        //this is for the table
        initializeTable();
        loadMedicamentsData();

        //this is for the search action
        setupRealTimeFiltering();

        //this for the update action
        medTable.setRowFactory(tv -> {
            TableRow<Medicament> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Medicament selectedMed = row.getItem();
                    MedicamentsUpdate updateController = new MedicamentsUpdate(this);
                    updateController.openpageMU(null, selectedMed);
                }
            });
            return row;
        });
    }

    private void initializeTable() {
        id_c.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        nom_c.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        quantite_c.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());
        prix_c.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrice()).asObject());
        dateexp_c.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getExpirationDate()));
        fourniceur_c.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSupplier()));
        type_c.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getType()));
    }

    private void loadMedicamentsData() {
        ObservableList<Medicament> medicaments = med.getAll(urldb);
        medTable.setItems(medicaments);
        setupRealTimeFiltering();
    }

    private void setupRealTimeFiltering() {
        filteredMedicaments = new FilteredList<>(medTable.getItems(), p -> true);

        SortedList<Medicament> sortedList = new SortedList<>(filteredMedicaments);
        sortedList.comparatorProperty().bind(medTable.comparatorProperty());
        medTable.setItems(sortedList);
        
        // Add listeners to all search fields
        searchNomField.textProperty().addListener((obs, oldVal, newVal) -> updateFilter());
        searchQuantiteField.textProperty().addListener((obs, oldVal, newVal) -> updateFilter());
        searchPrixField.textProperty().addListener((obs, oldVal, newVal) -> updateFilter());
        searchDateField.valueProperty().addListener((obs, oldVal, newVal) -> updateFilter());
        searchFournisseurField.textProperty().addListener((obs, oldVal, newVal) -> updateFilter());
        typeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> updateFilter());
    }

    private void updateFilter() {
        filteredMedicaments.setPredicate(medicament -> {
            // If all search fields are empty, show all medicaments
            if (allFieldsEmpty()) {
                return true;
            }
            
            // Check each field
            boolean matchesNom = matchesString(medicament.getName(), searchNomField.getText());
            boolean matchesQuantite = matchesNumber(medicament.getQuantity(), searchQuantiteField.getText());
            boolean matchesPrix = matchesNumber(medicament.getPrice(), searchPrixField.getText());
            boolean matchesDate = matchesDate(medicament.getExpirationDate(), searchDateField.getValue());
            boolean matchesFournisseur = matchesString(medicament.getSupplier(), searchFournisseurField.getText());
            boolean matchesType = matchesType(medicament.getType(), typeComboBox.getValue());
            
            return matchesNom && matchesQuantite && matchesPrix && 
                   matchesDate && matchesFournisseur && matchesType;
        });
    }

    private boolean allFieldsEmpty() {
        return (searchNomField.getText() == null || searchNomField.getText().isEmpty()) &&
               (searchQuantiteField.getText() == null || searchQuantiteField.getText().isEmpty()) &&
               (searchPrixField.getText() == null || searchPrixField.getText().isEmpty()) &&
               (searchDateField.getValue() == null) &&
               (searchFournisseurField.getText() == null || searchFournisseurField.getText().isEmpty()) &&
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

    public void ajouterM(ActionEvent event) {
        try {
            // Refresh the label to ensure the count operation is completed
            refreshNbrMedLabel();
    
            // Open the insert window
            MedicamentsAdd m = new MedicamentsAdd(this);
            m.openpageMO(event);
        } catch (Exception e) {
            System.out.println("Error in ajouterM: " + e.getMessage());
        }
    }

    public void refreshNbrMedLabel() {
        try {
            int count = med.count(urldb);
            nbrMed.setText("LISTE DES MÉDICAMENTS(" + count + ")");
            loadMedicamentsData();
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

