package com.example.ultramarket.ui.adminLayer;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.ultramarket.database.Entities.Order;
import com.google.android.gms.maps.model.Dash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashBoardViewModel extends AndroidViewModel {

    MutableLiveData<Map<String, Double>> ordersListMutableLiveData = new MutableLiveData<>();

    public DashBoardViewModel(@NonNull Application application) {
        super(application);

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(Order.class.getSimpleName());
        Map<String, Double> stats = new HashMap<>();
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                stats.clear();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                double cost = 0;
                double prevCost = 0;
                String currentDate = "";
                for (DataSnapshot snap : snapshot.getChildren()) {
                    for (DataSnapshot s: snap.getChildren()){
                        Order order = s.getValue(Order.class);
                        currentDate = sdf.format(new Date(order.getOrder_date()));
                        cost = order.getPrice();
                        prevCost = 0;
                        if(stats.get(currentDate) != null){
                            prevCost = stats.get(currentDate);
                        }
                        stats.put(currentDate,  prevCost + cost);
                    }
                }
                ordersListMutableLiveData.setValue(stats);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(application.getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public MutableLiveData<Map<String, Double>> loadAllOrders(){
        return ordersListMutableLiveData;
    }

}

