package com.example.ultramarket.ui.adminLayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ultramarket.R;
import com.example.ultramarket.adapters.CustomersAdapter;
import com.example.ultramarket.adapters.OrderAdminAdapter;
import com.example.ultramarket.database.Entities.Order;
import com.example.ultramarket.database.Entities.User;

import java.util.ArrayList;
import java.util.List;

public class OrdersActivity extends AppCompatActivity {

    private OrdersViewModel ordersViewModel;

    private RecyclerView recyclerView;

    private OrderAdminAdapter orderAdminAdapter;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        setupRecyclerView();

        setupViewModel();
    }

    private void setupRecyclerView() {
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_orders);
        recyclerView = (RecyclerView) findViewById(R.id.orders_recycler);
        orderAdminAdapter = new OrderAdminAdapter(this, new ArrayList<Order>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(orderAdminAdapter);
    }

    private void setupViewModel() {
        ordersViewModel  = new ViewModelProvider(this).get(OrdersViewModel.class);
        ordersViewModel.loadAllOrders();
        ordersViewModel.loadAllOrders().observe(this, (List<Order> orders) -> {
            orderAdminAdapter.setOrderList(orders);
            progressBar.setVisibility(View.GONE);
        });
    }
}