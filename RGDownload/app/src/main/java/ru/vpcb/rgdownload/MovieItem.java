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
}
