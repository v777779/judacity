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
        int counter = 0;
//        for(mCursor.moveToFirst();!mCursor.isAfterLast();mCursor.moveToNext()) {
//            Timber.d("lifecycle detail  : getItem():"+
//                            mCursor.getString(ArticleLoader.Query.TITLE)+" " +
//                            mCursor.getString(ArticleLoader.Query.AUTHOR)+" "+
//                    counter++);
//        }


        mCursor.moveToPosition(position);
        Timber.d("lifecycle detail  : getItem():"+position);




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