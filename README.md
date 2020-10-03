# Demo
This is a demo app to show how to solve the issue when our shared view is in a scrolling container and is only partially visible at the time when the shared element transition started. Following is a recorded demo after the fix.

![Demo](demo.gif)

# Steps

Let us say we have two activities viz. `MainActivity` which has a scrolling container with a grid/list of thumbnails, and we have a `SecondActivity` which shows the image in a slideshow in fullscreen.

**Please checkout the full code to completely understand the solution.**

1. Inside your `MainActivity` which hosts the scrolling container, set a click listener on your thumbnail to open `SecondActivity`:
```java
ImageView imageView = findViewById(R.id.image_view);
imageView.setOnClickListener(v -> {
    // Set the transition name. We could also do it in the xml layout but this is to demo
    // that we can choose any name generated dynamically.
    String transitionName = getString(R.string.transition_name);
    imageView.setTransitionName(transitionName);

    // This part is important. We first need to clip this view to only its visible part.
    // We will also clip the corresponding view in the SecondActivity using shared element
    // callbacks.
    Rect localVisibleRect = new Rect();
    imageView.getLocalVisibleRect(localVisibleRect);
    imageView.setClipBounds(localVisibleRect);
    mClippedView = imageView;

    Intent intent = new Intent(MainActivity.this, SecondActivity.class);
    intent.putExtra(SecondActivity.EXTRA_TRANSITION_NAME, transitionName);
    intent.putExtra(SecondActivity.EXTRA_CLIP_RECT, localVisibleRect);
    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                    MainActivity.this,
                    Pair.create(imageView, transitionName));
    startActivity(intent, options.toBundle());
});
```
2. Restore the clip in `onResume()` of your `MainActivity`.
```java
@Override
protected void onResume() {
    super.onResume();

    // This is also important. When we come back to this activity, we need to reset the clip.
    if (mClippedView != null) {
        mClippedView.setClipBounds(null);
    }
}
```
3. Create transition resource in your `res` folder like this:
`app/src/main/res/transition/shared_element_transition.xml`
The contents should be similar to this:
```xml
<?xml version="1.0" encoding="utf-8"?>
<transitionSet
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:duration="375"
    android:interpolator="@android:interpolator/fast_out_slow_in"
    android:transitionOrdering="together">

    <!-- This is needed to clip the invisible part of the view being transitioned. Otherwise we
         will see weird transitions when the image is partially hidden behind appbar or any other
         view. -->
    <changeClipBounds/>

    <changeTransform/>
    <changeBounds/>

</transitionSet>
```
4. Set the transition in your `SecondActivity`.

```java
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
    ActivityCompat.postponeEnterTransition(this);

    // more code ... 
    // See next step below.
}
```
5. Now we need to clip the shared view in the `SecondActivity` as well.
```java
// Setup the clips
String transitionName = getIntent().getStringExtra(EXTRA_TRANSITION_NAME);
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
```