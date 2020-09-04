package com.example.ultramarket.ui.adminLayer;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.ultramarket.database.Entities.Category;
import com.example.ultramarket.database.Entities.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrdersViewModel extends AndroidViewModel {

    MutableLiveData<List<Order>> ordersListMutableLiveData = new MutableLiveData<>();

    public OrdersViewModel(@NonNull Application application) {
        super(application);

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(Order.class.getSimpleName());
        List<Order> ordersList = new ArrayList<>();
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ordersList.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    for (DataSnapshot s: snap.getChildren()){
                        Order order = s.getValue(Order.class);
                        order.setCustomerId(snap.getKey());
                        ordersList.add(order);
                    }
                }
                ordersListMutableLiveData.setValue(ordersList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(application.getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public MutableLiveData<List<Order>> loadAllOrders(){
        return ordersListMutableLiveData;
    }

}
