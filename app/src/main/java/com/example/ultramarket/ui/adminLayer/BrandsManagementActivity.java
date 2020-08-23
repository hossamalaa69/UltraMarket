package com.example.ultramarket.ui.adminLayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.ultramarket.R;

public class BrandsManagementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brands_management);
    }

    public void addBrand(View view) {
        Intent i = new Intent(this, BrandActivity.class);
        startActivity(i);
    }
}