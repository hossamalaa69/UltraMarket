package com.example.ultramarket.framgnets.user_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ultramarket.R;
import com.example.ultramarket.database.Entities.Brand;
import com.example.ultramarket.database.Entities.Category;
import com.example.ultramarket.database.Entities.Product;
import com.example.ultramarket.adapters.user_adapters.BrandAdapter;
import com.example.ultramarket.adapters.user_adapters.CategoriesAdapter;
import com.example.ultramarket.adapters.user_adapters.ProductAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserHomeFrag extends Fragment {
    public static final CharSequence TITLE = "Home";
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        ButterKnife.bind(this, view);
        catAdapter = new CategoriesAdapter(getContext(), null);
        rvCategories.setAdapter(catAdapter);
        catLayoutManager = new GridLayoutManager(getContext(), 3);
        rvCategories.setLayoutManager(catLayoutManager);
        // product views
        prodAdapter = new ProductAdapter(getContext(), null);
        rvLatestProd.setAdapter(prodAdapter);
        prodLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL,false);
        rvLatestProd.setLayoutManager(prodLayoutManager);
        rvLatestProd.setHasFixedSize(true);
        //  featured product views
        featuredProdAdapter = new ProductAdapter(getContext(), null);
        rvFeaturedProd.setAdapter(featuredProdAdapter);
        featuredProdLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL,false);
        rvFeaturedProd.setLayoutManager(featuredProdLayoutManager);
        rvFeaturedProd.setHasFixedSize(true);
        //brand views
        brandAdapter = new BrandAdapter(getContext(), null);
        rvBrands.setAdapter(brandAdapter);
        brandLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL,false);
        rvBrands.setLayoutManager(brandLayoutManager);
        rvBrands.setHasFixedSize(true);


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setObservers();

   /*    for (int i = 1; i < 10; i++)
            mViewModel.insertBrand(new Brand(i, "brand", null));
       for (int i = 1; i < 10; i++)
            mViewModel.insertCategory(new Category(i, "food", null));
        for (int i = 20; i < 30; i++)
            mViewModel.insertProd(new Product(i, "featured prod"+ i, null,"kg",123,14,"hahah",true,12,i-19,i-19,new Date()));
*/
    }
    private void setObservers(){
        mViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
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


}