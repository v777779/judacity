package com.example.xyzreader.ui;


import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.NestedScrollView;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import timber.log.Timber;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.example.xyzreader.remote.Config.BUNDLE_FRAGMENT_ID;

/**
 * Created by V1 on 30-Dec-17.
 */

public class ArticleDetailFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private View mRootView;
    private TextView mBodyText;
    private NestedScrollView mNestedScrollView;


    private Cursor mCursor;
    private long mItemId;
    private Typeface mCaecilia;

    private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
    private SimpleDateFormat mOutputFormat = new SimpleDateFormat();
    private GregorianCalendar mStartOfEpoch = new GregorianCalendar(2, 1, 1);


    private LayoutInflater mInflater;
    private LinearLayout mLinearLayout;
    private String mTextSource;
    private int mTextSize;

    private final int FRAGMENT_TEXT_SIZE = 2000;
    private final int FRAGMENT_TEXT_OFFSET = 700;
    private LinearLayout.LayoutParams mLayoutParams;

    // fab
    private FloatingActionButton fabLeft;
    private FloatingActionButton fabRight;
    private ImageButton mImageButtonLeft;
    private ImageButton mImageButtonRight;
    private ConstraintLayout mConstraintBottom;

    // progress
    private ProgressBar mProgressBar;

    // skip
    private boolean mIsSkipToEnd;

    private ActionBar mActionBar;

    public static Fragment newInstance(long itemId) {
        Bundle arguments = new Bundle();
        arguments.putLong(BUNDLE_FRAGMENT_ID, itemId);
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
        if (args != null && args.containsKey(BUNDLE_FRAGMENT_ID)) {
            mItemId = getArguments().getLong(BUNDLE_FRAGMENT_ID);

        }
        Timber.d("lifecycle fragment: onCreate():" + mItemId);

    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_article_detail, container, false);


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

        Resources res = getResources();
        mDateFormat = new SimpleDateFormat(getString(R.string.calendar_format));
        mOutputFormat = new SimpleDateFormat();
        mStartOfEpoch = new GregorianCalendar(
                res.getInteger(R.integer.calendar_year),
                res.getInteger(R.integer.calendar_moanth),
                res.getInteger(R.integer.calendar_day)
        );

        mNestedScrollView = mRootView.findViewById(R.id.nested_scrollview);

// linear
        mLinearLayout = mRootView.findViewById(R.id.linear_body);
        mLinearLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                if (mIsSkipToEnd && mTextSize >= mTextSource.length()) {                // scroll to end
                    mNestedScrollView.scrollTo(0, mLinearLayout.getHeight());
                    mIsSkipToEnd = false;

                }
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        });

        mInflater = inflater;

        mLayoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        int side_margin = getResources().getDimensionPixelSize(R.dimen.large_margin);
        mLayoutParams.setMargins(side_margin, 0, side_margin, 0);

        mNestedScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            int mLineY = 0;
            int mLastY = 0;

            @Override
            public void onScrollChanged() {
                mLineY = mNestedScrollView.getScrollY();
                if (mLineY > 0 &&                                                       // scroll down
                        mLineY > mLastY &&                                              // after last
                        mLineY > mLinearLayout.getHeight() - FRAGMENT_TEXT_OFFSET &&    // before the end
                        mTextSize < mTextSource.length()) {                             // not all loaded

                    loadTextToLayout();
                    mLastY = mLineY;        // last position
                }
            }
        });


// image buttons
        mConstraintBottom = mRootView.findViewById(R.id.bottom_toolbar);
        mImageButtonLeft = mRootView.findViewById(R.id.image_bitton_left);
        mImageButtonRight = mRootView.findViewById(R.id.image_button_right);

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
                    mProgressBar.setVisibility(View.VISIBLE);

                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            // add textView
                            View item = mInflater.inflate(R.layout.fragment_text_item, null);
                            TextView textView = item.findViewById(R.id.article_body_ext);
                            textView.setTypeface(mCaecilia);

                            String subText = "";
                            subText = mTextSource.substring(mTextSize);     // up to the end
                            mTextSize = mTextSource.length();     // block next addition

                            subText = htmlConvert(subText);
                            textView.setText(subText);
                            mLinearLayout.addView(textView);
                            mProgressBar.setVisibility(View.INVISIBLE);
                        }
                    });

                } else {
                    mNestedScrollView.scrollTo(0, mLinearLayout.getHeight());
                }
            }
        });

// progress bar
        mProgressBar = mRootView.findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.VISIBLE);


        return mRootView;
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


    int bind;

    // callbacks
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newInstanceForItemId(getActivity(), mItemId);
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
        TextView titleView = mRootView.findViewById(R.id.article_title);
        TextView bylineView = mRootView.findViewById(R.id.article_byline);
        final ImageView imageView = mRootView.findViewById(R.id.toolbar_image);


        titleView.setText(mCursor.getString(ArticleLoader.Query.TITLE));
        Date publishedDate = parsePublishedDate();
        if (!publishedDate.before(mStartOfEpoch.getTime())) {
            bylineView.setText(Html.fromHtml(
                    DateUtils.getRelativeTimeSpanString(
                            publishedDate.getTime(),
                            System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                            DateUtils.FORMAT_ABBREV_ALL).toString()
                            + " by " + mCursor.getString(ArticleLoader.Query.AUTHOR)));
        } else {
            // If date is before 1902, just show the string
            bylineView.setText(Html.fromHtml(
                    mOutputFormat.format(publishedDate) + " by "
                            + mCursor.getString(ArticleLoader.Query.AUTHOR)
            ));

        }



        String imageURL = mCursor.getString(ArticleLoader.Query.PHOTO_URL);
        if (imageURL != null && !imageURL.isEmpty()) {
            Glide.with(this)
                    .load(imageURL)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.empty_loading)
                            .error(R.drawable.error_loading))
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                            imageView.animate().alpha(1.0f).setStartDelay(0).setDuration(250).start();
                            return false;
                        }
                    })
                    .transition(withCrossFade())
                    .into(imageView);
        }
        imageView.setAlpha(1f);
        String text = mCursor.getString(ArticleLoader.Query.BODY);
        mTextSource = text;
        mTextSize = 0;

        loadTextToLayout();

    }


    private String htmlConvert(String text) {
        text = text.replaceAll("(\r\n\r\n\r\n|\n\n\n)", "<br/><br/><br/>");
        text = text.replaceAll("(\r\n\r\n|\n\n)", "<br/><br/>");//&emsp;");
        text = text.replaceAll("(\r\n|\n)", " ");

        return Html.fromHtml(text).toString();
    }

    private void loadTextToLayout() {
        View item = mInflater.inflate(R.layout.fragment_text_item, null);
        TextView textView = item.findViewById(R.id.article_body_ext);
        textView.setTypeface(mCaecilia);

        String subText = "";
        int pos = mTextSource.indexOf("\n", mTextSize + FRAGMENT_TEXT_SIZE);
        if (pos > 0) {
            if (mTextSource.substring(pos - 1, pos).equals("\r")) pos = pos - 1;
            subText = mTextSource.substring(mTextSize, pos);
            mTextSize = pos; // last not used position
        } else {
            subText = mTextSource.substring(mTextSize);     // up to the end
            mTextSize = mTextSource.length();     // block next addition
        }

        subText = htmlConvert(subText);
        textView.setText(subText);
        mLinearLayout.addView(textView);

    }


}
