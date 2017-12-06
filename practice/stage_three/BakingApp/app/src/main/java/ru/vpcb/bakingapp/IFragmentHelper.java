package ru.vpcb.bakingapp;

import java.util.List;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 15-Nov-17
 * Email: vadim.v.voronov@gmail.com
 */


/**
 *  IFragmentHelper callback interface for RecyclerView items
 */
public interface IFragmentHelper {
    /**
     *  Performs callback processing when onClick() called
     *
     * @param position  int position of item which was selected
     */
    void onCallback(int position);

    /**
     *  Performs callback processing when onClick() for CLOSE
     *  button of FrameError fragment called. Not used.
     */
    void showError();

}
