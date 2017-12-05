package ru.vpcb.popularmovie.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.vpcb.popularmovie.pager.MovieItem;
import ru.vpcb.popularmovie.pager.ReviewItem;
import ru.vpcb.popularmovie.trailer.TrailerItem;

import static ru.vpcb.popularmovie.utils.Constants.KEY_GENRES;
import static ru.vpcb.popularmovie.utils.Constants.KEY_ID;
import static ru.vpcb.popularmovie.utils.Constants.KEY_NAME;
import static ru.vpcb.popularmovie.utils.Constants.KEY_PAGE;
import static ru.vpcb.popularmovie.utils.Constants.KEY_RESULT;
import static ru.vpcb.popularmovie.utils.Constants.KEY_STATUS;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 29-Sep-17
 * Email: vadim.v.voronov@gmail.com
 */

public class ParseUtils {
    private static Map<Integer, String> mapGenre;

    /**
     * Checks input JSON string for valid data
     * Parse input JSON data string and makes List<MovieItem> object
     * Returns List<MovieItem> object or null
     *
     * @param jsonPage input JSON format file with page of movie parameters
     * @return List of MovieItem objects if completed successfully  or null otherwise
     */
    public static List<MovieItem> getPageList(String jsonPage) {
        List<MovieItem> list = new ArrayList<>();
        if (jsonPage == null || jsonPage.isEmpty()) {
            return null;
        }
        try {
            JSONObject json = new JSONObject(jsonPage);
            if (json.has(KEY_STATUS)) {
                return null;
            }
            if (json.has(KEY_PAGE) && json.has(KEY_RESULT)) {
                JSONArray jsonArray = json.getJSONArray(KEY_RESULT);
                int n = jsonArray.length();
                for (int i = 0; i < n; i++) {
                    JSONObject jsonItem = jsonArray.getJSONObject(i);
                    MovieItem movieItem = new MovieItem(jsonItem);
                    if (!movieItem.isValid()) {
                        continue;
                    }
                    list.add(movieItem);
                }
            }
        } catch (JSONException e) {
            return null;
        }
        return list;
    }

    /**
     * Parse input JSON data string and makes List<TrailerItem> object
     * Returns List<TrailerItem> object or null
     *
     * @param jsonPage input JSON format file with page of movie parameters
     * @return List of TrailerItem objects if completed successfully  or null otherwise
     */
    public static List<TrailerItem> getTrailerList(String jsonPage) {
        List<TrailerItem> list = new ArrayList<>();
        if (jsonPage == null || jsonPage.isEmpty()) {
            return null;
        }
        try {
            JSONObject json = new JSONObject(jsonPage);
            if (json.has(KEY_STATUS)) {            // status code
                return null;
            }

            if (json.has(KEY_RESULT)) {  // parse results
                JSONArray jsonArray = json.getJSONArray(KEY_RESULT);
                int n = jsonArray.length();
                for (int i = 0; i < n; i++) {
                    JSONObject jsonItem = jsonArray.getJSONObject(i);
                    TrailerItem trailerItem = new TrailerItem(jsonItem);
                    if (!trailerItem.isValid()) {
                        continue;
                    }
                    list.add(trailerItem);
                }
            }
        } catch (JSONException e) {
            return null;
        }
        return list;
    }


    /**
     * Parse input JSON data string and extract genre elements from results array
     * Returns the result of operation
     *
     * @param jsonPage input JSON format file with page of movie parameters
     * @return true if completed successfully or false otherwise
     */
    public static boolean setGenres(String jsonPage) {
        if (jsonPage == null || jsonPage.isEmpty()) {
            return false;
        }
        mapGenre = new HashMap<>();
        try {
            JSONObject json = new JSONObject(jsonPage);
            if (json.has(KEY_STATUS)) {            // status code
                return false;
            }
            JSONArray jsonArray = json.getJSONArray(KEY_GENRES);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonItem = jsonArray.getJSONObject(i);
                mapGenre.put(jsonItem.getInt(KEY_ID), jsonItem.getString(KEY_NAME));
            }
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Checks if mapGenre Map<Integer, String> is empty
     *
     * @return true if empty, false otherwise
     */
    public static boolean isMapGenreEmpty() {
        return mapGenre == null || mapGenre.isEmpty();
    }

    /**
     * Returns  String genre description value from mapGenre by the  key or null otherwise
     *
     * @return String value of genre description or null otherwise
     */
    public static String getGenre(int n) {
        if (isMapGenreEmpty()) {
            return null;
        }
        return mapGenre.get(n);
    }

    /**
     * Parse input JSON data string and makes List<ReviewItem> object
     * Returns List<ReviewItem> object or null
     *
     * @param jsonPage input JSON format file with page of movie parameters
     * @return List of ReviewItem objects if completed successfully  or null otherwise
     */
    public static List<ReviewItem> getReviewList(String jsonPage) {
        List<ReviewItem> list = new ArrayList<>();
        if (jsonPage == null || jsonPage.isEmpty()) {
            return null;
        }
        try {
            JSONObject json = new JSONObject(jsonPage);
            if (json.has(KEY_STATUS)) {
                return null;
            }
            if (json.has(KEY_PAGE) && json.has(KEY_RESULT)) {
                JSONArray jsonArray = json.getJSONArray(KEY_RESULT);
                int n = jsonArray.length();
                for (int i = 0; i < n; i++) {
                    JSONObject jsonItem = jsonArray.getJSONObject(i);
                    ReviewItem reviewItem = new ReviewItem(jsonItem);
                    if (!reviewItem.isValid()) {
                        continue;
                    }
                    list.add(reviewItem);
                }
            }
        } catch (JSONException e) {
            return null;
        }
        if (list.isEmpty()) {
            list.add(new ReviewItem());
        }
        return list;
    }
}
