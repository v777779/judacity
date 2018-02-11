package ru.vpcb.footballassistant.dbase;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 25-Jan-18
 * Email: vadim.v.voronov@gmail.com
 */
public class FDContract {
    public static final String CONTENT_AUTHORITY = "ru.vpcb.footballassistant";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String TABLE_COMPETITIONS = "competitions";
    public static final String TABLE_COMPETITION_TEAMS = "competition_teams";
    public static final String TABLE_COMPETITION_FIXTURES = "competition_fixtures";
    public static final String TABLE_TEAMS = "teams";
    public static final String TABLE_FIXTURES = "fixtures";
    public static final String TABLE_TABLES = "tables";
    public static final String TABLE_PLAYERS = "players";
    public static final String TABLE_NEWS = "news";

    public static final String DATABASE_NAME = "footballDb.db";
    public static final int DATABASE_VERSION = 1;


    /**
     * Entry class for RecipeItem Database Content Provider
     */


    public static final class CpEntry implements BaseColumns {
        public static final String TABLE_NAME = TABLE_COMPETITIONS;
        public static final int TABLE_MATCHER = 100;
        public static final int TABLE_ID_MATCHER = 101;
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();
        public static final String COLUMN_COMPETITION_ID = "competition_id";            // int
        public static final String COLUMN_COMPETITION_CAPTION = "competition_caption";  // string
        public static final String COLUMN_COMPETITION_LEAGUE = "competition_league";    // string
        public static final String COLUMN_COMPETITION_YEAR = "competition_year";        // string
        public static final String COLUMN_CURRENT_MATCHDAY = "current_matchday";        // int
        public static final String COLUMN_NUMBER_MATCHDAYS = "number_matchdays";        // int
        public static final String COLUMN_NUMBER_TEAMS = "number_teams";                // int
        public static final String COLUMN_NUMBER_GAMES = "number_games";                // int
        public static final String COLUMN_LAST_UPDATE = "last_update";                  // int from date
        public static final String COLUMN_LAST_REFRESH = "last_refresh";                // int from date

        public static final int LOADER_ID = 1220;                                       // int
    }

    public static final class CpTmEntry implements BaseColumns {
        public static final String TABLE_NAME = TABLE_COMPETITION_TEAMS;
        public static final int TABLE_MATCHER = 120;
        public static final int TABLE_ID_MATCHER = 121;
        public static final int TABLE_ID_MATCHER2 = 122;
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();
        public static final String COLUMN_COMPETITION_ID = "competition_id";            // int
        public static final String COLUMN_TEAM_ID = "team_id";                          // int
        public static final String COLUMN_LAST_REFRESH = "last_refresh";                // int from date

        public static final int LOADER_ID = 1221;                                       // int
    }

    public static final class CpFxEntry implements BaseColumns {
        public static final String TABLE_NAME = TABLE_COMPETITION_FIXTURES;
        public static final int TABLE_MATCHER = 140;
        public static final int TABLE_ID_MATCHER = 141;
        public static final int TABLE_ID_MATCHER2 = 142;
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();
        public static final String COLUMN_COMPETITION_ID = "competition_id";            // int
        public static final String COLUMN_FIXTURE_ID = "fixture_id";                    // int
        public static final String COLUMN_LAST_REFRESH = "last_refresh";                // int from date

        public static final int LOADER_ID = 1222;                                       // int
    }


    public static final class TmEntry implements BaseColumns {
        public static final String TABLE_NAME = TABLE_TEAMS;
        public static final int TABLE_MATCHER = 200;
        public static final int TABLE_ID_MATCHER = 201;
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();
        public static final String COLUMN_COMPETITION_ID = "competition_id";            // int
        public static final String COLUMN_TEAM_ID = "team_id";                          // int
        public static final String COLUMN_TEAM_NAME = "team_name";                      // string
        public static final String COLUMN_TEAM_CODE = "team_code";                      // string
        public static final String COLUMN_TEAM_SHORT_NAME = "team_short_name";          // string
        public static final String COLUMN_TEAM_MARKET_VALUE = "team_market_value";      // string
        public static final String COLUMN_TEAM_CREST_URI = "team_crest_url";            // string
        public static final String COLUMN_LAST_REFRESH = "last_refresh";                // int from date

        public static final int LOADER_ID = 1223;                                       // int
    }


