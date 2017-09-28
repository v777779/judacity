package ru.vpcb.rgdownload;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import ru.vpcb.rgdownload.utils.NetworkData;
import ru.vpcb.rgdownload.utils.NetworkUtils;
import ru.vpcb.rgdownload.utils.ParseUtils;
import ru.vpcb.rgdownload.utils.QueryType;

/**
 * Created by V1 on 27-Sep-17.
 */

public class ChildActivity extends AppCompatActivity {
    private static final String TAG = ChildActivity.class.getSimpleName();
    private static final String SIGNATURE = "ru.vpcb.rgdownload";
    private static final int REVIEW_NUMBER_MAX = 3;

    private TextView mMovieTitle;
    private TextView mMovieSynopsis;
    private TextView mMovieSynopsisText;
    private TextView mMovieReleaseDate;
    private TextView mMovieRating;
    private MovieItem movieItem;
    private ImageView mMoviePoster;
    private LinearLayout mLinearLayout;


    private ScrollView mPosterScrollView;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child);
        mMovieTitle = (TextView) findViewById(R.id.movie_title);
        mMovieSynopsis = (TextView) findViewById(R.id.movie_synopsis);
        mMovieSynopsisText = (TextView) findViewById(R.id.movie_synopsis_text);
        mMoviePoster = (ImageView) findViewById(R.id.movie_poster);
        mMovieReleaseDate = (TextView) findViewById(R.id.movie_date);
        mMovieRating = (TextView) findViewById(R.id.movie_rating);

        mLinearLayout = (LinearLayout)findViewById(R.id.movie_review);  // layout to add review
        LayoutInflater inflater = this.getLayoutInflater();

        Intent intent = getIntent();
        if (intent.hasExtra(MovieItem.class.getCanonicalName())) {                        // check if it's our intent
            movieItem = intent.getParcelableExtra(MovieItem.class.getCanonicalName());         // check for null already implemented

            if (movieItem != null) {
                mMovieTitle.setText(movieItem.getTitle());
                mMovieSynopsisText.setText("    "+ movieItem.getOverview());
                Picasso.with(this).load(movieItem.getBackDropHigh()).error(R.drawable.error_loading)
                        .placeholder(R.drawable.empty_loading).into(mMoviePoster);

                String sReleaseDate = movieItem.getReleaseDateVerbose();
                mMovieReleaseDate.setText("Release: " + sReleaseDate);
                mMovieRating.setText("TMDb: " + movieItem.getRating());


                List<ReviewItem> listReview = movieItem.getListReview();

                for (int i = 0; i < REVIEW_NUMBER_MAX && i < listReview.size(); i++) {
                    ReviewItem reviewItem = listReview.get(i);

                    LinearLayout child_review =(LinearLayout) inflater.inflate(R.layout.review_layout, null);  // new object

                    TextView review_author = child_review.findViewById(R.id.review_author);
                    TextView review_url = child_review.findViewById(R.id.review_url);
                    TextView review_text = child_review.findViewById(R.id.review_text);
                    review_author.setText(reviewItem.getAuthor());
                    review_url.setText(reviewItem.getUrl());
                    review_text.setText(reviewItem.getContent());
                    mLinearLayout.addView(child_review);
                    mLinearLayout.setVisibility(View.VISIBLE);
                    if(listReview.size() == 1 && reviewItem.getContent().equals("no review")) {
                        review_author.setHeight(0);
                        review_url.setHeight(0);
                    }



                }

             }

        }
    }

    private List<ReviewItem> loadReview(int page, int id) {
        String s = null;
        try {
            s = NetworkUtils.makeSearch(new NetworkData(QueryType.REVIEW, page, id));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (s == null) {
            return null;
        }
        return ParseUtils.getReviewList(s);
    }

}
