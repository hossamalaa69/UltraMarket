package com.example.ultramarket.ui.userUi.Fragments;

import android.graphics.Rect;
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
import androidx.recyclerview.widget.RecyclerView;

import com.example.ultramarket.R;
import com.example.ultramarket.database.Entities.Category;
import com.example.ultramarket.ui.userUi.Adapters.CategoriesAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserCatFrag extends Fragment {

    public static final CharSequence TITLE = "Categories";
    private CategoriesAdapter categoriesAdapter;
    private RecyclerView.LayoutManager catLayoutManager;
    @BindView(R.id.usr_rv_featured_cat_frag)
    RecyclerView rvCategories;
    private UserCatViewModel mViewModel;

    public static UserCatFrag newInstance() {
        return new UserCatFrag();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_cat_fragment, container, false);
        ButterKnife.bind(this, view);
        categoriesAdapter = new CategoriesAdapter(getContext(), null);
        catLayoutManager = new GridLayoutManager(getContext(), 2);
        rvCategories.setLayoutManager(catLayoutManager);
        rvCategories.setAdapter(categoriesAdapter);
        rvCategories.setHasFixedSize(true);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(UserCatViewModel.class);
        setUpObservers();
    }

    private void setUpObservers() {
        mViewModel.loadAllCategories().observe(getViewLifecycleOwner(), categories -> categoriesAdapter.setCategoryList(categories));

    }

}