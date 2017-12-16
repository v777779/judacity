package ru.vpcb.builditbigger;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

/**
 *  RecyclerView Adapter class
 *  Used to create and show Item objects of RecyclerView
 */

public class JokeAdapter extends RecyclerView.Adapter<JokeAdapter.JKViewHolder> {
    /**
     *  Context  context of calling activity
     */
    Context mContext;
    /**
     *  List<Integer>  list with image resource values used as data source for RecyclerView
     */
    private List<Integer> mList;
    /**
     *  Integer value of width of Item
     */
    private int mWidth;
    /**
     *  Integer value of height of Item
     */
    private int mHeight;

    /**
     *  Constructor
     *
     * @param context   Context  context of calling activity
     * @param list      List<Integer>  list with image resource values used as data source for RecyclerView
     * @param width     Integer value of width of Item
     * @param height    Integer value of width of Item
     */
    public JokeAdapter(Context context, List<Integer> list, int width, int height) {
        mContext = context;
        mList = list;
        mWidth = width;
        mHeight = height;
    }

    /**
     *  Creates ViewHolder of Item of RecyclerView
     *  Sets width or height of item according to span and size of RecyclerView Container
     * @param parent    ViewGroup parent of item
     * @param viewType  int type of View of Item, unused in this application
     * @return  ViewHolder of Item of RecyclerView
     */
    @Override
    public JKViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.joke_recycler_item, parent, false);

        if (mContext.getResources().getBoolean(R.bool.is_vert)) {
            itemView.getLayoutParams().height = mHeight;
        } else {
            itemView.getLayoutParams().width = mWidth;
        }
        return new JKViewHolder(itemView);
    }

    /**
     *  Fills ViewHolder Item with images from data source.
     *  Sets onClickListener which calls  ICllback.onComlete(int) method in calling Activity.
     *  This method in turn emulates button.click and starts new request to EndpointAsyncTask.
     *  and ultimately replaces Fragment with new one.
     *
     * @param holder       ViewHolder object which is filled
     * @param position      int position of imageId in mList List<Integer> data source
     */
    @Override
    public void onBindViewHolder(JKViewHolder holder, final int position) {

        if (mList == null || position < 0 || position > mList.size() - 1) return;
        holder.fill(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ICallback)mContext).onComplete(mList.get(position));
            }
        });

    }

    /**
     *  Returns number of Items of List<Integer> mList data source
     * @return  int number of Items of List<Integer> mList data source
     */
    @Override
    public int getItemCount() {
        if (mList == null) return 0;
        return mList.size();
    }

    /**
     *  ViewHolder class of RecyclerView Item
     *  Used to hold image resources of Item of RecyclerView
     *
     */
    public class JKViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImage;

        /** Constructor
         *  Saves ImageView object from Item to local mImage object
         *
         * @param itemView  View object of Item
         */
        public JKViewHolder(View itemView) {
            super(itemView);
            mImage = itemView.findViewById(R.id.joke_item_image);
//            mImage.getLayoutParams().height = mHeight;

        }

        /**
         * Fills mImage with new image resource.
         *  Extract imageId from mList by position
         *  Set image resource , using input imageId as parameter
         *
         * @param position
         */
        private void fill(int position) {
            mImage.setImageResource(mList.get(position));
        }

    }


}
