package ru.vpcb.rgdownload;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import ru.vpcb.rgdownload.utils.NetworkData;
import ru.vpcb.rgdownload.utils.ParseUtils;
import ru.vpcb.rgdownload.utils.NetworkUtils;
import ru.vpcb.rgdownload.utils.QueryType;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String SIGNATURE = "ru.vpcb.rgdownload";


    private final static float COLUMN_WIDTH_HIGH = 200;
    private final static float DP_HEIGHT_LOW = 480;
    private final static float COLUMN_WIDTH_LOW = 150;
    private final static int MAX_COLUMNS = 6;
    private final static int MIN_COLUMNS = 2;
    private final static int TEMP_MAX_ITEMS = 25;
    private final static int PAGE_FIRST = 1;

    private static final String ASYNC_MESSAGE = "{\"success\": false,  \"status_code\": 402 }";

    private static Random rnd = new Random();
    private MovieAdapter mFlavorAdapter;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private TextView mErrorText;
    private TextView mFirstText;

    private int mSpan;

    private List<MovieItem> mListMovie;
    private List<MovieItem> mPopularMovie;
    private List<MovieItem> mTopRatedMovie;
    private List<MovieItem> mNowdaysMovie;
    private QueryType mQueryMode;
    private List<Integer> mListPage;
    private List<Integer> mListPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSpan = getNumberOfColumns(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mProgressBar = (ProgressBar) findViewById(R.id.main_connect_pb);
        mErrorText = (TextView) findViewById(R.id.main_connect_text);
        mFirstText = (TextView) findViewById(R.id.main_first_text);

        if (savedInstanceState == null || !savedInstanceState.containsKey("movies")) {
            mListMovie = new ArrayList<>();
            mPopularMovie = new ArrayList<>();
            mTopRatedMovie = new ArrayList<>();
            mNowdaysMovie = new ArrayList<>();
            mQueryMode = QueryType.POPULAR;
            mListPage = new ArrayList<>(Arrays.asList(1, 1, 1));
            mListPosition = new ArrayList<>(Arrays.asList(0, 0, 0));

            if (loadInitList()) {
                mPopularMovie = mListMovie;
                loadContent();
            }

        } else {
            showRV();
            mListMovie = savedInstanceState.getParcelableArrayList("movies");
            mPopularMovie = savedInstanceState.getParcelableArrayList("popular");
            mTopRatedMovie = savedInstanceState.getParcelableArrayList("toprated");
            ;
            mNowdaysMovie = savedInstanceState.getParcelableArrayList("nowdays");
            mListPage = savedInstanceState.getIntegerArrayList("pages");
            mQueryMode = QueryType.valueOf(savedInstanceState.getString("querymode"));
            mListPosition = savedInstanceState.getIntegerArrayList("positions");

            loadContent();
        }
    }

    private boolean loadInitList() {
        if (!loadGenre()) {
            Log.v(TAG, "Can't load Genre Map");
            return false;
        }
        mListMovie = loadMovie(mQueryMode, mListPage.get(mQueryMode.ordinal()));       // load page 1
        if (mListMovie == null || mListMovie.size() == 0) {
            return false;
        }
        return true;
    }

    private void loadContent() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        final GridLayoutManager mLayoutManager = new GridLayoutManager(this,
                mSpan,
                GridLayoutManager.VERTICAL,
                false);

        mRecyclerView.setLayoutManager(mLayoutManager);                          // connect to LayoutManager
        mRecyclerView.setHasFixedSize(true);                                     // item size fixed
        mFlavorAdapter = new MovieAdapter(this, mListMovie, mSpan);  //context  and data
        mRecyclerView.setAdapter(mFlavorAdapter);
        mRecyclerView.scrollToPosition(mListPosition.get(mQueryMode.ordinal()));


// scrolled listener
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

