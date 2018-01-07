package com.example.xyzreader.ui;

import android.app.SharedElementCallback;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;
import com.example.xyzreader.data.UpdaterService;
import com.example.xyzreader.remote.Config;

import java.util.List;
import java.util.Map;

import timber.log.Timber;

import static com.example.xyzreader.remote.Config.ACTION_SWIPE_REFRESH;
import static com.example.xyzreader.remote.Config.ACTION_TIME_REFRESH;
import static com.example.xyzreader.remote.Config.ARTICLE_LIST_LOADER_ID;
import static com.example.xyzreader.remote.Config.BROADCAST_ACTION_NO_NETWORK;
import static com.example.xyzreader.remote.Config.BROADCAST_ACTION_UPDATE_FINISHED;
import static com.example.xyzreader.remote.Config.BROADCAST_ACTION_UPDATE_STARTED;
import static com.example.xyzreader.remote.Config.BUNDLE_CURRENT_ITEM_ID;
import static com.example.xyzreader.remote.Config.BUNDLE_STARTING_ITEM_ID;
import static com.example.xyzreader.remote.Config.CALLBACK_FRAGMENT_CLOSE;
import static com.example.xyzreader.remote.Config.CALLBACK_FRAGMENT_EXIT;
import static com.example.xyzreader.remote.Config.CALLBACK_FRAGMENT_RETRY;
import static com.example.xyzreader.remote.Config.EXTRA_EMPTY_CURSOR;
import static com.example.xyzreader.remote.Config.EXTRA_REFRESHING;
import static com.example.xyzreader.remote.Config.FRAGMENT_ERROR_CLOSE;
import static com.example.xyzreader.remote.Config.FRAGMENT_ERROR_EXIT;
import static com.example.xyzreader.remote.Config.FRAGMENT_ERROR_TAG;
import static com.example.xyzreader.remote.Config.FRAGMENT_ERROR_WAIT;

public class ArticleListActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, ICallback {

// TODO Palette to Detail Load status bar
// TODO Glide load support when transition
// TODO Landscape bottom bar to mode to side and add side margins to text
// TODO Cancel loader when click if not finished  , made simple block on click
// TODO ProgressBar on ScrollY() ???
// TODO Layouts on WXGA
// TODO BROADCAST ACTION in Exception of UpdateService and mIsRefreshing

    private static boolean mIsTimber;


    private Toolbar mToolbar;
    private ImageView mToolbarLogo;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;


    private BroadcastReceiver mRefreshingReceiver;
    private boolean mIsSwipeRefresh;
    private boolean mIsRefreshing;   // progress and check enter to second activity

    // transition
    private Bundle mTmpReenterState;
    private SharedElementCallback mSharedCallback;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
// transition
        mSharedCallback = setupSharedCallback();
        setExitSharedElementCallback(mSharedCallback);

