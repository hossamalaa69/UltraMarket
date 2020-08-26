package com.example.ultramarket.ui.adminLayer;

import androidx.appcompat.app.AlertDialog;
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
import com.example.ultramarket.adapters.ProductAdminAdapter;
import com.example.ultramarket.database.Entities.Brand;
import com.example.ultramarket.database.Entities.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductsManagementActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ProductAdminAdapter productAdminAdapter;
    ProgressBar progressBar_product;

    ProductManagementViewModel productManagementViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_management);

        initRecycler();

        setupViewModel();

        setupSwipe();
    }

    private void initRecycler(){
        progressBar_product = (ProgressBar) findViewById(R.id.products_progress);
        recyclerView = findViewById(R.id.products_recycler);
        productAdminAdapter = new ProductAdminAdapter(this, new ArrayList<Product>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(productAdminAdapter);

    }

    private void setupViewModel(){
        productManagementViewModel = new ViewModelProvider(this).get(ProductManagementViewModel.class);
        productManagementViewModel.loadAllProducts();

        productManagementViewModel.loadAllProducts().observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                productAdminAdapter.setProductList(products);
                progressBar_product.setVisibility(View.GONE);
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

                AlertDialog.Builder builder = new AlertDialog.Builder(ProductsManagementActivity.this);
                //Setting message manually and performing action on button click
                builder.setMessage("Do you want really to delete this Product?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", (dialog, id) -> {
                            dialog.cancel();
                            progressBar_product.setVisibility(View.VISIBLE);
                            int position = viewHolder.getAdapterPosition();
                            List<Product> products = productAdminAdapter.getProducts();
                            Toast.makeText(ProductsManagementActivity.this, ""+position, Toast.LENGTH_SHORT).show();
                            productManagementViewModel.deleteProduct(products.get(position));
                        })
                        .setNegativeButton("No", (dialog, id) -> {
                            productAdminAdapter.notifyDataSetChanged();
                            dialog.cancel();
                        });
                AlertDialog alert = builder.create();
                alert.setTitle("Delete Product!!!");
                alert.show();
            }
        }).attachToRecyclerView(recyclerView);
    }


    public void addProduct(View view) {
        Intent i = new Intent(this, ProductActivity.class);
        startActivity(i);
    }
}