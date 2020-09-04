package com.example.ultramarket.database.Entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
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
    @Exclude
    public static final int STATUS_DELIVERED = 4;

    public static final int  STATUS_INVISIBLE = 0 ;

    @PrimaryKey
    private String ID;
    private double price;
    Map<String, Integer> products;
    private long receiving_date;
    private long order_date;
    private int status;

    @Exclude
    private String customerId;

    @Exclude
    @Ignore
    private boolean isExpanded;


    public Order() {
    }

    public int getStatus() {
        return status;
    }

    public boolean setStatus(int status) {
        if (status < STATUS_INVISIBLE || status > STATUS_DELIVERED) return false;
        this.status = status;
        return true;
    }

    public Order(String ID, Map<String, Integer> products, double price, long order_date, long receiving_date) {
        this.ID = ID;
        this.price = price;
        this.order_date = order_date;
        this.products = products;
        this.receiving_date = receiving_date;
        this.isExpanded = false;
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


    @Exclude
    public String getStatusStr(){
        String status_str = "";
        switch (this.status) {
            case STATUS_INVISIBLE:
                status_str = "Invisible";
                break;
            case STATUS_CONFIRMED:
                status_str = "Confirmed";
                break;
            case STATUS_READY:
                status_str = "Ready";
                break;
            case STATUS_ON_WAY:
                status_str = "On Way";
                break;
            case STATUS_DELIVERED:
                status_str = "Delivered";
                break;
        }
        return status_str;
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

    public void replace(Order order) {
        this.order_date = order.getOrder_date();
        receiving_date = order.getReceiving_date();
        this.products = order.getProducts();
        this.price = order.getPrice();
        this.status = order.getStatus();
    }
}
