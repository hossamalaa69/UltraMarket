package com.example.ultramarket.database.DAOs;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.ultramarket.database.Entities.Category;
import com.example.ultramarket.database.Entities.Product;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface ProdDao {
    @Query("SELECT * FROM product ORDER BY launch_date DESC")
    LiveData<List<Product>> loadAllLatestProducts();

    @Query("SELECT * FROM product WHERE hasOffer LIKE 1")
    LiveData<List<Product>> loadFeaturedProducts();

    @Insert
    void insertProduct(Product product);
}
