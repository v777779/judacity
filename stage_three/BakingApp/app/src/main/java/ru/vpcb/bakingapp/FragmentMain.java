package ru.vpcb.bakingapp;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
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
import ru.vpcb.bakingapp.utils.FragmentData;
import ru.vpcb.bakingapp.utils.RecipeData;
import timber.log.Timber;

import static ru.vpcb.bakingapp.data.RecipeContract.RecipeEntry.COLUMN_RECIPE_VALUE;
import static ru.vpcb.bakingapp.utils.Constants.BUNDLE_LOADER_STRING_ID;
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
import static ru.vpcb.bakingapp.utils.Constants.RECIPES_BASE;
import static ru.vpcb.bakingapp.utils.Constants.RECIPE_POSITION;
import static ru.vpcb.bakingapp.utils.Constants.SCREEN_RATIO;
import static ru.vpcb.bakingapp.utils.Constants.SYSTEM_UI_SHOW_FLAGS;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 15-Nov-17
 * Email: vadim.v.voronov@gmail.com
 */

public class FragmentMain extends Fragment implements IFragmentHelper,
        LoaderDb.ICallbackDb {


    private RecyclerView mRecyclerView;
    private FragmentMainAdapter mRecyclerAdapter;
    private ProgressBar mProgressBar;
    private TextView mErrorMessage;

    private LoaderDb mLoaderDb;
    private int mSpan;
    private int mSpanHeight;
    private Cursor mCursor;
    private Context mContext;
    private Retrofit mRetrofit;
    private IRetrofitAPI mRetrofitAPI;


    public FragmentMain() {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        mLoaderDb = new LoaderDb(getContext(), this);
        getLoaderManager().initLoader(LOADER_RECIPES_DB_ID, null, mLoaderDb); // empty bundle FFU
        if (savedInstanceState == null) {
            startRetrofitLoader();
        }
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {


        final View rootView = inflater.inflate(R.layout.fragment_main_recycler, container, false);
        // load mock data

        mRecyclerView = rootView.findViewById(R.id.fc_recycler);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        setDisplayMetrics();
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), mSpan);
        mRecyclerView.setLayoutManager(layoutManager);                          // connect to LayoutManager
        mRecyclerView.setHasFixedSize(true);                                    // item size fixed

        mRecyclerAdapter = new FragmentMainAdapter(mContext, this);     //context  and data
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mProgressBar = rootView.findViewById(R.id.progress_bar);
        mErrorMessage = rootView.findViewById(R.id.error_message);

// loaders
        if (!isOnline(getContext())) {
            showError();
        }


        rootView.setSystemUiVisibility(SYSTEM_UI_SHOW_FLAGS);
        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }


    @Override
    public void onCallback(int position) {
        if (mCursor == null) {
            return;
        }
        showProgress();
        mCursor.moveToPosition(position);
        String recipeJson = mCursor.getString(mCursor.getColumnIndex(COLUMN_RECIPE_VALUE));


        FragmentDetail detailFragment = new FragmentDetail();
        Bundle detailArgs = new Bundle();
        detailArgs.putString(RECIPE_POSITION, recipeJson);
        detailFragment.setArguments(detailArgs);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit();

    }


    @Override
    public int getSpanHeight() {
        return mSpanHeight;
    }


    public void showProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

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
                FragmentData.addImages(list);
                RecipeData.bulkInsertBackground(mContext.getContentResolver(), getLoaderManager(), list, mLoaderDb);
                showResult();
            }

            @Override
            public void onFailure(Call<List<RecipeItem>> call, Throwable t) {
                Timber.d("Retrofit load error: " + t.getMessage());
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
        if (!isOnline(getContext())) {
            Snackbar.make(getView(), "No connection. Local data used", Snackbar.LENGTH_LONG).show();
            Timber.d("No connection. Local data used");
        }

        mRecyclerAdapter.swapCursor(cursor);
        mCursor = cursor;
    }

    @Override
    public void onReset() {

    }


    private void setDisplayMetrics() {
        DisplayMetrics dp = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dp);
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
