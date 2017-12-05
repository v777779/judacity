package ru.vpcb.popularmovie;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.List;

import static ru.vpcb.popularmovie.utils.Constants.DP_WIDTH_HIGH;
import static ru.vpcb.popularmovie.utils.Constants.REVIEW_NUMBER_MAX;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 29-Sep-17
 * Email: vadim.v.voronov@gmail.com
 */

public class ChildActivity extends AppCompatActivity {

    private ScrollView mPosterScrollView;

    /**
     *  Builds ChildActivity Window
     *  Inflates activity_child layout
     *  Fills resource objects
     *  Extract MovieItem from Intent
     *  Loads BackDrop Poster with Picasso loader, with image size according to screen size
     *  Inflates review_layout and fill it with up to three review from MovieItem Object
     *
     * @param savedInstanceState    saved instance data not used
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child);
        TextView mMovieTitle = (TextView) findViewById(R.id.movie_title);
        TextView mMovieSynopsisText = (TextView) findViewById(R.id.movie_synopsis_text);
        ImageView mMoviePoster = (ImageView) findViewById(R.id.movie_poster);
        TextView mMovieReleaseDate = (TextView) findViewById(R.id.movie_date);
        TextView mMovieRating = (TextView) findViewById(R.id.movie_rating);

        LinearLayout mLinearLayout = (LinearLayout) findViewById(R.id.movie_review);
        LayoutInflater inflater = getLayoutInflater();

        Intent intent = getIntent();
        if (intent.hasExtra(MovieItem.class.getCanonicalName())) {
            MovieItem movieItem = intent.getParcelableExtra(MovieItem.class.getCanonicalName());

            if (movieItem != null) {
                mMovieTitle.setText(movieItem.getTitle());
                mMovieSynopsisText.setText(getString(R.string.ovr_name, movieItem.getOverview()));
                Picasso.with(this).load(movieItem.getBackDropSelected(isScreenHighRes()))
                        .error(R.drawable.error_loading)
                        .placeholder(R.drawable.empty_loading).into(mMoviePoster);

                String sReleaseDate = movieItem.getReleaseDateVerbose();
                mMovieReleaseDate.setText(getString(R.string.rel_name, sReleaseDate));
                mMovieRating.setText(getString(R.string.tmd_name, movieItem.getRating()));
                List<ReviewItem> listReview = movieItem.getListReview();

                for (int i = 0; i < REVIEW_NUMBER_MAX && i < listReview.size(); i++) {
                    ReviewItem reviewItem = listReview.get(i);

                    LinearLayout child_review = (LinearLayout) inflater.inflate(R.layout.review_layout, mLinearLayout, false);  // new object

                    TextView review_author = child_review.findViewById(R.id.review_author);
                    TextView review_url = child_review.findViewById(R.id.review_url);
                    TextView review_text = child_review.findViewById(R.id.review_text);
                    review_author.setText(reviewItem.getAuthor());
                    review_url.setText(reviewItem.getUrl());
                    review_text.setText(reviewItem.getContent());
                    mLinearLayout.addView(child_review);
                    mLinearLayout.setVisibility(View.VISIBLE);
                    if (listReview.size() == 1 && reviewItem.getContent().equals("no reviews")) {
                        review_author.setHeight(0);
                        review_url.setHeight(0);
                    }
                }
            }
        }
    }

    /**
     *  Checks the screen width in pixels and returns true for greater than 1024 pixel
     *
     * @return  true for 1024 pixels or more and false otherwise
     */
    private boolean isScreenHighRes() {
        DisplayMetrics dp = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dp);

        return dp.widthPixels >= DP_WIDTH_HIGH;
    }

}
