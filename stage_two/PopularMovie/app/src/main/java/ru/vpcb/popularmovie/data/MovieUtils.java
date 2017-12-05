package ru.vpcb.popularmovie.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;

import java.util.List;

import ru.vpcb.popularmovie.LoaderDb;
import ru.vpcb.popularmovie.MainActivity;
import ru.vpcb.popularmovie.data.MovieContract;
import ru.vpcb.popularmovie.data.MovieContract.MovieEntry;
import ru.vpcb.popularmovie.pager.MovieItem;

import static ru.vpcb.popularmovie.data.MovieContract.MovieEntry.COLUMN_MOVIE_ID;
import static ru.vpcb.popularmovie.data.MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVG;
import static ru.vpcb.popularmovie.data.MovieContract.MovieEntry.CONTENT_URI;
import static ru.vpcb.popularmovie.utils.Constants.LOADER_BASE_ID;
import static ru.vpcb.popularmovie.utils.Constants.LOADER_MOVIE_DB_ID;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 23-Oct-17
 * Email: vadim.v.voronov@gmail.com
 */


public class MovieUtils {

    public static int deleteRecord(ContentResolver contentResolver,  LoaderManager loaderManager,
                                   MovieItem movieItem, LoaderDb mLoaderDb) {
        if (movieItem == null) {
            return 0;
        }
        Uri uri = MovieEntry.CONTENT_URI;           // it's already uri
        uri = uri.buildUpon().appendPath(Integer.toString(movieItem.getMovieId())).build();

        int nDelete = contentResolver.delete(
                uri,
                COLUMN_MOVIE_ID + "=?",
                new String[]{"" + movieItem.getMovieId()}
        );

        if (nDelete != 0) {
            loaderManager.restartLoader(LOADER_BASE_ID, null, mLoaderDb);
        }
        return nDelete;
    }

    public static boolean insertRecord(ContentResolver contentResolver,  LoaderManager loaderManager,
                                 MovieItem movieItem, LoaderDb mLoaderDb) {
        if (movieItem == null) {
            return false;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieEntry.COLUMN_MOVIE_VOTE_COUNT, movieItem.getVoteCount());   // int
        contentValues.put(MovieEntry.COLUMN_MOVIE_ID, movieItem.getMovieId());             // int
        contentValues.put(MovieEntry.COLUMN_MOVIE_VIDEO, (movieItem.isVideo() ? 1 : 0));   // int:boolean
        contentValues.put(MovieEntry.COLUMN_MOVIE_VOTE_AVG, movieItem.getVoteAverage());   // double
        contentValues.put(MovieEntry.COLUMN_MOVIE_TITLE, movieItem.getTitle());            // string
        contentValues.put(MovieEntry.COLUMN_MOVIE_POPULARITY, movieItem.getPopularity());  // double
        contentValues.put(MovieEntry.COLUMN_MOVIE_POSTER, movieItem.getPosterPath());       // string
        contentValues.put(MovieEntry.COLUMN_MOVIE_LANG_OGN, movieItem.getOriginLang());    // string
        contentValues.put(MovieEntry.COLUMN_MOVIE_TITLE_OGN, movieItem.getOriginTitle());  // string
        contentValues.put(MovieEntry.COLUMN_MOVIE_GENRES, movieItem.getGenres());          // string
        contentValues.put(MovieEntry.COLUMN_MOVIE_BACKDROP, movieItem.getBackDropPath());  // string
        contentValues.put(MovieEntry.COLUMN_MOVIE_ADULT, (movieItem.isAdult() ? 1 : 0));   // int:boolean
        contentValues.put(MovieEntry.COLUMN_MOVIE_OVERVIEW, movieItem.getOverview());      // string
        contentValues.put(MovieEntry.COLUMN_MOVIE_RELEASE, movieItem.getReleaseDate());    // string


        Uri returnUri = contentResolver.insert(CONTENT_URI, contentValues);

        if (returnUri != null) {
           loaderManager.restartLoader(LOADER_BASE_ID, null, mLoaderDb);
            return true;
        }

        return false;
    }

    private int updateRecord(ContentResolver contentResolver,  LoaderManager loaderManager,
                             MovieItem movieItem, LoaderDb mLoaderDb) {
        if (movieItem == null) {
            return 0;
        }
        Uri uri = MovieEntry.CONTENT_URI;           // it's already uri
        uri = uri.buildUpon().appendPath(Integer.toString(movieItem.getMovieId())).build();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_MOVIE_VOTE_AVG, movieItem.getVoteAverage());
        int nUpdated = contentResolver.update(
                uri, contentValues,
                COLUMN_MOVIE_ID + "=?",
                new String[]{"" + movieItem.getMovieId()}
        );

        if (nUpdated != 0) {
            loaderManager.restartLoader(LOADER_BASE_ID, null, mLoaderDb);
        }
        return nUpdated;
    }




}
