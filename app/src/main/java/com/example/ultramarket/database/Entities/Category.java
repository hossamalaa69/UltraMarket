package com.example.ultramarket.database.Entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "category")
public class Category {

    @NotNull
    @PrimaryKey
    private long ID;

    private String name;

    private String image;

    @Ignore
    public Category() {
    }

    public Category(long ID, String name, String image) {
        this.ID = ID;
        this.name = name;
        this.image = image;
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
}
