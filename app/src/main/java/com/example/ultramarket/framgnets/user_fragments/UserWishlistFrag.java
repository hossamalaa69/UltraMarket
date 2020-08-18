package com.example.ultramarket.framgnets.user_fragments;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ultramarket.R;

public class UserWishlistFrag extends Fragment {
    public static final CharSequence TITLE = "Wish list";

    private UserWishlistViewModel mViewModel;

    public static UserWishlistFrag newInstance() {
        return new UserWishlistFrag();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user_wishlist_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(UserWishlistViewModel.class);
        // TODO: Use the ViewModel
    }

}