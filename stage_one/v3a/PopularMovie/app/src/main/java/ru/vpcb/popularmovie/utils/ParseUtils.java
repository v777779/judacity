package ru.vpcb.popularmovie.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.vpcb.popularmovie.MovieItem;
import ru.vpcb.popularmovie.ReviewItem;

import static ru.vpcb.popularmovie.utils.Constants.KEY_GENRES;
import static ru.vpcb.popularmovie.utils.Constants.KEY_ID;
import static ru.vpcb.popularmovie.utils.Constants.KEY_NAME;
import static ru.vpcb.popularmovie.utils.Constants.KEY_PAGE;
import static ru.vpcb.popularmovie.utils.Constants.KEY_PAGE_TOTAL;
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
     * JSON Movie Data Response format
     * Response 200
     * Page Format
     * page
     * total_results
     * total_pages
     * results[]
     * <p>
     * Response 401
     * status message
     * success :false
     * status code:7
     * <p>
     * Response 404
     * status message
     * status code:34
     * <p>
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
            if (json.has(KEY_STATUS)) {            // status code
                return null;
            }

            if (json.has(KEY_PAGE) && json.has(KEY_RESULT)) {  // parse results
//                int page = json.getInt(KEY_PAGE); // number of page
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
     * Checks input JSON string for valid data
     * Parse input JSON data string and extract page number
     * Returns page number  if completed successfully or -1 otherwise
     *
     * @param jsonPage input JSON format file with page of movie parameters
     * @return int  page number  if completed successfully or -1 otherwise
     */
    public static int getPageNumber(String jsonPage) {
        int n = -1;
        if (jsonPage == null || jsonPage.isEmpty()) {
            return n;
        }
        try {
            JSONObject json = new JSONObject(jsonPage);
            if (json.has(KEY_STATUS)) {            // status code
                return n;
            }
            n = json.getInt(KEY_PAGE);             // number of page
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return n;
    }


    /**
     * Checks input JSON string for valid data
     * Parse input JSON data string and extract total pages
     * Returns total pages if completed successfully or -1 otherwise
     *
     * @param jsonPage input JSON format file with page of movie parameters
     * @return int total pages if completed successfully or -1 otherwise
     */
    public static int getPageTotal(String jsonPage) {
        int n = -1;
        if (jsonPage == null || jsonPage.isEmpty()) {
            return n;
        }
        try {
            JSONObject json = new JSONObject(jsonPage);
            if (json.has(KEY_STATUS) && json.getInt(KEY_STATUS) != 200) {            // status code
                return n;
            }
            n = json.getInt(KEY_PAGE_TOTAL);             // number of page
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return n;
    }

    /**
     * Checks input JSON string for valid data
     * Parse input JSON data string and extract number of elements of results array
     * Returns number of elements of results array if completed successfully or -1 otherwise
     *
     * @param jsonPage input JSON format file with page of movie parameters
     * @return int number of elements of results array if completed successfully or -1 otherwise
     */
    public static int getItemTotal(String jsonPage) {
        int n = -1;
        if (jsonPage == null || jsonPage.isEmpty()) {
            return n;
        }
        try {
            JSONObject json = new JSONObject(jsonPage);
            if (json.has(KEY_STATUS)) {            // status code
                return n;
            }
            n = json.getJSONArray(KEY_RESULT).length();             // number of items
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return n;
    }

    /**
     * Checks input JSON string for status
     * Parse input JSON data string and extract status code
     * Returns status code if completed successfully or -1 otherwise
     *
     * @param jsonPage input JSON format file with page of movie parameters
     * @return int status code if completed successfully or -1 otherwise
     */

    public static int getStatusCode(String jsonPage) {
        int n = -1;
        if (jsonPage == null || jsonPage.isEmpty()) {
            return n;
        }
        try {
            JSONObject json = new JSONObject(jsonPage);
            n = json.getInt(KEY_STATUS);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return n;
    }

    /**
     * Checks input JSON string for status
     * Parse input JSON data string and extract genre elements from results array
     * Creates mapGenre and fills it with data from elements
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
     * Returns mapGenre key value
     * Checks if mapGenre Map<Integer, String> is empty
     * Returns  string value of genre description or null otherwise
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
     * Checks input JSON string for valid data
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
            if (json.has(KEY_STATUS)) {            // status code
                return null;
            }

            if (json.has(KEY_PAGE) && json.has(KEY_RESULT)) {  // parse results
//                int page = json.getInt(KEY_PAGE); // number of page
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

        if (list.isEmpty()) {   // prevent future loading
            list.add(new ReviewItem());
        }
        return list;
    }


}
