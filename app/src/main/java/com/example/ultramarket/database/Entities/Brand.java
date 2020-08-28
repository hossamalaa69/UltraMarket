package com.example.ultramarket.database.Entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "brand")
public class Brand {

    public static final int TYPE_ID = 2;
    @NotNull
    @PrimaryKey
    private String ID;

    private String name;

    private String image;

    public Brand(String ID, String name, String image) {
        this.ID = ID;
        this.name = name;
        this.image = image;
    }

    @Ignore
    public Brand() {
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
}
