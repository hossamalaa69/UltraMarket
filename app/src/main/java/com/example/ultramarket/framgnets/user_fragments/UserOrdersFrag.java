package com.example.ultramarket.framgnets.user_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ultramarket.R;
import com.example.ultramarket.adapters.user_adapters.OrderAdapter;
import com.example.ultramarket.database.Entities.Order;
import com.example.ultramarket.firebase.FirebaseAuthHelper;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserOrdersFrag extends Fragment {
    public static final CharSequence TITLE = "Orders";
/*

    private UserOrdersViewModel mViewModel;
*/

    public static UserOrdersFrag newInstance() {
        return new UserOrdersFrag();
    }

    @BindView(R.id.user_orders_frag_rv)
    RecyclerView recyclerView;
    OrderAdapter adapter;
    @BindView(R.id.user_orders_frag_track_layout)
    View trackLayout;

    @OnClick(R.id.user_orders_track_order_btn)
    public void onTrackOrdersClicked() {
        if (FirebaseAuthHelper.getsInstance().getCurrUser() != null) {
            showOrders();
            loadOrders();
        } else {
            Toast.makeText(getContext(), R.string.you_must_signin_first, Toast.LENGTH_SHORT).show();
        }
    }

    private void loadOrders() {
        FirebaseDatabase.getInstance().getReference()
                .child(Order.class.getSimpleName()).child(FirebaseAuthHelper.getsInstance().getCurrUser().getUid())
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        adapter.insertOrder(snapshot.getValue(Order.class));
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                        adapter.removeOrder(snapshot.getValue(Order.class));
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void showOrders() {
        trackLayout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void hideOrders() {
        trackLayout.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_orders_fragment, container, false);
        ButterKnife.bind(this, view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new OrderAdapter(getContext(), null);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        hideOrders();
        // TODO: Use the ViewModel
    }

}