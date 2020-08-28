package com.example.ultramarket.framgnets.user_fragments;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class UserCartViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private String userId;

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new UserCartViewModel(userId);
    }

    public UserCartViewModelFactory(String userId) {
        this.userId = userId;
    }
}
