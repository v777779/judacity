package ru.vpcb.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.vpcb.bakingapp.data.IRetrofitAPI;
import ru.vpcb.bakingapp.data.LoaderDb;
import ru.vpcb.bakingapp.data.RecipeItem;
import ru.vpcb.bakingapp.utils.RecipeData;
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

public class MainActivity extends AppCompatActivity implements IFragmentHelper,
        LoaderDb.ICallbackDb {

    private RecyclerView mRecyclerView;
    private MainAdapter mRecyclerAdapter;
    private ProgressBar mProgressBar;
    private TextView mErrorMessage;
    private View mRootView;


    private LoaderDb mLoaderDb;
    private int mSpan;
    private int mSpanHeight;
    private Cursor mCursor;
    private Context mContext;
    private Retrofit mRetrofit;
    private IRetrofitAPI mRetrofitAPI;
    private boolean mIsWide;
    public static boolean mIsTimber;
    private String mWidgetId;
    private String mRecipeId;
    private boolean mIsLoadImages;
    private boolean mIsSaveInstance;
    private boolean mPreviousConnection;
    private boolean mIsErrorShowed;


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

// oNnCreateView
        if (!mIsTimber) {
            Timber.plant(new Timber.DebugTree());
            mIsTimber = true;
        }
// preferences
        mIsLoadImages = getLoadPreference(mContext);


// intent from widget
        Intent intent = getIntent();
        mWidgetId = "";
        if (intent != null && intent.hasExtra(BUNDLE_WIDGET_INTENT)) {
            Bundle args = intent.getBundleExtra(BUNDLE_WIDGET_INTENT);
            mWidgetId = args.getString(WIDGET_WIDGET_ID, "");
        }
// saveInstance
        mIsSaveInstance = savedInstanceState != null;
        if (savedInstanceState != null) {  // if repeated session but no connection before ==> load data
            mPreviousConnection = savedInstanceState.getBoolean(BUNDLE_PREVIOUS_CONNECTION, false);
            mIsErrorShowed = savedInstanceState.getBoolean(BUNDLE_ERROR_CONNECTION, false);
        } else {
            mPreviousConnection = false;
            mIsErrorShowed = false;
        }

// display parameters
        setDisplayMetrics();        //mSpan, mSpanHeight, mIsWide

        mRecyclerView = (RecyclerView) findViewById(R.id.fc_recycler);
        GridLayoutManager layoutManager = new GridLayoutManager(this, mSpan);
        mRecyclerView.setLayoutManager(layoutManager);                          // connect to LayoutManager
        mRecyclerView.setHasFixedSize(true);                                    // item size fixed

        mRecyclerAdapter = new MainAdapter(mContext, this, mSpanHeight);        //context  and data
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mErrorMessage = (TextView) findViewById(R.id.error_message);

//loaders
        mLoaderDb = new LoaderDb(this, this);
        getSupportLoaderManager().initLoader(LOADER_RECIPES_DB_ID, null, mLoaderDb); // empty bundle FFU
        if (!mPreviousConnection) {
//            startRetrofitLoader();
        }

// landscape fullscreen support
        mRootView.setSystemUiVisibility(SYSTEM_UI_SHOW_FLAGS);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem menuItemLoad = menu.findItem(R.id.item_load);
        MenuItem menuItemNoLoad = menu.findItem(R.id.item_no_load);
        if (mIsLoadImages) {
            menuItemLoad.setChecked(true);
        } else {
            menuItemNoLoad.setChecked(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

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
                setLoadPreference(mContext, mIsLoadImages);
                return true;
            case R.id.item_no_load:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                mIsLoadImages = !item.isChecked();
                setLoadPreference(mContext, mIsLoadImages);
                return true;
            default:
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(LOADER_RECIPES_DB_ID, null, mLoaderDb);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setLoadPreference(mContext, mIsLoadImages);  // for the first time
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(BUNDLE_PREVIOUS_CONNECTION, isOnline(this));
        outState.putBoolean(BUNDLE_ERROR_CONNECTION, mIsErrorShowed);


    }

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


    private void showProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

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

    @Override
    public void showError() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.VISIBLE);
    }

    private void showResult() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.INVISIBLE);
    }


    private void startRetrofitLoader() {
        showProgress();
        if (!isOnline(mContext)) {
            return;
        }
// setup Retrofit
        mRetrofit = new Retrofit.Builder()
                .baseUrl(RECIPES_BASE) //Базовая часть адреса
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mRetrofitAPI = mRetrofit.create(IRetrofitAPI.class);
        mRetrofitAPI.getData(null).enqueue(new Callback<List<RecipeItem>>() {
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
                RecipeData.bulkInsertBackground(mContext.getContentResolver(),
                        getSupportLoaderManager(), list, mLoaderDb);
                showResult();
            }

            @Override
            public void onFailure(Call<List<RecipeItem>> call, Throwable t) {
                Timber.d(t.getMessage());
                showResult();
            }
        });
    }


    @Override
    public void onComplete(Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0) {   // нет адаптера выходим
            if (!isOnline(mContext)) showErrorHandler();
            else startRetrofitLoader();
            return;
        }
        showResult(); // только после загрузки базы данных
        if (!isOnline(mContext) && !mIsErrorShowed) {  // for the first one only
            Snackbar.make(mRootView, getString(R.string.message_error), Snackbar.LENGTH_LONG).show();
            Timber.d(getString(R.string.message_error));
            mIsErrorShowed = true;
        }
        cursor.moveToFirst();
        mRecyclerAdapter.swapCursor(cursor);
        mCursor = cursor;

    }

    @Override
    public void onReset() {

    }


    private void setDisplayMetrics() {
        DisplayMetrics dp = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dp);
        boolean isLand = dp.widthPixels > dp.heightPixels;
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

        if (!isLand) {
            mIsWide = dp.widthPixels / dp.density >= MIN_WIDTH_WIDE_SCREEN;
        } else {
            mIsWide = dp.heightPixels / dp.density >= MIN_WIDTH_WIDE_SCREEN;
        }


    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static boolean getLoadPreference(Context context) {
        boolean isLoadImages = false;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        isLoadImages = sharedPreferences.getBoolean(context.getString(R.string.load_images_key),
                context.getResources().getBoolean(R.bool.load_images_default));

        return isLoadImages;
    }

    public static void setLoadPreference(Context context, boolean isImageLoad) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(context.getString(R.string.load_images_key), isImageLoad);
        editor.apply();
    }


}
