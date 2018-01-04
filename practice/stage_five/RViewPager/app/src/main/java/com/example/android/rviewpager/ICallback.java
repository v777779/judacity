package com.example.android.rviewpager;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 15-Nov-17
 * Email: vadim.v.voronov@gmail.com
 */


import android.net.Uri;
import android.view.View;

/**
 *  ICallback callback interface for RecyclerView items
 */
public interface ICallback {
    /**
     *  Performs callback processing when onClick() for CLOSE
     *  button of FrameError fragment called. Not used.
     */
    void onCallback(Uri uri, View view);
    void onCallback(int mode);

}
