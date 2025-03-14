package com.example.DB.models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Client extends Utilisateur implements Operations{

    // les information du client
    private int phoneNumber_cli;
    private String email_cli;
    private String address_cli;

    // Constructeur
    public Client(String name, String gender, int phoneNumber, String email, String address) {
        super(name, gender);
        this.phoneNumber_cli = phoneNumber;
        this.email_cli = email;
        this.address_cli = address;
    }

    // Getters 
    public int getId(){return super.getId();}
    public String getName() {return super.getName();}
    public String getGender() {return super.getGender();}
    public int getPhoneNumber() {return phoneNumber_cli;}
    public String getEmail() {return email_cli;}
    public String getAddress() {return address_cli;}
    
    //Setters
    public void setId(int id){super.setId(id);}
    public void setName(String name) {super.setName(name);}
    public void setGender(String gender) {super.setGender(gender);}
    public void setPhoneNumber(int phoneNumber) {this.phoneNumber_cli = phoneNumber;}
    public void setEmail(String email) {this.email_cli = email;}
    public void setAddress(String address) {this.address_cli = address;}

    @Override
    //une fonction qui retourne une chaine des caractaires pour facilite la tache des recherche ,inseriton ...
    public String toString() {
        return "Client{" +"id=" +super.getId()+", name='"+super.getName()+'\''+", gender='"+super.getGender()+'\''+", phoneNumber="+phoneNumber_cli+", email='"+email_cli+'\''+", address='"+address_cli+'\''+'}';
    }

    //methode to insert elements
    @Override
    public void insert(String URL){
        String sql = "INSERT INTO Clients(id_cli, nom_cli, sexe_cli, num_cli, email_cli, addre_cli) VALUES(?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(2, getName());
            pstmt.setString(3, getGender());
            pstmt.setInt(4, getPhoneNumber());
            pstmt.setString(5, getEmail());
            pstmt.setString(6, getAddress());

            pstmt.executeUpdate();
            System.out.println("Client inserted successfully!");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
              // Re-throwing the exception to handle it at a higher level if needed
        }
    }
}