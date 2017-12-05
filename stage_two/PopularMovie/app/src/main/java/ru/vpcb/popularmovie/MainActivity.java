package ru.vpcb.popularmovie;

import android.content.Intent;
import android.database.Cursor;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import ru.vpcb.popularmovie.data.MovieUtils;
import ru.vpcb.popularmovie.pager.IMovieListener;
import ru.vpcb.popularmovie.pager.MovieAdapter;
import ru.vpcb.popularmovie.pager.MovieItem;
import ru.vpcb.popularmovie.pager.ViewPagerAdapter;
import ru.vpcb.popularmovie.utils.NetworkUtils;
import ru.vpcb.popularmovie.utils.ParseUtils;

import static ru.vpcb.popularmovie.utils.Constants.*;
import static ru.vpcb.popularmovie.utils.NetworkUtils.putLoaderQuery;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 23-Oct-17
 * Email: vadim.v.voronov@gmail.com
 */


public class MainActivity extends AppCompatActivity
        implements LoaderUri.ICallbackUri, LoaderDb.ICallbackDb,
        IMovieListener<MovieItem> {

    private TabLayout mTabs;
    private TextView mTextView;

    private ConcurrentLinkedQueue<Bundle> mBundleStack;
    private LoaderUri mLoader;
    private LoaderDb mLoaderDb;

    private int mSpan;
    private ViewPager mViewPager;
    private ArrayList<MovieItem> mPopular;
    private ArrayList<Integer> mPosition;
    private ArrayList<MovieItem> mTopRated;
    private ProgressBar mProgressBar;
    private ArrayList<MovieItem> mFavorites;
    private boolean mErrorState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mTabs = (TabLayout) findViewById(R.id.tab_layout);
        mTabs.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.colorSelectTab));

        mTextView = (TextView) findViewById(R.id.error_message);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mPopular = new ArrayList<>();
        mTopRated = new ArrayList<>();

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mFavorites = new ArrayList<>();
        mLoader = new LoaderUri(this, this);
        mLoaderDb = new LoaderDb(this, this);
        mPosition = new ArrayList<>();
        mSpan = getNumberOfColumns();
        mBundleStack = new ConcurrentLinkedQueue<>();
        showResult();

        if (savedInstanceState != null) {
            ArrayList<Bundle> list = savedInstanceState.getParcelableArrayList(BUNDLE_LOADER_LIST_URI_ID);
            mBundleStack.addAll(list);
            mPopular = savedInstanceState.getParcelableArrayList(BUNDLE_LOADER_LIST_POPULAR_ID);
            mTopRated = savedInstanceState.getParcelableArrayList(BUNDLE_LOADER_LIST_TOPRATED_ID);
            mPosition = savedInstanceState.getIntegerArrayList(BUNDLE_LOADER_LIST_POSITION_ID);
        } else {
            mPosition.addAll(Arrays.asList(0, 0, 0));
            putLoaderQuery(mBundleStack, GENRES_ID, 0, 0);
            putLoaderQuery(mBundleStack, POPULAR_ID, 1, 0);
            putLoaderQuery(mBundleStack, TOPRATED_ID, 1, 0);
        }

        getSupportLoaderManager().initLoader(LOADER_CONSTANT_ID, mBundleStack.peek(), mLoader);
        getSupportLoaderManager().initLoader(LOADER_MOVIE_DB_ID, null, mLoaderDb);

        setupViewPager();
        mErrorState = NetworkUtils.isOnline(this);
        if (!mErrorState) {   // не пропускать
            showError();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.settings_popular) {
            mTabs.getTabAt(POPULAR_ID).select();
            return true;
        }
        if (id == R.id.settings_toprated) {
            mTabs.getTabAt(TOPRATED_ID).select();
            return true;
        }
        if (id == R.id.settings_favorite) {
            mTabs.getTabAt(FAVORITES_ID).select();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(BUNDLE_LOADER_LIST_URI_ID, new ArrayList<Parcelable>(mBundleStack));
        outState.putParcelableArrayList(BUNDLE_LOADER_LIST_POPULAR_ID, new ArrayList<Parcelable>(mPopular));
        outState.putParcelableArrayList(BUNDLE_LOADER_LIST_TOPRATED_ID, new ArrayList<Parcelable>(mTopRated));
        outState.putIntegerArrayList(BUNDLE_LOADER_LIST_POSITION_ID, mPosition);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(LOADER_CONSTANT_ID, mBundleStack.peek(), mLoader);
        getSupportLoaderManager().restartLoader(LOADER_MOVIE_DB_ID, null, mLoaderDb);
    }

    @Override
    public void showProgress() {
        mTextView.setVisibility(View.INVISIBLE);
        mViewPager.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void showError() {
        mTextView.setVisibility(View.VISIBLE);
        mViewPager.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    private void showResult() {
        mTextView.setVisibility(View.INVISIBLE);
        mViewPager.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    private void updateList(List<MovieItem> movieItemList, String s) {
        List<MovieItem> list = null;
        if (ParseUtils.isMapGenreEmpty()) {
            showError();
            return;
        }
        try {
            list = ParseUtils.getPageList(s);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        if (list != null && list.size() > 0) {
            setFavorites(list);
            movieItemList.addAll(list);
            mViewPager.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public void onComplete(Bundle data) {
        showResult();
        int id = data.getInt(BUNDLE_LOADER_QUERY_ID);
        String s = data.getString(BUNDLE_LOADER_STRING_ID);
        switch (id) {
            case GENRES_ID:
                if (ParseUtils.isMapGenreEmpty()) {     // load MapGenre
                    try {
                        ParseUtils.setGenres(s);
                    } catch (Exception e) {
                        // e.printStackTrace();
                    }
                }
                break;
            case POPULAR_ID:
                updateList(mPopular, s);
                break;
            case TOPRATED_ID:
                updateList(mTopRated, s);
                break;
            default:
        }
        mBundleStack.poll();
        if (mBundleStack.isEmpty()) {
            getSupportLoaderManager().destroyLoader(LOADER_CONSTANT_ID);
        } else {
            getSupportLoaderManager().restartLoader(LOADER_CONSTANT_ID, mBundleStack.peek(), mLoader);
        }
    }

    private void setFavorites(List<MovieItem> movieItemList) {
        if (mFavorites == null || mFavorites.isEmpty()) return;
        if (movieItemList == null || movieItemList.size() == 0) return;

        List<MovieItem> sortedList = new ArrayList<>(mFavorites);
        Collections.sort(sortedList);
        for (MovieItem movieItem : movieItemList) {
            movieItem.setFavorite(Collections.binarySearch(sortedList, movieItem) >= 0);
        }
    }

    @Override
    public void onComplete(final Cursor cursor) {
        if (cursor == null) {
            return;
        }
        mFavorites.clear();
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            MovieItem movieItem = new MovieItem(cursor);
            mFavorites.add(movieItem);
        }
        setFavorites(mPopular);
        setFavorites(mTopRated);
        mViewPager.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onReset() {
    }

    private RecyclerView setupRecycleView(List<MovieItem> movieItemList, int startPosition) {
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
        MovieAdapter movieAdapter = new MovieAdapter(this, movieItemList, mSpan, this);
        recyclerView.setAdapter(movieAdapter);
        recyclerView.scrollToPosition(startPosition);  // восстановить позицию
        recyclerView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorBlueDark));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int mode = mTabs.getSelectedTabPosition();
                int position = mLayoutManager.findFirstVisibleItemPosition();
                if (position < 0) {
                    return;
                }
                mPosition.set(mode, position);

                if (dy > 0) {
                    int v = mLayoutManager.getChildCount();
                    int p = mLayoutManager.findFirstVisibleItemPosition();
                    int total = mLayoutManager.getItemCount();

                    if (v + p >= total) {
                        int type = mTabs.getSelectedTabPosition();  // 0,1 или 2
                        if (!mBundleStack.isEmpty()) {  // отсекаем
                            if (getLoaderManager().getLoader(LOADER_CONSTANT_ID) == null) {
                                getSupportLoaderManager().restartLoader(LOADER_CONSTANT_ID, mBundleStack.peek(), mLoader);
                            }
                            return;
                        }

                        switch (type) {
                            case POPULAR_ID:
                                int page = mPopular.size() / MOVIE_PAGE_SIZE + 1;
                                putLoaderQuery(mBundleStack, POPULAR_ID, page, 0);
                                break;
                            case TOPRATED_ID:
                                page = mTopRated.size() / MOVIE_PAGE_SIZE + 1;
                                putLoaderQuery(mBundleStack, TOPRATED_ID, page, 0);
                                break;
                            default:
                                return;
                        }
                        getSupportLoaderManager().initLoader(LOADER_CONSTANT_ID, mBundleStack.peek(), mLoader);
                    }
                }
            }
        });
        return recyclerView;
    }

    private void setupViewPager() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter();
        viewPagerAdapter.add(setupRecycleView(mPopular, mPosition.get(POPULAR_ID)));
        viewPagerAdapter.add(setupRecycleView(mTopRated, mPosition.get(TOPRATED_ID)));
        viewPagerAdapter.add(setupRecycleView(mFavorites, mPosition.get(FAVORITES_ID)));

        mViewPager.setAdapter(viewPagerAdapter);
        mViewPager.setCurrentItem(mTabs.getSelectedTabPosition());
        mViewPager.setOffscreenPageLimit(PAGE_NUMBER_MAX);  //    ATTENTION  Prevents Adapter Exception
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabs));
        mTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.select();
                mViewPager.setCurrentItem(tab.getPosition());
                boolean networkState = NetworkUtils.isOnline(MainActivity.this);
                if (!mErrorState && networkState) {
                    mErrorState = networkState;
                    getSupportLoaderManager().restartLoader(LOADER_CONSTANT_ID, mBundleStack.peek(), mLoader);
                }

                if (tab.getPosition() == FAVORITES_ID) {
                    showResult();
                } else {
                    if (!networkState) {
                        showError();
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onItemClick(MovieItem movieItem) {
        Intent intent = new Intent(MainActivity.this, ChildActivity.class);
        intent.putExtra(INTENT_MOVIE_ITEM_ID, movieItem);
        startActivity(intent);
    }

    @Override
    public void onItemClickFavorites(MovieItem movieItem) {
        if (movieItem == null) {
            return;
        }

        if (movieItem.isFavorite()) {
            int nDelete = MovieUtils.deleteRecord(getContentResolver(), getSupportLoaderManager(),
                    movieItem, mLoaderDb);
            if (nDelete != 0) {
                movieItem.setFavorite(false);
            }
        } else {
            boolean isInsert = MovieUtils.insertRecord(getContentResolver(), getSupportLoaderManager(),
                    movieItem, mLoaderDb);
            if (isInsert) {
                movieItem.setFavorite(true);
            }
        }
        mViewPager.getAdapter().notifyDataSetChanged();
    }

    private int getNumberOfColumns() {
        DisplayMetrics dp = getResources().getDisplayMetrics();
        float dpWidth = dp.widthPixels / dp.density;
        boolean isLandscape = dp.widthPixels > dp.heightPixels;
        int nColumns;
        if (isLandscape) {   // for landscape
            if (dpWidth >= DP_WIDTH_LANDSCAPE_HIGH) {
                nColumns = (int) (dpWidth / COLUMN_WIDTH_HIGH);

            } else if (dpWidth >= DP_WIDTH_LANDSCAPE_MDL) {
                nColumns = (int) (dpWidth / COLUMN_WIDTH_MIDDLE);

            } else {
                nColumns = (int) (dpWidth / COLUMN_WIDTH_LOW);
            }

        } else {
            if (dpWidth >= DP_WIDTH_PORTRAIT_HIGH) {
                nColumns = (int) (dpWidth / COLUMN_WIDTH_HIGH);

            } else if (dpWidth >= DP_WIDTH_PORTRAIT_MDL) {
                nColumns = (int) (dpWidth / COLUMN_WIDTH_MIDDLE);

            } else {
                nColumns = (int) (dpWidth / COLUMN_WIDTH_LOW);
            }
        }
        if (nColumns > MAX_COLUMNS) {
            nColumns = MAX_COLUMNS;
        }
        if (nColumns < MIN_COLUMNS) {
            nColumns = MIN_COLUMNS;
        }
        return nColumns;
    }
}
