package com.example.ultramarket.ui.userUi.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.viewpager.widget.ViewPager;

import com.example.ultramarket.R;
import com.example.ultramarket.adapters.user_adapters.ViewPagerAdapter;
import com.example.ultramarket.database.Entities.User;
import com.example.ultramarket.firebase.FirebaseAuthHelper;
import com.example.ultramarket.framgnets.user_fragments.UserWishlistFrag;
import com.example.ultramarket.helpers.Utils;
import com.example.ultramarket.ui.SplashActivity;
import com.firebase.ui.auth.AuthUI;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        FirebaseAuthHelper.FirebaseAuthCallBacks,
        UserWishlistFrag.OnClickedListener {

    private static final int RC_SIGN_IN = 1;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.user_nav_view)
    NavigationView navView;
    @BindView(R.id.user_view_pager)
    ViewPager viewPager;
    @BindView(R.id.user_tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.user_toolbar)
    Toolbar toolbar;
    @OnClick(R.id.user_toolbar_location)
    public void onLocationButtonClicked(View view){
        startActivity(new Intent(this,LocationActivity.class));
    }

    private TextView mEmail;
    private ImageView mUserIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_home);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setupViewPager();
        setUpDrawerLayout();
    }

    private void updateNavViewHeader(String imageUri, String email) {
        mEmail.setText(email);
        Picasso.get().load(imageUri).into(mUserIcon);
    }

    @Override
    public void onLoginStateChanges(User user) {
        Utils.user = user;
        updateNavViewHeader(user.getImageUrl(), user.getEmail());
        Toast.makeText(HomeActivity.this, "logged in\n".concat(user.getEmail()), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoggedOutStateChanges() {
        Utils.user = null;
        updateNavViewHeader(null, null);
    }

    @Override
    public void onLoginClickListener() {
        loginToFirebase();
    }


    @Override
    protected void onResume() {
        super.onResume();
        FirebaseAuthHelper.getsInstance().attachAuthStateListener(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseAuthHelper.getsInstance().detachAuthStateListener();


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.user_drawer_offers:
                viewPager.setCurrentItem(viewPager.getCurrentItem(), false);
                viewPager.setCurrentItem(3); // offers fragment
                drawerLayout.closeDrawer(GravityCompat.START);//close drawer
            case R.id.user_drawer_category:
                viewPager.setCurrentItem(viewPager.getCurrentItem(), false);
                viewPager.setCurrentItem(1); // category fragment
                drawerLayout.closeDrawer(GravityCompat.START);//close drawer
            case R.id.user_drawer_wishlist:
                viewPager.setCurrentItem(viewPager.getCurrentItem(), false);
                viewPager.setCurrentItem(2); // category fragment
                drawerLayout.closeDrawer(GravityCompat.START);//close drawer
            case R.id.user_drawer_track:
                // go to track order
            case R.id.user_drawer_login:
                loginToFirebase();
            case R.id.user_drawer_contact_us:
                // contactUs();
            case R.id.user_drawer_terms:
                //  openTermsBottomNavigation();
            case R.id.user_drawer_about_us:
                //  openAboutUsBottomSheet();
        }
        return false;
    }

    private void loginToFirebase() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build());
        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setLogo(R.drawable.logo)
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * setup drawer layout and its items
     */
    private void setUpDrawerLayout() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawerLayout, toolbar, R.string.open_nav_dawer,
                R.string.close_nav_drawer);
        drawerLayout.addDrawerListener(toggle);
        navView.setNavigationItemSelectedListener(this);
        mEmail = navView.getHeaderView(0).findViewById(R.id.user_drawer_header_email);
        mUserIcon = navView.getHeaderView(0).findViewById(R.id.user_drawer_header_icon);
        toggle.syncState();
    }


    @Override
    public void onBackPressed() {
        //check if drawer is open
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {   //close drawer
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if(viewPager.getCurrentItem() == 0) {
            SplashActivity.isBackPressed = true;
            super.onBackPressed();
        }

    }

    /**
     * setup view pager adapter and tab layout
     */
    private void setupViewPager() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.user_menu_cart:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}