package ru.vpcb.footballassistant.add;

import android.view.View;
import java.util.List;
import java.util.Map;
import ru.vpcb.footballassistant.data.FDFixture;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 05-Feb-18
 * Email: vadim.v.voronov@gmail.com
 */
public class ViewPagerDataExt {
    private List<View> mRecyclers;
    private List<String> mTitles;
    private int mPos;
    private List<List<FDFixture>> mList;
    private Map<Long, Integer> mMap;


    public ViewPagerDataExt(List<View> recyclers, List<String> titles, int pos,
                            List<List<FDFixture>> list,
                            Map<Long, Integer> map) {
        this.mRecyclers = recyclers;
        this.mTitles = titles;
        this.mPos = pos;
        this.mList = list;
        this.mMap = map;

    }

    public List<View> getRecyclers() {
        return mRecyclers;
    }

    public List<String> getTitles() {
        return mTitles;
    }

    public int getPos() {
        return mPos;
    }

    public List<List<FDFixture>> getList() {
        return mList;
    }

    public Map<Long, Integer> getMap() {
        return mMap;
    }
}
