package com.example.ultramarket.ui.adminLayer;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.ultramarket.database.Entities.Order;
import com.example.ultramarket.database.Entities.Product;
import com.google.android.gms.maps.model.Dash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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
    MutableLiveData<Map<String, String>> most_selling_prod = new MutableLiveData<>();


    public DashBoardViewModel(@NonNull Application application) {
        super(application);

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(Order.class.getSimpleName());
        Map<String, Double> stats = new HashMap<>();
        Map<String, Integer> products = new HashMap<>();
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                stats.clear();
                products.clear();
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
                        for (Map.Entry<String,Integer> entry : order.getProducts().entrySet()) {
                            int oldQuantity = 0;
                            if(products.get(entry.getKey()) != null)
                                oldQuantity = products.get(entry.getKey());
                            products.put(entry.getKey(), entry.getValue() + oldQuantity);
                        }
                    }
                }
                ordersListMutableLiveData.setValue(stats);
                int max_quantity = -1;
                String max_ID = "";
                for (Map.Entry<String,Integer> entry : products.entrySet()) {
                    if(entry.getValue()>max_quantity) {
                        max_ID = entry.getKey();
                        max_quantity = entry.getValue();
                    }
                }
                DatabaseReference prodDbRef = FirebaseDatabase.getInstance().getReference().child(Product.class.getSimpleName());
                Query query = prodDbRef.orderByKey().equalTo(max_ID);
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String prod_name = "";
                        String prod_img = "";
                        Map<String,String> best_prod_name_img= new HashMap<>();
                        best_prod_name_img.clear();
                        for(DataSnapshot snap:snapshot.getChildren()){
                            prod_name = snap.getValue(Product.class).getName();
                            prod_img = snap.getValue(Product.class).getImage();
                            best_prod_name_img.put(prod_name, prod_img);
                        }
                        most_selling_prod.setValue(best_prod_name_img);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
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

    public MutableLiveData<Map<String, String>> loadBestProduct(){
        return most_selling_prod;
    }
}

