package com.example.ultramarket.ui.adminLayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.ultramarket.R;

public class WarehouseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehouse);
    }

    public void openBrands(View view) {
        Intent i = new Intent(this, BrandsManagementActivity.class);
        startActivity(i);
    }

    public void openCategories(View view) {
        Intent i = new Intent(this, CategoriesManagementActivity.class);
        startActivity(i);
    }

    public void openProducts(View view) {
        Intent i = new Intent(this, ProductsManagementActivity.class);
        startActivity(i);
    }
}