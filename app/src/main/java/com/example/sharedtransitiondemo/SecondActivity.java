package com.example.sharedtransitiondemo;

import android.graphics.Rect;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.SharedElementCallback;
import androidx.viewpager2.widget.ViewPager2;

import java.util.List;
import java.util.Objects;

public class SecondActivity extends AppCompatActivity {

    public static final String EXTRA_TRANSITION_NAME = "extra_transition_name";
    public static final String EXTRA_CLIP_RECT = "extra_clip_rect";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        // Setup transition
        Transition transition =
                TransitionInflater.from(this)
                        .inflateTransition(R.transition.shared_element_transition);
        getWindow().setSharedElementEnterTransition(transition);

        // Postpone the transition. We will start it when the slideshow is ready.
        // Check ImageFragment to know when we start it.
        ActivityCompat.postponeEnterTransition(this);

        // Setup our view pager.
        String transitionName = getIntent().getStringExtra(EXTRA_TRANSITION_NAME);
        SlideshowAdapter adapter = new SlideshowAdapter(this);
        ViewPager2 viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(adapter);
        int currentItem = 1;
        adapter.setTransitionName(transitionName, currentItem);
        viewPager.setCurrentItem(currentItem, false);

        // Setup the clips
        Rect clipRect = getIntent().getParcelableExtra(EXTRA_CLIP_RECT);
        setEnterSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onSharedElementStart(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
                for (int i = 0; i < sharedElementNames.size(); i++) {
                    if (Objects.equals(transitionName, sharedElementNames.get(i))) {
                        View view = sharedElements.get(i);
                        view.setClipBounds(clipRect);
                    }
                }
                super.onSharedElementStart(sharedElementNames, sharedElements, sharedElementSnapshots);
            }

            @Override
            public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
                for (int i = 0; i < sharedElementNames.size(); i++) {
                    if (Objects.equals(transitionName, sharedElementNames.get(i))) {
                        View view = sharedElements.get(i);
                        view.setClipBounds(null);
                    }
                }
                super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots);
            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // noinspection SwitchStatementWithTooFewBranches
        switch (item.getItemId()) {
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}