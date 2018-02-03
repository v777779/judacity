package ru.vpcb.footballassistant;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import static ru.vpcb.footballassistant.utils.Config.EMPTY_FIXTURE_DATE;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 29-Sep-17
 * Email: vadim.v.voronov@gmail.com
 */

public class ViewPagerAdapter extends PagerAdapter {
    private final List<View> listPages;
    private List<String> listTitles;

    /**
     * Constructor  created object from List<View>> data source
     *
     * @param listPages input List<View> data source
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
        if (listTitles == null || position < 0 || position >= listTitles.size()) return EMPTY_FIXTURE_DATE;
        String title = listTitles.get(position);
        if (title == null) title = EMPTY_FIXTURE_DATE;
        return title;
    }
}
