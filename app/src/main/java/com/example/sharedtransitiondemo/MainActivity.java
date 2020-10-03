package com.example.sharedtransitiondemo;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.facebook.drawee.backends.pipeline.Fresco;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private final ClipState mClipState = new ClipState();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fresco.initialize(this);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageView image = findViewById(R.id.image_view);
        image.setOnClickListener(v -> {
            // Set the transition name. We could also do it in the xml layout but this is to demo
            // that we can choose any name generated dynamically.
            String transitionName = getString(R.string.transition_name);
            image.setTransitionName(transitionName);

            // This part is important. We first need to clip this view to only its visible part.
            // We will also clip the corresponding view in the SecondActivity using shared element
            // callbacks.
            mClipState.save(image);
            Rect localVisibleRect = new Rect();
            image.getLocalVisibleRect(localVisibleRect);
            image.setClipBounds(localVisibleRect);

            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
            intent.putExtra(SecondActivity.EXTRA_TRANSITION_NAME, transitionName);
            intent.putExtra(SecondActivity.EXTRA_CLIP_RECT, localVisibleRect);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                            MainActivity.this,
                            Pair.create(image, transitionName));
            startActivity(intent, options.toBundle());
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // This is also important. When we come back to this activity, we need to reset the clip.
        mClipState.restore();
    }

    /**
     * Temporarily holds clipped views to be restored later.
     */
    private static final class ClipState {
        // Stores the current clip bounds state of views.
        private final Set<Pair<WeakReference<View>, Rect>> mState = new HashSet<>();

        void save(View... views) {
            for (View view: views) {
                mState.add(Pair.create(new WeakReference<>(view), view.getClipBounds()));
            }
        }

        void restore() {
            for (Pair<WeakReference<View>, Rect> pair: mState) {
                View view = pair.first.get();
                if (view != null) {
                    view.setClipBounds(pair.second);
                }
            }
        }
    }
}