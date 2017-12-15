package ru.vpcb.builditbigger;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import static ru.vpcb.constants.Constants.BUNDLE_FRONT_IMAGE_ID;
import static ru.vpcb.constants.Constants.BUNDLE_FRONT_TEXT_ID;
import static ru.vpcb.constants.Constants.BUNDLE_JOKE_LIST;
import static ru.vpcb.constants.Constants.BUNDLE_POSITION;
import static ru.vpcb.constants.Constants.BUNDLE_PROGRESS_BAR;
import static ru.vpcb.constants.Constants.INTENT_STRING_EXTRA;
import static ru.vpcb.constants.Constants.REQUEST_GET_TEMPLATE;

public class MainActivity extends AppCompatActivity implements ICallback {
    /**
     *  Boolean value used to reject calls from Java Development server when activity is done
     */
    private static boolean mIsActive;
    /**
     *  Boolean value used for making TimberTree one time only
     */
    private static boolean mIsTimber;
    /**
     * Progress Bar view object
     */
    @Nullable @BindView(R.id.progress_bar)  ProgressBar mProgressBar;
    /**
     *  Button view object
     */
    @Nullable @BindView(R.id.joke_button)  Button mButton;
    /**
     * Image view object for the  screen of Main Activity
     */
    @Nullable @BindView(R.id.front_image) ImageView mFrontImage;
    /**
     * Text view object for the screen of Main Activity
     */
    @Nullable @BindView(R.id.front_text)  TextView mFrontText;
    /**
     * RecyclerView with jokes support images used for wide screen devices only
     */
    @Nullable  @BindView(R.id.joke_recycler) RecyclerView mRecycler;

    /**
     *  Adapter for RecyclerView
     */
    private JokeAdapter mAdapter;

    /**
     *  String value   is the text received from EndPoint
     */
    private String mJokeReceived;
    /**
     *  Boolean value is wide screen used
     */
    private boolean mIsWide;
    /**
     *  Integer value is resource Id of text that used for Main Activity Screen
     */
    private int mFrontTextId;
    /**
     *  Integer value is resource Id of image that used for Main Activity Screen
     */
    private int mFrontImageId;
    /**
     *  Integer value is resource Id of image that passed to Detail Activity or Fragment screen
     */
    private int mJokeImageId;
    /**
     *  List<Integer> is list of joke image Ids for RecyclerView
     */
    private List<Integer> mList;
    /**
     *  Integer value of current position of RecyclerView
     */
    private int mPosition;
    /**
     * ButterKnife object, used to close all binds on destroy.
     */
    private Unbinder mUnBinder;

    /**
     * Initializes Main Activity
     * Setup actionBar home button with custom icon.
     * Setup Timber.Tree if not exists.
     * Setup "GET JOKE" Button object and listener which is used for making request to Cloud Endpoint.
     * Setup or extract values fro savedInstance object
     * For Wide screen devices<br>
     * Setup RecyclerView
     * Pass front text string value and imageId to JokeFragment and run Joke Fragment object
     *
     * @param savedInstanceState  Bundle storage object with parameters. <br>
     *  Bundle parameters: <br>
     *  List<Integer>   mList of imageId that is used as data source for RecyclerView.<br>
     *  Integer         mPosition   current position of RecyclerView.<br>
     *  Integer         mFrontTextId    current text Id of welcome message of Main Activity Screen.<br>
     *  Integer         mFrontImaged    current image Id of Main Activity Screen.<br>
     *  ProgressBar     mProgressBar    current visibility state of ProgressBar view object.<br>
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
        mIsActive = true;

// log
        // log
        if (!mIsTimber) {
            Timber.plant(new Timber.DebugTree());
            mIsTimber = true;
        }

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
        mIsActive = true;
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
        mIsActive = false;
    }

    /**
     * Destroys Activity.
     *  Unbinds ButterKnife object.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnBinder.unbind();

    }

    /**
     *  Saves parameters to Bundle storage object
     *
     * @param outState Bundle storage object for parameters.
     *  Bundle Parameters: <br>
     *  List<Integer>   mList of imageId that is used as data source for RecyclerView.<br>
     *  Integer         mPosition   current position of RecyclerView.<br>
     *  Integer         mFrontTextId    current text Id of welcome message of Main Activity Screen.<br>
     *  Integer         mFrontImaged    current image Id of Main Activity Screen.<br>
     *  ProgressBar     mProgressBar    current visibility state of ProgressBar view object.<br>
     *
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntegerArrayList(BUNDLE_JOKE_LIST, new ArrayList<Integer>(mList));
        outState.putInt(BUNDLE_POSITION, mPosition);
        outState.putInt(BUNDLE_FRONT_TEXT_ID, mFrontTextId);
        outState.putInt(BUNDLE_FRONT_IMAGE_ID, mFrontImageId);
        outState.putInt(BUNDLE_PROGRESS_BAR, mProgressBar.getVisibility());
    }

    /**
     *  Callback for Endpoint AsyncTask object. Saves received Joke to mReceivedJoke variable.
     *  Calls nextActivity() method which runs DetailActivity or JokeFragment activities
     *
     * @param s  String id Joke Text or Diagnostic message from Cloud EndPoint.
     */
    @Override
    public void onComplete(String s) {
        mJokeReceived = s;
        nextActivity();

    }

