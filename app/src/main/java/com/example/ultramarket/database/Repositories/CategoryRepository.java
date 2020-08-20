package com.example.ultramarket.database.Repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.ultramarket.database.AppDatabase;
import com.example.ultramarket.database.DAOs.CatDao;
import com.example.ultramarket.database.Entities.Category;
import com.example.ultramarket.firebase.realtime_database.CategoryFbDao;
import com.example.ultramarket.helpers.AppExecutors;
import com.example.ultramarket.helpers.Utils;

import java.util.ArrayList;
import java.util.List;

public class CategoryRepository {
    private LiveData<List<Category>> categoryList;
    private CatDao catDao;
    public CategoryRepository(Application application) {
        if (Utils.LOCAL) {
            AppDatabase db = AppDatabase.getInstance(application.getApplicationContext());
            catDao = db.catDao();
        } else {
            catDao = CategoryFbDao.getsInstance(application.getApplicationContext());
        }
        categoryList = catDao.loadAllCategories();
    }
    // user categories DAO
    public void insertCategory(Category category) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                catDao.insertCategory(category);
            }
        });
    }

    public void insertCategoryList(ArrayList<Category> categories) {
    }

    public void updateCategory(Category category) {
    }

    public void updateCategoryById(String id) {
    }

    public LiveData<List<Category>> loadAllCategories() {
        return categoryList;
    }

    public LiveData<Category> loadCategoryById(String id) {
        return null;
    }

    public void deleteCategory(Category category) {
    }

    public void deleteCategoryById(String id) {
    }

    //----------------------------------------------------------------------------------------------
}
