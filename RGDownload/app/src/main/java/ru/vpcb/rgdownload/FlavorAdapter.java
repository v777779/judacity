package ru.vpcb.rgdownload;


import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

class FlavorAdapter extends RecyclerView.Adapter<FlavorAdapter.FlavorViewHolder> {
    private static final double FRAME_RATIO = 1.8;
    private static final String TAG = FlavorAdapter.class.getSimpleName();
    private List<Flavor> mFlavorList;
    private LayoutInflater mInflater;
    private int mSpan;
    private MainActivity context;
    private int size;

    FlavorAdapter(Context context, List<Flavor> flavorList, int span) {
        mFlavorList = flavorList;

        mInflater = LayoutInflater.from(context);
        mSpan = span;
        this.context = (MainActivity) context;
        size = mFlavorList.size();

    }

    @Override
    public FlavorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {  // XML >> holder
//        Context context = parent.getContext();
//        LayoutInflater inflater = LayoutInflater.from(context);
        View view = mInflater.inflate(R.layout.recycle_item_flavor, parent, false); // распаковать

        GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) view.getLayoutParams();
        lp.height = (int) (parent.getMeasuredWidth() / mSpan * FRAME_RATIO);
        view.setLayoutParams(lp);


        return new FlavorViewHolder(view);      // создали holder
    }

    @Override
    public void onBindViewHolder(FlavorViewHolder holder, int position) {
        //   context.load();
        holder.fill(mFlavorList.get(position));
        Log.v(TAG, " #" + position);  // распечатать

    }

    void setSize(int size) {
        this.size = size;
    }

    @Override
    public int getItemCount() {
        return size;
    }


    class FlavorViewHolder extends RecyclerView.ViewHolder {
        private ImageView mIcon;
        private TextView mRating;
        private TextView mYear;


        FlavorViewHolder(View itemView) {
            super(itemView);

            mIcon = itemView.findViewById(R.id.list_item_icon);
            mRating = itemView.findViewById(R.id.movie_rating_text);
            mYear = itemView.findViewById(R.id.movie_year_text);
        }

        void fill(Flavor flavor) {

            if (flavor.getImagePath() == null) {
                mIcon.setImageResource(flavor.getImageId());
            } else {
                Picasso.with(itemView.getContext()).load(flavor.getImagePath()).into(mIcon);
            }

            mRating.setText(flavor.getRating());
            mYear.setText(flavor.getYear());

        }
    }
}
