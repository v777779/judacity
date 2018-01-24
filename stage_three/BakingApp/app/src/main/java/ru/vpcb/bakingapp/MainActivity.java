package ru.vpcb.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.vpcb.bakingapp.data.IRetrofitAPI;
import ru.vpcb.bakingapp.data.LoaderDb;
import ru.vpcb.bakingapp.data.RecipeItem;
import ru.vpcb.bakingapp.utils.RecipeUtils;
import timber.log.Timber;

import static android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;
import static ru.vpcb.bakingapp.data.RecipeContract.RecipeEntry.COLUMN_RECIPE_ID;
import static ru.vpcb.bakingapp.utils.Constants.BUNDLE_DETAIL_INTENT;
import static ru.vpcb.bakingapp.utils.Constants.BUNDLE_ERROR_CONNECTION;
import static ru.vpcb.bakingapp.utils.Constants.BUNDLE_PREVIOUS_CONNECTION;
import static ru.vpcb.bakingapp.utils.Constants.BUNDLE_WIDGET_INTENT;
import static ru.vpcb.bakingapp.utils.Constants.FRAGMENT_ERROR_NAME;
import static ru.vpcb.bakingapp.utils.Constants.FRAGMENT_ERROR_TAG;
import static ru.vpcb.bakingapp.utils.Constants.FRAGMENT_PLAYER_NAME;
import static ru.vpcb.bakingapp.utils.Constants.HIGH_SCALE_LANDSCAPE;
import static ru.vpcb.bakingapp.utils.Constants.HIGH_SCALE_PORTRAIT;
import static ru.vpcb.bakingapp.utils.Constants.HIGH_WIDTH_LANDSCAPE;
import static ru.vpcb.bakingapp.utils.Constants.HIGH_WIDTH_PORTRAIT;
import static ru.vpcb.bakingapp.utils.Constants.LOADER_RECIPES_DB_ID;
import static ru.vpcb.bakingapp.utils.Constants.LOW_SCALE_LANDSCAPE;
import static ru.vpcb.bakingapp.utils.Constants.LOW_SCALE_PORTRAIT;
import static ru.vpcb.bakingapp.utils.Constants.MAX_SPAN;
import static ru.vpcb.bakingapp.utils.Constants.MESSAGE_ERROR_ID;
import static ru.vpcb.bakingapp.utils.Constants.MIN_HEIGHT;
import static ru.vpcb.bakingapp.utils.Constants.MIN_SPAN;
import static ru.vpcb.bakingapp.utils.Constants.MIN_WIDTH_WIDE_SCREEN;
import static ru.vpcb.bakingapp.utils.Constants.RECIPES_BASE;
import static ru.vpcb.bakingapp.utils.Constants.SCREEN_RATIO;
import static ru.vpcb.bakingapp.utils.Constants.SYSTEM_UI_SHOW_FLAGS;
import static ru.vpcb.bakingapp.utils.Constants.WIDGET_RECIPE_ID;
import static ru.vpcb.bakingapp.utils.Constants.WIDGET_WIDGET_ID;
import static ru.vpcb.bakingapp.utils.RecipeUtils.bulkInsert;
import static ru.vpcb.bakingapp.utils.RecipeUtils.isOnline;

/**
 * MainActivity class of current project.
 * Load data from the internet, saves them into database.
 * Creates RecyclerView for RecipeItem objects.
 * Implements IFragmentHelper callback interface.
 * Implements LoaderDb.ICallbackDb interface.
 */
