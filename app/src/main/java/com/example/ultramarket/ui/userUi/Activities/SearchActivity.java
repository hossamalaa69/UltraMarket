package com.example.ultramarket.ui.userUi.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ultramarket.R;
import com.example.ultramarket.adapters.user_adapters.OfferAdapter;
import com.example.ultramarket.database.Entities.Product;
import com.example.ultramarket.helpers.AppExecutors;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity implements OfferAdapter.ProductCallBacks {

    @BindView(R.id.user_search_rv)
    RecyclerView recyclerView;
    @BindView(R.id.user_search_toolbar)
    Toolbar toolbar;
    @BindView(R.id.user_search_empty_layout)
    View emptyLayout;
    @BindView(R.id.user_search_sv)
    androidx.appcompat.widget.SearchView searchView;
    private OfferAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_search);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OfferAdapter(this, null, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.onActionViewExpanded();
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.matches("")) {
                    showEmptyLayout();
                    return false;
                } else {
                    hideEmptyLayout();
                }
                adapter.clear();
                AppExecutors.getInstance().networkIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        FirebaseDatabase.getInstance().getReference()
                                .child(Product.class.getSimpleName()).orderByChild("name")
                                .startAt(newText.substring(0, 1).toUpperCase() + newText.substring(1))
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        for (DataSnapshot shot : snapshot.getChildren())
                                            adapter.insertProduct(shot.getValue(Product.class));
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }
                });
                return false;
            }
        });
    }

    private void showEmptyLayout() {
        emptyLayout.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    private void hideEmptyLayout() {
        emptyLayout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onProductClickedListener(Intent intent, View shared1, View shared2) {

    }
}