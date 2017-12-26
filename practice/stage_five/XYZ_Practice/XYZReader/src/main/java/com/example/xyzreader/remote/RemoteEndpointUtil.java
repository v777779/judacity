package com.example.xyzreader.remote;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;
import com.example.xyzreader.ui.ArticleListActivity;

import org.json.JSONArray;
import java.util.concurrent.TimeUnit;

public class RemoteEndpointUtil {
    private static final String TAG = "RemoteEndpointUtil";

    public static JSONArray getJsonArray() {
        String url = Config.BASE_URL;
        RequestFuture<JSONArray> future = RequestFuture.newFuture();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, future, future);
        jsonArrayRequest.setTag(TAG);
        VolleyQueueSingleton.getInstance().add(jsonArrayRequest);

        try {
            JSONArray jsonArray = future.get();
            return jsonArray;
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        return null;
    }

    public static boolean isCursorEmpty(Context context) {
        Cursor cursor = context.getContentResolver().query(
                ItemsContract.Items.buildDirUri(),
                ArticleLoader.Query.PROJECTION,
                null,
                null,
                ItemsContract.Items.DEFAULT_SORT);

        return cursor == null || cursor.getCount() == 0;
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

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
     * Saves mIsLoadImages flag to Preferences
     * Used by Retrofit downloader to store time when data was loaded
     */
    public static void saveReLoadTimePreference(Context context) {
        Resources res = context.getResources();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int currentTimeSec = (int) TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());  // to seconds
        editor.putInt(res.getString(R.string.pref_reload_time_key), currentTimeSec);
        editor.apply();
    }


}

