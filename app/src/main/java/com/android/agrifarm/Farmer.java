package com.android.agrifarm;

import java.io.Serializable;

public class Farmer implements Serializable {
    private String name;
    private String cropType;
    private double price;
    private String imageUrl;
    private String location;
    private String contact;

    public Farmer(String name, String cropType, double price, String imageUrl, String location, String contact) {
        this.name = name;
        this.cropType = cropType;
        this.price = price;
        this.imageUrl = imageUrl;
        this.location = location;
        this.contact = contact;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getLocation() {
        return location;
    }

    public String getContact() {
        return contact;
    }
}