package com.example.ultramarket.framgnets.user_fragments;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class UserCartViewModel extends ViewModel {
    private MutableLiveData<Map.Entry<String, Integer>> products = new MutableLiveData<>();

    public UserCartViewModel(String userId) {
        loadData(userId);
    }

    private void loadData(String userId) {
        FirebaseDatabase.getInstance().getReference()
                .child("Cart").child(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(!snapshot.exists()){

                }
                products.setValue(new Map.Entry<String, Integer>() {
                    @Override
                    public String getKey() {
                        return snapshot.getKey();
                    }

                    @Override
                    public Integer getValue() {
                        return snapshot.getValue(Integer.class);
                    }

                    @Override
                    public Integer setValue(Integer integer) {
                        return integer;
                    }
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                products.setValue(new Map.Entry<String, Integer>() {
                    @Override
                    public String getKey() {
                        return snapshot.getKey();
                    }

                    @Override
                    public Integer getValue() {
                        return snapshot.getValue(Integer.class);
                    }

                    @Override
                    public Integer setValue(Integer integer) {
                        return integer;
                    }
                });

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                int y =0 ;
            }
        });
    }

    public MutableLiveData<Map.Entry<String, Integer>> getProducts() {
        return products;
    }
}