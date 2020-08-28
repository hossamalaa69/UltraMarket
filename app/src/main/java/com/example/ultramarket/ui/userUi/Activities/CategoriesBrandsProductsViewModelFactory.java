package com.example.ultramarket.ui.userUi.Activities;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

public class CategoriesBrandsProductsViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private String  id;
    private int type;

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new CategoriesBrandsProductsViewModel(type, id);
    }

    public CategoriesBrandsProductsViewModelFactory(int type, String id) {
        this.id = id;
        this.type = type;
    }
}
