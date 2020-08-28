package com.example.ultramarket.adapters.user_adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.ultramarket.framgnets.user_fragments.UserCartFrag;
import com.example.ultramarket.framgnets.user_fragments.UserCatFrag;
import com.example.ultramarket.framgnets.user_fragments.UserHomeFrag;
import com.example.ultramarket.framgnets.user_fragments.UserOffersFrag;
import com.example.ultramarket.framgnets.user_fragments.UserOrdersFrag;

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
                return UserOffersFrag.newInstance();
            case 3:
                return UserCartFrag.newInstance();
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
                return UserOffersFrag.TITLE;
            case 3:
                return UserCartFrag.TITLE;
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
