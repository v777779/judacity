package ru.vpcb.popularmovie.pager;


import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.concurrent.TimeUnit;


import ru.vpcb.popularmovie.MainActivity;
import ru.vpcb.popularmovie.R;

import static ru.vpcb.popularmovie.utils.Constants.FRAME_RATIO;

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
    private final IMovieListener<MovieItem> listener;
    private final Picasso mPicasso;

    public MovieAdapter(Context context, List<MovieItem> movieList, int span, IMovieListener<MovieItem> listener) {
        this.movieList = movieList;
        mInflater = LayoutInflater.from(context);
        mSpan = span;
        this.context = (MainActivity) context;
        size = this.movieList.size();
        this.listener = listener;


        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setConnectTimeout(10,TimeUnit.SECONDS);
        okHttpClient.setReadTimeout(10,TimeUnit.SECONDS);
        OkHttpDownloader downloader = new OkHttpDownloader(okHttpClient);
        mPicasso = new Picasso.Builder(context).downloader(downloader).build();
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {  // XML >> holder
        View view = mInflater.inflate(R.layout.recycle_item, parent, false);
        GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) view.getLayoutParams();
        lp.height = (int) (parent.getMeasuredWidth() / mSpan * FRAME_RATIO);
        view.setLayoutParams(lp);
        return new MovieViewHolder(view);
    }


    @Override
    public void onBindViewHolder(MovieViewHolder holder, final int position) {
        final MovieItem movieItem = movieList.get(position);
        holder.fill(movieItem);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(movieItem);
            }
        });
    }

    void setSize(int size) {
        this.size = size;
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }


    class MovieViewHolder extends RecyclerView.ViewHolder {
        private final ImageView mIcon;
        private final TextView mRating;
        private final TextView mYear;
        private final ImageView mFavoriteStar;
        private final RelativeLayout mRelative;

        MovieViewHolder(View itemView) {
            super(itemView);
            mIcon = itemView.findViewById(R.id.list_item_icon);
            mRating = itemView.findViewById(R.id.movie_rating_text);
            mYear = itemView.findViewById(R.id.movie_year_text);
            mFavoriteStar = itemView.findViewById(R.id.movie_favorite_star);
            mRelative = itemView.findViewById(R.id.recycler_bar);
        }

        void fill(final MovieItem movieItem) {
            if (movieItem == null) {
                mIcon.setImageResource(R.drawable.empty_loading);
                mRating.setText(R.string.pad_name);
                mYear.setText(R.string.pad_name);
            } else {

                mPicasso.with(itemView.getContext())
                        .load(movieItem.getPosterMid())
                        .placeholder(R.drawable.empty_loading)
                        .error(R.drawable.error_loading)
                        .into(mIcon);

                mRating.setText(movieItem.getRating());
                mYear.setText(movieItem.getReleaseYear());
                if (movieItem.isFavorite()) {
                    mFavoriteStar.setImageResource(R.drawable.ic_star_white_24dp);
                }else {
                    mFavoriteStar.setImageResource(R.drawable.ic_star_border_white_24dp);
                }

                mRelative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        Toast.makeText(context, "Click on Bar", Toast.LENGTH_SHORT).show();
                        listener.onItemClickFavorites(movieItem);
                    }
                });
            }
        }
    }
}
