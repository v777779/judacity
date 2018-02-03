package ru.vpcb.viewpagertab;

import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.Date;
import java.util.List;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 29-Sep-17
 * Email: vadim.v.voronov@gmail.com
 */

class ViewPagerAdapter extends PagerAdapter {
    private List<View> listPages;
    private List<String> listTitles;

    /**
     * Constructor  created object from List<View>> data source
     *
     * @param listPages  input List<View> data source
     */
    public ViewPagerAdapter(List<View> listPages, List<String> listTitles) {
        this.listPages = listPages;
        this.listTitles = listTitles;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = listPages.get(position);
        container.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return listPages.size();
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return listTitles.get(position);
    }
}
