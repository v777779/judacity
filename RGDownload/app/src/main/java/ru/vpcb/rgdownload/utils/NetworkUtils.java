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

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.NetworkOnMainThreadException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import ru.vpcb.rgdownload.BuildConfig;
import ru.vpcb.rgdownload.MovieTask;

/**
 * These utilities will be used to communicate with the network.
 */
public class NetworkUtils {
    private static final String MOVIE_BASE = "https://api.themoviedb.org/3/";
    private static final String[] MOVIE_QUERY = {
            "movie/popular",
            "movie/now_playing",
            "movie/top_rated",
            "genre/movie/list",
            "movie/*id*/reviews",
            "empty"

    };

    private static final String MOVIE_KEY = "?api_key=" + BuildConfig.MOVIE_DB_API_KEY;
    private static final String MOVIE_LANG = "&language=";
    private static final String MOVIE_PAGE = "&page=";


    private static Uri getURI(NetworkData networkData) {
        int type = networkData.getType();
        int page = networkData.getPage();
        String lang = networkData.getLang();
        switch (type) {
            case 0:
            case 1:
            case 2:
                Uri.parse(MOVIE_BASE + MOVIE_QUERY[type] + MOVIE_KEY + MOVIE_LANG + lang + MOVIE_PAGE + page);
            case 3:
                return Uri.parse(MOVIE_BASE + MOVIE_QUERY[type] + MOVIE_KEY + MOVIE_LANG + lang);
            case 4:
                String sMovieQuery = MOVIE_QUERY[type].replace("*id*", "" + networkData.getId());
                return Uri.parse(MOVIE_BASE + sMovieQuery + MOVIE_KEY + MOVIE_LANG + lang + MOVIE_PAGE + page);
            case 5:
                sMovieQuery = MOVIE_BASE.substring(0,MOVIE_BASE.length()-3); // just access to site
                return Uri.parse(sMovieQuery);
            default:
                return null;
        }
    }


    /**
     * @param networkData NetworkData object with parameters of query
     * @return URL object or null
     */
    public static URL buildUrl(NetworkData networkData) {
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
     *
     * @param networkData  NetworkData class object with parameters of query
     * @return  String object if success or null in other case
     * @throws Exception
     */
    public static String makeSearch(NetworkData networkData) throws Exception {

        URL url = NetworkUtils.buildUrl(networkData);
        if(url == null) {
            return  null;
        }
        return new MovieTask().execute(url).get();
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