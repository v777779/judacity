package ru.vpcb.contentprovider.fdbase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static ru.vpcb.bakingapp.data.RecipeContract.RecipeEntry.COLUMN_RECIPE_ID;
import static ru.vpcb.bakingapp.data.RecipeContract.RecipeEntry.COLUMN_RECIPE_IMAGE;
import static ru.vpcb.bakingapp.data.RecipeContract.RecipeEntry.COLUMN_RECIPE_LENGTH;
import static ru.vpcb.bakingapp.data.RecipeContract.RecipeEntry.COLUMN_RECIPE_NAME;
import static ru.vpcb.bakingapp.data.RecipeContract.RecipeEntry.COLUMN_RECIPE_VALUE;
import static ru.vpcb.bakingapp.data.RecipeContract.RecipeEntry.TABLE_NAME;
import static ru.vpcb.bakingapp.utils.Constants.DATABASE_NAME;
import static ru.vpcb.bakingapp.utils.Constants.DATABASE_VERSION;
import static ru.vpcb.contentprovider.fdbase.FDContract.DATABASE_NAME;
import static ru.vpcb.contentprovider.fdbase.FDContract.DATABASE_VERSION;
import static ru.vpcb.contentprovider.fdbase.FDContract.TABLE_FIXTURES;
import static ru.vpcb.contentprovider.fdbase.FDContract.TABLE_PLAYERS;
import static ru.vpcb.contentprovider.fdbase.FDContract.TABLE_TABLES;

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
        final String CREATE_TABLE_COMPETITIONS = "CREATE TABLE " + FDContract.CmEntry.TABLE_NAME + " (" +
//                FDContract.CmEntry._ID + " INTEGER PRIMARY KEY, " +
                FDContract.CmEntry.COLUMN_COMPETITION_ID + " INTEGER PRIMARY KEY, " +       // int
                FDContract.CmEntry.COLUMN_CAPTION + " TEXT NOT NULL, " +                    // string
                FDContract.CmEntry.COLUMN_COMPETITION_LEAGUE + " TEXT NOT NULL, " +         // string
                FDContract.CmEntry.COLUMN_COMPETITION_YEAR + " TEXT NOT NULL, " +           // string
                FDContract.CmEntry.COLUMN_CURRENT_MATCHDAY + " INTEGER NOT NULL, " +        // int
                FDContract.CmEntry.COLUMN_NUMBER_MATCHDAYS + " INTEGER NOT NULL, " +        // int
                FDContract.CmEntry.COLUMN_NUMBER_TEAMS + " INTEGER NOT NULL, " +            // int
                FDContract.CmEntry.COLUMN_NUMBER_GAMES + " INTEGER NOT NULL, " +            // int
                FDContract.CmEntry.COLUMN_LAST_UPDATE + " TEXT NOT NULL);";                 // string from date


        db.execSQL(CREATE_TABLE_COMPETITIONS);

        final String CREATE_TABLE_TEAMS = "CREATE TABLE " + FDContract.TmEntry.TABLE_NAME + " (" +
//                RecipeEntry._ID + " INTEGER PRIMARY KEY, " +
                COLUMN_RECIPE_ID + " INTEGER PRIMARY KEY, " +           // int
                COLUMN_RECIPE_NAME + " TEXT NOT NULL, " +               // string
                COLUMN_RECIPE_LENGTH + " INTEGER NOT NULL, " +          // int
                COLUMN_RECIPE_IMAGE + " TEXT NOT NULL, " +               // string
                COLUMN_RECIPE_VALUE + " TEXT NOT NULL);";               // string
        db.execSQL(CREATE_TABLE_TEAMS);

        final String CREATE_TABLE_FIXTURES = "CREATE TABLE " + TABLE_FIXTURES + " (" +
//                RecipeEntry._ID + " INTEGER PRIMARY KEY, " +
                COLUMN_RECIPE_ID + " INTEGER PRIMARY KEY, " +           // int
                COLUMN_RECIPE_NAME + " TEXT NOT NULL, " +               // string
                COLUMN_RECIPE_LENGTH + " INTEGER NOT NULL, " +          // int
                COLUMN_RECIPE_IMAGE + " TEXT NOT NULL, " +               // string
                COLUMN_RECIPE_VALUE + " TEXT NOT NULL);";               // string
        db.execSQL(CREATE_TABLE_FIXTURES);

        final String CREATE_TABLE_TABLES = "CREATE TABLE " + TABLE_TABLES + " (" +
//                RecipeEntry._ID + " INTEGER PRIMARY KEY, " +
                COLUMN_RECIPE_ID + " INTEGER PRIMARY KEY, " +           // int
                COLUMN_RECIPE_NAME + " TEXT NOT NULL, " +               // string
                COLUMN_RECIPE_LENGTH + " INTEGER NOT NULL, " +          // int
                COLUMN_RECIPE_IMAGE + " TEXT NOT NULL, " +               // string
                COLUMN_RECIPE_VALUE + " TEXT NOT NULL);";               // string
        db.execSQL(CREATE_TABLE_TABLES);

        final String CREATE_TABLE_PLAYERS = "CREATE TABLE " + TABLE_PLAYERS + " (" +
//                RecipeEntry._ID + " INTEGER PRIMARY KEY, " +
                COLUMN_RECIPE_ID + " INTEGER PRIMARY KEY, " +           // int
                COLUMN_RECIPE_NAME + " TEXT NOT NULL, " +               // string
                COLUMN_RECIPE_LENGTH + " INTEGER NOT NULL, " +          // int
                COLUMN_RECIPE_IMAGE + " TEXT NOT NULL, " +               // string
                COLUMN_RECIPE_VALUE + " TEXT NOT NULL);";               // string
        db.execSQL(CREATE_TABLE_PLAYERS);


    }

    /**
     * Upgrades old version database to new version
     *
     * @param db         SQLiteDatabase databed for upgrading
     * @param oldVersion int old version to compare
     * @param newVersion int new version to compare
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + RecipeEntry.TABLE_NAME);
        onCreate(db);

    }
}
