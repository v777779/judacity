package ru.vpcb.btplay.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 23-Oct-17
 * Email: vadim.v.voronov@gmail.com
 */


public class RecipeContract {
    public static final String AUTHORITY = "ru.vpcb.btplay";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_RECIPES = "recipes";

    public static final class RecipeEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_RECIPES).build();
        public static final String TABLE_NAME = "recipes";
        public static final String COLUMN_RECIPE_ID = "recipe_id";                // int
        public static final String COLUMN_RECIPE_NAME = "recipe_name";            // string
        public static final String COLUMN_RECIPE_LENGTH = "recipe_length";        // int
        public static final String COLUMN_RECIPE_IMAGE = "recipe_image";        // int
        public static final String COLUMN_RECIPE_VALUE = "recipe_value";          // string
    }

}
