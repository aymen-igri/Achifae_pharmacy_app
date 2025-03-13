package com.example.DB.models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Pharmacien extends Utilisateur implements Operations{
    private String contact_ph;
    private String username_ph;
    private String password_ph;
    private String role_ph;

    // Constructor
    public Pharmacien(int id, String name, String gender, String contact, String username, String password, String role) {
        super(id, name, gender);
        this.contact_ph = contact;
        this.username_ph = username;
        this.password_ph = password;
        this.role_ph = role;
    }

    // Getters
    public int getId() {return super.getId();}
    public String getName() {return super.getName();}
    public String getGender() {return super.getGender();}
    public String getContact() {return contact_ph;}
    public String getUsername() {return username_ph;}
    public String getPassword() {return password_ph;}
    public String getRole() {return role_ph;}

    // Setters
    public void setId(int id) {super.setId(id);}
    public void setName(String name) {super.setName(name);}
    public void setGender(String gender) {super.setGender(gender);}
    public void setContact(String contact) {this.contact_ph = contact;}
    public void setUsername(String username) {this.username_ph = username;}
    public void setPassword(String password) {this.password_ph = password;}
    public void setRole(String role) {this.role_ph = role;}

    @Override
    public String toString() {
        return "Pharmacien{"+"id="+getId()+", name='"+getName()+'\''+", gender='"+getGender()+'\''+", contact='"+contact_ph+'\''+", username='"+username_ph+'\''+", password='"+password_ph+'\''+", role='"+role_ph+'\''+'}';
    }

    @Override
    public void insert(String URL){
        String sql = "INSERT INTO Pharmacien(id_ph, nom_ph, sexe_ph, cont_ph, username_ph, password_ph, role_ph) VALUES(?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(2,getName());
            pstmt.setString(3,getGender());
            pstmt.setString(4,getContact());
            pstmt.setString(5,getUsername());
            pstmt.setString(6,getPassword());
            pstmt.setString(7,getRole());

            pstmt.executeUpdate();
            System.out.println("Pharmacien inserted successfully!");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

}