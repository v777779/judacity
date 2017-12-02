package ru.vpcb.bakingapp.data;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

import static ru.vpcb.bakingapp.data.RecipeContract.RecipeEntry.COLUMN_RECIPE_ID;


/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 23-Oct-17
 * Email: vadim.v.voronov@gmail.com
 */

/**
 * Loader of RecipeItem Database
 */
public class LoaderDb implements LoaderManager.LoaderCallbacks<Cursor> {
    private Context mContext;
    private ICallbackDb mCallback;
    private String mRecipeId;

    /**
     * Callback Interface for loader
     */
    public interface ICallbackDb {
        /**
         * Gets cursor data as parameter from loader after load finished
         * Used as callback method by user to work with loaded cursor data
         */
        void onComplete(Cursor data);

        /**
         * Resets cursor object
         */
        void onReset();
    }

    /**
     * Constructor of one RecipeItem
     *
     * @param context Context of calling activity
     * @param callback ICallbackDB interface object with callback method placed in user activity
     * @param recipeId String RecipeID used to load specific recipe from database
     */
    public LoaderDb(Context context, ICallbackDb callback, String recipeId) {
        mContext = context;
        mCallback = callback;
        mRecipeId = recipeId;
    }

    /**
     * Constructor of all RecipeItems
     *
     * @param context Context of calling activity
     * @param callback ICallbackDB interface object with callback method placed in user activity

     */
    public LoaderDb(Context context, ICallbackDb callback) {
        this(context, callback, "");
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
                    Uri uri = RecipeContract.RecipeEntry.CONTENT_URI;
                    if (mRecipeId != null && !mRecipeId.isEmpty()) {
                        uri = uri.buildUpon().appendPath(mRecipeId).build();
                        return mContext.getContentResolver().query(uri,
                                null,
                                COLUMN_RECIPE_ID + "=?",
                                new String[]{mRecipeId},
                                COLUMN_RECIPE_ID + " ASC"
                        ); // sorted by recipe ID ascending
                    } else
                        return mContext.getContentResolver().query(uri,
                                null,
                                null,
                                null,
                                COLUMN_RECIPE_ID + " ASC"); // sorted by recipe ID ascending

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(Cursor cursor) {
                mCached = cursor;
                super.deliverResult(cursor);

            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        mCallback.onComplete(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCallback.onReset();
    }

}
