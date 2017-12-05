package ru.vpcb.popularmovie;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;


import java.util.Random;


import ru.vpcb.popularmovie.data.MovieContract.MovieEntry;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 23-Oct-17
 * Email: vadim.v.voronov@gmail.com
 */

public class LoaderDb implements LoaderManager.LoaderCallbacks<Cursor> {
    private Context mContext;
    private ICallbackDb mCallback;

    public interface ICallbackDb {
        void onComplete(Cursor data);
        void onReset();
    }

    public LoaderDb(Context context, ICallbackDb callback) {
        mContext = context;
        mCallback = callback;
    }

    @Override
    public Loader<Cursor> onCreateLoader(final int id, final Bundle args) {
        return new AsyncTaskLoader<Cursor>(mContext) {
            private Cursor mCached;

            @Override
            protected void onStartLoading() {
                if (mCached != null) {
                    deliverResult(mCached);
                } else {
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {
                try {
                    return mContext.getContentResolver().query(MovieEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            MovieEntry.COLUMN_MOVIE_VOTE_AVG+" DESC");
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(Cursor data) {
                mCached = data;
                super.deliverResult(data);

            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

            mCallback.onComplete(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCallback.onReset();
    }

}
