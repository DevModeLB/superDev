package com.devmode.superdev.models;

public class Supplier {
    private int id;
    private String name;

    public Supplier(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name; // This is what will be displayed in the ComboBox
    }
}
