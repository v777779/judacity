package com.example.xyzreader.ui;


import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
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

public class ArticleDetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private ViewPager mPager;
    private ArticleDetailAdapter mPagerAdapter;
//    private ScreenSlidePagerAdapter mPagerAdapter;
    private Cursor mCursor;
    private long mStartId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
//        getWindow().setExitTransition(new Explode());
//        getWindow().setReenterTransition(new Slide(Gravity.TOP));

        postponeEnterTransition();

        setContentView(R.layout.activity_article_detail);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        ActionBar actionBar = getSupportActionBar();
//
//        if (actionBar != null) {
//            actionBar.setTitle("");
//            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }

// bundle
        if (savedInstanceState == null) {
            if (getIntent() != null && getIntent().getData() != null) {
                mStartId = ItemsContract.Items.getItemId(getIntent().getData());  // by Uri
            }
        }
// fab
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });


// test!!!
//        Typeface mCaecillia = Typeface.createFromAsset(getAssets(), "caecilia-light-webfont.ttf");
//        TextView mBodyText = findViewById(R.id.article_body);
//        mBodyText.setTypeface(mCaecillia);

// viewpager
        Resources res = getResources();
        mPager = findViewById(R.id.viewpager_container);
        mPagerAdapter = new ArticleDetailAdapter(getSupportFragmentManager(), mCursor);
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
//                if (mCursor != null) {
//                    mCursor.moveToPosition(position);
//                }
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


        getSupportLoaderManager().initLoader(0,null,this);
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newAllArticlesInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0) return;
        mCursor = cursor;
//        mPagerAdapter.notifyDataSetChanged();
        mPagerAdapter.swap(mCursor);
        mPager.setVisibility(View.VISIBLE);
        if (mStartId == 0) return;

        for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
            if (mCursor.getLong(ArticleLoader.Query._ID) == mStartId) {
                mPager.setCurrentItem(mCursor.getPosition(), true);
                break;
            }
        }



        mStartId = 0;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursor = null;
        mPagerAdapter.notifyDataSetChanged();
    }

//    /**
//     * A simple pager adapter that represents 5 {@link ArticleDetailFragment} objects, in
//     * sequence.
//     */
//    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
//        public ScreenSlidePagerAdapter(FragmentManager fm) {
//            super(fm);
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            Log.d("Detail","lifecycle detail: getItem() position: "+position);
//            return ArticleDetailFragment.newInstance(position, this);
//        }
//
//        @Override
//        public int getCount() {
//            if(mCursor == null) return 0;
//            return mCursor.getCount();
//        }
//    }

}
