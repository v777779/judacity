package com.example.android.rviewpager;

import android.app.SharedElementCallback;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.GridLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements ICallback {
    public static final String BUNDLE_STARTING_IMAGE_RESOURCE = "bundle_starting_image_resource";
    public static final String BUNDLE_CURRENT_IMAGE_RESOURCE = "bundle_current_image_resource";
    public static final String BUNDLE_FRAGMENT_ID = "bundle_fragment_id";

    private static boolean mIsTimber;

    @Nullable
    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;

    @Nullable
    @BindView(R.id.fab)
    FloatingActionButton mFab;

    @Nullable
    @BindView(R.id.main_text)
    TextView mText;

    private Unbinder mUnbinder;
    private RecyclerAdapter mRecyclerAdapter;
    private GridLayoutManager mGridLayout;
    private int mSpan;
    private int mHeight;
    private List<String> mList;

    private Bundle mTmpReenterState;

    private final SharedElementCallback mCallback = new SharedElementCallback() {
        @Override
        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
            if (mTmpReenterState != null) {
                int startingPosition = mTmpReenterState.getInt(BUNDLE_STARTING_IMAGE_RESOURCE);
                int currentPosition = mTmpReenterState.getInt(BUNDLE_CURRENT_IMAGE_RESOURCE);
                if (startingPosition != currentPosition) {
                    String newTransitionName = mList.get(currentPosition);
//                    View newSharedElement = mRecyclerView.findViewWithTag(newTransitionName);
                    RecyclerAdapter.ViewHolder holder = (RecyclerAdapter.ViewHolder) mRecyclerView.findViewHolderForAdapterPosition(currentPosition);
                    View newSharedElement = holder.mItemImage;

                    if (newSharedElement != null) {
                        names.clear();
                        names.add(newTransitionName);
                        sharedElements.clear();
                        sharedElements.put(newTransitionName, newSharedElement);
// fab
                        sharedElements.put(mFab.getTransitionName(),mFab);
// text
                        sharedElements.put(mText.getTransitionName(),mText);
                    }
                }

                mTmpReenterState = null;
            } else {
                // If mTmpReenterState is null, then the activity is exiting.
                View navigationBar = findViewById(android.R.id.navigationBarBackground);
                View statusBar = findViewById(android.R.id.statusBarBackground);
                if (navigationBar != null) {
                    names.add(navigationBar.getTransitionName());
                    sharedElements.put(navigationBar.getTransitionName(), navigationBar);
                }
                if (statusBar != null) {
                    names.add(statusBar.getTransitionName());
                    sharedElements.put(statusBar.getTransitionName(), statusBar);
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

//        Transition move = TransitionInflater.from(this).inflateTransition(R.transition.move);
//// set an enter transition
//        getWindow().setEnterTransition(new Explode());
//        getWindow().setSharedElementEnterTransition(move);
//// set an exit transition
        getWindow().setExitTransition(new Explode());
//        getWindow().setSharedElementExitTransition(move);
//// set an reenter transition
        getWindow().setReenterTransition(new Slide(Gravity.TOP));
//        getWindow().setSharedElementReenterTransition(move);

//        setExitSharedElementCallback(mCallback);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mUnbinder = ButterKnife.bind(this);
        mList = listImagesLoader();

        // timber
        if (!mIsTimber) {
            Timber.plant(new Timber.DebugTree());
            mIsTimber = true;
        }



        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, DetailActivity.class));
            }
        });
        mFab.setTransitionName(getString(R.string.transition_fab));
        mText.setTransitionName(getString(R.string.transition_text));

        setupRecycler();
        mRecyclerAdapter.setCursor(mList);


    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        mTmpReenterState = new Bundle(data.getExtras());
        int startingPosition = mTmpReenterState.getInt(BUNDLE_STARTING_IMAGE_RESOURCE);
        int currentPosition = mTmpReenterState.getInt(BUNDLE_CURRENT_IMAGE_RESOURCE);
        if (startingPosition != currentPosition) {
            mRecyclerView.scrollToPosition(currentPosition);
        }
        postponeEnterTransition();
        mRecyclerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mRecyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                mRecyclerView.requestLayout();
                startPostponedEnterTransition();
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    private void setupRecycler() {
        mSpan = 2;
        mHeight = 350;

        mRecyclerAdapter = new RecyclerAdapter(this, mHeight);
        mRecyclerAdapter.setHasStableIds(true);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mRecyclerAdapter);
        GridLayoutManager layoutManager = new GridLayoutManager(
                this,
                mSpan,
                GridLayout.VERTICAL,
                false);
        mRecyclerView.setLayoutManager(layoutManager);
    }

    public static List<String> listImagesLoader() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(String.format("image_%03d.jpg", i + 1));
        }

        return list;
    }

    @Override
    public void onCallback(Uri uri, View view) {

    }

    @Override
    public void onCallback(int mode) {

    }

    @Override
    public void onCallback(View view, int position) {
//        Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this
//                , view, getString(R.string.transition_name)).toBundle();
//        Intent intent = new Intent(this, DetailActivity.class);
//        intent.putExtra(BUNDLE_STARTING_IMAGE_RESOURCE, mList.get(position));
//        startActivity(intent, bundle);

        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(BUNDLE_STARTING_IMAGE_RESOURCE, mList.get(position)); // start position Id
        View mImage = view;

        Pair<View, String> p1 = Pair.create(mImage, mImage.getTransitionName());  // unique name
        Pair<View, String> p2 = Pair.create((View)mFab, mFab.getTransitionName());
        Pair<View, String> p3 = Pair.create((View)mText, mText.getTransitionName());

        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this, p1, p2, p3);
// test!!!
       optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,mImage, "transition_demo");
        startActivity(intent, optionsCompat.toBundle());

    }

    @Override
    public void onCallback(FragmentDetail fragment) {
    }
}
