package ru.vpcb.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.Gson;

import ru.vpcb.bakingapp.data.LoaderDb;
import ru.vpcb.bakingapp.data.RecipeItem;
import timber.log.Timber;

import static android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;

import static ru.vpcb.bakingapp.utils.RecipeUtils.isOnline;
import static ru.vpcb.bakingapp.data.RecipeContract.RecipeEntry.COLUMN_RECIPE_VALUE;
import static ru.vpcb.bakingapp.utils.Constants.BUNDLE_DETAIL_EXPANDED;
import static ru.vpcb.bakingapp.utils.Constants.BUNDLE_DETAIL_INTENT;
import static ru.vpcb.bakingapp.utils.Constants.BUNDLE_DETAIL_POSITION;
import static ru.vpcb.bakingapp.utils.Constants.BUNDLE_DETAIL_WIDGET_FILLED;
import static ru.vpcb.bakingapp.utils.Constants.BUNDLE_ERROR_CONNECTION;
import static ru.vpcb.bakingapp.utils.Constants.FRAGMENT_ERROR_NAME;
import static ru.vpcb.bakingapp.utils.Constants.FRAGMENT_ERROR_TAG;
import static ru.vpcb.bakingapp.utils.Constants.FRAGMENT_PLAYER_NAME;
import static ru.vpcb.bakingapp.utils.Constants.LOADER_RECIPES_DB_ID;
import static ru.vpcb.bakingapp.utils.Constants.MESSAGE_ERROR_ID;
import static ru.vpcb.bakingapp.utils.Constants.MESSAGE_PLAYER_ID;
import static ru.vpcb.bakingapp.utils.Constants.RECIPE_POSITION;
import static ru.vpcb.bakingapp.utils.Constants.RECIPE_STEP_POSITION;
import static ru.vpcb.bakingapp.utils.Constants.STEP_DEFAULT_POSITION;
import static ru.vpcb.bakingapp.utils.Constants.SYSTEM_UI_SHOW_FLAGS;
import static ru.vpcb.bakingapp.utils.Constants.WIDGET_RECIPE_ID;
import static ru.vpcb.bakingapp.utils.Constants.WIDGET_WIDGET_ID;
import static ru.vpcb.bakingapp.widget.RecipeWidgetService.startFillWidgetAction;

/**
 * DetailActivity class
 * Creates RecyclerView with information about Steps and Ingredients
 */
public class DetailActivity extends AppCompatActivity implements IFragmentHelper, LoaderDb.ICallbackDb {

    /**
     * RecyclerView of Steps list with LinearLayout
     */
    private RecyclerView mRecyclerView;
    /**
     * RecyclerView Adapter of Steps list
     */
    private DetailAdapter mRecyclerAdapter;
    /**
     * Step Position in Recipe.Steps list
     */
    private int mPosition;
    /**
     * The flag if screen smallest wide greater or even 550dp (true) or less (false)
     */
    private boolean mIsWide;
    /**
     * The RecipeItem parent object
     */
    private RecipeItem mRecipeItem;
    /**
     * Context of current activity
     */
    private Context mContext;
    /**
     * The flag is true if the list of Ingredients in RecyclerView is expanded
     */
    private boolean mIsExpanded;
    /**
     * Root View of DetailActivity layout
     */
    private View mRootView;
    /**
     * The WidgetID from input Intent object
     */
    private String mWidgetId;
    /**
     * The RecipeItemID from input Intent object
     */
    private String mRecipeId;
    /**
     * The flag is true if widget filled
     */
    private boolean mIsWidgetFilled;
    /**
     * Loader database object, loads data into mCursor object
     */
    private LoaderDb mLoaderDb;
    /**
     * Cursor with RecipeItem data, filled by mLoaderDb
     */
    private Cursor mCursor;
    /**
     * The flag is true if savedInstance is not null
     */
    private boolean mIsSavedInstance;
    /**
     * The flag is true if warning already showed
     */
    private boolean mIsErrorShowed;
    /**
     * Preference flag, is true if load thumbnails enabled
     */
    private boolean mIsLoadImages;
    /**
     * Preference flag, is true if warning show is enabled
     */
    private boolean mIsShowWarning;

