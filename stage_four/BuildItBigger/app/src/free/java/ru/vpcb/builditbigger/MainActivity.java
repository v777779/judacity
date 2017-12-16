package ru.vpcb.builditbigger;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

import static android.view.View.INVISIBLE;
import static ru.vpcb.constants.Constants.AD_ACTIVATION_COUNTER;
import static ru.vpcb.constants.Constants.BUNDLE_AD_COUNTER;
import static ru.vpcb.constants.Constants.BUNDLE_FRONT_IMAGE_ID;
import static ru.vpcb.constants.Constants.BUNDLE_FRONT_TEXT_ID;
import static ru.vpcb.constants.Constants.BUNDLE_JOKE_LIST;
import static ru.vpcb.constants.Constants.BUNDLE_POSITION;
import static ru.vpcb.constants.Constants.GET_REQUEST;
import static ru.vpcb.constants.Constants.INTENT_REQUEST_CODE;
import static ru.vpcb.constants.Constants.INTENT_STRING_EXTRA;
import static ru.vpcb.constants.Constants.TEST_REQUEST;

public class MainActivity extends AppCompatActivity implements ICallback {
    /**
     * Context used to reject calls from Java Development server with old context
     */
    private static Context mContext;
    /**
     * Boolean value used for making TimberTree one time only
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    public static boolean mIsTimber;
    /**
     * Static Endpoint AsyncTask
     */
    private static EndpointsAsyncTask mEndPointTask;
    /**
     * AdView Ad Banner object
     */
    @Nullable
    @BindView(R.id.adview_banner)
    AdView mAdView;
    /**
     * Progress Bar view object
     */
    @Nullable
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    /**
     * Button view object
     */
    @Nullable
    @BindView(R.id.joke_button)
    Button mButton;
    /**
     * Image view object for the  screen of Main Activity
     */
    @Nullable
    @BindView(R.id.front_image)
    ImageView mFrontImage;
    /**
     * Text view object for the screen of Main Activity
     */
    @Nullable
    @BindView(R.id.front_text)
    TextView mFrontText;
    /**
     * RecyclerView with jokes support images used for wide screen devices only
     */
    @Nullable
    @BindView(R.id.joke_recycler)
    RecyclerView mRecycler;

    /**
     * Adapter for RecyclerView
     */
    private JokeAdapter mAdapter;
    /**
     * Interstitial Ad object
     */
    private InterstitialAd mInterstitialAd;
    /**
     * Boolean used to track if onComplete is done for
     * sync between EndPoint Callback and Interstitial Listener
     */
    private boolean mIsOnComplete;
    /**
     * Boolean used to track if Interstitial Ad is closed for
     * sync between EndPoint Callback and Interstitial Listener
     */
    private boolean mIsOnAdClosed;
    /**
     * Boolean used to block start Detail Activity when user
     * clicked on Interstitial Ad
     */
    private boolean mIsBlocked;
    /**
     * Integer value Interstitial Ad counter used as ratio between Jokes and
     * Interstitial Ad, default value is 3 jokes for one Interstitial
     */
    private int mAdCounter;
    /**
     * String value   is the text received from EndPoint
     */
    private String mJokeReceived;
    /**
     * Boolean value is wide screen used
     */
    private boolean mIsWide;
    /**
     * Integer value is resource Id of text that used for Main Activity Screen
     */
    private int mFrontTextId;
    /**
     * Integer value is resource Id of image that used for Main Activity Screen
     */
    private int mFrontImageId;
    /**
     * Integer value is resource Id of image that passed to Detail Activity or Fragment screen
     */
    private int mJokeImageId;
    /**
     * List<Integer> is list of joke image Ids for RecyclerView
     */
    private List<Integer> mList;
    /**
     * Integer value of current position of RecyclerView
     */
    private int mPosition;
    /**
     * ButterKnife object, used to close all binds on destroy.
     */
    private Unbinder mUnBinder;
    /**
     * Boolean flag is true when test mode on and all responses from Endpoint will the same
     */
    private boolean mIsTest;
    /**
     * IdlingResource used for Endpoint Callback tests
     */
    @Nullable
    private EndpointIdling mEndpointIdling;

