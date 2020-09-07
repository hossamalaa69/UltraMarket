package com.example.ultramarket.framgnets.user_fragments;

import android.animation.ValueAnimator;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ultramarket.R;
import com.example.ultramarket.adapters.user_adapters.BrandAdapter;
import com.example.ultramarket.adapters.user_adapters.CategoriesAdapter;
import com.example.ultramarket.adapters.user_adapters.ProductAdapter;
import com.example.ultramarket.database.Entities.Brand;
import com.example.ultramarket.database.Entities.Category;
import com.example.ultramarket.database.Entities.Notification;
import com.example.ultramarket.database.Entities.Product;
import com.example.ultramarket.helpers.AppExecutors;
import com.example.ultramarket.ui.userUi.Activities.ProductActivity;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserHomeFrag extends Fragment implements ProductAdapter.OnItemClicked, BrandAdapter.BrandCallBacks, CategoriesAdapter.CategoryCallBacks {
    public static final CharSequence TITLE = "Home";
    public static boolean catRvExtended = false;
    @BindView(R.id.usr_rv_category)
    RecyclerView rvCategories;
    @BindView(R.id.usr_rv_latest)
    RecyclerView rvLatestProd;
    @BindView(R.id.usr_rv_brands)
    RecyclerView rvBrands;
    @BindView(R.id.usr_rv_featured)
    RecyclerView rvFeaturedProd;
    @BindView(R.id.user_ad_img)
    ImageView adImage;
    @BindView(R.id.user_ad_title)
    TextView adName;
    @BindView(R.id.user_ad_body)
    TextView adBody;
    @BindView(R.id.user_ad_layout)
    View adLayout;

    @OnClick(R.id.user_ad_layout)
    public void onNotificationClicked(View view) {
        if (notiIdx > -1) {
            Intent intent = new Intent(getContext(), ProductActivity.class);
            intent.putExtra("prod_id", mNotifications.get(notiIdx).getProductID());
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(
                    getActivity()
                    , adImage, "image").toBundle());
        }
    }

    private CategoriesAdapter catAdapter;
    private BrandAdapter brandAdapter;
    private ProductAdapter prodAdapter;
    private ProductAdapter featuredProdAdapter;
    private ArrayList<Notification> mNotifications = new ArrayList<>();
    private boolean newNotification = false;
    private int notiIdx = -1;


    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mNotifications.size() > 0) {
                if (notiIdx < mNotifications.size() - 1) {
                    notiIdx++;
                } else {
                    notiIdx = 0;
                }
                Notification notification;
                if (newNotification) {
                    notification = mNotifications.get(mNotifications.size() - 1);
                    newNotification = false;
                } else {
                    notification = mNotifications.get(notiIdx);
                }
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateNotification(notification);
                    }
                });
            }
            handler.postDelayed(this, 5000);
        }
    };


    public static UserHomeFrag newInstance() {
        return new UserHomeFrag();
    }

    @BindView(R.id.user_home_show_more)
    Button showMoreBtn;

    @OnClick(R.id.user_home_show_more)
    void extendCatRv(View view) {
        if (catRvExtended) {
            ValueAnimator animator = ValueAnimator.ofInt(rvCategories.getHeight(), rvCategories.getMinimumHeight());
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int value = (int) valueAnimator.getAnimatedValue();
                    ViewGroup.LayoutParams params = rvCategories.getLayoutParams();
                    params.height = value;
                    showMoreBtn.setText(R.string.show_more);
                    rvCategories.setLayoutParams(params);
                }
            });
            animator.setDuration(300);
            animator.start();

        } else {
            rvCategories.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ValueAnimator animator = ValueAnimator.ofInt(rvCategories.getHeight(), rvCategories.getMeasuredHeight());
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int value = (int) valueAnimator.getAnimatedValue();
                    ViewGroup.LayoutParams params = rvCategories.getLayoutParams();
                    params.height = value;
                    rvCategories.setLayoutParams(params);
                    showMoreBtn.setText(R.string.show_less);
                }
            });
            animator.setDuration(300);
            animator.start();

        }
        catRvExtended = !catRvExtended;
    }


    @Override
    public void onResume() {
        handler.post(runnable);
        super.onResume();
    }

    @Override
    public void onPause() {
        handler.removeCallbacks(runnable);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_home_fragment, container, false);
        ButterKnife.bind(this, view);
        catAdapter = new CategoriesAdapter(getContext(), null, this);
        rvCategories.setAdapter(catAdapter);

        RecyclerView.LayoutManager catLayoutManager = new GridLayoutManager(getContext(), 3);
        rvCategories.setLayoutManager(catLayoutManager);
        rvCategories.setNestedScrollingEnabled(false);
        // product views
        prodAdapter = new ProductAdapter(getContext(), null, this);
        rvLatestProd.setAdapter(prodAdapter);
        RecyclerView.LayoutManager prodLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        rvLatestProd.setLayoutManager(prodLayoutManager);
        rvLatestProd.setHasFixedSize(true);
        //  featured product views
        featuredProdAdapter = new ProductAdapter(getContext(), null, this);
        rvFeaturedProd.setAdapter(featuredProdAdapter);
        RecyclerView.LayoutManager featuredProdLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        rvFeaturedProd.setLayoutManager(featuredProdLayoutManager);
        rvFeaturedProd.setHasFixedSize(true);
        //brand views
        brandAdapter = new BrandAdapter(getContext(), null, this);
        rvBrands.setAdapter(brandAdapter);
        RecyclerView.LayoutManager brandLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        rvBrands.setLayoutManager(brandLayoutManager);
        rvBrands.setHasFixedSize(true);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setObservers();
        AppExecutors.getInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                FirebaseDatabase.getInstance().getReference()
                        .child(Notification.class.getSimpleName())
                        .addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                mNotifications.add(snapshot.getValue(Notification.class));
                                newNotification = true;
                            }

                            @Override
                            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                            }

                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                            }

                            @Override
                            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

            }
        });
    }

    private void updateNotification(Notification notification) {
        if (notification == null) {
            adLayout.setVisibility(View.GONE);
            return;
        } else
            adLayout.setVisibility(View.VISIBLE);
        Picasso.get().load(notification.getImageUrl()).into(adImage);
        adName.setText(notification.getTitle());
        adBody.setText(notification.getBody());
        int random = new Random().nextInt(3);
        animateView(random);
    }

    private void animateView(int random) {
        if (random == 1) {
            TranslateAnimation animation = new TranslateAnimation(
                    -adName.getWidth(), adName.getX(), 0, 0);
            animation.setDuration(500);
            animation.setFillAfter(true);
            TranslateAnimation animation3 = new TranslateAnimation(
                    -adName.getWidth(), adBody.getX(), 0, 0);
            animation3.setDuration(500);
            animation3.setFillAfter(true);
            adName.setAnimation(animation);
            adBody.setAnimation(animation3);
        } else {
            TranslateAnimation animation = new TranslateAnimation(
                    adImage.getX() + adImage.getWidth(), adName.getX(), 0, 0);
            animation.setDuration(500);
            animation.setFillAfter(true);
            TranslateAnimation animation3 = new TranslateAnimation(
                    adImage.getX() + adImage.getWidth(), adBody.getX(), 0, 0);
            animation3.setDuration(500);
            animation3.setFillAfter(true);
            adName.setAnimation(animation);
            adBody.setAnimation(animation3);
        }
    }


    private void setObservers() {
        HomeViewModel mViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        mViewModel.getmCatList().observe(getViewLifecycleOwner(), new Observer<List<Category>>() {
            @Override
            public void onChanged(List<Category> categories) {
                catAdapter.setCategoryList(categories);
            }
        });
        mViewModel.getmBrandList().observe(getViewLifecycleOwner(), new Observer<List<Brand>>() {
            @Override
            public void onChanged(List<Brand> brands) {
                brandAdapter.setBrandList(brands);
            }
        });
        mViewModel.getmLatestProdList().observe(getViewLifecycleOwner(), new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                prodAdapter.setProdList(products);
            }
        });
        mViewModel.getmLatestProdList().observe(getViewLifecycleOwner(), new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                prodAdapter.setProdList(products);
            }
        });
        mViewModel.getmFeaturedProdList().observe(getViewLifecycleOwner(), new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                featuredProdAdapter.setProdList(products);
            }
        });
    }

    @Override
    public void onItemClicked(String prodId, View prodImage) {
        Intent intent = new Intent(getContext(), ProductActivity.class);
        intent.putExtra("prod_id", prodId);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(
                getActivity(),
                prodImage,
                "image").toBundle());
    }

    @Override
    public void onBrandClickedListener(Intent intent, View sharedView) {
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(
                getActivity(),
                sharedView,
                "name").toBundle());
    }

    @Override
    public void onCategoryClickedListener(Intent intent, View sharedView) {
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(
                getActivity(),
                sharedView,
                "name").toBundle());
    }
}