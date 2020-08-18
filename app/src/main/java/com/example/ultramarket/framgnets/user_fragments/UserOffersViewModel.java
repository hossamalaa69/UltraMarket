package com.example.ultramarket.framgnets.user_fragments;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.ultramarket.database.Entities.Product;
import com.example.ultramarket.database.Repositories.ProductRepository;

import java.util.List;

public class UserOffersViewModel extends AndroidViewModel {
    private ProductRepository mProdRepo;
    private LiveData<List<Product>> mProdList;

    public UserOffersViewModel(@NonNull Application application) {
        super(application);
        mProdRepo = new ProductRepository(application);
        mProdList = mProdRepo.loadFeaturedProdList();
    }

    public LiveData<List<Product>> getFeaturedProdList() {
        return mProdList;
    }

}