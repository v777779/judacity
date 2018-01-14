package com.example.xyzreader.ui;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 15-Nov-17
 * Email: vadim.v.voronov@gmail.com
 */


import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;

/**
 *  ICallback callback common user interface.
 */
public interface ICallback {
    /**
     *  Performs callback processing for RecyclerView
     *  in ArticleListActivity
     */
    void onCallback(View view, int pos);
    /**
     *  Performs callback processing for FragmentError and ArticleDetailFragment
     *  in ArticleListActivity
     */
    void onCallback(int mode);

    /**
     *  Performs callback processing for ArticleListActivity
     *
     * @param fragment ArticleDetailFragment object.
     */
    void onCallback(ArticleDetailFragment fragment);

}
