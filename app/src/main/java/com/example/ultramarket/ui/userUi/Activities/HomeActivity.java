package com.example.ultramarket.ui.userUi.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Explode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
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
import com.example.ultramarket.framgnets.user_fragments.UserCartFrag;
import com.example.ultramarket.helpers.AppExecutors;
import com.example.ultramarket.helpers.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,

        UserCartFrag.OnClickedListener {
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

    private AlertDialog mRateDialog;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (isRating) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showRateAlertDialog();
                    }
                });
            }
            handler.postDelayed(this, 1000 * 60);
        }
    };
    private boolean isRating = true;

    @OnClick(R.id.user_toolbar_location)
    public void onLocationButtonClicked(View view) {
        if (FirebaseAuthHelper.getsInstance().getCurrUser() != null) {
            startActivity(new Intent(this, LocationActivity.class));
        } else {
            Utils.createToast(this, R.string.you_must_signin_first, Toast.LENGTH_SHORT);
        }
    }

    private TextView mUserName;
    private ImageView mUserIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_home);
        ButterKnife.bind(this);
        setMarketAds();
        Intent intent = getIntent();
        String adProduct = intent.getStringExtra("product_id");
        getWindow().setSharedElementEnterTransition(new Explode());
        setSupportActionBar(toolbar);
        setupViewPager();
        setUpDrawerLayout();
        if (FirebaseAuthHelper.getsInstance().getCurrUser() != null) {
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

        if (FirebaseAuthHelper.getsInstance().getCurrUser() != null) {
            AppExecutors.getInstance().networkIO().execute(new Runnable() {
                @Override
                public void run() {
                    FirebaseDatabase.getInstance().getReference()
                            .child(User.class.getSimpleName()).child(FirebaseAuthHelper.getsInstance().getCurrUser().getUid())
                            .child("rate").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                int rate = snapshot.getValue(Integer.class);
                                if (rate == 0) {
                                    handler.post(runnable); // show rating dialog
                                } else {
                                    isRating = false;
                                    if (mRateDialog != null && mRateDialog.isShowing())
                                        mRateDialog.dismiss();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });
        }
        if (adProduct != null) {
            Intent intent1 = new Intent(this, ProductActivity.class);
            intent1.putExtra("prod_id", adProduct);
            startActivity(intent1);
        }
    }

    private void setMarketAds() {
        AppExecutors.getInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                FirebaseMessaging.getInstance().subscribeToTopic("offers")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                String msg = "Subscribed in notifications successfully";
                                if (!task.isSuccessful()) {
                                    msg = "Failed Subscribtion in notifications";
                                }
                                // Toast.makeText(HomeActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private void showRateAlertDialog() {
        if (mRateDialog != null && mRateDialog.isShowing()) {
            mRateDialog.cancel();
            mRateDialog = null;
        }
        mRateDialog = new AlertDialog.Builder(this).create();
        View view = LayoutInflater.from(this).inflate(R.layout.user_rate_layout, null, false);
        RatingBar mRatingBar = view.findViewById(R.id.user_rating_bar);
        Button cancel = view.findViewById(R.id.user_rate_cancel);
        cancel.setOnClickListener(view1 -> mRateDialog.dismiss());
        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                addRatingToFirebase((int) v);
            }
        });
        mRateDialog.setView(view);
        mRateDialog.show();
    }

    private void addRatingToFirebase(int rate) {
        if (FirebaseAuthHelper.getsInstance().getCurrUser() != null) {
            AppExecutors.getInstance().networkIO().execute(new Runnable() {
                @Override
                public void run() {
                    FirebaseAuthHelper.getsInstance()
                            .addRating(FirebaseAuthHelper.getsInstance().getCurrUser().getUid(),
                                    rate, null);
                }
            });
        }
    }

    private void updateNavViewHeader(User user) {
        if (user != null) {
            mUserName.setText(user.getName());
            if (user.getImageUrl() != null) {
                Picasso.get().load(user.getImageUrl())
                        .resize(100, 100).into(mUserIcon);
            } else {
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
                viewPager.setCurrentItem(2); // offers fragment
                drawerLayout.closeDrawer(GravityCompat.START);//close drawer
                break;
            case R.id.user_drawer_category:
                viewPager.setCurrentItem(viewPager.getCurrentItem(), false);
                viewPager.setCurrentItem(1); // category fragment
                drawerLayout.closeDrawer(GravityCompat.START);//close drawer
                break;
            case R.id.user_drawer_cart:
                openCartFrag();
                break;
            case R.id.user_drawer_track:
                viewPager.setCurrentItem(viewPager.getCurrentItem(), false);
                viewPager.setCurrentItem(4); // category fragment
                drawerLayout.closeDrawer(GravityCompat.START);//close drawer
                break;
            case R.id.user_drawer_login:
                startLogin();
                break;
            case R.id.user_drawer_contact_us:
                contactUs();
                break;
            case R.id.user_drawer_terms:
                startActivity(new Intent(this, AboutUsActivity.class).putExtra("type", "terms"));
                break;
            case R.id.user_drawer_about_us:
                startActivity(new Intent(this, AboutUsActivity.class).putExtra("type", "about_us"));
                break;
        }
        return false;
    }

    private void contactUs() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:contact@ultramarket.com"));
        intent.putExtra(Intent.EXTRA_EMAIL, FirebaseAuthHelper.getsInstance().getCurrUser().getEmail());
        intent.putExtra(Intent.EXTRA_SUBJECT, "Contact Us");
        intent.putExtra(Intent.EXTRA_TEXT, FirebaseAuthHelper.getsInstance().getCurrUser().getDisplayName());
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Utils.createToast(this, R.string.no_app_send_messages, Toast.LENGTH_SHORT);
        }
    }


    private void openCartFrag() {
        viewPager.setCurrentItem(viewPager.getCurrentItem(), false);
        viewPager.setCurrentItem(3); // category fragment
        drawerLayout.closeDrawer(GravityCompat.START);//close drawer
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
                if (FirebaseAuthHelper.getsInstance().getCurrUser() != null)
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
            Intent i = new Intent();
            i.setAction(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_HOME);
            this.startActivity(i);
        } else {
            viewPager.setCurrentItem(viewPager.getCurrentItem(), false);
            viewPager.setCurrentItem(0); // offers fragment
            drawerLayout.closeDrawer(GravityCompat.START);//close drawer
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
                openCartFrag();
                break;
            case R.id.user_menu_notification:
                openNotificationActivity();
                break;
            case R.id.user_menu_search:
                openSearchActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    private void openSearchActivity() {
        startActivity(new Intent(this, SearchActivity.class));
    }

    private void openNotificationActivity() {
        startActivity(new Intent(this, NotificationActivity.class));
    }
}