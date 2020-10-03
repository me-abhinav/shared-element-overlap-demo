package com.example.sharedtransitiondemo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class SlideshowAdapter extends FragmentStateAdapter {

    private String mTransitionName;
    private int mTransitionPosition;

    public SlideshowAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @SuppressWarnings("unused")
    public SlideshowAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = new ImageFragment();
        Bundle args = new Bundle();

        if (position == mTransitionPosition) {
            args.putString(ImageFragment.ARG_TRANSITION_NAME, mTransitionName);
        }

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public void setTransitionName(String transitionName, int position) {
        mTransitionName = transitionName;
        mTransitionPosition = position;
    }
}
