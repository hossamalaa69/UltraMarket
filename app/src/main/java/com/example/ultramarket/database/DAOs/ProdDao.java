package com.example.ultramarket.database.DAOs;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.ultramarket.database.Entities.Product;

import java.util.List;

@Dao
public interface ProdDao {
    @Query("SELECT * FROM product ORDER BY launch_date DESC")
    LiveData<List<Product>> loadAllLatestProducts();

    @Query("SELECT * FROM product WHERE hasOffer LIKE 1")
    LiveData<List<Product>> loadFeaturedProducts();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertProduct(Product product);

    @Query("DELETE FROM product")
    void clearTable();



}
