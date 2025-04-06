package com.example.DB.models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Medicament implements Operations {
    private int id_med;
    private String name_med;
    private int quantity_med;
    private double price_med;
    private String expirationDate_med;
    private String supplier_med;
    private String type_med;

    // Constructeur
    public Medicament(String name, int quantity, double price, String expirationDate, String supplier, String type) {
        this.id_med = 0; // Assuming ID is auto-generated in the database
        this.name_med = name;
        this.quantity_med = quantity;
        this.price_med = price;
        this.expirationDate_med = expirationDate;
        this.supplier_med = supplier;
        this.type_med = type;
    }

    public Medicament() {
        this.id_med = 0;
        this.name_med = "";
        this.quantity_med = 0;
        this.price_med = 0;
        this.expirationDate_med = "";
        this.supplier_med = "";
        this.type_med = "";
    }

    // Getters
    public int getId() {return id_med;}
    public String getName() {return name_med;}
    public int getQuantity() {return quantity_med;}
    public double getPrice() {return price_med;}
    public String getExpirationDate() {return expirationDate_med;}
    public String getSupplier() {return supplier_med;}
    public String getType() {return type_med;}

    // Setters
    public void setId(int id) {this.id_med = id;}
    public void setName(String name) {this.name_med = name;}
    public void setQuantity(int quantity) {this.quantity_med = quantity;}
    public void setPrice(double price) {this.price_med = price;}
    public void setExpirationDate(String expirationDate) {this.expirationDate_med = expirationDate;}
    public void setSupplier(String supplier) {this.supplier_med = supplier;}
    public void setType(String type) {this.type_med = type;}

    @Override
    public String toString() {
        return "Medicament{"+"id="+id_med+", name='"+name_med+'\''+", quantity="+quantity_med+", price="+price_med+", expirationDate='"+expirationDate_med+'\''+", supplier='"+supplier_med+'\''+", type='"+type_med+'\''+'}';
    }

    @Override
    public synchronized void insert(String URL) {
        String sql = "INSERT INTO Medicaments(nom_med, quantité_med, prix_med, date_exp_med, forn_med, type_med) VALUES(?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection(URL);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
                

            // Set parameters for the prepared statement
            pstmt.setString(1, getName());
            pstmt.setInt(2, getQuantity());
            pstmt.setDouble(3, getPrice());
            pstmt.setString(4, getExpirationDate());
            pstmt.setString(5, getSupplier());
            pstmt.setString(6, getType());

            // Execute the insert operation
            pstmt.executeUpdate();
            System.out.println("Medicament inserted successfully!");

            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Error during insert: " + e.getMessage());
            throw new RuntimeException("Failed to insert medicament: " + e.getMessage());
        }
    }

    @Override
    public synchronized int count(String URL) {
        String sql = "SELECT COUNT(id_med) FROM Medicaments";
        int count = 0;

        try (Connection conn = DatabaseManager.getConnection(URL);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            java.sql.ResultSet rs = pstmt.executeQuery()) {

            // Retrieve the count from the result set
            if (rs.next()) {
                count = rs.getInt(1);
            }
            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            System.out.println("Error counting medicaments: " + e.getMessage());
        }

        return count;
    }

    public synchronized ObservableList<Medicament> getAll(String URL) {
        String sql = "SELECT * FROM Medicaments";
        ObservableList<Medicament> medicaments = FXCollections.observableArrayList();

        try (Connection conn = DatabaseManager.getConnection(URL);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Medicament med = new Medicament(
                    rs.getString("nom_med"),
                    rs.getInt("quantité_med"),
                    rs.getDouble("prix_med"),
                    rs.getString("date_exp_med"),
                    rs.getString("forn_med"),
                    rs.getString("type_med")
                );
                med.setId(rs.getInt("id_med"));
                medicaments.add(med);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching medicaments: " + e.getMessage());
        }

        return medicaments;
    }
    
}