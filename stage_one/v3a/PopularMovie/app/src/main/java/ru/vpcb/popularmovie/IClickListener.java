package ru.vpcb.popularmovie;

/**
 * Created by V1 on 01-Oct-17.
 */

public interface IClickListener<T> {
    void onItemClick(T item);
}
