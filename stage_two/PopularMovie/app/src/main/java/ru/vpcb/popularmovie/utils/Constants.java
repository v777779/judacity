package ru.vpcb.popularmovie.utils;

import ru.vpcb.popularmovie.BuildConfig;
import ru.vpcb.popularmovie.MainActivity;
import ru.vpcb.popularmovie.pager.MovieAdapter;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 29-Sep-17
 * Email: vadim.v.voronov@gmail.com
 */

public class Constants {

    public static final String DATABASE_NAME = "moviesDb.db";
    public static final int DATABASE_VERSION = 1;

    public static final String BUNDLE_LOADER_URL_ID = "bundle_loader_url_id";
    public static final String BUNDLE_LOADER_ID = "bundle_loader_id";
    public static final String BUNDLE_LOADER_LIST_URI_ID = "bundle_loader_list_uri_id";
    public static final String BUNDLE_LOADER_LIST_POPULAR_ID = "bundle_loader_list_popular_id";
    public static final String BUNDLE_LOADER_LIST_TOPRATED_ID = "bundle_loader_list_toprated_id";
    public static final String BUNDLE_LOADER_LIST_POSITION_ID = "bundle_loader_list_position_id";
    public static final String BUNDLE_LOADER_STRING_ID = "bundle_loader_string_id";
    public static final String BUNDLE_LOADER_QUERY_ID = "bundle_loader_query_id";
    public static final String BUNDLE_LOADER_PAGE_ID = "bundle_loader_page_id";
    public static final String BUNDLE_LOADER_MOVIE_ID = "bundle_loader_movie_id";
    public static final String BUNDLE_LOADER_MOVIE_ITEM_ID = "bundle_loader_movie_item_id";
    public static final String BUNDLE_LOADER_LIST_REVIEW_ID = "bundle_loader_list_review_id";
    public static final String BUNDLE_LOADER_LIST_TRAILER_ID = "bundle_loader_list_trailer_id";


    // MainActivity constants
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final float COLUMN_WIDTH_LOW = 150;
    public static final float COLUMN_WIDTH_MIDDLE = 150;
    public static final float COLUMN_WIDTH_HIGH = 200;

    public static final float DP_WIDTH_PIXELS_HIGH = 800;

    public static final float DP_WIDTH_PORTRAIT_LOW = 320;
    public static final float DP_WIDTH_PORTRAIT_MDL = 400;
    public static final float DP_WIDTH_PORTRAIT_HIGH = 800;

    public static final float DP_WIDTH_LANDSCAPE_LOW = 500;
    public static final float DP_WIDTH_LANDSCAPE_MDL = 600;
    public static final float DP_WIDTH_LANDSCAPE_HIGH = 1200;

    public static final int MAX_COLUMNS = 6;
    public static final int MIN_COLUMNS = 2;
    public static final int BASE_ID_RECYCLEVIEW = 8200;
    public static final int PAGE_FIRST = 1;
    public static final int PAGE_NUMBER_MAX = 3;
    public static final int POSITION_FIRST = 0;


    // ChildActivity Constants
//    public static final String TAG_CHILD = ChildActivity.class.getSimpleName();
    public static final String SIGNATURE = "ru.vpcb.popularmovie";
    public static final int REVIEW_NUMBER_MAX = 25;
    public static final String INTENT_MOVIE_ITEM_ID = "intent_movie_item_id";


    // NetworkData constants
    public static final String DEFAULT_LANGUAGE = "en_US";
    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_ID = 0;

    // NetworkUtils constants
    public static final int POPULAR_ID = 0;
    public static final int TOPRATED_ID = 1;
    public static final int FAVORITES_ID = 2;
    public static final int NOWDAYS_ID = 3;
    public static final int GENRES_ID = 4;
    public static final int REVIEW_ID = 5;
    public static final int TRAILER_ID = 6;
    public static final int EMPTY_ID = 7;

    public static final int LOADER_BASE_ID = 1200;
    public static final int LOADER_POPULAR_ID = LOADER_BASE_ID + POPULAR_ID;
    public static final int LOADER_TOPRATED_ID = LOADER_BASE_ID + TOPRATED_ID;
    public static final int LOADER_FAVORITES_ID = LOADER_BASE_ID + FAVORITES_ID;
    public static final int LOADER_NOWDAYS_ID = LOADER_BASE_ID + NOWDAYS_ID;
    public static final int LOADER_GENRE_ID = LOADER_BASE_ID + GENRES_ID;
    public static final int LOADER_REVIEW_ID = LOADER_BASE_ID + REVIEW_ID;
    public static final int LOADER_TRAILER_ID = LOADER_BASE_ID + TRAILER_ID;
    public static final int LOADER_EMPTY_ID = LOADER_BASE_ID + EMPTY_ID;

    public static final int LOADER_CONSTANT_ID = 1210;
    public static final int LOADER_MOVIE_DB_ID = 1220;

    public static final int MOVIE_PAGE_SIZE = 20;

    public static final String MOVIE_BASE = "https://api.themoviedb.org/3/";
    public static final String[] MOVIE_QUERY = {
            "movie/popular",
            "movie/top_rated",
            "placeholder_for_favorites",
            "movie/now_playing",
            "genre/movie/list",
            "movie/*id*/reviews",
            "movie/*id*/videos",
            "empty"
    };

    public static final String YOUTUBE_MOVIE_BASE = "https://www.youtube.com/watch?v=";
    public static final String YOUTUBE_APP_BASE = "vnd.youtube:";
    public static final String YOUTUBE_POSTER_BASE = "https://img.youtube.com/vi/";
    public static final String YOUTUBE_DEFAULT_IMAGE = "default.jpg";

    public static final int[] MOVIE_LOADER_IDS = {
            LOADER_POPULAR_ID,
            LOADER_TOPRATED_ID,
            LOADER_FAVORITES_ID,
            LOADER_NOWDAYS_ID,
            LOADER_GENRE_ID,
            LOADER_REVIEW_ID,
            LOADER_TRAILER_ID,
            LOADER_EMPTY_ID
    };


    // ParseUtils constants
    public static final String MOVIE_KEY = "?api_key=" + BuildConfig.MOVIE_DB_API_KEY;
    public static final String MOVIE_LANG = "&language=";
    public static final String MOVIE_PAGE = "&page=";

    public static final String KEY_STATUS = "status_code";
    public static final String KEY_PAGE = "page";
    public static final String KEY_PAGE_TOTAL = "total_pages";
    public static final String KEY_RESULT = "results";
    public static final String KEY_GENRES = "genres";
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";

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
    public static final int KEY_POSTER_MID = 3;
    public static final int KEY_POSTER_HIGH = 5;
    public static final int KEY_POSTER_SUPER = 6;
    public static final int KEY_POSTER_ORIG = 7;
    public static final String POSTER_BASE = "http://image.tmdb.org/t/p/";


    // TrailerItem constants
    //    public static final String KEY_ID = "id";
    public static final String KEY_CODE = "key";
    //    public static final String KEY_NAME = "name";
    public static final String KEY_ISO639 = "iso_639_1";
    public static final String KEY_ISO3166 = "iso_3166_1";
    public static final String KEY_SITE = "site";
    public static final String KEY_SIZE = "size";
    public static final String KEY_TYPE = "type";
    public static final String KEY_YOUTUBE = "youtube";

    // MovieAdapter constants
    public static final String TAG_MOVIE = MovieAdapter.class.getSimpleName();
    public static final double FRAME_RATIO = 1.8;

    // ReviewItem constants
//  public static final String KEY_ID = "id";
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_CONTENT = "content";
    public static final String KEY_URL = "url";


}
