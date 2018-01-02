package com.example.xyzreader.ui;

import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.example.xyzreader.data.ArticleLoader;

import timber.log.Timber;

public class ArticleDetailAdapter extends FragmentStatePagerAdapter {
    private Cursor mCursor;

    public ArticleDetailAdapter(FragmentManager fm, Cursor cursor) {
        super(fm);
        mCursor = cursor;

    }

    @Override
    public Fragment getItem(int position) {
        mCursor.moveToPosition(position);
        Timber.d("lifecycle detail  : getItem():"+position+" id:"+mCursor.getLong(ArticleLoader.Query._ID));
        return ArticleDetailFragment.newInstance(mCursor.getLong(ArticleLoader.Query._ID));
    }

    @Override
    public int getCount() {
        return (mCursor != null) ? mCursor.getCount() : 0;
    }

    public  void swap(Cursor cursor) {
        if(cursor == null || cursor.getCount()==0) return;
        mCursor = cursor;
        notifyDataSetChanged();
    }
}