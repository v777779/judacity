package com.example.android.rviewpager;


import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

import static com.example.android.rviewpager.MainActivity.BUNDLE_IMAGE_RESOURCE;

public class FragmentDetail extends Fragment {

    @Nullable
    @BindView(R.id.app_bar)
    AppBarLayout mAppBar;

    @Nullable
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Nullable
    @BindView(R.id.toolbar_image)
    ImageView mToolbarImage;

    @Nullable
    @BindView(R.id.fab)
    FloatingActionButton mFab;

    private String mImageId;
    private Unbinder mUnbinder;

    private Activity mActivity;
    private View mRootView;
    private String transitionName;
    private boolean mIsVisibleToUser;


    public static Fragment newInstance(String itemId) {
        Bundle arguments = new Bundle();
        arguments.putString(BUNDLE_IMAGE_RESOURCE, itemId);
        FragmentDetail fragment = new FragmentDetail();
        fragment.setArguments(arguments);
        return fragment;
    }

    public void setTransitionName(String s) {
        transitionName = s;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ActivityCompat.startPostponedEnterTransition(getActivity());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (AppCompatActivity) getActivity();


        Transition move = TransitionInflater.from(getContext()).inflateTransition(R.transition.move);
        setSharedElementEnterTransition(move);

//        setSharedElementReturnTransition(null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.activity_detail, container, false);
        mUnbinder = ButterKnife.bind(this, mRootView);

        Toolbar toolbar = (Toolbar) mRootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle("");
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });


        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.onBackPressed();
            }
        });


        Intent intent = mActivity.getIntent();
        if (intent != null && intent.hasExtra(BUNDLE_IMAGE_RESOURCE)) {
            mImageId = intent.getStringExtra(BUNDLE_IMAGE_RESOURCE);
        }

        Bundle args = getArguments();
        if (args != null) {
            mImageId = args.getString(BUNDLE_IMAGE_RESOURCE);
        }

        loadImageFromAssets(mImageId);
        transitionName = getString(R.string.transition_image);
        if (mIsVisibleToUser) {
            mToolbarImage.setTransitionName(transitionName);
        }else {
            mToolbarImage.setTransitionName("");
        }

        mAppBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            private boolean isActive;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                isActive = true;
                if (Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange() == 0) {
                    //  Collapsed
                    mFab.animate().scaleX(0).scaleY(0).setDuration(250).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            isActive = false;
                        }
                    }).start();

                } else {
                    //Expanded
                    mFab.animate().scaleX(1).scaleY(1).setDuration(250).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            isActive = false;
                        }
                    }).start();

                }


            }
        });

        return mRootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        mIsVisibleToUser = isVisibleToUser;
        if(mToolbarImage != null ) {
            mToolbarImage.setTransitionName(!isVisibleToUser?"":getString(R.string.transition_image));
        }
    }

    private void loadImageFromAssets(String imageName) {
        try {
            InputStream in = mActivity.getAssets().open("images/" + imageName);
            Drawable drawable = Drawable.createFromStream(in, null);
            mToolbarImage.setImageDrawable(drawable);

        } catch (Exception e) {
            Timber.d(e.getMessage());
        }
    }


}
