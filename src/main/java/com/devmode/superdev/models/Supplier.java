package com.devmode.superdev.models;

public class Supplier {
    private int id;
    private String name;
    private String phone_nb;

    public Supplier(int id, String name, String phone_nb) {
        this.id = id;
        this.name = name;
        this.phone_nb = phone_nb;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setPhone_nb(String phone_nb){
        this.phone_nb = phone_nb;
    }

    public String getPhone_nb(){
        return this.phone_nb;
    }

    @Override
    public String toString() {
        return name; // This is what will be displayed in the ComboBox
    }
}
