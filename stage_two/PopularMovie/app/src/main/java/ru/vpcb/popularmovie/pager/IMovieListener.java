package ru.vpcb.popularmovie.pager;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 23-Oct-17
 * Email: vadim.v.voronov@gmail.com
 */


public interface IMovieListener<T> {
    void onItemClick(T item);
    void onItemClickFavorites(T item);

}
