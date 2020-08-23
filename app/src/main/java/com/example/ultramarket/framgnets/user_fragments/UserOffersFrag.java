package com.example.ultramarket.framgnets.user_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ultramarket.R;
import com.example.ultramarket.adapters.user_adapters.OfferAdapter;
import com.example.ultramarket.database.Entities.Product;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserOffersFrag extends Fragment {
    public static final CharSequence TITLE = "Offers";
    @BindView(R.id.usr_rv_offers)
    RecyclerView rvOffers;
    private RecyclerView.LayoutManager mOffersLayoutManager;
    private OfferAdapter mOfferAdapter;

    private UserOffersViewModel mViewModel;

    public static UserOffersFrag newInstance() {
        return new UserOffersFrag();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_offers_fragment, container, false);
        ButterKnife.bind(this, view);
        mOfferAdapter = new OfferAdapter(getContext(), null);
        mOffersLayoutManager = new LinearLayoutManager(getContext());
        rvOffers.setHasFixedSize(true);
        rvOffers.setLayoutManager(mOffersLayoutManager);
        rvOffers.setAdapter(mOfferAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(UserOffersViewModel.class);
        setupObservers();
    }

    private void setupObservers() {
        mViewModel.getFeaturedProdList().observe(getViewLifecycleOwner(), new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                mOfferAdapter.setProdList(products);
            }
        });
    }

}