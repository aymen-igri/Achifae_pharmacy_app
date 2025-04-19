package com.example.DB.models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Pharmacien extends Utilisateur implements Operations{
    private String contact_ph;
    private String username_ph;
    private String password_ph;
    private String role_ph;

    // Constructor
    public Pharmacien(String name,String lastn , String gender, String contact, String username, String password, String role) {
        super(name,lastn, gender);
        this.contact_ph = contact;
        this.username_ph = username;
        this.password_ph = password;
        this.role_ph = role;
    }

    public Pharmacien() {
        super("","", "");
        this.contact_ph = "";
        this.username_ph = "";
        this.password_ph = "";
        this.role_ph = "";
    }

    // Getters
    public int getId() {return super.getId();}
    public String getName() {return super.getName();}
    public String getLastN() {return super.getLastN();}
    public String getGender() {return super.getGender();}
    public String getContact() {return contact_ph;}
    public String getUsername() {return username_ph;}
    public String getPassword() {return password_ph;}
    public String getRole() {return role_ph;}

    // Setters
    public void setId(int id) {super.setId(id);}
    public void setName(String name) {super.setName(name);}
    public void setLastN(String lastn) {super.setLastN(lastn);}
    public void setGender(String gender) {super.setGender(gender);}
    public void setContact(String contact) {this.contact_ph = contact;}
    public void setUsername(String username) {this.username_ph = username;}
    public void setPassword(String password) {this.password_ph = password;}
    public void setRole(String role) {this.role_ph = role;}

    @Override
    public String toString() {
        return "Pharmacien{"+"id="+getId()+", name='"+getName()+", pren='"+getLastN()+'\''+", gender='"+getGender()+'\''+", contact='"+contact_ph+'\''+", username='"+username_ph+'\''+", password='"+password_ph+'\''+", role='"+role_ph+'\''+'}';
    }

    @Override
    public void insert(String URL){
        String sql = "INSERT INTO Pharmacien(nom_ph, pren_ph, sexe_ph, cont_ph, username_ph, password_ph, role_ph) VALUES(?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, getName());
        pstmt.setString(2, getLastN());
        pstmt.setString(3, getGender());
        pstmt.setString(4, getContact());
        pstmt.setString(5, getUsername());
        pstmt.setString(6, getPassword());
        pstmt.setString(7, getRole());

        pstmt.executeUpdate();
        System.out.println("Pharmacien inserted successfully!");

    } catch (SQLException e) {
        System.out.println("Database error: " + e.getMessage());
        e.printStackTrace();
    }
}

    public boolean exist(String URL){
        String sql = "SELECT * FROM Pharmacien WHERE username_ph=? AND password_ph=?";
        boolean userExists = false;
    
        try (Connection conn = DriverManager.getConnection(URL);
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
            pstmt.setString(1, username_ph);
            pstmt.setString(2, password_ph);
        
            try (ResultSet rs = pstmt.executeQuery()) {

                userExists = rs.next();
            
                if (userExists) {
                    System.out.println("Login successful!");
                } else {
                    System.out.println("Invalid username or password.");
                }
                rs.close();
                pstmt.close();
                conn.close();
            }   
        }catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
        return userExists;
    }

    public Pharmacien getByUserPass(String URL){

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{
                conn = DriverManager.getConnection(URL);
                String sql = "SELECT * FROM Pharmacien WHERE username_ph=? AND password_ph=?";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, this.username_ph);
                stmt.setString(2, this.password_ph);
                rs = stmt.executeQuery();
                if (rs.next()) {
                    // Create and return a new Pharmacien object with all the data
                    Pharmacien pharmacien = new Pharmacien(
                        rs.getString("nom_ph"),
                        rs.getString("pren_ph"),
                        rs.getString("sexe_ph"),
                        rs.getString("cont_ph"),
                        rs.getString("username_ph"),
                        rs.getString("password_ph"),
                        rs.getString("role_ph")
                    );
                    pharmacien.setId(rs.getInt("id_ph"));
                    rs.close();
                    conn.close();
                    return pharmacien;
                }
                return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public void updateIP(String URL) {
        String sql = "UPDATE Pharmacien SET nom_ph = ?, pren_ph = ?, sexe_ph = ?, cont_ph = ? WHERE id_ph = ?";
        
        try (Connection conn = DriverManager.getConnection(URL);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Set parameters for fields to update
            pstmt.setString(1, getName());
            pstmt.setString(2, getLastN());
            pstmt.setString(3, getGender());
            pstmt.setString(4, getContact());
            pstmt.setInt(5, getId());
            
            // Execute the update
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                System.out.println("Pharmacien updated successfully!");
            } else {
                System.out.println("Pharmacien update failed. No rows affected.");
            }
            
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void updateC(String URL) {
        String sql = "UPDATE Pharmacien SET username_ph = ?, password_ph = ? WHERE id_ph = ?";
        
        try (Connection conn = DriverManager.getConnection(URL);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Set parameters for fields to update
            pstmt.setString(1, getUsername());
            pstmt.setString(2, getPassword());
            pstmt.setInt(3, getId());
            
            // Execute the update
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                System.out.println("Pharmacien updated successfully!");
            } else {
                System.out.println("Pharmacien update failed. No rows affected.");
            }
            
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void updateR(String URL) {
        String sql = "UPDATE Pharmacien SET role_ph=? WHERE id_ph = ?";
        
        try (Connection conn = DriverManager.getConnection(URL);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Set parameters for fields to update
            pstmt.setString(1, getRole());
            pstmt.setInt(2, getId());
            
            // Execute the update
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                System.out.println("Pharmacien updated successfully!");
            } else {
                System.out.println("Pharmacien update failed. No rows affected.");
            }
            
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean delete(String URL) {
        String sql = "DELETE FROM Pharmacien WHERE id_ph = ?";
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

    public synchronized ObservableList<Pharmacien> getAll(String URL) {
        String sql = "SELECT id_ph,nom_ph,pren_ph,role_ph,sexe_ph,cont_ph FROM Pharmacien";
        ObservableList<Pharmacien> phs = FXCollections.observableArrayList();

        try (Connection conn = DatabaseManager.getConnection(URL);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Pharmacien ph = new Pharmacien();
                ph.setId(rs.getInt("id_ph"));
                ph.setName(rs.getString("nom_ph"));
                ph.setLastN(rs.getString("pren_ph"));
                ph.setRole(rs.getString("role_ph"));
                ph.setGender(rs.getString("sexe_ph"));
                ph.setContact(rs.getString("cont_ph"));
                phs.add(ph);
                
            }
            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            System.out.println("Error fetching medicaments: " + e.getMessage());
        }

        return phs;
    }

    @Override
    public int count(String URL){
        String sql="SELECT COUNT(id_ph) FROM Pharmacien";

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