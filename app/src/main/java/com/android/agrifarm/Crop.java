package com.android.agrifarm;

public class Crop {
    private String name;
    private int iconResId;

    public Crop(String name, int iconResId) {
        this.name = name;
        this.iconResId = iconResId;
    }

    public String getName() { return name; }
    public int getIconResId() { return iconResId; }
}