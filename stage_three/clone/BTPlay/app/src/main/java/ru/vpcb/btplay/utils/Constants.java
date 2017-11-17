package ru.vpcb.btplay.utils;


import ru.vpcb.btplay.MainActivity;
import ru.vpcb.btplay.R;


/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 29-Sep-17
 * Email: vadim.v.voronov@gmail.com
 */

public class Constants {

    // database data
    public static final String DATABASE_NAME = "recipesDb.db";
    public static final int DATABASE_VERSION = 1;


// bundle saveInstance
//    public static final String BUNDLE_LOADER_URL_ID = "bundle_loader_url_id";
//    public static final String BUNDLE_LOADER_ID = "bundle_loader_id";
//    public static final String BUNDLE_LOADER_LIST_URI_ID = "bundle_loader_list_uri_id";
//    public static final String BUNDLE_LOADER_LIST_POPULAR_ID = "bundle_loader_list_popular_id";
//    public static final String BUNDLE_LOADER_LIST_TOPRATED_ID = "bundle_loader_list_toprated_id";
//    public static final String BUNDLE_LOADER_LIST_POSITION_ID = "bundle_loader_list_position_id";
//    public static final String BUNDLE_LOADER_STRING_ID = "bundle_loader_string_id";
//    public static final String BUNDLE_LOADER_RECIPE_ID = "bundle_loader_query_id";
//    public static final String BUNDLE_LOADER_PAGE_ID = "bundle_loader_page_id";
//    public static final String BUNDLE_LOADER_MOVIE_ID = "bundle_loader_movie_id";
//    public static final String BUNDLE_LOADER_MOVIE_ITEM_ID = "bundle_loader_movie_item_id";
//    public static final String BUNDLE_LOADER_LIST_REVIEW_ID = "bundle_loader_list_review_id";
//    public static final String BUNDLE_LOADER_LIST_TRAILER_ID = "bundle_loader_list_trailer_id";


    // MainActivity constants
    public static final String TAG = MainActivity.class.getSimpleName();


    // ChildActivity Constants
//    public static final String TAG_CHILD = ChildActivity.class.getSimpleName();
    public static final String SIGNATURE = "ru.vpcb.popularmovie";
    public static final int REVIEW_NUMBER_MAX = 25;
    public static final String INTENT_MOVIE_ITEM_ID = "intent_movie_item_id";

    // recycler data
    public static final int COLLAPSED_TYPE = 0;
    public static final int EXPANDED_TYPE = 1;
    public static final int CHILD_TYPE = 2;
    public static final int[] MAIN_IMAGE_IDS = new int[]{
            R.drawable.cakes_002,
            R.drawable.cakes_001,
            R.drawable.cakes_005,
            R.drawable.cakes_003,
            R.drawable.cakes_004
    };


    // network data
    public static final int LOADER_RECIPES_ID = 1210;
    public static final int LOADER_RECIPES_DB_ID = 1220;
    public static final String RECIPES_BASE = "https://go.udacity.com/android-baking-app-json";

    public static final String BUNDLE_LOADER_STRING_ID = "bundle_loader_string_id";
    public static final String BUNDLE_LOADER_RECIPE_ID = "bundle_loader_recipe_id";

    public static final int RECIPE_RESPONSE_ID = 0;
    public static final int RECIPE_EMPTY_ID = 1;


    // ParseData constants
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";

    public static final String KEY_INGREDIENTS = "ingredients";
    public static final String KEY_QUANTITY = "quantity";
    public static final String KEY_MEASURE = "measure";
    public static final String KEY_INGREDIENT = "ingredient";

    public static final String KEY_STEPS = "steps";
    public static final String KEY_SHORT_DESCRIPTION = "shortDescription";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_VIDEO_URL = "videoURL";
    public static final String KEY_THUMBNAIL_URL = "thumbnailURL";

    public static final String KEY_SERVINGS = "servings";
    public static final String KEY_IMAGE_URL = "image";


}
