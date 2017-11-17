package ru.vpcb.btplay.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ru.vpcb.btplay.data.RecipeContract.RecipeEntry;

import static ru.vpcb.btplay.utils.Constants.DATABASE_NAME;
import static ru.vpcb.btplay.utils.Constants.DATABASE_VERSION;
import static ru.vpcb.btplay.utils.Constants.DATABASE_NAME;
import static ru.vpcb.btplay.utils.Constants.DATABASE_VERSION;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 23-Oct-17
 * Email: vadim.v.voronov@gmail.com
 */


public class RecipeDbHelper extends SQLiteOpenHelper {

    RecipeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE = "CREATE TABLE " + RecipeEntry.TABLE_NAME + " (" +
//                RecipeEntry._ID + " INTEGER PRIMARY KEY, " +
                RecipeEntry.COLUMN_RECIPE_ID + " INTEGER PRIMARY KEY, " +           // int
                RecipeEntry.COLUMN_RECIPE_NAME + " TEXT NOT NULL, " +               // string
                RecipeEntry.COLUMN_RECIPE_LENGTH + " INTEGER NOT NULL, " +          // int
                RecipeEntry.COLUMN_RECIPE_VALUE + " TEXT NOT NULL);";               // string
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS " + RecipeEntry.TABLE_NAME);
//        onCreate(db);
    }
}
