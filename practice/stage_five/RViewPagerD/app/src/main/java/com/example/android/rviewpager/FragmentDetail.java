package com.example.android.rviewpager;


import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.example.android.rviewpager.MainActivity.BUNDLE_CURRENT_IMAGE_RESOURCE;
import static com.example.android.rviewpager.MainActivity.BUNDLE_STARTING_IMAGE_RESOURCE;

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

    @Nullable
    @BindView(R.id.detail_text)
    TextView mText;

    @Nullable
    @BindView(R.id.large_text)
    TextView mLargeText;

    private String mStartingImageId;
    private String mCurrentImageId;
    private Unbinder mUnbinder;

    private Activity mActivity;
    private View mRootView;
    private String transitionName;
    private boolean mIsVisibleToUser;
    private ICallbackImage mCallbackImage;
    private boolean mIsTransitioning;

    public static Fragment newInstance(String currentImageId, String startingImageId) {
        Bundle arguments = new Bundle();
        arguments.putString(BUNDLE_STARTING_IMAGE_RESOURCE, startingImageId);
        arguments.putString(BUNDLE_CURRENT_IMAGE_RESOURCE, currentImageId);
        FragmentDetail fragment = new FragmentDetail();
        fragment.setArguments(arguments);
        return fragment;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

// enter transition ***setUserVisibility***
//        ActivityCompat.startPostponedEnterTransition(getActivity());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (AppCompatActivity) getActivity();

        Bundle args = getArguments();
        if (args != null) {
            mStartingImageId = args.getString(BUNDLE_STARTING_IMAGE_RESOURCE);
            mCurrentImageId = args.getString(BUNDLE_CURRENT_IMAGE_RESOURCE);
        }

        mIsTransitioning = savedInstanceState == null && mStartingImageId.equals(mCurrentImageId);

// enter transition ***setUserVisibility***
//        Transition move = TransitionInflater.from(getContext()).inflateTransition(R.transition.move);
//        setSharedElementEnterTransition(move);

        mCallbackImage = new ICallbackImage() {
            @Override
            public void onSuccess() {
                startPostponedEnterTransition();
            }

            @Override
            public void onError() {
                startPostponedEnterTransition();
            }
        };

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_detail, container, false);
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
                mActivity.onBackPressed();
            }
        });

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.onBackPressed();
            }
        });

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int k = 1;
            }
        });

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
        if (mIsTransitioning) {
            mLargeText.setAlpha(0f);
            getActivity().getWindow().getSharedElementEnterTransition().addListener(new TransitionListenerAdapter() {
                @Override
                public void onTransitionEnd(Transition transition) {
                    if (mLargeText != null) {
                        mLargeText.animate().setDuration(1000).alpha(1f);
                    }
                }
            });
        }

        bindViews();
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

    // enter transition ***setUserVisibility***
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        mIsVisibleToUser = isVisibleToUser;

        if (mFab != null) {
            mFab.setTransitionName(!isVisibleToUser ? "" : getString(R.string.transition_fab));
        }
        if (mText != null) {
            mText.setTransitionName(!isVisibleToUser ? "" : getString(R.string.transition_text));
        }
    }

    @Override
    public void startPostponedEnterTransition() {
        if (mCurrentImageId == mStartingImageId) {
            mToolbarImage.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    mToolbarImage.getViewTreeObserver().removeOnPreDrawListener(this);
                    getActivity().startPostponedEnterTransition();
                    return true;
                }
            });
        }
    }

    private void bindViews() throws NullPointerException {

// glide
        String imageURL = "file:///android_asset/images/" + mCurrentImageId;
//            Picasso.with(mContext).load(imageURL).into(mItemImage);
        Glide.with(this).load(imageURL)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e,
                                                Object model,
                                                Target<Drawable> target,
                                                boolean isFirstResource) {
                        mCallbackImage.onError();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource,
                                                   Object model,
                                                   Target<Drawable> target,
                                                   DataSource dataSource,
                                                   boolean isFirstResource) {
                        mCallbackImage.onSuccess();
                        return false;
                    }
                })
                .into(mToolbarImage);

        mToolbarImage.setTransitionName(mCurrentImageId);
// enter transition ***setUserVisibility***
        mFab.setTransitionName(!mIsVisibleToUser ? "" : getString(R.string.transition_fab));
        mText.setTransitionName(!mIsVisibleToUser ? "" : getString(R.string.transition_text));


//// input stream
//        try {
//            InputStream in = mActivity.getAssets().open("images/" + mCurrentImageId);
//            Drawable drawable = Drawable.createFromStream(in, null);
//            mToolbarImage.setImageDrawable(drawable);
//            mToolbarImage.setTransitionName(mCurrentImageId);
//
//        } catch (Exception e) {
//            Timber.d(e.getMessage());
//        }
    }

    @Nullable
    public List<View> getSharedViews() {
        List<View> list = new ArrayList<>();
        list.add(mToolbarImage);
        list.add(mFab);
        list.add(mText);
        return list;
    }

    private class TransitionListenerAdapter implements Transition.TransitionListener {
        @Override
        public void onTransitionStart(Transition transition) {
        }

        @Override
        public void onTransitionEnd(Transition transition) {
        }

        @Override
        public void onTransitionCancel(Transition transition) {
        }

        @Override
        public void onTransitionPause(Transition transition) {
        }

        @Override
        public void onTransitionResume(Transition transition) {
        }
    }
}
