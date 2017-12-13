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
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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


    public static final int HIGH_WIDTH_PORTRAIT = 600;     // dpi  600
    public static final int HIGH_WIDTH_LANDSCAPE = 900;    // dpi  900
    public static final int HIGH_SCALE_PORTRAIT = 240;     // dpi
    public static final int HIGH_SCALE_LANDSCAPE = 250;    // dpi
    public static final double SCREEN_RATIO = 1.8;
    public static final int MAX_SPAN = 6;
    public static final int MIN_SPAN = 1;
    public static final int MIN_HEIGHT = 100;


    private AdView mAdView;
    private FloatingActionButton mFab;
    private Button mButton;
    private AsyncJoke mAsyncJoke;

    private RecyclerView mRecyclerView;
    private JokeAdapter mRecyclerAdapter;
    private int mSpan;
    private int mSpanHeight;
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
        setDisplayMetrics();        // mSpan, mSpanHeight
        mList = FragmentJoke.getJokeList();
        mRecyclerView = findViewById(R.id.joke_recycler);  // appcompat design
        final GridLayoutManager layoutManager = new GridLayoutManager(this, mSpan);
        mRecyclerView.setLayoutManager(layoutManager);                          // connect to LayoutManager
        mRecyclerView.setHasFixedSize(true);                                    // item size fixed
        mRecyclerAdapter = new JokeAdapter(mList, mSpanHeight, this);
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (!mIsEndless) return;
                if (dy > 0) {
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


    /**
     * Sets screen parameters for RecyclerView mSpan, mSpanHeight
     */

    private void setDisplayMetrics() {
        DisplayMetrics dp = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dp);
        Resources res = getResources();
        boolean isLand = res.getBoolean(R.bool.is_land);
        boolean mIsWide = res.getBoolean(R.bool.is_wide);
        double width = dp.widthPixels / dp.density;

        if (!isLand) {
            mSpan = (int) Math.round(width / HIGH_SCALE_PORTRAIT);
            mSpanHeight = (int) (dp.widthPixels / mSpan / SCREEN_RATIO);

        } else {
            mSpan = (int) Math.round(width / HIGH_SCALE_LANDSCAPE);
            mSpanHeight = (int) (dp.widthPixels / mSpan / SCREEN_RATIO);
        }

        if (mSpan < MIN_SPAN) mSpan = MIN_SPAN;
        if (mSpan > MAX_SPAN) mSpan = MAX_SPAN;
        if (mSpanHeight < MIN_HEIGHT) mSpanHeight = MIN_HEIGHT;

    }

}
