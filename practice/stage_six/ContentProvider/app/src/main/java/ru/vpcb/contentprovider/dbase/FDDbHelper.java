package ru.vpcb.contentprovider.dbase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static ru.vpcb.contentprovider.dbase.FDContract.DATABASE_NAME;
import static ru.vpcb.contentprovider.dbase.FDContract.DATABASE_VERSION;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 23-Oct-17
 * Email: vadim.v.voronov@gmail.com
 */

/**
 * RecipeDbHelper class for RecipeItem Database Content Provider
 */
public class FDDbHelper extends SQLiteOpenHelper {

    FDDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Creates Database of RecipeItems
     *
     * @param db SQLiteDatabse database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE_COMPETITIONS = "CREATE TABLE " + FDContract.CpEntry.TABLE_NAME + " (" +
//                FDContract.CpEntry._ID + " INTEGER PRIMARY KEY, " +
                FDContract.CpEntry.COLUMN_COMPETITION_ID + " INTEGER PRIMARY KEY, " +       // int
                FDContract.CpEntry.COLUMN_COMPETITION_CAPTION + " TEXT NOT NULL, " +                    // string
                FDContract.CpEntry.COLUMN_COMPETITION_LEAGUE + " TEXT NOT NULL, " +         // string
                FDContract.CpEntry.COLUMN_COMPETITION_YEAR + " TEXT NOT NULL, " +           // string
                FDContract.CpEntry.COLUMN_CURRENT_MATCHDAY + " INTEGER NOT NULL, " +        // int
                FDContract.CpEntry.COLUMN_NUMBER_MATCHDAYS + " INTEGER NOT NULL, " +        // int
                FDContract.CpEntry.COLUMN_NUMBER_TEAMS + " INTEGER NOT NULL, " +            // int
                FDContract.CpEntry.COLUMN_NUMBER_GAMES + " INTEGER NOT NULL, " +            // int
                FDContract.CpEntry.COLUMN_LAST_UPDATE + " INTEGER NOT NULL, " +              // int from date
                FDContract.CpEntry.COLUMN_LAST_REFRESH + " INTEGER NOT NULL);";                 // int from date

        final String CREATE_TABLE_COMPETITION_TEAMS = "CREATE TABLE " + FDContract.CpTeamEntry.TABLE_NAME + " (" +
//                FDContract.CpEntry._ID + " INTEGER PRIMARY KEY, " +
                FDContract.CpTeamEntry.COLUMN_COMPETITION_ID + " INTEGER PRIMARY KEY, " +   // int
                FDContract.CpTeamEntry.COLUMN_TEAM_ID + " INTEGER NOT NULL);";              // int

        final String CREATE_TABLE_COMPETITION_FIXTURES = "CREATE TABLE " + FDContract.CpFixtureEntry.TABLE_NAME + " (" +
//                FDContract.CpEntry._ID + " INTEGER PRIMARY KEY, " +
                FDContract.CpFixtureEntry.COLUMN_COMPETITION_ID + " INTEGER PRIMARY KEY, " + // int
                FDContract.CpFixtureEntry.COLUMN_FIXTURE_ID + " INTEGER NOT NULL, " +        // int
                FDContract.CpFixtureEntry.COLUMN_TEAM_HOME_ID + " INTEGER NOT NULL, " +      // int
                FDContract.CpFixtureEntry.COLUMN_TEAM_AWAY_ID + " INTEGER NOT NULL);";       // int

        final String CREATE_TABLE_TEAMS = "CREATE TABLE " + FDContract.TmEntry.TABLE_NAME + " (" +
//                RecipeEntry._ID + " INTEGER PRIMARY KEY, " +
                FDContract.TmEntry.COLUMN_TEAM_ID + " INTEGER PRIMARY KEY, " +              // int
                FDContract.TmEntry.COLUMN_TEAM_NAME + " TEXT NOT NULL, " +                  // string
                FDContract.TmEntry.COLUMN_TEAM_CODE + " TEXT NOT NULL, " +                  // string
                FDContract.TmEntry.COLUMN_TEAM_SHORT_NAME + " TEXT NOT NULL, " +            // string
                FDContract.TmEntry.COLUMN_TEAM_MARKET_VALUE + " TEXT NOT NULL, " +          // string
                FDContract.TmEntry.COLUMN_TEAM_CREST_URI + " TEXT NOT NULL, " +             // string
                FDContract.TmEntry.COLUMN_REFRESH_DATE + " INTEGER NOT NULL);";             // int from date


