package ru.vpcb.popularmovie.trailer;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 23-Oct-17
 * Email: vadim.v.voronov@gmail.com
 */


public interface ITrailerListener<T> {
    void onItemClick(T position);
}
