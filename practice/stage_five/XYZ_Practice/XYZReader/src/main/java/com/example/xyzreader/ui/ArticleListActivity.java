package com.example.xyzreader.ui;

import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
//import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;
import com.example.xyzreader.data.UpdaterService;
import com.example.xyzreader.remote.VolleyQueueSingleton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import static com.example.xyzreader.remote.Config.ACTION_SWIPE_REFRESH;
import static com.example.xyzreader.remote.Config.ACTION_TIME_REFRESH;
import static com.example.xyzreader.remote.Config.CALLBACK_ACTIVITY;
import static com.example.xyzreader.remote.Config.CALLBACK_FRAGMENT;
import static com.example.xyzreader.remote.Config.CALLBACK_FRAGMENT_CLOSE;
import static com.example.xyzreader.remote.Config.CALLBACK_FRAGMENT_EXIT;
import static com.example.xyzreader.remote.Config.CALLBACK_FRAGMENT_RETRY;
import static com.example.xyzreader.remote.Config.FRAGMENT_ERROR_TAG;

/**
 * An activity representing a list of Articles. This activity has different presentations for
 * handset and tablet-size devices. On handsets, the activity presents a list of items, which when
 * touched, lead to a {@link ArticleDetailActivity} representing item details. On tablets, the
 * activity presents a grid of items as cards.
 */
public class ArticleListActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, ICallback {


    public static final String TAG = ArticleListActivity.class.toString();


    private Toolbar mToolbar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;


    private BroadcastReceiver mRefreshingReceiver;
    private boolean mIsRefreshing = false;

    private ProgressBar mProgressBar;
    private Context mContext;
    private boolean mIsSwipeRefresh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);
// error!!! set background, set style in XML
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        final View toolbarContainerView = findViewById(R.id.toolbar_container);

// correction!!!
        ImageView toolbarLogo = findViewById(R.id.toolbar_logo);
        int toolbarHeight = (int) (toolbarLogo.getLayoutParams().height * 0.75);
        toolbarLogo.getLayoutParams().height = toolbarHeight;
        toolbarLogo.getLayoutParams().width = (int) (toolbarHeight * 4);
        toolbarLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");


// error!!! set Listener
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mIsSwipeRefresh = true;
                refresh(ACTION_SWIPE_REFRESH);
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        ArticleAdapter adapter = new ArticleAdapter(this);
        adapter.setHasStableIds(true);
        mRecyclerView.setAdapter(adapter);
        int columnCount = getResources().getInteger(R.integer.list_column_count);
        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(sglm);



        getLoaderManager().initLoader(0, null, this);

        if (savedInstanceState == null) {
            refresh(ACTION_TIME_REFRESH);
        }

// broadcast receiver, receives  messages from service
// intent.boolean  true/false  on/off progressbar or swipe refresh
        mRefreshingReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent == null) return;
                String action = intent.getAction();

                if (action == null || action.isEmpty()) return;
                if (UpdaterService.BROADCAST_ACTION_STATE_CHANGE.equals(action)) {
                    mIsRefreshing = intent.getBooleanExtra(UpdaterService.EXTRA_REFRESHING, false);
                    updateRefreshingUI();
                }
// correction!!!
                if (UpdaterService.BROADCAST_ACTION_NO_NETWORK.equals(action)) {
                    boolean isCursorEmpty = intent.getBooleanExtra(UpdaterService.EXTRA_EMPTY_CURSOR, false);
                    showErrorDialog(isCursorEmpty);

                }
            }
        };

// progress bar
        mProgressBar = findViewById(R.id.progress_bar);

