package com.example.xyzreader.ui;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 15-Nov-17
 * Email: vadim.v.voronov@gmail.com
 */


import android.database.Cursor;
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
    void onCallback(View view, int pos);
    void onCallback(int mode);
    void onCallback(ArticleDetailFragment fragment);

}
