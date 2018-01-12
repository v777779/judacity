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
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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

public class FragmentArticleListActivity extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>, ICallback {

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
    private int mStartingItemPosition;
    private int mCurrentItemPosition;

    private Cursor mCursor;
    private FragmentDetailActivity mFragment;

    private View mRootView;


    public static Fragment newInstance() {
        Bundle arguments = new Bundle();
        FragmentArticleListActivity fragment = new FragmentArticleListActivity();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
//        postponeEnterTransition();

// transition
//        mSharedCallback = setupSharedCallback();
//        setExitSharedElementCallback(mSharedCallback);

//        getLoaderManager().initLoader(ARTICLE_LIST_LOADER_ID, null, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.activity_article_main,container,false);



// bind
        mToolbar = mRootView.findViewById(R.id.toolbar_main);
        mToolbarLogo = mRootView.findViewById(R.id.toolbar_logo);
        mSwipeRefreshLayout = mRootView.findViewById(R.id.swipe_refresh);
        mRecyclerView = mRootView.findViewById(R.id.recycler_view);
        mProgressBar = mRootView.findViewById(R.id.progress_bar);
// wide
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

                if ((getActivity().getWindow().getDecorView().getWindowSystemUiVisibility() & (int) View.SYSTEM_UI_FLAG_FULLSCREEN) !=
                        View.SYSTEM_UI_FLAG_FULLSCREEN) {
                    offsetTop = res.getDimensionPixelSize(R.dimen.micro_margin) + sysBarHeight;
                }

                mRecyclerView.setPadding(offsetSide, offsetTop, offsetSide, offsetBottom);

                int offsetSwipe = res.getDimensionPixelSize(R.dimen.progress_swipe_offset) + sysBarHeight;
                mSwipeRefreshLayout.setProgressViewEndTarget(true, offsetSwipe);

// wide
                if (mIsWide && mIsLand) {
                    ViewGroup.MarginLayoutParams lp;

                    lp = (ViewGroup.MarginLayoutParams) ((View)mRootView.findViewById(R.id.fragment_background)).getLayoutParams();
                    lp.setMargins(lp.leftMargin, offsetTop, lp.rightMargin, lp.bottomMargin);

                    lp = (ViewGroup.MarginLayoutParams) ((View)mRootView.findViewById(R.id.fragment_image)).getLayoutParams();
                    lp.setMargins(lp.leftMargin, offsetTop, lp.rightMargin, lp.bottomMargin);
// TODO change name to viewpager_container
//                    lp = (ViewGroup.MarginLayoutParams) ((View)mRootView.findViewById(R.id.fragment_container)).getLayoutParams();
//                    lp.setMargins(lp.leftMargin, offsetTop, lp.rightMargin, lp.bottomMargin);
// wide
                }
                return windowInsets;
            }
        });


        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        mToolbarLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("");

        }

        setupRecycler();
        setupSwipeRefresh();


        if (savedInstanceState == null) {
            refresh(ACTION_TIME_REFRESH);
        }

//// wide
//        if (mIsWide && mIsLand) {
//// viewpager
//            mStartingItemPosition = -1;
//            Resources res = getResources();
//            mPager = findViewById(R.id.viewpager_container);
//            mPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), this);
//            mPager.setAdapter(mPagerAdapter);
//            mPager.setPageMargin((int) TypedValue
//                    .applyDimension(TypedValue.COMPLEX_UNIT_DIP,
//                            res.getInteger(R.integer.pager_side_margin), res.getDisplayMetrics()));
//            mPager.setPageMarginDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.colorPagerMargin)));
//
//            mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//                @Override
//                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                }
//
//                @Override
//                public void onPageSelected(int position) {
//                    mCurrentItemPosition = position;
//                }
//
//                @Override
//                public void onPageScrollStateChanged(int state) {
//                }
//            });
//            mPager.setVisibility(View.INVISIBLE);
// wide
//        }


        return mRootView;
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_article_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
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
            getActivity().getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN
            );
            ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BROADCAST_ACTION_UPDATE_STARTED);
        intentFilter.addAction(BROADCAST_ACTION_NO_NETWORK);
        intentFilter.addAction(BROADCAST_ACTION_UPDATE_FINISHED);
        getActivity().registerReceiver(mRefreshingReceiver, intentFilter);

    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(mRefreshingReceiver);

    }


    public void swap(Cursor cursor) {
        onLoadFinished(null, cursor);
    }

    // callbacks
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newAllArticlesInstance(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (cursor != null && cursor.getCount() == 0) return;
        mCursor = cursor;
        ((RecyclerAdapter) mRecyclerView.getAdapter()).setCursor(cursor);

        if(mFragment != null) {
            mFragment.swap(mCursor);
        }
// wide
//        if (mIsWide && mIsLand) {
//            mPagerAdapter.swap(cursor);
//            mPagerAdapter.setStartingItemId(mStartingItemId);
//        }

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
                    getActivity(), p1, p2);
        if (!(mIsWide && mIsLand)) {

//            startActivity(intent, optionsCompat.toBundle());
        }