// volley singleton
        VolleyQueueSingleton.getInstance(this);

    }

    private void showErrorDialog(boolean isCursorEmpty) {
        FragmentError fragmentError = FragmentError.newInstance(isCursorEmpty);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(fragmentError, FRAGMENT_ERROR_TAG);
        ft.commit();
    }

    private void refresh(String action) {
        startService(new Intent(action, null, this, UpdaterService.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UpdaterService.BROADCAST_ACTION_STATE_CHANGE);
        intentFilter.addAction(UpdaterService.BROADCAST_ACTION_NO_NETWORK);
        registerReceiver(mRefreshingReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mRefreshingReceiver);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VolleyQueueSingleton.getInstance().getRequestQueue().cancelAll(TAG);
    }


    private void hideRefreshingUI() {
        mIsSwipeRefresh = false;
        mIsRefreshing = false;
        mSwipeRefreshLayout.setRefreshing(mIsRefreshing);
        mProgressBar.setVisibility(mIsRefreshing ? View.VISIBLE : View.INVISIBLE);
    }

    private void updateRefreshingUI() {
        if (mIsSwipeRefresh) {
            mSwipeRefreshLayout.setRefreshing(mIsRefreshing);
            mIsSwipeRefresh = mIsRefreshing;        // switch mIsRefresh at second broadcastReceive
        } else {
            mProgressBar.setVisibility(mIsRefreshing ? View.VISIBLE : View.INVISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newAllArticlesInstance(this);
    }
// correction !!!
    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        ((ArticleAdapter)mRecyclerView.getAdapter()).setCursor(cursor);

    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }


    // correction!!! frameError support
    @Override
    public void onCallback(Uri uri) {
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    // correction!!! frameError support
    public void onCallback(int mode) {
        switch (mode) {
            case CALLBACK_FRAGMENT_RETRY:
                refresh(mIsSwipeRefresh ? ACTION_SWIPE_REFRESH : ACTION_TIME_REFRESH);
                break;
            case CALLBACK_FRAGMENT_CLOSE:
                hideRefreshingUI();
                break;
            case CALLBACK_FRAGMENT_EXIT:
                finish();
                break;
            default:
        }

    }

//    private class Adapter extends RecyclerView.Adapter<ViewHolder> {
//        private Cursor mCursor;
//
//        private SimpleDateFormat dateFormat;
//        // Use default locale format
//        private SimpleDateFormat outputFormat;
//        // Most time functions can only handle 1902 - 2037
//        private GregorianCalendar startOfEpoch;
//
//
//        public Adapter(Cursor cursor) {
//            mCursor = cursor;
//
//            dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
//            outputFormat = new SimpleDateFormat();
//            startOfEpoch = new GregorianCalendar(2, 1, 1);
//
//        }
//
//        @Override
//        public long getItemId(int position) {
//            mCursor.moveToPosition(position);
//            return mCursor.getLong(ArticleLoader.Query._ID);
//        }
//
//        @Override
//        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            View view = getLayoutInflater().inflate(R.layout.list_item_article, parent, false);
//            final ViewHolder vh = new ViewHolder(view);
//            view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    startActivity(new Intent(Intent.ACTION_VIEW,
//                            ItemsContract.Items.buildItemUri(getItemId(vh.getAdapterPosition()))));
//                }
//            });
//            return vh;
//        }
//
//        private Date parsePublishedDate() {
//            try {
//                String date = mCursor.getString(ArticleLoader.Query.PUBLISHED_DATE);
//                return dateFormat.parse(date);
//            } catch (ParseException ex) {
//                Log.e(TAG, ex.getMessage());
//                Log.i(TAG, "passing today's date");
//                return new Date();
//            }
//        }
//
//        @Override
//        public void onBindViewHolder(ViewHolder holder, int position) {
//            mCursor.moveToPosition(position);
//            holder.titleView.setText(mCursor.getString(ArticleLoader.Query.TITLE));
//            Date publishedDate = parsePublishedDate();
//            if (!publishedDate.before(startOfEpoch.getTime())) {
//
//                holder.subtitleView.setText(Html.fromHtml(
//                        DateUtils.getRelativeTimeSpanString(
//                                publishedDate.getTime(),
//                                System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
//                                DateUtils.FORMAT_ABBREV_ALL).toString()
//                                + "<br/>" + " by "
//                                + mCursor.getString(ArticleLoader.Query.AUTHOR)));
//            } else {
//                holder.subtitleView.setText(Html.fromHtml(
//                        outputFormat.format(publishedDate)
//                                + "<br/>" + " by "
//                                + mCursor.getString(ArticleLoader.Query.AUTHOR)));
//            }
//            holder.thumbnailView.setImageUrl(
//                    mCursor.getString(ArticleLoader.Query.THUMB_URL),
//                    ImageLoaderHelper.getInstance(ArticleListActivity.this).getImageLoader());
//            holder.thumbnailView.setAspectRatio(mCursor.getFloat(ArticleLoader.Query.ASPECT_RATIO));
//        }
//
//        @Override
//        public int getItemCount() {
//            return mCursor.getCount();
//        }
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        public DynamicHeightNetworkImageView thumbnailView;
//        public TextView titleView;
//        public TextView subtitleView;
//
//        public ViewHolder(View view) {
//            super(view);
//            thumbnailView = (DynamicHeightNetworkImageView) view.findViewById(R.id.thumbnail);
//            titleView = (TextView) view.findViewById(R.id.article_title);
//            subtitleView = (TextView) view.findViewById(R.id.article_subtitle);
//        }
//    }

}