    /**
     * Initializes Detail Activity.
     * Setup actionBar home button with custom icon.
     * Loads Preferences with loadPreference() method.
     * Setup Timber.Tree if not exists.
     * Extracts  mWidgetId and mRecipeId from input Intent bundle,  calls finish() if failed.
     * Extracts parameters from savedInstance bundle, see teh list of parameters below.
     * Setup RecyclerView with LinearLayout.
     * Shows "Filled widget" button if mIsWidgetFilled true.
     * Start database loader mLoaderDb.
     * Setup System.Visibility Flags to FULLSCREEN mode.
     *
     * @param savedInstanceState Bundle  with instance parameters
     *                           Saved parameters:
     *                           mIsExpanded          if list of ingredients was expanded
     *                           mPosition            position selected RecyclerView Item
     *                           mIsWidgetFilled      if widget filled button pressed
     *                           mIsErrorShowed       if warning showed
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_detail);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);  // обязательно без Manifest.PARENT
            actionBar.setHomeAsUpIndicator(R.drawable.ic_home_white_24dp);
        }

        mContext = this;
        mRootView = findViewById(R.id.fragment_container);
// timber
        if (!MainActivity.mIsTimber) {
            Timber.plant(new Timber.DebugTree());
            MainActivity.mIsTimber = true;
        }
// preferences
        loadPreferences();
// intent
        mRecipeItem = null;
        try {
            Intent intent = getIntent();
            Bundle detailArgs = intent.getBundleExtra(BUNDLE_DETAIL_INTENT);
            mWidgetId = detailArgs.getString(WIDGET_WIDGET_ID, "");
            mRecipeId = detailArgs.getString(WIDGET_RECIPE_ID, "");
        } catch (Exception e) {
            Timber.d(e.getMessage());
            finish();
            return;
        }

// savedInstance
        mIsSavedInstance = savedInstanceState != null;
        if (savedInstanceState != null) {
            mIsExpanded = savedInstanceState.getBoolean(BUNDLE_DETAIL_EXPANDED, false);
            mPosition = savedInstanceState.getInt(BUNDLE_DETAIL_POSITION, STEP_DEFAULT_POSITION);
            mIsWidgetFilled = savedInstanceState.getBoolean(BUNDLE_DETAIL_WIDGET_FILLED, false);
            mIsErrorShowed = savedInstanceState.getBoolean(BUNDLE_ERROR_CONNECTION, false);

        } else {
            mPosition = STEP_DEFAULT_POSITION;
            mIsExpanded = false;
            mIsWidgetFilled = mWidgetId.isEmpty();
            mIsErrorShowed = false;
        }

        mRecyclerView = mRootView.findViewById(R.id.fc_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);


        mRecyclerView.setLayoutManager(layoutManager);                              // connect to LayoutManager
        mRecyclerView.setHasFixedSize(false);                                       // item size fixed
        mRecyclerAdapter = new DetailAdapter(mContext, this, mRecipeItem, mIsLoadImages);      //context  and data
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerAdapter.setExpanded(mIsExpanded);
        mRecyclerView.setHasFixedSize(true);
        mIsWide = getResources().getBoolean(R.bool.is_wide);

        if (!mIsWidgetFilled) {
            mRootView.findViewById(R.id.widget_button).setVisibility(View.VISIBLE);
        } else {
            mRootView.findViewById(R.id.widget_button).setVisibility(View.GONE);
        }

// loader for one recipe
        mLoaderDb = new LoaderDb(this, this, mRecipeId);
        getSupportLoaderManager().initLoader(LOADER_RECIPES_DB_ID, null, mLoaderDb); // empty bundle FFU
        mRootView.setSystemUiVisibility(SYSTEM_UI_SHOW_FLAGS);
    }

    /**
     * Processes Home button click
     * If screen is Wide the finish() called and onBackPressed() in other case
     *
     * @param item MenuItem object whic is selected in options menu
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (mIsWide) {
//                getSupportFragmentManager().popBackStack(FRAGMENT_PLAYER_NAME, POP_BACK_STACK_INCLUSIVE);
                finish();
            } else {
                onBackPressed();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Saves Instance parameters to Bundle object
     * Saved parameters:
     * mIsExpanded          if list of ingredients was expanded
     * mPosition            position selected RecyclerView Item
     * mIsWidgetFilled      if widget filled button pressed
     * mIsErrorShowed       if warning showed
     *
     * @param outState Bundle storage for parameters
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(BUNDLE_DETAIL_EXPANDED, mIsExpanded);
        outState.putInt(BUNDLE_DETAIL_POSITION, mPosition);
        outState.putBoolean(BUNDLE_DETAIL_WIDGET_FILLED, mIsWidgetFilled);
        outState.putBoolean(BUNDLE_ERROR_CONNECTION, mIsErrorShowed);

    }

    /**
     * Resume activity and restart database loader with mLoaderDb object
     */
    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(LOADER_RECIPES_DB_ID, null, mLoaderDb); // empty bundle FFU

    }

    /**
     * Starts RecipeWidgetService with WIDGET_SERVICE_FILL_ACTION to fill widget with current RecipeItem data
     * Shows Snackbar object with message about action
     *
     * @param view View button View object
     */
    public void onClickTapWidget(View view) {
        startFillWidgetAction(mContext, mRecipeId, mWidgetId);
        mRootView.findViewById(R.id.widget_button).setVisibility(View.GONE);
        if (mIsShowWarning) {
            Snackbar.make(mRootView, getString(R.string.widget_list_added), Snackbar.LENGTH_SHORT).show();
//            Toast.makeText(mContext, getString(R.string.widget_list_added), Toast.LENGTH_LONG).show();
        }
        mIsWidgetFilled = true;
    }

    /**
     * Callback method from RecyclerView HolderView.onClick() method
     * Creates new Fragment player activity and run it
     * Uses getFragment() method to create new fragment
     *
     * @param position int position of item that was selected
     */
    @Override
    public void onCallback(int position) {
        mPosition = position;
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentPlayer playerFragment = FragmentPlayer.newInstance(mRecipeItem,mPosition);

        fragmentManager.popBackStack(FRAGMENT_PLAYER_NAME, POP_BACK_STACK_INCLUSIVE);

        if (mIsWide) {
            fragmentManager.beginTransaction()
                    .replace(R.id.fc_p_container, playerFragment)
                    .commit();
        } else {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, playerFragment)
                    .addToBackStack(FRAGMENT_PLAYER_NAME)
                    .commit();
        }
    }

    /**
     * Returns new FragmentPlayer object
     * Creates Bundle with parameters, put it into fragment object
     * Bundle parameters
     * mRecipeItem     RecipeItem object converted to JSON format at RECIPE_POSITION
     * mPosition       position of current Step at RECIPE_STEP_POSITION
     * mIsWide         flag is true if screen is wide
     *
     * @return FragmentPlayer object with bundle of parameters
     */
