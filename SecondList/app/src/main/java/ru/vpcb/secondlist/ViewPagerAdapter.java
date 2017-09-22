package ru.vpcb.secondlist;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by V1 on 21-Sep-17.
 */

public class ViewPagerAdapter extends PagerAdapter {
    List<View> listPages;


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
        container.removeViewInLayout((View) object);
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
