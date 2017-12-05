/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.vpcb.popularmovie.utils;

import android.net.Uri;
import android.os.NetworkOnMainThreadException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import ru.vpcb.popularmovie.MovieTask;

import static ru.vpcb.popularmovie.utils.Constants.MOVIE_BASE;
import static ru.vpcb.popularmovie.utils.Constants.MOVIE_KEY;
import static ru.vpcb.popularmovie.utils.Constants.MOVIE_LANG;
import static ru.vpcb.popularmovie.utils.Constants.MOVIE_PAGE;
import static ru.vpcb.popularmovie.utils.Constants.MOVIE_QUERY;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 29-Sep-17
 * Email: vadim.v.voronov@gmail.com
 */

public class NetworkUtils {

    /**
     * Builds Uri address from NetworkData object with query parameters
     *
     * @param networkData object with query parameters like type POPULAR, TOPRATED, page number etc.
     * @return Uri address of request to tmdb.com
     */
    private static Uri getURI(NetworkData networkData) {
        int type = networkData.getType();
        int page = networkData.getPage();
        String lang = networkData.getLang();
        switch (type) {
            case 0:
            case 1:
            case 2:
                return Uri.parse(MOVIE_BASE + MOVIE_QUERY[type] + MOVIE_KEY + MOVIE_LANG + lang + MOVIE_PAGE + page);
            case 3:
                return Uri.parse(MOVIE_BASE + MOVIE_QUERY[type] + MOVIE_KEY + MOVIE_LANG + lang);
            case 4:
                String sMovieQuery = MOVIE_QUERY[type].replace("*id*", "" + networkData.getId());
                return Uri.parse(MOVIE_BASE + sMovieQuery + MOVIE_KEY + MOVIE_LANG + lang + MOVIE_PAGE + page);
            case 5:
                sMovieQuery = MOVIE_BASE.substring(0, MOVIE_BASE.length() - 3); // just access to site
                return Uri.parse(sMovieQuery);
            default:
                return null;
        }
    }

    /**
     * Builds URL address from Uri address
     *
     * @param networkData NetworkData object with query parameters
     * @return URL address or null
     */
    private static URL buildUrl(NetworkData networkData) {
        try {
            Uri uri = getURI(networkData);
            if (uri == null) {
                return null;
            }
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *  Loads data from IMDb site using AsyncTask object
     *
     * @param networkData NetworkData class object with parameters of query
     * @return  JSON string of loaded data if successfully completed or null
     * @throws Exception from Async Task
     */
    public static String makeSearch(NetworkData networkData) throws Exception {

        URL url = NetworkUtils.buildUrl(networkData);
        if (url == null) {
            return null;
        }
        return new MovieTask(networkData.getContext()).execute(url).get();
    }

    /**
     * Returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException or Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException, NetworkOnMainThreadException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            int code = urlConnection.getResponseCode();
            InputStream in;
            if (code == 200) {
                in = urlConnection.getInputStream();
            } else {
                in = urlConnection.getErrorStream();
            }
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();

            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

}