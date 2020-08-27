package com.example.ultramarket.framgnets.user_fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ultramarket.R;
import com.example.ultramarket.adapters.user_adapters.BrandAdapter;
import com.example.ultramarket.adapters.user_adapters.CategoriesAdapter;
import com.example.ultramarket.adapters.user_adapters.ProductAdapter;
import com.example.ultramarket.database.Entities.Brand;
import com.example.ultramarket.database.Entities.Category;
import com.example.ultramarket.database.Entities.Product;
import com.example.ultramarket.ui.userUi.Activities.ProductActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserHomeFrag extends Fragment implements ProductAdapter.OnItemClicked {
    public static final CharSequence TITLE = "Home";
    public static boolean catRvExtended = false;
    @BindView(R.id.usr_rv_category)
    RecyclerView rvCategories;
    @BindView(R.id.usr_rv_latest)
    RecyclerView rvLatestProd;
    @BindView(R.id.usr_rv_brands)
    RecyclerView rvBrands;
    @BindView(R.id.usr_rv_featured)
    RecyclerView rvFeaturedProd;
    private RecyclerView.LayoutManager catLayoutManager;
    private CategoriesAdapter catAdapter;
    private HomeViewModel mViewModel;
    private RecyclerView.LayoutManager brandLayoutManager;
    private BrandAdapter brandAdapter;
    private RecyclerView.LayoutManager prodLayoutManager;
    private ProductAdapter prodAdapter;
    private RecyclerView.LayoutManager featuredProdLayoutManager;
    private ProductAdapter featuredProdAdapter;

    public static UserHomeFrag newInstance() {
        return new UserHomeFrag();
    }

    @BindView(R.id.user_home_show_more)
    Button showMoreBtn;

    @OnClick(R.id.user_home_show_more)
    void extendCatRv(View view) {
        if (catRvExtended) {
            ViewGroup.LayoutParams params = rvCategories.getLayoutParams();
            params.height = rvCategories.getMinimumHeight();
            showMoreBtn.setText(R.string.show_more);
            rvCategories.setLayoutParams(params);
        } else {
            ViewGroup.LayoutParams params = rvCategories.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            rvCategories.setLayoutParams(params);
            showMoreBtn.setText(R.string.show_less);
        }
        catRvExtended = !catRvExtended;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_home_fragment, container, false);
        ButterKnife.bind(this, view);
        catAdapter = new CategoriesAdapter(getContext(), null);
        rvCategories.setAdapter(catAdapter);
        catLayoutManager = new GridLayoutManager(getContext(), 3);
        rvCategories.setLayoutManager(catLayoutManager);
        rvCategories.setNestedScrollingEnabled(false);
        // product views
        prodAdapter = new ProductAdapter(getContext(), null,this);
        rvLatestProd.setAdapter(prodAdapter);
        prodLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        rvLatestProd.setLayoutManager(prodLayoutManager);
        rvLatestProd.setHasFixedSize(true);
        //  featured product views
        featuredProdAdapter = new ProductAdapter(getContext(), null,this);
        rvFeaturedProd.setAdapter(featuredProdAdapter);
        featuredProdLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        rvFeaturedProd.setLayoutManager(featuredProdLayoutManager);
        rvFeaturedProd.setHasFixedSize(true);
        //brand views
        brandAdapter = new BrandAdapter(getContext(), null);
        rvBrands.setAdapter(brandAdapter);
        brandLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        rvBrands.setLayoutManager(brandLayoutManager);
        rvBrands.setHasFixedSize(true);


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setObservers();

    /*  for (int i = 1; i < 10; i++)
            mViewModel.insertBrand(new Brand(i, "brand", null));
         for (int i = 1; i < 10; i++)
            mViewModel.insertCategory(new Category(i, "food", null));
         for (int i = 1; i < 5; i++)
            mViewModel.insertProd(new Product(i, "featured prod" + i, null, "1 kg", 123, 14, "hahah", true, 0.5, i, i , new Date()));
       for (int i = 5; i < 10; i++)
            mViewModel.insertProd(new Product(i, "product" + i, null, "500 g", 123, 14, "hahah", false, 0, i, i, new Date()));
*/
    }

    private void setObservers() {
        mViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        mViewModel.getmCatList().observe(getViewLifecycleOwner(), new Observer<List<Category>>() {
            @Override
            public void onChanged(List<Category> categories) {
                catAdapter.setCategoryList(categories);
            }
        });
        mViewModel.getmBrandList().observe(getViewLifecycleOwner(), new Observer<List<Brand>>() {
            @Override
            public void onChanged(List<Brand> brands) {
                brandAdapter.setBrandList(brands);
            }
        });
        mViewModel.getmLatestProdList().observe(getViewLifecycleOwner(), new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                prodAdapter.setProdList(products);
            }
        });
        mViewModel.getmLatestProdList().observe(getViewLifecycleOwner(), new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                prodAdapter.setProdList(products);
            }
        });
        mViewModel.getmFeaturedProdList().observe(getViewLifecycleOwner(), new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                featuredProdAdapter.setProdList(products);
            }
        });
    }

    @Override
    public void onItemClicked(String prodId) {
        Intent intent = new Intent(getContext(), ProductActivity.class);
        intent.putExtra("prod_id", prodId);
        startActivity(intent);
    }
}