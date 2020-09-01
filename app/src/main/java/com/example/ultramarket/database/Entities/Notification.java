package com.example.ultramarket.database.Entities;

public class Notification {

    private String ID;

    private String title;

    private String body;

    private String imageUrl;

    private String productID;

    private long date;

    public Notification(String ID, String title, String body, String imageUrl, String productID, long date) {
        this.ID = ID;
        this.title = title;
        this.body = body;
        this.imageUrl = imageUrl;
        this.productID = productID;
        this.date = date;
    }

    public Notification() {
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
