package com.example.ultramarket.ui.userUi.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.ultramarket.R;
import com.example.ultramarket.adapters.user_adapters.ViewPagerAdapter;
import com.example.ultramarket.firebase.FirebaseAuthHelper;
import com.example.ultramarket.framgnets.user_fragments.UserWishlistFrag;
import com.example.ultramarket.helpers.Utils;
import com.example.ultramarket.ui.SplashActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,

        UserWishlistFrag.OnClickedListener {

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
    public void onLocationButtonClicked(View view) {
        startActivity(new Intent(this, LocationActivity.class));
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
        if (Utils.user != null)
            updateNavViewHeader(Utils.user.getImageUrl(), Utils.user.getEmail());
    }

    private void updateNavViewHeader(String imageUri, String email) {
        mEmail.setText(email);
        Picasso.get().load(imageUri).into(mUserIcon);
    }

    @Override
    public void onLoginClickListener() {
        FirebaseAuthHelper.getsInstance().loginToFirebase(this);
    }


    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onPause() {
        super.onPause();
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
                FirebaseAuthHelper.getsInstance().loginToFirebase(this);
            case R.id.user_drawer_contact_us:
                // contactUs();
            case R.id.user_drawer_terms:
                //  openTermsBottomNavigation();
            case R.id.user_drawer_about_us:
                //  openAboutUsBottomSheet();
        }
        return false;
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
        mEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, UserProfile.class));
            }
        });
        mUserIcon = navView.getHeaderView(0).findViewById(R.id.user_drawer_header_icon);
        toggle.syncState();
    }


    @Override
    public void onBackPressed() {
        //check if drawer is open
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {   //close drawer
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (viewPager.getCurrentItem() == 0) {
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
        switch (item.getItemId()) {
            case R.id.user_menu_cart:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}