    /**
     * Initializes Main Activity
     * Setup actionBar home button with custom icon.
     * Setup AdMob Banner and Interstitial for Free Flavor.
     * Setup Timber.Tree if not exists.
     * Setup "GET JOKE" Button object and listener which is used for making request to Cloud Endpoint.
     * Setup or extract values fro savedInstance object
     * For Wide screen devices<br>
     * Setup RecyclerView
     * Pass front text string value and imageId to JokeFragment and run Joke Fragment object
     *
     * @param savedInstanceState Bundle storage object with parameters. <br>
     *                           Bundle parameters: <br>
     *                           List<Integer>   mList of imageId that is used as data source for RecyclerView.<br>
     *                           Integer         mPosition   current position of RecyclerView.<br>
     *                           Integer         mFrontTextId  current text Id of welcome message of Main Activity Screen.<br>
     *                           Integer         mFrontImaged  current image Id of Main Activity Screen.<br>
     *                           Integer         mAdCounter    current vslue of AdMob delay counter
     */
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
        mUnBinder = ButterKnife.bind(this);

// local
        mIsWide = getResources().getBoolean(R.bool.is_wide);
        mJokeImageId = 0;  // image to pass to fragment
        mContext = this;

// log
        if (!mIsTimber) {
            Timber.plant(new Timber.DebugTree());
            mIsTimber = true;
        }
// test
        setIdlingResource();
        mIsTest = false;

        if (savedInstanceState != null) {
            mList = savedInstanceState.getIntegerArrayList(BUNDLE_JOKE_LIST);
            mPosition = savedInstanceState.getInt(BUNDLE_POSITION);
            mFrontTextId = savedInstanceState.getInt(BUNDLE_FRONT_TEXT_ID);
            mFrontImageId = savedInstanceState.getInt(BUNDLE_FRONT_IMAGE_ID);
            mAdCounter = savedInstanceState.getInt(BUNDLE_AD_COUNTER);

        } else {
            mList = JokeUtils.getImageList();
            mPosition = 0;
            mFrontTextId = R.string.welcome_message;
            mFrontImageId = JokeUtils.getFrontImage();
            mAdCounter = 0;
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

    /**
     * Setup items of options menu with Preferences values.
     *
     * @param menu Menu object.
     * @return boolean value.
     */
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

    /**
     * Processes Home and settings menu items clicks.
     * Settings Menu is empty.
     *
     * @param item MenuItem object that was selected.
     * @return true if item was processed.
     */
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

    /**
     *  Starts Activity
     *  Set values of MainActivity text and image view objects
     *  when start or come back from Detail Activity.
     *  Set mIsActive = true to admit Endpoint Requests.
     *
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (!mIsWide) {
            mFrontText.setText(mFrontTextId);
            mFrontImage.setImageResource(mFrontImageId);
        }

    }

    /**
     *  Stops Activity.
     *  Set mIsActive = false to reject Endpoint Requests.
     *  It's extremely important when application is closed and postponed.
     *  requests come back from Cloud Endpoint.
     */
    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * Destroys Activity.
     * Unbinds ButterKnife object.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnBinder.unbind();
        mContext = null;

    }

    /**
     * Saves parameters to Bundle storage object
     *
     * @param outState Bundle storage object for parameters.
     *                 Bundle Parameters: <br>
     *                 List<Integer>   mList of imageId that is used as data source for RecyclerView.<br>
     *                 Integer         mPosition   current position of RecyclerView.<br>
     *                 Integer         mFrontTextId    current text Id of welcome message of Main Activity Screen.<br>
     *                 Integer         mFrontImaged    current image Id of Main Activity Screen.<br>
     *                 Integer         mAdCounter    current vslue of AdMob delay counter
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);  // Attention!!! After bundle operations only

        outState.putIntegerArrayList(BUNDLE_JOKE_LIST, new ArrayList<Integer>(mList));
        outState.putInt(BUNDLE_POSITION, mPosition);
        outState.putInt(BUNDLE_FRONT_TEXT_ID, mFrontTextId);
        outState.putInt(BUNDLE_FRONT_IMAGE_ID, mFrontImageId);
        outState.putInt(BUNDLE_AD_COUNTER, mAdCounter);

    }

    /**
     * Callback for Endpoint AsyncTask object. Saves received Joke to mReceivedJoke variable.
     * Set flag mIsOnComplete = true
     * Calls nextActivity() method which runs DetailActivity or JokeFragment activities
     * There are two Async tasks : EndPoint onComplete() callback and
     * Interstitial Ad Listener onAdClosed() callback.
     * Any of them can be first, so to sync this, onComplete and onAdClosed()
     * set own flags when finished.
     * onComplete() and onAdClosed() calls nextActivity(), which checks both flags and
     * starts Activity if both flags set true.
     *
     * @param s String id Joke Text or Diagnostic message from Cloud EndPoint.
     */
    @Override
    public void onComplete(String s) {
        mJokeReceived = s;
        mIsOnComplete = true;
        nextActivity();

    }

