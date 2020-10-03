package com.example.sharedtransitiondemo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

public class ImageFragment extends Fragment {

    public static final String ARG_TRANSITION_NAME = "arg_transition_name";

    public ImageFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_image, container, false);

        // noinspection ConstantConditions
        String transitionName = getArguments().getString(ARG_TRANSITION_NAME);
        if (transitionName != null) {
            ImageView imageView = rootView.findViewById(R.id.drawee_view_2);
            imageView.setTransitionName(transitionName);
            imageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    ActivityCompat.startPostponedEnterTransition(requireActivity());
                    imageView.getViewTreeObserver().removeOnPreDrawListener(this);
                    return true;
                }
            });
        }

        return rootView;
    }
}
