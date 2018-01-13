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

/**
 *   ArticleDetailActivity  activity for non tablet devices
 *   Creates ViewPager with ArticelDetailFragment objects
 *   Performs shared element forward and back shared element processing
 */
public class ArticleDetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, ICallback {

    /**
     * ViewPager for ArticleDetailFragments objects
     */
    private ViewPager mPager;
    /**
     * ViewPagerAdapter for ViewPager object
     */
    private ViewPagerAdapter mPagerAdapter;
    /**
     * Cursor object with data from database for ArticleDetailFragments
     */
    private Cursor mCursor;
    /**
     * Integer  id of starting item in Cursor
     */
    private long mStartingItemId;
    /**
     * Integer  position of starting item in  Cursor/ViewPager
     */
    private int mStartingItemPosition;
    /**
     * Integer  position of current item in  Cursor/ViewPager
     */
    private int mCurrentItemPosition;

    /**
     * Boolean flag is true when first time entered into activity
     */
    private boolean mIsStartingActivity;

    // transition
    /**
     * ArticleDetailFragment  current selected fragment
     */
    private ArticleDetailFragment mCurrentFragment;

    /**
     * Boolean true when transition returns from  ArticleDetailFragment fragment
     */
    private boolean mIsReturning;

    /**
     * SharedElementCallback serves to processing transition of shared elements
     */
    public SharedElementCallback mSharedCallback;


    /**
     * Creates View of Activity
     * Initializes  ViewPager with ArticleDetailFragment items
     * Runs loader for Cursor object
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

// transition
        mSharedCallback = setupSharedCallback();
        postponeEnterTransition();
        setEnterSharedElementCallback(mSharedCallback);

        setContentView(R.layout.activity_article_detail);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

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

        mPager.setVisibility(View.GONE);


        getSupportLoaderManager().initLoader(ARTICLE_DETAIL_LOADER_ID, null, this);
    }


    /**
     * Saves parameters to Bundle storage object
     *
     * @param outState Bundle storage object for parameters.
     *                 Bundle Parameters: <br>
     *                 Integer         mStartingItemPosition   starting position of selected item
     *                 Integer         mCurrentItemPosition    current position of selected item
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(BUNDLE_STARTING_ITEM_POS, mStartingItemPosition);
        outState.putLong(BUNDLE_CURRENT_ITEM_POS, mCurrentItemPosition);

    }

    /**
     * Puts data into Intent about shared elements to perfrom back transition
     */
    @Override
    public void finishAfterTransition() {
        mIsReturning = true;                            // before super()
        Intent intent = new Intent();
        intent.putExtra(BUNDLE_STARTING_ITEM_POS, mStartingItemPosition);
        intent.putExtra(BUNDLE_CURRENT_ITEM_POS, mCurrentItemPosition);
        setResult(RESULT_OK, intent);
        super.finishAfterTransition();
    }

    /**
     * Callback of Cursor Loader
     * Creates Loader for Cursor object
     *
     * @param i      int the ID whose loader is to be created.
     * @param bundle Bundle  any arguments supplied by the caller.
     * @return Loader for Cursor object
     */
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newAllArticlesInstance(this);
    }

    /**
     * Called when loader is finished load, provides Cursor object with data
     * Cursor object copied to mPagerAdapter
     * When activity is started first time   starting item Id is verified with Cursor data
     * If starting item Id is obsoleted  activity is finished
     *
     * @param loader Loader<Crusor> the Loader that has finished.
     * @param cursor Cursor the data generated by the Loader.
     */
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

            Toast.makeText(this, getString(R.string.cursor_message), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * Resets loader and makes deletes Cursor object.
     * Notified ViewPager Adapter that cursor is erased.
     *
     * @param loader Loader<Cursor> loader which is reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursor = null;
        mPagerAdapter.notifyDataSetChanged();
    }

    /**
     * Placeholder for common user interface, not used in this activity.
     *
     * @param view     View object unused.
     * @param position int  unused.
     */
    @Override
    public void onCallback(View view, int position) {
    }

    /**
     * Placeholder for common user interface, not used in this activity
     *
     * @param mode int unused.
     */
    @Override
    public void onCallback(int mode) {
    }

    /**
     *  Extracts system shared elements from current set of shared elements to
     *  List<String></String> names and Map<String,View> sharedElements storages
     *  All other shared elements is deleted.
     *
     * @param names          List<String></String>  list of transition names of shared elements
     * @param sharedElements Map<String,View> map with transition names and views of shared elements
     */
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

    /**
     *  Callback of common user interface.
     *  Called from ViewPagerAdapter setPrimaryItem() method
     *  Stores current  ArticleDetailFragment object into mCurrentFragment field
     *  mCurrentFragment used for extraction of bitmap
     *  Bitmap is needed for palette generation and when bitmap is available
     *  Fragment is completely visible and instructive motion is started
     *
     * @param fragment  ArticleDetailFragment fragment
     */
    @Override
    public void onCallback(ArticleDetailFragment fragment) {
        mCurrentFragment = fragment;
        if (fragment != null) {
            instructiveMotion(fragment);
        }
    }

    /**
     * Placeholder for common user interface, not used in this activity
     *
     * @param view View unused.
     */
    @Override
    public void onCallback(View view) {
    }

    /**
     *  Setup shared element calback listener.
     *  Listener used for back transition processing
     *  All shared names from fragment removed and replaced by actual destination names and view
     *  Thus back transition moved towards to new current selected element rather than starting one.
     *
     * @return  SharedElementCallback  callback for shared elements
     */
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

}
