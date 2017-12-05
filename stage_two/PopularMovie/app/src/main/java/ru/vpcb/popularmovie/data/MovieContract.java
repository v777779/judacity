package ru.vpcb.popularmovie.data;

import android.net.Uri;
import android.provider.BaseColumns;

import org.json.JSONArray;

import ru.vpcb.popularmovie.pager.ReviewItem;
import ru.vpcb.popularmovie.utils.ParseUtils;

import static ru.vpcb.popularmovie.utils.Constants.KEY_GENRE_IDS;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 23-Oct-17
 * Email: vadim.v.voronov@gmail.com
 */


public class MovieContract {
    public static final String AUTHORITY = "ru.vpcb.popularmovie";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_MOVIES = "movies";

    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();
        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_MOVIE_VOTE_COUNT = "movie_vote";      // int
        public static final String COLUMN_MOVIE_ID = "movie_id";                // int
        public static final String COLUMN_MOVIE_VIDEO = "movie_video";          // int
        public static final String COLUMN_MOVIE_VOTE_AVG = "movie_vote_avg";    // double
        public static final String COLUMN_MOVIE_TITLE = "movie_title";          // string
        public static final String COLUMN_MOVIE_POPULARITY = "movie_popularity";  // double
        public static final String COLUMN_MOVIE_POSTER = "movie_poster";        // string
        public static final String COLUMN_MOVIE_LANG_OGN = "movie_lang_ogn";    // string
        public static final String COLUMN_MOVIE_TITLE_OGN = "movie_title_ogn";  // string
        public static final String COLUMN_MOVIE_GENRES = "movie_genre";         // string
        public static final String COLUMN_MOVIE_BACKDROP = "movie_backdrop";    // string
        public static final String COLUMN_MOVIE_ADULT = "movie_adult";          // int
        public static final String COLUMN_MOVIE_OVERVIEW = "movie_overview";    // string
        public static final String COLUMN_MOVIE_RELEASE = "movie_release";      // string

    }

}
