package com.example.ultramarket.adapters.user_adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ultramarket.R;
import com.example.ultramarket.database.Entities.Product;
import com.example.ultramarket.firebase.FirebaseAuthHelper;
import com.example.ultramarket.helpers.AppExecutors;
import com.example.ultramarket.helpers.Utils;
import com.example.ultramarket.ui.userUi.Activities.ProductActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.ProductViewHolder> {

    private static final int INCREASE = 1;
    private static final int DECREASE = 2;
    private Context mContext;
    private List<Product> productList;
    private ProductCallBacks interfaceInstance;

    public void updateProduct(Product product) {
        for (int i = 0; i < productList.size(); i++) {
            if (product.getID().matches(productList.get(i).getID())) {
                productList.get(i).setCount(product.getCount());
                notifyDataSetChanged();
            }
        }
    }

    public void clear() {
        productList = null;
        notifyDataSetChanged();
    }

    public void insertProduct(Product product) {
        if(productList == null) productList = new ArrayList<>( );
        productList.add(product);
        notifyDataSetChanged();
    }

    public interface ProductCallBacks {
        void onProductClickedListener(Intent intent, View shared1, View shared2);
    }

    public OfferAdapter(Context mContext, List<Product> productList, Fragment listener) {
        this.mContext = mContext;
        interfaceInstance = (ProductCallBacks) listener;
        this.productList = productList;
    }

    public OfferAdapter(Context mContext, List<Product> productList, Context listener) {
        this.mContext = mContext;
        interfaceInstance = (ProductCallBacks) listener;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_offers_list_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (productList == null) return 0;
        return productList.size();
    }

    public void setProdList(List<Product> products) {
        productList = products;
        notifyDataSetChanged();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.user_offers_frag_item_image)
        ImageView prodImage;
        @BindView(R.id.user_offers_item_title)
        TextView prodName;
        @BindView(R.id.user_offers_item_price_layout)
        LinearLayout priceLayout;
        @BindView(R.id.user_product_item_old_price)
        TextView oldPrice;
        @BindView(R.id.user_product_item_new_price)
        TextView newPrice;
        @BindView(R.id.user_offers_item__weight_unit)
        TextView prodWeight;
        @BindView(R.id.user_offers_item_saved_amount)
        TextView prodSavedAmount;
        @BindView(R.id.user_offers_add_to_wishlist)
        Button prodAddBtn;
        @BindView(R.id.user_offers_increase_to_wishlist)
        ImageButton prodIncBtn;
        @BindView(R.id.user_offers_progress)
        ProgressBar progressBar;
        @BindView(R.id.user_offers_decrease_from_wishlist)
        ImageButton prodDecreaseInCartBtn;

        @OnClick(R.id.user_offers_add_to_wishlist)
        public void prodAddBtn(View view) {
            addProductToFirebase(productList.get(getAdapterPosition()).getID(), INCREASE);
        }

        @OnClick(R.id.user_offers_increase_to_wishlist)
        public void prodIncreaseInCart(View view) {
            prodAddBtn(null);
        }

        @OnClick(R.id.user_offers_decrease_from_wishlist)
        public void prodDecreaseInCart(View view) {
            addProductToFirebase(productList.get(getAdapterPosition()).getID(), DECREASE);
        }

        private void disableBtns() {
            prodDecreaseInCartBtn.setEnabled(false);
            prodDecreaseInCartBtn.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            prodAddBtn.setVisibility(View.INVISIBLE);
            prodAddBtn.setEnabled(false);
        }

        private void enableBtns() {
            progressBar.setVisibility(View.GONE);
            prodAddBtn.setVisibility(View.VISIBLE);
            prodDecreaseInCartBtn.setEnabled(true);
            prodDecreaseInCartBtn.setEnabled(true);
            prodAddBtn.setEnabled(true);
        }

        private void addProductToFirebase(String prodId, int operation) {
            if (FirebaseAuthHelper.getsInstance().getCurrUser() == null) {
                Utils.createToast(mContext, R.string.you_must_signin_first, Toast.LENGTH_SHORT);
                return;
            }
            disableBtns();
            AppExecutors.getInstance().networkIO().execute(() -> {
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
                                prodDecreaseInCartBtn.setVisibility(View.VISIBLE);
                            }
                        };
                        if (!snapshot.exists() && productList.get(getAdapterPosition()).getCount() > 0) {
                            cartRef.child(prodId).setValue(1).addOnSuccessListener(listener);
                            prodAddBtn.setText(String.valueOf(1));
                        } else if (snapshot.exists() &&
                                operation == INCREASE &&
                                snapshot.getValue(Integer.class) + 1 <= productList.get(getAdapterPosition()).getCount()) {
                            int num = snapshot.getValue(Integer.class);
                            cartRef.child(prodId).setValue(num + 1).addOnSuccessListener(listener);
                            prodAddBtn.setText(String.valueOf(num + 1));
                        } else if (snapshot.exists() &&
                                operation == DECREASE &&
                                snapshot.getValue(Integer.class) - 1 > 0) {
                            int num = snapshot.getValue(Integer.class);
                            cartRef.child(prodId).setValue(num > 1 ? num - 1 : 0).addOnSuccessListener(listener);
                            prodAddBtn.setText(String.valueOf(num > 1 ? num - 1 : 0));
                        } else {
                            listener.onSuccess(null);
                            Utils.createToast(mContext, R.string.not_available, Toast.LENGTH_SHORT);
                            return;
                        }
                        Utils.createToast(mContext, R.string.done, Toast.LENGTH_SHORT);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            });
        }

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext.getApplicationContext(), ProductActivity.class);
                    intent.putExtra("prod_id", productList.get(getAdapterPosition()).getID());
                    interfaceInstance.onProductClickedListener(intent, prodImage, prodSavedAmount);
                }
            });
            priceLayout.setVisibility(View.VISIBLE);
        }

        public void bind(int position) {
            if (productList.get(position).getCount() > 0) {
                setAvailable();
            } else {
                setNotAvailable();
            }
            if (productList.get(position).getImage() != null)
                Picasso.get().load(productList.get(position).getImage()).into(prodImage);
            prodName.setText(productList.get(position).getName());
            prodWeight.setText(productList.get(position).getUnit());
            if (productList.get(position).isHasOffer()) {
                oldPrice.setVisibility(View.VISIBLE);
                prodSavedAmount.setVisibility(View.VISIBLE);
                double nPrice = Utils.calcDiscount(productList.get(position).getPrice(),
                        productList.get(position).getDiscount_percentage());
                newPrice.setText(String.valueOf
                        (nPrice).concat(productList.get(position).getCurrency()));
                oldPrice.setText(String.valueOf
                        (productList.get(position).getPrice()).concat(productList.get(position).getCurrency()));
                prodSavedAmount.setText(mContext.getString(R.string.you_saved_money,
                        productList.get(position).getPrice() - nPrice).concat(productList.get(position).getCurrency()));
                oldPrice.setPaintFlags(oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                oldPrice.setVisibility(View.GONE);
                prodSavedAmount.setVisibility(View.GONE);
                newPrice.setText(String.valueOf(productList.get(position).getPrice()).concat(productList.get(position).getCurrency()));
            }

        }

        private void setNotAvailable() {
            prodAddBtn.setText(R.string.not_available);
            prodAddBtn.setEnabled(false);
            prodIncBtn.setVisibility(View.GONE);
            prodDecreaseInCartBtn.setVisibility(View.GONE);
        }

        private void setAvailable() {
            prodAddBtn.setText(R.string.add);
            prodAddBtn.setEnabled(true);
            prodIncBtn.setVisibility(View.VISIBLE);
            prodDecreaseInCartBtn.setVisibility(View.VISIBLE);
        }


    }
}
