package com.example.xyzreader.ui;


import android.app.SharedElementCallback;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.xyzreader.remote.Config.ARTICLE_DETAIL_LOADER_ID;
import static com.example.xyzreader.remote.Config.BUNDLE_CURRENT_ITEM_POS;
import static com.example.xyzreader.remote.Config.BUNDLE_STARTING_ITEM_ID;
import static com.example.xyzreader.remote.Config.BUNDLE_STARTING_ITEM_POS;
import static com.example.xyzreader.remote.Config.instructiveMotion;

public class ArticleDetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, ICallback {

    private static boolean sIsInstructed;

    private ViewPager mPager;
    private ViewPagerAdapter mPagerAdapter;
    //    private ScreenSlidePagerAdapter mPagerAdapter;
    private Cursor mCursor;

    private long mStartingItemId;
    private int mStartingItemPosition;
    private int mCurrentItemPosition;
    private boolean mIsStartingActivity;

    // transition
    private ArticleDetailFragment mCurrentFragment;
    private boolean mIsReturning;


    public SharedElementCallback mSharedCallback;

    // land
    private boolean mIsLand;
    private boolean mIsWide;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

// transition
        mSharedCallback = setupSharedCallback();
        postponeEnterTransition();
        setEnterSharedElementCallback(mSharedCallback);

        //setContentView(R.layout.activity_article_detail);
        setContentView(R.layout.activity_article_detail);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);



// bundle
// works but not used
//            if (getIntent() != null && getIntent().getData() != null) {
//                mStartingItemId = ItemsContract.Items.getItemId(getIntent().getData());  // from Uri
//            }
        if (savedInstanceState == null) {
            if (getIntent() != null) {
                mStartingItemId = getIntent().getLongExtra(BUNDLE_STARTING_ITEM_ID, 0);
                mStartingItemPosition = getIntent().getIntExtra(BUNDLE_STARTING_ITEM_POS, 0);
            }
            mCurrentItemPosition = mStartingItemPosition;
        } else {
            mStartingItemPosition = savedInstanceState.getInt(BUNDLE_STARTING_ITEM_POS);
            mCurrentItemPosition = savedInstanceState.getInt(BUNDLE_CURRENT_ITEM_POS);

        }
        mIsStartingActivity = savedInstanceState == null;

        Resources res = getResources();
        mIsLand = res.getBoolean(R.bool.is_land);
        mIsWide = res.getBoolean(R.bool.is_wide);

// viewpager
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

//        mPager = (ViewPager) findViewById(R.id.viewpager_container);
//        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
//        mPager.setAdapter(mPagerAdapter);
//        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
//            @Override
//            public void onPageSelected(int position) {
//                // When changing pages, reset the action bar actions since they are dependent
//                // on which page is currently active. An alternative approach is to have each
//                // fragment expose actions itself (rather than the activity exposing actions),
//                // but for simplicity, the activity provides the actions in this sample.
//                invalidateOptionsMenu();
//            }
//        });

        mPager.setVisibility(View.GONE);
//        mPager.setCurrentItem(4);


        getSupportLoaderManager().initLoader(ARTICLE_DETAIL_LOADER_ID, null, this);
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        return super.onOptionsItemSelected(item);
//    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(BUNDLE_STARTING_ITEM_POS, mStartingItemPosition);
        outState.putLong(BUNDLE_CURRENT_ITEM_POS, mCurrentItemPosition);

    }


    @Override
    public void finishAfterTransition() {
        mIsReturning = true;                            // before super()
        Intent intent = new Intent();
        intent.putExtra(BUNDLE_STARTING_ITEM_POS, mStartingItemPosition);
        intent.putExtra(BUNDLE_CURRENT_ITEM_POS, mCurrentItemPosition);
        setResult(RESULT_OK, intent);
        super.finishAfterTransition();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newAllArticlesInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0) return;
        mCursor = cursor;

        mPagerAdapter.swap(mCursor);
        mPager.setCurrentItem(mCurrentItemPosition);
        mPager.setVisibility(View.VISIBLE);

        if (mIsStartingActivity) {
            for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
                if (mCursor.getLong(ArticleLoader.Query._ID) == mStartingItemId) {
                    return;
                }
            }
// TODO stop loading service optimization
// bug fix  if cursor obsoleted exit from activity

            Toast.makeText(this,getString(R.string.cursor_message), Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursor = null;
        mPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCallback(View view, int position) {
    }

    @Override
    public void onCallback(int mode) {
    }

// TODO remove method
    private List<View> getSharedViews(View view) {
        List<View> list = new ArrayList<>();
        list.add(view.findViewById(R.id.article_image));
        list.add(view.findViewById(R.id.article_title));
        return list;
    }

    private void copySystemSharedElements(List<String> names, Map<String, View> sharedElements) {
        List<String> cloneList = new ArrayList<>(names);
        Map<String, View> cloneMap = new HashMap<>(sharedElements);
        names.clear();
        sharedElements.clear();
        for (int i = 0; i < cloneList.size(); i++) {
            String name = cloneList.get(i);
            if (name.contains("android")) {
                names.add(name);
                sharedElements.put(name, cloneMap.get(name));
            }
        }

    }

    @Override
    public void onCallback(ArticleDetailFragment fragment) {
        mCurrentFragment = fragment;

        if(fragment != null) {
            instructiveMotion(fragment);
        }

    }

    @Override
    public void onCallback(View view) {

    }

    private SharedElementCallback setupSharedCallback() {
        return new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                if (mIsReturning) {
// bug fix
                    View fab = mCurrentFragment.getView().findViewById(R.id.fab);  // off FAB
                    fab.setVisibility(View.GONE);

                    List<View> list = mCurrentFragment.getSharedViews();
                    copySystemSharedElements(names, sharedElements);             // smart clear
                    for (View sharedElement : list) {
                        sharedElements.put(sharedElement.getTransitionName(), sharedElement);
                        names.add(sharedElement.getTransitionName());
                    }
                }
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }
}
