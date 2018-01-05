package com.example.xyzreader.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowInsets;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.UpdaterService;
import com.example.xyzreader.remote.Config;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

import static com.example.xyzreader.remote.Config.ACTION_SWIPE_REFRESH;
import static com.example.xyzreader.remote.Config.ACTION_TIME_REFRESH;
import static com.example.xyzreader.remote.Config.CALLBACK_FRAGMENT_CLOSE;
import static com.example.xyzreader.remote.Config.CALLBACK_FRAGMENT_EXIT;
import static com.example.xyzreader.remote.Config.CALLBACK_FRAGMENT_RETRY;
import static com.example.xyzreader.remote.Config.FRAGMENT_ERROR_TAG;

public class ArticleListActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, ICallback {
    private static boolean mIsTimber;


    @Nullable
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @Nullable
    @BindView(R.id.toolbar_logo)
    ImageView mToolbarLogo;
    @Nullable
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Nullable
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Nullable
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    private Unbinder mUnBinder;
    private BroadcastReceiver mRefreshingReceiver;
    private boolean mIsSwipeRefresh;
    private boolean mIsRefreshing;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);


// bind
        mUnBinder = ButterKnife.bind(this);
// timber
        if (!mIsTimber) {
            Timber.plant(new Timber.DebugTree());
            mIsTimber = true;
        }


// toolbar

        mToolbar.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                return null;
            }
        });

        mToolbar.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                view.onApplyWindowInsets(windowInsets);
                Resources res = getResources();
                int sysBarHeight = windowInsets.getSystemWindowInsetTop() + mToolbar.getLayoutParams().height;
                int offsetTop = res.getDimensionPixelSize(R.dimen.micro_margin) + sysBarHeight;
                int offsetBottom = res.getDimensionPixelOffset(R.dimen.recycler_bottom_offset);
                int offsetSide = res.getDimensionPixelOffset(R.dimen.micro_margin);

                mRecyclerView.setPadding(offsetSide, offsetTop, offsetSide, offsetBottom);

                offsetTop = res.getDimensionPixelSize(R.dimen.progress_swipe_offset) + sysBarHeight;
                mSwipeRefreshLayout.setProgressViewEndTarget(true, offsetTop);


                return windowInsets;
            }
        });


        setSupportActionBar(mToolbar);
        mToolbarLogo = mToolbar.findViewById(R.id.toolbar_logo);
        mToolbarLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle("");
        }
//        mSysBarHeight = getResources().getDimensionPixelSize(R.dimen.status_bar_height) +
//                mToolbar.getLayoutParams().height;


// fab
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        setupRecycler();
        setupSwipeRefresh();


        if (savedInstanceState == null) {
            refresh(ACTION_TIME_REFRESH);
        }

        getSupportLoaderManager().initLoader(0, null, this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_article_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UpdaterService.BROADCAST_ACTION_STATE_CHANGE);
        intentFilter.addAction(UpdaterService.BROADCAST_ACTION_NO_NETWORK);
        registerReceiver(mRefreshingReceiver, intentFilter);

    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mRefreshingReceiver);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnBinder.unbind();
    }

    // callbacks
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newAllArticlesInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        ((ArticleListAdapter) mRecyclerView.getAdapter()).setCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }

    @Override
    public void onCallback(Uri uri) {
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    public void onCallback(int mode) {
        switch (mode) {
            case CALLBACK_FRAGMENT_RETRY:
                refresh(mIsSwipeRefresh ? ACTION_SWIPE_REFRESH : ACTION_TIME_REFRESH);
                break;
            case CALLBACK_FRAGMENT_CLOSE:
                hideRefreshingUI();
                break;
            case CALLBACK_FRAGMENT_EXIT:
                finish();
                break;
            default:
        }

    }

    // common methods
    private void hideRefreshingUI() {
        mIsSwipeRefresh = false;
        mIsRefreshing = false;
        mSwipeRefreshLayout.setRefreshing(mIsRefreshing);
        mProgressBar.setVisibility(mIsRefreshing ? View.VISIBLE : View.INVISIBLE);
    }

    private void updateRefreshingUI() {
        if (mIsSwipeRefresh) {
            mSwipeRefreshLayout.setRefreshing(mIsRefreshing);
            mIsSwipeRefresh = mIsRefreshing;        // switch mIsRefresh at second broadcastReceive
        } else {
            mProgressBar.setVisibility(mIsRefreshing ? View.VISIBLE : View.INVISIBLE);
        }
    }

    private void showErrorDialog(boolean isCursorEmpty) {
        FragmentError fragmentError = FragmentError.newInstance(isCursorEmpty);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(fragmentError, FRAGMENT_ERROR_TAG);
        ft.commit();
    }


    private void setupRecycler() {
        Config.Span sp = Config.getDisplayMetrics(this);

        ArticleListAdapter adapter = new ArticleListAdapter(this, sp);
        adapter.setHasStableIds(true);
        mRecyclerView.setAdapter(adapter);
        GridLayoutManager layoutManager = new GridLayoutManager(
                this,
                sp.getSpanX(),
                GridLayout.VERTICAL,
                false);
        mRecyclerView.setLayoutManager(layoutManager);
        Resources res = getResources();

//        int offsetTop = res.getDimensionPixelSize(R.dimen.micro_margin) + mSysBarHeight;
//        int offsetBottom = res.getDimensionPixelOffset(R.dimen.recycler_bottom_offset);
//        int offsetSide = res.getDimensionPixelOffset(R.dimen.micro_margin);
//        mRecyclerView.setPadding(offsetSide, offsetTop, offsetSide, offsetBottom);


        Timber.d("status/navigation : " + getStatusBarHeight() + ", " + getNavigationBarHeight());

    }

    private void setupSwipeRefresh() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mIsSwipeRefresh = true;
                refresh(ACTION_SWIPE_REFRESH);
            }
        });

//        int offsetTop = getResources().getDimensionPixelSize(R.dimen.progress_swipe_offset) + mSysBarHeight;
//        mSwipeRefreshLayout.setProgressViewEndTarget(true, offsetTop);

        mRefreshingReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent == null) return;
                String action = intent.getAction();

                if (action == null || action.isEmpty()) return;
                if (UpdaterService.BROADCAST_ACTION_STATE_CHANGE.equals(action)) {
                    mIsRefreshing = intent.getBooleanExtra(UpdaterService.EXTRA_REFRESHING, false);
                    updateRefreshingUI();
                }

                if (UpdaterService.BROADCAST_ACTION_NO_NETWORK.equals(action)) {
                    boolean isCursorEmpty = intent.getBooleanExtra(UpdaterService.EXTRA_EMPTY_CURSOR, false);
                    showErrorDialog(isCursorEmpty);
                }
            }
        };

    }


    private void refresh(String action) {
        startService(new Intent(action, null, this, UpdaterService.class));
    }

    private int getNavigationBarHeight() {
        int height = 0;
        int statusId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (statusId > 0) {
            height = getResources().getDimensionPixelSize(statusId);
        }

        return height;
    }

    private int getStatusBarHeight() {
        int height = 0;
        int statusId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (statusId > 0) {
            height = getResources().getDimensionPixelSize(statusId);
        }

        return height;
    }
}