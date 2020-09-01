package com.example.ultramarket.framgnets.user_fragments;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ultramarket.R;
import com.example.ultramarket.adapters.user_adapters.OfferAdapter;
import com.example.ultramarket.database.Entities.Product;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserOffersFrag extends Fragment implements OfferAdapter.ProductCallBacks {
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
        mOfferAdapter = new OfferAdapter(getContext(), null,this);
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
        mViewModel.getFeaturedProdList().observe(getViewLifecycleOwner(), products -> mOfferAdapter.setProdList(products));
        FirebaseDatabase.getInstance().getReference()
                .child(Product.class.getSimpleName()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                mOfferAdapter.updateProduct(snapshot.getValue(Product.class));
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onProductClickedListener(Intent intent, View shared1, View shared2) {
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(
                getActivity(),
                Pair.create(shared1,"image"),
                Pair.create(shared2,"saved_money")).toBundle());
    }
}