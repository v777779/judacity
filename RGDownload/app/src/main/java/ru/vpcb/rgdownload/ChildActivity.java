package ru.vpcb.rgdownload;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created by V1 on 27-Sep-17.
 */

public class ChildActivity extends AppCompatActivity {
    private static final String TAG = ChildActivity.class.getSimpleName();
    private static final String SIGNATURE = "ru.vpcb.rgdownload";

    private TextView mMovieTitle;
    private TextView mMovieSynopsis;
    private TextView mMovieSynopsisText;
    private MovieItem movieItem;
    private ImageView mMoviePoster;

    private Target mTarget;

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
        mPosterScrollView = (ScrollView) findViewById(R.id.poster_scrollview);


        mTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                mMoviePoster.setImageBitmap(bitmap);
                mPosterScrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        mPosterScrollView.scrollTo(0, 50);
                    }
                });

            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };


        Intent intent = getIntent();
        if (intent.hasExtra(MovieItem.class.getCanonicalName())) {                        // check if it's our intent
            movieItem = intent.getParcelableExtra(MovieItem.class.getCanonicalName());         // check for null already implemented
            Log.v(TAG, "received parcelable movie");
            if (movieItem != null) {
                mMovieTitle.setText(movieItem.getTitle());
                mMovieSynopsisText.setText(movieItem.getId() + " " + movieItem.getOverview());
                Picasso.with(this).load(movieItem.getBackDropMid()).into(mMoviePoster);
           }

        }


    }

    @Override
    protected void onDestroy() {
        Picasso.with(this).cancelRequest(mTarget);
        super.onDestroy();
    }
}
