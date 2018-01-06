package com.example.xyzreader.ui;


import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.content.Loader;
import android.support.v4.widget.NestedScrollView;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.format.DateUtils;
import android.transition.Transition;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

import static com.example.xyzreader.remote.Config.BUNDLE_FRAGMENT_CURRENT_ID;
import static com.example.xyzreader.remote.Config.BUNDLE_FRAGMENT_STARTING_ID;
import static com.example.xyzreader.remote.Config.FRAGMENT_TEXT_OFFSET;
import static com.example.xyzreader.remote.Config.FRAGMENT_TEXT_SIZE;
import static com.example.xyzreader.remote.Config.LOAD_ALL_PAGES;
import static com.example.xyzreader.remote.Config.LOAD_NEXT_PAGE;

/**
 * Created by V1 on 30-Dec-17.
 */

public class ArticleDetailFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    // layout
    private NestedScrollView mNestedScrollView;
    private LinearLayout mLinearLayout;
    // text
    private TextView mTitleView;
    private TextView mSubTitleView;
    private ImageView mToolbarImage;
    // fab
    private FloatingActionButton mFab;
    private ImageButton mImageButtonLeft;
    private ImageButton mImageButtonRight;
    private ImageButton mImageButtonHome;
    // progress
    private ProgressBar mProgressBarText;
    private ProgressBar mProgressBarImage;


    private View mRootView;
    private Cursor mCursor;
    private Typeface mCaecilia;
    // date
    private SimpleDateFormat mDateFormat;
    private SimpleDateFormat mOutputFormat;
    private GregorianCalendar mStartOfEpoch;
    // text
    private LayoutInflater mInflater;
    private String mTextSource;
    private int mTextSize;

    // transition
    private long mStartingItemId;
    private long mCurrentItemId;


    // skip
    private boolean mIsSkipToEnd;


    public static Fragment newInstance(long startingItemId, long currentItemId) {
        Bundle arguments = new Bundle();
        arguments.putLong(BUNDLE_FRAGMENT_STARTING_ID, startingItemId);
        arguments.putLong(BUNDLE_FRAGMENT_CURRENT_ID, currentItemId);
        ArticleDetailFragment fragment = new ArticleDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCaecilia = Typeface.createFromAsset(context.getAssets(), "caecilia-light-webfont.ttf");


        Timber.d("lifecycle fragment: onAttach()");
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {  // get bundle
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mStartingItemId = getArguments().getLong(BUNDLE_FRAGMENT_STARTING_ID);
            mCurrentItemId = getArguments().getLong(BUNDLE_FRAGMENT_CURRENT_ID);
        }

        Timber.d("lifecycle fragment: onCreate():" + mCurrentItemId);
// fab ***hiding***
//        getActivity().getWindow().getSharedElementEnterTransition().addListener(new TransitionAdapter() {
//            private boolean mIsTransition = false;
//            @Override
//            public void onTransitionStart(Transition transition) {
//                if (mIsTransition && mFab != null) {
//                    mFab.setVisibility(View.GONE);
//                }
//            }
//            @Override
//            public void onTransitionEnd(Transition transition) {
//                mIsTransition = true;
//            }
//        });


    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mInflater = inflater;
        mRootView = inflater.inflate(R.layout.fragment_article_detail, container, false);

// bind
        mNestedScrollView = mRootView.findViewById(R.id.nested_scrollview);
        mLinearLayout = mRootView.findViewById(R.id.linear_body);
// text
        mTitleView = mRootView.findViewById(R.id.article_title);
        mSubTitleView = mRootView.findViewById(R.id.article_subtitle);
        mToolbarImage = mRootView.findViewById(R.id.article_image);
// button
        mFab = mRootView.findViewById(R.id.fab);
        mImageButtonLeft = mRootView.findViewById(R.id.image_button_left);
        mImageButtonRight = mRootView.findViewById(R.id.image_button_right);
        mImageButtonHome = mRootView.findViewById(R.id.image_button_home);
// progress
        mProgressBarText = mRootView.findViewById(R.id.progress_bar_text);
        mProgressBarImage = mRootView.findViewById(R.id.progress_bar_image);

        Toolbar toolbar = (Toolbar) mRootView.findViewById(R.id.toolbar_detail);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        final ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle("");
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });


        Resources res = getResources();
        mDateFormat = new SimpleDateFormat(getString(R.string.calendar_format), Locale.ENGLISH);
        mOutputFormat = new SimpleDateFormat();
        mStartOfEpoch = new GregorianCalendar(
                res.getInteger(R.integer.calendar_year),
                res.getInteger(R.integer.calendar_moanth),
                res.getInteger(R.integer.calendar_day)
        );

// linear

        mLinearLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                if (mIsSkipToEnd && mTextSize >= mTextSource.length()) {                // scroll to end
                    mNestedScrollView.scrollTo(0, mLinearLayout.getHeight());
                    mIsSkipToEnd = false;

                }
                mProgressBarText.setVisibility(View.INVISIBLE);
            }
        });


        mNestedScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            int mLineY = 0;
            int mLastY = 0;

            @Override
            public void onScrollChanged() {
                if (mNestedScrollView == null) return;
                mLineY = mNestedScrollView.getScrollY();
                if (mLineY > 0 &&                                                       // scroll down
                        mLineY > mLastY &&                                              // after last
                        mLineY > mLinearLayout.getHeight() - FRAGMENT_TEXT_OFFSET &&    // before the end
                        mTextSize < mTextSource.length()) {                             // not all loaded

                    loadTextToLayout(LOAD_NEXT_PAGE);
                    mLastY = mLineY;        // last position
                }
            }
        });


