package com.example.ultramarket.ui;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ultramarket.NetworkConnection.NetworkReceiver;
import com.example.ultramarket.R;
import com.example.ultramarket.ui.userUi.Activities.HomeActivity;

public class MainActivity extends AppCompatActivity implements NetworkReceiver.ConnectionReceiver {
    BroadcastReceiver br;

    @Override
    public void onConnectionReceived() {
        startActivity(new Intent(this, HomeActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerBroadcast();
     }

    private void registerBroadcast() {
        br = new NetworkReceiver(this);
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        this.registerReceiver(br, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(br);
    }
}