package com.example.ultramarket.ui.adminLayer;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.ultramarket.database.Entities.Brand;
import com.example.ultramarket.database.Entities.Category;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class CategoriesManagementViewModel extends AndroidViewModel {

    MutableLiveData<List<Category>> categoryListMutableLiveData = new MutableLiveData<>();

    public CategoriesManagementViewModel(@NonNull Application application) {
        super(application);

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(Category.class.getSimpleName());
        List<Category> categoryList = new ArrayList<>();
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Category category = snap.getValue(Category.class);
                    categoryList.add(category);
                }
                categoryListMutableLiveData.setValue(categoryList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(application.getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public MutableLiveData<List<Category>> loadAllCategories(){
        return categoryListMutableLiveData;
    }

    public void deleteCategory(Category category) {

        String id = category.getID();
        String imageUrl = category.getImage();

        StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
        imageRef.delete().addOnSuccessListener(aVoid -> {
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(Category.class.getSimpleName()).child(id);
            dbRef.removeValue((error, ref) -> Toast.makeText(getApplication().getApplicationContext(), "deleted successfully", Toast.LENGTH_SHORT).show());

        }).addOnFailureListener(exception -> {
            Toast.makeText(getApplication().getApplicationContext(), "Failed deletion", Toast.LENGTH_SHORT).show();
        });
    }
}

