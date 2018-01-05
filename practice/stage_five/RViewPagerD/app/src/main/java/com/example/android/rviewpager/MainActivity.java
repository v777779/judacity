package com.example.android.rviewpager;

import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.AutoTransition;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.GridLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements ICallback {
    public static final String BUNDLE_IMAGE_RESOURCE = "bundle_image_resource";
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
    View mView;

    private Unbinder mUnbinder;
    private RecyclerAdapter mRecyclerAdapter;
    private GridLayoutManager mGridLayout;
    private int mSpan;
    private int mHeight;
    private List<String> mList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        Transition move = TransitionInflater.from(this).inflateTransition(R.transition.move);
//// set an enter transition
//        getWindow().setEnterTransition(new Explode());
//        getWindow().setSharedElementEnterTransition(move);
//// set an exit transition
        getWindow().setExitTransition(new Explode());
        getWindow().setSharedElementExitTransition(move);
//// set an reenter transition
//        getWindow().setReenterTransition(new Explode());
//        getWindow().setSharedElementReenterTransition(move);


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

        setupRecycler();
        mRecyclerAdapter.setCursor(mList);


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
        mHeight = 150;

        mRecyclerAdapter = new RecyclerAdapter(this, mHeight);
        mRecyclerAdapter.setHasStableIds(true);
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
//        intent.putExtra(BUNDLE_IMAGE_RESOURCE, mList.get(position));
//        startActivity(intent, bundle);


        Intent intent = new Intent(this, DetailActivity.class);

        intent.putExtra(BUNDLE_IMAGE_RESOURCE, mList.get(position));
        View viewFab = (View) findViewById(R.id.fab);
        View viewText = (View) findViewById(R.id.main_text);

        Pair<View, String> p1 = Pair.create(view, getString(R.string.transition_image));
        Pair<View, String> p2 = Pair.create(viewFab, getString(R.string.transition_fab));
        Pair<View, String> p3 = Pair.create(viewText, getString(R.string.transition_text));


        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this, p1, p2, p3);

        intent.putExtra("BUNDLE_OPTIONS",optionsCompat.toBundle());

        startActivity(intent, optionsCompat.toBundle());


    }
}
