package ru.vpcb.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Iterator;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
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
import static ru.vpcb.bakingapp.data.RecipeContract.RecipeEntry.COLUMN_RECIPE_VALUE;
import static ru.vpcb.bakingapp.utils.Constants.BUNDLE_DETAIL_INTENT;
import static ru.vpcb.bakingapp.utils.Constants.HIGH_SCALE_LANDSCAPE;
import static ru.vpcb.bakingapp.utils.Constants.HIGH_SCALE_PORTRAIT;
import static ru.vpcb.bakingapp.utils.Constants.HIGH_WIDTH_LANDSCAPE;
import static ru.vpcb.bakingapp.utils.Constants.HIGH_WIDTH_PORTRAIT;
import static ru.vpcb.bakingapp.utils.Constants.LOADER_RECIPES_DB_ID;
import static ru.vpcb.bakingapp.utils.Constants.LOW_SCALE_LANDSCAPE;
import static ru.vpcb.bakingapp.utils.Constants.LOW_SCALE_PORTRAIT;
import static ru.vpcb.bakingapp.utils.Constants.MAX_SPAN;
import static ru.vpcb.bakingapp.utils.Constants.MIN_HEIGHT;
import static ru.vpcb.bakingapp.utils.Constants.MIN_SPAN;
import static ru.vpcb.bakingapp.utils.Constants.MIN_WIDTH_WIDE_SCREEN;
import static ru.vpcb.bakingapp.utils.Constants.RECIPES_BASE;
import static ru.vpcb.bakingapp.utils.Constants.RECIPE_POSITION;
import static ru.vpcb.bakingapp.utils.Constants.SCREEN_RATIO;
import static ru.vpcb.bakingapp.utils.Constants.SYSTEM_UI_SHOW_FLAGS;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);
        mRootView = findViewById(R.id.fragment_container);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);  // обязательно без Manifest.PARENT
            actionBar.setHomeAsUpIndicator(R.drawable.ic_home_white_24dp);
        }

// oNnCreateView
        mContext = this;
        if (!mIsTimber) {
            Timber.plant(new Timber.DebugTree());
            mIsTimber = true;
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.fc_recycler);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        setDisplayMetrics();
        GridLayoutManager layoutManager = new GridLayoutManager(this, mSpan);
        mRecyclerView.setLayoutManager(layoutManager);                          // connect to LayoutManager
        mRecyclerView.setHasFixedSize(true);                                    // item size fixed

        mRecyclerAdapter = new MainAdapter(mContext, this, mSpanHeight);                     //context  and data
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mErrorMessage = (TextView) findViewById(R.id.error_message);

// loaders
        if (!isOnline(mContext)) {
            showError();
        }

        mRootView.setSystemUiVisibility(SYSTEM_UI_SHOW_FLAGS);

        DisplayMetrics dp = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dp);
        if (dp.heightPixels < dp.widthPixels) {
            mIsWide = dp.widthPixels / dp.density >= MIN_WIDTH_WIDE_SCREEN;
        } else {
            mIsWide = dp.heightPixels / dp.density >= MIN_WIDTH_WIDE_SCREEN;
        }

        mLoaderDb = new LoaderDb(this, this);
        getSupportLoaderManager().initLoader(LOADER_RECIPES_DB_ID, null, mLoaderDb); // empty bundle FFU
        if (savedInstanceState == null) {
            startRetrofitLoader();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            hideProgress();
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(LOADER_RECIPES_DB_ID, null, mLoaderDb);
    }


    @Override
    public void onCallback(int position) {
        if (mCursor == null) {
            return;
        }
        showProgress();
        mCursor.moveToPosition(position);
        String recipeJson = mCursor.getString(mCursor.getColumnIndex(COLUMN_RECIPE_VALUE));

        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        Bundle detailArgs = new Bundle();
        detailArgs.putString(RECIPE_POSITION, recipeJson);
        intent.putExtra(BUNDLE_DETAIL_INTENT, detailArgs);
        startActivity(intent);


    }


    private void showProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    private void showError() {
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
        boolean isLogging = true;
        if (isLogging) {
//logging
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);     // set your desired log level  NONE, BASIC, HEADERS, BODY
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(logging);  // <-- this is the important line!

            mRetrofit = new Retrofit.Builder()   // add your other interceptors …
                    .baseUrl(RECIPES_BASE)       // add logging as last interceptor
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();

        } else {
// no logging
            mRetrofit = new Retrofit.Builder()
                    .baseUrl(RECIPES_BASE) //Базовая часть адреса
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        mRetrofitAPI = mRetrofit.create(IRetrofitAPI.class);


        mRetrofitAPI.getData(null).enqueue(new Callback<List<RecipeItem>>() {
            @Override
            public void onResponse(Call<List<RecipeItem>> call, Response<List<RecipeItem>> response) {
                if (response.body() == null) {
                    showError();
                    return;
                }

                List<RecipeItem> list = response.body();
                Iterator<RecipeItem> it = list.iterator();
                while (it.hasNext()) {
                    if (it.next() == null) {
                        it.remove();
                    }
                }
// test!!!
                RecipeData.addImages(list);
                RecipeData.bulkInsertBackground(mContext.getContentResolver(),
                        getSupportLoaderManager(), list, mLoaderDb);
                showResult();
            }
            @Override
            public void onFailure(Call<List<RecipeItem>> call, Throwable t) {
                Timber.d(t.getMessage());
                showError();
            }
        });
    }


    @Override
    public void onComplete(Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0 || mRecyclerAdapter == null) {   // нет адаптера выходим
            return;
        }
        showResult(); // только после загрузки базы данных
        if (!isOnline(mContext)) {
            Snackbar.make(mRootView, getString(R.string.message_error), Snackbar.LENGTH_LONG).show();
            Timber.d(getString(R.string.message_error));
        }
        cursor.moveToFirst();
        mRecyclerAdapter.swapCursor(cursor);
        mCursor = cursor;
        showResult();
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

    }

    private boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}
