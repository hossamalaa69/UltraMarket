package com.example.ultramarket.database.Entities;

public class Location {
    private String country_name;
    private String city_name;
    private String road_name;
    private String latitude;
    private String longitude;

    public void setCountry_name(String country_name) {
        this.country_name = country_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public void setRoad_name(String road_name) {
        this.road_name = road_name;
    }


    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getCountry_name() {
        return country_name;
    }

    public String getCity_name() {
        return city_name;
    }

    public String getRoad_name() {
        return road_name;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public Location() {
    }

    public Location(String country_name, String city_name, String road_name, String latitude, String longitude) {
        this.country_name = country_name;
        this.city_name = city_name;
        this.road_name = road_name;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
