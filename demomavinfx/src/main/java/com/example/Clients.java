package com.example;

import java.net.URL;
import java.util.ResourceBundle;

import com.example.DB.models.Client;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class Clients {
    
    private Pharmacien ph;
    private String urldb = "jdbc:sqlite:src/main/java/com/example/DB/pharmacy.db";
    private Client cli;

    @FXML
    private Label name;
    
    @FXML
    private Label role;

    @FXML
    private Label nbrCli;

    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    private TableView<Client> cliTable;

    @FXML
    private TableColumn<Client, Integer> id_c;

    @FXML
    private TableColumn<Client, String> nom_c;

    @FXML
    private TableColumn<Client, String> pren_c;

    @FXML
    private TableColumn<Client, String> sexe_c;

    @FXML
    private TableColumn<Client, Integer> tele_c;

    @FXML
    private TableColumn<Client, String> email_c;

    @FXML
    private TableColumn<Client, String> adresse_c;

    @FXML
    private TextField searchNomField;

    @FXML
    private TextField searchPrenomField;

    @FXML
    private TextField searchTeleField;
    
    @FXML
    private TextField searchEmailField;
    
    @FXML
    private TextField searchAdresseField;

    @FXML
    private FilteredList<Client> filteredClients;

    public Clients(Pharmacien ph){this.ph=ph;}

    public void openpageC(ActionEvent event){
        try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/clients.fxml"));
                loader.setController(this);  // Ensure FXML elements are linked
                Parent root = loader.load();

                name.setText("Nom: " + ph.getLastN());
                role.setText("Poste: " + ph.getRole());
                this.cli = new Client();
                nbrCli.setText("LISTE DES CLIENTS("+cli.count(urldb)+")");

                initialize(null, null);

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setFullScreenExitHint("");
                stage.setTitle("Gestion des clients");
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
        typeComboBox.setItems(FXCollections.observableArrayList("","Homme", "Femme"));

        initializeTable();
        loadClientsData();

        //this is for the search action
        setupRealTimeFiltering();

        //this for the update action
        cliTable.setRowFactory(tv -> {
            TableRow<Client> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Client selectedMed = row.getItem();
                    ClientsUpdate updateController = new ClientsUpdate(this);
                    updateController.openpagecliu(null, selectedMed);
                }
            });
            return row;
        });
    }

    private void initializeTable() {
        id_c.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        nom_c.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        pren_c.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLastN()));
        sexe_c.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGender()));
        tele_c.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getPhoneNumber()).asObject());
        email_c.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        adresse_c.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAddress()));
    }

    private void loadClientsData() {
        ObservableList<Client> clients = cli.getAll(urldb);
        cliTable.setItems(clients);
        setupRealTimeFiltering();
    }

    private void setupRealTimeFiltering() {
        // Wrap the ObservableList in a FilteredList
        filteredClients = new FilteredList<>(cliTable.getItems(), p -> true);
        
        // Bind the FilteredList to the TableView
        SortedList<Client> sortedList = new SortedList<>(filteredClients);
        sortedList.comparatorProperty().bind(cliTable.comparatorProperty());
        cliTable.setItems(sortedList);
        
        // Add listeners to all search fields
        searchNomField.textProperty().addListener((obs, oldVal, newVal) -> updateFilter());
        searchPrenomField.textProperty().addListener((obs, oldVal, newVal) -> updateFilter());
        searchTeleField.textProperty().addListener((obs, oldVal, newVal) -> updateFilter());
        searchAdresseField.textProperty().addListener((obs, oldVal, newVal) -> updateFilter());
        searchEmailField.textProperty().addListener((obs, oldVal, newVal) -> updateFilter());
        typeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> updateFilter());
    }

    private void updateFilter() {
        filteredClients.setPredicate(client -> {
            // If all search fields are empty, show all medicaments
            if (allFieldsEmpty()) {
                return true;
            }
            
            // Check each field
            boolean matchesNom = matchesString(client.getName(), searchNomField.getText());
            boolean matchesPrenom = matchesString(client.getLastN(), searchPrenomField.getText());
            boolean matchesSexe = matchesType(client.getGender(), typeComboBox.getValue());
            boolean matchesTele = matchesNumber(client.getPhoneNumber(), searchTeleField.getText());
            boolean matchesEmail = matchesString(client.getEmail(), searchEmailField.getText());
            boolean matchesAdresse = matchesString(client.getAddress(), searchAdresseField.getText());
            
            return matchesNom && matchesPrenom && matchesSexe && 
                   matchesTele && matchesEmail && matchesAdresse;
        });
    }

    private boolean allFieldsEmpty() {
        return (searchNomField.getText() == null || searchNomField.getText().isEmpty()) &&
               (searchPrenomField.getText() == null || searchPrenomField.getText().isEmpty()) &&
               (searchTeleField.getText() == null || searchTeleField.getText().isEmpty()) &&
               (searchEmailField.getText() == null || searchEmailField.getText().isEmpty()) &&
               (searchAdresseField.getText() == null || searchAdresseField.getText().isEmpty()) &&
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
    
    private boolean matchesType(String fieldValue, String searchValue) {
        if (searchValue == null || searchValue.isEmpty()) {
            return true;
        }
        return fieldValue.equals(searchValue);
    }

    public void refreshNbrMedLabel() {
        try {
            int count = cli.count(urldb);
            nbrCli.setText("LISTE DES CLIENTS(" + count + ")");
            loadClientsData();
        } catch (Exception e) {
            System.out.println("Error refreshing label: " + e.getMessage());
        } 
    }

    public void ajoutercli(ActionEvent event) {
        try {
            // Refresh the label to ensure the count operation is completed
            refreshNbrMedLabel();
    
            // Open the insert window
            ClientsAdd clia = new ClientsAdd(this);
            clia.openpagecli(event);
        
        } catch (Exception e) {
            System.out.println("Error in ajouterM: " + e.getMessage());
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
        Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation");
        confirmAlert.setHeaderText("Confirmer la déconnexion");
        confirmAlert.setContentText("Êtes-vous sûr de vouloir dconnecter?");

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
