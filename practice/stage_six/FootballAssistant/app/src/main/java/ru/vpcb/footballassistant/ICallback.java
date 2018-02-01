package ru.vpcb.footballassistant;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 02-Feb-18
 * Email: vadim.v.voronov@gmail.com
 */

import android.view.View;

/**
 *  ICallback callback common user interface.
 */
public interface ICallback {
    /**
     *  Performs callback processing for RecyclerView
     *  in MainActivity
     */
    void onCallback(View view, int pos);
    /**
     *  Performs callback processing for FragmentError
     *  in MainActivity
     */
    void onCallback(int mode);



}
