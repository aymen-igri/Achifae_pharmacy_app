package com.example.DB.models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class Réapprovisionnements implements Operations{
    private int id_re;
    private int medicamentId_re;
    private int pharmacienId_re;
    private int quantity_re;
    private String status_re;
    private String date_re;

    // Constructor
    public Réapprovisionnements(int id, int medicamentId, int pharmacienId, int quantity, String status, String date) {
        this.id_re = id;
        this.medicamentId_re = medicamentId;
        this.pharmacienId_re = pharmacienId;
        this.quantity_re = quantity;
        this.status_re = status;
        this.date_re = date;
    }

    // Getters
    public int getId() {return id_re;}
    public int getMedicamentId() {return medicamentId_re;}
    public int getPharmacienId() {return pharmacienId_re;}
    public int getQuantity() {return quantity_re;}
    public String getStatus() {return status_re;}
    public String getDate() {return date_re;}

    // Setters
    public void setId(int id) {this.id_re = id;}
    public void setMedicamentId(int medicamentId) {this.medicamentId_re = medicamentId;}
    public void setPharmacienId(int pharmacienId) {this.pharmacienId_re = pharmacienId;}
    public void setQuantity(int quantity) {this.quantity_re = quantity;}
    public void setStatus(String status) {this.status_re = status;}
    public void setDate(String date) {this.date_re = date;}

    @Override
    public String toString() {
        return "Réapprovisionnements{"+"id="+id_re+", medicamentId="+medicamentId_re+", pharmacienId="+pharmacienId_re+", quantity="+quantity_re+", status='"+status_re+'\''+", date='"+date_re+'\''+'}';
    }

    @Override
    public void insert(String URL) {
        String sql = "INSERT INTO Réapprovisionnements(id_re, id_med, id_ph, quantité_re, statu_re, date_re) VALUES(?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL)) {
            // Enable foreign key constraints
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON");
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(2, getMedicamentId());
                pstmt.setInt(3, getPharmacienId());
                pstmt.setInt(4, getQuantity());
                pstmt.setString(5, getStatus());
                pstmt.setString(6, getDate());

                pstmt.executeUpdate();
                System.out.println("Réapprovisionnement inserted successfully!");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}