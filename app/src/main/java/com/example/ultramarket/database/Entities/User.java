package com.example.ultramarket.database.Entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "user")
public class User {

    @PrimaryKey(autoGenerate = true)
    private long ID;

    private String email;

    private String name;

    private String password;

    private double longitude;

    private double latitude;

    private String building;

    private int floor;

    private String phone;

    private String image;

    private int rate;

    public User(long ID, String email, String name, String password,
                double longitude, double latitude, String building,
                int floor, String phone, String image, int rate) {

        this.ID = ID;
        this.email = email;
        this.name = name;
        this.password = password;
        this.longitude = longitude;
        this.latitude = latitude;
        this.building = building;
        this.floor = floor;
        this.phone = phone;
        this.image = image;
        this.rate = rate;
    }

    @Ignore
    public User() {
    }

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
