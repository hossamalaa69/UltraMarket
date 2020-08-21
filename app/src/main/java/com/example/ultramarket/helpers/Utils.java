package com.example.ultramarket.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

import androidx.lifecycle.MutableLiveData;

import com.example.ultramarket.database.Entities.User;

public class Utils {
    public static boolean LOCAL = false;
    public static  User user = null;

    public static String[] availableCountries = new String[]{"Available Countries","Egypt","Palestine","Libya"};
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
        return isMobileConn | isWifiConn;
    }
}
