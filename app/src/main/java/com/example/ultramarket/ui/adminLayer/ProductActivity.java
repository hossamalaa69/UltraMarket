package com.example.ultramarket.ui.adminLayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.ultramarket.R;
import com.example.ultramarket.adapters.user_adapters.BrandProdAdapter;
import com.example.ultramarket.adapters.user_adapters.CategoryProdAdapter;
import com.example.ultramarket.database.Entities.Brand;
import com.example.ultramarket.database.Entities.Category;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ProductActivity extends AppCompatActivity  {

    private Spinner spinner_currency;
    private Spinner spinner_unit;
    private RecyclerView brand_recycler;
    private BrandProdAdapter brandProdAdapter;
    private RecyclerView category_recycler;
    private CategoryProdAdapter categoryProdAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        setupRecyclers();
        setupSpinners();
    }

    private void setupRecyclers() {
        brand_recycler = (RecyclerView) findViewById(R.id.brand_prod_recycler);
        brand_recycler.setLayoutManager(new LinearLayoutManager(this));
        brand_recycler.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        ArrayList<Brand> brandList = new ArrayList<>();
        brandList.add(new Brand("dsf", "Brand1", "fdgffdgdf"));
        brandList.add(new Brand("dsf", "Brand2", "fdgffdgdf"));
        brandList.add(new Brand("dsf", "Brand3", "fdgffdgdf"));
        brandList.add(new Brand("dsf", "Brand4", "fdgffdgdf"));
        brandList.add(new Brand("dsf", "Brand5", "fdgffdgdf"));
        brandList.add(new Brand("dsf", "Brand6", "fdgffdgdf"));
        brandList.add(new Brand("dsf", "Brand7", "fdgffdgdf"));
        brandList.add(new Brand("dsf", "Brand8", "fdgffdgdf"));
        brandProdAdapter = new BrandProdAdapter(this, brandList);
        brand_recycler.setAdapter(brandProdAdapter);

        category_recycler = (RecyclerView) findViewById(R.id.category_prod_recycler);
        category_recycler.setLayoutManager(new LinearLayoutManager(this));
        brand_recycler.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        ArrayList<Category> categoryList = new ArrayList<>();
        categoryList.add(new Category("dsf", "Category1", "fdgffdgdf"));
        categoryList.add(new Category("dsf", "Category2", "fdgffdgdf"));
        categoryList.add(new Category("dsf", "Category3", "fdgffdgdf"));
        categoryList.add(new Category("dsf", "Category4", "fdgffdgdf"));
        categoryList.add(new Category("dsf", "Category5", "fdgffdgdf"));
        categoryList.add(new Category("dsf", "Category6", "fdgffdgdf"));

        categoryProdAdapter = new CategoryProdAdapter(this, categoryList);
        category_recycler.setAdapter(categoryProdAdapter);
    }

    private void setupSpinners() {
        spinner_currency = (Spinner) findViewById(R.id.currency_spinner);
        spinner_unit = (Spinner) findViewById(R.id.unit_spinner);
        //spinner.setOnItemSelectedListener(this);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.currency_array, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.units_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner_currency.setAdapter(adapter1);
        spinner_unit.setAdapter(adapter2);
    }

    public void uploadFromGallery(View view) {
    }

    public void saveProduct(View view) {
        Toast.makeText(this, "" + spinner_currency.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "" + spinner_unit.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();

        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String timestamp = simpleDateFormat.format(now);
        Toast.makeText(this, timestamp, Toast.LENGTH_LONG).show();

    }

}