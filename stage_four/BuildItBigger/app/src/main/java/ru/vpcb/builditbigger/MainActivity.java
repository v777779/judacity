package ru.vpcb.builditbigger;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import static ru.vpcb.constants.Constants.AD_ACTIVATION_COUNTER;
import static ru.vpcb.constants.Constants.REQUEST_GET_TEMPLATE;

public class MainActivity extends AppCompatActivity implements ICallback {
    private static boolean isActive;
    private static boolean isTimber;
    private static final String TAG = MainActivity.class.getSimpleName();

    // bind
    private AdView mAdView;
    private ProgressBar mProgressBar;
    private Button mButton;


    private InterstitialAd mInterstitialAd;
    private boolean mIsOnComplete;
    private boolean mIsOnAdClosed;
    private boolean mIsBlocked;
    private int mAdCounter;
    private String mJokeReceived;

// conditions
    private boolean mIsWide;
    private boolean mIsLand;


// fragment
    private ImageView mFrontImage;
    private TextView  mFrontText;



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

// local
        mIsWide = getResources().getBoolean(R.bool.is_wide);
        mIsLand = getResources().getBoolean(R.bool.is_land);


// adMob
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

// button
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

// layout
        if(!mIsWide) {
            mFrontImage = findViewById(R.id.front_image);
            mFrontText = findViewById(R.id.front_text);
            setFront(false);
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
        isActive = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActive = false;
    }


    @Override
    public void onComplete(String s) {
        mJokeReceived = s;
        mIsOnComplete = true;
        nextActivity();
    }

    @Override
    public void onComplete(int value) {

    }

    private void nextActivity() {
        if (!mIsOnComplete || !mIsOnAdClosed || mIsBlocked) return;  // if one of onComplete  and Interstitial not done

        if(!isActive ) return;


//        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
//        intent.putExtra(INTENT_STRING_EXTRA, mJokeReceived);
//        startActivity(intent);

        mProgressBar.setVisibility(View.INVISIBLE);
        mAdCounter++;

// front
        setFront(true);
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

    private void setFront(boolean mode) {
        mFrontImage.setImageResource(JokeImage.getFrontImage());
        if(!mode) {
            mFrontText.setText(R.string.welcome_message);
        }else {
            mFrontText.setText(R.string.next_message);
        }

    }

}
