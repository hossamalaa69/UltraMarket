package com.example.ultramarket.database.Repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.ultramarket.database.AppDatabase;
import com.example.ultramarket.database.DAOs.BrandDao;
import com.example.ultramarket.database.Entities.Brand;
import com.example.ultramarket.firebase.realtime_database.BrandFbDao;
import com.example.ultramarket.helpers.AppExecutors;
import com.example.ultramarket.helpers.Utils;

import java.util.ArrayList;
import java.util.List;

public class BrandRepository {
    private LiveData<List<Brand>> mBrandList;
    private BrandDao brandDao;

    public BrandRepository(Application application) {
        if (Utils.LOCAL) {
            AppDatabase db = AppDatabase.getInstance(application.getApplicationContext());
            brandDao = db.brandDao();
        } else {
            brandDao = BrandFbDao.getsInstance(application.getApplicationContext());
        }
        mBrandList = brandDao.loadAllBrands();
    }

    // user categories DAO
    public void insertBrand(Brand brand) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                brandDao.insertBrand(brand);
            }
        });
    }

    public void insertBrandList(ArrayList<Brand> brands) {

    }

    public void updateBrand(Brand brand) {
    }

    public void updateBrandById(String id) {
    }

    public LiveData<List<Brand>> loadAllBrands() {
        return mBrandList;
    }

    public LiveData<Brand> loadBrandById(String id) {
        return null;
    }

    public void deleteBrand(Brand brand) {
    }

    public void deleteBrandById(String id) {

    }
    //----------------------------------------------------------------------------------------------
}
