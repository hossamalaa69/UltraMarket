package com.example.ultramarket.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.example.ultramarket.database.Entities.User;

import java.util.List;

@Dao
public interface UltraDao {

    @Query("SELECT * FROM user")
    LiveData<List<User>> loadAllTasks();

}