public class MainActivity extends AppCompatActivity implements IFragmentHelper,
        LoaderDb.ICallbackDb {

    /**
     * RecyclerView of RecipeItem List with GridLayout
     */
    private RecyclerView mRecyclerView;
    /**
     * RecyclerView Adapter of RecipeItem List
     */
    private MainAdapter mRecyclerAdapter;
    /**
     * ProgressBar View object
     */
    private ProgressBar mProgressBar;
    /**
     * TextView Error Message  not used
     */
    private TextView mErrorMessage;
    /**
     * Root view of current activity
     */
    private View mRootView;
    /**
     * Loader database object, loads data into mCursor object
     */
    private LoaderDb mLoaderDb;
    /**
     * The number of items in row of GridLayout
     */
    private int mSpan;
    /**
     * The height of Item View in pixels
     */
    private int mSpanHeight;

    /**
     * Cursor with RecipeItem data, filled by mLoaderDb
     */
    private Cursor mCursor;
    /**
     * Context of current activity
     */
    private Context mContext;
    /**
     * Retrofit network loader object
     */
    private Retrofit mRetrofit;
    /**
     * Retrofit API interface callback object
     */
    private IRetrofitAPI mRetrofitAPI;
    /**
     * The flag is true if the smallest screen width greater or even 550dp (true)
     */
    private boolean mIsWide;
    /**
     * The flag is true it Timber.Tree is exists
     */
    public static boolean mIsTimber;
    /**
     * The WidgetID from input Intent object
     */
    private String mWidgetId;
    /**
     * The RecipeItemID from input Intent object
     */
    private String mRecipeId;

    /**
     * The flag is true if warning was showed
     */
    private boolean mIsErrorShowed;
    /**
     * Preference flag, is true if load thumbnails enabled
     */
    private boolean mIsLoadImages;
    /**
     * Preference flag, is true if reload recipes every time at start.
     * When this flag is false, reload timeout is 24 hours.
     * The timeout is set in resources.
     */
    private boolean mIsReloadEnabled;
    /**
     * Preference flag, is true if warning show is enabled
     */
    private boolean mIsShowWarning;

    /**
     * Creates main view of current activity
     * Setup actionBar home button with custom icon.
     * Setup Timber.Tree if not exists.
     * <p>
     * Loads Preferences with loadPreference() method.
     * Preference parameters are: mIsLoadImages, mIsReloadEnabled, mIsShowWarning.
     * <p>
     * Extracts  mWidgetId from input Intent bundle object
     * <p>
     * Set display of RecyclerView GridLayout parameters mSpan and mSpanHeight
     * Setup RecyclerView with GridLayout
     * <p>
     * Starts database loader mLoaderDb, AsyncTask loader, which returns Cursor object
     * Start Retrofit Loader if last time was 24hrs ago or mIsReloadEnabled is true
     * Setup System.Visibility Flags to FULLSCREEN mode
     *
     * @param savedInstanceState Bundle  with instance parameters.
     *                           Bundle parameters: <br>
     *                           mIsErrorShowed     is true if warning showed<br>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);
        mRootView = findViewById(R.id.fragment_container);
        mContext = this;

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);  // обязательно без Manifest.PARENT
            actionBar.setHomeAsUpIndicator(R.drawable.ic_home_white_24dp);
        }

// log
        if (!mIsTimber) {
            Timber.plant(new Timber.DebugTree());
            mIsTimber = true;
        }
// preferences
        loadPreferences();  // mIsLoadImages, mIsReloadEnabled, mIsShowWarning, mReloadLastTime

// intent from widget
        Intent intent = getIntent();
        mWidgetId = "";
        if (intent != null && intent.hasExtra(BUNDLE_WIDGET_INTENT)) {
            Bundle args = intent.getBundleExtra(BUNDLE_WIDGET_INTENT);
            mWidgetId = args.getString(WIDGET_WIDGET_ID, "");
        }
// saveInstance
        if (savedInstanceState != null) {  // if repeated session but no connection before ==> load data
            mIsErrorShowed = savedInstanceState.getBoolean(BUNDLE_ERROR_CONNECTION, false);
        } else {
            mIsErrorShowed = false;
        }

// display parameters
        setDisplayMetrics();        // mSpan, mSpanHeight, mIsWide

        mRecyclerView = (RecyclerView) findViewById(R.id.fc_recycler);
        GridLayoutManager layoutManager = new GridLayoutManager(this, mSpan);
        mRecyclerView.setLayoutManager(layoutManager);                          // connect to LayoutManager
        mRecyclerView.setHasFixedSize(true);                                    // item size fixed

        mRecyclerAdapter = new MainAdapter(mContext, this, mSpanHeight);        //context  and data
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mErrorMessage = (TextView) findViewById(R.id.error_message);

// loaders
        mLoaderDb = new LoaderDb(this, this);
        getSupportLoaderManager().initLoader(LOADER_RECIPES_DB_ID, null, mLoaderDb); // empty bundle FFU

// reload from internet
        if (savedInstanceState == null && isReloadTimeout()) {
            startRetrofitLoader();
        }

// landscape fullscreen support
        mRootView.setSystemUiVisibility(SYSTEM_UI_SHOW_FLAGS);
    }

    /**
     * Setup items of options menu with Preferences values
     *
     * @param menu Menu object
     * @return boolean value
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        menu.findItem(R.id.item_load).setChecked(mIsLoadImages);
        menu.findItem(R.id.item_reload).setChecked(mIsReloadEnabled);
        menu.findItem(R.id.item_show).setChecked(mIsShowWarning);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Processes Home and options menu items clicks.
     * When home selected, all fragments cleared from stack.
     * When isLoadImages option clicked, the state immediately saved to Preferences,
     * because DetailActivity and fragments use this value.
     * Other options are saved on exit of application.
     *
     * @param item MenuItem object that was selected
     * @return true if item was processed
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                FragmentManager fragmentManager = getSupportFragmentManager();
                if (mIsWide) {
                    fragmentManager.popBackStack(FRAGMENT_PLAYER_NAME, POP_BACK_STACK_INCLUSIVE);
                }
                fragmentManager.popBackStack(FRAGMENT_ERROR_NAME, POP_BACK_STACK_INCLUSIVE);
                showResult();
                onBackPressed();
                return true;

            case R.id.item_load:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                mIsLoadImages = item.isChecked();
                saveLoadImagePreference();
                return true;

            case R.id.item_reload:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                mIsReloadEnabled = item.isChecked();
                return true;

            case R.id.item_show:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                mIsShowWarning = item.isChecked();
                return true;
            default:
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Resume activity and restart database loader with mLoaderDb object
     */
    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(LOADER_RECIPES_DB_ID, null, mLoaderDb);
    }

    /**
     * Destroy activity and saves to Preferences
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        savePreferences();
    }

    /**
     * Saves Instance parameters to Bundle object.
     * Saved parameters: <br>
     * mIsErrorShowed       if warning showed
     *
     * @param outState Bundle storage for parameters
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(BUNDLE_ERROR_CONNECTION, mIsErrorShowed);
    }

    /**
     * Callback method from RecyclerView HolderView.onClick() method
     * Creates Intent to DetailActivity, attach bundle with RecipeId and WidgetID parameters
     * Starts new Activity
     *
     * @param position int position of item that was selected
     */

    @Override
    public void onCallback(int position) {
        if (mCursor == null) {
            return;
        }
        showProgress();
        mRecipeId = "";
        try {
            mCursor.moveToPosition(position);
            mRecipeId = Integer.toString(mCursor.getInt(mCursor.getColumnIndex(COLUMN_RECIPE_ID)));
        } catch (Exception e) {
            Timber.d(e.getMessage());
        }

        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        Bundle detailArgs = new Bundle();
        detailArgs.putString(WIDGET_RECIPE_ID, mRecipeId);   // to get ""
        detailArgs.putString(WIDGET_WIDGET_ID, mWidgetId);  // to get ""

        intent.putExtra(BUNDLE_DETAIL_INTENT, detailArgs);
        startActivity(intent);
    }

    /**
     * Shows ProgressBar View object
     */
    private void showProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    /**
     * Shows FragmentError dialog if database is empty and connection is absent
     * Creates Fragment Error object which extends DialogFragment class
     * Clear stack of fragment from previous versions of this type of objects
     * Runs FragmentError object
     */
    private void showErrorDialog() {
        FragmentError fragmentError = new FragmentError();
        fragmentError.setStyle(R.style.dialog_title_style, R.style.CustomDialog);
        fragmentError.setCallback(this);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack(FRAGMENT_ERROR_NAME, POP_BACK_STACK_INCLUSIVE);
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(fragmentError, FRAGMENT_ERROR_TAG);
        ft.addToBackStack(FRAGMENT_ERROR_NAME);
        ft.commit();
    }

    /**
     * Shows FragmentError Dialog in background mode
     * This method called from onFinishLoader() method,
     * it requires to start Fragment activities in background
     */

    private void showErrorHandler() {
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_ERROR_ID) {
                    showResult();
                    showErrorDialog();
                }
            }
        };
        handler.sendEmptyMessage(MESSAGE_ERROR_ID);
    }

    /**
     * Shows error message when button CLOSE clicked in FrameError dialog. Not used.
     */
    @Override
    public void showError() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.INVISIBLE);
    }

    /**
     * Hides ProgressBar and ErrorMessage Views
     */
    private void showResult() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.INVISIBLE);
    }

    /**
     * Retrofit Loader data from network. Uses GSon library
     * for parsing JSON string data.
     * When finished updates database with bulkInsert() method.
     * Saves current time in seconds to lastTime Preference
     */
    private void startRetrofitLoader() {
        if (!isOnline(mContext)) {
            return;
        }
        showProgress();
// setup Retrofit
        mRetrofit = new Retrofit.Builder()
                .baseUrl(RECIPES_BASE) //Базовая часть адреса
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mRetrofitAPI = mRetrofit.create(IRetrofitAPI.class);
        mRetrofitAPI.getData().enqueue(new Callback<List<RecipeItem>>() {
            @Override
            public void onResponse(Call<List<RecipeItem>> call, Response<List<RecipeItem>> response) {
                if (response.body() == null) {
                    showResult();
                    return;
                }
                List<RecipeItem> list = response.body();
                Iterator<RecipeItem> it = list.iterator();
                while (it.hasNext()) {
                    if (it.next() == null) {
                        it.remove();
                    }
                }
                RecipeUtils.bulkInsertBackground(mContext.getContentResolver(),
                        getSupportLoaderManager(), list, mLoaderDb);
                showResult();
                saveReLoadTimePreference();
            }

            @Override
            public void onFailure(Call<List<RecipeItem>> call, Throwable t) {
                Timber.d(t.getMessage());
                showResult();
            }
        });
    }

    /**
     * Callback method of mLoaderDb object
     * Checks if cursor is empty and starts Retrofit downloader.
     * Retrofit restart mLoaderDb, which in turn calls this  method again.
     * If cursor is empty and no connection the FragmentError dialog is started.
     * When cursor arrived with data it passed to RecyclerView Adapter.
     * The mCursor holds cursor object.
     *
     * @param cursor Cursor input data object with RecipeItem data
     */
    @Override
    public void onComplete(Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0) {   // нет адаптера выходим
            if (!isOnline(mContext)) showErrorHandler();
            else startRetrofitLoader();
            return;
        }
        showResult(); // только после загрузки базы данных
        if (!isOnline(mContext) && !mIsErrorShowed && mIsShowWarning) {  // for the first one only
            Snackbar.make(mRootView, getString(R.string.message_error), Snackbar.LENGTH_SHORT).show();
            Timber.d(getString(R.string.message_error));
            mIsErrorShowed = true;
        }
        cursor.moveToFirst();
        mRecyclerAdapter.swapCursor(cursor);
        mCursor = cursor;

    }

    /**
     * Callback method of mLoaderDb object
     * Resets Cursor object
     */

    @Override
    public void onReset() {

    }

    /**
     * Sets screen parameters for RecyclerView mSpan, mSpanHeight
     */
    private void setDisplayMetrics() {
        DisplayMetrics dp = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dp);
