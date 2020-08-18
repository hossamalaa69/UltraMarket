package com.example.ultramarket.database.DAOs;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.ultramarket.database.Entities.Category;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface CatDao {
    @Query("SELECT * FROM category")
    LiveData<List<Category>> loadAllCategories();

    @Insert
    void insertCategory(Category category);

}
