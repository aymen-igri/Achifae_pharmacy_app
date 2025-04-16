package com.example.DB.models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Ordonnance implements Operations{
    private int id_ord;
    private int clientId_ord;
    private String clientN_ord;
    private int medicamentId_ord;
    private String medicamentN_ord;
    private String doctorName_ord;
    private String doctorContact_ord;
    private String date_ord;
    private String expirationDate_ord;
    private String status_ord;

    // Constructor
    public Ordonnance(){}

    public Ordonnance(int clientId, int medicamentId, String clientN_ord, String medicamentN_ord,  String doctorName, String doctorContact, String date, String expirationDate, String status) {
        this.clientId_ord = clientId;
        this.clientN_ord = clientN_ord;
        this.medicamentId_ord = medicamentId;
        this.medicamentN_ord = medicamentN_ord;
        this.doctorName_ord = doctorName;
        this.doctorContact_ord = doctorContact;
        this.date_ord = date;
        this.expirationDate_ord = expirationDate;
        this.status_ord = status;
    }

    // Getters
    public int getId() {return id_ord;}
    public int getClientId() {return clientId_ord;}
    public String getClientN() {return clientN_ord;}
    public int getMedicamentId() {return medicamentId_ord;}
    public String getMedicamentN() {return medicamentN_ord;}
    public String getDoctorName() {return doctorName_ord;}
    public String getDoctorContact() {return doctorContact_ord;}
    public String getDate() {return date_ord;}
    public String getExpirationDate() {return expirationDate_ord;}
    public String getStatus() {return status_ord;}

    // Setters
    public void setId(int id) {this.id_ord = id;}
    public void setClientId(int clientId) {this.clientId_ord = clientId;}
    public void setClientN(String clientN) {this.clientN_ord = clientN;}
    public void setMedicamentId(int medicamentId) {this.medicamentId_ord = medicamentId;}
    public void setMedicamentN(String medicamentN) {this.medicamentN_ord = medicamentN;}
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

    public void update(String URL) {
        String sql = "UPDATE Ordonnances SET " +
                    "id_cli = ?, " +
                    "id_med = ?, " +
                    "nom_doc_ord = ?, " +
                    "conta_doc_ord = ?, " +
                    "date_ord = ?, " +
                    "date_exp_ord = ?, " +
                    "statu_ord = ? " +
                    "WHERE id_ord = ?";

        try (Connection conn = DriverManager.getConnection(URL)) {
            // Enable foreign key constraints
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON");
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, getClientId());
                pstmt.setInt(2, getMedicamentId());
                pstmt.setString(3, getDoctorName());
                pstmt.setString(4, getDoctorContact());
                pstmt.setString(5, getDate());
                pstmt.setString(6, getExpirationDate());
                pstmt.setString(7, getStatus());
                pstmt.setInt(8, getId());

                int rows = pstmt.executeUpdate();

                if (rows > 0) {
                    System.out.println("Ordonnance updated successfully!");
                } else {
                    System.out.println("No ordonnance found with ID: " + getId());
                }

            }

        } catch (SQLException e) {
            System.out.println("Error updating ordonnance: " + e.getMessage());
        }
    }


    @Override
    public int count(String URL){
        String sql="SELECT COUNT(id_ord) FROM Ordonnances";

        int count = 0;
        try(Connection conn = DriverManager.getConnection(URL);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            java.sql.ResultSet rs = pstmt.executeQuery()){
               if (rs.next()){
                count=rs.getInt(1);
               }
            }catch(Exception e){
               System.out.println("error:"+e.getMessage());
            }
        return count;
    }

    public synchronized ObservableList<Ordonnance> getAll(String URL) {
        String sql = "SELECT o.*, c.nom_cli, m.nom_med " +
                     "FROM Ordonnances o " +
                     "INNER JOIN Clients c ON o.id_cli = c.id_cli " +
                     "INNER JOIN Medicaments m ON o.id_med = m.id_med";
        
        ObservableList<Ordonnance> ordonnances = FXCollections.observableArrayList();
    
        try (Connection conn = DatabaseManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
    
            while (rs.next()) {
                // 1. Create Ordonnance object with client/medicament IDs and names
                Ordonnance ord = new Ordonnance(
                    rs.getInt("id_cli"),        // Client ID (from Ordonnances)
                    rs.getInt("id_med"),       // Medicament ID (from Ordonnances)
                    rs.getString("nom_cli"),   // Client name (from Clients)
                    rs.getString("nom_med"),   // Medicament name (from Medicaments)
                    rs.getString("nom_doc_ord"),
                    rs.getString("conta_doc_ord"),
                    rs.getString("date_ord"),
                    rs.getString("date_exp_ord"),
                    rs.getString("statu_ord")
                );
                
                // 2. Set the ordonnance ID separately
                ord.setId(rs.getInt("id_ord"));
                ordonnances.add(ord);
            }
    
        } catch (SQLException e) {
            System.out.println("Error fetching ordonnances: " + e.getMessage());
        }
    
        return ordonnances;
    }

}
