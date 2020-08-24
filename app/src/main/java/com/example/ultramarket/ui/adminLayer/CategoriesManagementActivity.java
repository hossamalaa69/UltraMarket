package com.example.ultramarket.ui.adminLayer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ultramarket.R;
import com.example.ultramarket.adapters.CategoriesAdminAdapter;
import com.example.ultramarket.database.Entities.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoriesManagementActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    CategoriesAdminAdapter categoriesAdminAdapter;
    ProgressBar progressBar_categories;

    CategoriesManagementViewModel categoriesManagementViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories_management);

        initRecycler();

        setupViewModel();

        setupSwipe();
    }


    private void initRecycler(){
        progressBar_categories = (ProgressBar) findViewById(R.id.categories_progress);
        recyclerView = findViewById(R.id.categories_recycler);
        categoriesAdminAdapter = new CategoriesAdminAdapter(this, new ArrayList<Category>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(categoriesAdminAdapter);

        //holds listener for clicking the notification item
        categoriesAdminAdapter.setOnItemClickListener(new CategoriesAdminAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //gets index of item pressed, then send it to its target
                sendToUpdate(position);
            }
        });
    }

    private void sendToUpdate(int position) {
        //Toast.makeText(this, ""+position, Toast.LENGTH_SHORT).show();
        Category category = categoriesAdminAdapter.getCategories().get(position);
        Intent i = new Intent(this, CategoryActivity.class);
        i.putExtra("ID", category.getID());
        i.putExtra("imageUrl", category.getImage());
        i.putExtra("category_name", category.getName());
        startActivity(i);
    }

    private void setupViewModel(){
        categoriesManagementViewModel = new ViewModelProvider(this).get(CategoriesManagementViewModel.class);
        categoriesManagementViewModel.loadAllCategories();

        categoriesManagementViewModel.loadAllCategories().observe(this, (Observer<List<Category>>) categories -> {
            categoriesAdminAdapter.setCategoryList(categories);
            progressBar_categories.setVisibility(View.GONE);
        });
    }

    private void setupSwipe(){

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {

                AlertDialog.Builder builder = new AlertDialog.Builder(CategoriesManagementActivity.this);
                //Setting message manually and performing action on button click
                builder.setMessage("Do you want to delete this category ? \nAll products related to this category will be deleted!")
                        .setCancelable(false)
                        .setPositiveButton("Yes", (dialog, id) -> {
                            dialog.cancel();
                            progressBar_categories.setVisibility(View.VISIBLE);
                            int position = viewHolder.getAdapterPosition();
                            List<Category> categories = categoriesAdminAdapter.getCategories();
                            Toast.makeText(CategoriesManagementActivity.this, ""+position, Toast.LENGTH_SHORT).show();
                            categoriesManagementViewModel.deleteCategory(categories.get(position));
                        })
                        .setNegativeButton("No", (dialog, id) -> {
                            categoriesAdminAdapter.notifyDataSetChanged();
                            dialog.cancel();
                        });
                AlertDialog alert = builder.create();
                alert.setTitle("Delete Category!!!");
                alert.show();
            }
        }).attachToRecyclerView(recyclerView);
    }


    public void addCategory(View view) {
        Intent i = new Intent(this, CategoryActivity.class);
        startActivity(i);
    }
}