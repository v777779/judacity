package com.example.xyzreader.copy;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.RemoteException;
import android.text.format.Time;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.xyzreader.data.ItemsContract;
import com.example.xyzreader.remote.Config;
import com.example.xyzreader.remote.VolleyQueueSingleton;
import com.example.xyzreader.ui.ArticleListActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UpdaterService2 extends IntentService implements Response.Listener<JSONArray>,
        Response.ErrorListener {
    private static final String TAG = "UpdaterService";

    public static final String BROADCAST_ACTION_STATE_CHANGE
            = "com.example.xyzreader.intent.action.STATE_CHANGE";
    public static final String EXTRA_REFRESHING
            = "com.example.xyzreader.intent.extra.REFRESHING";

    public UpdaterService2() {
        super(TAG);
    }

//    @Override
//    protected void onHandleIntent(Intent intent) {
//        Time time = new Time();
//
//        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
//        NetworkInfo ni = cm.getActiveNetworkInfo();
//        if (ni == null || !ni.isConnected()) {
//            Log.w(TAG, "Not online, not refreshing.");
//            return;
//        }
//
//        sendStickyBroadcast(
//                new Intent(BROADCAST_ACTION_STATE_CHANGE).putExtra(EXTRA_REFRESHING, true));
//
//        // Don't even inspect the intent, we only do one thing, and that's fetch content.
//        ArrayList<ContentProviderOperation> cpo = new ArrayList<ContentProviderOperation>();
//
//        Uri dirUri = ItemsContract.Items.buildDirUri();
//
//        // Delete all items
//        cpo.add(ContentProviderOperation.newDelete(dirUri).build());
//        getJSONArray();
//
//        try {
//            JSONArray array = RemoteEndpointUtil.fetchJsonArray();
//            if (array == null) {
//                throw new JSONException("Invalid parsed item array" );
//            }
//
//            for (int i = 0; i < array.length(); i++) {
//                ContentValues values = new ContentValues();
//                JSONObject object = array.getJSONObject(i);
//                values.put(ItemsContract.Items.SERVER_ID, object.getString("id" ));
//                values.put(ItemsContract.Items.AUTHOR, object.getString("author" ));
//                values.put(ItemsContract.Items.TITLE, object.getString("title" ));
//                values.put(ItemsContract.Items.BODY, object.getString("body" ));
//                values.put(ItemsContract.Items.THUMB_URL, object.getString("thumb" ));
//                values.put(ItemsContract.Items.PHOTO_URL, object.getString("photo" ));
//                values.put(ItemsContract.Items.ASPECT_RATIO, object.getString("aspect_ratio" ));
//                values.put(ItemsContract.Items.PUBLISHED_DATE, object.getString("published_date"));
//                cpo.add(ContentProviderOperation.newInsert(dirUri).withValues(values).build());
//            }
//
//            getContentResolver().applyBatch(ItemsContract.CONTENT_AUTHORITY, cpo);
//
//        } catch (JSONException | RemoteException | OperationApplicationException e) {
//            Log.e(TAG, "Error updating content.", e);
//        }
//
//        sendStickyBroadcast(
//                new Intent(BROADCAST_ACTION_STATE_CHANGE).putExtra(EXTRA_REFRESHING, false));
//    }

//    @Override
//    protected void onHandleIntent(Intent intent) {
//        Time time = new Time();
//
//        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
//        NetworkInfo ni = cm.getActiveNetworkInfo();
//        if (ni == null || !ni.isConnected()) {
//            Log.w(TAG, "Not online, not refreshing.");
//            return;
//        }
//
//        sendBroadcast(
//                new Intent(BROADCAST_ACTION_STATE_CHANGE).putExtra(EXTRA_REFRESHING, true));
//
//        // Don't even inspect the intent, we only do one thing, and that's fetch content.
//        ArrayList<ContentProviderOperation> cpo = new ArrayList<ContentProviderOperation>();
//        Uri dirUri = ItemsContract.Items.buildDirUri();
//
//        // Delete all items
//        cpo.add(ContentProviderOperation.newDelete(dirUri).build());  // delete for batch operation
//// error!!!  замена JsonNArray()
//        try {
//            JSONArray array = RemoteEndpointUtil.getJsonArray();   // null if no result
//            if (array == null) {
//                throw new JSONException("Invalid parsed item array" );
//            }
//
//            for (int i = 0; i < array.length(); i++) {
//                ContentValues values = new ContentValues();
//                JSONObject object = array.getJSONObject(i);
//                values.put(ItemsContract.Items.SERVER_ID, object.getString("id" ));
//                values.put(ItemsContract.Items.AUTHOR, object.getString("author" ));
//                values.put(ItemsContract.Items.TITLE, object.getString("title" ));
//                values.put(ItemsContract.Items.BODY, object.getString("body" ));
//                values.put(ItemsContract.Items.THUMB_URL, object.getString("thumb" ));
//                values.put(ItemsContract.Items.PHOTO_URL, object.getString("photo" ));
//                values.put(ItemsContract.Items.ASPECT_RATIO, object.getString("aspect_ratio" ));
//                values.put(ItemsContract.Items.PUBLISHED_DATE, object.getString("published_date"));
//                cpo.add(ContentProviderOperation.newInsert(dirUri).withValues(values).build());
//            }
//
//            getContentResolver().applyBatch(ItemsContract.CONTENT_AUTHORITY, cpo);
//
//        } catch (JSONException | RemoteException | OperationApplicationException e) {
//            Log.e(TAG, "Error updating content.", e);
//        }
//
//        sendBroadcast(
//                new Intent(BROADCAST_ACTION_STATE_CHANGE).putExtra(EXTRA_REFRESHING, false));
//    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Time time = new Time();

        if(!isOnline(this)) {
            Log.w(TAG, "Not online, not refreshing.");
            return;
        }
        initResponse();
    }


    private void initResponse() {
        sendBroadcast(
                new Intent(BROADCAST_ACTION_STATE_CHANGE).putExtra(EXTRA_REFRESHING, true));
        String url = Config.BASE_URL.toString();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, this, this);
        jsonArrayRequest.setTag(ArticleListActivity.TAG);
        VolleyQueueSingleton.getInstance().add(jsonArrayRequest);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        Log.e(TAG, "Error updating content. " + volleyError.getMessage());
        sendBroadcast(
                new Intent(BROADCAST_ACTION_STATE_CHANGE).putExtra(EXTRA_REFRESHING, false));
    }

    @Override
    public void onResponse(JSONArray jsonArray) {
        try {
            if (jsonArray == null) {
                throw new JSONException("Invalid parsed item array");
            }

            ArrayList<ContentProviderOperation> cpo = new ArrayList<ContentProviderOperation>();
            Uri dirUri = ItemsContract.Items.buildDirUri();
            cpo.add(ContentProviderOperation.newDelete(dirUri).build());  // delete for batch operation

            for (int i = 0; i < jsonArray.length(); i++) {
                ContentValues values = new ContentValues();
                JSONObject object = jsonArray.getJSONObject(i);
                values.put(ItemsContract.Items.SERVER_ID, object.getString("id"));
                values.put(ItemsContract.Items.AUTHOR, object.getString("author"));
                values.put(ItemsContract.Items.TITLE, object.getString("title"));
                values.put(ItemsContract.Items.BODY, object.getString("body"));
                values.put(ItemsContract.Items.THUMB_URL, object.getString("thumb"));
                values.put(ItemsContract.Items.PHOTO_URL, object.getString("photo"));
                values.put(ItemsContract.Items.ASPECT_RATIO, object.getString("aspect_ratio"));
                values.put(ItemsContract.Items.PUBLISHED_DATE, object.getString("published_date"));
                cpo.add(ContentProviderOperation.newInsert(dirUri).withValues(values).build());
            }
            getContentResolver().applyBatch(ItemsContract.CONTENT_AUTHORITY, cpo);

        } catch (JSONException | RemoteException | OperationApplicationException e) {
            Log.e(TAG, "Error updating content.", e);
        }

        sendBroadcast(
                new Intent(BROADCAST_ACTION_STATE_CHANGE).putExtra(EXTRA_REFRESHING, false));

    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}
