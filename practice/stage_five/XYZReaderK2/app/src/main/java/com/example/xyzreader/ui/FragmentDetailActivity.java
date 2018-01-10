package com.example.xyzreader.ui;


import android.app.SharedElementCallback;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.transition.TransitionInflater;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.example.xyzreader.remote.Config.ARTICLE_DETAIL_LOADER_ID;
import static com.example.xyzreader.remote.Config.BUNDLE_CURRENT_ITEM_POS;
import static com.example.xyzreader.remote.Config.BUNDLE_FRAGMENT_CURRENT_ID;
import static com.example.xyzreader.remote.Config.BUNDLE_FRAGMENT_STARTING_ID;
import static com.example.xyzreader.remote.Config.BUNDLE_FRAGMENT_STARTING_POS;
import static com.example.xyzreader.remote.Config.BUNDLE_STARTING_ITEM_ID;
import static com.example.xyzreader.remote.Config.BUNDLE_STARTING_ITEM_POS;
import static java.lang.reflect.Array.getInt;

public class FragmentDetailActivity extends Fragment implements ICallback {

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


    private View mRootView;
    private FragmentActivity mActivity;

    public static Fragment newInstance(long startingItemId, int startingItemPosition) {
        Bundle arguments = new Bundle();
        arguments.putLong(BUNDLE_STARTING_ITEM_ID, startingItemId);
        arguments.putInt(BUNDLE_STARTING_ITEM_POS, startingItemPosition);
        FragmentDetailActivity fragment = new FragmentDetailActivity();
        fragment.setArguments(arguments);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postponeEnterTransition();
        setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));

        mActivity = getActivity();
        mCursor = ((ArticleListActivity) mActivity).getCursor();


        Bundle args = getArguments();
        if (savedInstanceState == null) {


            if (args != null) {
                mStartingItemId = args.getLong(BUNDLE_STARTING_ITEM_ID, 0);
                mStartingItemPosition = args.getInt(BUNDLE_STARTING_ITEM_POS, 0);
            }
            mCurrentItemPosition = mStartingItemPosition;
        } else {
            mStartingItemPosition = savedInstanceState.getInt(BUNDLE_STARTING_ITEM_POS);
            mCurrentItemPosition = savedInstanceState.getInt(BUNDLE_CURRENT_ITEM_POS);
        }
        mIsStartingActivity = savedInstanceState == null;


//        getLoaderManager().initLoader(ARTICLE_DETAIL_LOADER_ID, null, this);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.activity_article_detail, container, false);


// viewpager
        Resources res = getResources();
        mPager = mRootView.findViewById(R.id.viewpager_container);
        mPagerAdapter = new ViewPagerAdapter(getFragmentManager(), this);
        mPager.setAdapter(mPagerAdapter);
        mPager.setPageMargin((int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        res.getInteger(R.integer.pager_side_margin), res.getDisplayMetrics()));
        mPager.setPageMarginDrawable(new ColorDrawable(ContextCompat.getColor(mActivity, R.color.colorPagerMargin)));

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

        mPagerAdapter.swap(mCursor);
        mPager.setCurrentItem(mCurrentItemPosition);
        mPager.setVisibility(View.VISIBLE);


        return mRootView;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(BUNDLE_STARTING_ITEM_POS, mStartingItemPosition);
        outState.putInt(BUNDLE_CURRENT_ITEM_POS, mCurrentItemPosition);

    }


//    @Override
//    public void finishAfterTransition() {
//        mIsReturning = true;                            // before super()
//        Intent intent = new Intent();
//        intent.putExtra(BUNDLE_STARTING_ITEM_POS, mStartingItemPosition);
//        intent.putExtra(BUNDLE_CURRENT_ITEM_POS, mCurrentItemPosition);
//        setResult(RESULT_OK, intent);
//        super.finishAfterTransition();
//    }

//    @Override
//    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
//        return ArticleLoader.newAllArticlesInstance(getContext());
//    }

    public void swap(Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0) return;
        mCursor = cursor;
        mPagerAdapter.swap(mCursor);
    }

//    @Override
//    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
//        if (cursor == null || cursor.getCount() == 0) return;
//        mCursor = cursor;
//
//        mPagerAdapter.swap(mCursor);
//        mPager.setCurrentItem(mCurrentItemPosition);
//        mPager.setVisibility(View.VISIBLE);
//
//        if (mIsStartingActivity) {
//            for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
//                if (mCursor.getLong(ArticleLoader.Query._ID) == mStartingItemId) {
//                    return;
//                }
//            }
//// TODO stop loading service optimization
//// bug fix  if cursor obsoleted exit from activity
//
//            Toast.makeText(mActivity, getString(R.string.cursor_message), Toast.LENGTH_SHORT).show();
//
//
//        }
//
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Cursor> loader) {
//        mCursor = null;
//        mPagerAdapter.notifyDataSetChanged();
//    }

    @Override
    public void onCallback(View view, int position) {
    }

    @Override
    public void onCallback(int mode) {
    }


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

}
