package com.example.DB.models;

public class Utilisateur {
    private int id;
    private String name;
    private String lastn;
    private String gender;

    // Constructeur
    public Utilisateur(String name,String lastn,String gender){
        this.name=name;
        this.lastn=lastn;
        this.gender=gender;
    }

    // Getters 
    public int getId(){return id;}
    public String getName() {return name;}
    public String getGender() {return gender;}
    public String getLastN() {return lastn;}

    //Setters
    public void setId(int id){this.id = id;}
    public void setName(String name) {this.name = name;}
    public void setGender(String gender) {this.gender = gender;}
    public void setLastN(String lastn) {this.lastn=lastn;}
}
