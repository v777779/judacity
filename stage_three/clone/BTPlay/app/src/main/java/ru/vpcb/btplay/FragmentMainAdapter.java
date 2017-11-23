package ru.vpcb.btplay;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import static ru.vpcb.btplay.data.RecipeContract.RecipeEntry.COLUMN_RECIPE_NAME;
//import static ru.vpcb.btplay.utils.Constants.MAIN_IMAGE_IDS;

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

    public FragmentMainAdapter(Context context,IFragmentHelper helper) {
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
         mCursor.moveToPosition(position);


//        LayoutParams lp = holder.itemView.findViewById(R.id.fc_recycler_main_image).getLayoutParams();
//        lp.height = mHelper.getSpanHeight(); // set to display metrics

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
            mText.setText(mCursor.getString(mCursor.getColumnIndex(COLUMN_RECIPE_NAME)));
//            mImage.setImageResource(MAIN_IMAGE_IDS[position % MAIN_IMAGE_IDS.length]);
// test!!! add Picasso here
              mImage.setImageResource(R.drawable.cakes_025);

        }

    }


}
