package com.example.ultramarket.ui.adminLayer;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.ultramarket.database.Entities.Category;
import com.example.ultramarket.database.Entities.Order;
import com.example.ultramarket.database.Entities.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class CategoriesDashboardViewModel extends AndroidViewModel {

    private MutableLiveData<Map<String, Integer>> categoriesStatsMutable = new MutableLiveData<>();

    public CategoriesDashboardViewModel(@NonNull Application application) {
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

                Map<String, Integer> categoriesIDsQuantities = new HashMap<>();
                DatabaseReference prodDbRef = FirebaseDatabase.getInstance().getReference().child(Product.class.getSimpleName());
                prodDbRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        categoriesIDsQuantities.clear();
                        int oldCount;
                        for(DataSnapshot snap:snapshot.getChildren()){
                            Product p = snap.getValue(Product.class);
                            oldCount = 0;
                            if(categoriesIDsQuantities.get(p.getCategory_ID()) != null)
                                oldCount = categoriesIDsQuantities.get(p.getCategory_ID());
                            categoriesIDsQuantities.put(p.getCategory_ID(), products.get(p.getID()) + oldCount);
                        }
                        Map<String, Integer> categoriesNamesQuantities = new HashMap<>();
                        DatabaseReference categoriesDbRef = FirebaseDatabase.getInstance().getReference().child(Category.class.getSimpleName());
                        categoriesDbRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                categoriesNamesQuantities.clear();
                                for (DataSnapshot snap : snapshot.getChildren()) {
                                    Category category = snap.getValue(Category.class);
                                    if(categoriesIDsQuantities.get(category.getID()) != null){
                                        categoriesNamesQuantities.put(category.getName(), categoriesIDsQuantities.get(category.getID()));
                                    }
                                }
                                categoriesStatsMutable.setValue(categoriesNamesQuantities);
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

    public MutableLiveData<Map<String, Integer>> loadAllCategoriesStats(){
        return categoriesStatsMutable;
    }

}

