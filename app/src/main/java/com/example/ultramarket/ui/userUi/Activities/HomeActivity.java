package com.example.ultramarket.ui.userUi.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.ultramarket.R;
import com.example.ultramarket.adapters.user_adapters.ViewPagerAdapter;
import com.example.ultramarket.database.Entities.User;
import com.example.ultramarket.firebase.FirebaseAuthHelper;
import com.example.ultramarket.framgnets.user_fragments.UserWishlistFrag;
import com.example.ultramarket.helpers.AppExecutors;
import com.example.ultramarket.ui.SplashActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
        if (FirebaseAuthHelper.getsInstance().getCurrUser() != null) {
            startActivity(new Intent(this, LocationActivity.class));
        } else {
            Toast.makeText(this, R.string.you_must_signin_first, Toast.LENGTH_SHORT).show();
        }

    }

    private TextView mUserName;
    private ImageView mUserIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_home);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setupViewPager();
        setUpDrawerLayout();
        if (FirebaseAuthHelper.getsInstance().getCurrUser() != null)
            AppExecutors.getInstance().networkIO().execute(new Runnable() {
                @Override
                public void run() {
                    FirebaseDatabase.getInstance().getReference()
                            .child(User.class.getSimpleName()).child(FirebaseAuthHelper.getsInstance().getCurrUser().getUid())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    User user = snapshot.getValue(User.class);
                                    updateNavViewHeader(user);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }
            });
    }

    private void updateNavViewHeader(User user) {
        if (user != null) {
            mUserName.setText(user.getName());
            if (user.getImageUrl() != null)
                Picasso.get().load(user.getImageUrl()).into(mUserIcon);
            else {
                mUserIcon.setImageResource(R.drawable.logo);
            }
        } else {
            mUserName.setText(R.string.not_logged_in);
            mUserIcon.setImageResource(R.drawable.logo);
        }
    }

    @Override
    public void onLoginClickListener() {
        startActivity(new Intent(HomeActivity.this, SignUpActivity.class));
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
                startLogin();
            case R.id.user_drawer_contact_us:
                // contactUs();
            case R.id.user_drawer_terms:
                //  openTermsBottomNavigation();
            case R.id.user_drawer_about_us:
                //  openAboutUsBottomSheet();
        }
        return false;
    }

    private void startLogin() {
        if (FirebaseAuthHelper.getsInstance().getCurrUser() == null)// user not signed it yet
        {
            startActivity(new Intent(this, SignUpActivity.class));
        } else // user signed in needs to log out first
        {
            showLogOutAlertDialog();
        }
    }

    private void showLogOutAlertDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.login)
                .setMessage(getString(R.string.already_signed_in, FirebaseAuthHelper.getsInstance().getCurrUser().getEmail()))
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.logout, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseAuthHelper.getsInstance().logOut(HomeActivity.this, new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                updateNavViewHeader(null);
                                startActivity(new Intent(HomeActivity.this, SignUpActivity.class));
                            }
                        });
                    }
                }).create();
        alertDialog.show();

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
        mUserName = navView.getHeaderView(0).findViewById(R.id.user_drawer_header_user_name);
        mUserName.setOnClickListener(new View.OnClickListener() {
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