package com.example.ultramarket.ui.adminLayer;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.ultramarket.R;
import com.example.ultramarket.database.Entities.Order;
import com.example.ultramarket.database.Entities.Product;
import com.example.ultramarket.database.Entities.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CustomersViewModel extends AndroidViewModel {

    private MutableLiveData<List<User>> listMutableLiveData = new MutableLiveData<>();

    public CustomersViewModel(@NonNull Application application) {
        super(application);

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(User.class.getSimpleName());
        List<User> userList = new ArrayList<>();
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    User user = snap.getValue(User.class);
                    if(user.getEmail().equals(application.getString(R.string.admin_email)))
                        continue;
                    if(user.getPhone()==null || user.getPhone().isEmpty()){
                        user.setPhone("Unknown");
                    }
                    userList.add(user);
                }

                DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference().child(Order.class.getSimpleName());
                ordersRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot2) {
                        for(DataSnapshot s: snapshot2.getChildren()){
                            for(int i=0;i<userList.size();i++){
                                if(userList.get(i).getID().equals(s.getKey())){
                                    userList.get(i).setNumOrders((int)s.getChildrenCount());
                                    break;
                                }
                            }
                        }
                        listMutableLiveData.setValue(userList);
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

    public MutableLiveData<List<User>> loadAllCustomers(){
        return listMutableLiveData;
    }
}
