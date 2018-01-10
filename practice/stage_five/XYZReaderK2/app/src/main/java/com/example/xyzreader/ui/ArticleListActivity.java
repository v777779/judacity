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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowInsets;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;
import com.example.xyzreader.data.UpdaterService;
import com.example.xyzreader.remote.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

import static com.example.xyzreader.remote.Config.ACTION_SWIPE_REFRESH;
import static com.example.xyzreader.remote.Config.ACTION_TIME_REFRESH;
import static com.example.xyzreader.remote.Config.ARTICLE_LIST_LOADER_ID;
import static com.example.xyzreader.remote.Config.BROADCAST_ACTION_NO_NETWORK;
import static com.example.xyzreader.remote.Config.BROADCAST_ACTION_UPDATE_FINISHED;
import static com.example.xyzreader.remote.Config.BROADCAST_ACTION_UPDATE_STARTED;
import static com.example.xyzreader.remote.Config.BUNDLE_CURRENT_ITEM_POS;
import static com.example.xyzreader.remote.Config.BUNDLE_STARTING_ITEM_ID;
import static com.example.xyzreader.remote.Config.BUNDLE_STARTING_ITEM_POS;
import static com.example.xyzreader.remote.Config.CALLBACK_FRAGMENT_CLOSE;
import static com.example.xyzreader.remote.Config.CALLBACK_FRAGMENT_EXIT;
import static com.example.xyzreader.remote.Config.CALLBACK_FRAGMENT_RETRY;
import static com.example.xyzreader.remote.Config.EXTRA_EMPTY_CURSOR;
import static com.example.xyzreader.remote.Config.FRAGMENT_ERROR_CLOSE;
import static com.example.xyzreader.remote.Config.FRAGMENT_ERROR_EXIT;
import static com.example.xyzreader.remote.Config.FRAGMENT_ERROR_TAG;
import static com.example.xyzreader.remote.Config.FRAGMENT_ERROR_WAIT;

public class ArticleListActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

// TODO Palette to Detail Load status bar
// TODO Glide load support when transition
// TODO Landscape bottom bar to mode to side and add side margins to text
// TODO landscape add instructive movement
// TODO Cancel loader when click if not finished  , made simple block on click
// TODO ProgressBar on ScrollY() ???
// TODO Layouts on WXGA
// TODO BROADCAST ACTION in Exception of UpdateService and mIsRefreshing


// TODO buttons in bottom view
// TODO mPagerAdapter setCurrentItemId() add function

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

    // viewpager
//    private ViewPager mPager;
//    private ViewPagerAdapter mPagerAdapter;
    private Resources mRes;
    private boolean mIsWide;
    private boolean mIsLand;
    private long mStartingItemId;

    private Cursor mCursor;
    private FragmentArticleListActivity mFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
//        postponeEnterTransition();

// transition
        mSharedCallback = setupSharedCallback();
        setExitSharedElementCallback(mSharedCallback);
        setContentView(R.layout.activity_container_main);  // transition set in styles

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = FragmentArticleListActivity.newInstance();
        fm.beginTransaction()
                .replace(R.id.container1, fragment)
                .commit();

        mFragment = (FragmentArticleListActivity)fragment;

        getSupportLoaderManager().initLoader(ARTICLE_LIST_LOADER_ID, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_article_list, menu);
        return true;
    }




    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        mTmpReenterState = new Bundle(data.getExtras());

// test!!!
        if(mRecyclerView == null) return;



        int startingItemPosition = mTmpReenterState.getInt(BUNDLE_STARTING_ITEM_POS);
        int currentItemPosition = mTmpReenterState.getInt(BUNDLE_CURRENT_ITEM_POS);
        if (startingItemPosition != currentItemPosition) {
            mRecyclerView.scrollToPosition(currentItemPosition); // scroll RecyclerView
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
        if (cursor != null && cursor.getCount() == 0) return;
        mCursor = cursor;

        if (mFragment != null) {
            mFragment.swap(mCursor);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    public Cursor getCursor() {
        return mCursor;
    }





    private void defaultTransition(List<String> names, Map<String, View> sharedElements) {
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

    private List<View> getSharedViews(int position) {
        RecyclerView.ViewHolder holder = mRecyclerView.findViewHolderForAdapterPosition(position);
        List<View> list = new ArrayList<>();
        if (holder == null || holder.itemView == null) {
            return list;
        }

        View view = holder.itemView;
        list.add(view.findViewById(R.id.article_image));
        list.add(view.findViewById(R.id.article_title));
        return list;
    }


    private SharedElementCallback setupSharedCallback() {
        return new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                if (mTmpReenterState != null) {
                    int startingItemPosition = mTmpReenterState.getInt(BUNDLE_STARTING_ITEM_POS);
                    int currentItemPosition = mTmpReenterState.getInt(BUNDLE_CURRENT_ITEM_POS);
                    if (startingItemPosition != currentItemPosition) {
                        List<View> list = getSharedViews(currentItemPosition);

                        if (list.isEmpty()) {
                            defaultTransition(names, sharedElements);
                            mTmpReenterState = null;
                            return;
                        }
                        names.clear();
                        sharedElements.clear();
                        for (View sharedElement : list) {
                            if (sharedElement == null) continue;
                            names.add(sharedElement.getTransitionName());
                            sharedElements.put(sharedElement.getTransitionName(), sharedElement);
                        }
                        mTmpReenterState = null;
                    } else {
                        defaultTransition(names, sharedElements);
                    }
                }
            }

        };

    }
}