        setContentView(R.layout.activity_article_main);  // transition set in styles

// bind
        mToolbar = findViewById(R.id.toolbar_main);
        mToolbarLogo = findViewById(R.id.toolbar_logo);
        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh);
        mRecyclerView = findViewById(R.id.recycler_view);
        mProgressBar = findViewById(R.id.progress_bar);

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

        mToolbarLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("");
        }

        setupRecycler();
        setupSwipeRefresh();


        if (savedInstanceState == null) {
            refresh(ACTION_TIME_REFRESH);
        }

        getSupportLoaderManager().initLoader(ARTICLE_LIST_LOADER_ID, null, this);

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
        intentFilter.addAction(BROADCAST_ACTION_UPDATE_STARTED);
        intentFilter.addAction(BROADCAST_ACTION_NO_NETWORK);
        intentFilter.addAction(BROADCAST_ACTION_UPDATE_FINISHED);
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

    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        mTmpReenterState = new Bundle(data.getExtras());
        long startingItemId = mTmpReenterState.getLong(BUNDLE_STARTING_ITEM_ID);
        long currentItemid = mTmpReenterState.getLong(BUNDLE_CURRENT_ITEM_ID);
        if (startingItemId != currentItemid) {

            int currentPosition = -1;

            mRecyclerView.scrollToPosition(currentPosition);
        }
        postponeEnterTransition();
        mRecyclerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mRecyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                mRecyclerView.requestLayout();
                startPostponedEnterTransition();
                return true;
            }
        });
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
    public void onCallback(Uri uri, View view) {
        if (mIsRefreshing) {
            showErrorDialog(FRAGMENT_ERROR_WAIT);
            return;
        }

        View mImage = view.findViewById(R.id.article_image);
        View mTitle = view.findViewById(R.id.article_title);
        View mSubTitle = view.findViewById(R.id.article_subtitle);


        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.putExtra(BUNDLE_STARTING_ITEM_ID, ItemsContract.Items.getItemId(uri));
// works but no used
//        Intent intent = new Intent(this, ArticleDetailActivity.class);
//        intent.putExtra(BUNDLE_STARTING_ITEM_ID, ItemsContract.Items.getItemId(uri));

        Pair<View, String> p1 = Pair.create(mImage, mImage.getTransitionName());  // unique name
        Pair<View, String> p2 = Pair.create(mTitle, mTitle.getTransitionName());
        Pair<View, String> p3 = Pair.create(mSubTitle, mSubTitle.getTransitionName());

        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this, p1, p2);

        startActivity(intent, optionsCompat.toBundle());

    }


    @Override
    public void onCallback(int mode) {  // FragmentError Support
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

    @Override
    public void onCallback(ArticleDetailFragment fragment) {  // DetailActivity transition support

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

    private void showErrorDialog(int[] parameters) {
        FragmentError fragment = FragmentError.newInstance(parameters);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(fragment, FRAGMENT_ERROR_TAG);
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

    }

    private void setupSwipeRefresh() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mIsSwipeRefresh = true;
                refresh(ACTION_SWIPE_REFRESH);
            }
        });

        mRefreshingReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent == null) return;
                String action = intent.getAction();

                if (action == null || action.isEmpty()) return;
                if (BROADCAST_ACTION_UPDATE_STARTED.equals(action)) {
                    mIsRefreshing = true;
                    updateRefreshingUI();
                }
                if (BROADCAST_ACTION_UPDATE_FINISHED.equals(action)) {
                    mIsRefreshing = false;
                    updateRefreshingUI();
                }
                if (BROADCAST_ACTION_NO_NETWORK.equals(action)) {
                    boolean isCursorEmpty = intent.getBooleanExtra(EXTRA_EMPTY_CURSOR, false);
                    showErrorDialog(isCursorEmpty ? FRAGMENT_ERROR_EXIT : FRAGMENT_ERROR_CLOSE);
                    mIsRefreshing = false; // no Internet no loading
                }
            }
        };

    }

    private void refresh(String action) {
        startService(new Intent(action, null, this, UpdaterService.class));
    }

    private SharedElementCallback setupSharedCallback() {
        return new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                if (mTmpReenterState != null) {
                    int startingItemId = mTmpReenterState.getInt(BUNDLE_STARTING_ITEM_ID);
                    int currentItemId = mTmpReenterState.getInt(BUNDLE_CURRENT_ITEM_ID);
                    if (startingItemId != currentItemId) {
//                        String newTransitionName = mList.get(currentPosition);
////                    View newSharedElement = mRecyclerView.findViewWithTag(newTransitionName);
//                        RecyclerAdapter.ViewHolder holder = (RecyclerAdapter.ViewHolder) mRecyclerView.findViewHolderForAdapterPosition(currentPosition);
//                        View newSharedElement = holder.mItemImage;
//
//                        if (newSharedElement != null) {
//                            names.clear();
//                            names.add(newTransitionName);
//                            sharedElements.clear();
//                            sharedElements.put(newTransitionName, newSharedElement);
//// fab
//                            sharedElements.put(mFab.getTransitionName(),mFab);
//// text
//                            sharedElements.put(mText.getTransitionName(),mText);
//                        }
                    }

                    mTmpReenterState = null;
                } else {
                    // If mTmpReenterState is null, then the activity is exiting.
                    View navigationBar = findViewById(android.R.id.navigationBarBackground);
                    View statusBar = findViewById(android.R.id.statusBarBackground);
                    if (navigationBar != null) {
                        names.add(navigationBar.getTransitionName());
                        sharedElements.put(navigationBar.getTransitionName(), navigationBar);
                    }
                    if (statusBar != null) {
                        names.add(statusBar.getTransitionName());
                        sharedElements.put(statusBar.getTransitionName(), statusBar);
                    }
                }
            }
        };

    }
}
