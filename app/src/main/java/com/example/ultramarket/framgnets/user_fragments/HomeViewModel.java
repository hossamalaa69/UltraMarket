package com.example.ultramarket.framgnets.user_fragments;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.ultramarket.database.Entities.Brand;
import com.example.ultramarket.database.Entities.Category;
import com.example.ultramarket.database.Entities.Product;
import com.example.ultramarket.database.Repositories.BrandRepository;
import com.example.ultramarket.database.Repositories.CategoryRepository;
import com.example.ultramarket.database.Repositories.ProductRepository;
import com.example.ultramarket.helpers.Utils;

import java.util.List;

import okhttp3.internal.Util;

public class HomeViewModel extends AndroidViewModel {
    private LiveData<List<Product>> mLatestProdList;
    private LiveData<List<Brand>> mBrandList;
    private LiveData<List<Category>> mCatList;
    private LiveData<List<Product>> mFeaturedProdList;
    private CategoryRepository catRepo;
    private BrandRepository brandRepo;
    private ProductRepository prodRepo;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        catRepo = new CategoryRepository(application);
        mCatList = catRepo.loadAllCategories();

        prodRepo = new ProductRepository(application);
        mLatestProdList = prodRepo.loadAllLatestProducts();
        mFeaturedProdList = prodRepo.loadFeaturedProdList();

        brandRepo = new BrandRepository(application);
        mBrandList = brandRepo.loadAllBrands();
    }

    public LiveData<List<Category>> getmCatList() {
        return mCatList;
    }

    public void insertCategory(Category category) {
        catRepo.insertCategory(category);
    }

    public void insertBrand(Brand brand) {
        brandRepo.insertBrand(brand);
    }

    public void insertProd(Product product) {
        prodRepo.insertProduct(product);
    }

    public LiveData<List<Product>> getmLatestProdList() {
        return mLatestProdList;
    }

    public LiveData<List<Brand>> getmBrandList() {
        return mBrandList;
    }

    public LiveData<List<Product>> getmFeaturedProdList() {
        return mFeaturedProdList;
    }
}