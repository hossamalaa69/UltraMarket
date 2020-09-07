package com.example.ultramarket.ui.userUi.Activities;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.transition.Slide;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ultramarket.R;
import com.example.ultramarket.database.Entities.Brand;
import com.example.ultramarket.database.Entities.Product;
import com.example.ultramarket.firebase.FirebaseAuthHelper;
import com.example.ultramarket.helpers.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProductActivity extends AppCompatActivity {

    public static final int INCREASE = 1;
    public static final int DECREASE = 2;
    @BindView(R.id.user_product_image)
    ImageView mProdImage;
    @BindView(R.id.user_product_name)
    TextView mProdName;
    @BindView(R.id.user_product_item_old_price)
    TextView mProdOldPrice;
    @BindView(R.id.user_product_item_new_price)
    TextView mProdNewPrice;
    @BindView(R.id.user_product_you_saved_money)
    TextView mProdSavedMoney;
    @BindView(R.id.user_product__weight_unit)
    TextView mProdWeightUnit;
    @BindView(R.id.user_product__add_to_wishlist)
    Button mAddToWishlist;
    @BindView(R.id.user_product_increase_to_wishlist)
    ImageButton mIncreaseInWishList;
    @BindView(R.id.user_product_decrease_from_wishlist)
    ImageButton mmDecreaseInWishList;
    @BindView(R.id.user_product_description)
    TextView mProdDescription;
    @BindView(R.id.user_product_item_price_layout)
    LinearLayout mPriceLayout;
    @BindView(R.id.user_product_progress)
    ProgressBar mProgressBar;
    @BindView(R.id.user_product_brand)
    TextView prodBrand;
    private ActionBar actionBar;
    private String prodId;
    private Product mProduct;
    private boolean inNotification = false;
    private void disableBtns() {
        mmDecreaseInWishList.setEnabled(false);
        mIncreaseInWishList.setEnabled(false);
        mAddToWishlist.setEnabled(false);
    }

    private void enableBtns() {
        mmDecreaseInWishList.setEnabled(true);
        mIncreaseInWishList.setEnabled(true);
        mAddToWishlist.setEnabled(true);
    }

    @OnClick(R.id.user_product_increase_to_wishlist)
    public void onIncreaseInWishListClicked(View view) {
        OnAddItemToWishListClicked(null);
    }

    @OnClick(R.id.user_product_decrease_from_wishlist)
    public void onDecreaseInWishListClicked(View view) {
        addProductToFirebase(prodId, DECREASE);
    }

    @OnClick(R.id.user_product__add_to_wishlist)
    public void OnAddItemToWishListClicked(View view) {
        addProductToFirebase(prodId, INCREASE);
    }

    private void addProductToFirebase(String prodId, int operation) {
        if (FirebaseAuthHelper.getsInstance().getCurrUser() == null) {
            Utils.createToast(ProductActivity.this, R.string.you_must_signin_first, Toast.LENGTH_SHORT);
            return;
        }
        showProgress();
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference()
                .child("Cart").child(FirebaseAuthHelper.getsInstance().getCurrUser().getUid());
        cartRef.child(prodId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                OnSuccessListener<Void> listener = new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //update ui
                        enableBtns();
                        hideProgress();
                        mmDecreaseInWishList.setVisibility(View.VISIBLE);
                    }
                };
                if (!snapshot.exists() && mProduct.getCount() > 0) {
                    cartRef.child(prodId).setValue(1).addOnSuccessListener(listener);
                    mAddToWishlist.setText(String.valueOf(1));
                } else if (snapshot.exists() &&
                        operation == INCREASE &&
                        snapshot.getValue(Integer.class) + 1 <= mProduct.getCount()) {
                    int num = snapshot.getValue(Integer.class);
                    cartRef.child(prodId).setValue(num + 1).addOnSuccessListener(listener);
                    mAddToWishlist.setText(String.valueOf(num + 1));
                } else if (snapshot.exists() &&
                        operation == DECREASE &&
                        snapshot.getValue(Integer.class) - 1 > 0) {
                    int num = snapshot.getValue(Integer.class);
                    cartRef.child(prodId).setValue(num > 1 ? num - 1 : 0).addOnSuccessListener(listener);
                    mAddToWishlist.setText(String.valueOf(num > 1 ? num - 1 : 0));
                } else {
                    listener.onSuccess(null);
                    Utils.createToast(ProductActivity.this, R.string.not_available, Toast.LENGTH_SHORT);
                    return;
                }
                Utils.createToast(ProductActivity.this, R.string.done, Toast.LENGTH_SHORT);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void setAvailable() {
        mmDecreaseInWishList.setVisibility(View.VISIBLE);
        mIncreaseInWishList.setVisibility(View.VISIBLE);
        mAddToWishlist.setText(R.string.add);
        mAddToWishlist.setEnabled(true);
    }

    private void setNotAvailable() {
        mmDecreaseInWishList.setVisibility(View.GONE);
        mIncreaseInWishList.setVisibility(View.GONE);
        mAddToWishlist.setText(R.string.not_available);
        mAddToWishlist.setEnabled(false);
    }

    private void showProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
        mAddToWishlist.setVisibility(View.INVISIBLE);
    }

    private void hideProgress() {
        mProgressBar.setVisibility(View.GONE);
        mAddToWishlist.setVisibility(View.VISIBLE);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //add transition
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setEnterTransition(new Slide());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_product);
        //bind views
        ButterKnife.bind(this);
        mmDecreaseInWishList.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setTitle("");
        Intent intent = getIntent();
        if (intent != null) {
            prodId = intent.getStringExtra("prod_id");
            inNotification = intent.getBooleanExtra("is_notification",false);
            if (prodId != null) {
                loadProductData(prodId);
            }
        }
    }

    private void loadProductData(String prodId) {
        FirebaseDatabase.getInstance().getReference()
                .child(Product.class.getSimpleName()).child(prodId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Product product = snapshot.getValue(Product.class);
                        if (product != null) {
                            DatabaseReference brandDbRef = FirebaseDatabase.getInstance()
                                    .getReference().child(Brand.class.getSimpleName()).child(product.getBrand_ID());
                            brandDbRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Brand brand = dataSnapshot.getValue(Brand.class);
                                        product.setBrand_name(brand.getName());
                                        mProduct = product;
                                        updateUI(product);
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(ProductActivity.this, "failed loading brand", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(this.getClass().getSimpleName(), error.getMessage());
                        Toast.makeText(ProductActivity.this, R.string.failed_to_load_product, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUI(Product product) {
        if (actionBar != null) {
            actionBar.setTitle(product.getName());
        }
        if (product.getImage() != null) {
            Picasso.get().load(product.getImage()).into(mProdImage);
        }
        if (product.getCount() > 0) {
            setAvailable();
        } else {
            setNotAvailable();
        }
        mPriceLayout.setVisibility(View.VISIBLE);
        mProdName.setText(product.getName());
        prodBrand.setText(getString(R.string.brand_is, product.getBrand_name()));
        double oldPrice = product.getPrice();
        if (product.isHasOffer()) {
            double newPrice = Utils.calcDiscount(oldPrice, product.getDiscount_percentage());
            mProdOldPrice.setVisibility(View.VISIBLE);
            mProdNewPrice.setVisibility(View.VISIBLE);
            mProdSavedMoney.setVisibility(View.VISIBLE);
            mProdOldPrice.setPaintFlags(mProdOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            mProdOldPrice.setText(String.valueOf(oldPrice).concat(product.getCurrency()));
            mProdNewPrice.setText(String.valueOf(newPrice).concat(product.getCurrency()));
            mProdSavedMoney.setText(getString(R.string.you_saved_money, oldPrice - newPrice).concat(product.getCurrency()));

        } else {
            mProdOldPrice.setVisibility(View.GONE);
            mProdNewPrice.setVisibility(View.VISIBLE);
            mProdNewPrice.setText(String.valueOf(oldPrice).concat(product.getCurrency()));
            mProdSavedMoney.setVisibility(View.GONE);
        }
        mProdWeightUnit.setText(product.getUnit());
        mProdDescription.setText(product.getDescription());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}