package com.example.ultramarket.framgnets.user_fragments;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
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

    public static UserHomeFrag newInstance() {
        return new UserHomeFrag();
    }

    @BindView(R.id.user_home_show_more)
    Button showMoreBtn;

    @OnClick(R.id.user_home_show_more)
    void extendCatRv(View view) {
        if (catRvExtended) {
            ViewGroup.LayoutParams params = rvCategories.getLayoutParams();
            params.height = rvCategories.getMinimumHeight();
            showMoreBtn.setText(R.string.show_more);
            rvCategories.setLayoutParams(params);
        } else {
            ViewGroup.LayoutParams params = rvCategories.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            rvCategories.setLayoutParams(params);
            showMoreBtn.setText(R.string.show_less);
        }
        catRvExtended = !catRvExtended;
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
        Thread mNotificationThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    for (int i = 0; i < mNotifications.size(); i++) {
                        Notification notification;
                        if (newNotification) {
                            notification = mNotifications.get(mNotifications.size() - 1);
                            newNotification = false;
                        } else {
                            notification = mNotifications.get(i);
                        }
                        notiIdx = i;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateNotification(notification);
                            }
                        });
                        try {
                            Thread.sleep(1000 * 5);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        mNotificationThread.start();
    /*  for (int i = 1; i < 10; i++)
            mViewModel.insertBrand(new Brand(i, "brand", null));
         for (int i = 1; i < 10; i++)
            mViewModel.insertCategory(new Category(i, "food", null));
         for (int i = 1; i < 5; i++)
            mViewModel.insertProd(new Product(i, "featured prod" + i, null, "1 kg", 123, 14, "hahah", true, 0.5, i, i , new Date()));
       for (int i = 5; i < 10; i++)
            mViewModel.insertProd
            (new Product(i, "product" + i, null, "500 g", 123, 14, "hahah", false, 0, i, i, new Date()));
*/
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
        } else if (random == 2) {
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
        } else {
            adBody.setAlpha(0f);
            adName.setAlpha(0f);
            adBody.animate().alpha(1.0f).setDuration(850).start();
            adName.animate().alpha(1.0f).setDuration(900).start();
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