package ru.vpcb.rgdownload;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import ru.vpcb.rgdownload.utils.MovieTask;
import ru.vpcb.rgdownload.utils.NetworkUtils;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private final static float COLUMN_WIDTH_HIGH = 200;
    private final static float DP_HEIGHT_LOW = 480;
    private final static float COLUMN_WIDTH_LOW = 150;
    private final static int MAX_COLUMNS = 6;
    private final static int MIN_COLUMNS = 2;
    private final static int TEMP_MAX_ITEMS = 25;  //size if content
    private static Random rnd = new Random();

    private FlavorAdapter mFlavorAdapter;
    private ArrayList<Flavor> listFlavors;
    private RecyclerView mRecyclerView;
    private int mSpan;

    private Flavor[] arrayFlavors = {
            new Flavor("Cupcake", "1.5", R.drawable.cupcake),
            new Flavor("Donut", "1.6", R.drawable.donut),
            new Flavor("Eclair", "2.0-2.1", R.drawable.eclair),
            new Flavor("Froyo", "2.2-2.2.3", R.drawable.froyo),
            new Flavor("GingerBread", "2.3-2.3.7", R.drawable.gingerbread),
            new Flavor("Honeycomb", "3.0-3.2.6", R.drawable.honeycomb),
            new Flavor("Ice Cream Sandwich", "4.0-4.0.4", R.drawable.icecream),
            new Flavor("Jelly Bean", "4.1-4.3.1", R.drawable.jellybean),
            new Flavor("KitKat", "4.4-4.4.4", R.drawable.kitkat),
            //  new Flavor("Lollipop", "5.0-5.1.1", R.drawable.lollipop),
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSpan = getNumberOfColumns(this);

        if (savedInstanceState == null || !savedInstanceState.containsKey("flavors")) {
            listFlavors = new ArrayList<>(Arrays.asList(arrayFlavors));
            listFlavors.addAll(Arrays.asList(arrayFlavors)); // 18 сразу
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
        POPULAR, NOWDAYS, TOPRATED
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();


        if (itemThatWasClickedId == R.id.action_search) {
            String s = "";
            try {
                s = makeSearch(this, MOVIE_TYPE.POPULAR, counter++);
            } catch (ExecutionException |InterruptedException e) {
                s = "async task execution error";
                e.printStackTrace();
            }

            Toast.makeText(this, "POPULAR: "+s, Toast.LENGTH_SHORT).show();
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
        return super.onOptionsItemSelected(item);
    }

    private String mResult;

    private String makeSearch(Context context, MOVIE_TYPE searchType, int searchPage) throws ExecutionException, InterruptedException {
        URL url = NetworkUtils.buildUrl(searchType.ordinal(), searchPage, null);
        return new MovieTask().execute(url).get();
    }


}
