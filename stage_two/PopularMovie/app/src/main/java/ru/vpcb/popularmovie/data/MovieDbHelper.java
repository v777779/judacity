package ru.vpcb.popularmovie.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ru.vpcb.popularmovie.data.MovieContract.MovieEntry;

import static ru.vpcb.popularmovie.utils.Constants.DATABASE_NAME;
import static ru.vpcb.popularmovie.utils.Constants.DATABASE_VERSION;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 23-Oct-17
 * Email: vadim.v.voronov@gmail.com
 */


public class MovieDbHelper extends SQLiteOpenHelper {

    MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY, " +
                MovieEntry.COLUMN_MOVIE_VOTE_COUNT + " INTEGER NOT NULL, " +    // int
                MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +            // int
                MovieEntry.COLUMN_MOVIE_VIDEO + " INTEGER NOT NULL, " +         // boolean
                MovieEntry.COLUMN_MOVIE_VOTE_AVG + " REAL NOT NULL, " +         // double
                MovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +            // string
                MovieEntry.COLUMN_MOVIE_POPULARITY + " REAL NOT NULL, " +       // double
                MovieEntry.COLUMN_MOVIE_POSTER + " TEXT NOT NULL, " +           // string
                MovieEntry.COLUMN_MOVIE_LANG_OGN + " TEXT NOT NULL, " +         // string
                MovieEntry.COLUMN_MOVIE_TITLE_OGN + " TEXT NOT NULL, " +        // string
                MovieEntry.COLUMN_MOVIE_GENRES + " TEXT NOT NULL, " +           // string
                MovieEntry.COLUMN_MOVIE_BACKDROP + " TEXT NOT NULL, " +         // string
                MovieEntry.COLUMN_MOVIE_ADULT + " INTEGER NOT NULL, " +         // boolean
                MovieEntry.COLUMN_MOVIE_OVERVIEW + " TEXT NOT NULL, " +         // string
                MovieEntry.COLUMN_MOVIE_RELEASE + " TEXT NOT NULL);";           // string

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
