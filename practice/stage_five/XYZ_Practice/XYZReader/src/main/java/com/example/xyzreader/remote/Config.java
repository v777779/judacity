package com.example.xyzreader.remote;

import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

public class Config {
    private static String TAG = Config.class.toString();

// remoteEndpointUtil
// correction!!! hardcoded url
    public static final String BASE_URL = "https://go.udacity.com/xyz-reader-json" ;

// articleListActivity
    public static final String ACTION_TIME_REFRESH = "action_time_refresh";
    public static final String ACTION_SWIPE_REFRESH = "action_swipe_refresh";
    public static final String FRAGMENT_ERROR_NAME = "fragment_error_name";
    public static final String FRAGMENT_ERROR_TAG = "fragment_error_tag";

}
