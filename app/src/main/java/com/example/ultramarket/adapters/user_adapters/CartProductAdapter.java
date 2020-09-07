package com.example.ultramarket.adapters.user_adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ultramarket.R;
import com.example.ultramarket.database.Entities.Product;
import com.example.ultramarket.firebase.FirebaseAuthHelper;
import com.example.ultramarket.helpers.AppExecutors;
import com.example.ultramarket.helpers.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.ultramarket.ui.userUi.Activities.ProductActivity.DECREASE;
import static com.example.ultramarket.ui.userUi.Activities.ProductActivity.INCREASE;

public class CartProductAdapter extends RecyclerView.Adapter<CartProductAdapter.Holder> {
    public static final int EMPTY_LIST = 1;
    public static final int NON_EMPTY_LIST = 2;


    public Context mContext;
    public Map<Product, Integer> productsMap;

    public CartProductAdapter(Context mContext, Map<Product, Integer> productsMap, ProductCallBacks listener) {
        this.mContext = mContext;
        interfaceInstance = listener;
        this.productsMap = productsMap;
    }

    public void insertProduct(Product product, int value) {

        if (productsMap == null) {
            productsMap = new HashMap<>();
        }
        interfaceInstance.onListStatesChanges(NON_EMPTY_LIST);
        productsMap.put(product, value);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_cart_product_list_item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (productsMap == null) return 0;
        return productsMap.size();
    }

    private ProductCallBacks interfaceInstance;

    public void clear() {
        if (productsMap != null)
            productsMap.clear();
        productsMap = null; 
        notifyDataSetChanged();
    }

    public boolean hasEntry(String id) {
        if (productsMap == null) return false;
        for (Map.Entry<Product, Integer> entry : productsMap.entrySet()) {
            if (entry.getKey().getID().matches(id))
                return true;

        }
        return false;
    }

    public void updateValue(String key, int value) {
        if (productsMap == null) return;
        for (Map.Entry<Product, Integer> entry : productsMap.entrySet()) {
            if (entry.getKey().getID().matches(key)) {
                productsMap.replace(entry.getKey(), value);
                notifyDataSetChanged();
            }
        }
    }

    public void forceUpdateValue(String key, int value) {
        if (productsMap == null) return;
        int i = 0;
        String idx = ",";
        for (Map.Entry<Product, Integer> entry : productsMap.entrySet()) {
            if (entry.getKey().getID().matches(key) && entry.getValue() > value) {
                if (value == 0) {
                    interfaceInstance.onRemoveProductClickedListener(key);
                    Toast.makeText(mContext, R.string.products_count_is_decreasing, Toast.LENGTH_LONG).show();
                    return;
                }
                updateValueOnFirebase(entry.getKey().getID(), value);
                productsMap.replace(entry.getKey(), value);
                i++;
                idx = idx.concat(i + ",");
            }
        }
        if (i > 0) {
            notifyDataSetChanged();
            Toast.makeText(mContext, R.string.products_count_is_decreasing, Toast.LENGTH_LONG).show();
            Toast.makeText(mContext, "products had less value than wanted" + idx, Toast.LENGTH_SHORT).show();
        }
    }

