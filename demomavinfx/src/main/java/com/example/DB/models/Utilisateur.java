package com.example.DB.models;

public class Utilisateur {
    private int id;
    private String name;
    private String gender;

    // Constructeur
    public Utilisateur(int id,String name,String gender){
        this.id=id;
        this.name=name;
        this.gender=gender;
    }

    // Getters 
    public int getId(){return id;}
    public String getName() {return name;}
    public String getGender() {return gender;}

    //Setters
    public void setId(int id){this.id = id;}
    public void setName(String name) {this.name = name;}
    public void setGender(String gender) {this.gender = gender;}
}