//    private FragmentPlayer getFragmentPlayer() {
//        FragmentPlayer playerFragment = new FragmentPlayer();
//        Bundle playerArgs = new Bundle();
//        playerArgs.putString(RECIPE_POSITION, new Gson().toJson(mRecipeItem));
//        playerArgs.putInt(RECIPE_STEP_POSITION, mPosition);
//        playerArgs.putBoolean(RECIPE_SCREEN_WIDE, mIsWide);
//        playerFragment.setArguments(playerArgs);
//        return playerFragment;
//    }

    /**
     * Returns RecipeItem object from the first position of input Cursor object
     * Extracts JSON string data, converts them to RecipeItem object
     *
     * @param cursor Cursor input Cursor object with JSON string data
     * @return RecipeItem object or null
     */
    private RecipeItem getRecipeItem(Cursor cursor) {
        RecipeItem recipeItem = null;
        try {
            cursor.moveToFirst();
            String recipeJson = cursor.getString(mCursor.getColumnIndex(COLUMN_RECIPE_VALUE));
            recipeItem = new Gson().fromJson(recipeJson, RecipeItem.class);
        } catch (Exception e) {
            Timber.d(e.getMessage());
        }
        return recipeItem;
    }

    /**
     * Shows FragmentError dialog if database is empty and connection is absent
     * Creates Fragment Error object which extends DialogFragment class
     * Clear stack of fragment from previous versions of this type of objects
     * Runs FragmentError object
     */
    public void showErrorDialog() {
        FragmentError fragmentError = new FragmentError();
        fragmentError.setLayoutId(R.layout.fragment_error_detail);
        fragmentError.setStyle(R.style.dialog_title_style, R.style.CustomDialog);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack(FRAGMENT_ERROR_NAME, POP_BACK_STACK_INCLUSIVE);
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(fragmentError, FRAGMENT_ERROR_TAG);
        ft.addToBackStack(FRAGMENT_ERROR_NAME);
        ft.commit();

    }

    /**
     * Creates and runs FragmentPlayer when data from database is loaded and screen is wide
     * This method called from onComplete() method of mLoaderDb Listener
     */
    private void showPlayerFragment() {
        if (mIsWide && !mIsSavedInstance) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentPlayer playerFragment = FragmentPlayer.newInstance(mRecipeItem,mPosition);
            fragmentManager.beginTransaction()
                    .replace(R.id.fc_p_container, playerFragment)
                    .addToBackStack(FRAGMENT_PLAYER_NAME)
                    .commit();
        }
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
                    showError();
                    showErrorDialog();
                }
            }
        };
        handler.sendEmptyMessage(MESSAGE_ERROR_ID);
    }

    /**
     * Shows FragmentPlayer in background mode
     * This method called from onFinishLoader() method,
     * it requires to start Fragment activities in background
     */
    private void showPlayerHandler() {
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_PLAYER_ID) {
                    showPlayerFragment();
                }
            }
        };
        handler.sendEmptyMessage(MESSAGE_PLAYER_ID);
    }

    /**
     * Shows Snackbar with warning message if preference mIsShowWarnig is enabled and
     * this is the first time, after onCreate()
     */
    public void showError() {
        if (mIsErrorShowed || !mIsShowWarning) {
            return;
        }
        Snackbar.make(mRootView, getString(R.string.message_error), Snackbar.LENGTH_SHORT).show();
        Timber.d(getString(R.string.message_error));
        mIsErrorShowed = true;
    }

    /**
     * Callback method of mLoaderDb object
     * Performs input Cursor object data processing
     * Fills mRecipeItem object and pass it to RecyclerView Adapter
     *
     * @param cursor Cursor input data object with RecipeItem data
     */
    @Override
    public void onComplete(Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0 || mRecyclerAdapter == null) {   // нет адаптера выходим
            showErrorHandler();
            return;
        }
//        showResult(); // только после загрузки базы данных
        if (!isOnline(mContext)) {
            showError();
        }
        mCursor = cursor;
        mRecipeItem = getRecipeItem(mCursor);
        mRecyclerAdapter.swapRecipe(mRecipeItem);
        showPlayerHandler();
    }

    /**
     * Callback method of mLoaderDb object
     * Resets Cursor object
     */
    @Override
    public void onReset() {
    }

    /**
     * Loads Preferences
     * mIsLoadImages    flag if Load of thumbnails images enabled
     * mIsShowWarning   flag if warning show is enabled
     */
    private void loadPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        mIsLoadImages = sharedPreferences.getBoolean(getString(R.string.pref_load_images_key),
                getResources().getBoolean(R.bool.pref_load_images_default));
        mIsShowWarning = sharedPreferences.getBoolean(getString(R.string.pref_show_warnings_key),
                getResources().getBoolean(R.bool.pref_show_warning_default));
    }
}
