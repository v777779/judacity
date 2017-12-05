package ru.vpcb.popularmovie;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.vpcb.popularmovie.utils.NetworkData;
import ru.vpcb.popularmovie.utils.NetworkUtils;
import ru.vpcb.popularmovie.utils.ParseUtils;
import ru.vpcb.popularmovie.utils.QueryType;

import static ru.vpcb.popularmovie.utils.Constants.BASE_ID_RECYCLEVIEW;
import static ru.vpcb.popularmovie.utils.Constants.BUTTON_IDS;
import static ru.vpcb.popularmovie.utils.Constants.COLUMN_WIDTH_HIGH;
import static ru.vpcb.popularmovie.utils.Constants.COLUMN_WIDTH_LOW;
import static ru.vpcb.popularmovie.utils.Constants.DP_WIDTH_LOW;
import static ru.vpcb.popularmovie.utils.Constants.DP_WIDTH_MID;
import static ru.vpcb.popularmovie.utils.Constants.MAX_COLUMNS;
import static ru.vpcb.popularmovie.utils.Constants.MIN_COLUMNS;
import static ru.vpcb.popularmovie.utils.Constants.PAGE_FIRST;
import static ru.vpcb.popularmovie.utils.Constants.PAGE_NUMBER_MAX;
import static ru.vpcb.popularmovie.utils.Constants.POSITION_FIRST;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 29-Sep-17
 * Email: vadim.v.voronov@gmail.com
 */

public class MainActivity extends AppCompatActivity {
    //    private ProgressBar mProgressBar;
    private TextView mErrorText;
    private TextView mFirstText;
    private ViewPager mViewPager;

    private int mSpan;
    private QueryType mQueryMode;
    private List<Integer> mListPage;
    private List<Integer> mListPosition;
    private List<List<MovieItem>> mArrayListMovie;

    //new
    private List<TextView> mTextViewList;
    private List<MovieAdapter> mArrayListAdapter;
    private List<RecyclerView> mArrayListRecycler;

    /**
     * Builds MainActivity Window
     * Inflates activity_main layout
     * Fills resource objects
     * Uses savedInstance to restore data to DataSource class object
     * Extract data from DataSource object and copy them to fields
     * For new instance calls  loadInitList() and loadContent for every viewPager item
     * For restore instance calls loadContent()  for every viewPager item only
     * Loads Menu with activeMenuLoader() method
     * Loads ViewPager with viewPagerLoader() method
     *
     * @param savedInstanceState saved instance data not used
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSpan = getNumberOfColumns();
        mErrorText = (TextView) findViewById(R.id.main_connect_text);
        mFirstText = (TextView) findViewById(R.id.main_first_text);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);

        mArrayListMovie = new ArrayList<>();
        mArrayListAdapter = new ArrayList<>();
        mArrayListRecycler = new ArrayList<>();
        for (int i = 0; i < PAGE_NUMBER_MAX; i++) {
            mArrayListRecycler.add(new RecyclerView(this)); // empty
            mArrayListMovie.add(new ArrayList<MovieItem>()); // empty
        }

        if (savedInstanceState == null || !savedInstanceState.containsKey("datasource")) {
            mQueryMode = QueryType.POPULAR;
            mListPage = new ArrayList<>(Arrays.asList(PAGE_FIRST, PAGE_FIRST, PAGE_FIRST));
            mListPosition = new ArrayList<>(Arrays.asList(POSITION_FIRST, POSITION_FIRST, POSITION_FIRST));
            for (int i = 0; i < PAGE_NUMBER_MAX; i++) {
                if (loadInitList(i)) {
                    loadContent(i);
                } else {
                    showError();
                    break;
                }
            }

        } else {
            DataSource ds = savedInstanceState.getParcelable("datasource");

            mArrayListMovie = ds.getArrayListMovie();
            mListPage = ds.getListPage();
            mQueryMode = ds.getQueryMode();
            mListPosition = ds.getListPosition();
            for (int i = 0; i < PAGE_NUMBER_MAX; i++) {
                loadContent(i);
            }
        }
        activeMenuLoader();  // uses mQueryMode
        viewPagerLoader();
    }

    /**
     * Updates current visible position of RecyclerView in mListPosition List<Integer>
     *
     * @param position number of current visible Item of MovieAdapter RecyclerView
     */
    private void updatePosition(int position) {
        if (position < 0) {
            return;
        }
        mListPosition.set(mQueryMode.ordinal(), position);
    }

