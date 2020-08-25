package com.example.ultramarket.ui.adminLayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.ultramarket.R;

public class ProductsManagementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_management);
    }

    public void addProduct(View view) {
        Intent i = new Intent(this, ProductActivity.class);
        startActivity(i);
    }
}