package com.example.ultramarket.firebase.realtime_database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ultramarket.database.AppDatabase;
import com.example.ultramarket.database.DAOs.CatDao;
import com.example.ultramarket.database.Entities.Category;
import com.example.ultramarket.helpers.AppExecutors;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CategoryFbDao implements CatDao {

    private static CatDao sInstance;
    private Context mContext;

    public static CatDao getsInstance(Context context) {
        if (sInstance == null) {
            sInstance = new CategoryFbDao(context);
        }
        return sInstance;
    }

    @Override
    public void clearTable() {
        // only used by admin users
    }

    public CategoryFbDao(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public LiveData<List<Category>> loadAllCategories() {
        MutableLiveData<List<Category>> liveData = new MutableLiveData<>();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(Category.class.getSimpleName());
        List<Category> categories = new ArrayList<>();
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    categories.add(snap.getValue(Category.class));
                }
                liveData.setValue(categories);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return liveData;
    }

    @Override
    public void insertCategory(Category category) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(Category.class.getSimpleName());
        dbRef.push().setValue(category);
    }
}
