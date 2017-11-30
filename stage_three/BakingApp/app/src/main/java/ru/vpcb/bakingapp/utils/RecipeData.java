package ru.vpcb.bakingapp.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ru.vpcb.bakingapp.data.LoaderDb;
import ru.vpcb.bakingapp.data.RecipeContract.RecipeEntry;
import ru.vpcb.bakingapp.data.RecipeItem;
import timber.log.Timber;

import static ru.vpcb.bakingapp.data.RecipeContract.RecipeEntry.COLUMN_RECIPE_ID;
import static ru.vpcb.bakingapp.data.RecipeContract.RecipeEntry.COLUMN_RECIPE_IMAGE;
import static ru.vpcb.bakingapp.data.RecipeContract.RecipeEntry.COLUMN_RECIPE_LENGTH;
import static ru.vpcb.bakingapp.data.RecipeContract.RecipeEntry.COLUMN_RECIPE_NAME;
import static ru.vpcb.bakingapp.data.RecipeContract.RecipeEntry.COLUMN_RECIPE_VALUE;
import static ru.vpcb.bakingapp.data.RecipeContract.RecipeEntry.CONTENT_URI;
import static ru.vpcb.bakingapp.utils.Constants.LOADER_RECIPES_DB_ID;


/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 23-Oct-17
 * Email: vadim.v.voronov@gmail.com
 */
public class RecipeData {
    public static String queryRecord(Context context, int recipeId) throws Exception {

        Uri uri = RecipeEntry.CONTENT_URI;           // it's already uri
        uri = uri.buildUpon().appendPath(Integer.toString(recipeId)).build();

        Cursor cursor = context.getContentResolver().query(
                uri,
                null,
                COLUMN_RECIPE_ID + "=?",
                new String[]{Integer.toString(recipeId)},
                null,
                null
        );

        if (cursor == null) return null;
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_VALUE));
    }

    public static int deleteRecord(Context context, int recipeId) throws Exception {

        String sRecipeId = Integer.toString(recipeId);
        Uri uri = RecipeEntry.CONTENT_URI;           // it's already uri
        uri = uri.buildUpon().appendPath(sRecipeId).build();

       return context.getContentResolver().delete(
                uri,
                COLUMN_RECIPE_ID + "=?",
                new String[]{sRecipeId
                }
        );

    }

    public static String queryRecord(ContentResolver contentResolver, int recipeId) {
        try {
            Uri uri = RecipeEntry.CONTENT_URI;           // it's already uri
            uri = uri.buildUpon().appendPath(Integer.toString(recipeId)).build();

            Cursor cursor = contentResolver.query(
                    uri,
                    null,
                    COLUMN_RECIPE_ID + "=?",
                    new String[]{Integer.toString(recipeId)},
                    null,
                    null
            );

            if (cursor == null) return null;
            cursor.moveToFirst();
            return cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_VALUE));
        } catch (Exception e) {
            Timber.d(e.getMessage());
        }
        return null;
    }


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
        Gson gson = new GsonBuilder()
//                    .setLenient()
//                    .setPrettyPrinting()
                .create();

        String jsonString = gson.toJson(recipeItem);
        if (jsonString == null || jsonString.isEmpty() || jsonString.equals("null")) return null;

        contentValues.put(COLUMN_RECIPE_ID, recipeItem.getId());                        // int
        contentValues.put(COLUMN_RECIPE_NAME, recipeItem.getName());                    // string
        contentValues.put(COLUMN_RECIPE_LENGTH, jsonString.length());                   // double
        contentValues.put(COLUMN_RECIPE_IMAGE, recipeItem.getImage());      // string
        contentValues.put(COLUMN_RECIPE_VALUE, jsonString);                             // string

        Uri returnUri = contentResolver.insert(CONTENT_URI, contentValues);

        if (returnUri != null) {
            loaderManager.restartLoader(LOADER_RECIPES_DB_ID, null, mLoaderDb);
        }

        return returnUri;
    }


    public static int bulkInsert(ContentResolver contentResolver, LoaderManager loaderManager,
                                 List<RecipeItem> list, LoaderDb mLoaderDb) {

        if (list == null || list.isEmpty()) return 0;

//        ContentValues[] contentValues = new ContentValues[list.size()];  // n records
        List<ContentValues> listContent = new ArrayList<>();
        Gson gson = new GsonBuilder()
//                    .setLenient()
//                    .setPrettyPrinting()
                .create();


        for (int i = 0; i < list.size(); i++) {
            ContentValues dest = new ContentValues();
            RecipeItem src = list.get(i);
            String jsonString = gson.toJson(src);
            if (jsonString == null || jsonString.isEmpty() || jsonString.equals("null")) continue;

            dest.put(COLUMN_RECIPE_ID, src.getId());                // int
            dest.put(COLUMN_RECIPE_NAME, src.getName());            // string
            dest.put(COLUMN_RECIPE_LENGTH, jsonString.length());    // string
            dest.put(COLUMN_RECIPE_IMAGE, src.getImage());          // string
            dest.put(COLUMN_RECIPE_VALUE, jsonString);              // string
            listContent.add(dest);
        }

        ContentValues[] contentValues = listContent.toArray(new ContentValues[listContent.size()]);
        int nInserted = contentResolver.bulkInsert(CONTENT_URI, contentValues);
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
        Gson gson = new GsonBuilder()
//                    .setLenient()
//                    .setPrettyPrinting()
                .create();

        String jsonString = gson.toJson(recipeItem);
        if (jsonString == null || jsonString.isEmpty() || jsonString.equals("null")) return 0;

        contentValues.put(COLUMN_RECIPE_NAME, recipeItem.getName());
        contentValues.put(COLUMN_RECIPE_LENGTH, jsonString.length());
        contentValues.put(COLUMN_RECIPE_IMAGE, recipeItem.getImage());
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

    public static int bulkInsertBackground(ContentResolver resolver, LoaderManager manager,
                                           List<RecipeItem> list, LoaderDb loader) {
        if (resolver == null || manager == null) return 0;

        BulkInsert bulkInsert = new BulkInsert(resolver, manager, list, loader);
        bulkInsert.execute();
        return bulkInsert.mResult;
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
