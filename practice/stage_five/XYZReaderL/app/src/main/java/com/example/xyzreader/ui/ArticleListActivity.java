package com.example.xyzreader.ui;


import android.app.SharedElementCallback;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.ImageButton;
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
import static com.example.xyzreader.remote.Config.BUNDLE_FULL_SCREEN_MODE;
import static com.example.xyzreader.remote.Config.BUNDLE_STARTING_ITEM_ID;
import static com.example.xyzreader.remote.Config.BUNDLE_STARTING_ITEM_POS;
import static com.example.xyzreader.remote.Config.CALLBACK_FRAGMENT_CLOSE;
import static com.example.xyzreader.remote.Config.CALLBACK_FRAGMENT_EXIT;
import static com.example.xyzreader.remote.Config.CALLBACK_FRAGMENT_FULLSCREEN;
import static com.example.xyzreader.remote.Config.CALLBACK_FRAGMENT_RETRY;
import static com.example.xyzreader.remote.Config.EXTRA_EMPTY_CURSOR;
import static com.example.xyzreader.remote.Config.FRAGMENT_ERROR_CLOSE;
import static com.example.xyzreader.remote.Config.FRAGMENT_ERROR_EXIT;
import static com.example.xyzreader.remote.Config.FRAGMENT_ERROR_TAG;
import static com.example.xyzreader.remote.Config.FRAGMENT_ERROR_WAIT;
import static com.example.xyzreader.remote.Config.FULL_SCREEN_MODE_OFF;
import static com.example.xyzreader.remote.Config.FULL_SCREEN_MODE_ON;
import static com.example.xyzreader.remote.Config.instructiveMotion;
import static com.example.xyzreader.remote.RemoteEndpointUtil.loadPreferenceFullScreen;
import static com.example.xyzreader.remote.RemoteEndpointUtil.loadPreferenceSwipe;
import static com.example.xyzreader.remote.RemoteEndpointUtil.savePreferences;


public class ArticleListActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, ICallback {

// _TODO isInstructive move to Config
// TODO Instructive motion after rotation while in middle text position


// TODO Palette to Detail Load status bar


// _TODO Glide load support when transition

// TODO Landscape bottom bar to mode to side and add side margins to text

// TODO landscape add instructive movement
// TODO Cancel loader when click if not finished  , made simple block on click
// TODO Layouts on WXGA
// TODO BROADCAST ACTION in Exception of UpdateService and mIsRefreshing
// _TODO status bar hide on second screen
// TODO BottomBar buttons
// _TODO Text overlap in landscape 1st element recycler

// _TODO Settings   fullscreen always, swipe off

// _TODO buttons in bottom view and smart scrolling tracking

// TODO move 600 to 800 and make new layout 600
// TODO mPagerAdapter setCurrentItemId() add function
// TODO  mRes add to all activities
// TODO Code Inspection


    private static boolean mIsTimber;

    private Toolbar mToolbar;
    private ImageView mToolbarLogo;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;

    // bottom bar
    private View mBottomBar;
    private ImageButton mImageButtonHome;
    private ImageButton mImageButtonFullScreen;


    private BroadcastReceiver mRefreshingReceiver;
    private boolean mIsSwipeRefresh;
    private boolean mIsRefreshing;   // progress and check enter to second activity

    // transition
    private Bundle mTmpReenterState;
    private SharedElementCallback mSharedCallback;

    // viewpager
    private ViewPager mPager;
    private ViewPagerAdapter mPagerAdapter;
    private Resources mRes;
    private boolean mIsWide;
    private boolean mIsLand;
    private long mStartingItemId;
    private int mStartingItemPosition;
    private int mCurrentItemPosition;


    private Cursor mCursor;
    private ArticleDetailFragment mFragmentPage;

    // preferences
    private boolean mIsFullScreen;
    private boolean mIsFullScreenMode;
    private boolean mIsSwipeMode;

    private boolean mIsStarting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        postponeEnterTransition();

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
// wide
        mPager = findViewById(R.id.viewpager_container);
//        mPager.setScrollDurationFactor(2);

