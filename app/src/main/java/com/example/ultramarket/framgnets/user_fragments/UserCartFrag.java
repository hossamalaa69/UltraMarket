package com.example.ultramarket.framgnets.user_fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ultramarket.R;
import com.example.ultramarket.adapters.user_adapters.CartProductAdapter;
import com.example.ultramarket.database.Entities.Order;
import com.example.ultramarket.database.Entities.Product;
import com.example.ultramarket.firebase.FirebaseAuthHelper;
import com.example.ultramarket.helpers.AppExecutors;
import com.example.ultramarket.helpers.Utils;
import com.example.ultramarket.ui.userUi.Activities.OrderConfirmActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.ultramarket.ui.userUi.Activities.ProductActivity.DECREASE;
import static com.example.ultramarket.ui.userUi.Activities.ProductActivity.INCREASE;

public class UserCartFrag extends Fragment implements CartProductAdapter.ProductCallBacks {
    public static final CharSequence TITLE = "Cart";
    @BindView(R.id.user_wishlist_frag_login_layout)
    View loginLayout;
    @BindView(R.id.user_cart_layout)
    View cartLayout;
    @BindView(R.id.user_cart_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.user_cart_progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.user_cart_details)
    TextView orderDetails;
    @BindView(R.id.user_cart_details_layout)
    View orderDetailsLayout;
    @BindView(R.id.user_wishlist_frag_empty)
    View emptyCart;
    private CartProductAdapter adapter;
    private String cartDetails;

