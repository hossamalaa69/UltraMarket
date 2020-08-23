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
        void onConnectionReceived(boolean connected);
    }

    public NetworkReceiver(Context mContext) {
        mCR =(ConnectionReceiver) mContext;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //TODO add listener for connection changes
      /*  ConnectivityManager conn = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conn.getActiveNetworkInfo();
        mCR.onConnectionReceived((networkInfo != null) && networkInfo.isConnected()); */
    }
    public static boolean isInternetConnected(Context context){
        ConnectivityManager conn = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conn.getActiveNetworkInfo();
        return (networkInfo != null) && networkInfo.isConnected();
    }
}