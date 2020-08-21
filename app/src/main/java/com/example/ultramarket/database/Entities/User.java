package com.example.ultramarket.database.Entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Exclude;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "user")
public class User {

    @NotNull
    @PrimaryKey
    private String ID;

    private String email;

    private String name;

    private double longitude;

    private double latitude;

    private String building;

    private int floor;

    private String phone;

    private String imageUrl;

    private int rate;

    @Exclude
    @Ignore
    private boolean isExpanded;

    @Ignore
    private int numOrders;

    @Ignore
    @Exclude
    private int imageID;

    public User(String ID, String email, String name,
                double longitude, double latitude, String building,
                int floor, String phone, String imageUrl, int rate) {

        this.ID = ID;
        this.email = email;
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.building = building;
        this.floor = floor;
        this.phone = phone;
        this.imageUrl = imageUrl;
        this.rate = rate;
        this.isExpanded = false;
    }

    @Ignore
    public User(String email, String name, String phone, int rate, int numOrders, int imageID) {
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.rate = rate;
        this.numOrders = numOrders;
        this.imageID = imageID;
        this.isExpanded = false;
    }

    @Ignore
    public User() {
    }

    @Ignore
    public  User(FirebaseUser user) {
        ID = user.getUid();
        imageUrl = user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null;
        email = user.getEmail();
        name = user.getDisplayName();
        phone = user.getPhoneNumber();
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Ignore
    @Exclude
    public boolean isExpanded() {
        return isExpanded;
    }

    @Ignore
    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    @Ignore
    @Exclude
    public int getNumOrders() {
        return numOrders;
    }

    @Ignore
    public void setNumOrders(int numOrders) {
        this.numOrders = numOrders;
    }

    @Ignore
    @Exclude
    public int getImageID() {
        return imageID;
    }

    @Ignore
    public void setImageID(int imageID) {
        this.imageID = imageID;
    }
}
