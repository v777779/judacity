package ru.vpcb.rgdownload;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ru.vpcb.rgdownload.utils.NetworkData;
import ru.vpcb.rgdownload.utils.ParseUtils;
import ru.vpcb.rgdownload.utils.NetworkUtils;
import ru.vpcb.rgdownload.utils.QUERY_TYPE;

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
    private int mSpan;

    private List<MovieItem> mListMovie;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSpan = getNumberOfColumns(this);

        if (savedInstanceState == null || !savedInstanceState.containsKey("movies")) {

            if (!loadGenre()) {
                Log.v(TAG, "Can't load Genre Map");
                return;
            }

            // ВНИМАНИЕ ВСТРОИТЬ ЗДЕСЬ ПРОВЕРКУ на наличие Internet
            mListMovie = loadMovie(QUERY_TYPE.POPULAR, 1);       // load page 1
            if (mListMovie == null || mListMovie.size() == 0) {
                Log.v(TAG, "Can't load Popular Movie page 1");
            }

        } else {
            mListMovie = savedInstanceState.getParcelableArrayList("movies");
        }

        // ВНИМАНИЕ ВСТРОИТЬ ОБРАБОТКУ ОШИБОК ИНТЕРНЕТ И ПРОВЕРКУ mListMovie на null


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

    //test!!!
    private int counter = 1;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();
        String s;
        try {

            if (itemThatWasClickedId == R.id.action_search) {
                s = NetworkUtils.makeSearch(new NetworkData(QUERY_TYPE.NOWDAYS, counter++));
                List<MovieItem> list = ParseUtils.getPageList(s);
                int page = ParseUtils.getPageNumber(s);
                int total = ParseUtils.getPageTotal(s);
                int n = ParseUtils.getItemTotal(s);
                int code = ParseUtils.getStatusCode(s);

                Toast.makeText(this, "POPULAR: " + page + " " + total + " " + n + " "
                        + " " + code + " " + s, Toast.LENGTH_SHORT).show();
                return true;
            }
            if (itemThatWasClickedId == R.id.item_menu2) {
                Toast.makeText(this, "NOWDAYS", Toast.LENGTH_SHORT).show();
                return true;
            }
            if (itemThatWasClickedId == R.id.item_menu3) {
//
//                MovieItem movieItem = mListMovie.get(rnd.nextInt(mListMovie.size()));
//                sendIntent(movieItem, true);                    // new task to work with Toast
                Toast.makeText(this, "TOP RATED", Toast.LENGTH_SHORT).show();

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
        try {
            String s = NetworkUtils.makeSearch(new NetworkData(QUERY_TYPE.GENRES));
            return ParseUtils.setGenres(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    private List<MovieItem> loadMovie(QUERY_TYPE type, int page) {
        try {
            String s = NetworkUtils.makeSearch(new NetworkData(type, page));
            return ParseUtils.getPageList(s);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private List<ReviewItem> loadReview(int page, int id) {
        try {
            String s = NetworkUtils.makeSearch(new NetworkData(QUERY_TYPE.REVIEW, page, id));
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


//    public boolean isOnline() {
//        ConnectivityManager cm =
//                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo netInfo = cm.getActiveNetworkInfo();
//        return netInfo != null && netInfo.isConnectedOrConnecting();
//    }

}
