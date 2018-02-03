package ru.vpcb.viewpagertab;


import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * RecyclerView Adapter class
 * Used to create and show Item objects of RecyclerView
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    /**
     * Cursor object source of data
     */
    private Cursor mCursor;
    /**
     * Context  context of calling activity
     */
    private Context mContext;
    private List<String> mList;
    private DateFormat mDateFormat;

    /**
     * Constructor of RecyclerAdapter
     *
     * @param context Context of calling activity
     * @param list    List<String>  object used for RecyclerView as storage of display item parameters
     */
    public RecyclerAdapter(Context context, List<String> list) {
        mContext = context;
        mList = list;
        mDateFormat = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT);
    }

    /**
     * Returns itemID by position
     *
     * @param position in position of item
     * @return int itemID
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Returns item viewType by position
     *
     * @param position in position of item
     * @return int item viewType
     */

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    /**
     * Creates ViewHolder of Item of RecyclerView
     * Sets width or height of item according to span and size of RecyclerView Container
     *
     * @param parent   ViewGroup parent of item
     * @param viewType int type of View of Item, unused in this application
     * @return ViewHolder of Item of RecyclerView
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = ((AppCompatActivity) mContext).getLayoutInflater()
                .inflate(R.layout.recycler_match_item, parent, false);

//        view.getLayoutParams().height = mSpan.getHeight();
        return new ViewHolder(view);
    }


    /**
     * Fills ViewHolder Item with image and text from data source.
     * Sets onClickListener which calls  ICallback.onComplete(view, int) method in calling activity.
     *
     * @param holder   ViewHolder object which is filled
     * @param position int position of item in Cursor data source
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.fill(position);
        final int pos = position;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Recycler item clicked on", BaseTransientBottomBar.LENGTH_SHORT)
                        .show();
            }
        });

    }

    /**
     * Returns number of Items of Cursor mCursor data source
     *
     * @return int number of Items of Cursor mCursor data source
     */
    @Override
    public int getItemCount() {
        if (mList == null) return 0;
        return mList.size();
    }

    /**
     * Replaces mList with new List<String> object and
     * calls notifyDataSetChanged() method.
     *
     * @param list List<String> parameter.
     */
    public void swap(List<String> list) {
        if (list == null) return;
        mList = list;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder class of RecyclerView Item
     * Used to hold text and image resources of Item of RecyclerView
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        @Nullable
        @BindView(R.id.text_tm_item_time)
        TextView mTextTime;

        /**
         * Constructor
         * Binds all views with the ButterKnife object.
         *
         * @param view View of parent
         */
        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        /**
         * Fills TextViews with the data from source data object
         * Loads image with the Glide loader to ImageViews.
         *
         * @param position int position of item in RecyclerView
         */
        private void fill(int position) {
            if (mList == null) return;
            String s = mList.get(position);
            mTextTime.setText(s);

        }


    }


}