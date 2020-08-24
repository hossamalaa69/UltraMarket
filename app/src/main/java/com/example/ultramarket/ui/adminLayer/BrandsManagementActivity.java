package com.example.ultramarket.ui.adminLayer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ultramarket.R;
import com.example.ultramarket.adapters.BrandsAdminAdapter;
import com.example.ultramarket.adapters.CustomersAdapter;
import com.example.ultramarket.adapters.StatsAdapter;
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

        //holds listener for clicking the notification item
        brandsAdminAdapter.setOnItemClickListener(new StatsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //gets index of item pressed, then send it to its target
                sendToUpdate(position);
            }
        });
    }

    private void sendToUpdate(int position) {
        //Toast.makeText(this, ""+position, Toast.LENGTH_SHORT).show();
        Brand brand = brandsAdminAdapter.getBrands().get(position);
        Intent i = new Intent(this, BrandActivity.class);
        i.putExtra("ID", brand.getID());
        i.putExtra("imageUrl", brand.getImage());
        i.putExtra("brand_name", brand.getName());
        startActivity(i);
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

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {

                AlertDialog.Builder builder = new AlertDialog.Builder(BrandsManagementActivity.this);
                //Setting message manually and performing action on button click
                builder.setMessage("Do you want to delete this brand ? \nAll products related to this brand will be deleted!")
                        .setCancelable(false)
                        .setPositiveButton("Yes", (dialog, id) -> {
                            dialog.cancel();
                            progressBar_brands.setVisibility(View.VISIBLE);
                            int position = viewHolder.getAdapterPosition();
                            List<Brand> brands = brandsAdminAdapter.getBrands();
                            Toast.makeText(BrandsManagementActivity.this, ""+position, Toast.LENGTH_SHORT).show();
                            brandsManagementViewModel.deleteBrand(brands.get(position));
                        })
                        .setNegativeButton("No", (dialog, id) -> {
                            brandsAdminAdapter.notifyDataSetChanged();
                            dialog.cancel();
                        });
                AlertDialog alert = builder.create();
                alert.setTitle("Delete Brand!!!");
                alert.show();
            }
        }).attachToRecyclerView(recyclerView);
    }

    public void addBrand(View view) {
        Intent i = new Intent(this, BrandActivity.class);
        startActivity(i);
    }
}