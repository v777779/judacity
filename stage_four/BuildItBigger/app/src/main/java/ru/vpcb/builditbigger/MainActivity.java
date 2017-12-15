package ru.vpcb.builditbigger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.INVISIBLE;
import static ru.vpcb.constants.Constants.AD_ACTIVATION_COUNTER;
import static ru.vpcb.constants.Constants.BUNDLE_FRONT_IMAGE_ID;
import static ru.vpcb.constants.Constants.BUNDLE_FRONT_TEXT_ID;
import static ru.vpcb.constants.Constants.BUNDLE_JOKE_LIST;
import static ru.vpcb.constants.Constants.BUNDLE_POSITION;
import static ru.vpcb.constants.Constants.BUNDLE_PROGRESS_BAR;
import static ru.vpcb.constants.Constants.INTENT_STRING_EXTRA;
import static ru.vpcb.constants.Constants.REQUEST_GET_TEMPLATE;

public class MainActivity extends AppCompatActivity implements ICallback {
    private static boolean mIsActive;
    private static boolean isTimber;
    private static final String TAG = MainActivity.class.getSimpleName();

    // bind
    private AdView mAdView;
    private ProgressBar mProgressBar;
    private Button mButton;
    private ImageView mFrontImage;
    private TextView mFrontText;
    private RecyclerView mRecycler;
    private JokeAdapter mAdapter;


    // local
    private InterstitialAd mInterstitialAd;
    private boolean mIsOnComplete;
    private boolean mIsOnAdClosed;
    private boolean mIsBlocked;
    private int mAdCounter;
    private String mJokeReceived;

    private boolean mIsWide;
    private boolean mIsLand;
    private int mFrontTextId;
    private int mFrontImageId;
    private int mJokeImageId;
    private List<Integer> mList;
    private int mPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_home_white_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
// bind
        mProgressBar = findViewById(R.id.progress_bar);
        mAdView = findViewById(R.id.adview_banner);
        mFrontImage = findViewById(R.id.front_image);
        mFrontText = findViewById(R.id.front_text);

// local
        mIsWide = getResources().getBoolean(R.bool.is_wide);
        mIsLand = getResources().getBoolean(R.bool.is_land);
        mJokeImageId = 0;  // image to pass to fragment
        mIsActive = true;

        if (savedInstanceState != null) {
            mList = savedInstanceState.getIntegerArrayList(BUNDLE_JOKE_LIST);
            mPosition = savedInstanceState.getInt(BUNDLE_POSITION);
            mFrontTextId = savedInstanceState.getInt(BUNDLE_FRONT_TEXT_ID);
            mFrontImageId = savedInstanceState.getInt(BUNDLE_FRONT_IMAGE_ID);
            mProgressBar.setVisibility(savedInstanceState.getInt(BUNDLE_PROGRESS_BAR, INVISIBLE));

        } else {
            mList = JokeUtils.getImageList();
            mPosition = 0;
            mFrontTextId = R.string.welcome_message;
            mFrontImageId = JokeUtils.getFrontImage();
            if (mIsWide) {
                startFragment(getString(mFrontTextId), mFrontImageId); // for tablet only
            }
        }

        setAdMob();
        setGetButton();
        if (mIsWide) {
            setRecycler();  // for tablet only
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mIsActive = true;
        if (!mIsWide) {
            mFrontText.setText(mFrontTextId);
            mFrontImage.setImageResource(mFrontImageId);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        mIsActive = false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntegerArrayList(BUNDLE_JOKE_LIST, new ArrayList<Integer>(mList));
        outState.putInt(BUNDLE_POSITION, mPosition);
        outState.putInt(BUNDLE_FRONT_TEXT_ID, mFrontTextId);
        outState.putInt(BUNDLE_FRONT_IMAGE_ID, mFrontImageId);
        outState.putInt(BUNDLE_PROGRESS_BAR, mProgressBar.getVisibility());
    }

    @Override
    public void onComplete(String s) {
        mJokeReceived = s;
        mIsOnComplete = true;
        nextActivity();

    }

    @Override
    public void onComplete(int value) {
        mJokeImageId = value;
        mButton.callOnClick();

    }

    private void nextActivity() {
        if (!mIsOnComplete || !mIsOnAdClosed || mIsBlocked)
            return;  // if one of onComplete  and Interstitial not done

        if (!mIsActive) return;

        if (!mIsWide) {
            startActivity(mJokeReceived);

        } else {
            startFragment(mJokeReceived, mJokeImageId);  //  imageId = 0
            mJokeImageId = 0;
        }

        mFrontTextId = R.string.next_message;
        mProgressBar.setVisibility(INVISIBLE);
        mAdCounter++;
    }

    private void startActivity(String s) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(INTENT_STRING_EXTRA, mJokeReceived);
        startActivity(intent);

    }