// fab
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();

            }
        });
// image buttons
        mImageButtonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ArticleDetailScroll.setContinue();
                mNestedScrollView.scrollTo(0, 0);
            }
        });

        mImageButtonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ArticleDetailScroll.setContinue();
                if (mTextSize < mTextSource.length()) {
                    mIsSkipToEnd = true;
                    mProgressBarText.setVisibility(View.VISIBLE);

                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            loadTextToLayout(LOAD_ALL_PAGES);
                            mProgressBarText.setVisibility(View.INVISIBLE);
                        }
                    });

                } else {
                    mNestedScrollView.scrollTo(0, mLinearLayout.getHeight());
                }
            }
        });

        mImageButtonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

// progress bar
        mProgressBarText.setVisibility(View.VISIBLE);
        mProgressBarImage.setVisibility(View.VISIBLE);

        return mRootView;
    }


    public void closeFab() {
        if (mFab != null) {
            mFab.setVisibility(View.INVISIBLE);

        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {   // set adapter
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    // callbacks
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newInstanceForItemId(getActivity(), mCurrentItemId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
// test!!! check this
//        if (!isAdded()) {
//            if (cursor != null) {
//                cursor.close();
//            }
//            return;
//        }
        if (cursor == null || cursor.getCount() == 0) return;

        mCursor = cursor;
        mCursor.moveToFirst();
// test!!!
        bindViews();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursor = null;
// test!!!
        bindViews();
    }

    private Date parsePublishedDate() {
        try {
            String date = mCursor.getString(ArticleLoader.Query.PUBLISHED_DATE);
            return mDateFormat.parse(date);
        } catch (ParseException ex) {
            Timber.d("Error passing today's date: " + ex.getMessage());
            return new Date();
        }
    }


    private void bindViews() {
        if (mRootView == null || mCursor == null || mCursor.getCount() == 0) {
            return;
        }

        mTitleView.setText(mCursor.getString(ArticleLoader.Query.TITLE));
        Date publishedDate = parsePublishedDate();
        if (!publishedDate.before(mStartOfEpoch.getTime())) {
            mSubTitleView.setText(Html.fromHtml(
                    DateUtils.getRelativeTimeSpanString(
                            publishedDate.getTime(),
                            System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                            DateUtils.FORMAT_ABBREV_ALL).toString()
                            + " by " + mCursor.getString(ArticleLoader.Query.AUTHOR)));
        } else {
            // If date is before 1902, just show the string
            mSubTitleView.setText(Html.fromHtml(
                    mOutputFormat.format(publishedDate) + " by "
                            + mCursor.getString(ArticleLoader.Query.AUTHOR)
            ));

        }


        String imageURL = mCursor.getString(ArticleLoader.Query.PHOTO_URL);
        Glide.with(this)
                .load(imageURL)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.empty_loading)
                        .error(R.drawable.error_loading))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e,
                                                Object model,
                                                Target<Drawable> target,
                                                boolean isFirstResource) {
                        ActivityCompat.startPostponedEnterTransition(getActivity());
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource,
                                                   Object model,
                                                   Target<Drawable> target,
                                                   DataSource dataSource,
                                                   boolean isFirstResource) {
                        mProgressBarImage.setVisibility(View.INVISIBLE);
                        ActivityCompat.startPostponedEnterTransition(getActivity());
                        return false;
                    }
                })
                .transition(withCrossFade())
                .into(mToolbarImage);


        mToolbarImage.setTransitionName(getString(R.string.transition_image, mCurrentItemId));
        mTitleView.setTransitionName(getString(R.string.transition_title, mCurrentItemId));
        mSubTitleView.setTransitionName(getString(R.string.transition_sub_title, mCurrentItemId));


        mTextSource = mCursor.getString(ArticleLoader.Query.BODY);  // load all text
        mTextSize = 0;
        loadTextToLayout(LOAD_NEXT_PAGE);
    }


    private String htmlConvert(String text) {
        text = text.replaceAll("(\r\n\r\n\r\n|\n\n\n)", "<br/><br/><br/>");
        text = text.replaceAll("(\r\n\r\n|\n\n)", "<br/><br/>");//&emsp;");
        text = text.replaceAll("(\r\n|\n)", " ");

        return Html.fromHtml(text).toString();
    }

    private void loadTextToLayout(boolean isLoadPage) {
        String subText = "";
        int pos = mTextSource.indexOf("\n", mTextSize + FRAGMENT_TEXT_SIZE);
        if (pos > 0 && isLoadPage) {
            if (mTextSource.substring(pos - 1, pos).equals("\r")) pos = pos - 1;
            subText = mTextSource.substring(mTextSize, pos);
            mTextSize = pos; // last not used position
        } else {
            subText = mTextSource.substring(mTextSize);     // up to the end
            mTextSize = mTextSource.length();     // block next addition
        }

        subText = htmlConvert(subText);

        View item = mInflater.inflate(R.layout.fragment_text_item, null);
        TextView textView = item.findViewById(R.id.article_body_ext);
        textView.setTypeface(mCaecilia);
        textView.setText(subText);
        mLinearLayout.addView(textView);

    }

// fab ***hiding***
//    private class TransitionAdapter implements Transition.TransitionListener {
//        @Override
//        public void onTransitionStart(Transition transition) {
//        }
//
//        @Override
//        public void onTransitionEnd(Transition transition) {
//        }
//
//        @Override
//        public void onTransitionCancel(Transition transition) {
//        }
//
//        @Override
//        public void onTransitionPause(Transition transition) {
//        }
//
//        @Override
//        public void onTransitionResume(Transition transition) {
//        }
//    }


}
