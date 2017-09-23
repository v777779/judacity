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
package ru.vpcb.rgdownload.utils;

import android.net.Uri;
import android.os.NetworkOnMainThreadException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import ru.vpcb.rgdownload.BuildConfig;

/**
 * These utilities will be used to communicate with the network.
 */
public class NetworkUtils {

    private static final String MOVIE_BASE = "https://api.themoviedb.org/3/";
    private static final String[] MOVIE_QUERY = (
                      "movie/popular,"
                    + "movie/now_playing,"
                    + "movie/top_rated,"
                    + "genre/movie/list")
            .split(",");
    private static final String MOVIE_KEY = "?api_key=" + BuildConfig.MOVIE_DB_API_KEY;
    private static final String MOVIE_LANG = "&language=";
    private static final String MOVIE_PAGE = "&page=";


    private static Uri getURI(int type, int page, String lang) {
        if (type < 0 || type >= MOVIE_QUERY.length) {
            throw new IndexOutOfBoundsException();
        }
        if (page < 0) {
            throw new IllegalArgumentException();
        }
        if (lang == null || lang.length() == 0) {
            lang = "en_US";
        }
        if (page > 0) {
            return Uri.parse(MOVIE_BASE + MOVIE_QUERY[type] + MOVIE_KEY + MOVIE_LANG + lang + MOVIE_PAGE + page);
        } else {
            return Uri.parse(MOVIE_BASE + MOVIE_QUERY[type] + MOVIE_KEY + MOVIE_LANG + lang);
        }
    }


    /**
     * @param type search query for MovieItem Database
     * @param page page number for MovieItem Database
     * @param lang language for MovieItem Database  for null used default en_US value
     * @return URL object or null
     */
    public static URL buildUrl(int type, int page, String lang) {
        URL url = null;
        try {
            url = new URL(getURI(type, page, lang).toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }


    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
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