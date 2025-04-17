package com.example.DB.models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Vente implements Operations{
    private int id_v;
    private int clientId_v;
    private String clientN_v;
    private int medicamentId_v;
    private String medicamentN_v;
    private int pharmacienId_v;
    private String pharmacienN_v;
    private int quantity_v;
    private double totalPrice_v;
    private String date_v;

    private boolean insufficientQuantity = false;
    private int availableQuantity = 0;
    private int requestedQuantity = 0;

    // Constructor
    public Vente(int id, int clientId, int medicamentId, int pharmacienId, int quantity, double totalPrice, String date) {
        this.id_v = id;
        this.clientId_v = clientId;
        this.medicamentId_v = medicamentId;
        this.pharmacienId_v = pharmacienId;
        this.quantity_v = quantity;
        this.totalPrice_v = totalPrice;
        this.date_v = date;
    }

    public Vente() {
        this.id_v = 0;
        this.clientId_v = 0;
        this.medicamentId_v = 0;
        this.pharmacienId_v = 0;
        this.quantity_v = 0;
        this.totalPrice_v = 0;
        this.date_v = "";
    }

    // Getters
    public int getId() {return id_v;}
    public int getClientId() {return clientId_v;}
    public String getClientN() {return clientN_v;}
    public int getMedicamentId() {return medicamentId_v;}
    public String getMedicamentN() {return medicamentN_v;}
    public int getPharmacienId() {return pharmacienId_v;}
    public String getPharmacienN() {return pharmacienN_v;}
    public int getQuantity() {return quantity_v;}
    public double getTotalPrice() {return totalPrice_v;}
    public String getDate() {return date_v;}

    public boolean hasInsufficientQuantity() {
        return insufficientQuantity;
    }
    
    public int getAvailableQuantity() {
        return availableQuantity;
    }
    
    public int getRequestedQuantity() {
        return requestedQuantity;
    }

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

    // Add these instance variables to your Vente class


    @Override
    public void insert(String URL) {
        // Reset the state
        insufficientQuantity = false;
        
        // First, check if there's enough quantity available
        String checkQuantitySql = "SELECT quantité_med FROM Medicaments WHERE id_med = ?";
        String insertSql = "INSERT INTO Ventes(id_cli, id_med, id_ph, quantité_v, prix_total_v, date_v) VALUES(?, ?, ?, ?, ?, ?)";
        String updateQuantitySql = "UPDATE Medicaments SET quantité_med = quantité_med - ? WHERE id_med = ?";
        
        Connection conn = null;
        
        try {
            // Get connection and enable foreign keys
            conn = DriverManager.getConnection(URL);
            conn.setAutoCommit(false); // Start transaction  
            
            // Enable foreign key constraints
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON");
            }
            
            // First check if we have enough quantity
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuantitySql)) {
                checkStmt.setInt(1, getMedicamentId());
                
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        availableQuantity = rs.getInt("quantité_med");
                        requestedQuantity = getQuantity();
                        
                        if (availableQuantity < requestedQuantity) {
                            // Set the flag and return early
                            insufficientQuantity = true;
                            throw new SQLException("Quantité insuffisante pour le médicament ID " + getMedicamentId());
                        }
                    } else {
                        throw new SQLException("Médicament avec ID " + getMedicamentId() + " non trouvé");
                    }
                }
            }
            
            // Then insert the sale
            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                pstmt.setInt(1, getClientId());
                pstmt.setInt(2, getMedicamentId());
                pstmt.setInt(3, getPharmacienId());
                pstmt.setInt(4, getQuantity());
                pstmt.setDouble(5, getTotalPrice());
                pstmt.setString(6, getDate());
                
                pstmt.executeUpdate();
            }
            
            // Then update the quantity
            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuantitySql)) {
                updateStmt.setInt(1, getQuantity());
                updateStmt.setInt(2, getMedicamentId());
                
                int rowsAffected = updateStmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("Mise à jour de la quantité échouée");
                }
            }
            
            // If everything went well, commit the transaction
            conn.commit();
            System.out.println("Vente insérée avec succès et quantité mise à jour!");
            
        } catch (SQLException e) {
            // If anything goes wrong, rollback the transaction
            System.out.println(e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("Transaction annulée");
                } catch (SQLException ex) {
                    System.out.println("Impossible d'annuler la transaction: " + ex.getMessage());
                }
            }
            throw new RuntimeException(e.getMessage());
        } finally {
            // Finally close the connection
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Reset auto-commit mode
                    conn.close();
                } catch (SQLException e) {
                    System.out.println("Impossible de fermer la connexion: " + e.getMessage());
                }
            }
        }
    }

    public void update(String URL, Vente newVente) {
        // Reset the state
        insufficientQuantity = false;
        availableQuantity = 0;
        requestedQuantity = 0;
        
        String updateVenteSQL = "UPDATE Ventes SET " +
                                "quantité_v = ?, " +
                                "prix_total_v = ?, " +
                                "id_ph = ?, " +
                                "date_v = ? " +
                                "WHERE id_v = ?";
    
        String selectOldVenteSQL = "SELECT quantité_v, id_med FROM Ventes WHERE id_v = ?";
        String checkStockSQL = "SELECT quantité_med FROM Medicaments WHERE id_med = ?";
        String updateStockSQL = "UPDATE Medicaments SET quantité_med = quantité_med + ? WHERE id_med = ?";
    
        Connection conn = null;
        
        try {
            conn = DriverManager.getConnection(URL);
            conn.setAutoCommit(false); // Start transaction
            
            // Enable foreign key constraints
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON");
            }
    
            int oldQuantity = 0;
            int idMed = 0;
    
            // Step 1: Get old vente data
            try (PreparedStatement selectStmt = conn.prepareStatement(selectOldVenteSQL)) {
                selectStmt.setInt(1, this.getId());
                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        oldQuantity = rs.getInt("quantité_v");
                        idMed = rs.getInt("id_med");
                    } else {
                        System.out.println("No vente found with ID: " + this.getId());
                        return;
                    }
                }
            }
            
            // Step 2: Check if there's enough stock after adding back the old quantity
            try (PreparedStatement checkStmt = conn.prepareStatement(checkStockSQL)) {
                checkStmt.setInt(1, idMed);
                
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        int currentStock = rs.getInt("quantité_med");
                        availableQuantity = currentStock + oldQuantity; // Available after returning old quantity
                        requestedQuantity = newVente.getQuantity();
                        
                        if (availableQuantity < requestedQuantity) {
                            // Not enough stock after returning old quantity
                            insufficientQuantity = true;
                            throw new SQLException("Quantité insuffisante pour le médicament ID " + idMed + 
                                                 ".\n Disponible: " + availableQuantity + 
                                                 "\n, Demandé: " + requestedQuantity);
                        }
                    } else {
                        throw new SQLException("Médicament avec ID " + idMed + " non trouvé");
                    }
                }
            }
    
            // Step 3: Add back old quantity to stock
            try (PreparedStatement addBackStmt = conn.prepareStatement(updateStockSQL)) {
                addBackStmt.setInt(1, oldQuantity);
                addBackStmt.setInt(2, idMed);
                addBackStmt.executeUpdate();
            }
    
            // Step 4: Subtract new quantity from stock
            try (PreparedStatement subtractStmt = conn.prepareStatement(updateStockSQL)) {
                subtractStmt.setInt(1, -newVente.getQuantity()); // subtracting
                subtractStmt.setInt(2, idMed);
                subtractStmt.executeUpdate();
            }
    
            // Step 5: Update the vente record
            try (PreparedStatement pstmt = conn.prepareStatement(updateVenteSQL)) {
                pstmt.setInt(1, newVente.getQuantity());
                pstmt.setDouble(2, newVente.getTotalPrice());
                pstmt.setInt(3, newVente.getPharmacienId());
                pstmt.setString(4, newVente.getDate());
                pstmt.setInt(5, this.getId());
    
                pstmt.executeUpdate();
            }
            
            // Commit the transaction if everything is successful
            conn.commit();
            System.out.println("Vente updated successfully with new date and stock adjusted!");
    
        } catch (SQLException e) {
            // Rollback in case of failure
            System.out.println("Error updating vente: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("Transaction rolled back");
                } catch (SQLException ex) {
                    System.out.println("Failed to rollback: " + ex.getMessage());
                }
            }
            throw new RuntimeException(e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Reset auto-commit mode
                    conn.close();
                } catch (SQLException e) {
                    System.out.println("Failed to close connection: " + e.getMessage());
                }
            }
        }
    }

    public void delete(String URL) {
        // SQL query to delete a vente by its ID
        String sql = "DELETE FROM Ventes WHERE id_v = ?";
    
        try (Connection conn = DatabaseManager.getConnection(URL)) {
            // Enable foreign key constraints
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON");
            }
    
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                // Set the id_v to delete
                pstmt.setInt(1, getId());
    
                // Execute the delete query
                int rows = pstmt.executeUpdate();
    
                if (rows > 0) {
                    conn.commit();
                    System.out.println("Vente with ID " + getId() + " deleted successfully!");
                } else {
                    conn.rollback();
                    System.out.println("No vente found with ID: " + getId());
                }
            }
    
        } catch (SQLException e) {
            System.out.println("Error deleting vente: " + e.getMessage());
        }
    } 
    
    @Override
    public int count(String URL){
        String sql="SELECT COUNT(id_v) FROM Ventes";

        int count = 0;
        try(Connection conn = DriverManager.getConnection(URL);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            java.sql.ResultSet rs = pstmt.executeQuery()){
               if (rs.next()){
                count=rs.getInt(1);
               }
               rs.close();
               pstmt.close();
               conn.close();
            }catch(Exception e){
               System.out.println("error:"+e.getMessage());
            }
        return count;
    }

    public ObservableList<Vente> getAll(String URL) {
        ObservableList<Vente> ventes = FXCollections.observableArrayList();

        String sql = "SELECT v.id_v, v.id_cli, c.nom_cli, v.id_med, m.nom_med, v.id_ph, p.nom_ph, " +
                    "v.quantité_v, v.prix_total_v, v.date_v " +
                    "FROM Ventes v " +
                    "JOIN Clients c ON v.id_cli = c.id_cli " +
                    "JOIN Medicaments m ON v.id_med = m.id_med " +
                    "JOIN Pharmacien p ON v.id_ph = p.id_ph";

        try (Connection conn = DatabaseManager.getConnection(URL);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            java.sql.ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Vente vente = new Vente();
                vente.setId(rs.getInt("id_v"));
                vente.setClientId(rs.getInt("id_cli"));
                vente.clientN_v = rs.getString("nom_cli");  // using directly since no setter exists
                vente.setMedicamentId(rs.getInt("id_med"));
                vente.medicamentN_v = rs.getString("nom_med");
                vente.setPharmacienId(rs.getInt("id_ph"));
                vente.pharmacienN_v = rs.getString("nom_ph");
                vente.setQuantity(rs.getInt("quantité_v"));
                vente.setTotalPrice(rs.getDouble("prix_total_v"));
                vente.setDate(rs.getString("date_v"));

                ventes.add(vente);
            }
            rs.close();
            pstmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println("Error fetching ventes with names: " + e.getMessage());
        }

        return ventes;
    }
}