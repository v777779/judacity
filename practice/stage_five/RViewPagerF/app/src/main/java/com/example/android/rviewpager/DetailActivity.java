package com.example.android.rviewpager;


import android.app.SharedElementCallback;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.example.android.rviewpager.MainActivity.BUNDLE_CURRENT_IMAGE_RESOURCE;
import static com.example.android.rviewpager.MainActivity.BUNDLE_STARTING_IMAGE_RESOURCE;
import static com.example.android.rviewpager.MainActivity.listImagesLoader;

public class DetailActivity extends AppCompatActivity implements ICallback {

    @Nullable
    @BindView(R.id.fragment_container)
    ConstraintLayout mFragmentContainer;

    @Nullable
    @BindView(R.id.viewpager_container)
    ViewPager mPager;

    private List<String> mList;
    private String mStartingImageId;
    private Unbinder mUnbinder;
    private FragmentAdapter mPagerAdapter;

    private boolean mIsReturning;
    private int mStartingPosition;
    private int mCurrentPosition;
    private FragmentDetail mCurrentFragmentDetail;


    public final SharedElementCallback mCallback = new SharedElementCallback() {
        @Override
        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
            if (mIsReturning) {
                List<View> list = mCurrentFragmentDetail.getSharedViews();

                View sharedElement = list.get(0);


                if (sharedElement == null) {
                    // If shared element is null, then it has been scrolled off screen and
                    // no longer visible. In this case we cancel the shared element transition by
                    // removing the shared element from the shared elements map.
                    names.clear();
                    sharedElements.clear();
                } else if (mStartingPosition != mCurrentPosition) {
                    // If the user has swiped to a different ViewPager page, then we need to
                    // remove the old shared element and replace it with the new shared element
                    // that should be transitioned instead.
                    names.clear();
                    names.add(sharedElement.getTransitionName());
                    sharedElements.clear();
                    sharedElements.put(sharedElement.getTransitionName(), sharedElement);
                    sharedElement = list.get(1);
                    sharedElements.put(sharedElement.getTransitionName(), sharedElement);
                    sharedElement = list.get(2);
                    sharedElements.put(sharedElement.getTransitionName(), sharedElement);

                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
//        getWindow().setExitTransition(new Explode());
//        getWindow().setSharedElementExitTransition(new Explode());

// transition support
        postponeEnterTransition();
//        setEnterSharedElementCallback(mCallback);

        setContentView(R.layout.activity_detail);


        mUnbinder = ButterKnife.bind(this);
        mList = listImagesLoader();

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(BUNDLE_STARTING_IMAGE_RESOURCE)) {
            mStartingImageId = intent.getStringExtra(BUNDLE_STARTING_IMAGE_RESOURCE);
        }

        if(savedInstanceState == null) {
            mCurrentPosition = mStartingPosition;
        }else {
            mCurrentPosition = savedInstanceState.getInt(BUNDLE_CURRENT_IMAGE_RESOURCE);
        }


        Resources res = getResources();
        mPagerAdapter = new FragmentAdapter(getSupportFragmentManager(), this);
        mPager.setAdapter(mPagerAdapter);
        mPager.setPageMargin((int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        res.getInteger(R.integer.pager_side_margin), res.getDisplayMetrics()));
        mPager.setPageMarginDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.colorPagerMargin)));
        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mCurrentPosition = position;
            }
        });

// callback
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).equals(mStartingImageId)) {
                mStartingPosition = i;
                break;
            }
        }

        mPagerAdapter.swap(mList, mStartingPosition);
        mPager.setCurrentItem(mStartingPosition);


    }

    @Override
    public void finishAfterTransition() {
        mIsReturning = true;              // before super()
        Intent data = new Intent();
        data.putExtra(BUNDLE_STARTING_IMAGE_RESOURCE, mStartingPosition);
        data.putExtra(BUNDLE_CURRENT_IMAGE_RESOURCE, mCurrentPosition);
        setResult(RESULT_OK, data);
        super.finishAfterTransition();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(BUNDLE_CURRENT_IMAGE_RESOURCE, mCurrentPosition);
    }

    // callbacks

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();

    }


    @Override
    public void onCallback(Uri uri, View view) {

    }

    @Override
    public void onCallback(int mode) {

    }

    @Override
    public void onCallback(View view, int position) {

    }

    @Override
    public void onCallback(FragmentDetail fragment) {
        mCurrentFragmentDetail = fragment;
    }
}
