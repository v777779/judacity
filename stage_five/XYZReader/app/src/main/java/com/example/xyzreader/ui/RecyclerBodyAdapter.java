package com.example.xyzreader.ui;


import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xyzreader.R;

import java.util.List;
/**
 *  RecyclerView Adapter class
 *  Used to show Text of item in ArticleFragmentDetail
 */
public class RecyclerBodyAdapter extends RecyclerView.Adapter<RecyclerBodyAdapter.ViewHolder> {
    /**
     *  List<String>  source of Text for  RecyclerBodyAdapter
     *  Grows dynamically while scrolling text down
     */
    private List<String> mList;
    /**
     *  Context context of calling activity
     */
    private Context mContext;
    /**
     * Typeface for text
     */
   private Typeface mCaecilia;

    /**
     * Constructor of RecyclerBodyAdapter
     *
     * @param context  Context of calling activity
     */
    public RecyclerBodyAdapter(Context context, Typeface caecilia) {
        mContext = context;
        mCaecilia = caecilia;
    }

    /**
     *  Returns itemID by position
     *
     * @param position in position of item
     * @return int itemID
     */
    @Override
    public long getItemId(int position) {
        return position;
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
                .inflate(R.layout.fragment_text_item, parent, false);


        return new ViewHolder(view);
    }

    /**
     *  Fills ViewHolder Item with text from data source.
     *
     * @param holder       ViewHolder object which is filled
     * @param position      int position of item in List<String> data source
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.fill(position);

    }

    /**
     *  Returns number of Items of List<String> mList data source
     * @return  int number of Items of List<string> mList data source
     */
    @Override
    public int getItemCount() {
        if (mList == null) return 0;
        return mList.size();
    }

    /**
     *  Replaces mCursor with new Cursor object and
     *  calls notifyDataSetChanged() method.
     *
     * @param list List<String> parameter
     */
    public void swap(List<String> list) {
        if (list == null) return;
        mList = list;
        notifyDataSetChanged();
    }

    /**
     *  ViewHolder class of RecyclerView Item
     *  Used to hold text resources of Item of RecyclerView
     *
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mItemText;
        /**
         * Constructor
         *  Binds TextView and set typeface to mCaecilia object.
         *
         * @param view View of parent
         */

        public ViewHolder(View view) {
            super(view);
            mItemText = view.findViewById(R.id.article_body_ext);
            mItemText.setTypeface(mCaecilia);
        }

        /**
         * Fills mItemText with the data from mList object
         *
         * @param position int position of item in RecyclerView
         */
        private void fill(int position) {
            if (mList == null || position < 0 || position >= mList.size()) return;

            String s = mList.get(position);
            if (s != null && !s.isEmpty()) {
                mItemText.setText(s);
            }
        }
    }


}