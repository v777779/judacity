package com.example.xyzreader.ui;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;
import com.example.xyzreader.remote.Config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class ArticleListAdapter extends RecyclerView.Adapter<ArticleListAdapter.ViewHolder> {
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat outputFormat;
    private GregorianCalendar startOfEpoch;

    private Cursor mCursor;
    private Context mContext;
    private Config.Span mSpan;

    // correction!!!
    public ArticleListAdapter(Context context, Config.Span sp) {
        mContext = context;
        mSpan = sp;

        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
        outputFormat = new SimpleDateFormat();
        startOfEpoch = new GregorianCalendar(2, 1, 1);

    }

    @Override
    public long getItemId(int position) {
        mCursor.moveToPosition(position);
        return mCursor.getLong(ArticleLoader.Query._ID);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = ((AppCompatActivity) mContext).getLayoutInflater()
                .inflate(R.layout.content_article_item, parent, false);

        view.getLayoutParams().height = mSpan.getHeight();
        return new ViewHolder(view);
    }

    private Date parsePublishedDate() {
        try {
            String date = mCursor.getString(ArticleLoader.Query.PUBLISHED_DATE);
            return dateFormat.parse(date);
        } catch (ParseException ex) {
            Timber.e("Error passing date:" + ex.getMessage());
            return new Date();
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final long itemId = getItemId(position);
        holder.fill(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = ItemsContract.Items.buildItemUri(itemId);
                ((ICallback) mContext).onCallback(uri);

            }
        });

    }

    @Override
    public int getItemCount() {
        if (mCursor == null) return 0;
        return mCursor.getCount();
    }

    public void setCursor(Cursor cursor) {
        if (cursor == null) return;
        mCursor = cursor;
        notifyDataSetChanged();
    }

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

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

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

            String imageURL = mCursor.getString(ArticleLoader.Query.THUMB_URL);
            if (imageURL != null && !imageURL.isEmpty()) {
                Glide.with(mContext)
                        .load(imageURL)
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.empty_loading)
                                .error(R.drawable.error_loading))
                        .into(mItemImage);
            } else {
                mItemImage.setImageResource(R.drawable.error_loading);
            }
        }

    }
}