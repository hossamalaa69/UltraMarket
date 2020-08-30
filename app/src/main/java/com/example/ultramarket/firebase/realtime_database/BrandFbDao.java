package com.example.ultramarket.firebase.realtime_database;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ultramarket.R;
import com.example.ultramarket.database.AppDatabase;
import com.example.ultramarket.database.DAOs.BrandDao;
import com.example.ultramarket.database.Entities.Brand;
import com.example.ultramarket.helpers.AppExecutors;
import com.example.ultramarket.helpers.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BrandFbDao implements BrandDao {

    private static BrandDao sInstance;
    private Context mContext;


    @Override
    public void clearTable() {
        // only used by admin users
    }

    public static BrandDao getsInstance(Context context) {
        if (sInstance == null) {
            sInstance = new BrandFbDao(context);
        }
        return sInstance;
    }

    public BrandFbDao(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public LiveData<List<Brand>> loadAllBrands() {
        MutableLiveData<List<Brand>> brandListLivedata = new MutableLiveData<>();
        List<Brand> brands = new ArrayList<>();
        DatabaseReference mBrandsRef = FirebaseDatabase.getInstance().getReference().child(Brand.class.getSimpleName());
        mBrandsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //get instance of database to add fetched data
                for (DataSnapshot snap : snapshot.getChildren()) {
                    brands.add(snap.getValue(Brand.class));
                }
                brandListLivedata.setValue(brands);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return brandListLivedata;
    }

    // used only by admin users
    @Override
    public void insertBrand(Brand brand) {
        DatabaseReference mBrandsRef = FirebaseDatabase.getInstance().getReference().child(Brand.class.getSimpleName());
        mBrandsRef.push().setValue(brand).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Utils.createToast(mContext, R.string.brand_saved, Toast.LENGTH_SHORT);
            }
        });
    }
}
