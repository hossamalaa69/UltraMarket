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

    private String building;

    private String phone;

    private String imageUrl;
    @Ignore
    private Location location = new Location();

    private int rate;

    @Exclude
    @Ignore
    private boolean isExpanded;

    @Ignore
    private int numOrders;

    @Ignore
    @Exclude
    private int imageID;
    private int floor;

    public User(String ID, String email, String name,
                String building,
                int floor, String phone, String imageUrl, int rate) {

        this.ID = ID;
        this.email = email;
        this.name = name;
        this.building = building;
        this.floor = floor;
        this.phone = phone;
        this.imageUrl = imageUrl;
        this.rate = rate;
        this.isExpanded = false;
    }

    @Ignore
    public User(@NotNull String ID, String email, String name, String building,
                String phone, String imageUrl, Location location, int rate, boolean isExpanded, int numOrders, int imageID, int floor) {
        this.ID = ID;
        this.email = email;
        this.name = name;
        this.building = building;
        this.phone = phone;
        this.imageUrl = imageUrl;
        this.location = location;
        this.rate = rate;
        this.isExpanded = isExpanded;
        this.numOrders = numOrders;
        this.imageID = imageID;
        this.floor = floor;
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
    public Location getLocation() {
        return location;
    }

    @Ignore
    public void setLocation(Location location) {
        this.location = location;
    }

    @Ignore
    public User(FirebaseUser user) {
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
