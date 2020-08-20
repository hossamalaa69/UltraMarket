package com.example.ultramarket.firebase.realtime_database;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ultramarket.R;
import com.example.ultramarket.database.AppDatabase;
import com.example.ultramarket.database.DAOs.ProdDao;
import com.example.ultramarket.database.Entities.Product;
import com.example.ultramarket.helpers.AppExecutors;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProductFbDao implements ProdDao {
    private static ProdDao sInstance;
    private Context mContext;

    public static ProdDao getsInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ProductFbDao(context);
        }
        return sInstance;
    }

    public ProductFbDao(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public LiveData<List<Product>> loadAllLatestProducts() {
        MutableLiveData<List<Product>> liveData = new MutableLiveData<>();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(Product.class.getSimpleName());
        List<Product> products = new ArrayList<>();
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ProdDao prodDao = AppDatabase.getInstance(mContext).prodDao();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    products.add(snap.getValue(Product.class));
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            prodDao.insertProduct(snap.getValue(Product.class));
                        }
                    });//insert into local database
                }
                Collections.sort(products);
                liveData.setValue(products);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, mContext.getString(R.string.try_again), Toast.LENGTH_SHORT).show();
            }
        });
        return liveData;
    }

    @Override
    public LiveData<List<Product>> loadFeaturedProducts() {
        MutableLiveData<List<Product>> liveData = new MutableLiveData<>();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(Product.class.getSimpleName());
        List<Product> products = new ArrayList<>();
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ProdDao prodDao = AppDatabase.getInstance(mContext).prodDao();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Product product = snap.getValue(Product.class);
                    if (product != null && product.isHasOffer()) {
                        products.add(product);
                        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                prodDao.insertProduct(snap.getValue(Product.class));
                            }
                        });//insert into local database
                    }
                }
                Collections.sort(products);
                liveData.setValue(products);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, mContext.getString(R.string.try_again), Toast.LENGTH_SHORT).show();
            }
        });
        return liveData;
    }

    @Override
    public void insertProduct(Product product) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(Product.class.getSimpleName());
        dbRef.push().setValue(product);
    }

    @Override
    public void clearTable() {
        // only used by admin users
    }
}
