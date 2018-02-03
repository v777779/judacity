package ru.vpcb.footballassistant;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 29-Sep-17
 * Email: vadim.v.voronov@gmail.com
 */

public class ViewPagerAdapter extends PagerAdapter {
    private final List<View> listPages;

    /**
     * Constructor  created object from List<View>> data source
     *
     * @param listPages  input List<View> data source
     */
    public ViewPagerAdapter(List<View> listPages) {
        this.listPages = listPages;
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
}
