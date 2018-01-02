package com.example.xyzreader.ui;


import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.graphics.Palette;
import android.text.Html;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import timber.log.Timber;

import static com.example.xyzreader.remote.Config.BUNDLE_FRAGMENT_ID;

/**
 * Created by V1 on 30-Dec-17.
 */

public class ArticleDetailFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private View mRootView;
    private TextView mBodyText;

    private Cursor mCursor;
    private long mItemId;
    private Typeface mCaecilia;
    private Resources mRes;
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
    private SimpleDateFormat mOutputFormat = new SimpleDateFormat();
    private GregorianCalendar mStartOfEpoch = new GregorianCalendar(2, 1, 1);


    public static ArticleDetailFragment newInstance(long itemId) {
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
        Timber.d("lifecycle fragment: onCreate():"+mItemId);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_article_detail, container, false);
        mBodyText = mRootView.findViewById(R.id.article_body);
        mBodyText.setTypeface(mCaecilia);

        mDateFormat = new SimpleDateFormat(mRes.getString(R.string.calendar_format));
        mOutputFormat = new SimpleDateFormat();
        mStartOfEpoch = new GregorianCalendar(
                mRes.getInteger(R.integer.calendar_year),
                mRes.getInteger(R.integer.calendar_moanth),
                mRes.getInteger(R.integer.calendar_day)
        );
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
        TextView bodyView = mRootView.findViewById(R.id.article_body);
        bodyView.setTypeface(mCaecilia);

//        mRootView.setAlpha(0);
//        mRootView.setVisibility(View.VISIBLE);
//        mRootView.animate().alpha(1);
        titleView.setText(mCursor.getString(ArticleLoader.Query.TITLE));
        Date publishedDate = parsePublishedDate();
        if (!publishedDate.before(mStartOfEpoch.getTime())) {
            bylineView.setText(Html.fromHtml(
                    DateUtils.getRelativeTimeSpanString(
                            publishedDate.getTime(),
                            System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                            DateUtils.FORMAT_ABBREV_ALL).toString()
                            + " by "+ mCursor.getString(ArticleLoader.Query.AUTHOR)));

        CharSequence cs = DateUtils.getRelativeTimeSpanString(
                publishedDate.getTime(),
                System.currentTimeMillis(),
                DateUtils.HOUR_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_ALL);

        String s2 = " by <font color='#ffffff'>"+ mCursor.getString(ArticleLoader.Query.AUTHOR)+"</font>";

        } else {
            // If date is before 1902, just show the string
            bylineView.setText(Html.fromHtml(
                    mOutputFormat.format(publishedDate) + " by <font color='#ffffff'>"
                            + mCursor.getString(ArticleLoader.Query.AUTHOR)
                            + "</font>"));

        }

        String text = mCursor.getString(ArticleLoader.Query.BODY);
//        text = text.replaceAll("(\r\n\r\n\r\n|\n\n\n)", "<br/><br/><br/>");
//        text = text.replaceAll("(\r\n\r\n|\n\n)", "<br/><br/>");//&emsp;");
//        text = text.replaceAll("(\r\n|\n)", " ");
        bodyView.setText(Html.fromHtml(text));


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
}
