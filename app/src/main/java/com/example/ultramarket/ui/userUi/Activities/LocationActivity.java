package com.example.ultramarket.ui.userUi.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.ultramarket.R;
import com.example.ultramarket.firebase.FirebaseAuthHelper;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LocationActivity extends AppCompatActivity implements LocationListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int REQUEST_CODE = 1;
    private LocationManager locationManager;

    @OnClick(R.id.user_location_use_curr_loc_btn)
    public void onGetLocationClicked(View view) {
        if (FirebaseAuthHelper.getsInstance().getCurrUser() != null)
            checkForPermissions();
        else {
            Toast.makeText(this, R.string.you_must_signin_first, Toast.LENGTH_SHORT).show();
        }
    }

    @BindView(R.id.user_location_progress_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.user_location_use_curr_loc_btn)
    Button mGetLocationBtn;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, R.string.permission_confirmed, Toast.LENGTH_SHORT).show();
                checkForPermissions();
            } else {
                Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void checkForPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE);
        } else {
            mGetLocationBtn.setEnabled(false);
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

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

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if (FirebaseAuthHelper.getsInstance().getCurrUser() != null) {
            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();
            getLocationDetails(location.getLatitude(), location.getLongitude());
            updateLocationOfUser(FirebaseAuthHelper.getsInstance().getCurrUser().getUid());
            locationManager.removeUpdates(this);
        }
    }

    private void updateLocationOfUser(String uid) {
        com.example.ultramarket.database.Entities.Location location = new com.example.ultramarket.database.Entities.Location(
                mCountry,
                mCity,
                mRoad,
                String.valueOf(mLatitude),
                String.valueOf(mLongitude)
        );
        FirebaseAuthHelper.getsInstance().updateLocation(location, FirebaseAuthHelper.getsInstance().getCurrUser().getUid(),
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(LocationActivity.this, getString(R.string.location_saved), Toast.LENGTH_SHORT).show();
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
        mProgressBar.setVisibility(View.GONE);
        mGetLocationBtn.setEnabled(true);

    }

}