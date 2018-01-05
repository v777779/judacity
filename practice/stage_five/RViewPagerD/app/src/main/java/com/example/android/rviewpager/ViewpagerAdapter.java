package com.example.android.rviewpager;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import timber.log.Timber;

public class ViewpagerAdapter extends FragmentStatePagerAdapter {
    private List<String> mList;
    private Bundle mOptions;

    public ViewpagerAdapter(FragmentManager fm, List<String> list, Bundle options) {
        super(fm);
        mList = list;
        mOptions = options;
    }

    @Override
    public Fragment getItem(int position) {
        Timber.d("lifecycle detail  : getItem():" + position);
        return FragmentDetail.newInstance(mList.get(position));
    }

    @Override
    public int getCount() {
        return (mList != null) ? mList.size() : 0;
    }

    public void swap(List list) {
        if (list == null || list.size() == 0) return;
        mList = list;
        notifyDataSetChanged();
    }
}