//        boolean isLand = dp.widthPixels > dp.heightPixels;
        boolean isLand = getResources().getBoolean(R.bool.is_land);
        mIsWide = getResources().getBoolean(R.bool.is_wide);

        double width = dp.widthPixels / dp.density;

        if (!isLand) {
            mSpan = 1;
            if (width >= HIGH_WIDTH_PORTRAIT) {
                mSpan = (int) Math.round(width / HIGH_SCALE_PORTRAIT);
                mSpanHeight = (int) (dp.widthPixels / mSpan / SCREEN_RATIO);
            } else {
                mSpan = (int) Math.round(width / LOW_SCALE_PORTRAIT);
                mSpanHeight = (int) (dp.widthPixels / mSpan / SCREEN_RATIO);
            }
        } else {
            if (width >= HIGH_WIDTH_LANDSCAPE) {
                mSpan = (int) Math.round(width / HIGH_SCALE_LANDSCAPE);
                mSpanHeight = (int) (dp.widthPixels / mSpan / SCREEN_RATIO);
            } else {
                mSpan = (int) Math.round(width / LOW_SCALE_LANDSCAPE);
                mSpanHeight = (int) (dp.widthPixels / mSpan / SCREEN_RATIO);
            }
        }

        if (mSpan < MIN_SPAN) mSpan = MIN_SPAN;
        if (mSpan > MAX_SPAN) mSpan = MAX_SPAN;
        if (mSpanHeight < MIN_HEIGHT) mSpanHeight = MIN_HEIGHT;

