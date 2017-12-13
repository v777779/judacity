package ru.vpcb.basicactivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends AppCompatActivity implements ICallback {
    private static boolean isActive;

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String INTENT_STRING_EXTRA = "intent_string_extra";
    public static final String REQUEST_GET_TEMPLATE = "get";
    public static final String REQUEST_TEST_GET_TEMPLATE = "test";
    public static final String REQUEST_TEST_OUT_TEMPLATE = "test joke received";
    public static final int CONNECT_TIMEOUT = 5;
    public static final String MESSAGE_TEST_OK = "*** Endpont Test passed ***";
    public static final int AD_ACTIVATION_COUNTER = 3;


    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    private boolean mIsEndpointEmpty;
    public  ProgressBar mProgressBar;
    private boolean mIsOnComplete;
    private boolean mIsOnAdClosed;
    private boolean mIsRequested;
    public  int mAdCounter;
    private boolean mIsBlocked;


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


        mProgressBar = findViewById(R.id.progress_bar);

        mIsOnComplete = false;
        mIsOnAdClosed = false;
        mAdCounter = AD_ACTIVATION_COUNTER;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//progress bar
                mProgressBar.setVisibility(View.VISIBLE);
// interstitial
                if(mAdCounter >= AD_ACTIVATION_COUNTER) {
                    if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                        mIsOnAdClosed = false;
                    } else {
                        nextInterstitial();
                        mIsOnAdClosed = true;
                        nextActivity();
                    }
                    mAdCounter = 0;
                }else {
                    mIsOnAdClosed = true; // skip ad
                }
// endpoints
                if(!mIsRequested) {
                    new EndpointsAsyncTask(MainActivity.this, REQUEST_GET_TEMPLATE).execute();
                     mIsRequested = true;
                     mIsOnComplete = false;
                }
                mIsBlocked = false;

            }
        });

// admob
        MobileAds.initialize(this, getString(R.string.banner_ad_app_id));
// banner
        mAdView = findViewById(R.id.adview_banner);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);

// interstitial
        mInterstitialAd = newInterstitialAd();
        loadInterstitial();


        FloatingActionButton fab2 = findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Detail2Activity.class);
                startActivity(intent);

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
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    protected void onResume() {
        super.onResume();
        isActive = true;

//        if(mInterstitialAd != null) {
//            mInterstitialAd.setAdListener(new AdListener() {
//                @Override
//                public void onAdClosed() {
//                    mInterstitialAd.loadAd(new AdRequest.Builder().build());
//                }
//            });
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if(mInterstitialAd != null) {
//            mInterstitialAd.setAdListener(null);
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isActive = false;
    }

    private String mJokeReceived;

    @Override
    public void onComplete(String s) {
        mJokeReceived = s;
        mIsOnComplete = true;
        mIsRequested = false;
        nextActivity();

    }

    private void nextActivity() {
       if (!mIsOnComplete || !mIsOnAdClosed || mIsBlocked) return;  // if one of onComplete  and Interstitial not done

        if(!isActive ) return;


        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(INTENT_STRING_EXTRA, mJokeReceived);
        startActivity(intent);
        mIsEndpointEmpty = true;

        if (getResources().getBoolean(R.bool.transition_light)) {
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        } else if (getResources().getBoolean(R.bool.transition_dark)) {
            overridePendingTransition(R.anim.slide_right, R.anim.slide_left_out);
        }

        mProgressBar.setVisibility(View.INVISIBLE);
         mAdCounter++;
    }

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
                mProgressBar.setVisibility(View.INVISIBLE);
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

}
