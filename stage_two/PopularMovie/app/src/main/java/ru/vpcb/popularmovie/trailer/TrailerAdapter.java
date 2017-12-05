package ru.vpcb.popularmovie.trailer;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import ru.vpcb.popularmovie.R;

import static ru.vpcb.popularmovie.utils.Constants.COLUMN_WIDTH_HIGH;
import static ru.vpcb.popularmovie.utils.Constants.COLUMN_WIDTH_LOW;
import static ru.vpcb.popularmovie.utils.Constants.COLUMN_WIDTH_MIDDLE;
import static ru.vpcb.popularmovie.utils.Constants.DP_WIDTH_LANDSCAPE_HIGH;
import static ru.vpcb.popularmovie.utils.Constants.DP_WIDTH_LANDSCAPE_MDL;
import static ru.vpcb.popularmovie.utils.Constants.DP_WIDTH_PORTRAIT_HIGH;
import static ru.vpcb.popularmovie.utils.Constants.DP_WIDTH_PORTRAIT_MDL;
import static ru.vpcb.popularmovie.utils.Constants.FRAME_RATIO;
import static ru.vpcb.popularmovie.utils.Constants.MAX_COLUMNS;
import static ru.vpcb.popularmovie.utils.Constants.MIN_COLUMNS;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 29-Sep-17
 * Email: vadim.v.voronov@gmail.com
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {
    private final List<TrailerItem> trailerList;
    private final LayoutInflater mInflater;
    private final ITrailerListener<Integer> listener;

    public TrailerAdapter(Context context, List<TrailerItem> trailerList, ITrailerListener<Integer> listener) {
        this.trailerList = trailerList;
        mInflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {  // XML >> holder
        View view = mInflater.inflate(R.layout.trailer_item, parent, false);
        return new TrailerViewHolder(view);
    }


    @Override
    public void onBindViewHolder(TrailerViewHolder holder, final int position) {
        final TrailerItem trailerItem = trailerList.get(position);
        holder.fill(trailerItem);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(position);
            }
        });
    }



    @Override
    public int getItemCount() {
        return trailerList.size();
    }


    class TrailerViewHolder extends RecyclerView.ViewHolder {
        private final ImageView mIcon;

        TrailerViewHolder(View itemView) {
            super(itemView);
            mIcon = itemView.findViewById(R.id.list_item_icon);
        }

        void fill(TrailerItem trailerItem) {
            if (trailerItem == null) {
                mIcon.setImageResource(R.drawable.empty_loading);
            } else {
               Picasso.with(itemView.getContext())
                        .load(trailerItem.getPoster())
                        .placeholder(R.drawable.empty_loading)
                        .error(R.drawable.error_loading)
                        .into(mIcon);
            }
        }
    }



}
