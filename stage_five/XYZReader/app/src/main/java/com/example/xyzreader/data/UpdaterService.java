package com.example.xyzreader.data;


import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.RemoteException;

import com.example.xyzreader.remote.RemoteEndpointUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.xyzreader.remote.Config.ACTION_SWIPE_REFRESH;
import static com.example.xyzreader.remote.Config.BROADCAST_ACTION_NO_NETWORK;
import static com.example.xyzreader.remote.Config.BROADCAST_ACTION_UPDATE_FINISHED;
import static com.example.xyzreader.remote.Config.BROADCAST_ACTION_UPDATE_STARTED;
import static com.example.xyzreader.remote.Config.EXTRA_EMPTY_CURSOR;
import static com.example.xyzreader.remote.Config.EXTRA_REFRESHING;
import static com.example.xyzreader.remote.Config.UPDATE_SERVICE_TAG;
import static com.example.xyzreader.remote.RemoteEndpointUtil.isCursorEmpty;
import static com.example.xyzreader.remote.RemoteEndpointUtil.isOnline;
import static com.example.xyzreader.remote.RemoteEndpointUtil.isReloadTimeout;

/**
 *  UpdateService class service for downloading and update data
 *  from the Internet to local database
 *  Service provides broadcast messages to  ArticleListActivity
 *  provides
 */
public class UpdaterService extends IntentService {

    public UpdaterService() {
        super(UPDATE_SERVICE_TAG);
    }

    /**
     *  Retrieves data from the Internet and push them onto local database
     *  OkHttp3 client used for communication
     *  Data arrived as JSON string, parsed into JasonArray and put into database
     *  Before starting download BROADCAST_ACTION_UPDATE_STARTED message sent to activity
     *  On result either BROADCAST_ACTION_NO_NETWORK or BROADCAST_ACTION_UPDATE_FINISHED
     *  sent to activity
     *  In response activity shows progress bar
     *  After data received written to database, ContentProvider is updated
     *
     * @param intent  Intent from calling activity
     */

    @Override
    protected void onHandleIntent(Intent intent) {
        boolean isUpdate = false;


        String action = intent.getAction();
        if (action != null && action.equals(ACTION_SWIPE_REFRESH)) {
            isUpdate = true;
        }

// check swipe, cursor, timeout
        boolean isCursorEmpty = isCursorEmpty(this);
        if (!isUpdate && !isCursorEmpty && !isReloadTimeout(this)) {
            return;
        }

// network update start point
        if (!isOnline(this)) {                                     // no network
            sendBroadcast(new Intent(BROADCAST_ACTION_NO_NETWORK)
                    .putExtra(EXTRA_EMPTY_CURSOR, isCursorEmpty));
            return;
        }
        sendBroadcast(new Intent(BROADCAST_ACTION_UPDATE_STARTED));         // started

        ArrayList<ContentProviderOperation> cpo = new ArrayList<ContentProviderOperation>();
        Uri dirUri = ItemsContract.Items.buildDirUri();


        cpo.add(ContentProviderOperation.newDelete(dirUri).build());  // delete for batch operation
        try {
            JSONArray array = RemoteEndpointUtil.fetchJsonArray();
            if (array == null) {
                throw new JSONException("Invalid parsed item array");
            }

            for (int i = 0; i < array.length(); i++) {
                ContentValues values = new ContentValues();
                JSONObject object = array.getJSONObject(i);
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

            RemoteEndpointUtil.saveReLoadTimePreference(this);  // save timestamp

        } catch (JSONException | RemoteException | OperationApplicationException e) {
            sendBroadcast(new Intent(BROADCAST_ACTION_NO_NETWORK)       // network error dialog
                    .putExtra(EXTRA_EMPTY_CURSOR, isCursorEmpty));

        }
        sendBroadcast(new Intent(BROADCAST_ACTION_UPDATE_FINISHED));  // finished
    }

}
