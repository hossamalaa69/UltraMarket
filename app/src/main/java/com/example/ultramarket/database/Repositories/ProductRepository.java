package com.example.ultramarket.database.Repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.ultramarket.database.AppDatabase;
import com.example.ultramarket.database.DAOs.ProdDao;
import com.example.ultramarket.database.Entities.Product;
import com.example.ultramarket.helpers.AppExecutors;

import java.util.ArrayList;
import java.util.List;

public class ProductRepository {
    private LiveData<List<Product>> mProdList;
    private ProdDao prodDao;
    private LiveData<List<Product>> mFeaturedProdList;
    private LiveData<List<Product>> mProdWithOffersList;

    public ProductRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application.getApplicationContext());
        prodDao = db.prodDao();
        mProdList = prodDao.loadAllLatestProducts();
        mFeaturedProdList = prodDao.loadFeaturedProducts();
    }

    // user categories DAO
    public void insertProduct(Product product) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                prodDao.insertProduct(product);
            }
        });
    }


    public void insertProductList(ArrayList<Product> products) {
    }

    public void updateProduct(Product product) {
    }

    public void updateProductById(String id) {
    }

    public LiveData<List<Product>> loadAllLatestProducts() {
        return mProdList;
    }

    public LiveData<Product> loadProductById(String id) {
        return null;
    }

    public void deleteProduct(Product product) {
    }

    public void deleteProductById(String id) {
    }

    public LiveData<List<Product>> loadFeaturedProdList() {
        return mFeaturedProdList;
    }


    //----------------------------------------------------------------------------------------------
}