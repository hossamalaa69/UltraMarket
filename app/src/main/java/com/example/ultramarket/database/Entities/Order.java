package com.example.ultramarket.database.Entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.Map;

@Entity(tableName = "cart", indices = {@Index(value = "user_ID"), @Index(value = "product_ID")},
        foreignKeys = {@ForeignKey(entity = User.class,
                parentColumns = "ID",
                childColumns = "user_ID",
                onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Product.class,
                        parentColumns = "ID",
                        childColumns = "product_ID",
                        onDelete = ForeignKey.CASCADE)
        })
public class Order implements Serializable {

    @Exclude
    public static final int STATUS_CONFIRMED = 1;
    @Exclude
    public static final int STATUS_READY = 2;
    @Exclude
    public static final int STATUS_ON_WAY = 3;

    @PrimaryKey
    private String ID;
    private double price;
    Map<String, Integer> products;
    private long receiving_date;
    private long order_date;
    private int status = STATUS_CONFIRMED;

    @Exclude
    private String customerId;


    public Order() {
    }

    public int getStatus() {
        return status;
    }

    public boolean setStatus(int status) {
        if (status < STATUS_CONFIRMED || status > STATUS_ON_WAY) return false;
        this.status = status;
        return true;
    }

    public Order(String ID, Map<String, Integer> products, double price, long order_date, long receiving_date) {
        this.ID = ID;
        this.price = price;
        this.order_date = order_date;
        this.products = products;
        this.receiving_date = receiving_date;
    }

    public void setOrder_date(long order_date) {
        this.order_date = order_date;
    }

    public long getOrder_date() {
        return order_date;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public Map<String, Integer> getProducts() {
        return products;
    }

    public void setProducts(Map<String, Integer> products) {
        this.products = products;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public long getReceiving_date() {
        return receiving_date;
    }

    public void setReceiving_date(long receiving_date) {
        this.receiving_date = receiving_date;
    }

    @Exclude
    public String getCustomerId() {
        return customerId;
    }

    @Exclude
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
}
