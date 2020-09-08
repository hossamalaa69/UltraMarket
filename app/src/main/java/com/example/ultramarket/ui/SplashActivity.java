package com.example.ultramarket.ui;

import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ultramarket.NetworkConnection.NetworkReceiver;
import com.example.ultramarket.R;
import com.example.ultramarket.database.Entities.User;
import com.example.ultramarket.firebase.FirebaseAuthHelper;
import com.example.ultramarket.helpers.Utils;
import com.example.ultramarket.ui.adminLayer.AdminHomeActivity;
import com.example.ultramarket.ui.userUi.Activities.HomeActivity;
import com.example.ultramarket.ui.userUi.Activities.SignUpActivity;

public class SplashActivity extends AppCompatActivity implements NetworkReceiver.ConnectionReceiver,
        FirebaseAuthHelper.FirebaseAuthCallBacks {
    BroadcastReceiver br;
    private AlertDialog mAlertDialog;
    private String adProductId = null;

    @Override
    public void onFailedToSignUp() {
        Utils.createToast(this, R.string.failed_to_sign_up, Toast.LENGTH_LONG);
        FirebaseAuthHelper.getsInstance().logOut(this,null);
        startActivity(new Intent(this, SignUpActivity.class));
        finish();
    }

    @Override
    public void onLoginStateChanges(User user) {
        FirebaseAuthHelper.getsInstance().isAdmin(user.getID(), this);
    }

    @Override
    public void onCheckAdminResult(boolean isAdmin) {
        if (isAdmin) {
            startActivity(new Intent(this, AdminHomeActivity.class));
        } else {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.putExtra("product_id", adProductId);
            startActivity(intent);
        }
        finish();
    }

    @Override
    public void onLoggedOutStateChanges() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    @Override
    public void onConnectionReceived(boolean isConnected) {
        //TODO add listener for connectivity changes
        int toastMsg = R.string.back_online;
        if (mAlertDialog != null && isConnected) {
            mAlertDialog.dismiss();
            mAlertDialog = null;
            attachAuthListener();
        } else if (!isConnected) {
            toastMsg = R.string.you_are_offline;
        }
        Utils.createToast(SplashActivity.this, toastMsg, Toast.LENGTH_SHORT);
    }

    private void attachAuthListener() {
        FirebaseAuthHelper.getsInstance().attachAuthStateListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkReceivedNotification();

        checkInternetConnection();
    }

    private void checkReceivedNotification() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            adProductId = bundle.getString("product_id");
            // Toast.makeText(this, product_id,Toast.LENGTH_SHORT).show();
        }
    }

    private void checkInternetConnection() {
        if (NetworkReceiver.isInternetConnected(this)) {
            attachAuthListener();
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

    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseAuthHelper.getsInstance().detachAuthStateListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //this.unregisterReceiver(br);
    }
}