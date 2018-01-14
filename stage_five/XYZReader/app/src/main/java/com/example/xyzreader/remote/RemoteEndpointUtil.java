package com.example.xyzreader.remote;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;

public class RemoteEndpointUtil {
    /**
     *  Returns JSONArray object
     *  Calls fetchPlainText(BASE_URL) method which returns String with JSON data.
     *  Creates JSONArray object from that string and returns it.
     *
     * @return JSONArray object
     */
    public static JSONArray fetchJsonArray() {
        try {
            JSONArray jsonArray = new JSONArray(fetchPlainText(Config.BASE_URL));
            return jsonArray;
        } catch (IOException |JSONException e) {
            Timber.d("Error parsing items JSON: "+ e);
        }
        return null;
    }

    /**
     *  Returns String with JSONArray data
     *  Downloads  string from address provided to method as parameter
     *  Returns String object
     * @param address String URL from string downloaded
     * @return String downloaded result
     * @throws IOException
     */
    static String fetchPlainText(String address) throws IOException {
        URL url = new URL(address );
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        String result = "";
        if (!response.isSuccessful()) return result;

        result = response.body().string();
        response.close();
        return result;
    }

    /**
     *  Returns true if cursor object from given activity is empty
     *
     * @param context Context context of calling activity
     * @return boolean true is cursor object is empty or false in other case
     */
    public static boolean isCursorEmpty(Context context) {
        Cursor cursor = context.getContentResolver().query(
                ItemsContract.Items.buildDirUri(),
                ArticleLoader.Query.PROJECTION,
                null,
                null,
                ItemsContract.Items.DEFAULT_SORT);

        boolean isEmpty = cursor == null || cursor.getCount() == 0;
        if(cursor != null) cursor.close();
        return isEmpty;
    }

    /**
     *  Returns true if network access is present
     *
     * @param context Context context of calling activity
     * @return boolean true if network is present.
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }



// preferences


    /**
     *  Return true if current time data in content provider  is obsoleted
     *  The delay set in shared preferences and equals to 60 min by default
     *  In current version it's possible to change this value via sources only.
     *
     * @param context Context context of calling activity
     * @return boolean true if data is obsoleted
     */
    public static boolean isReloadTimeout(Context context) {
        Resources res = context.getResources();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int lastTimeSec = sharedPreferences.getInt(res.getString(R.string.pref_reload_time_key),
                res.getInteger(R.integer.pref_reload_time_default));
        long lastTime = TimeUnit.SECONDS.toMillis(lastTimeSec);  // seconds to milliseconds

        int delayTimeMin = sharedPreferences.getInt(res.getString(R.string.pref_reload_delay_key),
                res.getInteger(R.integer.pref_reload_delay_default));

        long delayTime = TimeUnit.MINUTES.toMillis(delayTimeMin); // minutes to milliseconds

        return (System.currentTimeMillis() - lastTime) > delayTime;
    }

    /**
     * Saves current time to preferences
     * Used by UpdaterService to update last downloading time
     */
    public static void saveReLoadTimePreference(Context context) {
        Resources res = context.getResources();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int currentTimeSec = (int) TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());  // to seconds
        editor.putInt(res.getString(R.string.pref_reload_time_key), currentTimeSec);
        editor.apply();
    }


    /**
     *  Return boolean state of default swipe refresh mode.
     *  Loads value from shared preferences and returns.
     *  Swipe refresh is enabled by default.
     *  Can be changed via setting menu.
     *
     * @param context Context context of calling activity
     * @return boolean true if data is obsoleted
     */
    public static boolean  loadPreferenceSwipe(Context context) {
        Resources res = context.getResources();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isSwipeEnabled = sharedPreferences.getBoolean(
                res.getString(R.string.pref_swipe_mode_key),
                res.getBoolean(R.bool.pref_swipe_mode_default));

        return isSwipeEnabled;
    }


    /**
     *  Return boolean state of default full screen mode.
     *  Loads value from shared preferences and returns.
     *  Full screen is disabled  by default.
     *  Can be changed via setting menu.
     *
     * @param context Context context of calling activity
     * @return boolean true if data is obsoleted
     */
    public static boolean  loadPreferenceFullScreen(Context context) {
        Resources res = context.getResources();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isFullScreenEnabled = sharedPreferences.getBoolean(
                res.getString(R.string.pref_full_screen_key),
                res.getBoolean(R.bool.pref_full_screen_default));

        return isFullScreenEnabled;
    }


    /**
     *  Saves all preferences to Preferences
     */
    public static void savePreferences(Context context, boolean isSwipeEnabled, boolean isFullScreenEnabled) {
        Resources res = context.getResources();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(res.getString(R.string.pref_swipe_mode_key), isSwipeEnabled);
        editor.putBoolean(res.getString(R.string.pref_full_screen_key), isFullScreenEnabled);
        editor.apply();
    }

}

