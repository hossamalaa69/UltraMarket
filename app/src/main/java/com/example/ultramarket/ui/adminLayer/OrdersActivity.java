package com.example.ultramarket.ui.adminLayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.ultramarket.R;
import com.example.ultramarket.database.Entities.Order;
import com.example.ultramarket.database.Entities.User;

import java.util.List;

public class OrdersActivity extends AppCompatActivity {

    private OrdersViewModel ordersViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        ordersViewModel  = new ViewModelProvider(this).get(OrdersViewModel.class);
        ordersViewModel.loadAllOrders();
        ordersViewModel.loadAllOrders().observe(this, (List<Order> orders) -> {
            Toast.makeText(OrdersActivity.this, "Got Orders", Toast.LENGTH_SHORT).show();
        });
    }
}