    public static final class FxEntry implements BaseColumns {
        public static final String TABLE_NAME = TABLE_FIXTURES;
        public static final int TABLE_MATCHER = 300;
        public static final int TABLE_ID_MATCHER = 301;
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();
        public static final String COLUMN_COMPETITION_ID = "competition_id";            // int
        public static final String COLUMN_FIXTURE_ID = "fixture_id";                    // int
        public static final String COLUMN_TEAM_HOME_ID = "team_home_id";                // int
        public static final String COLUMN_TEAM_AWAY_ID = "team_away_id";                // int
        public static final String COLUMN_FIXTURE_DATE = "fixture_date";                // int from date
        public static final String COLUMN_FIXTURE_DATE_LONG = "fixture_date_long";      // int from date
        public static final String COLUMN_FIXTURE_STATUS = "fixture_status";            // string
        public static final String COLUMN_FIXTURE_MATCHDAY = "fixture_matchday";        // int
        public static final String COLUMN_FIXTURE_TEAM_HOME = "fixture_team_home";      // string
        public static final String COLUMN_FIXTURE_TEAM_AWAY = "fixture_team_away";      // string
        public static final String COLUMN_FIXTURE_GOALS_HOME = "fixture_goals_home";    // int
        public static final String COLUMN_FIXTURE_GOALS_AWAY = "fixture_goals_away";    // int
        public static final String COLUMN_FIXTURE_ODDS_WIN = "fixture_odds_home_win";   // real
        public static final String COLUMN_FIXTURE_ODDS_DRAW = "fixture_odds_draw";      // real
        public static final String COLUMN_FIXTURE_ODDS_AWAY = "fixture_odds_away_win";  // real
        public static final String COLUMN_LAST_REFRESH = "last_refresh";                // int from date

        public static final int LOADER_ID = 1224;                                       // int
    }

    public static final class TbEntry implements BaseColumns {
        public static final String TABLE_NAME = TABLE_TABLES;
        public static final int TABLE_MATCHER = 400;
        public static final int TABLE_ID_MATCHER = 401;
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();
        public static final String COLUMN_COMPETITION_ID = "competition_id";                // int
        public static final String COLUMN_TEAM_ID = "team_id";                              // int
        public static final String COLUMN_COMPETITION_MATCHDAY = "competition_matchday";    // int
        public static final String COLUMN_LEAGUE_CAPTION = "league_caption";                // string
        public static final String COLUMN_TEAM_POSITION = "team_position";                  // int
        public static final String COLUMN_TEAM_NAME = "team_name";                          // string
        public static final String COLUMN_CREST_URI = "crest_uri";                          // string
        public static final String COLUMN_TEAM_PLAYED_GAMES = "team_played_games";          // int
        public static final String COLUMN_TEAM_POINTS = "team_points";                      // int
        public static final String COLUMN_TEAM_GOALS = "team_goals";                        // int
        public static final String COLUMN_TEAM_GOALS_AGAINST = "team_goals_against";        // int
        public static final String COLUMN_TEAM_GOALS_DIFFERENCE = "team_goals_difference";  // int
        public static final String COLUMN_TEAM_WINS = "team_wins";                          // int
        public static final String COLUMN_TEAM_DRAWS = "team_draws";                        // int
        public static final String COLUMN_TEAM_LOSSES = "team_losses";                      // int
        public static final String COLUMN_LAST_REFRESH = "last_refresh";                    // int from date

        public static final int LOADER_ID = 1225;                                           // int
    }

    public static final class PlEntry implements BaseColumns {
        public static final String TABLE_NAME = TABLE_PLAYERS;
        public static final int TABLE_MATCHER = 500;
        public static final int TABLE_ID_MATCHER = 501;
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();
        public static final String COLUMN_PLAYER_ID = "player_id";                          // int
        public static final String COLUMN_TEAM_ID = "team_id";                              // int
        public static final String COLUMN_PLAYER_NAME = "player_name";                      // string
        public static final String COLUMN_PLAYER_POSITION = "player_position";              // string
        public static final String COLUMN_PLAYER_JERSEY_NUMBER = "player_jersey_number";    // int
        public static final String COLUMN_PLAYER_DATE_BIRTH = "player_date_birth";          // int from date
        public static final String COLUMN_PLAYER_NATIONALITY = "player_nationality";        // string
        public static final String COLUMN_PLAYER_DATE_CONTRACT = "player_date_contract";    // int from date
        public static final String COLUMN_PLAYER_MARKET_VALUE = "player_market_value";      // string
        public static final String COLUMN_LAST_REFRESH = "last_refresh";                    // int from date

