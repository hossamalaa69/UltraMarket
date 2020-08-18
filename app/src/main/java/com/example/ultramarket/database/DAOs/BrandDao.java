package com.example.ultramarket.database.DAOs;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.ultramarket.database.Entities.Brand;
import com.example.ultramarket.database.Entities.Category;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface BrandDao {
    @Query("SELECT * FROM brand")
    LiveData<List<Brand>> loadAllBrands();

    @Insert
    void insertBrand(Brand brand);
}
