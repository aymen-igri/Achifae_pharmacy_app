package com.example.DB.models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Medicaments implements Operations {
    private int id_med;
    private String name_med;
    private int quantity_med;
    private double price_med;
    private String expirationDate_med;
    private String supplier_med;
    private String type_med;

    // Constructeur
    public Medicaments(int id, String name, int quantity, double price, String expirationDate, String supplier, String type) {
        this.id_med = id;
        this.name_med = name;
        this.quantity_med = quantity;
        this.price_med = price;
        this.expirationDate_med = expirationDate;
        this.supplier_med = supplier;
        this.type_med = type;
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
    public void insert(String URL){
        String sql = "INSERT INTO Medicaments(id_med, nom_med, quantité_med, prix_med, date_exp_med, forn_med, type_med) VALUES(?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(2,getName());
            pstmt.setInt(3,getQuantity());
            pstmt.setDouble(4,getPrice());
            pstmt.setString(5,getExpirationDate());
            pstmt.setString(6,getSupplier());
            pstmt.setString(7,getType());

            pstmt.executeUpdate();
            System.out.println("Medicament inserted successfully!");

        } catch (SQLException e) {
            System.out.println(e.getMessage()); // Re-throwing the exception to handle it at a higher level if needed
        }
    }
}