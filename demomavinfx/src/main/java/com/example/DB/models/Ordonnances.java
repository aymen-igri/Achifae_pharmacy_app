package com.example.DB.models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class Ordonnances implements Operations{
    private int id_ord;
    private int clientId_ord;
    private int medicamentId_ord;
    private String doctorName_ord;
    private String doctorContact_ord;
    private String date_ord;
    private String expirationDate_ord;
    private String status_ord;

    // Constructor
    public Ordonnances(int id, int clientId, int medicamentId, String doctorName, String doctorContact, String date, String expirationDate, String status) {
        this.id_ord = id;
        this.clientId_ord = clientId;
        this.medicamentId_ord = medicamentId;
        this.doctorName_ord = doctorName;
        this.doctorContact_ord = doctorContact;
        this.date_ord = date;
        this.expirationDate_ord = expirationDate;
        this.status_ord = status;
    }

    // Getters
    public int getId() {return id_ord;}
    public int getClientId() {return clientId_ord;}
    public int getMedicamentId() {return medicamentId_ord;}
    public String getDoctorName() {return doctorName_ord;}
    public String getDoctorContact() {return doctorContact_ord;}
    public String getDate() {return date_ord;}
    public String getExpirationDate() {return expirationDate_ord;}
    public String getStatus() {return status_ord;}

    // Setters
    public void setId(int id) {this.id_ord = id;}
    public void setClientId(int clientId) {this.clientId_ord = clientId;}
    public void setMedicamentId(int medicamentId) {this.medicamentId_ord = medicamentId;}
    public void setDoctorName(String doctorName) {this.doctorName_ord = doctorName;}
    public void setDoctorContact(String doctorContact) {this.doctorContact_ord = doctorContact;}
    public void setDate(String date) {this.date_ord = date;}
    public void setExpirationDate(String expirationDate) {this.expirationDate_ord = expirationDate;}
    public void setStatus(String status) {this.status_ord = status;}

    @Override
    public String toString() {
        return "Ordonnance{"+"id="+id_ord+", clientId="+clientId_ord+", medicamentId="+medicamentId_ord+", doctorName='"+doctorName_ord+'\''+", doctorContact='"+doctorContact_ord+'\''+", date='"+date_ord+'\''+", expirationDate='"+expirationDate_ord+'\''+", status='"+status_ord+'\''+'}'; 
    }

    @Override
    public void insert(String URL){
        String sql = "INSERT INTO Ordonnances(id_ord, id_cli, id_med, nom_doc_ord, conta_doc_ord, date_ord, date_exp_ord, statu_ord) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL)) {
            // Enable foreign key constraints
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON");
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(2, getClientId());
                pstmt.setInt(3, getMedicamentId());
                pstmt.setString(4, getDoctorName());
                pstmt.setString(5, getDoctorContact());
                pstmt.setString(6, getDate());
                pstmt.setString(7, getExpirationDate());
                pstmt.setString(8, getStatus());

                pstmt.executeUpdate();
                System.out.println("Ordonnance inserted successfully!");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
