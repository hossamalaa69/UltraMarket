package com.example.ultramarket.ui.adminLayer;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.ultramarket.database.Entities.Brand;
import com.example.ultramarket.database.Entities.Order;
import com.example.ultramarket.database.Entities.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BrandsDashboardViewModel extends AndroidViewModel {

    private MutableLiveData<Map<String, Integer>> brandsStatsMutable = new MutableLiveData<>();

    public BrandsDashboardViewModel(@NonNull Application application) {
        super(application);

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(Order.class.getSimpleName());
        Map<String, Integer> products = new HashMap<>();
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                products.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    for (DataSnapshot s: snap.getChildren()){
                        Order order = s.getValue(Order.class);
                        for (Map.Entry<String,Integer> entry : order.getProducts().entrySet()) {
                            int oldQuantity = 0;
                            if(products.get(entry.getKey()) != null)
                                oldQuantity = products.get(entry.getKey());
                            products.put(entry.getKey(), entry.getValue() + oldQuantity);
                        }
                    }
                }

                Map<String, Integer> brandsIDsQuantities = new HashMap<>();
                DatabaseReference prodDbRef = FirebaseDatabase.getInstance().getReference().child(Product.class.getSimpleName());
                prodDbRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        brandsIDsQuantities.clear();
                        int oldCount;
                        for(DataSnapshot snap:snapshot.getChildren()){
                            Product p = snap.getValue(Product.class);
                            oldCount = 0;
                            if(brandsIDsQuantities.get(p.getBrand_ID()) != null)
                                oldCount = brandsIDsQuantities.get(p.getBrand_ID());
                            brandsIDsQuantities.put(p.getBrand_ID(), products.get(p.getID()) + oldCount);
                        }
                        Map<String, Integer> brandsNamesQuantities = new HashMap<>();
                        DatabaseReference brandsDbRef = FirebaseDatabase.getInstance().getReference().child(Brand.class.getSimpleName());
                        brandsDbRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                brandsNamesQuantities.clear();
                                for (DataSnapshot snap : snapshot.getChildren()) {
                                    Brand brand = snap.getValue(Brand.class);
                                    if(brandsIDsQuantities.get(brand.getID()) != null){
                                        brandsNamesQuantities.put(brand.getName(), brandsIDsQuantities.get(brand.getID()));
                                    }
                                }
                                brandsStatsMutable.setValue(brandsNamesQuantities);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(application.getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(application.getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(application.getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public MutableLiveData<Map<String, Integer>> loadAllBrandsStats(){
        return brandsStatsMutable;
    }

}

