package ru.vpcb.popularmovie;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import ru.vpcb.popularmovie.data.MovieUtils;
import ru.vpcb.popularmovie.pager.MovieItem;
import ru.vpcb.popularmovie.pager.ReviewItem;
import ru.vpcb.popularmovie.trailer.ITrailerListener;
import ru.vpcb.popularmovie.trailer.TrailerAdapter;
import ru.vpcb.popularmovie.trailer.TrailerItem;
import ru.vpcb.popularmovie.utils.NetworkUtils;
import ru.vpcb.popularmovie.utils.ParseUtils;

import static ru.vpcb.popularmovie.utils.Constants.*;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 23-Oct-17
 * Email: vadim.v.voronov@gmail.com
 */

public class ChildActivity extends AppCompatActivity
        implements LoaderUri.ICallbackUri, LoaderDb.ICallbackDb {

    private ProgressBar mProgressBar;
    private TextView mTextView;
    private FloatingActionButton mFab;
    private MovieItem mMovieItem;
    private ConcurrentLinkedQueue<Bundle> mBundleStack;
    private LoaderUri mLoader;
    private LoaderDb mLoaderDb;
    private List<ReviewItem> mReview;
    private List<TrailerItem> mTrailer;
    private RecyclerView mRecycler;
    private List<MovieItem> mFavorites;
    private Picasso mPicasso;
    private TextView mTrailerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbar.setTitle("");


        mTextView = (TextView) findViewById(R.id.error_message);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mFab = (FloatingActionButton) findViewById(R.id.fab);

        mMovieItem = null;
        mBundleStack = new ConcurrentLinkedQueue<>();
        mLoader = new LoaderUri(this, this);
        mReview = new ArrayList<>();
        mTrailer = new ArrayList<>();
        mRecycler = (RecyclerView) findViewById(R.id.trailer_recycle_view);
        mLoaderDb = new LoaderDb(this, this);
        mFavorites = new ArrayList<>();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setConnectTimeout(10, TimeUnit.SECONDS);
        okHttpClient.setReadTimeout(10, TimeUnit.SECONDS);
        OkHttpDownloader downloader = new OkHttpDownloader(okHttpClient);
        mPicasso = new Picasso.Builder(this).downloader(downloader).build();

        mTrailerText = (TextView) findViewById(R.id.trailer_text);

        showResult();

        if (savedInstanceState != null) {
            ArrayList<Bundle> list = savedInstanceState.getParcelableArrayList(BUNDLE_LOADER_LIST_URI_ID);
            mBundleStack.addAll(list);
            mMovieItem = savedInstanceState.getParcelable(BUNDLE_LOADER_MOVIE_ITEM_ID);
            mReview = savedInstanceState.getParcelableArrayList(BUNDLE_LOADER_LIST_REVIEW_ID);
            mTrailer = savedInstanceState.getParcelableArrayList(BUNDLE_LOADER_LIST_TRAILER_ID);
        } else {
            Intent intent = getIntent();
            if (intent.hasExtra(INTENT_MOVIE_ITEM_ID)) {
                mMovieItem = intent.getParcelableExtra(INTENT_MOVIE_ITEM_ID);
                if (mMovieItem != null) {
                    NetworkUtils.putLoaderQuery(mBundleStack, REVIEW_ID, 1, mMovieItem.getMovieId());
                    NetworkUtils.putLoaderQuery(mBundleStack, TRAILER_ID, 0, mMovieItem.getMovieId());
                }
            }
        }
        setupMovieDetails();
        setupReviews();
        setupTrailerRecycler();
        setupFab();


        getSupportLoaderManager().initLoader(LOADER_CONSTANT_ID, mBundleStack.peek(), mLoader);
        getSupportLoaderManager().initLoader(LOADER_MOVIE_DB_ID, null, mLoaderDb);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_child, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        if (item.getItemId() == R.id.action_share) {
            if (mTrailer == null || mTrailer.size() == 0) {
                return false;
            }
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, mMovieItem.getTitle());
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, mTrailer.get(0).getWebLink());
            startActivity(sharingIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(BUNDLE_LOADER_LIST_URI_ID, new ArrayList<Parcelable>(mBundleStack));
        outState.putParcelable(BUNDLE_LOADER_MOVIE_ITEM_ID, mMovieItem);
        outState.putParcelableArrayList(BUNDLE_LOADER_LIST_REVIEW_ID, new ArrayList<Parcelable>(mReview));
        outState.putParcelableArrayList(BUNDLE_LOADER_LIST_TRAILER_ID, new ArrayList<Parcelable>(mTrailer));
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(LOADER_CONSTANT_ID, mBundleStack.peek(), mLoader);
        getSupportLoaderManager().restartLoader(LOADER_MOVIE_DB_ID, null, mLoaderDb);
    }

    private void setFabIcon() {
        if (mMovieItem == null || !mMovieItem.isFavorite()) {
            mFab.setImageResource(R.drawable.ic_favorite_border_white_24dp);
        } else {
            mFab.setImageResource(R.drawable.ic_favorite_white_24dp);
        }
    }

    private void setupFab() {
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMovieItem == null) {
                    return;
                }
                String fabMessage = null;
                if (mMovieItem.isFavorite()) {
                    if (MovieUtils.deleteRecord(getContentResolver(),
                            getSupportLoaderManager(), mMovieItem, mLoaderDb) != 0) {
                        fabMessage = "Movie removed from favorites";
                        mMovieItem.setFavorite(false);
                    }
                } else {
                    mMovieItem.setFavorite(true);
                    if (MovieUtils.insertRecord(getContentResolver(),
                            getSupportLoaderManager(), mMovieItem, mLoaderDb)) {
                        fabMessage = "Movie added to favorites";
                        mMovieItem.setFavorite(true);
                    }
                }
                if (fabMessage != null) {
                    setFabIcon();
                    Snackbar.make(view, fabMessage, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
        mFab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorAccentFab)));
    }

    private void setupTrailerRecycler() {
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecycler.setLayoutManager(mLayoutManager);
        mRecycler.setHasFixedSize(true);
        TrailerAdapter trailerAdapter = new TrailerAdapter(this, mTrailer,
                new ITrailerListener<Integer>() {
                    @Override
                    public void onItemClick(Integer position) {
                        if (mTrailer == null || position < 0 || position >= mTrailer.size()) {
                            return;
                        }
                        startYouTubeIntent(mTrailer.get(position));
                    }
                });
        mRecycler.setAdapter(trailerAdapter);
        mTrailerText.setText("no trailers");
        mTrailerText.setVisibility(View.VISIBLE);
        if (mTrailer.size() > 0) {
            mTrailerText.setHeight(0);
            mTrailerText.setVisibility(View.INVISIBLE);
        }

    }

    private boolean isScreenHighRes() {
        DisplayMetrics dp = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dp);
        return dp.widthPixels >= DP_WIDTH_PIXELS_HIGH;
    }

    private void setupMovieDetails() {
        if (mMovieItem == null) {
            return;
        }
        TextView movieTitle = (TextView) findViewById(R.id.movie_title);
        TextView movieSynopsisText = (TextView) findViewById(R.id.movie_synopsis_text);
        ImageView moviePoster = (ImageView) findViewById(R.id.movie_poster);
        TextView movieReleaseDate = (TextView) findViewById(R.id.movie_date);
        TextView movieRating = (TextView) findViewById(R.id.movie_rating);

        movieTitle.setText(mMovieItem.getTitle());
        movieSynopsisText.setText(getString(R.string.ovr_name, mMovieItem.getOverview()));
        mPicasso.with(this).load(mMovieItem.getBackDropSelected(isScreenHighRes()))
                .error(R.drawable.not_available)
                .placeholder(R.drawable.empty_loading).into(moviePoster);

        String sReleaseDate = mMovieItem.getReleaseDateVerbose();
        movieReleaseDate.setText(getString(R.string.rel_name, sReleaseDate));
        movieRating.setText(getString(R.string.tmd_name, mMovieItem.getRating()));
    }

    private void setupReviews() {
        LinearLayout mLinearLayout = (LinearLayout) findViewById(R.id.movie_review);
        LayoutInflater inflater = getLayoutInflater();
        LinearLayout childReview = null;
        if (mReview == null || mReview.size() == 0) {
            return;
        }
        for (int i = 0; i < mReview.size(); i++) {
            ReviewItem reviewItem = mReview.get(i);
            childReview = (LinearLayout) inflater.inflate(R.layout.review_item, mLinearLayout, false);  // new object
            TextView reviewAuthor = childReview.findViewById(R.id.review_author);
            TextView reviewUrl = childReview.findViewById(R.id.review_url);
            TextView reviewText = childReview.findViewById(R.id.review_text);
            reviewAuthor.setText(reviewItem.getAuthor());
            reviewUrl.setText(reviewItem.getUrl());
            reviewText.setText(reviewItem.getContent());
            mLinearLayout.addView(childReview);
        }
        if (mReview.size() == 1 && mReview.get(0).getContent().equals("no reviews")) {
            ((TextView) childReview.findViewById(R.id.review_author)).setHeight(0);
            ((TextView) childReview.findViewById(R.id.review_url)).setHeight(0);
        }
        mLinearLayout.setVisibility(View.VISIBLE);
    }


    private void showResult() {
        mTextView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showProgress() {
        mTextView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void showError() {
        mTextView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    private void updateReviewList(List<ReviewItem> reviewList, String s) {
        List<ReviewItem> list = null;
        list = ParseUtils.getReviewList(s);
        if (list != null && list.size() > 0) {
            reviewList.addAll(list);
        }
    }

    private void updateTrailerList(List<TrailerItem> trailerList, String s) {
        List<TrailerItem> list = null;
        list = ParseUtils.getTrailerList(s);
        if (list != null && list.size() > 0) {
            trailerList.addAll(list);
        }
    }

    @Override
    public void onComplete(Bundle data) {
        showResult();
        int id = data.getInt(BUNDLE_LOADER_QUERY_ID);
        String s = data.getString(BUNDLE_LOADER_STRING_ID);

        switch (id) {
            case REVIEW_ID:
                updateReviewList(mReview, s);
                setupReviews();
                break;
            case TRAILER_ID:
                updateTrailerList(mTrailer, s);
                if (mRecycler != null) {
                    RecyclerView.Adapter mAdapter = mRecycler.getAdapter();
                    mAdapter.notifyDataSetChanged();

                } else {
                    showError();
                }
                if (mTrailer == null || mTrailer.isEmpty()) {
                    mTrailerText.setVisibility(View.VISIBLE);
                } else {
                    mTrailerText.setVisibility(View.INVISIBLE);
                    mTrailerText.setHeight(0);
                }
                break;
            default:
        }
        mBundleStack.poll();
        if (mBundleStack.isEmpty()) {
            getSupportLoaderManager().destroyLoader(LOADER_CONSTANT_ID);
        } else {
            getSupportLoaderManager().restartLoader(LOADER_CONSTANT_ID, mBundleStack.peek(), mLoader);
        }
    }

    @Override
    public void onComplete(Cursor cursor) {
        if (cursor == null) {
            return;
        }
        mFavorites.clear();
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            MovieItem movieItem = new MovieItem(cursor);
            mFavorites.add(movieItem);
        }
        if (mMovieItem == null || mFavorites == null || mFavorites.isEmpty()) {
            return;
        }
        Collections.sort(mFavorites);
        if (Collections.binarySearch(mFavorites, mMovieItem) >= 0) {
            mMovieItem.setFavorite(true);
        } else {
            mMovieItem.setFavorite(false);
        }
        setFabIcon();
    }

    @Override
    public void onReset() {

    }

    private void startYouTubeIntent(final TrailerItem trailerItem) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(trailerItem.getAppLink()));
        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(trailerItem.getWebLink()));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }

    }

}
