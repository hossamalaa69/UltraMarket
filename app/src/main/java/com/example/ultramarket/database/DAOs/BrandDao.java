package com.example.ultramarket.database.DAOs;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.ultramarket.database.Entities.Brand;

import java.util.List;

@Dao
public interface BrandDao {
    @Query("SELECT * FROM brand")
    LiveData<List<Brand>> loadAllBrands();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBrand(Brand brand);

    @Query("DELETE FROM brand")
    void clearTable();
}
