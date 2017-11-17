package ru.vpcb.btplay.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;

import org.json.JSONObject;

import java.util.List;

import ru.vpcb.btplay.RecipeItem;
import ru.vpcb.btplay.data.RecipeContract.RecipeEntry;
import ru.vpcb.btplay.network.LoaderDb;

import static ru.vpcb.btplay.data.RecipeContract.RecipeEntry.COLUMN_RECIPE_ID;
import static ru.vpcb.btplay.data.RecipeContract.RecipeEntry.COLUMN_RECIPE_LENGTH;
import static ru.vpcb.btplay.data.RecipeContract.RecipeEntry.COLUMN_RECIPE_NAME;
import static ru.vpcb.btplay.data.RecipeContract.RecipeEntry.COLUMN_RECIPE_VALUE;
import static ru.vpcb.btplay.data.RecipeContract.RecipeEntry.CONTENT_URI;
import static ru.vpcb.btplay.utils.Constants.LOADER_RECIPES_DB_ID;


/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 23-Oct-17
 * Email: vadim.v.voronov@gmail.com
 */
public class RecipeData {

    private static int deleteRecord(ContentResolver contentResolver, LoaderManager loaderManager,
                                    RecipeItem recipeItem, LoaderDb mLoaderDb) {
        if (recipeItem == null) {
            return 0;
        }
        Uri uri = RecipeEntry.CONTENT_URI;           // it's already uri
        uri = uri.buildUpon().appendPath(Integer.toString(recipeItem.getId())).build();

        int nDelete = contentResolver.delete(
                uri,
                COLUMN_RECIPE_ID + "=?" + " AND " + COLUMN_RECIPE_NAME + "=?",
                new String[]{Integer.toString(recipeItem.getId()), recipeItem.getName()
                }
        );

        if (nDelete != 0) {
            loaderManager.restartLoader(LOADER_RECIPES_DB_ID, null, mLoaderDb);
        }
        return nDelete;
    }

    private static Uri insertRecord(ContentResolver contentResolver, LoaderManager loaderManager,
                                    RecipeItem recipeItem, LoaderDb mLoaderDb) {
        if (recipeItem == null) {
            return null;
        }
        ContentValues contentValues = new ContentValues();
        String jsonString = recipeItem.getSource();

        contentValues.put(COLUMN_RECIPE_ID, recipeItem.getId());                                // int
        contentValues.put(COLUMN_RECIPE_NAME, recipeItem.getName());       // string
        contentValues.put(COLUMN_RECIPE_LENGTH, jsonString.length());  // double
        contentValues.put(COLUMN_RECIPE_VALUE, jsonString);       // string

        Uri returnUri = contentResolver.insert(CONTENT_URI, contentValues);

        if (returnUri != null) {
            loaderManager.restartLoader(LOADER_RECIPES_DB_ID, null, mLoaderDb);
        }

        return returnUri;
    }


    public static int bulkInsert(ContentResolver contentResolver, LoaderManager loaderManager,
                                  List<RecipeItem> list, LoaderDb mLoaderDb) {

        if (list == null || list.isEmpty()) return 0;

        ContentValues[] contentValues = new ContentValues[list.size()];  // n records
        for (int i = 0; i < contentValues.length; i++) {
            ContentValues dest = new ContentValues();
            contentValues[i] = dest;
            RecipeItem src = list.get(i);
            if (src == null) continue;

            String jsonString = src.getSource().toString();

            dest.put(RecipeEntry.COLUMN_RECIPE_ID, src.getId());             // int
            dest.put(RecipeEntry.COLUMN_RECIPE_NAME, src.getName());            // string
            dest.put(RecipeEntry.COLUMN_RECIPE_LENGTH, jsonString.length());            // string
            dest.put(RecipeEntry.COLUMN_RECIPE_VALUE, jsonString);            // string
        }
        int nInserted = contentResolver.bulkInsert(RecipeEntry.CONTENT_URI, contentValues);
        if (nInserted > 0) {
            loaderManager.restartLoader(LOADER_RECIPES_DB_ID, null, mLoaderDb);
        }

        return nInserted;
    }


    private static int updateRecord(ContentResolver contentResolver, LoaderManager loaderManager,
                                    RecipeItem recipeItem, LoaderDb mLoaderDb) {
        if (recipeItem == null) {
            return 0;
        }
        Uri uri = CONTENT_URI;           // it's already uri
        uri = uri.buildUpon().appendPath(Integer.toString(recipeItem.getId())).build();
        ContentValues contentValues = new ContentValues();
        String jsonString = recipeItem.getSource();

        contentValues.put(COLUMN_RECIPE_NAME, recipeItem.getName());
        contentValues.put(COLUMN_RECIPE_LENGTH, jsonString.length());
        contentValues.put(COLUMN_RECIPE_VALUE, jsonString);


        int nUpdated = contentResolver.update(
                uri, contentValues,
                COLUMN_RECIPE_ID + "=?",
                new String[]{"" + recipeItem.getId()}
        );

        if (nUpdated != 0) {
            loaderManager.restartLoader(LOADER_RECIPES_DB_ID, null, mLoaderDb);
        }
        return nUpdated;
    }









    private static class BulkInsert extends AsyncTask<Void, Void, Integer> {
        private final ContentResolver mContentResolver;
        private final LoaderManager mLoaderManager;
        private final List<RecipeItem> list;
        private final LoaderDb mLoaderDb;
        private int mResult;


        public BulkInsert(ContentResolver resolver, LoaderManager manager, List<RecipeItem> list, LoaderDb loader) {
            this.mContentResolver = resolver;
            this.mLoaderManager = manager;
            this.list = list;
            this.mLoaderDb = loader;
            this.mResult = -1;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            if (list == null || list.isEmpty()) return 0;

            return bulkInsert(mContentResolver, mLoaderManager, list, mLoaderDb);
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);

            mResult = result;
        }

        public boolean isInserted() {
            return mResult > 0;
        }
    }


}
