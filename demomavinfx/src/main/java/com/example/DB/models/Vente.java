package com.example.DB.models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Vente implements Operations{
    private int id_v;
    private int clientId_v;
    private String clientN_v;
    private int medicamentId_v;
    private String medicamentN_v;
    private int pharmacienId_v;
    private String pharmacienN_v;
    private int quantity_v;
    private double totalPrice_v;
    private String date_v;

    // Constructor
    public Vente(int id, int clientId, int medicamentId, int pharmacienId, int quantity, double totalPrice, String date) {
        this.id_v = id;
        this.clientId_v = clientId;
        this.medicamentId_v = medicamentId;
        this.pharmacienId_v = pharmacienId;
        this.quantity_v = quantity;
        this.totalPrice_v = totalPrice;
        this.date_v = date;
    }

    public Vente() {
        this.id_v = 0;
        this.clientId_v = 0;
        this.medicamentId_v = 0;
        this.pharmacienId_v = 0;
        this.quantity_v = 0;
        this.totalPrice_v = 0;
        this.date_v = "";
    }

    // Getters
    public int getId() {return id_v;}
    public int getClientId() {return clientId_v;}
    public String getClientN() {return clientN_v;}
    public int getMedicamentId() {return medicamentId_v;}
    public String getMedicamentN() {return medicamentN_v;}
    public int getPharmacienId() {return pharmacienId_v;}
    public String getPharmacienN() {return pharmacienN_v;}
    public int getQuantity() {return quantity_v;}
    public double getTotalPrice() {return totalPrice_v;}
    public String getDate() {return date_v;}

    // Setters
    public void setId(int id) {this.id_v = id;}
    public void setClientId(int clientId) {this.clientId_v = clientId;}
    public void setMedicamentId(int medicamentId) {this.medicamentId_v = medicamentId;}
    public void setPharmacienId(int pharmacienId) {this.pharmacienId_v = pharmacienId;}
    public void setQuantity(int quantity) {this.quantity_v = quantity;}
    public void setTotalPrice(double totalPrice) {this.totalPrice_v = totalPrice;}
    public void setDate(String date) {this.date_v = date;}

    @Override
    public String toString() {
        return "Ventes{"+"id="+id_v+", clientId="+clientId_v+", medicamentId="+medicamentId_v+", pharmacienId="+pharmacienId_v+", quantity="+quantity_v+", totalPrice="+totalPrice_v+", date='"+date_v+'\''+'}';
    }

    @Override
    public void insert(String URL) {
        String sql = "INSERT INTO Ventes(id_v, id_cli, id_med, id_ph, quantité_v, prix_total_v, date_v) VALUES(?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL)) {
            // Enable foreign key constraints
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON");
            }
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, getId());
                pstmt.setInt(2, getClientId());
                pstmt.setInt(3, getMedicamentId());
                pstmt.setInt(4, getPharmacienId());
                pstmt.setInt(5, getQuantity());
                pstmt.setDouble(6, getTotalPrice());
                pstmt.setString(7, getDate());

                pstmt.executeUpdate();
                System.out.println("Vente inserted successfully!");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public int count(String URL){
        String sql="SELECT COUNT(id_v) FROM Ventes";

        int count = 0;
        try(Connection conn = DriverManager.getConnection(URL);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            java.sql.ResultSet rs = pstmt.executeQuery()){
               if (rs.next()){
                count=rs.getInt(1);
               }
               rs.close();
               pstmt.close();
               conn.close();
            }catch(Exception e){
               System.out.println("error:"+e.getMessage());
            }
        return count;
    }

    public ObservableList<Vente> getAll(String URL) {
        ObservableList<Vente> ventes = FXCollections.observableArrayList();

        String sql = "SELECT v.id_v, v.id_cli, c.nom_cli, v.id_med, m.nom_med, v.id_ph, p.nom_ph, " +
                    "v.quantité_v, v.prix_total_v, v.date_v " +
                    "FROM Ventes v " +
                    "JOIN Clients c ON v.id_cli = c.id_cli " +
                    "JOIN Medicaments m ON v.id_med = m.id_med " +
                    "JOIN Pharmacien p ON v.id_ph = p.id_ph";

        try (Connection conn = DatabaseManager.getConnection(URL);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            java.sql.ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Vente vente = new Vente();
                vente.setId(rs.getInt("id_v"));
                vente.setClientId(rs.getInt("id_cli"));
                vente.clientN_v = rs.getString("nom_cli");  // using directly since no setter exists
                vente.setMedicamentId(rs.getInt("id_med"));
                vente.medicamentN_v = rs.getString("nom_med");
                vente.setPharmacienId(rs.getInt("id_ph"));
                vente.pharmacienN_v = rs.getString("nom_ph");
                vente.setQuantity(rs.getInt("quantité_v"));
                vente.setTotalPrice(rs.getDouble("prix_total_v"));
                vente.setDate(rs.getString("date_v"));

                ventes.add(vente);
                System.out.println("hi me"+vente.toString());
            }
            rs.close();
            pstmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println("Error fetching ventes with names: " + e.getMessage());
        }

        return ventes;
    }
}