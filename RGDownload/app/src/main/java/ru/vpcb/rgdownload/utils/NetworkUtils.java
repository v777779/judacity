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
    final static String GITHUB_BASE_URL =
            "https://api.github.com/search/repositories";
    private static final String MOVIE_BASE = "https://api.themoviedb.org/3/movie/";
    private static final String[] MOVIE_QUERY = "popular,now_playing,top_rated".split(",");
    private static final String MOVIE_KEY = "?api_key=" + BuildConfig.MOVIE_DB_API_KEY;
    private static final String MOVIE_LANG = "&language=";
    private static final String MOVIE_PAGE = "&page=";

    private static Uri getURI(int type, int page, String lang) {
        if (type < 0 || type >= MOVIE_QUERY.length || page < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (page < 0) {
            throw new IllegalArgumentException();
        }
        if (lang == null || lang.length() == 0) {
            lang = "en_US";
        }
        return Uri.parse(MOVIE_BASE + MOVIE_QUERY[type] + MOVIE_KEY + MOVIE_LANG + lang + MOVIE_PAGE + page);
    }

    /**
     * @param type search query for MovieData Database
     * @param page page number for MovieData Database
     * @param lang language for MovieData Database  for null used default en_US value
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
    public static String getResponseFromHttpUrl(URL url) throws IOException,NetworkOnMainThreadException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

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