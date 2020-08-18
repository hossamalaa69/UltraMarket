package com.example.ultramarket.database.Entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

@Entity(tableName = "product", indices = {@Index(value = "category_ID"), @Index(value = "brand_ID")},
        foreignKeys = {@ForeignKey(entity = Category.class,
                parentColumns = "ID",
                childColumns = "category_ID",
                onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Brand.class,
                        parentColumns = "ID",
                        childColumns = "brand_ID",
                        onDelete = ForeignKey.CASCADE)
        })
public class Product {

    @NotNull
    @PrimaryKey
    private long ID;

    private String name;

    private String image;

    private String unit;

    private double price;

    private int count;

    private String description;

    private boolean hasOffer;

    private double percentage;

    private long brand_ID;

    private long category_ID;
    private Date launch_date;

    public void setLaunch_date(Date launch_date) {
        this.launch_date = launch_date;
    }

    public Date getLaunch_date() {
        return launch_date;
    }

    public Product(long ID, String name, String image, String unit,
                   double price, int count, String description,
                   boolean hasOffer, double percentage, long brand_ID, long category_ID, Date launch_date) {
        this.ID = ID;
        this.name = name;
        this.image = image;
        this.unit = unit;
        this.price = price;
        this.count = count;
        this.description = description;
        this.hasOffer = hasOffer;
        this.percentage = percentage;
        this.brand_ID = brand_ID;
        this.category_ID = category_ID;
        this.launch_date = launch_date;
    }

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isHasOffer() {
        return hasOffer;
    }

    public void setHasOffer(boolean hasOffer) {
        this.hasOffer = hasOffer;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public long getBrand_ID() {
        return brand_ID;
    }

    public void setBrand_ID(long brand_ID) {
        this.brand_ID = brand_ID;
    }

    public long getCategory_ID() {
        return category_ID;
    }

    public void setCategory_ID(long category_ID) {
        this.category_ID = category_ID;
    }
}