        mRes = getResources();
        mIsWide = mRes.getBoolean(R.bool.is_wide);
        mIsLand = mRes.getBoolean(R.bool.is_land);
// preferences
        mIsFullScreenMode = loadPreferenceFullScreen(this);
        mIsSwipeMode = loadPreferenceSwipe(this);

// timber
        if (!mIsTimber) {
            Timber.plant(new Timber.DebugTree());
            mIsTimber = true;
        }

        if (savedInstanceState == null) {
            refresh(ACTION_TIME_REFRESH);
            mIsFullScreen = mIsFullScreenMode;
        } else {
            mIsFullScreen = savedInstanceState.getBoolean(BUNDLE_FULL_SCREEN_MODE, mIsFullScreenMode);
        }


// toolbar


        mToolbar.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                view.onApplyWindowInsets(windowInsets);
                Resources res = getResources();
                int sysBarHeight = windowInsets.getSystemWindowInsetTop() + mToolbar.getLayoutParams().height;
                int offsetTop = res.getDimensionPixelSize(R.dimen.micro_margin);
                int offsetBottom = res.getDimensionPixelOffset(R.dimen.recycler_bottom_offset);
                int offsetSide = res.getDimensionPixelOffset(R.dimen.micro_margin);

                if (!mIsFullScreen) {
                    offsetTop = res.getDimensionPixelSize(R.dimen.micro_margin) + sysBarHeight;
                }
                mRecyclerView.setPadding(offsetSide, offsetTop, offsetSide, offsetBottom);


                int offsetSwipe = res.getDimensionPixelSize(R.dimen.progress_swipe_offset) + sysBarHeight;
                mSwipeRefreshLayout.setProgressViewEndTarget(true, offsetSwipe);
// wide
                if (mIsWide) {
                    ViewGroup.MarginLayoutParams lp;


                    lp = (ViewGroup.MarginLayoutParams) (findViewById(R.id.viewpager_container)).getLayoutParams();
                    lp.setMargins(lp.leftMargin, offsetTop, lp.rightMargin, lp.bottomMargin);

                    lp = (ViewGroup.MarginLayoutParams) (findViewById(R.id.border_view)).getLayoutParams();
                    lp.setMargins(lp.leftMargin, offsetTop, lp.rightMargin, lp.bottomMargin);

                    lp = (ViewGroup.MarginLayoutParams) (findViewById(R.id.fragment_image)).getLayoutParams();
                    lp.setMargins(lp.leftMargin, offsetTop, lp.rightMargin, lp.bottomMargin);

                    lp = (ViewGroup.MarginLayoutParams) (findViewById(R.id.viewpager_background)).getLayoutParams();
                    lp.setMargins(lp.leftMargin, offsetTop, lp.rightMargin, lp.bottomMargin);


// wide
                }
                return windowInsets;
            }
        });

// action bar
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


// wide
        if (mIsWide) {
// viewpager
            mStartingItemPosition = -1;
            Resources res = getResources();
            mPager = findViewById(R.id.viewpager_container);
            mPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), this);
            mPager.setAdapter(mPagerAdapter);
            mPager.setPageMargin((int) TypedValue
                    .applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                            res.getInteger(R.integer.pager_side_margin), res.getDisplayMetrics()));
            mPager.setPageMarginDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.colorPagerMargin)));

            mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    mCurrentItemPosition = position;

                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });

            if(mIsStarting) {
                mPager.setVisibility(View.GONE);
            }

            mPager.setPageTransformer(false, new PageTransformer());
//            mPager.setScrollDurationFactor(1);
        }

// bottom bar
        mBottomBar = findViewById(R.id.bottom_toolbar);
        mImageButtonHome = findViewById(R.id.image_button_home);
        mImageButtonFullScreen = findViewById(R.id.image_button_fullscreen);

        if (mImageButtonHome != null) {
            mImageButtonHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }
        if (mImageButtonFullScreen != null) {
            mImageButtonFullScreen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setFullScreen(FULL_SCREEN_MODE_OFF);
                }
            });
        }
