package ru.vpcb.bakingapp.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ru.vpcb.bakingapp.R;
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

/**
 *  Utils class for common used static methods
 */
public class RecipeUtils {

    /**
     *  Inserts a number of RecipeItem objects into database
     *
     * @param contentResolver ContentResolver object
     * @param loaderManager   LoaderManager object
     * @param list List<RecipeItem>  list of RecipeItem objects to store into database
     * @param mLoaderDb LoaderDb loader that updates database after successful operation
     * @return int number of inserted records, 0 if records already in database
     */
    public static int bulkInsert(ContentResolver contentResolver, LoaderManager loaderManager,
                                 List<RecipeItem> list, LoaderDb mLoaderDb) {

        if (list == null || list.isEmpty()) return 0;
        List<ContentValues> listContent = new ArrayList<>();
        Gson gson = new GsonBuilder()
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

    /**
     *  Inserts a number of RecipeItem objects into database in background
     *  Uses BulkInsert object to insert records in background
     *
     * @param resolver ContentResolver object
     * @param manager   LoaderManager object
     * @param list List<RecipeItem>  list of RecipeItem objects to store into database
     * @param loader LoaderDb loader that updates database after successful operation
     * @return int number of inserted records, 0 if records already in database
     */
    public static int bulkInsertBackground(ContentResolver resolver, LoaderManager manager,
                                           List<RecipeItem> list, LoaderDb loader) {
        if (resolver == null || manager == null) return 0;

        BulkInsert bulkInsert = new BulkInsert(resolver, manager, list, loader);
        bulkInsert.execute();
        return bulkInsert.mResult;
    }

    /**
     * BulkInsert AsyncTask class used to store a number of RecipeItems objects to database
     *  in background mode
     *  Uses bulkInsert() method to store RecipeItems and BulkInsert AsyncTask class
     *
     */
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

    /**
     *  Returns status of connecton to network
     *
     * @param context Context of calling activity
     * @return boolean status of connection, true if connected, false if not
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /**
     * Removes  characters with ASCII codes greater than 0xBE from input string
     *
     * @param res Resources resources of activity
     * @param s String input string
     * @return String cleared string
     */
    public static String clrText(Resources res, String s) {
        if (s == null || s.isEmpty()) return "";
        return s.replaceAll("[^\\x00-\\xBE]", "");  // clear from broken symbols
    }

    /**
     * Returns string with all ingredients of RecipeItem
     * Clears output from the wrong characters
     *
     * @param res Resources resources of activity
     * @param list List<Recipe.INgredients> list of ingredients objects of RecipeItem
     * @return String with all ingredients
     */
    public static String getIngredientString(Resources res, List<RecipeItem.Ingredient> list) {
        if (list == null || list.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        int count = 1;
        for (RecipeItem.Ingredient ingredient : list) {
            String s = ingredient.toString().substring(0, 1).toUpperCase() + ingredient.toString().substring(1);
            sb.append(count + ". " + s + "\n");
            count++;
        }
        return clrText(res, sb.toString());
    }

    /**
     * Returns the name of Step object of RecipeItem
     * Clears output from the wrong characters
     *
     * @param res Resources resources of activity
     * @param step RecipeItem.Step object
     * @return String name of Step object
     */
    public static String getStepName(Resources res, RecipeItem.Step step) {
        if (step == null) return "";
        if (step.getId() == 0) {
            return res.getString(R.string.play_header_intro);
        }
        String s = res.getString(R.string.play_header_step, "" + step.getId());
        return clrText(res, s);
    }

    /**
     * Returns short description of Step object of RecipeItem
     * Clears output from the wrong characters
     *
     * @param res Resources resources of activity
     * @param step RecipeItem.Step object
     * @return String short description of Step object
     */
    public static String getShortDescription(Resources res, RecipeItem.Step step) {
        if (step == null) return "";
        String s = step.getShortDescription();
        if (s == null) return "";
        return clrText(res, s);
    }

    /**
     * Returns description of Step object of RecipeItem
     * Clears output from the wrong characters
     *
     * @param res Resources resources of activity
     * @param step RecipeItem.Step object
     * @return String description of Step object
     */
    public static String getDescription(Resources res, RecipeItem.Step step) {
        if (step == null) return "";
        String s = step.getDescription();
        if (s == null) return "";
        return clrText(res, s);
    }

    /**
     * Returns the name of RecipeItem
     * Clears output from the wrong characters
     *
     * @param res Resources resources of activity
     * @param recipeItem  RecipeItem object
     * @return String name of RecipeItem object
     */
    public static String getRecipeName(Resources res, RecipeItem recipeItem) {
        String s = recipeItem.getName();
        return clrText(res, s);
    }
}