//        if (!isLand) {
//            mIsWide = dp.widthPixels / dp.density >= MIN_WIDTH_WIDE_SCREEN;
//        } else {
//            mIsWide = dp.heightPixels / dp.density >= MIN_WIDTH_WIDE_SCREEN;
//        }


    }

    /**
     * Load Preferences
     * mIsLoadImages  flag is true if load thumbnails images is enabled
     * mIsReloadEnabled flag is true if load data from the internet performed every time at start
     * mIsShowWarning flag is true if sho warnings is enabled
     * lastTime       int value, holds the time in seconds when retrofit downloaded data
     * delayTime       int value, the minimum time between two downloads
     */
    private void loadPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mIsLoadImages = sharedPreferences.getBoolean(getString(R.string.pref_load_images_key),
                getResources().getBoolean(R.bool.pref_load_images_default));

        mIsReloadEnabled = sharedPreferences.getBoolean(getString(R.string.pref_reload_recipes_key),
                getResources().getBoolean(R.bool.pref_reload_recipes_default));

        mIsShowWarning = sharedPreferences.getBoolean(getString(R.string.pref_show_warnings_key),
                getResources().getBoolean(R.bool.pref_show_warning_default));

    }

    /**
     * Returns true if more than 24hrs past from last download or if mIsReloadEnabled is true
     * Checks lastTime preference, compares it with current time and returns the result.
     *
     * @return boolean the result of comparison current time and lastTime preference
     */
    private boolean isReloadTimeout() {
        if (mIsReloadEnabled) return true;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        int lastTimeSec = sharedPreferences.getInt(getString(R.string.pref_reload_time_key),
                getResources().getInteger(R.integer.pref_reload_time_default));
        long lastTime = TimeUnit.SECONDS.toMillis(lastTimeSec);

        int delayTimeHr = sharedPreferences.getInt(getString(R.string.pref_reload_delay_key),
                getResources().getInteger(R.integer.pref_reload_delay_default));

        long delayTime = TimeUnit.HOURS.toMillis(delayTimeHr);

        return (System.currentTimeMillis() - lastTime) > delayTime;
    }

    /**
     * Saves mIsLoadImages flag to Preferences
     * Used by onItemOptions() method when item_load selected
     */
    private void saveLoadImagePreference() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(getString(R.string.pref_load_images_key), mIsLoadImages);
        editor.apply();
    }

    /**
     * Saves mIsLoadImages flag to Preferences
     * Used by Retrofit downloader to store time when data was loaded
     */
    private void saveReLoadTimePreference() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int currentTimeSec = (int) TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        editor.putInt(getString(R.string.pref_reload_time_key), currentTimeSec);
        editor.apply();
    }

    /**
     *  Saves all preferences to Preferences
     */
    private void savePreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(getString(R.string.pref_load_images_key), mIsLoadImages);
        editor.putBoolean(getString(R.string.pref_reload_recipes_key), mIsReloadEnabled);
        editor.putBoolean(getString(R.string.pref_show_warnings_key), mIsShowWarning);
        editor.apply();
    }

    /**
     *  Returns Cursor object
     *  Used by BackingAppTest class
     *
     * @return  Cursor object
     */
    public Cursor getCursor() {
        return mCursor;
    }


}
