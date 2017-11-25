package ru.vpcb.bakingapp;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import static ru.vpcb.bakingapp.data.RecipeContract.RecipeEntry.COLUMN_RECIPE_IMAGE;
import static ru.vpcb.bakingapp.data.RecipeContract.RecipeEntry.COLUMN_RECIPE_NAME;


/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 15-Nov-17
 * Email: vadim.v.voronov@gmail.com
 */

public class FragmentMainAdapter extends RecyclerView.Adapter<FragmentMainAdapter.FCViewHolder> {

    private IFragmentHelper mHelper;
    private Cursor mCursor;
    private Context mContext;
    private List<RecipeItem> mList;


    public FragmentMainAdapter(Context context, IFragmentHelper helper) {
        mContext = context;
        mHelper = helper;
        mCursor = null;


    }

    @Override
    public FCViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.fragment_main_item, parent, false);
        return new FCViewHolder(itemView);
    }

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

    @Override
    public int getItemCount() {
        if (mCursor == null) return 0;
        return mCursor.getCount();
    }



    public Cursor swapCursor(Cursor cursor) {
        Cursor oldCursor = mCursor;
        mCursor = cursor;
        if (cursor != null) {
            notifyDataSetChanged();
        }
        return oldCursor;
    }



    class FCViewHolder extends RecyclerView.ViewHolder {
        private final TextView mText;
        private final ImageView mImage;


        public FCViewHolder(View itemView) {
            super(itemView);
            mText = itemView.findViewById(R.id.fc_recycler_text);
            mImage = itemView.findViewById(R.id.fc_recycler_image);
            mImage.getLayoutParams().height = mHelper.getSpanHeight();

        }

        private void fill(int position) {
            mCursor.moveToPosition(position);
            mText.setText(mCursor.getString(mCursor.getColumnIndex(COLUMN_RECIPE_NAME)));
            String imageURL =  mCursor.getString(mCursor.getColumnIndex(COLUMN_RECIPE_IMAGE));
            if (imageURL != null && !imageURL.isEmpty()) {
                Glide.with(mContext)
                        .load(imageURL)
                        .apply(new RequestOptions().error(R.drawable.cakes_025))
                        .into(mImage);
            } else {
                mImage.setImageResource(R.drawable.cakes_025);
            }
        }
    }

}
