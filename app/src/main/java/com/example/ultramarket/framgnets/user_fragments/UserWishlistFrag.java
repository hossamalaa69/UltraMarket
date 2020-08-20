package com.example.ultramarket.framgnets.user_fragments;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ultramarket.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserWishlistFrag extends Fragment {
    public static final CharSequence TITLE = "Wish list";
    @BindView(R.id.user_wishlist_frag_login_layout)
    View loginLayout;


    private OnClickedListener listener;
    public interface OnClickedListener{
        void onLoginClickListener();
    }
    private UserWishlistViewModel mViewModel;
    @OnClick(R.id.user_wishlist_login_btn)
    public void onLoginClicked(View view){
        listener.onLoginClickListener();
        hideLoginLayout();

    }

    private void hideLoginLayout() {
        //TODO hide login layout and show the other layout
        loginLayout.setVisibility(View.GONE);
    }

    public static UserWishlistFrag newInstance() {
        return new UserWishlistFrag();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_wishlist_fragment, container, false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listener = (OnClickedListener) getContext();
        mViewModel = ViewModelProviders.of(this).get(UserWishlistViewModel.class);
        // TODO: Use the ViewModel
    }

}