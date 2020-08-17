package com.example.ultramarket.ui.userUi.Adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.ultramarket.ui.userUi.Fragments.UserCatFrag;
import com.example.ultramarket.ui.userUi.Fragments.UserHomeFrag;
import com.example.ultramarket.ui.userUi.Fragments.UserOffersFrag;
import com.example.ultramarket.ui.userUi.Fragments.UserOrdersFrag;
import com.example.ultramarket.ui.userUi.Fragments.UserWishlistFrag;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    public static final int FRAGS_COUNT = 5;

    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 1:
                return UserCatFrag.newInstance();
            case 2:
                return UserWishlistFrag.newInstance();
            case 3:
                return UserOffersFrag.newInstance();
            case 4:
                return UserOrdersFrag.newInstance();
            default:
                return UserHomeFrag.newInstance();
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return UserHomeFrag.TITLE;
            case 1:
                return UserCatFrag.TITLE;
            case 2:
                return UserWishlistFrag.TITLE;
            case 3:
                return UserOffersFrag.TITLE;
            case 4:
                return UserOrdersFrag.TITLE;
            default:
                return super.getPageTitle(position);
        }

    }

    @Override
    public int getCount() {
        return FRAGS_COUNT;
    }
}
