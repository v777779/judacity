package ru.vpcb.bakingapp.utils;


import android.view.View;

import ru.vpcb.bakingapp.FragmentDetail;
import ru.vpcb.bakingapp.FragmentMain;
import ru.vpcb.bakingapp.MainActivity;


/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 29-Sep-17
 * Email: vadim.v.voronov@gmail.com
 */

public class Constants {

    // database data
    public static final String DATABASE_NAME = "recipesDb.db";
    public static final int DATABASE_VERSION = 2;
    public static final int OLD_DATABASE_VERSION = 1;


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
//    public static final int[] MAIN_IMAGE_IDS = new int[]{
//            R.drawable.cakes_002,
//            R.drawable.cakes_001,
//            R.drawable.cakes_005,
//            R.drawable.cakes_003,
//            R.drawable.cakes_004
//    };


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

    // screen width
    public static final int LOW_WIDTH_PORTRAIT = 320;          // dpi
    public static final int LOW_WIDTH_LANDSCAPE = 520;     // dpi
    public static final int LOW_SCALE_PORTRAIT = 300;      // dpi
    public static final int LOW_SCALE_LANDSCAPE = 300;     // dpi
    public static final double SCREEN_RATIO = 1.8;


    public static final int HIGH_WIDTH_PORTRAIT = 600;     // dpi
    public static final int HIGH_WIDTH_LANDSCAPE = 900;    // dpi
    public static final int HIGH_SCALE_PORTRAIT = 240;     // dpi
    public static final int HIGH_SCALE_LANDSCAPE = 250;    // dpi

    public static final int MAX_SPAN = 6;
    public static final int MIN_SPAN = 1;
    public static final int MIN_HEIGHT = 100;


    // player animation
    public static final float BUTTON_DOWN_ALPHA = 0.5f;
    public static final float BUTTON_UP_ALPHA = 1.0f;
    public static final int BUTTON_DOWN_DELAY = 1250;
    public static final int BUTTON_UP_DELAY = 2550;

    // fragment main data
//    public static final String TAG_FMAIN = FragmentMain.class.getSimpleName();
    public static final String RECIPE_POSITION = "recipe_position";
    public static final int STEP_DEFAULT_POSITION = 1;

    // fragment detail data
//    public static final String TAG_FDETAIL = FragmentDetail.class.getSimpleName();
    public static final String DETAIL_IS_EXPANDED = "detail_is_expanded";
    public static final String ERROR_RECIPE_EMPTY ="Error RecipeItem object is null";
    public static final String BUNDLE_DETAIL_EXPANDED = "bundle_detail_expanded";
    public static final String BUNDLE_DETAIL_POSITION = "bundle_detail_position";



    // fragment play data
    public static final String RECIPE_STEP_POSITION = "recipe_step_position";
    public static final String RECIPE_SCREEN_WIDE = "recipe_screen_wide";

    public static final int PLAY_BUTTON_ANIMATION = 500;
    public static final int PLAY_CONTROL_SHOWTIME = 2500;

    public static final String BUNDLE_PLAY_WINDOW_INDEX = "bundle_play_window_index";
    public static final String BUNDLE_PLAY_SEEK_POSITION = "bundle_play_seek_position";
    public static final String BUNDLE_PLAY_PAUSE_READY = "bundle_play_pause_ready";
    public static final String BUNDLE_PLAY_BACK_ENDED = "bundle_play_back_ended";

    public static final int SYSTEM_UI_HIDE_FLAGS = View.SYSTEM_UI_FLAG_LOW_PROFILE
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

    public static final int SYSTEM_UI_SHOW_FLAGS = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;

}
