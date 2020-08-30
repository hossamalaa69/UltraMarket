package com.example.ultramarket.ui.userUi.Activities;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ultramarket.database.Entities.Brand;
import com.example.ultramarket.database.Entities.Category;
import com.example.ultramarket.database.Entities.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CategoriesBrandsProductsViewModel extends ViewModel {
    private MutableLiveData<List<Product>> products = new MutableLiveData<>();
    public CategoriesBrandsProductsViewModel(int type, String id) {
        String childIdOfType;
        switch (type) {
            case Category.TYPE_ID:
                childIdOfType = "category_ID";
                break;
            case Brand.TYPE_ID:
                childIdOfType = "brand_ID";
                break;
            default:
                return;
        }
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(Product.class.getSimpleName());
        Query query = ref.orderByChild(childIdOfType).equalTo(id);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Product> productList = new ArrayList<>();
                for (DataSnapshot shot : snapshot.getChildren()) {
                    productList.add(shot.getValue(Product.class));
                }
                products.setValue(productList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public LiveData<List<Product>> getProducts() {
        return products;
    }
}
