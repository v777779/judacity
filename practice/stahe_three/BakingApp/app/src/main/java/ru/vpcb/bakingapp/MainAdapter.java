package ru.vpcb.bakingapp;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.vpcb.bakingapp.data.RecipeItem;

import static ru.vpcb.bakingapp.data.RecipeContract.RecipeEntry.COLUMN_RECIPE_IMAGE;
import static ru.vpcb.bakingapp.data.RecipeContract.RecipeEntry.COLUMN_RECIPE_NAME;


/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 15-Nov-17
 * Email: vadim.v.voronov@gmail.com
 */

/**
 * MainActivity RecyclerView Adapter Class with RecipeItem items
 */
public class MainAdapter extends RecyclerView.Adapter<MainAdapter.FCViewHolder> {


    /**
     * Callback interface object for RecyclerView Adapter
     */
    private IFragmentHelper mHelper;
    /**
     * Cursor object with RecipeItem data
     */
    private Cursor mCursor;
    /**
     * Context of current activity
     */
    private Context mContext;
    /**
     * The height of Item View in pixels
     */
    private int mSpanHeight;

    /**
     * Constructir of RecyclerView Adapter of RecipeItem items
     *
     * @param context    Context of current activity
     * @param helper     IFragmentHelper callback interface object for RecyclerView Adapter
     * @param spanHeight int height of Item View in pixels
     */
    public MainAdapter(Context context, IFragmentHelper helper, int spanHeight) {
        mContext = context;
        mHelper = helper;
        mSpanHeight = spanHeight;
        mCursor = null;


    }

    /**
     * Creates FCViewHolder object of item
     *
     * @param parent   ViewGroup parent View
     * @param viewType int type of itemView
     * @return FCViewHolder object of item
     */
    @Override
    public FCViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.fragment_main_item, parent, false);
        return new FCViewHolder(itemView);
    }

    /**
     * Binds RecipeItem data to itemView object
     * Instantiates onClick() method which selects item and
     * calls IFragmentHelper.callback for all items.
     *
     * @param holder   FCViewHolder object of Step item
     * @param position int position in the Cursor RecipeItem object
     */
    @Override
    public void onBindViewHolder(FCViewHolder holder, final int position) {

        if (mCursor == null || position < 0 || position > mCursor.getCount() - 1) return;
        holder.fill(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHelper.onCallback(position);
            }
        });
    }

    /**
     * Returns the number RecipeItem  objects in Cursor object
     *
     * @return int number RecipeItem  objects in Cursor object
     */
    @Override
    public int getItemCount() {
        if (mCursor == null) return 0;
        return mCursor.getCount();
    }


    /**
     * Replaces Cursor object by the new one.
     * If cursor is not empty the notifyDataSetChanged() called.
     *
     * @param cursor Cursor input cursor object
     * @return Cursor current cursor object
     */
    public Cursor swapCursor(Cursor cursor) {
        Cursor oldCursor = mCursor;
        mCursor = cursor;
        if (cursor != null) {
            notifyDataSetChanged();
        }
        return oldCursor;
    }


    /**
     * FCViewHolder class of RecyclerView.
     */
    class FCViewHolder extends RecyclerView.ViewHolder {
        /**
         *  The name of RecipeItem object
         */
        @Nullable
        @BindView(R.id.fc_recycler_text)
        TextView mText;
        /**
         * The preview image of RecipeItem object
         */
        @Nullable
        @BindView(R.id.fc_recycler_image)
        ImageView mImage;

        /**
         *  Constructor
         *  Sets the height of item to mSpanHeight value
         *  The number of object in row was set when new GridLayout() constructor called;
         *
         * @param itemView View item object
         */
        public FCViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mImage.getLayoutParams().height = mSpanHeight;

        }

        /**
         *  Fills itemView with data from Cursor data source
         *  The name of recipe set from cursor object
         *  The image URL set from cursor object.
         *  Image downloaded by Glide client if flag mIsLOad Images is set and imageURL is valid.
         *  In other case placeholder used.
         *
         *
         * @param position
         */
        private void fill(int position) {
            mCursor.moveToPosition(position);
            mText.setText(mCursor.getString(mCursor.getColumnIndex(COLUMN_RECIPE_NAME)));
            String imageURL = mCursor.getString(mCursor.getColumnIndex(COLUMN_RECIPE_IMAGE));
            if (imageURL != null && !imageURL.isEmpty()) {
                Glide.with(mContext)
                        .load(imageURL)
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.empty_loading)
                                .error(R.drawable.cakes_025))
                        .into(mImage);
            } else {
                mImage.setImageResource(R.drawable.cakes_025);
            }
        }
    }

}
