package com.example.xyzreader.ui;

import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
//import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.UpdaterService;
import com.example.xyzreader.remote.Config;
import com.example.xyzreader.remote.VolleyQueueSingleton;

import static com.example.xyzreader.remote.Config.ACTION_SWIPE_REFRESH;
import static com.example.xyzreader.remote.Config.ACTION_TIME_REFRESH;
import static com.example.xyzreader.remote.Config.CALLBACK_FRAGMENT_CLOSE;
import static com.example.xyzreader.remote.Config.CALLBACK_FRAGMENT_EXIT;
import static com.example.xyzreader.remote.Config.CALLBACK_FRAGMENT_RETRY;
import static com.example.xyzreader.remote.Config.FRAGMENT_ERROR_TAG;

/**
 * An activity representing a list of Articles. This activity has different presentations for
 * handset and tablet-size devices. On handsets, the activity presents a list of items, which when
 * touched, lead to a {@link ArticleDetailActivity} representing item details. On tablets, the
 * activity presents a grid of items as cards.
 */
public class ArticleListActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, ICallback {


    public static final String TAG = ArticleListActivity.class.toString();


    private Toolbar mToolbar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;


    private BroadcastReceiver mRefreshingReceiver;
    private boolean mIsRefreshing = false;

    private ProgressBar mProgressBar;
    private Context mContext;
    private boolean mIsSwipeRefresh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);
// error!!! set background, set style in XML
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        final View toolbarContainerView = findViewById(R.id.toolbar_container);

// correction!!!
        ImageView toolbarLogo = findViewById(R.id.toolbar_logo);
        int toolbarHeight = (int) (toolbarLogo.getLayoutParams().height * 0.75);
        toolbarLogo.getLayoutParams().height = toolbarHeight;
        toolbarLogo.getLayoutParams().width = (int) (toolbarHeight * 4);
        toolbarLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");


// error!!! set Listener
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mIsSwipeRefresh = true;
                refresh(ACTION_SWIPE_REFRESH);
            }
        });




        Config.Span sp = Config.getDisplayMetrics(this,1,1);
        int columnCount = sp.getSpanX();
        int columnHeight = sp.getHeight();

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        ArticleListAdapter adapter = new ArticleListAdapter(this, sp);
        adapter.setHasStableIds(true);
        mRecyclerView.setAdapter(adapter);
//        StaggeredGridLayoutManager sglm =
//                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        final GridLayoutManager layoutManager = new GridLayoutManager(this, columnCount, GridLayout.VERTICAL, false);

//        mRecyclerView.setLayoutManager(sglm);
        mRecyclerView.setLayoutManager(layoutManager);



        getLoaderManager().initLoader(0, null, this);

        if (savedInstanceState == null) {
            refresh(ACTION_TIME_REFRESH);
        }

// broadcast receiver, receives  messages from service
// intent.boolean  true/false  on/off progressbar or swipe refresh
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
// correction!!!
                if (UpdaterService.BROADCAST_ACTION_NO_NETWORK.equals(action)) {
                    boolean isCursorEmpty = intent.getBooleanExtra(UpdaterService.EXTRA_EMPTY_CURSOR, false);
                    showErrorDialog(isCursorEmpty);

                }
            }
        };

// progress bar
        mProgressBar = findViewById(R.id.progress_bar);

// volley singleton
        VolleyQueueSingleton.getInstance(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.refresh) {
            refresh(ACTION_SWIPE_REFRESH);
        }

        return super.onOptionsItemSelected(item);
    }

    private void showErrorDialog(boolean isCursorEmpty) {
        FragmentError fragmentError = FragmentError.newInstance(isCursorEmpty);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(fragmentError, FRAGMENT_ERROR_TAG);
        ft.commit();
    }

    private void refresh(String action) {
        startService(new Intent(action, null, this, UpdaterService.class));
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
        VolleyQueueSingleton.getInstance().getRequestQueue().cancelAll(TAG);
    }


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

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newAllArticlesInstance(this);
    }

    // correction !!!
    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        ((ArticleListAdapter) mRecyclerView.getAdapter()).setCursor(cursor);

    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }


    // correction!!! frameError support
    @Override
    public void onCallback(Uri uri) {
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    // correction!!! frameError support
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

}
