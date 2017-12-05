package ru.vpcb.popularmovie;


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

import static ru.vpcb.popularmovie.utils.Constants.FRAME_RATIO;
import static ru.vpcb.popularmovie.utils.Constants.TAG_MOVIE;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 29-Sep-17
 * Email: vadim.v.voronov@gmail.com
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private final List<MovieItem> movieList;
    private final LayoutInflater mInflater;
    private final int mSpan;
    private final MainActivity context;
    private int size;
    private final IClickListener<MovieItem> listener;

    /**
     * Constructor         standard
     *
     * @param context   parent context
     * @param movieList List<Movie> data source
     * @param span      number of items in row
     */
    MovieAdapter(Context context, List<MovieItem> movieList, int span, IClickListener<MovieItem> listener) {
        this.movieList = movieList;
        mInflater = LayoutInflater.from(context);
        mSpan = span;
        this.context = (MainActivity) context;
        size = this.movieList.size();
        this.listener = listener;
    }

    /**
     * Creates ViewHolder object
     * Sets width and height of ViewHolder according to FRAME_RATIO constant
     *
     * @param parent   parent View object
     * @param viewType parameters
     * @return created ViewHolder object
     */
    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {  // XML >> holder
//        Context context = parent.getContext();
//        LayoutInflater inflater = LayoutInflater.from(context);
        View view = mInflater.inflate(R.layout.recycle_item, parent, false);
        GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) view.getLayoutParams();
        lp.height = (int) (parent.getMeasuredWidth() / mSpan * FRAME_RATIO);
        view.setLayoutParams(lp);
        return new MovieViewHolder(view);
    }

    /**
     * Fills ViewHolder object and attaches onClick() method
     * Calls sendIntent() method if clicked
     *
     * @param holder   ViewHolder object to fill in
     * @param position number of position of ViewHolder object
     */
    @Override
    public void onBindViewHolder(MovieViewHolder holder, final int position) {
        final MovieItem movieItem = movieList.get(position);
        holder.fill(movieItem);
//        Log.v(TAG_MOVIE, " #" + position);        // распечатать

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Log.v(TAG_MOVIE, "clicked on: " + movieItem.getTitle() + " " + movieItem.getRating());
                listener.onItemClick(movieItem);
            }
        });
    }

    /**
     * Sets new size of ViewHolder
     *
     * @param size new size to set
     */
    void setSize(int size) {
        this.size = size;
    }

    @Override
    public int getItemCount() {
        return size;
    }


    class MovieViewHolder extends RecyclerView.ViewHolder {
        private final ImageView mIcon;
        private final TextView mRating;
        private final TextView mYear;

        /**
         * Constructor
         *
         * @param itemView parent View object
         */
        MovieViewHolder(View itemView) {
            super(itemView);
            mIcon = itemView.findViewById(R.id.list_item_icon);
            mRating = itemView.findViewById(R.id.movie_rating_text);
            mYear = itemView.findViewById(R.id.movie_year_text);
        }

        /**
         * Fills  View of item  with data extracted from data source
         * Uses Picasso asr image loader with placeHolders for load and error
         * Checks connection and uses error placeholder instead of Picasso
         *
         * @param movieItem parent View object
         */
        void fill(MovieItem movieItem) {
            if (movieItem == null) {
                mIcon.setImageResource(R.drawable.empty_loading);
                mRating.setText(R.string.pad_name);
                mYear.setText(R.string.pad_name);
            } else {
                if (context.isOnline()) {
                    Picasso.with(itemView.getContext())
                            .load(movieItem.getPosterLow())
                            .placeholder(R.drawable.empty_loading)
                            .error(R.drawable.error_loading)
                            .into(mIcon);
                } else {
                    mIcon.setImageResource(R.drawable.error_loading);
                }
                mRating.setText(movieItem.getRating());
                mYear.setText(movieItem.getReleaseYear());
            }
        }
    }
}
