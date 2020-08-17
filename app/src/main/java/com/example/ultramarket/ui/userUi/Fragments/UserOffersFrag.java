package com.example.ultramarket.ui.userUi.Fragments;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ultramarket.R;

public class UserOffersFrag extends Fragment {
    public static final CharSequence TITLE = "Offers";

    private UserOffersViewModel mViewModel;

    public static UserOffersFrag newInstance() {
        return new UserOffersFrag();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user_offers_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(UserOffersViewModel.class);
        // TODO: Use the ViewModel
    }

}