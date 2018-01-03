package com.example.xyzreader.ui;


import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.NestedScrollView;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
    private Resources mRes;
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
    private SimpleDateFormat mOutputFormat = new SimpleDateFormat();
    private GregorianCalendar mStartOfEpoch = new GregorianCalendar(2, 1, 1);


    private LayoutInflater mInflater;
    private LinearLayout mLinearLayout;
    private String mTextSource;
    private int mTextSize;
    //    private ListView mListView;
//    private ArticleArrayAdapter mArrayAdapter;
    private List<String> mList;
    private int mListIndex;
    private final int SIZE_TEXT = 2000;
    private LinearLayout.LayoutParams mLayoutParams;

// fab
    private FloatingActionButton fabLeft;
    private FloatingActionButton fabRight;


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
        mRes = getResources();

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

    private int mCounterId = 1221;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_article_detail, container, false);
//        mBodyText = mRootView.findViewById(R.id.article_body);
//        mBodyText.setTypeface(mCaecilia);


        mDateFormat = new SimpleDateFormat(mRes.getString(R.string.calendar_format));
        mOutputFormat = new SimpleDateFormat();
        mStartOfEpoch = new GregorianCalendar(
                mRes.getInteger(R.integer.calendar_year),
                mRes.getInteger(R.integer.calendar_moanth),
                mRes.getInteger(R.integer.calendar_day)
        );

        mNestedScrollView = mRootView.findViewById(R.id.nested_scrollview);

        mLinearLayout = mRootView.findViewById(R.id.linear_body);
        mInflater = inflater;

        mList = new ArrayList<>();
//        mListView = mRootView.findViewById(R.id.article_body_listview);
//        mArrayAdapter = new ArticleArrayAdapter(getContext(), mList);
//        mListView.setAdapter(mArrayAdapter);
        mLayoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        int side_margin = getResources().getDimensionPixelSize(R.dimen.large_margin);
        mLayoutParams.setMargins(side_margin, 0, side_margin, 0);


        mNestedScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            int mLineY = 0;
            int mLastY = 0;

            @Override
            public void onScrollChanged() {
                int dY = mNestedScrollView.getScrollY();
                float y = mNestedScrollView.getY();
                if (dY > 0) {
                    mLineY = dY;

                    if (mLineY > mLastY && mLineY > mLinearLayout.getHeight() - 700 && mTextSize < mTextSource.length()) {


//                        String subText = htmlConvert(mTextSource.substring(mTextSize, mTextSize + 1000));
//
//                        View item = mInflater.inflate(R.layout.fragment_text_item, null);
//                        TextView textView = item.findViewById(R.id.article_body_ext);
//                        textView.setText(subText);
//                        mLinearLayout.addView(textView);
//
//                        mLastY = mLineY;
//                        mTextSize += subText.length();

                        View item = mInflater.inflate(R.layout.fragment_text_item, null);
                        TextView textView = item.findViewById(R.id.article_body_ext);
                        textView.setTypeface(mCaecilia);

                        String subText = "";
                        int pos = mTextSource.indexOf("\n", mTextSize + SIZE_TEXT);
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

                        mLastY = mLineY;

                    }
                }
            }
        });



// fab
        fabLeft = mRootView.findViewById(R.id.fab_left);
        fabRight = mRootView.findViewById(R.id.fab_right);
        fabLeft.animate().alpha(0).setDuration(500).start();
        fabRight.animate().alpha(0).setDuration(500).start();

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

        titleView.setText(mCursor.getString(ArticleLoader.Query.TITLE));
        Date publishedDate = parsePublishedDate();
        if (!publishedDate.before(mStartOfEpoch.getTime())) {
            bylineView.setText(Html.fromHtml(
                    DateUtils.getRelativeTimeSpanString(
                            publishedDate.getTime(),
                            System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                            DateUtils.FORMAT_ABBREV_ALL).toString()
                            + " by " + mCursor.getString(ArticleLoader.Query.AUTHOR)));

            CharSequence cs = DateUtils.getRelativeTimeSpanString(
                    publishedDate.getTime(),
                    System.currentTimeMillis(),
                    DateUtils.HOUR_IN_MILLIS,
                    DateUtils.FORMAT_ABBREV_ALL);

            String s2 = " by <font color='#ffffff'>" + mCursor.getString(ArticleLoader.Query.AUTHOR) + "</font>";

        } else {
            // If date is before 1902, just show the string
            bylineView.setText(Html.fromHtml(
                    mOutputFormat.format(publishedDate) + " by <font color='#ffffff'>"
                            + mCursor.getString(ArticleLoader.Query.AUTHOR)
                            + "</font>"));

        }

        String text = mCursor.getString(ArticleLoader.Query.BODY);
        mTextSource = text;