// wide
        if (!(mIsWide && mIsLand)) {
            mStartingItemId = id;
            mStartingItemPosition = pos;

//            mPagerAdapter.setStartingItemId(mStartingItemId);
//            mPager.setCurrentItem(mStartingItemPosition);
//            mPager.setVisibility(View.VISIBLE);

//            Fragment fragment = FragmentDetailActivity.newInstance(mStartingItemId, mStartingItemPosition);
//            FragmentManager fm = getSupportFragmentManager();
//            fm.beginTransaction()
//                    .addSharedElement(mImage, mImage.getTransitionName())
//                    .addSharedElement(mTitle, mTitle.getTransitionName())
//                    .replace(R.id.fragment_container,fragment)
//                    .commit();


            Fragment fragment = FragmentDetailActivity.newInstance(mStartingItemId, mStartingItemPosition);
            FragmentManager fm = getFragmentManager();
            fm.popBackStack("transaction",FragmentManager.POP_BACK_STACK_INCLUSIVE);


//            fragment.setEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.explode));



            getFragmentManager()
                    .beginTransaction()
                    .addSharedElement(mImage, mImage.getTransitionName())
//                    .addSharedElement(mTitle, mTitle.getTransitionName())
                    .replace(R.id.container1,fragment)
                    .addToBackStack("transaction")
                    .commit();

//              getSupportFragmentManager().executePendingTransactions();


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
                getActivity().finish();
                break;

            default:
        }
    }

    @Override
    public void onCallback(ArticleDetailFragment fragment) {  // DetailActivity transition support

    }

    // common methods
    public void hideRefreshingUI() {
        mIsSwipeRefresh = false;
        mIsRefreshing = false;
        mSwipeRefreshLayout.setRefreshing(mIsRefreshing);
        mProgressBar.setVisibility(mIsRefreshing ? View.VISIBLE : View.INVISIBLE);
    }

    public void updateRefreshingUI() {
        if (mIsSwipeRefresh) {
            mSwipeRefreshLayout.setRefreshing(mIsRefreshing);
            mIsSwipeRefresh = mIsRefreshing;        // switch mIsRefresh at second broadcastReceive
        } else {
            mProgressBar.setVisibility(mIsRefreshing ? View.VISIBLE : View.INVISIBLE);
        }

    }

    private void showErrorDialog(int[] parameters) {
        FragmentError fragment = FragmentError.newInstance(parameters);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(fragment, FRAGMENT_ERROR_TAG);
        ft.commit();
    }


    private void setupRecycler() {
        Config.Span sp = Config.getDisplayMetrics((AppCompatActivity)getActivity(), mRootView);

        RecyclerAdapter adapter = new RecyclerAdapter(this, sp);
        adapter.setHasStableIds(true);
        mRecyclerView.setAdapter(adapter);
        GridLayoutManager layoutManager = new GridLayoutManager(
                getActivity(),
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
        getActivity().startService(new Intent(action, null, getActivity(), UpdaterService.class));
    }

    private void defaultTransition(List<String> names, Map<String, View> sharedElements) {
        // If mTmpReenterState is null, then the activity is exiting.
        View navigationBar = mRootView.findViewById(android.R.id.navigationBarBackground);
        View statusBar = mRootView.findViewById(android.R.id.statusBarBackground);
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