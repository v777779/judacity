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
            mListMovie = null;
            showFirst();
            if (loadInitList()) {
                showRV();
                loadContent();
            }

        } else {
            showRV();
            mListMovie = savedInstanceState.getParcelableArrayList("movies");
            loadContent();
        }
    }

    private boolean loadInitList() {
        if (!loadGenre()) {
            Log.v(TAG, "Can't load Genre Map");
            showError();
            return false;
        }

        mListMovie = loadMovie(QueryType.POPULAR, 1);       // load page 1
        if (mListMovie == null || mListMovie.size() == 0) {
            Log.v(TAG, "Can't load Popular Movie page 1");
            showError();
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

// scrolled listener
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
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


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("movies", new ArrayList<Parcelable>(mListMovie));
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
        if (mFlavorAdapter.getItemCount() < TEMP_MAX_ITEMS) {  // denial of service
            //dummy item
            MovieItem movieItem = mListMovie.get(rnd.nextInt(mListMovie.size()));
            mListMovie.add(movieItem);
            // dummy
            mFlavorAdapter.setSize(mFlavorAdapter.getItemCount() + 1);
            mFlavorAdapter.notifyDataSetChanged();
        }
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
                if (mListMovie == null) {  // first screen
                    showPB();
                    if (!loadGenre()) {
                        Log.v(TAG, "Can't load Genre Map");
                        showError();
                        return true;
                    }
                    // ВНИМАНИЕ ВСТРОИТЬ ЗДЕСЬ ПРОВЕРКУ на наличие Internet
                    mListMovie = loadMovie(QueryType.POPULAR, 1);       // load page 1
                    if (mListMovie == null || mListMovie.size() == 0) {
                        Log.v(TAG, "Can't load Popular Movie page 1");
                        showError();
                        return true;

                    }

                    mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
                    final GridLayoutManager mLayoutManager = new GridLayoutManager(this,
                            mSpan,
                            GridLayoutManager.VERTICAL,
                            false);

                    mRecyclerView.setLayoutManager(mLayoutManager);                          // connect to LayoutManager
                    mRecyclerView.setHasFixedSize(true);                                     // item size fixed
                    mFlavorAdapter = new MovieAdapter(this, mListMovie, mSpan);  //context  and data
                    mRecyclerView.setAdapter(mFlavorAdapter);

// scrolled listener
                    mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
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
                    showRV();
                }

            }
            if (itemThatWasClickedId == R.id.item_menu2) {
                Toast.makeText(this, "NOWDAYS", Toast.LENGTH_SHORT).show();
                return true;
            }
            if (itemThatWasClickedId == R.id.item_menu3) {
//
//                MovieItem movieItem = mListMovie.get(rnd.nextInt(mListMovie.size()));
//                sendIntent(movieItem, true);                    // new task to work with Toast
                Toast.makeText(this, "TOP RATED  Mobile data false", Toast.LENGTH_SHORT).show();
                setMobileDataEnabled2(this, false);


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
        if (!isOnline()) {
            return false;
        }

        try {
            String s = NetworkUtils.makeSearch(new NetworkData(QueryType.GENRES));
            return ParseUtils.setGenres(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    private List<MovieItem> loadMovie(QueryType type, int page) {
        if (!isOnline()) {
            return null;
        }
        try {
            String s = NetworkUtils.makeSearch(new NetworkData(type, page));
            return ParseUtils.getPageList(s);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private List<ReviewItem> loadReview(int page, int id) {
        if (!isOnline()) {
            return null;
        }

        try {
            String s = NetworkUtils.makeSearch(new NetworkData(QueryType.REVIEW, page, id));
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

    private void setMobileDataEnabled2(Context context, boolean enabled) throws Exception{
        final ConnectivityManager conman = (ConnectivityManager)  context.getSystemService(Context.CONNECTIVITY_SERVICE);
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
