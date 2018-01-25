package ru.vpcb.contentprovider.fdbase;

import android.net.Uri;
import android.provider.BaseColumns;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

import ru.vpcb.contentprovider.data.FDFixture;

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

    public static final String DATABASE_NAME = "footballDb.db";
    public static final int DATABASE_VERSION = 1;



    /**
     * Entry class for RecipeItem Database Content Provider
     */
    public static final class CmEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_COMPETITIONS).build();
        public static final String TABLE_NAME = TABLE_COMPETITIONS;
        public static final String COLUMN_COMPETITION_ID = "competition_id";            // int
        public static final String COLUMN_CAPTION = "competition_caption";              // string
        public static final String COLUMN_COMPETITION_LEAGUE = "competition_league";    // string
        public static final String COLUMN_COMPETITION_YEAR = "competition_year";        // string
        public static final String COLUMN_CURRENT_MATCHDAY = "current_matchday";        // int
        public static final String COLUMN_NUMBER_MATCHDAYS = "number_matchdays";        // int
        public static final String COLUMN_NUMBER_TEAMS = "number_teams";                // int
        public static final String COLUMN_NUMBER_GAMES = "number_games";                // int
        public static final String COLUMN_LAST_UPDATE = "last_update";                  // string from date


    }

    public static final class TmEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_TEAMS).build();
        public static final String TABLE_NAME = TABLE_TEAMS;
        public static final String COLUMN_TEAM_ID = "team_id";                          // int
        public static final String COLUMN_TEAM_NAME = "team_name";                      // string
        public static final String COLUMN_TEAM_CODE = "team_code";                      // string
        public static final String COLUMN_TEAM_SHORT_NAME = "team_short_name";          // string
        public static final String COLUMN_TEAM_MARKET_VALUE = "team_market_value";      // string
        public static final String COLUMN_TEAM_CREST_URI = "team_crest_url";            // string

    }


    public static final class FxEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_FIXTURES).build();
        public static final String TABLE_NAME = TABLE_FIXTURES;
        public static final String COLUMN_FIXTURE_ID = "fixture_id";                    // int
        public static final String COLUMN_FIXTURE_DATE = "fixture_date";                // string from date
        public static final String COLUMN_FIXTURE_DATE_LONG = "fixture_date_long";      // int from date
        public static final String COLUMN_FIXTURE_STATUS = "fixture_status";            // string
        public static final String COLUMN_FIXTURE_MATCHDAY = "fixture_matchday";        // int
        public static final String COLUMN_FIXTURE_TEAM_HOME = "fixture_team_home";      // string
        public static final String COLUMN_FIXTURE_TEAM_AWAY = "fixture_team_away";      // string
        public static final String COLUMN_FIXTURE_GOALS_HOME = "fixture_goals_home";    // int
        public static final String COLUMN_FIXTURE_GOALS_AWAY = "fixture_goals_away";    // int
        public static final String COLUMN_FIXTURE_ODDS_WIN = "fixture_odds_home_win";   // real
        public static final String COLUMN_FIXTURE_ODDS_DRAW = "fixture_odds_draw";      // real
        public static final String COLUMN_FIXTURE_ODDS_LOSE = "fixture_odds_away_win";  // real

    }

    public static final class TbEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_TABLES).build();
        public static final String TABLE_NAME = TABLE_TABLES;
        public static final String COLUMN_RECIPE_ID = "recipe_id";                // int
        public static final String COLUMN_RECIPE_NAME = "recipe_name";            // string
        public static final String COLUMN_RECIPE_LENGTH = "recipe_length";        // int
        public static final String COLUMN_RECIPE_IMAGE = "recipe_image";        // int
        public static final String COLUMN_RECIPE_VALUE = "recipe_value";          // string
    }

    public static final class PlEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_PLAYERS).build();
        public static final String TABLE_NAME =TABLE_PLAYERS;
        public static final String COLUMN_RECIPE_ID = "recipe_id";                // int
        public static final String COLUMN_RECIPE_NAME = "recipe_name";            // string
        public static final String COLUMN_RECIPE_LENGTH = "recipe_length";        // int
        public static final String COLUMN_RECIPE_IMAGE = "recipe_image";        // int
        public static final String COLUMN_RECIPE_VALUE = "recipe_value";          // string
    }



}
