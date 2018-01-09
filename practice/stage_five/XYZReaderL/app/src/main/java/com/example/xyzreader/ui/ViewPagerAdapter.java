package com.example.xyzreader.ui;

import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.example.xyzreader.data.ArticleLoader;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private Cursor mCursor;
    private ICallback mCallback;
    private long mStartingItemId;
    private long mCurrentItemId;


    public ViewPagerAdapter(FragmentManager fm, ICallback callback) {
        super(fm);
        mCallback = callback;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        mCallback.onCallback((ArticleDetailFragment) object);
    }

    @Override
    public Fragment getItem(int position) {
        mCursor.moveToPosition(position);
        mCurrentItemId = mCursor.getLong(ArticleLoader.Query._ID);
        return ArticleDetailFragment.newInstance(mStartingItemId, mCurrentItemId);
    }

    @Override
    public int getCount() {
        return (mCursor != null) ? mCursor.getCount() : 0;
    }

    public void swap(Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0) return;
        mCursor = cursor;
        notifyDataSetChanged();
    }
    public void setStartingItemId(long startingItemId) {
        mStartingItemId = startingItemId;
    }
}