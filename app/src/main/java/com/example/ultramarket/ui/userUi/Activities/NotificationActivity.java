package com.example.ultramarket.ui.userUi.Activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ultramarket.R;
import com.example.ultramarket.adapters.user_adapters.NotificationAdapter;
import com.example.ultramarket.database.Entities.Notification;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationActivity extends AppCompatActivity implements NotificationAdapter.NotificationCallBacks {
    @BindView(R.id.user_notification_rv)
    RecyclerView recyclerView;
    NotificationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_notification);
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.notification);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        adapter = new NotificationAdapter(this, null);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        loadNotifications();

    }

    private void loadNotifications() {

        FirebaseDatabase.getInstance().getReference()
                .child(Notification.class.getSimpleName()).orderByChild("date").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Notification> notificationList = new ArrayList<>();
                        for (DataSnapshot shot : snapshot.getChildren()) {
                            notificationList.add(shot.getValue(Notification.class));
                        }
                        Collections.reverse(notificationList);
                        adapter.setNotificationList(notificationList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                }
        );
    }

    @Override
    public void onNotificationClickedListener(Intent intent, View sharedView) {
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(
                this,
                sharedView, "image").toBundle());
    }
}