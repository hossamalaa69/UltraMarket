package com.example.ultramarket.ui.adminLayer;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.ultramarket.database.Entities.Brand;
import com.example.ultramarket.database.Entities.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ProductManagementViewModel extends AndroidViewModel {

    MutableLiveData<List<Product>> productListMutableLiveData = new MutableLiveData<>();

    public ProductManagementViewModel(@NonNull Application application) {
        super(application);

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(Product.class.getSimpleName());
        List<Product> productList = new ArrayList<>();
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Product product = snap.getValue(Product.class);
                    product.setBrand_name("BrandX");
                    product.setCategory_name("CategoryY");
                    product.setOrders_number(12);
                    productList.add(product);
                }
                productListMutableLiveData.setValue(productList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(application.getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public MutableLiveData<List<Product>> loadAllProducts() {
        return productListMutableLiveData;
    }

    public void deleteProduct(Product product) {

        String id = product.getID();
        String imageUrl = product.getImage();

        StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
        imageRef.delete().addOnSuccessListener(aVoid -> {
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(Product.class.getSimpleName()).child(id);
            dbRef.removeValue((error, ref) -> Toast.makeText(getApplication().getApplicationContext(), "deleted successfully", Toast.LENGTH_SHORT).show());

        }).addOnFailureListener(exception -> {
            Toast.makeText(getApplication().getApplicationContext(), "Failed deletion", Toast.LENGTH_SHORT).show();
        });
    }
}