        public static final int LOADER_ID = 1226;                                       // int
    }

    public static final class NewsEntry implements BaseColumns {
        public static final String TABLE_NAME = TABLE_NEWS;
        public static final int TABLE_MATCHER = 600;
        public static final int TABLE_ID_MATCHER = 601;
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();
        public static final String COLUMN_COMPETITION_ID = "competition_id";            // int
        public static final String COLUMN_COMPETITION_CAPTION = "competition_caption";  // string
        public static final String COLUMN_COMPETITION_LEAGUE = "competition_league";    // string
        public static final String COLUMN_COMPETITION_YEAR = "competition_year";        // string
        public static final String COLUMN_CURRENT_MATCHDAY = "current_matchday";        // int
        public static final String COLUMN_NUMBER_MATCHDAYS = "number_matchdays";        // int
        public static final String COLUMN_NUMBER_TEAMS = "number_teams";                // int
        public static final String COLUMN_NUMBER_GAMES = "number_games";                // int
        public static final String COLUMN_LAST_UPDATE = "last_update";                  // int from date
        public static final String COLUMN_LAST_REFRESH = "last_refresh";                // int from date

        public static final int LOADER_ID = 1220;                                       // int
    }

    public static final FDParams[] MATCH_PARAMETERS = new FDParams[]{
            new FDParams(CpEntry.LOADER_ID,
                    CpEntry.TABLE_NAME, CpEntry.TABLE_MATCHER,
                    CpEntry.TABLE_ID_MATCHER, CpEntry.COLUMN_COMPETITION_ID),
            new FDParams(CpTmEntry.LOADER_ID, CpTmEntry.TABLE_NAME, CpTmEntry.TABLE_MATCHER,
                    CpTmEntry.TABLE_ID_MATCHER, CpTmEntry.COLUMN_COMPETITION_ID,
                    CpTmEntry.TABLE_ID_MATCHER2, CpTmEntry.COLUMN_TEAM_ID
            ),
            new FDParams(CpFxEntry.LOADER_ID, CpFxEntry.TABLE_NAME, CpFxEntry.TABLE_MATCHER,
                    CpFxEntry.TABLE_ID_MATCHER, CpFxEntry.COLUMN_COMPETITION_ID,
                    CpFxEntry.TABLE_ID_MATCHER2, CpFxEntry.COLUMN_FIXTURE_ID
            ),
            new FDParams(TmEntry.LOADER_ID, TmEntry.TABLE_NAME, TmEntry.TABLE_MATCHER, TmEntry.TABLE_ID_MATCHER, TmEntry.COLUMN_TEAM_ID),
            new FDParams(FxEntry.LOADER_ID, FxEntry.TABLE_NAME, FxEntry.TABLE_MATCHER, FxEntry.TABLE_ID_MATCHER, FxEntry.COLUMN_FIXTURE_ID),
            new FDParams(TbEntry.LOADER_ID, TbEntry.TABLE_NAME, TbEntry.TABLE_MATCHER, TbEntry.TABLE_ID_MATCHER, TbEntry.COLUMN_COMPETITION_ID),
            new FDParams(PlEntry.LOADER_ID, PlEntry.TABLE_NAME, PlEntry.TABLE_MATCHER, PlEntry.TABLE_ID_MATCHER, PlEntry.COLUMN_TEAM_ID),
    };


    public static final class FDParams {
        int id;
        String tableName;
        int tableMatcher;
        int tableIdMatcher;
        String columnId;
        int tableIdMatcher2;
        String columnId2;

        public FDParams(int id, String tableName, int tableMatcher, int tableIdMatcher, String column_id) {
            this.id = id;
            this.tableName = tableName;
            this.tableMatcher = tableMatcher;
            this.tableIdMatcher = tableIdMatcher;
            this.columnId = column_id;
            this.tableIdMatcher2 = -1;   // exception if using
            this.columnId2 = null;

        }

        public FDParams(int id, String tableName, int tableMatcher, int tableIdMatcher, String column_id,
                        int tableIdMatcher2, String column_id2) {
            this.id = id;
            this.tableName = tableName;
            this.tableMatcher = tableMatcher;
            this.tableIdMatcher = tableIdMatcher;
            this.columnId = column_id;
            this.tableIdMatcher2 = tableIdMatcher2;
            this.columnId2 = column_id2;

        }


    }


}
