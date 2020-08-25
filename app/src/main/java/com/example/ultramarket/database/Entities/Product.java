package com.example.ultramarket.database.Entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
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
public class Product implements Comparable<Product>{

    @NotNull
    @PrimaryKey
    private String ID;

    private String name;

    private String image;

    private String unit;

    private double price;

    private int count;

    private String description;

    private boolean hasOffer;

    private double percentage;

    private String brand_ID;

    private String category_ID;

    private String launch_date;

    public void setLaunch_date(String launch_date) {
        this.launch_date = launch_date;
    }

    public String getLaunch_date() {
        return launch_date;
    }

    @Ignore
    public Product() {
    }

    public Product(String ID, String name, String image, String unit,
                   double price, int count, String description,
                   boolean hasOffer, double percentage, String brand_ID, String category_ID, String launch_date) {
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

    @Override
    public int compareTo(Product product) {
        return  this.getLaunch_date().compareTo(product.getLaunch_date());
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
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

    public String getBrand_ID() {
        return brand_ID;
    }

    public void setBrand_ID(String brand_ID) {
        this.brand_ID = brand_ID;
    }

    public String getCategory_ID() {
        return category_ID;
    }

    public void setCategory_ID(String category_ID) {
        this.category_ID = category_ID;
    }
}