    private void startFragment(String s, int id) {
        Fragment fragment = JokeFragment.newInstance(s, id);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    // ad interstitial
    private InterstitialAd newInterstitialAd() {
        final InterstitialAd interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.inter_ad_unit_id));

        interstitialAd.setAdListener(new AdListener() {
            final InterstitialAd nInterstitialAd = interstitialAd;

            @Override
            public void onAdClosed() {
                nInterstitialAd.loadAd(new AdRequest.Builder().build());
                mIsOnAdClosed = true;
                nextActivity();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                Log.d(TAG, "Ad did not load");
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
                mIsBlocked = true;
                mProgressBar.setVisibility(INVISIBLE);
            }
        });
        return interstitialAd;
    }

    private void showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and reload the ad.
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d(TAG, "Ad did not load");
            nextInterstitial();
        }
    }

    private void loadInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        mInterstitialAd.loadAd(adRequest);
    }

    private void nextInterstitial() {
        mInterstitialAd = newInterstitialAd();
        loadInterstitial();
    }


    // button
    private void setGetButton() {

        mButton = findViewById(R.id.joke_button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressBar.setVisibility(View.VISIBLE);                           // progress bar
                if (mAdCounter >= AD_ACTIVATION_COUNTER) {                          // interstitial
                    if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                        mIsOnAdClosed = false;
                    } else {
                        nextInterstitial();
                        mIsOnAdClosed = true;
                        nextActivity();
                    }
                    mAdCounter = 0;
                } else {
                    mIsOnAdClosed = true; // skip ad
                }
// endpoints
                new EndpointsAsyncTask(MainActivity.this, REQUEST_GET_TEMPLATE).execute();
                mIsOnComplete = false;
                mIsBlocked = false;
            }
        });
    }

    // adMob
    private void setAdMob() {
        MobileAds.initialize(this, getString(R.string.banner_ad_app_id));
// banner
//        mAdView = findViewById(R.id.adview_banner);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);
// interstitial
        mInterstitialAd = newInterstitialAd();
        loadInterstitial();

    }


    private void setRecycler() {
// recycler
        final boolean mIsEndless = true;
        JokeUtils.Span sp = JokeUtils.getDisplayMetrics(this);
        mRecycler = findViewById(R.id.joke_recycler);  // appcompat design
        int span = sp.spanY;
        int orientation = GridLayout.HORIZONTAL;
        if (getResources().getBoolean(R.bool.is_vert)) {
            span = sp.spanX;
            orientation = GridLayout.VERTICAL;
        }
        final GridLayoutManager layoutManager = new GridLayoutManager(this, span, orientation, false);
        mRecycler.setLayoutManager(layoutManager);                          // connect to LayoutManager
        mRecycler.setHasFixedSize(true);                                    // item size fixed
        mAdapter = new JokeAdapter(this, mList, sp.width, sp.height);
        mRecycler.setAdapter(mAdapter);
        mRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (!mIsEndless) return;
                int delta = getResources().getBoolean(R.bool.is_vert) ? dy : dx;

                if (delta > 0) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();
                    mPosition = pastVisiblesItems;

                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount - 2) {
                        mList.addAll(JokeUtils.getImageList());
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        mRecycler.scrollToPosition(mPosition);

    }
}
