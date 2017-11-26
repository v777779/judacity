package ru.vpcb.bakingapp.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ru.vpcb.bakingapp.data.RecipeItem;
import ru.vpcb.bakingapp.data.RecipeContract.RecipeEntry;
import ru.vpcb.bakingapp.data.LoaderDb;

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

    //test!!!
    public static void addImages(List<RecipeItem> list) {

        String[] links = new String[]{
                "http://i.ndtvimg.com/i/2015-08/10-best-baking-recipes-2_625x350_81438697411.jpg",
                "http://www.fnstatic.co.uk/images/content/recipe/forest-fruit-cake.jpg",
                "http://www.bakewithstork.com/assets/Recipes/_resampled/croppedimage733456-Birthday-Cake-with-Cream-and-Fresh-Fruit.jpg",
                "http://www.countrycrock.com/Images/347/347-1131912-Baked_Goods_recipe_Landing_Hero.png"
        };
        String[] linkThumbs = new String[]{
                "http://www.rudyanddelilah.com/wp-content/uploads/2017/06/Grandma-Rudys-Frozen-Grape-Salad-Thumbnail-600x570.jpg",
                "https://www.biggerbolderbaking.com/wp-content/uploads/bb-plugin/cache/BBB84-Chocolate-Cake-Thumbnail-FINAL-landscape.jpeg",
                "http://cdn-image.myrecipes.com/sites/default/files/image/recipes/ck/04/10/orange-cake-ck-701058-x.jpg",
                "http://img.taste.com.au/GdH23_iI/w720-h480-cfill-q80/taste/2016/11/orange-almond-sour-cream-cake-2332-1.jpeg",
                "https://renditions-tastemade.akamaized.net/61426465-hasselback-baked-apple-lc/thumbnail-1920x1080-00001.png",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSZK7FntIsI7rJ_lHW3Gwa6CdKgteg7wpzSP4xtPCaIGxuuY0GX",
                "https://www.biggerbolderbaking.com/wp-content/uploads/2015/05/BBB66-Brownie-Layer-Cake-Thumbnail-v.1-1024x576.jpg",
                "https://www.biggerbolderbaking.com/wp-content/uploads/2014/09/BBB32-Homemade-Donuts-Thumbnail-newest-1024x576.jpg",
                "https://upload.wikimedia.org/wikipedia/commons/e/ec/Cinnamon_rolls%2C_ready_for_cutting_and_baking.jpg",
                "http://cdn.playbuzz.com/cdn/065503d0-c66b-41f7-8b5b-9dad0cfd018a/20330f15-4343-42dc-baea-bcb702896f49.jpg",
                "https://realfood.tesco.com/media/images/BramelyAppleTart_HERO-a3bdb18a-e8dc-46c4-84e8-f7505faf7fb2-0-472x310.jpg",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR1irZ8PRAUBDJy7eRSq3zNmWizICUCSP3vr5aiqP7P_SIySrfk",
                "https://i0.wp.com/bakingamoment.com/wp-content/uploads/2016/09/9901featured2.jpg?resize=720%2C720&ssl=1",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTOQvBvITmMUX4enzf1ENsHa12umUnYnAl04z2-CjFhzWAdXr0UNQ",
                "http://www.waitrose.com/content/dam/waitrose/recipes/images/m/WRWK081216_Martha-Collison_Pudding_Macarons.jpg/_jcr_content/renditions/cq5dam.thumbnail.400.400.png",
                "http://3o45wf6y35-flywheel.netdna-ssl.com/wp-content/uploads/2014/08/pumpkin-pie-mini-tarts-thumbnail-1024x687.jpg",
        };
        Random rnd = new Random();
        for (RecipeItem recipeItem : list) {
            if (recipeItem.getImage().isEmpty()) {
                recipeItem.setImage(links[rnd.nextInt(links.length)]);
            }
            List<RecipeItem.Step> steps = recipeItem.getSteps();
            for (RecipeItem.Step step : steps) {
                if (step.getThumbnailURL().isEmpty()) {
                    step.setThumbnailURL(linkThumbs[rnd.nextInt(linkThumbs.length)]);
                }
            }
        }
    }
// test!!!
}
