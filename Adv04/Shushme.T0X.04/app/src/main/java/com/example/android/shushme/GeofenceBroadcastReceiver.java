package com.example.android.shushme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by V1 on 03-Nov-17.
 */

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = GeofenceBroadcastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, context.getString(R.string.receiver_message));
    }
}