    /**
     *  Saves instance data to DataSource object
     *
     * @param outState  parcel object to store
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        DataSource ds = new DataSource(mArrayListMovie, mListPage, mQueryMode, mListPosition);
        outState.putParcelable("datasource", ds);
        super.onSaveInstanceState(outState);
    }

    /**
     * Sets number of columns depending on screen width for mSpan field
     *
     * @return number of columns suitable for current display
     */
    private int getNumberOfColumns() {
        DisplayMetrics dp = getResources().getDisplayMetrics();
        float dpWidth = dp.widthPixels / dp.density;
        int nColumns;
        if (dpWidth <= DP_WIDTH_MID) {
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

    /**
     * Loads next page from mListPage (List<Integer) for current mQueryMode and updates
     * corresponding List<MovieItem>  in mArrayListMovie  (List<List<MovieItem>>)
     * Updates size corresponding MovieAdapter from mArrayListAdapter (List<MovieAdapter>)
     */
    private void loadMovie() {
        int item = mQueryMode.ordinal();
        int page = mListPage.get(item) + 1;
        List<MovieItem> list = loadMovie(mQueryMode, page);
        if (list == null || list.size() == 0) {
            return;
        }
        mListPage.set(item, page);     // next page loaded
        List<MovieItem> listMovie = mArrayListMovie.get(item);
        listMovie.addAll(list);
        MovieAdapter movieAdapter = mArrayListAdapter.get(item);
        movieAdapter.setSize(listMovie.size());
        movieAdapter.notifyDataSetChanged();
    }

    /**
     * Changes viewPager viewPort, load movie page and updates ViewPager if necessary
     *
     * @param item number of page opf viewPager
     * @return true if connection works, false otherwise
     */
    private boolean onItemSelected(int item) {
        mQueryMode = QueryType.values()[item];  // new mode
        List<MovieItem> listMovie = mArrayListMovie.get(item);
        if (listMovie == null || listMovie.size() == 0) {  // first screen
            if (loadInitList(item)) {
                loadContent(item);
                viewPagerLoader();
            } else {
                showError();
                return false;
            }
        } else {
            mViewPager.setCurrentItem(item);// loadContent(item);
        }
        return true;
    }

    /**
     * Low level movie genres loader
     * Checks if Map<Integer, String> mapGenre  is not empty
     * If empty loads Genres from IMDb and parses loaded data and fills mapGenre
     * Returns the result of operation
     *
     * @return true if completed successfully or Map already exists, false otherwise
     */
    private boolean loadGenre() {
        if (!ParseUtils.isMapGenreEmpty()) {     // load MapGenre
            return true;
        }
        if (!isOnline()) {
            return false;
        }

        try {
            String s = NetworkUtils.makeSearch(new NetworkData(this, QueryType.GENRES));
            return ParseUtils.setGenres(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    /**
     * Low level movie data loader
     * Loads movie data from IMDb for passed query type and page
     * Parses loaded JSON  string data  with ParseUtils.getPageList() method
     * Returns List<MovieItem> object
     *
     * @param type QueryType  POPULAR, NOWDAYS, TOPRATED
     * @param page number of page to load
     * @return List<MovieItem> object
     */
    private List<MovieItem> loadMovie(QueryType type, int page) {
        if (!isOnline()) {
            return null;
        }
        try {
            String s = NetworkUtils.makeSearch(new NetworkData(this, type, page));
            return ParseUtils.getPageList(s);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Low level movie review loader
     * Loads review data from IMDb for for passed movie ID and page  (default one)
     * Parses loaded JSON  string data  with ParseUtils.getReviewList() method
     * Returns List<ReviewItem> object
     *
     * @param page number of page to load
     * @param id   movie Id  for which reviews are loaded
     * @return List<MovieItem> object
     */
    private List<ReviewItem> loadReview(int page, int id) {
        if (!isOnline()) {
            return null;
        }
        try {
            String s = NetworkUtils.makeSearch(new NetworkData(this, QueryType.REVIEW, page, id));
            return ParseUtils.getReviewList(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Sends Intent with MovieItem object to ChildActivity
     * Loads reviews for passed movieItem object
     * Passes MovieItem object as data to ChildActivity
     *
     * @param movieItem MovieItem object to send
     */
    public void sendIntent(MovieItem movieItem) {
        // load list review here
        List<ReviewItem> listReview = movieItem.getListReview();
        if (listReview == null || listReview.isEmpty()) {
            listReview = loadReview(PAGE_FIRST, movieItem.getId());  // always first page
            movieItem.setListReview(listReview);
        }
        Intent intent = new Intent(this, ChildActivity.class);
        intent.putExtra(MovieItem.class.getCanonicalName(), movieItem);  // как вариант
//        if (flag)
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  // to start in thread

        startActivity(intent);
//        Log.v(TAG, "sent parcelable movie:" + movieItem.getTitle());
    }

    /**
     * Checks if Internet connection exists
     *
     * @return true if connection exists, false otherwise
     */
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /**
     * Sets  mErrorText VISIBLE, mViewPager and mFirstText INVISIBLE
     */
    public void showError() {
        mViewPager.setVisibility(View.INVISIBLE);
        mErrorText.setVisibility(View.VISIBLE);
        mFirstText.setVisibility(View.INVISIBLE);
    }

    /**
     * Sets  mViewPager VISIBLE, mErrorText and mFirstText INVISIBLE
     */
    public void showRV() {
        mViewPager.setVisibility(View.VISIBLE);
        mErrorText.setVisibility(View.INVISIBLE);
        mFirstText.setVisibility(View.INVISIBLE);
    }

    /**
     * Gets  active_bar layout depending on screen size
     * Returns defined layout  Id
     *
     * @return active_bar layout Id corresponding to screen size
     */
    private int getMenuId() {
        DisplayMetrics dp = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dp);
        int dpWidth = (int) (dp.widthPixels / dp.density);
        if (dp.widthPixels > dp.heightPixels) {
            dpWidth = (int) (dp.heightPixels / dp.density);
        }
        int menu_id = R.layout.active_bar_low;

        if (dpWidth >= DP_WIDTH_LOW) {
            menu_id = R.layout.active_bar_mid;
        }
        if (dpWidth >= DP_WIDTH_MID) {
            menu_id = R.layout.active_bar_high;
        }
        return menu_id;
    }

    /**
     * Creates ActionBar Menu with TextView items
     * Fills mTextViewList  List<TextView> items of ActionBar
     * Add ClickListener to every Item of ActionBar
     * Sets active item corresponding to mQueryItem
     */
    private void activeMenuLoader() {
        //menu loader
        android.support.v7.app.ActionBar abMainMenu = this.getSupportActionBar();
        abMainMenu.setDisplayShowCustomEnabled(true);
        LayoutInflater liAddActionBar = LayoutInflater.from(this);
        View customActionBar;

        customActionBar = liAddActionBar.inflate(getMenuId(), null);
        abMainMenu.setCustomView(customActionBar);
        mTextViewList = new ArrayList<>();

        for (int BUTTON_ID : BUTTON_IDS) {
            TextView textView = customActionBar.findViewById(BUTTON_ID);
            textView.setTextColor(Color.GRAY);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (int i = 0; i < mTextViewList.size(); i++) {
                        TextView textView = mTextViewList.get(i);
                        if (textView.getId() == view.getId()) {
                            textView.setTextColor(Color.WHITE);
                            //  viewPager.setCurrentItem(i);
                            onItemSelected(i);
                        } else {
                            textView.setTextColor(Color.GRAY);
                        }
                    }
                }
            });
            mTextViewList.add(textView);
        }
        mTextViewList.get(mQueryMode.ordinal()).setTextColor(Color.WHITE);
// menu loader
    }

    /**
     * Load first page for selected List<MovieItem>
     * Fills mArrayListMovie List<List<MovieItem>> with loaded list
     * Check if mapGenres is not empty, and if empty  calls loadGenre() method
     * Returns the result of operation
     *
     * @param item selected viewPager page to load
     * @return true if completed successfully, false otherwise
     */
    private boolean loadInitList(int item) {
        if (!loadGenre()) {
//            Log.v(TAG, "Can't load Genre Map");
            return false;
        }
        List<MovieItem> listMovie = loadMovie(QueryType.values()[item], mListPage.get(item));       // load page 1
        mArrayListMovie.set(item, listMovie);  // записали
        return !(listMovie == null || listMovie.size() == 0);
    }

    /**
     * Creates RecyclerView object from mArrayListMove item
     * Sets viewPager VISIBLE
     * Creates RecyclerView object, attaches GridLayout, MovieAdapter and onScrollListener()
     * Updates mArrayListAdapter List<MovieAdapter> with attached MovieAdapter
     * Updates mArrayListRecycler List<RecyclerView> with created RecyclerView
     *
     * @param item selected viewPager page for RecyclerView
     */
    private void loadContent(int item) {
        showRV();
        List<MovieItem> mListMovie = mArrayListMovie.get(item);  // прочитали
//        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        RecyclerView recyclerView = new RecyclerView(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        recyclerView.setId(View.generateViewId() + BASE_ID_RECYCLEVIEW);
        recyclerView.setLayoutParams(lp);


        final GridLayoutManager mLayoutManager = new GridLayoutManager(this,
                mSpan,
                GridLayoutManager.VERTICAL,
                false);

        recyclerView.setLayoutManager(mLayoutManager);                          // connect to LayoutManager
        recyclerView.setHasFixedSize(true);                                     // item size fixed
        MovieAdapter movieAdapter = new MovieAdapter(this, mListMovie, mSpan);   //context  and data
        recyclerView.setAdapter(movieAdapter);
        recyclerView.scrollToPosition(mListPosition.get(item));
        mArrayListAdapter.add(movieAdapter);
// scrolled listener
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                updatePosition(mLayoutManager.findFirstVisibleItemPosition());
//                updatePosition(mLayoutManager.findFirstCompletelyVisibleItemPosition());

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
        mArrayListRecycler.set(item, recyclerView);
    }


    /**
     * Loads ViewPager object with mArrayListRecycler data
     * Creates and attaches ViewPagerAdapter, attaches OnPageListener object
     */
    // viewpager loader
    private void viewPagerLoader() {
        List<View> listPager = new ArrayList<>();
        listPager.add(mArrayListRecycler.get(0));
        listPager.add(mArrayListRecycler.get(1));
        listPager.add(mArrayListRecycler.get(2));


        ViewPagerAdapter listPagerAdapter = new ViewPagerAdapter(listPager);
        mViewPager.setAdapter(listPagerAdapter);
        mViewPager.setCurrentItem(mQueryMode.ordinal());
        mViewPager.setOffscreenPageLimit(PAGE_NUMBER_MAX);  //    ATTENTION  Prevents Adapter Exception
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                int lastPos = mQueryMode.ordinal();
                if (lastPos != position) {
                    mTextViewList.get(lastPos).setTextColor(Color.GRAY);
                    onItemSelected(position);
                    mTextViewList.get(position).setTextColor(Color.WHITE);
                    mQueryMode = QueryType.values()[position];
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
    // viewpager loader
}
