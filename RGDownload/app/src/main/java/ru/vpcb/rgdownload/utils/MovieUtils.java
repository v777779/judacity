package ru.vpcb.rgdownload.utils;

import android.content.Intent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.vpcb.rgdownload.MovieItem;

/**
 * Created by V1 on 23-Sep-17.
 */

public class MovieUtils {
    private static final String KEY_STATUS = "status_code";
    private static final String KEY_PAGE = "page";
    private static final String KEY_PAGE_TOTAL = "total_pages";
    private static final String KEY_RESULT = "results";
    private static final String KEY_GENRES = "genres";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";

    private static Map<Integer, String> mapGenre;


    /**
     * Response 200
     * Page Format
     * page
     * total_results
     * totap_pages
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
     *
     * @param jsonPage
     * @return
     */
    public static List<MovieItem> getPageList(String jsonPage) {
        List<MovieItem> list = new ArrayList<>();
        try {
            JSONObject json = new JSONObject(jsonPage);
            if (json.has(KEY_STATUS)) {            // status code
                return null;
            }

            if (json.has(KEY_PAGE) && json.has(KEY_RESULT)) {  // parse results
                int page = json.getInt(KEY_PAGE); // number of page
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

    public static int getPageNumber(String jsonPage) {
        int n = -1;
        try {
            JSONObject json = new JSONObject(jsonPage);
            if (json.has(KEY_STATUS)) {            // status code
                return n;
            }
            n = json.getInt(KEY_PAGE);             // number of page
        } catch (JSONException e) {

        }
        return n;
    }


    public static int getPageTotal(String jsonPage) {
        int n = -1;
        try {
            JSONObject json = new JSONObject(jsonPage);
            if (json.has(KEY_STATUS) && json.getInt(KEY_STATUS) != 200) {            // status code
                return n;
            }
            n = json.getInt(KEY_PAGE_TOTAL);             // number of page
        } catch (JSONException e) {

        }
        return n;
    }

    public static int getItemTotal(String jsonPage) {
        int n = -1;
        try {
            JSONObject json = new JSONObject(jsonPage);
            if (json.has(KEY_STATUS)) {            // status code
                return n;
            }
            n = json.getJSONArray(KEY_RESULT).length();             // number of items
        } catch (JSONException e) {

        }
        return n;
    }

    public static int getStatusCode(String jsonPage) {
        int n = -1;
        try {
            JSONObject json = new JSONObject(jsonPage);
            n = json.getInt(KEY_STATUS);

        } catch (JSONException e) {
        }
        return n;
    }

    public static boolean setGenres(String jsonPage) {
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
        } catch (JSONException e) {
            return false;
        }
        return true;
    }


    public static String getGenre(int n) {
        if (mapGenre == null || mapGenre.size() == 0) {
            return null;
        }
        return mapGenre.get(n);
    }

    public static boolean isMapGenreEmpty () {
        if(mapGenre == null || mapGenre.isEmpty()) {
            return true;
        }
        return false;
    }


}
