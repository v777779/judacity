package com.example.xyzreader.ui;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.SharedElementCallback;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
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
import android.transition.TransitionInflater;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowInsets;
import android.view.animation.Interpolator;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Scroller;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;
import com.example.xyzreader.data.UpdaterService;
import com.example.xyzreader.remote.Config;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;

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
        LoaderManager.LoaderCallbacks<Cursor>, ICallback {

// TODO isInstructive move to Config
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
    private static boolean sIsInstructed;

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
    private ViewPager mPager;
    private ViewPagerAdapter mPagerAdapter;
    private Resources mRes;
    private boolean mIsWide;
    private boolean mIsLand;
    private long mStartingItemId;
    private int mStartingItemPosition;
    private int mCurrentItemPosition;

    private Cursor mCursor;
    private FragmentDetailActivity mFragment;


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
                int offsetTop = res.getDimensionPixelSize(R.dimen.micro_margin);
                int offsetBottom = res.getDimensionPixelOffset(R.dimen.recycler_bottom_offset);
                int offsetSide = res.getDimensionPixelOffset(R.dimen.micro_margin);

                if ((getWindow().getDecorView().getWindowSystemUiVisibility() & (int) View.SYSTEM_UI_FLAG_FULLSCREEN) !=
                        View.SYSTEM_UI_FLAG_FULLSCREEN) {
                    offsetTop = res.getDimensionPixelSize(R.dimen.micro_margin) + sysBarHeight;
                }

                mRecyclerView.setPadding(offsetSide, offsetTop, offsetSide, offsetBottom);

                int offsetSwipe = res.getDimensionPixelSize(R.dimen.progress_swipe_offset) + sysBarHeight;
                mSwipeRefreshLayout.setProgressViewEndTarget(true, offsetSwipe);

// wide
                if (mIsWide && mIsLand) {
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

// wide
        if (mIsWide && mIsLand) {
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
            mPager.setVisibility(View.INVISIBLE);
            mPager.setPageTransformer(false, new PageTransformer());
//            mPager.setScrollDurationFactor(1);


// wide
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
        if (id == R.id.action_fullscreen) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN
            );
            getSupportActionBar().hide();
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

        if (mFragment != null) {
            mFragment.swap(mCursor);
        }
// wide
        if (mIsWide && mIsLand) {
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
        if (!(mIsWide && mIsLand)) {

            startActivity(intent, optionsCompat.toBundle());
        }

// wide
        if (mIsWide && mIsLand) {
            mStartingItemId = id;
            mStartingItemPosition = pos;


            if (mCachedBitmap != null) {
                ImageView viewPagerImage = findViewById(R.id.fragment_image);
                viewPagerImage.setImageBitmap(mCachedBitmap);

                View viewPagerBackground = findViewById(R.id.viewpager_background);
                viewPagerBackground.setBackgroundColor(mCachedColor);
                viewPagerBackground.setAlpha(1f);
            }

            mPagerAdapter.setStartingItemId(mStartingItemId);
            mPager.setCurrentItem(mStartingItemPosition, true);
            mPager.setVisibility(View.VISIBLE);



//            intent = new Intent(this, ArticleListActivity.class);  // does not work push in stack and fading flashes all screen
//            intent.putExtra(BUNDLE_STARTING_ITEM_ID, id);
//            intent.putExtra(BUNDLE_STARTING_ITEM_POS, pos);
//
//            startActivity(intent, optionsCompat.toBundle());


//            Fragment fragment = FragmentDetailActivity.newInstance(mStartingItemId, mStartingItemPosition);
//            FragmentManager fm = getSupportFragmentManager();
//            fm.beginTransaction()
//                    .addSharedElement(mImage, mImage.getTransitionName())
//                    .addSharedElement(mTitle, mTitle.getTransitionName())
//                    .replace(R.id.fragment_container,fragment)
//                    .commit();


//            Fragment fragment = ArticleDetailFragment.newInstance(mStartingItemId, mStartingItemId);
//            FragmentManager fm = getSupportFragmentManager();
//            fm.popBackStack("transaction",FragmentManager.POP_BACK_STACK_INCLUSIVE);
//
//
//            fragment.setEnterTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.explode));
//            fragment.setSharedElementEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.transform));
//
//
//            getSupportFragmentManager()
//                    .beginTransaction()
//                    .addSharedElement(mImage, "image1221")
////                    .addSharedElement(mTitle, mTitle.getTransitionName())
//                    .replace(R.id.fragment_container,fragment)
//                    .addToBackStack("transaction")
//                    .commit();
//


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

        instructiveMotion(fragment.getRootView());
    }

    private int mCachedColor;
    private Bitmap mCachedBitmap;

    @Override
    public void onCallback(View view) {
        instructiveMotion(view);
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


    private void instructiveMotion(View view) {
        // instructive motion
        if(view == null || !mIsLand) return;
        if (!ArticleListActivity.sIsInstructed && mPager.getVisibility() == View.VISIBLE) {
            int startScrollPos = getResources().getDimensionPixelOffset(R.dimen.instructive_scroll);
            AnimatorSet as = new AnimatorSet();

            as.playSequentially(
                    ObjectAnimator.ofInt(view, "scrollY", 0).setDuration(0),
                    ObjectAnimator.ofInt(view, "scrollY", startScrollPos).setDuration(550),
                    ObjectAnimator.ofInt(view, "scrollY", 0).setDuration(750)

            );
            as.setStartDelay(500);
//        as.setDuration(2350);
            as.start();
            ArticleListActivity.sIsInstructed = true;
        }

    }

}
