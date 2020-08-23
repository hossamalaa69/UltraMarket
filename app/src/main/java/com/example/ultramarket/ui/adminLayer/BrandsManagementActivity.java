package com.example.ultramarket.ui.adminLayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ultramarket.R;
import com.example.ultramarket.adapters.BrandsAdminAdapter;
import com.example.ultramarket.adapters.CustomersAdapter;
import com.example.ultramarket.database.Entities.Brand;

import java.util.ArrayList;
import java.util.List;

public class BrandsManagementActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    BrandsAdminAdapter brandsAdminAdapter;
    ProgressBar progressBar_brands;

    BrandsManagementViewModel brandsManagementViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brands_management);

        initRecycler();

        setupViewModel();

        setupSwipe();
    }

    private void initRecycler(){
        progressBar_brands = (ProgressBar) findViewById(R.id.brands_progress);
        recyclerView = findViewById(R.id.brands_recycler);
        brandsAdminAdapter = new BrandsAdminAdapter(this, new ArrayList<Brand>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(brandsAdminAdapter);
    }

    private void setupViewModel(){
        brandsManagementViewModel = new ViewModelProvider(this).get(BrandsManagementViewModel.class);
        brandsManagementViewModel.loadAllBrands();

        brandsManagementViewModel.loadAllBrands().observe(this, new Observer<List<Brand>>() {
            @Override
            public void onChanged(List<Brand> brands) {
                brandsAdminAdapter.setBrandList(brands);
                progressBar_brands.setVisibility(View.GONE);
            }
        });
    }

    private void setupSwipe(){

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Here is where you'll implement swipe to delete
                  progressBar_brands.setVisibility(View.VISIBLE);
                  int position = viewHolder.getAdapterPosition();
                  List<Brand> brands = brandsAdminAdapter.getBrands();
                  Toast.makeText(BrandsManagementActivity.this, ""+position, Toast.LENGTH_SHORT).show();
                  brandsManagementViewModel.deleteBrand(brands.get(position));
            }
        }).attachToRecyclerView(recyclerView);
    }

    public void addBrand(View view) {
        Intent i = new Intent(this, BrandActivity.class);
        startActivity(i);
    }
}