    @OnClick(R.id.user_cart_order_cart)
    public void orderCartClicked(View view) {
        if (adapter.getTotalCount() == 0) {
            Utils.createToast(getContext(), R.string.you_dont_have_products_to_order, Toast.LENGTH_LONG);
            return;
        }
        Order order = new Order(null, adapter.getCartDetails(), adapter.getTotalPrice(), 0, 0);
        Intent intent = new Intent(getContext(), OrderConfirmActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("order", order);
        intent.putExtra("bundle", bundle);
        intent.putExtra("products", adapter.getCartProductNames());
        intent.putExtra("currency", adapter.getProductsCurrency());
        startActivity(intent);
    }


    private OnClickedListener listener;

    public interface OnClickedListener {
        void onLoginClickListener();
    }

    /*   private UserCartViewModel mViewModel;
     */
    @OnClick(R.id.user_wishlist_login_btn)
    public void onLoginClicked(View view) {
        listener.onLoginClickListener();
    }

    private void hideLoginLayout() {
        loginLayout.setVisibility(View.GONE);
        cartLayout.setVisibility(View.VISIBLE);
        orderDetailsLayout.setVisibility(View.VISIBLE);
    }

    public static UserCartFrag newInstance() {
        return new UserCartFrag();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_cart_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        initLayoutViews();
        initViewModel();
        if (FirebaseAuthHelper.getsInstance().getCurrUser() != null)
            addValueListener();
        updateOrderDetails();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void addValueListener() {
        AppExecutors.getInstance()
                .networkIO()
                .execute(() -> FirebaseDatabase.getInstance().getReference()
                        .child(Product.class.getSimpleName())
                        .addChildEventListener(new ChildEventListener() {
                                                   @Override
                                                   public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                                       adapter.forceUpdateValue(snapshot.getValue(Product.class).getID(),
                                                               snapshot.getValue(Product.class).getCount());
                                                   }

                                                   @Override
                                                   public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                                       adapter.forceUpdateValue(snapshot.getValue(Product.class).getID(),
                                                               snapshot.getValue(Product.class).getCount());
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
                                               }
                        )
                );
    }

    private void initLayoutViews() {
        listener = (OnClickedListener) getContext();
        if (FirebaseAuthHelper.getsInstance().getCurrUser() == null) {
            loginLayout.setVisibility(View.VISIBLE);
            cartLayout.setVisibility(View.GONE);
        } else {
            loginLayout.setVisibility(View.GONE);
            cartLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new CartProductAdapter(getContext(), null, this);
            recyclerView.setAdapter(adapter);
            recyclerView.setHasFixedSize(true);
            recyclerView.setNestedScrollingEnabled(true);
        }
    }

    private void updateOrderDetails() {
        Context context = getContext();
        if (context != null)
            cartDetails = context.getString(R.string.order_details, adapter.getTotalPrice(),
                    adapter.getProductsCurrency(),
                    adapter.getTotalCount());
        orderDetails.setText(cartDetails);
    }

    @Override
    public void onRemoveProductClickedListener(String prod_id) {
        FirebaseDatabase.getInstance().getReference()
                .child("Cart").child(FirebaseAuthHelper.getsInstance().getCurrUser().getUid())
                .child(prod_id).removeValue((error, ref) -> {
            Utils.createToast(getContext(), R.string.product_ran_out, Toast.LENGTH_SHORT);
            adapter.removeProduct(prod_id);
            updateOrderDetails();
        });
    }

    @Override
    public void onListStatesChanges(int status) {
        switch (status) {
            case CartProductAdapter.EMPTY_LIST:
                emptyCart.setVisibility(View.VISIBLE);
                break;
            case CartProductAdapter.NON_EMPTY_LIST:
                emptyCart.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void addProductToFirebaseListener(OnSuccessListener<Void> listener, String prodId, int operation, int count) {
        AppExecutors.getInstance().networkIO().execute(() -> {
            DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference()
                    .child("Cart").child(FirebaseAuthHelper.getsInstance().getCurrUser().getUid());
            cartRef.child(prodId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists() && count > 0) {
                        cartRef.child(prodId).setValue(1).addOnSuccessListener(listener);
                    } else if (snapshot.exists() &&
                            operation == INCREASE &&
                            snapshot.getValue(Integer.class) + 1 <= count) {
                        int num = snapshot.getValue(Integer.class);
                        cartRef.child(prodId).setValue(num + 1).addOnSuccessListener(listener);
                    } else if (snapshot.exists() &&
                            operation == DECREASE &&
                            snapshot.getValue(Integer.class) - 1 > 0) {
                        int num = snapshot.getValue(Integer.class);
                        cartRef.child(prodId).setValue(num > 1 ? num - 1 : 0).addOnSuccessListener(listener);
                    } else {
                        listener.onSuccess(null);
                        Utils.createToast(getContext(), R.string.not_available, Toast.LENGTH_SHORT);
                        return;
                    }
                    Utils.createToast(getContext(), R.string.done, Toast.LENGTH_SHORT);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        });
    }

    private void loadData(String userId) {
        AppExecutors.getInstance().networkIO().execute(() -> FirebaseDatabase.getInstance().getReference()
                .child("Cart").child(userId).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        loadItem(snapshot.getKey(), snapshot.getValue(Integer.class));
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        loadItem(snapshot.getKey(), snapshot.getValue(Integer.class));
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        int y = 0;
                    }
                }));
    }

    private void initViewModel() {
        if (FirebaseAuthHelper.getsInstance().getCurrUser() == null) {
            showLoginLayout();
            return;
        }
        hideLoginLayout();
        loadData(FirebaseAuthHelper.getsInstance().getCurrUser().getUid());
    }

    private void showLoginLayout() {
        loginLayout.setVisibility(View.VISIBLE);
        cartLayout.setVisibility(View.GONE);
        orderDetailsLayout.setVisibility(View.GONE);
    }

    private void loadItem(String key, Integer value) {
        progressBar.setVisibility(View.GONE);
        cartLayout.setVisibility(View.VISIBLE);
        AppExecutors.getInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                FirebaseDatabase.getInstance().getReference()
                        .child(Product.class.getSimpleName()).child(key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Product product = snapshot.getValue(Product.class);
                        if (product != null && !adapter.hasEntry(product.getID())) {
                            if (product.getCount() == 0)
                                onRemoveProductClickedListener(product.getID());
                            else
                                adapter.insertProduct(product, value);
                        } else if (product != null)
                            adapter.updateValue(product.getID(), value);
                        updateOrderDetails();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

}