    /**
     * Callback method for RecyclerView JKViewHolder.
     * Called when user clicked on RecyclerView Item.
     * Actual for wide screen devices only.
     * Passes integer value of imageId, that holds ViewHolder to mJokeImageId.
     * This imageId then passed to args bundle of JokeFragment and showed in
     * Fragment frame  ultimately.
     *
     * @param value
     */
    @Override
    public void onComplete(int value) {
        mJokeImageId = value;
        mButton.callOnClick();

    }

    /**
     *  Unlocks IdlingResource after mJokeText object was set
     *  by DetailActivity or JokeFragment to new value
     *  received from EndPointAsyncTask response.
     */
    @Override
    public void onCompleteIdling() {
        unlockEndpointIdling();
    }

    /**
     * Starts Detail Activity for smart phones or JokeFragment for tablets.
     * Checks context and rejects requests from the old context
     * Checks flags from Endpoint onComplete(s) and Interstitial Ad onAdClosed() callbacks.
     * If both done and blocking flag mIsBlocked is false, the activity is started.
     * Joke text from Endpoint Async Task object is passed via Intent object to  DetailActivity.
     * Joke text from Endpoint Async Task object is passed via Bundle object to  JokeFragment.
     * Joke imageId of selected RecyclerView item or Front Image is passed via Bundle object to Joke Fragment
     * Increments Interstitial Ad delay counter.
     * Set progress bar mProgressBar invisible
     */
    private void nextActivity() {
        if (mContext != this) return;  // rejects requests from old context

        if (!mIsOnComplete || !mIsOnAdClosed || mIsBlocked)
            return;

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

    /**
     * Callback from DetailActivity when mJokeText was filled by value received from MainActivity
     * Calls onCompleteIdling to unlock IdlingResource
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == INTENT_REQUEST_CODE) {
            onCompleteIdling();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Starts DetailActivity via Intent.
     * Saves string of text from Endpoint  to Intent object as parameter
     * Sets flags to quick return to Main when requests spawn a bunch of intents
     * Starts intent for result to get response from DetailActivity when mJokeText is set
     *
     * @param s String of joke text from Endpoint AsyncTask object.
     */
    private void startActivity(String s) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(INTENT_STRING_EXTRA, mJokeReceived);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(intent,INTENT_REQUEST_CODE);

    }

    /**
     * Starts Joke Fragment via Fragment transaction.
     * Saves string of text from Endpoint  to Bundle object as parameter.
     * Saves image Id of selected RecyclerView item or Front Image to Bundle object as parameter.
     *
     * @param s  String of joke text from Endpoint AsyncTask object.
     * @param id Integer  of imageId of selected RecyclerView item or
     *           Front Image to Bundle object as parameter.
     */
    private void startFragment(String s, int id) {
//        if (isFinishing()) return;                      // resolves conflicts with savedInstance

        Fragment fragment = JokeFragment.newInstance(s, id);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    /**
     * Returns  Interstitial Ad object with connected listener.
     * onAdClosed()            callback when user closed Interstitial Ad object.
     * onAdFailedToLoad()      callback when load ofInterstitial Ad object is failed.
     * onAdLeftApplication()   callback when user opened Interstitial Ad object.
     *
     * @return Interstitial Ad object
     */
    private InterstitialAd newInterstitialAd() {
        final InterstitialAd interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.inter_ad_unit_id));

