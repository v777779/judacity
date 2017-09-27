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

class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private static final String TAG = MovieAdapter.class.getSimpleName();
    private static final double FRAME_RATIO = 1.8;

    private List<MovieItem> movieList;
    private LayoutInflater mInflater;
    private int mSpan;
    private MainActivity context;
    private int size;


    MovieAdapter(Context context, List<MovieItem> movieList, int span) {
        this.movieList = movieList;

        mInflater = LayoutInflater.from(context);
        mSpan = span;
        this.context = (MainActivity) context;
        size = this.movieList.size();


    }


    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {  // XML >> holder
//        Context context = parent.getContext();
//        LayoutInflater inflater = LayoutInflater.from(context);
        View view = mInflater.inflate(R.layout.recycle_item, parent, false); // распаковать

        GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) view.getLayoutParams();
        lp.height = (int) (parent.getMeasuredWidth() / mSpan * FRAME_RATIO);
        view.setLayoutParams(lp);


        return new MovieViewHolder(view);      // создали holder
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, final int position) {
        //   context.load();
        final MovieItem movieItem = movieList.get(position);

        holder.fill(movieItem);
        Log.v(TAG, " #" + position);  // распечатать

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(TAG, "clicked on: " + movieItem.getTitle() + " " + movieItem.getRating());
                context.sendIntent(movieItem, false);
            }
        });
    }

    void setSize(int size) {
        this.size = size;
    }

    @Override
    public int getItemCount() {
        return size;
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {
        private ImageView mIcon;
        private TextView mRating;
        private TextView mYear;


        MovieViewHolder(View itemView) {
            super(itemView);

            mIcon = itemView.findViewById(R.id.list_item_icon);
            mRating = itemView.findViewById(R.id.movie_rating_text);
            mYear = itemView.findViewById(R.id.movie_year_text);
        }

        void fill(MovieItem movieItem) {

            if (movieItem == null) {
                mIcon.setImageResource(R.drawable.empty);
                mRating.setText("__");
                mYear.setText("__***___");
            } else {
                Picasso.with(itemView.getContext()).load(movieItem.getPosterLow()).into(mIcon);
                mRating.setText(movieItem.getRating());
                mYear.setText(movieItem.getReleaseYear());
            }
        }
    }
}
