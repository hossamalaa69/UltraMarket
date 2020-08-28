package com.example.ultramarket.ui.userUi.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ultramarket.R;
import com.example.ultramarket.adapters.user_adapters.OfferAdapter;
import com.example.ultramarket.database.Entities.Brand;
import com.example.ultramarket.database.Entities.Category;
import com.example.ultramarket.database.Entities.Product;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoryBrandProductsActivity extends AppCompatActivity {

    @BindView(R.id.user_cats_brands_products_rv)
    RecyclerView recyclerView;
    @BindView(R.id.user_cats_brands_products_name)
    TextView mName;
    private OfferAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoery_brand_products);
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();
        Intent intent = getIntent();
        if (intent != null) {
            int type = intent.getIntExtra("type", 0);
            if (type != 0)//there is a type
            {
                String id = intent.getStringExtra("id");
                String name = intent.getStringExtra("name");
                mName.setText(name);
                loadProducts(type, id);
                if (actionBar != null) {
                    if (type == Category.TYPE_ID) {
                        actionBar.setTitle(R.string.shop_by_categories);
                    } else if (type == Brand.TYPE_ID) {
                        actionBar.setTitle(R.string.shop_by_brands);
                    }

                }
            }
        }
        adapter = new OfferAdapter(this, null);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
    }

    private void loadProducts(int type, String id) {
        CategoriesBrandsProductsViewModelFactory factory = new CategoriesBrandsProductsViewModelFactory(type, id);
        final CategoriesBrandsProductsViewModel viewModel = ViewModelProviders.of(this, factory)
                .get(CategoriesBrandsProductsViewModel.class);
        viewModel.getProducts().observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                adapter.setProdList(products);
            }
        });
    }

}