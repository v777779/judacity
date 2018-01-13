package com.example.xyzreader.ui;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.content.Loader;
import android.support.v4.widget.NestedScrollView;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

import static com.example.xyzreader.remote.Config.BUNDLE_FRAGMENT_CURRENT_ID;
import static com.example.xyzreader.remote.Config.BUNDLE_FRAGMENT_STARTING_ID;


import static com.example.xyzreader.remote.Config.BUNDLE_FRAGMENT_TEXT_RECYCLER;
import static com.example.xyzreader.remote.Config.BUNDLE_FRAGMENT_TEXT_SOURCE;
import static com.example.xyzreader.remote.Config.CALLBACK_FRAGMENT_FULLSCREEN;
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
    private FrameLayout mFrameLayout;
    // text
    private TextView mTitleView;
    private TextView mSubTitleView;
    private ImageView mToolbarImage;
    // fab
    private FloatingActionButton mFab;
    private ImageButton mImageButtonLeft;
    private ImageButton mImageButtonRight;
    private ImageButton mImageButtonHome;
    private ImageButton mImageButtonFullScreen;
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

    // transition
    private long mStartingItemId;
    private long mCurrentItemId;

    private boolean mIsSkipToEnd;
    private boolean mIsWide;
    private Resources mRes;
    private Bitmap mBitmap;

    // recycler
    private boolean mIsStarting;
    private ArrayList<String> mList;
    private ArrayList<String> mListSource;
    private RecyclerView mRecyclerBody;
    private RecyclerBodyAdapter mRecyclerBodyAdapter;


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
        postponeEnterTransition();

        Bundle args = getArguments();
        if (args != null) {
            mStartingItemId = getArguments().getLong(BUNDLE_FRAGMENT_STARTING_ID);
            mCurrentItemId = getArguments().getLong(BUNDLE_FRAGMENT_CURRENT_ID);
        }

        mIsStarting = savedInstanceState == null;

        if (savedInstanceState != null) {
            mList = savedInstanceState.getStringArrayList(BUNDLE_FRAGMENT_TEXT_RECYCLER);
            mListSource = savedInstanceState.getStringArrayList(BUNDLE_FRAGMENT_TEXT_SOURCE);
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
        getLoaderManager().initLoader(0, null, this);

    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_article_detail, container, false);
        mRes = getResources();
// wide
        mIsWide = mRes.getBoolean(R.bool.is_wide);

// bind
        mNestedScrollView = mRootView.findViewById(R.id.nested_scrollview);
        mFrameLayout = mRootView.findViewById(R.id.linear_body);
// text
        mTitleView = mRootView.findViewById(R.id.article_title);
        mSubTitleView = mRootView.findViewById(R.id.article_subtitle);
        mToolbarImage = mRootView.findViewById(R.id.article_image);
// button
        mFab = mRootView.findViewById(R.id.fab);
        mImageButtonLeft = mRootView.findViewById(R.id.image_button_left);
        mImageButtonRight = mRootView.findViewById(R.id.image_button_right);
        mImageButtonHome = mRootView.findViewById(R.id.image_button_home);
        mImageButtonFullScreen = mRootView.findViewById(R.id.image_button_fullscreen);
// progress
        mProgressBarText = mRootView.findViewById(R.id.progress_bar_text);
        mProgressBarImage = mRootView.findViewById(R.id.progress_bar_image);

        if (!mIsWide) {
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
        }



        mDateFormat = new SimpleDateFormat(getString(R.string.calendar_format), Locale.ENGLISH);
        mOutputFormat = new SimpleDateFormat();
        mStartOfEpoch = new GregorianCalendar(
                mRes.getInteger(R.integer.calendar_year),
                mRes.getInteger(R.integer.calendar_moanth),
                mRes.getInteger(R.integer.calendar_day)
        );

// linear
        mFrameLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                if (mIsSkipToEnd && mList.size() >= mListSource.size()) {                // scroll to end
                    mNestedScrollView.scrollTo(0, mFrameLayout.getHeight());
                    mProgressBarText.setVisibility(View.INVISIBLE);
                }
                mIsSkipToEnd = false;

            }
        });


        mNestedScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            int mLineY = 0;
            int mLastY = 0;

            @Override
            public void onScrollChanged() {
                if (mNestedScrollView == null) return;
                mLineY = mNestedScrollView.getScrollY();
                if (mLineY > 0 &&
                        mRecyclerBody.getHeight() - mLineY < FRAGMENT_TEXT_OFFSET &&
                        mList.size() < mListSource.size()) {                // not all loaded
                    loadPages(LOAD_NEXT_PAGE);                              // freeze recycler
                    mLastY = mLineY;        // last position
                }
            }
        });

// fab
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String shareText = "";
                if (mTitleView != null && mSubTitleView != null) {
                    shareText = "Interesting book: " + mTitleView.getText() + " " + mSubTitleView.getText();
                }
                if (mCursor != null && mCursor.getCount() > 0) {
                    mCursor.moveToFirst();
                    shareText = shareText + ", cover image link: " + mCursor.getString(ArticleLoader.Query.PHOTO_URL);
                }

                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText(shareText)
                        .getIntent(), getString(R.string.action_share)));


            }
        });
