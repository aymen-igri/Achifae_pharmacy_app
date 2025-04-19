package com.example;



import com.example.DB.models.Client;
import com.example.DB.models.Medicament;
import com.example.DB.models.Pharmacien;
import com.example.DB.models.Vente;
import com.example.DB.models.Réapprovisionnement;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.scene.control.Label;

// for the graph
import javafx.scene.chart.LineChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;


public class Tableaudeboard {

    private Pharmacien ph;
    private Medicament med;
    private Client cli;
    private Vente ven;
    private Réapprovisionnement rea;

    
    private String URL = "jdbc:sqlite:src/main/java/com/example/DB/pharmacy.db";

    @FXML
    private Label name;
    
    @FXML
    private Label role;

    @FXML
    private Label bienvenue;

    @FXML
    private Label nbrMed ;

    @FXML 
    private Label nbrCli;

    @FXML
    private Label nbrVen;

    @FXML
    private Label nbrRea;

    //for the graph
    @FXML
    private LineChart<String, Number> lineChart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    @FXML
    private BarChart<String, Number> barChart;

    @FXML
    private CategoryAxis barXAxis;

    @FXML
    private NumberAxis barYAxis;

    public Tableaudeboard(Pharmacien ph){
        this.ph = ph;
        this.med = new Medicament();
        this.cli = new Client();
        this.ven = new Vente();
        this.rea = new Réapprovisionnement();
    }
    

    public void openpageT(ActionEvent event){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tableaudeboard.fxml"));
            loader.setController(this);  // Ensure FXML elements are linked
            Parent root = loader.load();

            name.setText("Nom: "+ph.getLastN());
            role.setText("Rôle: " + ph.getRole());
            bienvenue.setText("Bienvenue " + ph.getName()+" "+ph.getLastN());
            nbrMed.setText(String.valueOf(med.count(URL)));
            nbrCli.setText(String.valueOf(cli.count(URL)));
            nbrVen.setText(String.valueOf(ven.count(URL)));
            nbrRea.setText(String.valueOf(rea.count(URL)));

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);

            //the graphs functions
            loadChartData();
            loadBarChartData();

            stage.setFullScreenExitHint("");
            stage.setTitle("Tabeau de board");
            stage.setFullScreen(false);
            stage.centerOnScreen();
            stage.show();
        }catch(Exception e){
            showAlert(AlertType.ERROR, "Erreur", "Une erreur s'est produite", "Détails: " + e.getMessage());
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    //for the graph
    private void loadChartData() {
        try {
            // Clear any existing data
            lineChart.getData().clear();
            
            // Create a series for sales data
            XYChart.Series<String, Number> salesSeries = new XYChart.Series<>();
            salesSeries.setName("Ventes par mois");
            
            // Connect to the database
            Connection conn = DriverManager.getConnection(URL);
            Statement stmt = conn.createStatement();
            
            // Query for monthly sales data
            String ventesQuery = "SELECT strftime('%m-%Y', date_v) as month , SUM(prix_total_v) AS total" +
                                 " FROM Ventes GROUP BY month ORDER BY date_v";
            ResultSet rsVentes = stmt.executeQuery(ventesQuery);
            
            // Add data points to the sales series
            while (rsVentes.next()) {
                String month = rsVentes.getString("month");
                double total = rsVentes.getDouble("total");
                salesSeries.getData().add(new XYChart.Data<>(month, total));
            }
            
            
            // Add the series to the chart
            lineChart.getData().addAll(salesSeries);
            
            // Set chart title and axis labels
            xAxis.setLabel("Mois");
            yAxis.setLabel("Valeur");
            yAxis.setTickLabelsVisible(true);
            yAxis.setTickMarkVisible(true);
            yAxis.setAnimated(false);
            
            // Close resources
            rsVentes.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Erreur", "Erreur lors du chargement des données du graphique", 
                     "Détails: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadBarChartData() {
        try {
            // Clear any existing data
            barChart.getData().clear();
            
            // Create a series for the data
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Médicaments les plus vendus");
            
            // Connect to the database
            Connection conn = DriverManager.getConnection(URL);
            Statement stmt = conn.createStatement();
            
            // Query for top 5 most sold medications
            String query = "SELECT " +
                          "m.nom_med AS nom, " +
                          "SUM(v.quantité_v) AS total_sold " +
                          "FROM Ventes v " +
                          "JOIN Medicaments m ON v.id_med = m.id_med " +
                          "GROUP BY m.nom_med " +
                          "ORDER BY total_sold DESC " +
                          "LIMIT 5";
            
            ResultSet rs = stmt.executeQuery(query);
            
            // Add data points to the series
            while (rs.next()) {
                String medicationName = rs.getString("nom");
                int quantitySold = rs.getInt("total_sold");
                series.getData().add(new XYChart.Data<>(medicationName, quantitySold));
            }
            
            // Add the series to the chart
            barChart.getData().add(series);
            
            // Set chart title and axis labels
            barChart.setTitle("Top 5 Médicaments les plus vendus");
            barXAxis.setLabel("Médicament");
            barYAxis.setLabel("Quantité vendue");
            // After adding data to the chart
            barXAxis.setTickLabelsVisible(true);
            barXAxis.setTickMarkVisible(true);
            barXAxis.setAnimated(false);
             // Prevent automatic label skipping
            
            // Apply custom styling to bars
            for (XYChart.Data<String, Number> data : series.getData()) {
                data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                    if (newNode != null) {
                        // Add tooltip
                        javafx.scene.control.Tooltip tooltip = new javafx.scene.control.Tooltip(
                            data.getXValue() + ": " + data.getYValue() + " unités"
                        );
                        javafx.scene.control.Tooltip.install(newNode, tooltip);
                    }
                });
            }
            
            // Close resources
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Erreur", "Erreur lors du chargement des données du graphique à barres", 
                     "Détails: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public void openMed(ActionEvent event ){
        Medicaments m = new Medicaments(ph);
        m.openpageM(event);
    }

    public void openCli(ActionEvent event ){
        Clients c = new Clients(ph);
        c.openpageC(event);
    }

    public void openOrd(ActionEvent event){
        Ordonnances o = new Ordonnances(ph);
        o.openpageO(event);
    }

    public void openVen(ActionEvent event ){
        Ventes v = new Ventes(ph);
        v.openpageV(event);
    }

    public void openRea(ActionEvent event){
        Réapprovisionnements r = new Réapprovisionnements(ph);
        r.openpageR(event);
    }

    public void openPar(ActionEvent event ){
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
