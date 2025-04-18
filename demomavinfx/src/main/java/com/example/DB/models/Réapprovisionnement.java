package com.example.DB.models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Réapprovisionnement implements Operations{
    private int id_re;
    private int medicamentId_re;
    private String medicamentN_re;
    private int pharmacienId_re;
    private String pharmacienN_re;
    private int quantity_re;
    private String status_re;
    private String date_re;

    // Constructor
    public Réapprovisionnement(int medicamentId, int pharmacienId, int quantity, String status, String date) {
        this.medicamentId_re = medicamentId;
        this.pharmacienId_re = pharmacienId;
        this.quantity_re = quantity;
        this.status_re = status;
        this.date_re = date;
    }

    public Réapprovisionnement() {
        this.id_re = 0;
        this.medicamentId_re = 0;
        this.pharmacienId_re = 0;
        this.quantity_re = 0;
        this.status_re = "";
        this.date_re = "";
    }

    // Getters
    public int getId() {return id_re;}
    public int getMedicamentId() {return medicamentId_re;}
    public String getMedicamentN(){return medicamentN_re;}
    public int getPharmacienId() {return pharmacienId_re;}
    public String getPharmacienN(){return pharmacienN_re;}
    public int getQuantity() {return quantity_re;}
    public String getStatus() {return status_re;}
    public String getDate() {return date_re;}

    // Setters
    public void setId(int id) {this.id_re = id;}
    public void setMedicamentId(int medicamentId) {this.medicamentId_re = medicamentId;}
    public void setMedicamentN(String medicamentN) {this.medicamentN_re = medicamentN;}
    public void setPharmacienId(int pharmacienId) {this.pharmacienId_re = pharmacienId;}
    public void setPharmacienN(String pharmacienN) {this.pharmacienN_re = pharmacienN;}
    public void setQuantity(int quantity) {this.quantity_re = quantity;}
    public void setStatus(String status) {this.status_re = status;}
    public void setDate(String date) {this.date_re = date;}

    @Override
    public String toString() {
        return "Réapprovisionnements{"+"id="+id_re+", medicamentId="+medicamentId_re+", pharmacienId="+pharmacienId_re+", quantity="+quantity_re+", status='"+status_re+'\''+", date='"+date_re+'\''+'}';
    }

    public ObservableList<Réapprovisionnement> getAll(String URL) {
        ObservableList<Réapprovisionnement> reapprovisionnements = FXCollections.observableArrayList();
        
        String sql = "SELECT r.id_re, r.quantité_re, r.statu_re, r.date_re, " +
                    "m.nom_med AS medicament_name, p.nom_ph AS pharmacien_name " +
                    "FROM Réapprovisionnements r " +
                    "JOIN Medicaments m ON r.id_med = m.id_med " +
                    "JOIN Pharmacien p ON r.id_ph = p.id_ph";
        
        try (Connection conn = DriverManager.getConnection(URL);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Réapprovisionnement reappro = new Réapprovisionnement();
                reappro.setId(rs.getInt("id_re"));
                reappro.setMedicamentN(rs.getString("medicament_name"));
                reappro.setPharmacienN(rs.getString("pharmacien_name"));
                reappro.setQuantity(rs.getInt("quantité_re"));
                reappro.setStatus(rs.getString("statu_re"));
                reappro.setDate(rs.getString("date_re"));
                
                reapprovisionnements.add(reappro);
            }
        } catch (SQLException e) {
            System.out.println("Error in getAll(): " + e.getMessage());
        }
        
        return reapprovisionnements;
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
    
    public void update(String URL) {
        String sql = "UPDATE Réapprovisionnements SET id_ph = ?, quantité_re = ?, statu_re = ?, date_re = ? WHERE id_re = ?";

        try (Connection conn = DatabaseManager.getConnection(URL)) {
            // Enable foreign key constraints
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON");
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                // Set parameters
                pstmt.setInt(1, getPharmacienId());
                pstmt.setInt(2, getQuantity());
                pstmt.setString(3, getStatus());
                pstmt.setString(4, getDate());
                pstmt.setInt(5, getId());

                int rowsAffected = pstmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    System.out.println("Réapprovisionnement updated successfully!");
                } else {
                    System.out.println("No réapprovisionnement found with ID: " + getId());
                }
            }
        } catch (SQLException e) {
            System.out.println("Error updating réapprovisionnement: " + e.getMessage());
        }
    }

    public boolean delete(String URL) {
        String sql = "DELETE FROM Réapprovisionnements WHERE id_re = ?";
        boolean success = false;
    
        try (Connection conn = DriverManager.getConnection(URL)) {
            // Enable foreign key constraints
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON");
            }
    
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                // Set the ID parameter
                pstmt.setInt(1, getId());
    
                // Execute the delete operation
                int rowsAffected = pstmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    System.out.println("Réapprovisionnement with ID " + getId() + " deleted successfully!");
                    success = true;
                } else {
                    System.out.println("No réapprovisionnement found with ID: " + getId());
                }
            }
        } catch (SQLException e) {
            System.out.println("Error deleting réapprovisionnement: " + e.getMessage());
        }
        
        return success;
    }

    @Override
    public int count(String URL){
        String sql="SELECT COUNT(id_re) FROM Réapprovisionnements";

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
}