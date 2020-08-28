package com.example.ultramarket.database.Entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

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
public class Cart {

    @NotNull
    @PrimaryKey
    private long ID;

    private long user_ID;

    private long product_ID;

    private int count;

    private double price;

    private Date date;
    private boolean isOrdered = false;

    public Cart(long ID, long user_ID, long product_ID, int count, double price, Date date) {
        this.ID = ID;
        this.user_ID = user_ID;
        this.product_ID = product_ID;
        this.count = count;
        this.price = price;
        this.date = date;
    }

    public boolean isOrdered() {
        return isOrdered;
    }

    public void setIsOrdered(boolean is_ordered) {
        this.isOrdered = is_ordered;
    }

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public long getUser_ID() {
        return user_ID;
    }

    public void setUser_ID(long user_ID) {
        this.user_ID = user_ID;
    }

    public long getProduct_ID() {
        return product_ID;
    }

    public void setProduct_ID(long product_ID) {
        this.product_ID = product_ID;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
