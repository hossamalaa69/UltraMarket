package com.example.ultramarket.framgnets.user_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ultramarket.R;
import com.example.ultramarket.adapters.user_adapters.CartProductAdapter;
import com.example.ultramarket.database.Entities.Cart;
import com.example.ultramarket.database.Entities.Product;
import com.example.ultramarket.firebase.FirebaseAuthHelper;
import com.example.ultramarket.helpers.AppExecutors;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.ultramarket.ui.userUi.Activities.ProductActivity.DECREASE;
import static com.example.ultramarket.ui.userUi.Activities.ProductActivity.INCREASE;

public class UserCartFrag extends Fragment {
    public static final CharSequence TITLE = "Cart";
    @BindView(R.id.user_wishlist_frag_login_layout)
    View loginLayout;
    @BindView(R.id.user_cart_layout)
    View cartLayout;
    @BindView(R.id.user_cart_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.user_cart_progress_bar)
    ProgressBar progressBar;
    private CartProductAdapter adapter;

    @OnClick(R.id.user_cart_order_cart)
    public void orderCartClicked(View view) {

    }


    private OnClickedListener listener;

    public interface OnClickedListener {
        void onLoginClickListener();
    }

    private UserCartViewModel mViewModel;

    @OnClick(R.id.user_wishlist_login_btn)
    public void onLoginClicked(View view) {
        listener.onLoginClickListener();
        hideLoginLayout();

    }

    private void hideLoginLayout() {
        //TODO hide login layout and show the other layout
        loginLayout.setVisibility(View.GONE);
        cartLayout.setVisibility(View.VISIBLE);
    }

    public static UserCartFrag newInstance() {
        return new UserCartFrag();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_cart_fragment, container, false);
        ButterKnife.bind(this, view);

        if (FirebaseAuthHelper.getsInstance().getCurrUser() == null) {
            loginLayout.setVisibility(View.VISIBLE);
            cartLayout.setVisibility(View.GONE);
        } else {
            loginLayout.setVisibility(View.GONE);
            cartLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new CartProductAdapter(getContext(), null, new CartProductAdapter.ProductCallBacks() {
                @Override
                public void onRemoveProductClickedListener(String prod_id) {
                    //TODO remove from firebase
                    Toast.makeText(getContext(), "delete product", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void addProductToFirebaseListener(OnSuccessListener<Void> listener, String prodId, int operation) {
                    AppExecutors.getInstance().networkIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference()
                                    .child(Cart.class.getSimpleName()).child(FirebaseAuthHelper.getsInstance().getCurrUser().getUid());
                            cartRef.child(prodId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    if (!snapshot.exists()) {
                                        cartRef.child(prodId).setValue(1).addOnSuccessListener(listener);
                                    } else if (operation == INCREASE) {
                                        int num = snapshot.getValue(Integer.class);
                                        cartRef.child(prodId).setValue(num + 1).addOnSuccessListener(listener);
                                    } else if (operation == DECREASE) {
                                        int num = snapshot.getValue(Integer.class);
                                        cartRef.child(prodId).setValue(num > 1 ? num - 1 : 0).addOnSuccessListener(listener);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    });
                }
            });
            recyclerView.setAdapter(adapter);
            recyclerView.setHasFixedSize(true);
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listener = (OnClickedListener) getContext();
        if (FirebaseAuthHelper.getsInstance().getCurrUser() != null) {
            UserCartViewModelFactory factory = new UserCartViewModelFactory(
                    FirebaseAuthHelper.getsInstance().getCurrUser().getUid());
            UserCartViewModel viewModel = ViewModelProviders.of(this, factory).get(UserCartViewModel.class);
            viewModel.getProducts().observe(getViewLifecycleOwner(), new Observer<Map.Entry<String, Integer>>() {
                @Override
                public void onChanged(Map.Entry<String, Integer> stringIntegerEntry) {
                    loadItem(stringIntegerEntry.getKey(), stringIntegerEntry.getValue());
                }
            });

        }
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
                            adapter.insertProduct(product, value);
                        } else if (product != null)
                            adapter.updateValue(product.getID(), value);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });
    }

}