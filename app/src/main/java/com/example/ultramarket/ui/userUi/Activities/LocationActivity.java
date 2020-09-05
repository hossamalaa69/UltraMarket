package com.example.ultramarket.ui.userUi.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.ultramarket.R;
import com.example.ultramarket.firebase.FirebaseAuthHelper;
import com.example.ultramarket.helpers.Utils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LocationActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int PERMISSION_REQUEST_CODE = 123;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private boolean locPermission = false;
    private static final String TAG = "LocationActivity";

    @OnClick(R.id.user_location_use_curr_loc_btn)
    public void onGetLocationClicked(View view) {
        if (FirebaseAuthHelper.getsInstance().getCurrUser() != null) {
            {

                if (locPermission) {
                    mGetLocationBtn.setEnabled(false);
                    getDeviceLocation();
                } else {
                    checkForPermissions();
                }
            }
        } else {
            Utils.createToast(this, R.string.you_must_signin_first, Toast.LENGTH_SHORT);
        }
    }

    @OnClick(R.id.user_location_choose_manually)
    public void onSearchLocationClicked(View view) {
        if (locPermission) {
            startActivity(new Intent(this, MapActivity.class));
            finish();
        } else {
            checkForPermissions();
        }
    }

    @BindView(R.id.user_location_progress_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.user_location_use_curr_loc_btn)
    Button mGetLocationBtn;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                
                if (grantResults.length > 0) {
                        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
                            locPermission = false;
                            return;
                        }
                    // initialize map
                    locPermission = true;
                }
            }
        }
    }

    private void checkForPermissions() {
        String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this, FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locPermission = true;
        } else {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
        }
    }

    private void getDeviceLocation() {
        //TODO init fused location
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            //check if permission is granted
            if (locPermission) {
                // focus on type of Task here is gms.Task
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful())// got the location
                        {
                            Location currLoc = (Location) task.getResult();
                            getLocationDetails(currLoc.getLatitude(), currLoc.getLongitude());
                            updateLocationOfUser(FirebaseAuthHelper.getsInstance().getCurrUser().getUid());
                        } else {
                            Toast.makeText(LocationActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.d(TAG, "getDeviceLocation: SecurityException" + e.getMessage());

        }
    }

    private void getLocationDetails(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude,
                    longitude, 1);
            mCity = addresses.get(0).getLocality();
            mRoad = addresses.get(0).getFeatureName();
            mCountry = addresses.get(0).getCountryName();
        } catch (IOException e) {
            showAlertDialog(getString(R.string.location), getString(R.string.failed_location_details_go_to_profile));
        }
    }

    private void showAlertDialog(String title, String message) {
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.go_to_profile), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(LocationActivity.this, UserProfile.class));
            }
        });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog.show();
    }

    private void updateLocationOfUser(String uid) {
        com.example.ultramarket.database.Entities.Location location = new com.example.ultramarket.database.Entities.Location(
                mCountry,
                mCity,
                mRoad,
                String.valueOf(mLatitude),
                String.valueOf(mLongitude)
        );
        FirebaseAuthHelper.getsInstance().updateLocation(location, uid,
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Utils.createToast(LocationActivity.this, R.string.location_saved, Toast.LENGTH_SHORT);
                        finish();
                    }
                });
    }

    private double mLatitude;
    private double mLongitude;
    private String mCountry;
    private String mCity;
    private String mRoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_location);
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.location);
        }
        mProgressBar.setVisibility(View.GONE);
        mGetLocationBtn.setEnabled(true);
        checkForPermissions();

    }

}