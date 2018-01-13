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
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 03-Jan-18
 * Email: vadim.v.voronov@gmail.com
 */

/**
 * ArticleDetailFragment fragment visualizes Cursor item data
 */
public class ArticleDetailFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * NestedScrollView  view with item data
     */
    private NestedScrollView mNestedScrollView;
    /**
     * FrameLayout holds RecyclerView with text data
     */
    private FrameLayout mFrameLayout;
    /**
     * TextView with title of item
     */
    private TextView mTitleView;
    /**
     * TextView with sub title of item
     */
    private TextView mSubTitleView;
    /**
     * ImageView with image of item
     */
    private ImageView mToolbarImage;
    /**
     * Floating Action Button for Share Action
     */
    private FloatingActionButton mFab;
    /**
     * ImageButton for scrolling to top of text
     */
    private ImageButton mImageButtonLeft;
    /**
     * ImageButton for scrolling to bottom of text
     */
    private ImageButton mImageButtonRight;
    /**
     * ImageButton for onBackPressed() method
     */
    private ImageButton mImageButtonHome;
    /**
     * ImageButton for exit from full screen mode
     */
    private ImageButton mImageButtonFullScreen;
    /**
     * ProgressBar for text loading and processing
     */
    private ProgressBar mProgressBarText;
    /**
     * ProgressBar for image loading
     */
    private ProgressBar mProgressBarImage;

    /**
     * View root view of fragment
     */
    private View mRootView;
    /**
     * Cursor object with data of item
     */
    private Cursor mCursor;
    /**
     * Typeface for text
     */
    private Typeface mCaecilia;
    /**
     * SimpleDateFormat date format for item date and time
     */
    private SimpleDateFormat mDateFormat;
    /**
     * SimpleDateFormat date format for item date and time
     */
    private SimpleDateFormat mOutputFormat;
    /**
     * GregorianCalendar for item date and time
     */
    private GregorianCalendar mStartOfEpoch;

    /**
     * Integer  starting item ID
     */
    private long mStartingItemId;
    /**
     * Integer  current item ID
     */
    private long mCurrentItemId;
    /**
     * Boolean true when request for scrolling to the end of text is exists
     */
    private boolean mIsSkipToEnd;
    /**
     * Boolean true for devices with sw800dp
     */
    private boolean mIsWide;
    /**
     * Resources of activity
     */
    private Resources mRes;
    /**
     * Bitmap image  of item
     */
    private Bitmap mBitmap;

    /**
     * Boolean true when fragment started
     */
    private boolean mIsStarting;
    /**
     * List<String> dynamic list of strings for RecyclerView of text
     * Grows when scrolling dwon
     */
    private ArrayList<String> mList;
    /**
     * List<String> static list of strings with all text of item
     */
    private ArrayList<String> mListSource;
    /**
     * RecyclerView  with strings of text of item
     * Every item is a part of text between "/r/n/r/n" characters
     */
    private RecyclerView mRecyclerBody;
    /**
     * RecyclerBodyAdapter adapter for RecyclerView mRecyclerBody
     */
    private RecyclerBodyAdapter mRecyclerBodyAdapter;

    /**
     * Returns new Fragment with Bundle of arguments
     *
     * @param startingItemId int starting item ID
     * @param currentItemId  int current item ID
     * @return Fragment  with arguments
     */
    public static Fragment newInstance(long startingItemId, long currentItemId) {
        Bundle arguments = new Bundle();
        arguments.putLong(BUNDLE_FRAGMENT_STARTING_ID, startingItemId);
        arguments.putLong(BUNDLE_FRAGMENT_CURRENT_ID, currentItemId);
        ArticleDetailFragment fragment = new ArticleDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    /**
     * Attaches context to fragment
     * Load typeface from assets
     *
     * @param context Context of calling activity
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCaecilia = Typeface.createFromAsset(context.getAssets(), "caecilia-light-webfont.ttf");
    }


    /**
     * Creates fragment
     * Postpone transition of shared elements
     * Extract arguments to mStartingItemId and mCurrentItemId
     * Restores mList and mListSource Lst<String>  object with text data of fragment
     * Runs loader for Cursor object
     *
     * @param savedInstanceState Bundle with instance state data
     */
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

        getLoaderManager().initLoader(0, null, this);
    }

    /**
     * Creates main view of Fragment
     * Binds all views tha used in fragment
     * Setup listeners
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_article_detail, container, false);
        mRes = getResources();
        mIsWide = mRes.getBoolean(R.bool.is_wide);
        mDateFormat = new SimpleDateFormat(getString(R.string.calendar_format), Locale.ENGLISH);
        mOutputFormat = new SimpleDateFormat();
        mStartOfEpoch = new GregorianCalendar(
                mRes.getInteger(R.integer.calendar_year),
                mRes.getInteger(R.integer.calendar_moanth),
                mRes.getInteger(R.integer.calendar_day)
        );

        setupViews();
        setupActionBar();
        setupListeners();
        setupRecycler();

        mProgressBarText.setVisibility(View.VISIBLE);
        mProgressBarImage.setVisibility(View.VISIBLE);
        return mRootView;
    }

    /**
     * Saves parameters to Bundle storage object
     *
     * @param outState Bundle storage object for parameters.
     *                 Bundle Parameters: <br>
     *                 List<String>         mList           dynamic list of text of item
     *                 List<String>         mListSource     static list with full text of item
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(BUNDLE_FRAGMENT_TEXT_RECYCLER, mList);
        outState.putStringArrayList(BUNDLE_FRAGMENT_TEXT_SOURCE, mListSource);
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
        return ArticleLoader.newInstanceForItemId(getActivity(), mCurrentItemId);
    }

    /**
     * Called when loader is finished load, provides Cursor object with data.
     * Cursor object copied to mPagerAdapter.
     * When fragment is started first time  mList loaded with first 2000 characters only.
     * When fragment rotated mList restored from Bundle storage.
     * mLIst with data passed to RecyclerBodyAdapter.
     *  Method bindViews() fills mTitleView, mSubTitleView and load image to mTolbarImage objects
     *
     * @param loader Loader<Crusor> the Loader that has finished.
     * @param cursor Cursor the data generated by the Loader.
     */
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

    /**
     * Resets loader and makes deletes Cursor object.
     * Notified ViewPager Adapter that cursor is erased.
     *
     * @param loader Loader<Cursor> loader which is reset.
     */

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursor = null;
    }

    /**
     *  Copy  portion of strings from mListSource to mList
     *  Updates mRecyclerBodyAdapter with new data
     *
     * @param isLoadAll  boolean true if all content of mListSource copied to nList
     */
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

    /**
     *  Returns Date object.
     *  Loads data string from Cursor object and converts string to Date object
     *
     * @return  Date converted from string
     */
    private Date parsePublishedDate() {
        try {
            String date = mCursor.getString(ArticleLoader.Query.PUBLISHED_DATE);
            return mDateFormat.parse(date);
        } catch (ParseException ex) {
            Timber.d("Error passing today's date: " + ex.getMessage());
            return new Date();
        }
    }

    /**
     *  Fills views of fragment with the data from Cursor object.
     *  mTitleView, mSubtitleView loaded directly from Cursor object.
     *  mToolbarImage loaded from the Internet with the Glide library.
     *  Fills mBitmap with loaded image.
     *  Sets transition names to mTitleView and mToolbarImage objects.
     *
     */
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

    /**
     *  Returns Bitmap object from mBitmap.
     * @return Bitmap image.
     */
    public Bitmap getBitmap() {
        return mBitmap;
    }

    /**
     *  Returns converted string from Hrtml to Text format.
     *  Clears text from single "/r/n" or "/n" characters.
     *  Uses Html.fromHtml() method to convert text form HTML to Plain Text.
     *
     * @param text  String input HTML string.
     * @return String formatted plain text string.
     */
    private String htmlConvert(String text) {
        text = text.replaceAll("(\r\n\r\n\r\n|\n\n\n)", "<br/><br/><br/>");
        text = text.replaceAll("(\r\n\r\n|\n\n)", "<br/><br/>");//&emsp;");
        text = text.replaceAll("(\r\n|\n)", " ");

        return Html.fromHtml(text).toString();
    }

    /**
     *  Returns List<String> with text of item
     *  Splits all text into pages with 2000 characters and saves pages to ArrayList.
     *  It's necessary because of Html.fromHtml() is very slow method and conversion of
     *  all text takes up to 10 seconds per item.
     *  Next stuck point is TextView loading. Full text loaded into TextView up to 3 seconds.
     *  To reduce delay all text split into small portions and conversion done
     *  by no more than 2000 characters per time.
     *  This is reduces time for conversion and loading to TextView to less than a second.
     *
     * @return  List<Sting>  list with text of item
     */
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


    /**
     *  Returns true if  shared element is visible before transition.
     *  If element is invisible it removed from transition.
     *
     * @param container  View paren view of shared element.
     * @param view  View shared element.
     * @return  boolean true if shared element is visible and false if not.
     */
    private boolean isViewInBounds(@NonNull View container, @NonNull View view) {
        Rect containerBounds = new Rect();
        container.getHitRect(containerBounds);
        return view.getLocalVisibleRect(containerBounds);
    }

    /**
     *  Returns List<View> of shared element View objects
     * @return  List<View> of shared element View objects
     */
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

    /**
     *  Returns root view of Fragment.
     * @return View  root view of Fragment.
     */
    public View getRootView() {
        return mRootView;
    }

    /**
     *  Binds all views of Fragment to layout
     */
    private void setupViews() {
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
    }

    /**
     *  Setup ActionBar of Fragment
     *
     */
    private void  setupActionBar() {
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
    }

    /**
     * Setups listeners to Fab, image buttons, FrameLayout of text and NestedScrollView
     * Listeners: <br/>
     * <p>
     * mFrameLayout        calls every time when text added to RecyclerView
     * mNestedScrollView   tracks scrolling and adds text to RecyclerView
     * mFab                used for Intent Action.SHARE.
     * mImageButtonLeft    scrolls text to top of RecyclerView
     * mImageButtonRight   add all remained text to RecyclerView and scrolls to bottom of RecyclerView
     * mImageButtonHome    calls onBackPressed()
     * mImageButtonFullScreen  calls onCallback() method to exit from full screen mode
     */
    private void setupListeners() {
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
    }

    /**
     *  Setup RecyclerView of text of item
     */
    private void setupRecycler() {
        mRecyclerBody = mRootView.findViewById(R.id.article_body_recycler);
        mRecyclerBodyAdapter = new RecyclerBodyAdapter(getActivity(), mCaecilia);
        mRecyclerBody.setAdapter(mRecyclerBodyAdapter);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL,
                false);
        mRecyclerBody.setLayoutManager(layoutManager);
    }
}
