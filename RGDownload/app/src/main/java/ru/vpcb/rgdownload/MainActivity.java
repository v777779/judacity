package ru.vpcb.rgdownload;

import android.content.Context;
import android.os.Bundle;
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
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import ru.vpcb.rgdownload.utils.MovieUtils;
import ru.vpcb.rgdownload.utils.NetworkUtils;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private final static float COLUMN_WIDTH_HIGH = 200;
    private final static float DP_HEIGHT_LOW = 480;
    private final static float COLUMN_WIDTH_LOW = 150;
    private final static int MAX_COLUMNS = 6;
    private final static int MIN_COLUMNS = 2;
    private final static int TEMP_MAX_ITEMS = 25;  //size if content

    private static final String ASYNC_MESSAGE = "{\"success\": false,  \"status_code\": 402 }";

    private static Random rnd = new Random();

    private FlavorAdapter mFlavorAdapter;
    private ArrayList<Flavor> listFlavors;
    private RecyclerView mRecyclerView;
    private int mSpan;

    private Flavor[] arrayFlavors = {
            new Flavor("Cupcake", "1.5", R.drawable.cupcake, null),
            new Flavor("Donut", "1.6", R.drawable.donut, null),
            new Flavor("Eclair", "2.0-2.1", R.drawable.eclair, null),
            new Flavor("Froyo", "2.2-2.2.3", R.drawable.froyo, null),
            new Flavor("GingerBread", "2.3-2.3.7", R.drawable.gingerbread, null),
            new Flavor("Honeycomb", "3.0-3.2.6", R.drawable.honeycomb, null),
            new Flavor("Ice Cream Sandwich", "4.0-4.0.4", R.drawable.icecream, null),
            new Flavor("Jelly Bean", "4.1-4.3.1", R.drawable.jellybean, null),
            new Flavor("KitKat", "4.4-4.4.4", R.drawable.kitkat, null),
            //  new Flavor("Lollipop", "5.0-5.1.1", R.drawable.lollipop),
    };
    private List<MovieItem> listMovie;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSpan = getNumberOfColumns(this);

        if (savedInstanceState == null || !savedInstanceState.containsKey("flavors")) {
            listFlavors = new ArrayList<>();

            if (!loadGenreMap()) {
                Log.v(TAG, "Can't load Genre Map");
            }
            listMovie = loadPage(this, MOVIE_TYPE.POPULAR, 1); // load page 1
            if (listMovie == null || listMovie.size() == 0) {
                Log.v(TAG, "Can't load Popular Movie page 1");
            }

            for (int i = 0; i < listMovie.size(); i++) {
                MovieItem movieItem = listMovie.get(i);
                String mName = movieItem.getTitle();
                String mRating = movieItem.getRating();
                String mYear = movieItem.getReleaseYear();
                int mImageId = R.drawable.empty;
                String mImagePath = movieItem.getPosterLow();

                Flavor flavor = new Flavor(mRating, mYear, mImageId, mImagePath);
                listFlavors.add(flavor);
//                listFlavors.add(arrayFlavors[rnd.nextInt(arrayFlavors.length)]);
            }


        } else {
            listFlavors = savedInstanceState.getParcelableArrayList("flavors");
        }


        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        final GridLayoutManager mLayoutManager = new GridLayoutManager(this,
                mSpan,
                GridLayoutManager.VERTICAL,
                false);

        mRecyclerView.setLayoutManager(mLayoutManager);                          // connect to LayoutManager
        mRecyclerView.setHasFixedSize(true);                                     // item size fixed
        mFlavorAdapter = new FlavorAdapter(this, listFlavors, mSpan);  //context  and data
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
                        loadPage();
                    }
                }
            }
        });


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("flavors", listFlavors);
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


    private void loadPage() {
        if (mFlavorAdapter.getItemCount() < TEMP_MAX_ITEMS) {  // denial of service
            listFlavors.add(arrayFlavors[rnd.nextInt(arrayFlavors.length)]);
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

    private enum MOVIE_TYPE {
        POPULAR, NOWDAYS, TOPRATED, GENRES
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();
        String s;
        try {

            if (MovieUtils.isMapGenreEmpty()) {  // load MapGenre
                s = makeSearch(this, MOVIE_TYPE.GENRES, 0, null);  //
                MovieUtils.setGenres(s);

            }

            if (itemThatWasClickedId == R.id.action_search) {
                s = makeSearch(this, MOVIE_TYPE.NOWDAYS, counter++, null);


                List<MovieItem> list = MovieUtils.getPageList(s);
                int page = MovieUtils.getPageNumber(s);
                int total = MovieUtils.getPageTotal(s);
                int n = MovieUtils.getItemTotal(s);
                int code = MovieUtils.getStatusCode(s);

                Toast.makeText(this, "POPULAR: " + page + " " + total + " " + n + " "
                        + " " + code + " " + s, Toast.LENGTH_SHORT).show();
//            Toast.makeText(this, s, Toast.LENGTH_LONG).show();

                return true;
            }
            if (itemThatWasClickedId == R.id.item_menu2) {
                Toast.makeText(this, "NOWDAYS", Toast.LENGTH_SHORT).show();
                return true;
            }
            if (itemThatWasClickedId == R.id.item_menu3) {
                Toast.makeText(this, "TOP RATED", Toast.LENGTH_SHORT).show();
                return true;
            }

        } catch (Exception e) {
            s = ASYNC_MESSAGE;
        }

        return super.onOptionsItemSelected(item);
    }

    private String mResult;

    private String makeSearch(Context context, MOVIE_TYPE type, int page, String lang) throws Exception {
        URL url = NetworkUtils.buildUrl(type.ordinal(), page, lang);
        return new MovieTask().execute(url).get();
    }

    private boolean loadGenreMap() {
        if (MovieUtils.isMapGenreEmpty()) {  // load MapGenre
            String s = null;  //
            try {
                s = makeSearch(this, MOVIE_TYPE.GENRES, 0, null);
            } catch (Exception e) {
                return false;
            }
            MovieUtils.setGenres(s);
        }
        return true;
    }

    private List<MovieItem> loadPage(Context context, MOVIE_TYPE type, int page) {
        String s;
        try {
            s = makeSearch(context, type, page, null);
        } catch (Exception e) {
            return null;
        }

        return MovieUtils.getPageList(s);
    }
}
