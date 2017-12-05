package ru.vpcb.popularmovie;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 29-Sep-17
 * Email: vadim.v.voronov@gmail.com
 */

public interface ICallback {
    void showPB();
    void  showError();
    void showRV();
    void setData(String s);
}
