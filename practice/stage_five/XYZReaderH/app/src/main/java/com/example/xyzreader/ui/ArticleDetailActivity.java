package com.example.xyzreader.ui;


import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Slide;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;

import timber.log.Timber;

import static com.example.xyzreader.remote.Config.BUNDLE_CURRENT_ITEM_ID;
import static com.example.xyzreader.remote.Config.BUNDLE_STARTING_ITEM_ID;

public class ArticleDetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, ICallback {

    private ViewPager mPager;
    private ArticleDetailAdapter mPagerAdapter;
    //    private ScreenSlidePagerAdapter mPagerAdapter;
    private Cursor mCursor;

    private long mStartingItemId;
    private long mCurrentItemId;
    private boolean mIsStartingActivity;

    // transition
    private ArticleDetailFragment mCurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        postponeEnterTransition();

        setContentView(R.layout.activity_article_detail);


// bundle
// works but not used
//            if (getIntent() != null && getIntent().getData() != null) {
//                mStartingItemId = ItemsContract.Items.getItemId(getIntent().getData());  // from Uri
//            }
        if (savedInstanceState == null) {
            if(getIntent()!=null) {
                mStartingItemId = getIntent().getLongExtra(BUNDLE_STARTING_ITEM_ID,0);
            }
            mCurrentItemId = mStartingItemId;
        } else {
            mStartingItemId = savedInstanceState.getLong(BUNDLE_STARTING_ITEM_ID);
            mCurrentItemId = savedInstanceState.getLong(BUNDLE_CURRENT_ITEM_ID);
        }
        mIsStartingActivity = savedInstanceState == null;

// viewpager
        Resources res = getResources();
        mPager = findViewById(R.id.viewpager_container);
        mPagerAdapter = new ArticleDetailAdapter(getSupportFragmentManager(), this);
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


        getSupportLoaderManager().initLoader(0, null, this);
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        return super.onOptionsItemSelected(item);
//    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(BUNDLE_STARTING_ITEM_ID, mStartingItemId);
        outState.putLong(BUNDLE_CURRENT_ITEM_ID, mCurrentItemId);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newAllArticlesInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0) return;
        mCursor = cursor;

        mPagerAdapter.swap(mCursor, mStartingItemId);
        mPager.setVisibility(View.VISIBLE);

        if (mIsStartingActivity) {
            for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
                if (mCursor.getLong(ArticleLoader.Query._ID) == mStartingItemId) {
                    break;
                }
            }
            mPager.setCurrentItem(mCursor.getPosition(), true);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursor = null;
        mPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCallback(Uri uri, View view) {

    }

    @Override
    public void onCallback(int mode) {

    }

    @Override
    public void onCallback(ArticleDetailFragment fragment) {
        mCurrentFragment = fragment;
    }


}
