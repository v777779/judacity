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
public class RecipeUtils {


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

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static String clrText(Resources res, String s) {
        if (s == null || s.isEmpty()) return "";
        return s.replaceAll("[^\\x00-\\xBE]", "");  // clear from broken symbols

    }

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

    public static String getStepName(Resources res, RecipeItem.Step step) {
        if (step == null) return "";
        if (step.getId() == 0) {
            return res.getString(R.string.play_header_intro);
        }
        String s = res.getString(R.string.play_header_step, "" + step.getId());
        return clrText(res, s);
    }

    public static String getShortDescription(Resources res, RecipeItem.Step step) {
        if (step == null) return "";
        String s = step.getShortDescription();
        if (s == null) return "";
        return clrText(res, s);
    }

    public static String getDescription(Resources res, RecipeItem.Step step) {
        if (step == null) return "";
        String s = step.getDescription();
        if (s == null) return "";
        return clrText(res, s);
    }

    public static String getRecipeName(Resources res, RecipeItem recipeItem) {
        String s = recipeItem.getName();
        return clrText(res, s);
    }
}
