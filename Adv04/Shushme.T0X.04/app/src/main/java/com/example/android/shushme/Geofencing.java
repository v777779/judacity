package com.example.android.shushme;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;

import java.util.ArrayList;

/**
 * Created by V1 on 03-Nov-17.
 */

public class Geofencing implements ResultCallback {
    private static final String TAG = Geofencing.class.getSimpleName();

    private static final long GEOFENCE_TIMEOUT = 24 * 60 * 60 * 1000; // ms  24hrs,  NEVER -1
    private static final long GEOFENCE_RADIUS = 50; // meters

    private GoogleApiClient mClient;
    private Context mContext;
    private PendingIntent mGeofencePendingIntent;
    private ArrayList<Geofence> mGeofenceList;

    public Geofencing( Context context, GoogleApiClient client) {
        mContext = context;
        mClient = client;
        mGeofencePendingIntent = null;
        mGeofenceList = new ArrayList<>();
    }

    public void updateGeofenceList(PlaceBuffer places) {
        mGeofenceList = new ArrayList<>();
        if (places == null || places.getCount() == 0) return;
        for (Place place : places) {

            String placeId = place.getId();
            double placeLat = place.getLatLng().latitude;
            double placeLng = place.getLatLng().longitude;
            Geofence geofence = new Geofence.Builder()
                    .setRequestId(placeId)
                    .setExpirationDuration(GEOFENCE_TIMEOUT)
                    .setCircularRegion(placeLat, placeLng, GEOFENCE_RADIUS)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER
                            | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build();
            mGeofenceList.add(geofence);
        }
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencingPendingIntent() {
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }

        Intent intent = new Intent(mContext, GeofenceBroadcastReceiver.class);
        mGeofencePendingIntent = PendingIntent.getBroadcast(mContext,
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }

    public void registerAllGeofencies() {
        if (mClient == null || !mClient.isConnected() ||
                mGeofenceList == null || mGeofenceList.size() == 0) {
            return;
        }
        try {
            LocationServices.GeofencingApi.addGeofences(
                    mClient,
                    getGeofencingRequest(),
                    getGeofencingPendingIntent()
            ).setResultCallback(this);
        } catch (SecurityException e) {
            Log.e(TAG, mContext.getString(R.string.geofence_exception, e.getMessage()));
        }

    }

    public void unRegisterAllGeofencies() {
        if (mClient == null || !mClient.isConnected()) {
            return;
        }
        try {
            LocationServices.GeofencingApi.removeGeofences(
                    mClient,
                    getGeofencingPendingIntent()
            ).setResultCallback(this);
        } catch (SecurityException e) {
            Log.e(TAG, mContext.getString(R.string.geofence_exception, e.getMessage()));
        }

    }

    @Override
    public void onResult(@NonNull Result result) {
        Log.e(TAG, mContext.getString(R.string.geofence_error,
                result.getStatus().toString()));
    }
}
