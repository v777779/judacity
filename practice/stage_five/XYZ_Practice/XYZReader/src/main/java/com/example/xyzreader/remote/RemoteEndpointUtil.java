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
import com.example.xyzreader.data.ItemModel;
import com.example.xyzreader.data.ItemsContract;
import com.example.xyzreader.ui.ArticleListActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RemoteEndpointUtil {
    private static final String TAG = "RemoteEndpointUtil";


//    private RemoteEndpointUtil() {
//    }
//
//
//    public static JSONArray fetchJsonArray() {
//        String itemsJson = null;
//        try {
//            itemsJson = fetchPlainText(Config.BASE_URL);
//        } catch (IOException e) {
//            Log.e(TAG, "Error fetching items JSON", e);
//            return null;
//        }
//
//        // Parse JSON
//        try {
//            JSONTokener tokener = new JSONTokener(itemsJson);
//            Object val = tokener.nextValue();
//            if (!(val instanceof JSONArray)) {
//                throw new JSONException("Expected JSONArray");
//            }
//            return (JSONArray) val;
//        } catch (JSONException e) {
//            Log.e(TAG, "Error parsing items JSON", e);
//        }
//
//        return null;
//    }
//
//    static String fetchPlainText(URL url) throws IOException {
//        OkHttpClient client = new OkHttpClient();
//
//        Request request = new Request.Builder()
//                .url(url)
//                .build();
//
//        Response response = client.newCall(request).execute();
//        String result = "";
//        if (!response.isSuccessful()) return result;
//
//        result = response.body().string();
//        response.close();
//        return result;
//    }
//
//    private static String mResult;
//
//    static String fetchPlainTextE(URL url) throws IOException {
//        OkHttpClient client = new OkHttpClient();
//
//        final Request request = new Request.Builder()
//                .url(url)
//                .build();
//
//        mResult = "";
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                mResult = response.body().string();
//                response.close();
//            }
//        });
//
//        while (mResult.isEmpty()) {
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        return mResult;
//    }
//
//
//    static String fetchPlainTextN(URL url) throws IOException {
//        return NetworkUtils.getResponseFromHttpUrl(url);
//    }
//
//
//    static String fetchPlainTextV(URL url) throws IOException {
//// prepare the Request
//        mResult = "";
//
//        String urls = "https://go.udacity.com/xyz-reader-json";
//        StringRequest getRequest = new StringRequest(
//                com.android.volley.Request.Method.GET,
//                urls,
//                new com.android.volley.Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        // display response
////                        Log.d("Response", response.toString());
//                        mResult = response;
//
//                    }
//                },
//                new com.android.volley.Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError response) {
////                        Log.d("Error.Response", "ee");
//                        mResult = response.getMessage();
//                    }
//                }
//        );
////        RequestQueue queue = mVVolley.newRequestQueue(ArticleListActivity.context);
//// add it to the RequestQueue
////        queue.add(getRequest);
//        mVolleyQueue.add(getRequest);
//
//        while (mResult.isEmpty()) {
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//
//        return mResult;
//    }

    public static JSONArray getJsonArray() {
        String url = Config.BASE_URL.toString();
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


    public static final String RECIPES_BASE = "https://go.udacity.com/";
    public static final String RECIPES_QUERY = "xyz-reader-json";
    private Retrofit mRetrofit;
    private IRetrofitAPI mRetrofitAPI;

    public static List<ItemModel>  startRetrofitLoader() {
// setup Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RECIPES_BASE) //Базовая часть адреса
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        IRetrofitAPI retrofitAPI = retrofit.create(IRetrofitAPI.class);
        try {
            retrofit2.Response<List<ItemModel>> response = retrofitAPI.getData(null).execute();
            return response.body();
        } catch (Exception e) {
            Log.d(TAG, "retrofit failure: " + e.getMessage());
        }
        return null;

    }

}

