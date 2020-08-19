package com.example.ultramarket.NetworkConnection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.example.ultramarket.helpers.Utils;

public class NetworkReceiver extends BroadcastReceiver {
    private ConnectionReceiver mCR;
    public interface ConnectionReceiver{
        void onConnectionReceived();
    }

    public NetworkReceiver(Context mContext) {
        mCR =(ConnectionReceiver) mContext;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager conn = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conn.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            Utils.LOCAL = false;
            Toast.makeText(context, "wifi", Toast.LENGTH_SHORT).show();
        } else {
            Utils.LOCAL = true;
            Toast.makeText(context, "local", Toast.LENGTH_SHORT).show();
        }
        mCR.onConnectionReceived();
    }
}