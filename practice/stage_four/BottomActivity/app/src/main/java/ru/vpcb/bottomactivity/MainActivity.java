package ru.vpcb.bottomactivity;


import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Guideline;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ru.vpcb.jokelibrary.JokeLibrary;


public class MainActivity extends AppCompatActivity implements ICallback {
    public static final String BUNDLE_JOKE_STRING = "bundle_joke_string";
    public static final String BUNDLE_POSITION = "bundle_position";
    public static final String BUNDLE_JOKE_LIST = "bundle_joke_list";

    // recycler
    public static final int HIGH_SCALE_WIDTH = 240;     // dpi
    public static final int HIGH_SCALE_HEIGHT = 180;     // dpi
    public static final double SCALE_RATIO = 1.8;

    public static final int MIN_SPAN = 1;
    public static final int MIN_HEIGHT = 100;


    private AdView mAdView;
    private FloatingActionButton mFab;
    private Button mButton;
    private AsyncJoke mAsyncJoke;

    private RecyclerView mRecyclerView;
    private JokeAdapter mRecyclerAdapter;
    private List<Integer> mList;
    private boolean mIsEndless;
    private int mImageId;
    private int mPosition;


    private class AsyncJoke extends AsyncTask<Void, Void, String> {
        public AsyncJoke() {
        }

        @Override
        protected String doInBackground(Void... voids) {
            Log.d("AsyncTask", "thread = " + Thread.currentThread().getName());
            return new JokeLibrary().getJoke();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            onComplete(s);
            Log.d("AsyncPost", "thread = " + Thread.currentThread().getName());
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.show();

        MobileAds.initialize(this, getString(R.string.banner_ad_app_id));

        mAdView = findViewById(R.id.adview_banner);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);

//        AdSize.LEADERBOARD

        Log.d("MainActivity", "thread = " + Thread.currentThread().getName());
        mAsyncJoke = new AsyncJoke();
        mFab = findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncJoke().execute();

            }
        });
        mButton = findViewById(R.id.joke_button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AsyncJoke().execute();
            }
        });


        if (savedInstanceState != null) {
            mList = savedInstanceState.getIntegerArrayList(BUNDLE_JOKE_LIST);
            mPosition = savedInstanceState.getInt(BUNDLE_POSITION);
        } else {
            mList = FragmentJoke.getJokeList();
            mPosition = 0;
        }
        if (getResources().getBoolean(R.bool.is_wide)) {
// sharedPreference
            mIsEndless = true;
// recycler
            SpanData sp = getDisplayMetrics();
            mRecyclerView = findViewById(R.id.joke_recycler);  // appcompat design
            int span = sp.spanY;
            int orientation = GridLayout.HORIZONTAL;
            if (getResources().getBoolean(R.bool.is_vert)) {
                span = sp.spanX;
                orientation = GridLayout.VERTICAL;
            }
            final GridLayoutManager layoutManager = new GridLayoutManager(this, span, orientation, false);
            mRecyclerView.setLayoutManager(layoutManager);                          // connect to LayoutManager
            mRecyclerView.setHasFixedSize(true);                                    // item size fixed
            mRecyclerAdapter = new JokeAdapter(this, mList, sp);
            mRecyclerView.setAdapter(mRecyclerAdapter);
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (!mIsEndless) return;
                    int delta = getResources().getBoolean(R.bool.is_vert) ? dy : dx;

                    if (delta > 0) {
                        int visibleItemCount = layoutManager.getChildCount();
                        int totalItemCount = layoutManager.getItemCount();
                        int pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();
                        mPosition = pastVisiblesItems;

                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            mList.addAll(FragmentJoke.getJokeList());
                            mRecyclerAdapter.notifyDataSetChanged();
                        }
                    }
                }
            });

            mRecyclerView.scrollToPosition(mPosition);

        }
// fragment

        onComplete(getString(R.string.welcome_message));

    }

// async callback;

    public void onComplete(String s) {

        if (s == null || s.isEmpty()) return;
        Fragment fragment = FragmentJoke.newInstance(s, mImageId);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();

        mImageId = -1; // clear mPosition

    }

    @Override
    public void onComplete(int value) {
        mImageId = mList.get(value);
        new AsyncJoke().execute();
    }

    private double getPercent(int guideId) {
        return ((ConstraintLayout.LayoutParams) findViewById(guideId).getLayoutParams()).guidePercent;
    }

    private SpanData getDisplayMetrics() {
        DisplayMetrics dp = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dp);
        Resources res = getResources();


        double height_ratio = getPercent(R.id.guide_h2) - getPercent(R.id.guide_h1);  // tightened to layout
        double width_ratio = getPercent(R.id.guide_v2) - getPercent(R.id.guide_v1);   // tightened to layout

        double width = dp.widthPixels / dp.density * width_ratio;
        double height = dp.heightPixels / dp.density * height_ratio;  // real height


        int spanInWidth = (int) Math.round(width / HIGH_SCALE_WIDTH);
        int spanHeight = (int) (width * dp.density / spanInWidth / SCALE_RATIO);  // vertical only
        int spanInHeight = (int) Math.round(height / HIGH_SCALE_HEIGHT);
        int spanWidth = (int) (height * dp.density / spanInHeight * SCALE_RATIO);  // horizontal only


        if (spanInWidth < MIN_SPAN) spanInWidth = MIN_SPAN;
//        if (spanInWidth > MAX_SPAN) spanInWidth = MAX_SPAN;

        if (spanInHeight < MIN_SPAN) spanInHeight = MIN_SPAN;
//        if (spanInHeight > MAX_SPAN) spanInHeight = MAX_SPAN;

        if (spanHeight < MIN_HEIGHT) spanHeight = MIN_HEIGHT;

        int minWidth = (int) (MIN_HEIGHT * SCALE_RATIO);  // horizontal
        if (spanWidth < minWidth) spanWidth = minWidth;

// vertical
//        mSpan = spanInWidth;
//        mSpanHeight = spanHeight;
// horizontal
//        mSpan = spanInHeight;
//        mSpanWidth = spanWidth;

        return new SpanData(spanInWidth, spanInHeight, spanWidth, spanHeight);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntegerArrayList(BUNDLE_JOKE_LIST, new ArrayList<Integer>(mList));
        outState.putInt(BUNDLE_POSITION, mPosition);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
