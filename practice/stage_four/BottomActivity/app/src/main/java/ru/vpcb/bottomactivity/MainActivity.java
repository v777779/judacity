package ru.vpcb.bottomactivity;


import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import java.util.List;
import java.util.Random;

import ru.vpcb.jokelibrary.JokeLibrary;


public class MainActivity extends AppCompatActivity implements ICallback {
    public static final String BUNDLE_JOKE_STRING = "bundle_joke_string";
    public static final String START_CLICK_STRING_RESOURCE = "CLICK the BUTTON or SELECT the IMAGE";
    // recycler
    public static final int HIGH_SCALE_WIDTH = 240;     // dpi
    public static final int HIGH_SCALE_HEIGHT = 140;     // dpi
    public static final double SCALE_RATIO = 1.6;
    public static final int MAX_SPAN = 6;
    public static final int MIN_SPAN = 1;
    public static final int MIN_HEIGHT = 100;
    public static final double RECYCLER_HEIGHT_RATIO = 0.85 - 0.60;  // guides


    private AdView mAdView;
    private FloatingActionButton mFab;
    private Button mButton;
    private AsyncJoke mAsyncJoke;

    private RecyclerView mRecyclerView;
    private JokeAdapter mRecyclerAdapter;
    private List<Integer> mList;
    private boolean mIsEndless;
    private int mImageId;


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

// recycler
        mList = FragmentJoke.getJokeList();
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
        mRecyclerAdapter = new JokeAdapter(this,mList, sp);
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (!mIsEndless) return;

                int delta = getResources().getBoolean(R.bool.is_vert)?dy:dx;

                if (delta > 0) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();


                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        mList.addAll(FragmentJoke.getJokeList());
                        mRecyclerAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
// sharedPreference
        mIsEndless = true;
// fragment

        onComplete(START_CLICK_STRING_RESOURCE);

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

    private SpanData getDisplayMetrics() {
        DisplayMetrics dp = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dp);
        boolean isLand = dp.widthPixels > dp.heightPixels;
        double width = dp.widthPixels / dp.density;
        double height = dp.heightPixels / dp.density * RECYCLER_HEIGHT_RATIO;  // real height


        int spanInWidth = (int) Math.round(width / HIGH_SCALE_WIDTH);
        int spanHeight = (int) (width * dp.density / spanInWidth / SCALE_RATIO);
        int spanInHeight = (int) Math.round(height / HIGH_SCALE_HEIGHT);
        int spanWidth = (int) (height * dp.density / spanInHeight * SCALE_RATIO);


        if (spanInWidth < MIN_SPAN) spanInWidth = MIN_SPAN;
        if (spanInWidth > MAX_SPAN) spanInWidth = MAX_SPAN;

        if (spanInHeight < MIN_SPAN) spanInHeight = MIN_SPAN;
        if (spanInHeight > MAX_SPAN) spanInHeight = MAX_SPAN;

        int minWidth = (int) (MIN_HEIGHT * SCALE_RATIO);
        if (spanWidth < minWidth) spanWidth = minWidth;

// vertical
//        mSpan = spanInWidth;
//        mSpanHeight = spanHeight;
// horizontal
//        mSpan = spanInHeight;
//        mSpanWidth = spanWidth;

        return new SpanData(spanInWidth, spanInHeight, spanWidth, spanHeight);
    }


}
