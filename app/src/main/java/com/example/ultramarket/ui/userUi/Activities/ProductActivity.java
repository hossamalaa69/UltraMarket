package com.example.ultramarket.ui.userUi.Activities;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ultramarket.R;
import com.example.ultramarket.database.Entities.Product;
import com.example.ultramarket.firebase.FirebaseAuthHelper;
import com.example.ultramarket.helpers.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProductActivity extends AppCompatActivity {

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
    TextView mAddToWishlist;
    @BindView(R.id.user_product_description)
    TextView mProdDescription;
    @BindView(R.id.user_product_item_price_layout)
    LinearLayout mPriceLayout;
    private ActionBar actionBar;

    @OnClick(R.id.user_product__add_to_wishlist)
    public void OnAddItemToWishListClicked(View view) {
        if (FirebaseAuthHelper.getsInstance().getCurrUser() != null) {
            // TODO Add product to wishlist
        } else {
            Toast.makeText(ProductActivity.this, R.string.you_must_signin_first, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_product);
        ButterKnife.bind(this);
        actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setTitle("");
        Intent intent = getIntent();
        if (intent != null) {
            String prodId = intent.getStringExtra("prod_id");
            if (prodId != null) {
                loadProductData(prodId);
            }
        }
    }

    private void loadProductData(String prodId) {
        FirebaseDatabase.getInstance().getReference()
                .child(Product.class.getSimpleName()).child(prodId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Product product = snapshot.getValue(Product.class);
                        if (product != null) {
                            updateUI(product);
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
        mPriceLayout.setVisibility(View.VISIBLE);
        mProdName.setText(product.getName());
        double oldPrice = product.getPrice();
        if (product.isHasOffer()) {
            double newPrice = Utils.calcDiscount(oldPrice, product.getDiscount_percentage());
            mProdOldPrice.setVisibility(View.VISIBLE);
            mProdNewPrice.setVisibility(View.VISIBLE);
            mProdSavedMoney.setVisibility(View.VISIBLE);
            mProdOldPrice.setPaintFlags(mProdOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            mProdOldPrice.setText(String.valueOf(oldPrice).concat(product.getCurrency()));
            mProdNewPrice.setText(String.valueOf(newPrice).concat(product.getCurrency()));
            mProdSavedMoney.setText(String.valueOf(oldPrice - newPrice).concat(product.getCurrency()));

        } else {
            mProdOldPrice.setVisibility(View.GONE);
            mProdNewPrice.setVisibility(View.VISIBLE);
            mProdNewPrice.setText(String.valueOf(oldPrice).concat(product.getCurrency()));
            mProdSavedMoney.setVisibility(View.GONE);
        }
        mProdWeightUnit.setText(product.getUnit());
        mProdDescription.setText(product.getDescription());
    }
}