    private void updateValueOnFirebase(String prodID, int value) {
        AppExecutors.getInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                FirebaseDatabase.getInstance().getReference()
                        .child("Cart").child(FirebaseAuthHelper.getsInstance().getCurrUser().getUid())
                        .child(prodID)
                        .setValue(value).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });
            }
        });
    }

    public void removeProduct(String prod_id) {
        for (Map.Entry<Product, Integer> entry : productsMap.entrySet()) {
            if (entry.getKey().getID().matches(prod_id)) {
                productsMap.remove(entry.getKey());
                notifyDataSetChanged();
                if(productsMap.size()==0)
                    interfaceInstance.onListStatesChanges(EMPTY_LIST);
                return;
            }
        }

    }

    public double getTotalPrice() {
        if(productsMap==null) return 0;
        double price = 0;
        for (Map.Entry<Product, Integer> entry : productsMap.entrySet()) {
            if (entry.getKey().isHasOffer()) {
                price += Utils.calcDiscount(entry.getKey().getPrice(), entry.getKey().getDiscount_percentage()) * entry.getValue();
            } else
                price += entry.getKey().getPrice() * entry.getValue();
        }
        return price;
    }

    public int getTotalCount() {
        if(productsMap==null) return 0;
        int count = 0;
        for (Map.Entry<Product, Integer> entry : productsMap.entrySet()) {
            count += entry.getValue();
        }
        return count;
    }

    public String getProductsCurrency() {
        if(productsMap==null) return "";
        for (Map.Entry<Product, Integer> entry :
                productsMap.entrySet()) {
            return entry.getKey().getCurrency();

        }
        return "$"; // default currency
    }

    ArrayList<String> productsNames;

    public Map<String, Integer> getCartDetails() {
        if(productsMap==null) return null;
        Map<String, Integer> products = new HashMap<>();
        productsNames = new ArrayList<>();
        for (Map.Entry<Product, Integer> entry :
                productsMap.entrySet()) {
            products.put(entry.getKey().getID(), entry.getValue());
            productsNames.add(entry.getKey().getName());
        }
        return products;
    }

    public ArrayList<String> getCartProductNames() {
        return productsNames;
    }

    public interface ProductCallBacks {
        void onRemoveProductClickedListener(String prod_id);
        void onListStatesChanges(int status);
        void addProductToFirebaseListener(OnSuccessListener<Void> listener, String prodId, int operation, int count);
    }

    public class Holder extends RecyclerView.ViewHolder {
        @BindView(R.id.user_offers_item_title)
        TextView mName;
        @BindView(R.id.user_offers_frag_item_image)
        ImageView mImage;
        @BindView(R.id.user_offers_item_saved_amount)
        TextView mSavedMoney;
        @BindView(R.id.user_offers_item__weight_unit)
        TextView mWeightUnit;
        @BindView(R.id.user_offers_item_price_layout)
        View priceLayout;
        @BindView(R.id.user_offers_add_to_wishlist)
        Button addToCart;
        @BindView(R.id.user_offers_decrease_from_wishlist)
        ImageButton decreaseToCartBtn;
        @BindView(R.id.user_offers_increase_to_wishlist)
        ImageButton increaseToCartBtn;

        public void enableBtns() {
            mProgressBar.setVisibility(View.GONE);
            addToCart.setVisibility(View.VISIBLE);
            increaseToCartBtn.setEnabled(true);
            decreaseToCartBtn.setEnabled(true);
            addToCart.setEnabled(true);
        }

        public void disableBtns() {
            mProgressBar.setVisibility(View.VISIBLE);
            addToCart.setVisibility(View.INVISIBLE);
            increaseToCartBtn.setEnabled(false);
            decreaseToCartBtn.setEnabled(false);
            addToCart.setEnabled(false);
        }

        @OnClick(R.id.user_offers_add_to_wishlist)
        public void addToCart(View view) {
            if (FirebaseAuthHelper.getsInstance().getCurrUser() != null) {
                addProductToFirebase(getProductAt(getAdapterPosition()).getID(), INCREASE);
            } else {
                Utils.createToast(mContext, R.string.you_must_signin_first, Toast.LENGTH_SHORT);
            }
        }

        @OnClick(R.id.user_offers_increase_to_wishlist)
        public void increaseToCart(View view) {
            addToCart(null);
        }


        @OnClick(R.id.user_offers_decrease_from_wishlist)
        public void decreaseToCart(View view) {
            addProductToFirebase(getProductAt(getAdapterPosition()).getID(), DECREASE);
        }

        private void addProductToFirebase(String prodId, int operation) {
            if (FirebaseAuthHelper.getsInstance().getCurrUser() != null) {
                disableBtns();
                OnSuccessListener<Void> listener = new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //update ui
                        enableBtns();
                    }
                };
                interfaceInstance.addProductToFirebaseListener(listener, prodId, operation, getProductAt(getAdapterPosition()).getCount());
            } else {
                Utils.createToast(mContext, R.string.you_must_signin_first, Toast.LENGTH_SHORT);
            }
        }


        public Product getProductAt(int pos) {
            int i = 0;
            for (Map.Entry<Product, Integer> entry : productsMap.entrySet()) {
                if (i == pos) {
                    return entry.getKey();
                }
                i++;
            }
            return null;
        }

        public int getValueAt(int pos) {
            int i = 0;
            for (Map.Entry<Product, Integer> entry : productsMap.entrySet()) {
                if (i == pos) {
                    return entry.getValue();
                }
                i++;
            }
            return -1;
        }

        @OnClick(R.id.user_cart_remove_product)
        public void onRemoveProductFromCartClicked(View view) {
            interfaceInstance.onRemoveProductClickedListener(getProductAt(getAdapterPosition()).getID());
        }

        @BindView(R.id.user_product_item_new_price)
        TextView mNewPrice;
        @BindView(R.id.user_product_item_old_price)
        TextView mOldPrice;
        @BindView(R.id.user_cart_count)
        public TextView mCount;
        @BindView(R.id.user_offers_progress)
        ProgressBar mProgressBar;

        public Holder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            priceLayout.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
            addToCart.setVisibility(View.VISIBLE);

        }

        public void bind(int position) {
            Product product = getProductAt(position);
            if (product.getCount() > 0) {
                setAvailable();
            } else {
                setNotAvailable();
            }
            int value = getValueAt(position);
            mCount.setText(mContext.getString(R.string.count, value));
            addToCart.setText(String.valueOf(value));
            mName.setText(product.getName());
            if (product.isHasOffer()) {
                mOldPrice.setVisibility(View.VISIBLE);
                mSavedMoney.setVisibility(View.VISIBLE);
                mOldPrice.setText(String.valueOf(product.getPrice()).concat(product.getCurrency()));
                double newPrice = Utils.calcDiscount(product.getPrice(), product.getDiscount_percentage());
                mNewPrice.setText(String.valueOf(newPrice).concat(product.getCurrency()));
                mSavedMoney.setText(mContext.getString(R.string.you_saved_money, (product.getPrice() - newPrice))
                        .concat(product.getCurrency()));
                mOldPrice.setPaintFlags(mOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {

                mOldPrice.setVisibility(View.GONE);
                mSavedMoney.setVisibility(View.GONE);
                mNewPrice.setText(String.valueOf(product.getPrice()).concat(product.getCurrency()));

            }
            Picasso.get().load(product.getImage()).into(mImage);
            mWeightUnit.setText(product.getUnit());
            mName.setText(product.getName());

        }

        private void setAvailable() {
            decreaseToCartBtn.setVisibility(View.VISIBLE);
            increaseToCartBtn.setVisibility(View.VISIBLE);
            addToCart.setText(R.string.add);
            addToCart.setEnabled(true);
        }

        private void setNotAvailable() {
            decreaseToCartBtn.setVisibility(View.GONE);
            increaseToCartBtn.setVisibility(View.GONE);
            addToCart.setText(R.string.not_available);
            addToCart.setEnabled(false);
        }
    }
}
