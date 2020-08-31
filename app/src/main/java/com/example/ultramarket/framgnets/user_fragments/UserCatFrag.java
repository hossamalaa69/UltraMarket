package com.example.ultramarket.framgnets.user_fragments;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ultramarket.R;
import com.example.ultramarket.adapters.user_adapters.CategoryAdapterCatFrag;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserCatFrag extends Fragment implements CategoryAdapterCatFrag.CategoryCallBacks {

    public static final CharSequence TITLE = "Categories";
    private CategoryAdapterCatFrag categoriesAdapter;
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
        categoriesAdapter = new CategoryAdapterCatFrag(getContext(), null,this);
        catLayoutManager = new GridLayoutManager(getContext(), 2);
        rvCategories.setLayoutManager(catLayoutManager);
        rvCategories.setAdapter(categoriesAdapter);
        rvCategories.setHasFixedSize(true);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(UserCatViewModel.class);
        setUpObservers();
    }

    private void setUpObservers() {
        mViewModel.loadAllCategories().observe(getViewLifecycleOwner(), categories -> categoriesAdapter.setCategoryList(categories));

    }
    @Override
    public void onCategoryClickedListener(Intent intent, View sharedView) {
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(
                getActivity(),
                sharedView,
                "name").toBundle());
    }


}