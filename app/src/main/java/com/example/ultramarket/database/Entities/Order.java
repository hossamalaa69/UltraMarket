package com.example.ultramarket.database.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "cart",
        foreignKeys = @ForeignKey(entity = Cart.class,
                parentColumns = "ID",
                childColumns = "cart_ID",
                onDelete = ForeignKey.SET_DEFAULT))

public class Order {

    @PrimaryKey(autoGenerate = true)
    private long ID;

    @ColumnInfo(defaultValue = "-1")
    private long cart_ID;

    private Date estimated_time;

    private double price;

    public Order(long ID, long cart_ID, Date estimated_time, double price) {
        this.ID = ID;
        this.cart_ID = cart_ID;
        this.estimated_time = estimated_time;
        this.price = price;
    }

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public long getCart_ID() {
        return cart_ID;
    }

    public void setCart_ID(long cart_ID) {
        this.cart_ID = cart_ID;
    }

    public Date getEstimated_time() {
        return estimated_time;
    }

    public void setEstimated_time(Date estimated_time) {
        this.estimated_time = estimated_time;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
