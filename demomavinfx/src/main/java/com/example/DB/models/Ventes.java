package com.example.DB.models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class Ventes implements Operations{
    private int id_v;
    private int clientId_v;
    private int medicamentId_v;
    private int pharmacienId_v;
    private int quantity_v;
    private double totalPrice_v;
    private String date_v;

    // Constructor
    public Ventes(int id, int clientId, int medicamentId, int pharmacienId, int quantity, double totalPrice, String date) {
        this.id_v = id;
        this.clientId_v = clientId;
        this.medicamentId_v = medicamentId;
        this.pharmacienId_v = pharmacienId;
        this.quantity_v = quantity;
        this.totalPrice_v = totalPrice;
        this.date_v = date;
    }

    // Getters
    public int getId() {return id_v;}
    public int getClientId() {return clientId_v;}
    public int getMedicamentId() {return medicamentId_v;}
    public int getPharmacienId() {return pharmacienId_v;}
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
}