// image buttons
        mImageButtonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomBarScroll.setContinue();
                mNestedScrollView.scrollTo(0, 0);
            }
        });

        mImageButtonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomBarScroll.setContinue();

                if (mList.size() < mListSource.size()) {
                    mProgressBarText.setVisibility(View.VISIBLE);
                    loadPages(LOAD_ALL_PAGES);
                    mIsSkipToEnd = true;
                } else {
                    mNestedScrollView.scrollTo(0, mFrameLayout.getHeight());
                }
            }
        });


        mImageButtonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        mImageButtonFullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomBarScroll.setContinue();
                ((ICallback) getActivity()).onCallback(CALLBACK_FRAGMENT_FULLSCREEN);
            }
        });


// progress bar
        mProgressBarText.setVisibility(View.VISIBLE);
        mProgressBarImage.setVisibility(View.VISIBLE);

// recycler
        mRecyclerBody = mRootView.findViewById(R.id.article_body_recycler);
        mRecyclerBodyAdapter = new RecyclerBodyAdapter(getActivity());
        mRecyclerBody.setAdapter(mRecyclerBodyAdapter);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL,
                false);
        mRecyclerBody.setLayoutManager(layoutManager);

        return mRootView;
    }


    public void closeFab() {
        if (mFab != null) {
            mFab.setVisibility(View.INVISIBLE);

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(BUNDLE_FRAGMENT_TEXT_RECYCLER, mList);
        outState.putStringArrayList(BUNDLE_FRAGMENT_TEXT_SOURCE, mListSource);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {   // set adapter
        super.onActivityCreated(savedInstanceState);

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

//    private boolean mIsVisible;
//
//    @Override
//    public void setMenuVisibility(boolean menuVisible) {
//        super.setMenuVisibility(menuVisible);
//        mIsVisible = menuVisible;
//    }


    // callbacks
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newInstanceForItemId(getActivity(), mCurrentItemId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0) return;

        mCursor = cursor;
        mCursor.moveToFirst();

        if (mIsStarting) {
            mListSource = getList();
            mList = new ArrayList<>();
            loadPages(LOAD_NEXT_PAGE);
        } else {
            mRecyclerBody.setLayoutFrozen(false);
            mRecyclerBodyAdapter.swap(mList);
            mRecyclerBody.setLayoutFrozen(true);
        }

        bindViews();
    }

    // correction position
//            if (!mIsWide) {
//                mNestedScrollView.animate().withEndAction(new Runnable() {
//                    @Override
//                    public void run() {
//                        int scrollY = mNestedScrollView.getScrollY();
//                        int dY = (int) (scrollY * 0.295);
//                        if (mIsLand) {
//                            dY = -1500 + (int) (scrollY * 0.0003);
//                        }
//                        mNestedScrollView.scrollTo(0, scrollY + dY);
//                    }
//                }).start();
//            }


    private void loadPages(boolean isLoadAll) {
        mRecyclerBody.setLayoutFrozen(false);
        int count = 0;
        while ((count < FRAGMENT_TEXT_SIZE || isLoadAll) && mList.size() < mListSource.size()) {
            String s = htmlConvert(mListSource.get(mList.size()));
            mList.add(s);
            count += s.length();
        }
        mRecyclerBodyAdapter.swap(mList);
        mRecyclerBody.setLayoutFrozen(true);

    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursor = null;
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
                        .placeholder(R.drawable.empty_loading_007)
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
                        mProgressBarText.setVisibility(View.INVISIBLE);
                        ActivityCompat.startPostponedEnterTransition(getActivity());
                        mBitmap = ((BitmapDrawable) resource).getBitmap();
                        return false;
                    }
                })
                .transition(withCrossFade())
                .into(mToolbarImage);


        mToolbarImage.setTransitionName(getString(R.string.transition_image, mCurrentItemId));
        mTitleView.setTransitionName(getString(R.string.transition_title, mCurrentItemId));

    }

    public Bitmap getBitmap() {
        return mBitmap;
    }


    private String htmlConvert(String text) {
        text = text.replaceAll("(\r\n\r\n\r\n|\n\n\n)", "<br/><br/><br/>");
        text = text.replaceAll("(\r\n\r\n|\n\n)", "<br/><br/>");//&emsp;");
        text = text.replaceAll("(\r\n|\n)", " ");

        return Html.fromHtml(text).toString();
    }

    private ArrayList<String> getList() {
        ArrayList<String> list = new ArrayList<>();
        int startPos;
        int endPos;

        String s = mCursor.getString(ArticleLoader.Query.BODY);
        startPos = 0;
        while (startPos < s.length()) {
            endPos = s.indexOf("\n", startPos + FRAGMENT_TEXT_SIZE);
            if (endPos > 0) {
                if (s.substring(endPos - 1, endPos).equals("\r")) {
                    endPos = endPos - 1;
                }
                list.add(s.substring(startPos, endPos));
                startPos = endPos; // last not used position
            } else {
                if (startPos < s.length()) {  // remains
                    list.add(s.substring(startPos));
                    startPos = s.length();
                }
            }

        }
        return list;

    }


    //     * Returns true if {@param view} is contained within {@param container}'s bounds.
    private boolean isViewInBounds(@NonNull View container, @NonNull View view) {
        Rect containerBounds = new Rect();
        container.getHitRect(containerBounds);
        return view.getLocalVisibleRect(containerBounds);
    }


    @Nullable
    public List<View> getSharedViews() {
        List<View> list = new ArrayList<>();

        View decorView = getActivity().getWindow().getDecorView();

        if (isViewInBounds(decorView, mToolbarImage)) {     // check image in bounds
            list.add(mToolbarImage);
        }
        if (isViewInBounds(decorView, mTitleView)) {        // check text in bounds
            list.add(mTitleView);
        }


        return list;
    }

    public View getRootView() {
        return mRootView;
    }

}
