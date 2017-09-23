package ru.vpcb.rgdownload;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ru.vpcb.rgdownload.utils.MovieUtils;

/**
 * Created by V1 on 23-Sep-17.
 */

public class MovieItem {
    private static final String KEY_VOTE = "vote_count";
    private static final String KEY_ID = "id";
    private static final String KEY_VIDEO = "video";
    private static final String KEY_VOTE_AVG = "vote_average";
    private static final String KEY_TITLE = "title";
    private static final String KEY_POP = "popularity";
    private static final String KEY_PATH = "poster_path";
    private static final String KEY_ORIGIN_LANG = "original_language";
    private static final String KEY_ORIGIN_TITLE = "original_title";
    private static final String KEY_GENRE_IDS = "genre_ids";

    private static final String[] POSTER_SIZE = "w92,w154,w185,w342,w500,w780,original".split(",");
    private static final int KEY_POSTER_LOW = 2;
    private static final int KEY_POSTER_MID = 4;
    private static final int KEY_POSTER_HIGH = 5;
    private static final String POSTER_BASE = "http://image.tmdb.org/t/p/";

    private int voteCount;
    private int id;
    private boolean video;
    private double voteAverage;
    private String title;
    private double popularity;
    private String posterPath;
    private String originLang;
    private String originTitle;
    private List<Integer> listGenreID;
    private List<String> listGenres;
    private List<Integer> listReviewID;
    private String posterHighRes;
    private String posterLowRes;

    private boolean valid;

    public MovieItem(JSONObject json) {
        valid = parser(json);

    }

    /**
     * Parse JSON object and fills fields of the object
     * Returns true if completed successfully
     *
     * @param json input JSON object
     * @return result of operation,  true completed successfully
     */
    private boolean parser(JSONObject json) {
        try {
            voteCount = json.getInt(KEY_VOTE);
            id = json.getInt(KEY_ID);
            video = json.getBoolean(KEY_VIDEO);
            voteAverage = json.getDouble(KEY_VOTE_AVG);
            title = json.getString(KEY_TITLE);
            popularity = json.getDouble(KEY_POP);
            posterPath = json.getString(KEY_PATH);
            originLang = json.getString(KEY_ORIGIN_LANG);
            originTitle = json.getString(KEY_ORIGIN_TITLE);
            JSONArray jsonArray = json.getJSONArray(KEY_GENRE_IDS);
            listGenreID = new ArrayList<>();
            listGenres = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                int genreId = jsonArray.getInt(i);
                listGenreID.add(genreId);
                listGenres.add(MovieUtils.getGenre(genreId));
            }
        } catch (JSONException e) {
            return false;
        }
        return true;
    }


    public boolean isValid() {
        return valid;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public int getId() {
        return id;
    }

    public boolean isVideo() {
        return video;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public String getTitle() {
        return title;
    }

    public double getPopularity() {
        return popularity;
    }

    public String getPoster(int size) {
        if(size < 0 || size >= POSTER_SIZE.length) {
            size = KEY_POSTER_LOW;
        }
        return POSTER_BASE+POSTER_SIZE[size]+posterPath;
    }
    public String getPosterLow() {
        return getPoster(KEY_POSTER_LOW);
    }

    public String getPosterMid() {
        return getPoster(KEY_POSTER_MID);
    }

    public String getPosterHigh() {
        return getPoster(KEY_POSTER_HIGH);
    }

    public String getOriginLang() {
        return originLang;
    }

    public String getOriginTitle() {
        return originTitle;
    }

    public List<Integer> getListGenreID() {
        return listGenreID;
    }

    public List<String> getListGenres() {
        return listGenres;
    }

    public List<Integer> getListReviewID() {
        return listReviewID;
    }

    public String getPosterHighRes() {
        return posterHighRes;
    }

    public String getPosterLowRes() {
        return posterLowRes;
    }
}