//// instructive motion
//        if(mIsFullScreen ) {
//           instructiveMotion(this, mBottomBar);
//        }

// full screen
        setFullScreen(mIsFullScreen);

// test!!!!
        Config.sIsInstructedLand = false;
        Config.sIsInstructedPort = false;
        Config.sIsInstructedBottomLand = false;
        Config.sIsInstructedBottomPort = false;

        getSupportLoaderManager().initLoader(ARTICLE_LIST_LOADER_ID, null, this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(BUNDLE_FULL_SCREEN_MODE, mIsFullScreen);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_article_list, menu);
        menu.findItem(R.id.item_swipe).setChecked(mIsSwipeMode);
        menu.findItem(R.id.item_full_screen).setChecked(mIsFullScreenMode);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.item_swipe) {
            if (item.isChecked()) {
                item.setChecked(false);
            } else {
                item.setChecked(true);
            }
            mIsSwipeMode = item.isChecked();
            mSwipeRefreshLayout.setEnabled(mIsSwipeMode);
            savePreferences(this, mIsSwipeMode, mIsFullScreenMode);
            return true;
        }
        if (id == R.id.item_full_screen) {
            if (item.isChecked()) {
                item.setChecked(false);
            } else {
                item.setChecked(true);
            }
            mIsFullScreenMode = item.isChecked();
            savePreferences(this, mIsSwipeMode, mIsFullScreenMode);
            return true;
        }
        if (id == R.id.action_fullscreen) {
            setFullScreen(FULL_SCREEN_MODE_ON);
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

// instructive motion
        if (mIsFullScreen) {
            instructiveMotion(this, mBottomBar);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mRefreshingReceiver);

    }


    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        mTmpReenterState = new Bundle(data.getExtras());
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
        ((RecyclerAdapter) mRecyclerView.getAdapter()).setCursor(cursor);

// wide
        if (mIsWide) {
            mPagerAdapter.swap(cursor);
            mPagerAdapter.setStartingItemId(mStartingItemId);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }


    @Override
    public void onCallback(View view, int pos) {
        if (mIsRefreshing) {
            showErrorDialog(FRAGMENT_ERROR_WAIT);
            return;
        }

        long id = mRecyclerView.getAdapter().getItemId(pos);
        Uri uri = ItemsContract.Items.buildItemUri(id);

        View mImage = view.findViewById(R.id.article_image);
        View mTitle = view.findViewById(R.id.article_title);
        View mSubTitle = view.findViewById(R.id.article_subtitle);

        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.putExtra(BUNDLE_STARTING_ITEM_ID, id);
        intent.putExtra(BUNDLE_STARTING_ITEM_POS, pos);

// works but no used
//        Intent intent = new Intent(this, ArticleDetailActivity.class);
//        intent.putExtra(BUNDLE_STARTING_ITEM_ID, ItemsContract.Items.getItemId(uri));

        Pair<View, String> p1 = Pair.create(mImage, mImage.getTransitionName());  // unique name
        Pair<View, String> p2 = Pair.create(mTitle, mTitle.getTransitionName());

        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this, p1, p2);
        if (!(mIsWide)) {
            startActivity(intent, optionsCompat.toBundle());
            return;
        }

