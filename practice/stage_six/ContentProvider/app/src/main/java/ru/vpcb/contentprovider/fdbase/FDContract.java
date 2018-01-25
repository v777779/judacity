package ru.vpcb.contentprovider.fdbase;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 25-Jan-18
 * Email: vadim.v.voronov@gmail.com
 */
public class FDContract {
    public static final String AUTHORITY = "ru.vpcb.footballassistant";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String TABLE_COMPETITIONS = "competitions";
    public static final String TABLE_TEAMS = "teams";
    public static final String TABLE_FIXTURES = "fixtures";
    public static final String TABLE_TABLES = "tables";
    public static final String TABLE_PLAYERS = "players";


    /**
     * Entry class for RecipeItem Database Content Provider
     */
    public static final class CompetitionEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_COMPETITIONS).build();
        public static final String TABLE_NAME = "recipes";
        public static final String COLUMN_RECIPE_ID = "recipe_id";                // int
        public static final String COLUMN_RECIPE_NAME = "recipe_name";            // string
        public static final String COLUMN_RECIPE_LENGTH = "recipe_length";        // int
        public static final String COLUMN_RECIPE_IMAGE = "recipe_image";        // int
        public static final String COLUMN_RECIPE_VALUE = "recipe_value";          // string
    }

    public static final class TeamEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_TEAMS).build();
        public static final String TABLE_NAME = "teams";
        public static final String COLUMN_RECIPE_ID = "recipe_id";                // int
        public static final String COLUMN_RECIPE_NAME = "recipe_name";            // string
        public static final String COLUMN_RECIPE_LENGTH = "recipe_length";        // int
        public static final String COLUMN_RECIPE_IMAGE = "recipe_image";        // int
        public static final String COLUMN_RECIPE_VALUE = "recipe_value";          // string
    }


    public static final class FixtureEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_FIXTURES).build();
        public static final String TABLE_NAME = "teams";
        public static final String COLUMN_RECIPE_ID = "recipe_id";                // int
        public static final String COLUMN_RECIPE_NAME = "recipe_name";            // string
        public static final String COLUMN_RECIPE_LENGTH = "recipe_length";        // int
        public static final String COLUMN_RECIPE_IMAGE = "recipe_image";        // int
        public static final String COLUMN_RECIPE_VALUE = "recipe_value";          // string
    }

    public static final class TableEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_TABLES).build();
        public static final String TABLE_NAME = "teams";
        public static final String COLUMN_RECIPE_ID = "recipe_id";                // int
        public static final String COLUMN_RECIPE_NAME = "recipe_name";            // string
        public static final String COLUMN_RECIPE_LENGTH = "recipe_length";        // int
        public static final String COLUMN_RECIPE_IMAGE = "recipe_image";        // int
        public static final String COLUMN_RECIPE_VALUE = "recipe_value";          // string
    }

    public static final class PlayerEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_PLAYERS).build();
        public static final String TABLE_NAME = "teams";
        public static final String COLUMN_RECIPE_ID = "recipe_id";                // int
        public static final String COLUMN_RECIPE_NAME = "recipe_name";            // string
        public static final String COLUMN_RECIPE_LENGTH = "recipe_length";        // int
        public static final String COLUMN_RECIPE_IMAGE = "recipe_image";        // int
        public static final String COLUMN_RECIPE_VALUE = "recipe_value";          // string
    }



}
