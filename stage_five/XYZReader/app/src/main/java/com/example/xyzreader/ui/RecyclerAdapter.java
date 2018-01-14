package com.example.xyzreader.ui;


import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.xyzreader.remote.Config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 *  RecyclerView Adapter class
 *  Used to create and show Item objects of RecyclerView
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    /**
     * SimpleDateFormat formatter of date and time string
     */
    private SimpleDateFormat dateFormat;
    /**
     * SimpleDateFormat formatter of date and time string
     */
    private SimpleDateFormat outputFormat;
    /**
     * GregorianCalendar provides the standard calendar system used by most of the world
     */
    private GregorianCalendar startOfEpoch;
    /**
     * Cursor object source of data
     */
    private Cursor mCursor;
    /**
     *  Context  context of calling activity
     */
    private Context mContext;
    /**
     * Span object used for RecyclerView as storage of display item parameters
     */
    private Config.Span mSpan;
    /**
     * Boolean is true for landscape layout
     */
    private boolean mIsLand;
    /**
     * Boolean is true for tablet with sw800dp
     */
    private boolean mIsWide;
    /**
     * Resources of activity
     */
    private Resources mRes;

    /**
     * Constructor of RecyclerAdapter
     *
     *
     * @param context  Context of calling activity
     * @param sp Span  object used for RecyclerView as storage of display item parameters
     */
    public RecyclerAdapter(Context context, Config.Span sp) {
        mContext = context;
        mSpan = sp;
        mRes =  context.getResources();
        dateFormat = new SimpleDateFormat(mRes.getString(R.string.datetime_pattern));
        outputFormat = new SimpleDateFormat();
        startOfEpoch = new GregorianCalendar(2, 1, 1);

        mIsWide = mRes.getBoolean(R.bool.is_wide);
        mIsLand = mRes.getBoolean(R.bool.is_land);
    }

    /**
     *  Returns itemID by position
     *
     * @param position in position of item
     * @return int itemID
     */
    @Override
    public long getItemId(int position) {
        mCursor.moveToPosition(position);
        return mCursor.getLong(ArticleLoader.Query._ID);
    }

    /**
     *  Creates ViewHolder of Item of RecyclerView
     *  Sets width or height of item according to span and size of RecyclerView Container
     * @param parent    ViewGroup parent of item
     * @param viewType  int type of View of Item, unused in this application
     * @return  ViewHolder of Item of RecyclerView
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = ((AppCompatActivity) mContext).getLayoutInflater()
                .inflate(R.layout.content_article_item, parent, false);

        if (mIsWide && !mIsLand) {
            view.getLayoutParams().width = mSpan.getWidth();
        }else {
            view.getLayoutParams().height = mSpan.getHeight();
        }
        return new ViewHolder(view);
    }

    /**
     *  Returns Date object.
     *  Extract string with date from cursor and converts it to the Date object.
     *
     * @return Date object.
     */
    private Date parsePublishedDate() {
        try {
            String date = mCursor.getString(ArticleLoader.Query.PUBLISHED_DATE);
            return dateFormat.parse(date);
        } catch (ParseException ex) {
            Timber.e("Error passing date:" + ex.getMessage());
            return new Date();
        }
    }

    /**
     *  Fills ViewHolder Item with image and text from data source.
     *  Sets onClickListener which calls  ICallback.onComlete(view, int) method in calling activity.
     *  This method in turn selects new item in corresponding ViewPager
     *  and ultimately replaces ArticleDetailFragment with the new one.
     *
     * @param holder       ViewHolder object which is filled
     * @param position      int position of item in Cursor data source
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.fill(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ICallback) mContext).onCallback(view, position);
            }
        });

    }

    /**
     *  Returns number of Items of Cursor mCursor data source
     * @return  int number of Items of Cursor mCursor data source
     */
    @Override
    public int getItemCount() {
        if (mCursor == null) return 0;
        return mCursor.getCount();
    }

    /**
     *  Replaces mCursor with new Cursor object and
     *  calls notifyDataSetChanged() method.
     *
     * @param cursor Cursor parameter.
     */
    public void swap(Cursor cursor) {
        if (cursor == null) return;
        mCursor = cursor;
        notifyDataSetChanged();
    }

    /**
     *  ViewHolder class of RecyclerView Item
     *  Used to hold text and image resources of Item of RecyclerView
     *
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        @Nullable
        @BindView(R.id.article_title)
        TextView mItemTitle;
        @Nullable
        @BindView(R.id.article_subtitle)
        TextView mItemSubtitle;
        @Nullable
        @BindView(R.id.article_image)
        ImageView mItemImage;

        @Nullable
        @BindView(R.id.progress_bar_image)
        ProgressBar mProgressBarImage;

        /**
         * Constructor
         *  Binds all views with the ButterKnife object.
         *
         * @param view View of parent
         */
        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        /**
         * Fills mItemTitle, mItemSubtitle with the data from mCursor object
         * Loads image with the Glide loader to mToolbarImage.
         * Sets transition names to mItemTitle and  mToolbarImage objects.
         *
         * @param position  int position of item in RecyclerView
         */
        private void fill(int position) {
            mCursor.moveToPosition(position);

            mItemTitle.setText(mCursor.getString(ArticleLoader.Query.TITLE));
            Date publishedDate = parsePublishedDate();
            if (!publishedDate.before(startOfEpoch.getTime())) {
                mItemSubtitle.setText(Html.fromHtml(
                        DateUtils.getRelativeTimeSpanString(
                                publishedDate.getTime(),
                                System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                                DateUtils.FORMAT_ABBREV_ALL).toString()
                                + "<br/>" + " by "
                                + mCursor.getString(ArticleLoader.Query.AUTHOR)));
            } else {
                mItemSubtitle.setText(Html.fromHtml(
                        outputFormat.format(publishedDate)
                                + "<br/>" + " by "
                                + mCursor.getString(ArticleLoader.Query.AUTHOR)));
            }

            mProgressBarImage.setVisibility(View.VISIBLE);

            String imageURL = mCursor.getString(ArticleLoader.Query.THUMB_URL);

            Glide.with(mContext)
                    .load(imageURL)
                     .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            mProgressBarImage.setVisibility(View.INVISIBLE);
                            return false;
                        }
                    })
                    .into(mItemImage);
            mItemImage.setTransitionName(mRes.getString(R.string.transition_image, getItemId()));
            mItemTitle.setTransitionName(mRes.getString(R.string.transition_title, getItemId()));

        }
    }


}