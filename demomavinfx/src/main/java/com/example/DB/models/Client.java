package com.example.DB.models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Client extends Utilisateur implements Operations{

    // les information du client
    private String phoneNumber_cli;
    private String email_cli;
    private String address_cli;

    // Constructeur
    public Client(String name,String lastn, String gender, String phoneNumber, String email, String address) {
        super(name,lastn, gender);
        this.phoneNumber_cli = phoneNumber;
        this.email_cli = email;
        this.address_cli = address;
    }

    public Client() {
        super("","","");
        this.phoneNumber_cli ="";
        this.email_cli = "";
        this.address_cli = "";
    }

    // Getters 
    public int getId(){return super.getId();}
    public String getName() {return super.getName();}
    public String getLastN() {return super.getLastN();}
    public String getGender() {return super.getGender();}
    public String getPhoneNumber() {return phoneNumber_cli;}
    public String getEmail() {return email_cli;}
    public String getAddress() {return address_cli;}
    
    //Setters
    public void setId(int id){super.setId(id);}
    public void setName(String name) {super.setName(name);}
    public void setLastN(String lastn) {super.setLastN(lastn);}
    public void setGender(String gender) {super.setGender(gender);}
    public void setPhoneNumber(String phoneNumber) {this.phoneNumber_cli = phoneNumber;}
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
        String sql = "INSERT INTO Clients(id_cli, nom_cli, sexe_cli, num_cli, email_cli, addre_cli,pre_cli) VALUES(?, ?, ?, ?, ?, ?,?)";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(2, getName());
            pstmt.setString(3, getGender());
            pstmt.setString(4, getPhoneNumber());
            pstmt.setString(5, getEmail());
            pstmt.setString(6, getAddress());
            pstmt.setString(7, getLastN());

            pstmt.executeUpdate();
            System.out.println("Client inserted successfully!");
            pstmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
              // Re-throwing the exception to handle it at a higher level if needed
        }
    }

    @Override
    public int count(String URL){
        String sql="SELECT COUNT(id_cli) FROM Clients";

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

    public synchronized ObservableList<Client> getAll(String URL) {
        String sql = "SELECT * FROM Clients";
        ObservableList<Client> clients = FXCollections.observableArrayList();

        try (Connection conn = DatabaseManager.getConnection(URL);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Client cli = new Client(
                    rs.getString("nom_cli"),
                    rs.getString("pre_cli"),
                    rs.getString("sexe_cli"),
                    rs.getString("num_cli"),
                    rs.getString("email_cli"),
                    rs.getString("addre_cli")
                );
                cli.setId(rs.getInt("id_cli"));
                clients.add(cli);
            }
            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            System.out.println("Error fetching medicaments: " + e.getMessage());
        }

        return clients;
    }

    public synchronized void update(String URL) {
        String sql = "UPDATE Clients SET nom_cli = ?, pre_cli = ?, sexe_cli = ?, "
                + "num_cli = ?, email_cli = ?, addre_cli = ? WHERE id_cli = ?";

        try (Connection conn = DatabaseManager.getConnection(URL);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Set parameters for the prepared statement
            pstmt.setString(1, getName());
            pstmt.setString(2, getLastN());
            pstmt.setString(3, getGender());
            pstmt.setString(4, getPhoneNumber());
            pstmt.setString(5, getEmail());
            pstmt.setString(6, getAddress());
            pstmt.setInt(7, getId());

            // Execute the update operation
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Updating client failed, no rows affected.");
            }
            
            System.out.println("client updated successfully!");
            pstmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println("Error during update: " + e.getMessage());
            throw new RuntimeException("Failed to update client: " + e.getMessage());
        }
    }
}