        interstitialAd.setAdListener(new AdListener() {
            final InterstitialAd nInterstitialAd = interstitialAd;

            /**
             *  Callback for Interstitial Ad object. Called when user closed Interstitial Ad.
             *  Creates new request for Interstitial Ad.
             *  Set flag mIsOnAdClosed = true
             *  Calls nextActivity() method which runs DetailActivity or JokeFragment activities
             *  There are two Async tasks : EndPoint onComplete() callback and
             *  Interstitial Ad Listener onAdClosed() callback.
             *  Any of them can be first, so to sync this, onComplete and onAdClosed()
             *  set own flags when finished.
             *  onComplete() and onAdClosed() calls nextActivity(), which checks both flags and
             *  starts Activity if both flags set true.
             */
            @Override
            public void onAdClosed() {
                nInterstitialAd.loadAd(new AdRequest.Builder().build());
                mIsOnAdClosed = true;
                nextActivity();
            }

            /**
             *  Diagnostic method, when Interstitial Ad load failed, writes event to log.
             * @param i  Integer  return code.
             */
            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                Timber.d("Ad did not load");
            }

            /**
             *  Callback method, called when user click on Interstitial Ad.
             *  Set flag mIsBlocked = true to prevent starting DetailActivity
             *  Set progress bar mProgressBar invisible
             */
            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
                mIsBlocked = true;
                mProgressBar.setVisibility(INVISIBLE);
            }
        });
        return interstitialAd;
    }

    /**
     * Shows Interstitial Ad object
     * Checks if Interstitial Ad object is loaded and shows content.
     * If loading is failed or Interstitial Ad object is not exist
     * puts message to Debug log and generates next Interstitial Ad object.
     */
    private void showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and reload the ad.
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Timber.d("Ad did not load");
            nextInterstitial();
        }
    }


    /**
     * Generated new  new request for Interstitial Ad object.
     */
    private void loadInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        mInterstitialAd.loadAd(adRequest);
    }


    /**
     * Creates new Interstitial Ad object and generates new request for it.
     */
    private void nextInterstitial() {
        mInterstitialAd = newInterstitialAd();
        loadInterstitial();
    }


    /**
     * Creates Listener for "GET JOKE" Button.
     * Checks if Interstitial Ad counter is full and shows Interstitial Ad content.
     * Generate new request to Endpoint AsyncTask object for new Joke text.
     * Set flags according to application logic.
     * Shows progress bar, set mProgressBar is visible.
     */
    private void setGetButton() {
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
//                        nextActivity();
                    }
                    mAdCounter = 0;
                } else {
                    mIsOnAdClosed = true; // skip ad
                }
// endpoints static object
                makeEndpOintRequest(mIsTest);

                mIsOnComplete = false;
                mIsBlocked = false;
            }
        });
    }

    /**
     * Setup AdMob application and banner and Interstitial Ad objects
     * Builds both object for emulator device only.
     */
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

    /**
     * Setup RecyclerView object for wide screen devices
     * RecyclerView uses GridLayout with different scrolling direction
     * depending on orientation.
     * Setup JokeUtils.Span object with display parameters
     * using helper method JokeUtils.getDisplayMetrics().
     * JokeUtils.Span sp.spanX, sp.height  used for vertical scrolling
     * JokeUtils.Span sp.spanY, sp.width  used for horizontal scrolling
     * Setup Recycler Layout and  Adapter objects.
     * Setup listener which is emulates endless RecyclerView by adding copies of
     * imageIds to RecylerView data source mList<Integer> object.
     */
    private void setRecycler() {
// recycler
        final boolean mIsEndless = true;
        JokeUtils.Span sp = JokeUtils.getDisplayMetrics(this);

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

                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount - 5) {
                        mList.addAll(JokeUtils.getImageList());
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        mRecycler.scrollToPosition(mPosition);

    }

    /**
     * Makes request to EndpointAsyncTask object.
     * mIsTask flag defines if request is GET_REQUEST or TEST_REQUEST
     * Test request gets fixed value in response
     * Normal request gets random joke in response
     * Locks IdlingResource object until onIdlingComplete() is executed
     */
    private void makeEndpOintRequest(boolean isTest) {
        String request = GET_REQUEST;
        if (isTest) {
            request = TEST_REQUEST;
        }
        mEndPointTask = new EndpointsAsyncTask(MainActivity.this, request);
        mEndPointTask.execute();
        lockEndpointIdling();
    }

    /**
     * Returns Idling Resource for EndpointAsyncTask callback test
     * Visible as private for all types of flavor, but public while test
     *
     * @return IdlingResource object
     */
    @NonNull
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    public IdlingResource getIdlingResource() {
        if (mEndpointIdling == null) {
            mEndpointIdling = new EndpointIdling();
        }
        return mEndpointIdling;
    }

    /**
     * Creates IdlingResource object and saves it to mEndpointIdling
     */
    private void setIdlingResource() {
        if (mIsTest && mEndpointIdling == null) {
            mEndpointIdling = new EndpointIdling();
        }
    }

    /**
     * Locks IdlingResource while operation is going on
     */
    private void lockEndpointIdling() {
        if (mIsTest && mEndpointIdling != null) {
            mEndpointIdling.setIdleState(false);
        }
    }

    /**
     * UnLocks IdlingResource after operation is completed
     */
    private void unlockEndpointIdling() {
        if (mIsTest && mEndpointIdling != null) {
            mEndpointIdling.setIdleState(true);
        }
    }

    /**
     * Sets MainActivity to test mode
     * While test mode all IdlingResource  methods are active and request type is TEST_REQUEST
     */
    @NonNull
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    public void setTestMode() {
        mIsTest = true;
    }
}
