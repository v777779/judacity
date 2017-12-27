package com.example.xyzreader.remote;


import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by V1 on 25-Dec-17.
 */

public class VolleyQueueSingleton {
    private static VolleyQueueSingleton mInstance;
    private RequestQueue mRequestQueue;
    private static Context mContext;


    private VolleyQueueSingleton(Context  context) {
        mContext = context;
        mRequestQueue = getRequestQueue();

    }

    public static  synchronized VolleyQueueSingleton getInstance(Context context) {
        if(mInstance == null) {
            mInstance = new VolleyQueueSingleton(context.getApplicationContext());
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if(mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext);
        }
        return mRequestQueue;
    }

    public <T> void add(Request<T> request) {
        getRequestQueue().add(request);
    }


    public static  synchronized VolleyQueueSingleton getInstance() {
        return mInstance;
    }

    public static synchronized void removeInstance() {
        mInstance = null;
        mContext = null;
    }
}
