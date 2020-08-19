package com.example.ultramarket.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;

import com.google.firebase.auth.FirebaseUser;

public class Utils {
    public static boolean LOCAL = false ;
    public static FirebaseUser user = null;
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connMgr =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isWifiConn = false;
        boolean isMobileConn = false;
        for (Network network : connMgr.getAllNetworks()) {
            NetworkInfo networkInfo = connMgr.getNetworkInfo(network);
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                isWifiConn |= networkInfo.isConnected();
            }
            if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                isMobileConn |= networkInfo.isConnected();
            }
        }
        return isMobileConn|isWifiConn;
    }
}
