package com.example.ultramarket.ui.adminLayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.example.ultramarket.R;
import com.example.ultramarket.adapters.CustomersAdapter;
import com.example.ultramarket.database.Entities.User;

import java.util.ArrayList;
import java.util.List;

public class CustomersActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    CustomersAdapter customersAdapter;

    List<User> userList;

    CustomersViewModel customersViewModel;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers);

        setupRecycler();

        setupLiveData();

    }

    private void setupRecycler(){
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_customers);
        userList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        customersAdapter = new CustomersAdapter(this, userList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(customersAdapter);
    }

    private void setupLiveData() {
        customersViewModel  = new ViewModelProvider(this).get(CustomersViewModel.class);
        customersViewModel.loadAllCustomers();
        customersViewModel.loadAllCustomers().observe(this, (List<User> users) -> {
            customersAdapter.setUserList(users);
            progressBar.setVisibility(View.GONE);
        });
    }

    private void insertDummyData(){
        userList = new ArrayList<>();

        userList.add(new User("user1@gmail.com", "Dexter Morgan", "01162358155", 5, 12, R.drawable.user1));
        userList.add(new User("user2@gmail.com", "Kim Wicksler", "01065658262", 4, 7, R.drawable.user2));
        userList.add(new User("user3@gmail.com", "Michael Scott", "01265656126", 3, 9, R.drawable.user3));
        userList.add(new User("user4@gmail.com", "Pam Hamlet", "01235878465", 5, 5, R.drawable.user4));
        userList.add(new User("user5@gmail.com", "Frank Kastle", "01132561832", 4, 17, R.drawable.user5));
        userList.add(new User("user6@gmail.com", "Jack Shephard", "01513158463", 4, 20, R.drawable.user6));
        userList.add(new User("user7@gmail.com", "Debra Morgan", "01123264323", 3, 2, R.drawable.user7));
        userList.add(new User("user8@gmail.com", "John Luke", "01146513563", 5, 3, R.drawable.user8));

    }

}