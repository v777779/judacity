package com.example.android.rviewpager;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.List;

import timber.log.Timber;

public class FragmentAdapter extends FragmentStatePagerAdapter {
    private List<String> mList;
    private int mStartingPosition;
    private FragmentDetail mCurrentFragmentDetail;
    private ICallback mCallback;

    public FragmentAdapter(FragmentManager fm,ICallback callback) {
        super(fm);
        mCallback = callback;
    }


    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        mCallback.onCallback((FragmentDetail)object);
    }

    @Override
    public Fragment getItem(int position) {
        Timber.d("lifecycle detail  : getItem():" + position);
        return FragmentDetail.newInstance(mList.get(position), mList.get(mStartingPosition));
    }

    @Override
    public int getCount() {
        return (mList != null) ? mList.size() : 0;
    }

    public void swap(List list, int startingPosition) {
        if (list == null || list.size() == 0) return;
        mList = list;
        mStartingPosition = startingPosition;

        notifyDataSetChanged();
    }


}