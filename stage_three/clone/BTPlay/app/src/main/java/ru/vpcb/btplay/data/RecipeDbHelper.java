package ru.vpcb.btplay.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ru.vpcb.btplay.data.RecipeContract.RecipeEntry;

import static ru.vpcb.btplay.data.RecipeContract.RecipeEntry.*;
import static ru.vpcb.btplay.utils.Constants.DATABASE_NAME;
import static ru.vpcb.btplay.utils.Constants.DATABASE_VERSION;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 23-Oct-17
 * Email: vadim.v.voronov@gmail.com
 */


public class RecipeDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_ALTER_VERSION_1 = "ALTER TABLE "
            + TABLE_NAME + " ADD COLUMN " + COLUMN_RECIPE_IMAGE + " string;";

    RecipeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

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

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS " + RecipeEntry.TABLE_NAME);
//        onCreate(db);
        if (oldVersion < DATABASE_VERSION) db.execSQL(DATABASE_ALTER_VERSION_1);
    }
}
