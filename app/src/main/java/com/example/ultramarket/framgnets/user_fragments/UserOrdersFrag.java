package com.example.ultramarket.framgnets.user_fragments;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ultramarket.R;

public class UserOrdersFrag extends Fragment {
    public static final CharSequence TITLE = "Orders";

    private UserOrdersViewModel mViewModel;

    public static UserOrdersFrag newInstance() {
        return new UserOrdersFrag();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user_orders_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(UserOrdersViewModel.class);
        // TODO: Use the ViewModel
    }

}