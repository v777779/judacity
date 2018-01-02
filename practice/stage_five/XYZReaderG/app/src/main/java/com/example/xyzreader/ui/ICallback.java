package com.example.xyzreader.ui;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 15-Nov-17
 * Email: vadim.v.voronov@gmail.com
 */


import android.net.Uri;

/**
 *  ICallback callback interface for RecyclerView items
 */
public interface ICallback {
    /**
     *  Performs callback processing when onClick() for CLOSE
     *  button of FrameError fragment called. Not used.
     */
    void onCallback(Uri uri);
    void onCallback(int mode);

}