    /**
     *  Callback method for RecyclerView JKViewHolder.
     *  Called when user clicked on RecyclerView Item.
     *  Actual for wide screen devices only.
     *  Passes integer value of imageId, that holds ViewHolder to mJokeImageId.
     *  This imageId then passed to args bundle of JokeFragment and showed in
     *  Fragment frame  ultimately.
     *
     * @param value
     */
    @Override
    public void onComplete(int value) {
        mJokeImageId = value;
        mButton.callOnClick();

    }

    /**
     * Starts Detail Activity for smart phones or JokeFragment for tablets.
     * Joke text from Endpoint Async Task object is passed via Intent object to  DetailActivity.
     * Joke text from Endpoint Async Task object is passed via Bundle object to  JokeFragment.
     * Joke imageId of selected RecyclerView item or Front Image is passed via Bundle object to Joke Fragment
     * Increments Interstitial Ad delay counter.
     * Set progress bar mProgressBar invisible
     */
    private void nextActivity() {
        if (!mIsActive) return;

        if (!mIsWide) {
            startActivity(mJokeReceived);

        } else {
            startFragment(mJokeReceived, mJokeImageId);  //  imageId = 0
            mJokeImageId = 0;
        }

        mFrontTextId = R.string.next_message;
        mProgressBar.setVisibility(INVISIBLE);
    }

    /**
     * Starts DetailActivity via Intent.
     *  Saves string of text from Endpoint  to Intent object as parameter
     *
     * @param s  String of joke text from Endpoint AsyncTask object.
     */
    private void startActivity(String s) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(INTENT_STRING_EXTRA, mJokeReceived);
        startActivity(intent);
    }

    /**
     * Starts Joke Fragment via Fragment transaction.
     *  Saves string of text from Endpoint  to Bundle object as parameter.
     *  Saves image Id of selected RecyclerView item or Front Image to Bundle object as parameter.
     *
     * @param s  String of joke text from Endpoint AsyncTask object.
     * @param id  Integer  of imageId of selected RecyclerView item or
     *            Front Image to Bundle object as parameter.
     */
    private void startFragment(String s, int id) {
        Fragment fragment = JokeFragment.newInstance(s, id);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }


    /**
     *  Creates Listener for "GET JOKE" Button.
     *  Shows progress bar, set mProgressBar is visible.
     *  Generate new request to Endpoint AsyncTask object for new Joke text.
     */
    private void setGetButton() {
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressBar.setVisibility(View.VISIBLE);                           // progress bar
// endpoints
                new EndpointsAsyncTask(MainActivity.this, REQUEST_GET_TEMPLATE).execute();
            }
        });
    }

    /**
     * Setup RecyclerView object for wide screen devices
     *  RecyclerView uses GridLayout with different scrolling direction
     *  depending on orientation.
     *  Setup JokeUtils.Span object with display parameters
     *  using helper method JokeUtils.getDisplayMetrics().
     *  JokeUtils.Span sp.spanX, sp.height  used for vertical scrolling
     *  JokeUtils.Span sp.spanY, sp.width  used for horizontal scrolling
     *  Setup Recycler Layout and  Adapter objects.
     *  Setup listener which is emulates endless RecyclerView by adding copies of
     *  imageIds to RecylerView data source mList<Integer> object.
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