// wide
        if (mIsWide) {
            mStartingItemId = id;
            mStartingItemPosition = pos;

            if (mCachedBitmap != null) {
                ImageView viewPagerImage = findViewById(R.id.fragment_image);
                viewPagerImage.setImageBitmap(mCachedBitmap);

                View viewPagerBackground = findViewById(R.id.viewpager_background);
                viewPagerBackground.setBackgroundColor(mCachedColor);
                viewPagerBackground.setAlpha(1f);
            }

// reload first invisible fragment to support instructive transition
            if (mStartingItemPosition == 0) {
                mPagerAdapter.notifyDataSetChanged();
            }

            mPagerAdapter.setStartingItemId(mStartingItemId);
            mPager.setCurrentItem(mStartingItemPosition, false);
            mPager.setVisibility(View.VISIBLE);
        }
    }


    private boolean isValidId(long id) {
        if (mCursor == null) return false;
        for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
            if (mCursor.getLong(ArticleLoader.Query._ID) == id) {
                return true;
            }
        }
        int k = 21;
        return false;
    }


    public Cursor getCursor() {
        return mCursor;
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

            case CALLBACK_FRAGMENT_FULLSCREEN:
                if(mIsFullScreen) {
                    setFullScreen(FULL_SCREEN_MODE_OFF);
                }
                break;
            default:
        }
    }

    @Override
    public void onCallback(ArticleDetailFragment fragment) {  // DetailActivity transition support
        if (fragment == null) {
            mCachedBitmap = null;
            return;
        }
        Bitmap bitmap = fragment.getBitmap();
        if (bitmap != null) {
            mCachedBitmap = bitmap;
            Palette p = Palette.generate(bitmap, 12);
            mCachedColor = p.getDarkMutedColor(ContextCompat.getColor(this, R.color.colorBackMask));
        } else {
            mCachedBitmap = null;
        }


// instructive motion  viewpager here only
        if (fragment != null && mPager.getVisibility() == View.VISIBLE) {
            instructiveMotion(fragment);
        }

        mFragmentPage = fragment;
    }

    private int mCachedColor;
    private Bitmap mCachedBitmap;

    // TODO remove callback
// TODO move private variables to top
    @Override
    public void onCallback(View view) {

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

        RecyclerAdapter adapter = new RecyclerAdapter(this, sp);
        adapter.setHasStableIds(true);
        mRecyclerView.setAdapter(adapter);
        GridLayoutManager layoutManager = new GridLayoutManager(
                this,
                sp.getSpanX(),
                GridLayout.VERTICAL,
                false);
        mRecyclerView.setLayoutManager(layoutManager);

// swipe check
//        mRecyclerView.setOnFlingListener(new RecyclerView.OnFlingListener() {
//            @Override
//            public boolean onFling(int velocityX, int velocityY) {
//                return false;
//            }
//        });

    }

    private void setupSwipeRefresh() {

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mIsSwipeRefresh = true;
                refresh(ACTION_SWIPE_REFRESH);
            }
        });

        mSwipeRefreshLayout.setEnabled(mIsSwipeMode);  // enabled or disabled

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

    private class PageTransformer implements ViewPager.PageTransformer {

        @Override
        public void transformPage(View page, float position) {
// anim1
//            page.setTranslationX(page.getWidth()*-position);
//
//            if(position <= -1.0F || position >= 1.0F) {
//                page.setAlpha(0.0F);
//            } else if( position == 0.0F ) {
//                page.setAlpha(1.0F);
//            } else {
//                // position is between -1.0F & 0.0F OR 0.0F & 1.0F
//                page.setAlpha(1.0F - Math.abs(position));
//            }


            View dummyImageView = page.findViewById(R.id.article_image);
            int pageWidth = page.getWidth();


            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                page.setAlpha(1);


            } else if (position <= 1) { // [-1,1]

                dummyImageView.setTranslationX(-position * (pageWidth / 2)); //Half the normal speed

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                page.setAlpha(1);
            }
// test!!!
//            page.setAlpha(0.0f);

        }
    }

    private void setFullScreen(boolean isFullScreen) {
        Window w = getWindow(); // in Activity's onCreate() for instance
        ActionBar actionBar = getSupportActionBar();
        if (isFullScreen) {
            mIsFullScreen = true;
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN
            );

            if (actionBar != null) {
                actionBar.hide();
            }
            if (mBottomBar != null) {
                mBottomBar.setVisibility(View.VISIBLE);
            }
            if (!mIsWide) {  // flags for non tablet
                w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            }

        } else {
            mIsFullScreen = false;
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            if (actionBar != null) {
                actionBar.show();
            }
            if (mBottomBar != null) {
                mBottomBar.setVisibility(View.INVISIBLE);
            }
            w.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            if (mIsWide) {                    // updates top border of recycler and viewpager
                mPager.getAdapter().notifyDataSetChanged();

                mRecyclerView.scrollToPosition(mCurrentItemPosition);
            }
        }

    }


}
