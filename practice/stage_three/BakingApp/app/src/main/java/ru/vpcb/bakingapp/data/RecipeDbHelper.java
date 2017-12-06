package ru.vpcb.bakingapp.data;

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

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 23-Oct-17
 * Email: vadim.v.voronov@gmail.com
 */

/**
 * RecipeDbHelper class for RecipeItem Database Content Provider
 */
public class RecipeDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_ALTER_VERSION_1 = "ALTER TABLE "
            + TABLE_NAME + " ADD COLUMN " + COLUMN_RECIPE_IMAGE + " string;";

    RecipeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Creates Database of RecipeItems
     *
     * @param db SQLiteDatabse database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
//                RecipeEntry._ID + " INTEGER PRIMARY KEY, " +
                COLUMN_RECIPE_ID + " INTEGER PRIMARY KEY, " +           // int
                COLUMN_RECIPE_NAME + " TEXT NOT NULL, " +               // string
                COLUMN_RECIPE_LENGTH + " INTEGER NOT NULL, " +          // int
                COLUMN_RECIPE_IMAGE + " TEXT NOT NULL, " +               // string
                COLUMN_RECIPE_VALUE + " TEXT NOT NULL);";               // string
        db.execSQL(CREATE_TABLE);
    }

    /**
     *  Upgrades old version database to new version
     *
     * @param db    SQLiteDatabase databed for upgrading
     * @param oldVersion  int old version to compare
     * @param newVersion  int new version to compare
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS " + RecipeEntry.TABLE_NAME);
//        onCreate(db);
        if (oldVersion < DATABASE_VERSION) db.execSQL(DATABASE_ALTER_VERSION_1);
    }
}
