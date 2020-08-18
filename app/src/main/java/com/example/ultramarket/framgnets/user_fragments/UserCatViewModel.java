package com.example.ultramarket.framgnets.user_fragments;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.ultramarket.database.Entities.Category;
import com.example.ultramarket.database.Repositories.CategoryRepository;

import java.util.List;

public class UserCatViewModel extends AndroidViewModel {
    private CategoryRepository catRepo;
    private LiveData<List<Category>> categories;

    public UserCatViewModel(@NonNull Application application) {
        super(application);
        catRepo = new CategoryRepository(application);
        categories = catRepo.loadAllCategories();
    }

    public LiveData<List<Category>> loadAllCategories() {
        return categories;
    }
}