package ru.vpcb.popularmovie.pager;

import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 29-Sep-17
 * Email: vadim.v.voronov@gmail.com
 */

public class ViewPagerAdapter extends PagerAdapter {
    private final List<RecyclerView> recyclers;

    public ViewPagerAdapter() {
        recyclers = new ArrayList<>();
    }

    public void add(RecyclerView recyclerView) {
        recyclers.add(recyclerView);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = recyclers.get(position);
        container.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return recyclers.size();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        for (RecyclerView recycler : recyclers) {
            recycler.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
}