        final String CREATE_TABLE_FIXTURES = "CREATE TABLE " + FDContract.FxEntry.TABLE_NAME + " (" +
//                RecipeEntry._ID + " INTEGER PRIMARY KEY, " +
                FDContract.FxEntry.COLUMN_FIXTURE_ID + " INTEGER PRIMARY KEY, " +       // int
                FDContract.FxEntry.COLUMN_FIXTURE_DATE + " TEXT NOT NULL, " +           // string from date
                FDContract.FxEntry.COLUMN_FIXTURE_DATE_LONG + " INTEGER NOT NULL, " +   // int from date
                FDContract.FxEntry.COLUMN_FIXTURE_STATUS + " TEXT NOT NULL, " +         // string
                FDContract.FxEntry.COLUMN_FIXTURE_MATCHDAY + " INTEGER NOT NULL, " +    // int
                FDContract.FxEntry.COLUMN_FIXTURE_TEAM_HOME + " TEXT NOT NULL, " +      // string
                FDContract.FxEntry.COLUMN_FIXTURE_TEAM_AWAY + " TEXT NOT NULL, " +      // string
                FDContract.FxEntry.COLUMN_FIXTURE_GOALS_HOME + " INTEGER NOT NULL, " +  // int
                FDContract.FxEntry.COLUMN_FIXTURE_GOALS_AWAY + " INTEGER NOT NULL, " +  // int
                FDContract.FxEntry.COLUMN_FIXTURE_ODDS_WIN + " REAL NOT NULL, " +       // real
                FDContract.FxEntry.COLUMN_FIXTURE_ODDS_DRAW + " REAL NOT NULL, " +      // real
                FDContract.FxEntry.COLUMN_FIXTURE_ODDS_LOSE + " REAL NOT NULL, " +      // real
                FDContract.FxEntry.COLUMN_REFRESH_DATE + " INTEGER NOT NULL);";         // int from date


        final String CREATE_TABLE_TABLES = "CREATE TABLE " + FDContract.TbEntry.TABLE_NAME + " (" +
//                RecipeEntry._ID + " INTEGER PRIMARY KEY, " +
                FDContract.TbEntry.COLUMN_COMPETITION_ID + " INTEGER PRIMARY KEY, " +   // int
                FDContract.TbEntry.COLUMN_TEAM_ID + " INTEGER NOT NULL, " +             // int
                FDContract.TbEntry.COLUMN_COMPETITION_MATCHDAY + " INTEGER NOT NULL, " +    // int
                FDContract.TbEntry.COLUMN_LEAGUE_CAPTION + " TEXT NOT NULL, " +         // string
                FDContract.TbEntry.COLUMN_TEAM_POSITION + " INTEGER NOT NULL, " +       // int
                FDContract.TbEntry.COLUMN_TEAM_NAME + " TEXT NOT NULL, " +              // string
                FDContract.TbEntry.COLUMN_CREST_URI + " TEXT NOT NULL, " +              // string
                FDContract.TbEntry.COLUMN_TEAM_PLAYED_GAMES + " INTEGER NOT NULL, " +   // int
                FDContract.TbEntry.COLUMN_TEAM_POINTS + " INTEGER NOT NULL, " +         // int
                FDContract.TbEntry.COLUMN_TEAM_GOALS + " INTEGER NOT NULL, " +          // int
                FDContract.TbEntry.COLUMN_TEAM_GOALS_AGAINST + " INTEGER NOT NULL, " +  // int
                FDContract.TbEntry.COLUMN_TEAM_GOALS_DIFFERENCE + " INTEGER NOT NULL, " +   // int
                FDContract.TbEntry.COLUMN_TEAM_WINS + " INTEGER NOT NULL, " +           // int
                FDContract.TbEntry.COLUMN_TEAM_DRAWS + " INTEGER NOT NULL, " +          // int
                FDContract.TbEntry.COLUMN_TEAM_LOSSES + " INTEGER NOT NULL, " +         // int
                FDContract.TbEntry.COLUMN_REFRESH_DATE + " INTEGER NOT NULL);";         // int from date

        final String CREATE_TABLE_PLAYERS = "CREATE TABLE " + FDContract.PlEntry.TABLE_NAME + " (" +
//                RecipeEntry._ID + " INTEGER PRIMARY KEY, " +
                FDContract.PlEntry.COLUMN_TEAM_ID + " INTEGER PRIMARY KEY, " +          // int
                FDContract.PlEntry.COLUMN_PLAYER_NAME + " TEXT NOT NULL, " +            // string
                FDContract.PlEntry.COLUMN_PLAYER_POSITION + " TEXT NOT NULL, " +        // string
                FDContract.PlEntry.COLUMN_PLAYER_JERSEY_NUMBER + " INTEGER NOT NULL, " +  // int
                FDContract.PlEntry.COLUMN_PLAYER_DATE_BIRTH + " TEXT NOT NULL, " +      // string from date
                FDContract.PlEntry.COLUMN_PLAYER_NATIONALITY + " TEXT NOT NULL, " +     // string
                FDContract.PlEntry.COLUMN_PLAYER_DATE_CONTRACT + " TEXT NOT NULL, " +   // string from date
                FDContract.PlEntry.COLUMN_PLAYER_MARKET_VALUE + " TEXT NOT NULL, " +    // string
                FDContract.PlEntry.COLUMN_REFRESH_DATE + " INTEGER NOT NULL);";         // int from date

        db.execSQL(CREATE_TABLE_COMPETITIONS);
//        db.execSQL(CREATE_TABLE_COMPETITION_TEAMS);
//        db.execSQL(CREATE_TABLE_COMPETITION_FIXTURES);
//        db.execSQL(CREATE_TABLE_TEAMS);
//        db.execSQL(CREATE_TABLE_FIXTURES);
//        db.execSQL(CREATE_TABLE_TABLES);
//        db.execSQL(CREATE_TABLE_PLAYERS);

    }

    /**
     * Upgrades old version database to new version
     *
     * @param db         SQLiteDatabase database for upgrading
     * @param oldVersion int old version to compare
     * @param newVersion int new version to compare
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FDContract.CpEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FDContract.CpTeamEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FDContract.CpFixtureEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FDContract.TmEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FDContract.FxEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FDContract.TbEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FDContract.PlEntry.TABLE_NAME);
        onCreate(db);
    }
}