//                updatePosition(mLayoutManager.findFirstVisibleItemPosition());
                updatePosition(mLayoutManager.findFirstCompletelyVisibleItemPosition());

                if (dy > 0) {
                    int v = mLayoutManager.getChildCount();
                    int p = mLayoutManager.findFirstVisibleItemPosition();
                    int total = mLayoutManager.getItemCount();

                    if (v + p >= total) {
                        loadMovie();
                    }
                }
            }
        });

    }

    public void updatePosition(int position) {
        if(position < 0) {
            return;
        }
       mListPosition.set(mQueryMode.ordinal(),position);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("movies", new ArrayList<Parcelable>(mListMovie));
        outState.putParcelableArrayList("popular", new ArrayList<Parcelable>(mPopularMovie));
        outState.putParcelableArrayList("toprated", new ArrayList<Parcelable>(mTopRatedMovie));
        outState.putParcelableArrayList("nowdays", new ArrayList<Parcelable>(mNowdaysMovie));
        outState.putIntegerArrayList("pages", new ArrayList<Integer>(mListPage));
        outState.putString("querymode", mQueryMode.toString());
        outState.putIntegerArrayList("positions", new ArrayList<Integer>(mListPosition));


        super.onSaveInstanceState(outState);
    }

    private int getNumberOfColumns(Context context) {
        DisplayMetrics dp = context.getResources().getDisplayMetrics();
        float dpWidth = dp.widthPixels / dp.density;
        int nColumns;
        if (dp.heightPixels <= DP_HEIGHT_LOW) {
            nColumns = (int) (dpWidth / COLUMN_WIDTH_LOW);
        } else {
            nColumns = (int) (dpWidth / COLUMN_WIDTH_HIGH);
        }
        if (nColumns > MAX_COLUMNS) {
            nColumns = MAX_COLUMNS;
        }
        if (nColumns < MIN_COLUMNS) {
            nColumns = MIN_COLUMNS;
        }
        return nColumns;

    }


    private void loadMovie() {
        int page = mListPage.get(mQueryMode.ordinal()) + 1;
        List<MovieItem> list = loadMovie(mQueryMode, page);
        if (list == null || list.size() == 0) {
            return;
        }
        mListPage.set(mQueryMode.ordinal(), page);     // next page loaded
        mListMovie.addAll(list);
        mFlavorAdapter.setSize(mListMovie.size());
        mFlavorAdapter.notifyDataSetChanged();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();
        String s;
        try {

            if (itemThatWasClickedId == R.id.action_search) {
                mQueryMode = QueryType.POPULAR;
                if (mPopularMovie == null || mPopularMovie.size() == 0) {  // first screen
                    if (loadInitList()) {
                        mPopularMovie = mListMovie;
                        loadContent();
                    }
                } else {
                    mListMovie = mPopularMovie;
                    loadContent();
                }
                return true;
            }
            if (itemThatWasClickedId == R.id.item_menu2) {
                mQueryMode = QueryType.NOWDAYS;
                if (mNowdaysMovie == null || mNowdaysMovie.size() == 0) {                    // first screen
                    if (loadInitList()) {
                        mNowdaysMovie = mListMovie;
                        loadContent();
                    }
                } else {
                    mListMovie = mNowdaysMovie;
                    loadContent();

                }
                return true;
            }
            if (itemThatWasClickedId == R.id.item_menu3) {
                mQueryMode = QueryType.TOPRATED;
                if (mTopRatedMovie == null || mTopRatedMovie.size() == 0) {                    // first screen
                    if (loadInitList()) {
                        mTopRatedMovie = mListMovie;
                        loadContent();
                    }
                } else {
                    mListMovie = mTopRatedMovie;
                    loadContent();
                }


                return true;
            }

        } catch (Exception e) {
            s = ASYNC_MESSAGE;
        }

        return super.onOptionsItemSelected(item);
    }


    private boolean loadGenre() {
        if (!ParseUtils.isMapGenreEmpty()) {     // load MapGenre
            return true;
        }
//        if (!isOnline()) {
//            return false;
//        }

        try {
            String s = NetworkUtils.makeSearch(new NetworkData(this, QueryType.GENRES));
            return ParseUtils.setGenres(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    private List<MovieItem> loadMovie(QueryType type, int page) {
//        if (!isOnline()) {
//            return null;
//        }
        try {
            String s = NetworkUtils.makeSearch(new NetworkData(this, type, page));
            return ParseUtils.getPageList(s);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private List<ReviewItem> loadReview(int page, int id) {
//        if (!isOnline()) {
//            return null;
//        }
        try {
            String s = NetworkUtils.makeSearch(new NetworkData(this, QueryType.REVIEW, page, id));
            return ParseUtils.getReviewList(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public void sendIntent(MovieItem movieItem, boolean flag) {
        // load list review here
        List<ReviewItem> listReview = movieItem.getListReview();
        if (listReview == null || listReview.isEmpty()) {
            listReview = loadReview(PAGE_FIRST, movieItem.getId());  // always first page
            movieItem.setListReview(listReview);
        }

        Intent intent = new Intent(this, ChildActivity.class);
        intent.putExtra(MovieItem.class.getCanonicalName(), movieItem);  // как вариант
        if (flag) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        startActivity(intent);
        Log.v(TAG, "sent parcelable movie:" + movieItem.getTitle());
    }


    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void showPB() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        mErrorText.setVisibility(View.INVISIBLE);
        mFirstText.setVisibility(View.INVISIBLE);
    }

    public void showError() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mErrorText.setVisibility(View.VISIBLE);
        mFirstText.setVisibility(View.INVISIBLE);
    }

    public void showRV() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mErrorText.setVisibility(View.INVISIBLE);
        mFirstText.setVisibility(View.INVISIBLE);
    }

    public void showFirst() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mErrorText.setVisibility(View.INVISIBLE);
        mFirstText.setVisibility(View.VISIBLE);
    }


    private void setMobileDataEnabled(Context context, boolean enabled) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        final ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final Class conmanClass = Class.forName(conman.getClass().getName());
        final Field connectivityManagerField = conmanClass.getDeclaredField("mService");
        connectivityManagerField.setAccessible(true);
        final Object connectivityManager = connectivityManagerField.get(conman);
        final Class connectivityManagerClass = Class.forName(connectivityManager.getClass().getName());
        final Method setMobileDataEnabledMethod = connectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
        setMobileDataEnabledMethod.setAccessible(true);

        setMobileDataEnabledMethod.invoke(connectivityManager, enabled);
    }

    private void setMobileDataEnabled2(Context context, boolean enabled) throws Exception {
        final ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final Class conmanClass = Class.forName(conman.getClass().getName());
        final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
        iConnectivityManagerField.setAccessible(true);
        final Object iConnectivityManager = iConnectivityManagerField.get(conman);
        final Class iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
        final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
        setMobileDataEnabledMethod.setAccessible(true);
        setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);
    }
}
