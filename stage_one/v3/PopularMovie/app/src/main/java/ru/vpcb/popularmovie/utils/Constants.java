package ru.vpcb.popularmovie.utils;

import ru.vpcb.popularmovie.BuildConfig;
import ru.vpcb.popularmovie.ChildActivity;
import ru.vpcb.popularmovie.MainActivity;
import ru.vpcb.popularmovie.MovieAdapter;
import ru.vpcb.popularmovie.R;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 29-Sep-17
 * Email: vadim.v.voronov@gmail.com
 */

public class Constants {

    // MainActivity constants
    public static final String TAG = MainActivity.class.getSimpleName();
    public final static float COLUMN_WIDTH_HIGH = 200;
    public final static float DP_WIDTH_LOW = 400;
    public final static float DP_WIDTH_MID = 720;
    public final static float DP_WIDTH_HIGH = 1080;
    public final static float COLUMN_WIDTH_LOW = 150;
    public final static int MAX_COLUMNS = 6;
    public final static int MIN_COLUMNS = 2;
    public static final int BASE_ID_RECYCLEVIEW = 8200;
    public final static int PAGE_FIRST = 1;
    public static final int PAGE_NUMBER_MAX = 3;
    public final static int POSITION_FIRST = 0;
    public static final int[] BUTTON_IDS = new int[]{
            R.id.button_001, R.id.button_002, R.id.button_003
    };
//    public static final String ASYNC_MESSAGE = "{\"success\": false,  \"status_code\": 402 }";

    // ChildActivity Constants
    public static final String TAG_CHILD = ChildActivity.class.getSimpleName();
    public static final String SIGNATURE = "ru.vpcb.popularmovie";
    public static final int REVIEW_NUMBER_MAX = 3;


    // NetworkData constants
    static final String DEFAULT_LANGUAGE = "en_US";
    static final int DEFAULT_PAGE = 0;
    static final int DEFAULT_ID = 0;

    // NetworkUtils constants
    static final String MOVIE_BASE = "https://api.themoviedb.org/3/";
    static final String[] MOVIE_QUERY = {
            "movie/popular",
            "movie/now_playing",
            "movie/top_rated",
            "genre/movie/list",
            "movie/*id*/reviews",
            "empty"
    };

    // ParseUtils constants
    static final String MOVIE_KEY = "?api_key=" + BuildConfig.MOVIE_DB_API_KEY;
    static final String MOVIE_LANG = "&language=";
    static final String MOVIE_PAGE = "&page=";

    static final String KEY_STATUS = "status_code";
    static final String KEY_PAGE = "page";
    static final String KEY_PAGE_TOTAL = "total_pages";
    static final String KEY_RESULT = "results";
    static final String KEY_GENRES = "genres";
    public static final String KEY_ID = "id";
    static final String KEY_NAME = "name";

    // MovieItem constants
    public static final String KEY_VOTE = "vote_count";
    //    public static final String KEY_ID = "id";
    public static final String KEY_VIDEO = "video";
    public static final String KEY_VOTE_AVG = "vote_average";
    public static final String KEY_TITLE = "title";
    public static final String KEY_POP = "popularity";
    public static final String KEY_PATH = "poster_path";
    public static final String KEY_ORIGIN_LANG = "original_language";
    public static final String KEY_ORIGIN_TITLE = "original_title";
    public static final String KEY_GENRE_IDS = "genre_ids";
    public static final String KEY_BACKDROP_PATH = "backdrop_path";
    public static final String KEY_ADULT = "adult";
    public static final String KEY_OVERVIEW = "overview";
    public static final String KEY_RELEASE_DATE = "release_date";
    public static final String[] POSTER_SIZE = "w92,w154,w185,w342,w500,w780,w1280,original".split(",");
    public static final int KEY_POSTER_LOW = 2;
    public static final int KEY_POSTER_MID = 4;
    public static final int KEY_POSTER_HIGH = 5;
    public static final int KEY_POSTER_SUPER = 6;
    public static final int KEY_POSTER_ORIG = 7;
    public static final String POSTER_BASE = "http://image.tmdb.org/t/p/";


    // MovieAdapter constants
    public static final String TAG_MOVIE = MovieAdapter.class.getSimpleName();
    public static final double FRAME_RATIO = 1.8;

    // ReviewItem constants
//    public static final String KEY_ID = "id";
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_CONTENT = "content";
    public static final String KEY_URL = "url";


}
