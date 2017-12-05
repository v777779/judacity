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

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;


import static ru.vpcb.popularmovie.utils.Constants.*;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 29-Sep-17
 * Email: vadim.v.voronov@gmail.com
 */

public class NetworkUtils {

    /**
     * Returns Uri address from query parameters
     */
    private static Uri getURI(int type, int page, int id) {
        switch (type) {
            case 0:
            case 1:
                return Uri.parse(MOVIE_BASE + MOVIE_QUERY[type] + MOVIE_KEY + MOVIE_LANG + DEFAULT_LANGUAGE + MOVIE_PAGE + page);
            case 2:
                return null; //"placeholder for favorites"
            case 3:
                return Uri.parse(MOVIE_BASE + MOVIE_QUERY[type] + MOVIE_KEY + MOVIE_LANG + DEFAULT_LANGUAGE + MOVIE_PAGE + page);
            case 4:
                return Uri.parse(MOVIE_BASE + MOVIE_QUERY[type] + MOVIE_KEY + MOVIE_LANG + DEFAULT_LANGUAGE);
            case 5:
                String sMovieQuery = MOVIE_QUERY[type].replace("*id*", "" + id);
                return Uri.parse(MOVIE_BASE + sMovieQuery + MOVIE_KEY + MOVIE_LANG + DEFAULT_LANGUAGE + MOVIE_PAGE + page);
            case 6:
                String sTrailerQuery = MOVIE_QUERY[type].replace("*id*", "" + id);
                return Uri.parse(MOVIE_BASE + sTrailerQuery + MOVIE_KEY + MOVIE_LANG + DEFAULT_LANGUAGE);
            case 7:
                sMovieQuery = MOVIE_BASE.substring(0, MOVIE_BASE.length() - 3); // just access to site
                return Uri.parse(sMovieQuery);
            default:
                return null;
        }
    }

    /**
     * Returns URL address from query parameters
     */
    public static URL buildUrl(int type, int page, int id) {
        try {
            Uri uri = getURI(type, page, id);
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

    /**
     * Returns the current state of Internet access
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /**
     * Puts bundle with parameters for loader to Stack<Bundle> based on ConcurrentLinkedQueue<Bundle>
     *
     * @param bundleStack stack for parameters
     * @param type        int type of query
     * @param moviePage   int page naumber
     * @param movieId     int id of movie
     */
    public static void putLoaderQuery(ConcurrentLinkedQueue<Bundle> bundleStack, int type, int moviePage, int movieId) {
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_LOADER_QUERY_ID, type);
        bundle.putInt(BUNDLE_LOADER_PAGE_ID, moviePage);
        bundle.putInt(BUNDLE_LOADER_MOVIE_ID, movieId);
        bundleStack.offer(bundle);
    }
}