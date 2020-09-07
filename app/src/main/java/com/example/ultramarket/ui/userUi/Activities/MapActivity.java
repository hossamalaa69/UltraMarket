package com.example.ultramarket.ui.userUi.Activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ultramarket.R;
import com.example.ultramarket.adapters.user_adapters.PlacesAutoSuggestionAdapter;
import com.example.ultramarket.firebase.FirebaseAuthHelper;
import com.example.ultramarket.helpers.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "MapActivity";
    private static final LatLngBounds sLatLogBound = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));
    private double mLatitude;
    private double mLongitude;
    private String mCountry;
    private String mCity;
    private String mRoad;
    private boolean mSearchViewIsVisible = true;
    private PlacesAutoSuggestionAdapter autoSuggAdapter;
    private RecyclerView autoSuggRecyclerView;
    private GoogleApiClient googleApiClient;

    @BindView(R.id.user_map_activity_search_et)
    AutoCompleteTextView mSearchView;
    @BindView(R.id.user_map_activity_fragment)
    View mapView;

    @OnClick(R.id.user_map_activity_fap_save_location)
    public void onSaveClicked(View view) {
        getLocationDetails(mLatitude, mLongitude);
        updateLocationOfUser(FirebaseAuthHelper.getsInstance().getCurrUser().getUid());
    }

    @OnClick(R.id.user_map_activity_my_location_btn)
    public void onMyLocClicked(View view) {
        getDeviceLocation();
    }

    private static final float DEFAULT_ZOOM = 15f;
    // TODO  for init app
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_map);
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.map);
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setLogo(R.drawable.location_on_24);
        }
        initMap();
        mapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSearchViewIsVisible) {
                    searchViewTransitionHide();
                } else {
                    searchViewTransitionShow();
                }
                mSearchViewIsVisible = !mSearchViewIsVisible;
            }
        });

    }

    private String getLocationDetails(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude,
                    longitude, 1);
            mCity = addresses.get(0).getLocality();
            mRoad = addresses.get(0).getFeatureName();
            mCountry = addresses.get(0).getCountryName();
            return addresses.get(0).getAddressLine(0);
        } catch (IOException e) {

        }
        return "";
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
                        Utils.createToast(MapActivity.this, R.string.location_saved, Toast.LENGTH_SHORT);
                        finish();
                    }
                });
    }


    private void searchViewTransitionHide() {
        TranslateAnimation animation = new TranslateAnimation(
                0,
                0,
                mSearchView.getY(),
                -mSearchView.getHeight());
        animation.setDuration(300);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mSearchView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mSearchView.setAnimation(animation);
        animation.start();
    }

    private void searchViewTransitionShow() {
        TranslateAnimation animation = new TranslateAnimation(
                0,
                0,
                -mSearchView.getHeight(),
                mSearchView.getY()
        );
        animation.setDuration(300);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mSearchView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mSearchView.setAnimation(animation);
        animation.start();
    }

    private void initSearchView() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        autoSuggAdapter = new PlacesAutoSuggestionAdapter(this, googleApiClient, sLatLogBound, null);
        mSearchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH
                        || i == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                    geoLocation(mSearchView.getText().toString().trim());
                    hideSoftKeyboard();
                }
                return false;
            }
        });
    //    mSearchView.setAdapter(autoSuggAdapter);
    }

    private void geoLocation(String s) {
        Geocoder geocoder = new Geocoder(this);
        List<Address> addresses = new ArrayList<>();
        try {
            addresses = geocoder.getFromLocationName(s, 5);
            if (addresses.size() > 0)
                moveCameraToLocation(new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude()), DEFAULT_ZOOM,
                        addresses.get(0).getAddressLine(0));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //TODO get device location
    private void getDeviceLocation() {
        //TODO init fused location
        //TODO for device current location
        FusedLocationProviderClient mFusedLocProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            //check if permission is granted
            // focus on type of Task here is gms.Task
            Task location = mFusedLocProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful())// got the location
                    {
                        Location currLoc = (Location) task.getResult();
                        if (currLoc != null) {
                            moveCameraToLocation(new LatLng(currLoc.getLatitude(), currLoc.getLongitude()), DEFAULT_ZOOM,
                                    "My Location");
                            mSearchView.setText("My Location", false);
                            return;
                        }
                    }
                    Toast.makeText(MapActivity.this, "unable to get current location, check location is on", Toast.LENGTH_LONG).show();
                }
            });

        } catch (SecurityException e) {
            Log.d(TAG, "getDeviceLocation: SecurityException" + e.getMessage());

        }
    }

    private void moveCameraToLocation(LatLng latLng, float zoom, String title) {
        mMap.clear();
        mLatitude = latLng.latitude;
        mLongitude = latLng.longitude;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title(title);
        mMap.addMarker(options);
    }

    //TODO get fragment in layout and call getMapAsync and provide a listener
    //TODO fragment will be automatically updated but to make operation to the map keep reference to it
    private void initMap() {
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.user_map_activity_fragment);
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                getDeviceLocation();
                if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapActivity.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mMap.setMyLocationEnabled(true);
                // for search bar edit ui of mMap remove button
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        moveCameraToLocation(latLng, DEFAULT_ZOOM, getLocationDetails(latLng.latitude, latLng.longitude));
                    }
                });
                initSearchView();

            }
        });
    }

    //hide the keyboard
    private void hideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}