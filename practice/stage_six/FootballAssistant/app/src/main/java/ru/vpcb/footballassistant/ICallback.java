package ru.vpcb.footballassistant;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 02-Feb-18
 * Email: vadim.v.voronov@gmail.com
 */

import android.view.View;

import java.util.Calendar;
import java.util.Date;

/**
 *  ICallback callback common user interface.
 */
public interface ICallback {
    /**
     *  Performs callback processing for RecyclerView
     *  in MainActivity
     */
    void onComplete(View view, int pos);
    /**
     *  Performs callback processing for FragmentError
     *  in DetailActivity
     */
    void onComplete(int mode, Calendar calendar);



}
