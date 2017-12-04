package ru.vpcb.bakingapp.utils;


import android.view.View;


import ru.vpcb.bakingapp.MainActivity;


/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 29-Sep-17
 * Email: vadim.v.voronov@gmail.com
 */

/**
 * Constant class of constants for project classes
 */
public class Constants {

    // database data
    public static final String DATABASE_NAME = "recipesDb.db";
    public static final int DATABASE_VERSION = 2;
    public static final int OLD_DATABASE_VERSION = 1;

// main recycler view
    public static final int COLLAPSED_TYPE = 0;
    public static final int EXPANDED_TYPE = 1;


// network data
    public static final int LOADER_RECIPES_DB_ID = 1220;
    public static final String RECIPES_BASE = "https://go.udacity.com/";
    public static final String RECIPES_QUERY = "android-baking-app-json";

// screen width
    public static final int LOW_WIDTH_PORTRAIT = 320;          // dpi
    public static final int LOW_WIDTH_LANDSCAPE = 520;     // dpi
    public static final int LOW_SCALE_PORTRAIT = 300;      // dpi
    public static final int LOW_SCALE_LANDSCAPE = 300;     // dpi
    public static final double SCREEN_RATIO = 1.8;

    public static final int HIGH_WIDTH_PORTRAIT = 600;     // dpi  600
    public static final int HIGH_WIDTH_LANDSCAPE = 900;    // dpi  900
    public static final int HIGH_SCALE_PORTRAIT = 240;     // dpi
    public static final int HIGH_SCALE_LANDSCAPE = 250;    // dpi

    public static final int MAX_SPAN = 6;
    public static final int MIN_SPAN = 1;
    public static final int MIN_HEIGHT = 100;

    public static final int MIN_WIDTH_WIDE_SCREEN = 550;     // dpi  600



// fragment main data
    public static final String RECIPE_POSITION = "recipe_position";

    public static final String BUNDLE_PREVIOUS_CONNECTION = "bundle_previous_connection";
    public static final String BUNDLE_ERROR_CONNECTION = "bundle_error_connection";
    public static final String FRAGMENT_PLAYER_NAME = "fragment_player_name";
    public static final String FRAGMENT_ERROR_NAME = "fragment_error_name";
    public static final String FRAGMENT_ERROR_TAG = "fragment_error_tag";
    public static final int STEP_DEFAULT_POSITION = 1;
    public static final int MESSAGE_ERROR_ID = 1221;
    public static final int MESSAGE_PLAYER_ID = 1223;


// fragment detail data
    public static final String BUNDLE_DETAIL_EXPANDED = "bundle_detail_expanded";
    public static final String BUNDLE_DETAIL_POSITION = "bundle_detail_position";
    public static final String BUNDLE_DETAIL_INTENT = "bundle_detail_intent";
    public static final String BUNDLE_DETAIL_WIDGET_FILLED = "bundle_detail_widget_filled";


// fragment play data
    public static final String RECIPE_STEP_POSITION = "recipe_step_position";
    public static final String RECIPE_SCREEN_WIDE = "recipe_screen_wide";
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

// attention very important for navigation bar visible and bottom of player view
    public static final int SYSTEM_UI_SHOW_FLAGS = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION  // to compatibility with 5X and Pixel
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;

    public static final int PLAY_BUTTON_ANIMATION = 500;
    public static final int PLAY_CONTROL_SHOWTIME = 2500;


// widget data
    public static final String BUNDLE_PACKAGE = Constants.class.getPackage().toString();
    public static final String BUNDLE_WIDGET_INTENT = BUNDLE_PACKAGE+".bundle_widget_intent";
    public static final String WIDGET_SERVICE_FILL_ACTION = BUNDLE_PACKAGE+".widget_fill_action";
    public static final String WIDGET_SERVICE_UPDATE_ACTION = BUNDLE_PACKAGE+".widget_update_action";
    public static final String WIDGET_PREFERENCES = "ru.vpcb.bakingapp.widget.";
    public static final String WIDGET_WIDGET_ID = "widget_id";
    public static final String WIDGET_RECIPE_ID = "widget_recipe_position";
    public static final String WIDGET_RECIPE_NAME = "widget_recipe_name";


// test data
    public static final int TEST_SNACKBAR_TIMEOUT = 2500;
    public static final int TEST_EXPAND_TIMEOUT= 500;
    public static final int TEST_START_TIMEOUT= 500;
    public static final int TEST_LOAD_DATABASE_TRIALS= 10;

    public static final int TEST_RECIPE_0 = 0;
    public static final int TEST_RECIPE_1 = 1;
    public static final int TEST_RECIPE_2 = 2;
    public static final int TEST_RECIPE_3 = 3;

    public static final int TEST_STEP_0 = 0;
    public static final int TEST_STEP_1 = 1;
    public static final int TEST_STEP_2 = 2;
    public static final int TEST_STEP_3 = 3;
    public static final int TEST_STEP_4 = 4;
    public static final int TEST_STEP_5 = 5;
    public static final int TEST_STEP_7 = 7;
    public static final int TEST_STEP_9 = 9;
    public static final int TEST_STEP_12 = 12;
}