// 3
//        List<String> list = new ArrayList<>(Arrays.asList(text.split("\r\n")));
//
//        List<String> subList = list.subList(0, 25); // 25 subblocks
//        mList.addAll(subList);
//        mArrayAdapter.notifyDataSetChanged();
//        mListView.getLayoutParams().height = 200;
//        final int size =  25;
//        final int height = 25*40;
//        mListView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                if(motionEvent.getAction()== MotionEvent.ACTION_MOVE) {
//                    if(mListView.getLayoutParams().height < height) {
//                        mListView.getLayoutParams().height = height;
//                        mArrayAdapter.notifyDataSetChanged();
//                    }
//                }
//                return false;
//            }
//
//        });


// 2
        View item = mInflater.inflate(R.layout.fragment_text_item, null);
        TextView textView = item.findViewById(R.id.article_body_ext);
        textView.setTypeface(mCaecilia);
//        textView.setLayoutParams(mLayoutParams);


        int pos = text.indexOf("\n", SIZE_TEXT);
        if (text.substring(pos - 1, pos).equals("\r")) pos = pos - 1;
        String subText = text.substring(0, pos);
        mTextSize = pos; // last not used position


        subText = htmlConvert(subText);
        textView.setText(subText);
        mLinearLayout.addView(textView);


// 1
//        mBodyText.setText(htmlConvert(subText));
//        int counter = 0;
//        for (int i = 0; i < 5; i++) {
//            View item = mInflater.inflate(R.layout.fragment_text_item, null);
//            TextView textView = item.findViewById(R.id.article_body_ext);
//            String subText = text.substring(counter, counter + 200);
//            textView.setText(subText);
//            mLinearLayout.addView(textView);
//            counter += 200;
//        }

//        mAdapter.notifyDataSetChanged();

//        new BindTask().execute(text);


//            bodyView.setText(Html.fromHtml(mCursor.getString(ArticleLoader.Query.BODY).replaceAll("(\r\n|\n)", "<br />")));
//        ImageLoaderHelper.getInstance(getActivity()).getImageLoader()
//                .get(mCursor.getString(ArticleLoader.Query.PHOTO_URL), new ImageLoader.ImageListener() {
//                    @Override
//                    public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
//                        Bitmap bitmap = imageContainer.getBitmap();
//                        if (bitmap != null) {
//
//                            Palette p = Palette.generate(bitmap, 12);
//                            mMutedColor = p.getDarkMutedColor(0xFF333333);
//                            mPhotoView.setImageBitmap(imageContainer.getBitmap());
//                            mRootView.findViewById(R.id.meta_bar)
//                                    .setBackgroundColor(mMutedColor);
//                            updateStatusBar();
//                        }
//                    }
//
//                    @Override
//                    public void onErrorResponse(VolleyError volleyError) {
//
//                    }
//                });


    }

    private String htmlConvertLight(String text) {
        text = text.replaceAll("(\r\n\r\n)", "<br/><br/>");//&emsp;");
        return Html.fromHtml(text).toString();
    }


    private String htmlConvert(String text) {
        text = text.replaceAll("(\r\n\r\n\r\n|\n\n\n)", "<br/><br/><br/>");
        text = text.replaceAll("(\r\n\r\n|\n\n)", "<br/><br/>");//&emsp;");
        text = text.replaceAll("(\r\n|\n)", " ");

        return Html.fromHtml(text).toString();
    }


    private class BindTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            if (strings == null || strings.length == 0 || strings[0] == null || strings[0].length() == 0)
                return "";

            return htmlConvert(strings[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            mBodyText.setText(s);
        }
    }
}
