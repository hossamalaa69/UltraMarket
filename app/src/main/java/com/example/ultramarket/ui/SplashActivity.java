package com.example.ultramarket.ui;

import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ultramarket.NetworkConnection.NetworkReceiver;
import com.example.ultramarket.R;
import com.example.ultramarket.ui.userUi.Activities.HomeActivity;

import gr.net.maroulis.library.EasySplashScreen;

public class SplashActivity extends AppCompatActivity implements NetworkReceiver.ConnectionReceiver {
    BroadcastReceiver br;
    private AlertDialog mAlertDialog;
    public static boolean isBackPressed = false;

    @Override
    public void onConnectionReceived(boolean isConnected) {
        //TODO add listener for connectivity changes
        int  toastMsg = R.string.back_online;
        if(mAlertDialog!=null && isConnected){
            mAlertDialog.dismiss();
            mAlertDialog = null;
            startActivity(new Intent(SplashActivity.this,HomeActivity.class));
        }
        else if (!isConnected){
            toastMsg = R.string.you_are_offline;
        }
        Toast.makeText(SplashActivity.this, toastMsg,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkInternetConnection();
    }

    private void checkInternetConnection() {
        if (NetworkReceiver.isInternetConnected(this)) {
            startActivity(new Intent(this, HomeActivity.class));
        } else {
            showAlertDialog(getString(R.string.network_connection), getString(R.string.you_dont_have_connection));
        }
    }

    private void showAlertDialog(String title, String message) {
        mAlertDialog = new AlertDialog.Builder(this).create();
        mAlertDialog.setTitle(title);
        mAlertDialog.setCanceledOnTouchOutside(false);
        mAlertDialog.setMessage(message);
        mAlertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.retry), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                checkInternetConnection();
                dialogInterface.dismiss();
            }
        });
        mAlertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        mAlertDialog.show();
    }

    private void registerBroadcast() {
        br = new NetworkReceiver(this);
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        this.registerReceiver(br, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isBackPressed){
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //this.unregisterReceiver(br);
    }
}