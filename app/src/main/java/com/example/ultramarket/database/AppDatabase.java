package com.example.ultramarket.database;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.ultramarket.database.DAOs.BrandDao;
import com.example.ultramarket.database.DAOs.CatDao;
import com.example.ultramarket.database.DAOs.ProdDao;
import com.example.ultramarket.database.Entities.Brand;
import com.example.ultramarket.database.Entities.Category;
import com.example.ultramarket.database.Entities.Product;
import com.example.ultramarket.database.Entities.User;

@Database(entities = {Brand.class,Category.class, Product.class, User.class}
        , version = 2, exportSchema = false)

@TypeConverters(DateConverter.class)
public abstract class AppDatabase extends RoomDatabase {

    private static final String LOG_TAG = AppDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "ultra_market";
    private static AppDatabase sInstance;


    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(LOG_TAG, "Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, AppDatabase.DATABASE_NAME)
                        .fallbackToDestructiveMigration()
                        .build();
            }
        }
        Log.d(LOG_TAG, "Getting the database instance");
        return sInstance;
    }

    //  abstract UltraDao Daos
    public abstract CatDao catDao();

    public abstract ProdDao prodDao();

    public abstract BrandDao brandDao();

}

