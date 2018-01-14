package com.example.xyzreader.ui;

import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.example.xyzreader.data.ArticleLoader;

/**
 * ViewPagerAdapter class used to show ArticleDetailFragments objects.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    /**
     * Cursor source of data.
     */
    private Cursor mCursor;
    /**
     * ICallback common user interface object.
     */
    private ICallback mCallback;
    /**
     * Integer  starting item ID
     */
    private long mStartingItemId;
    /**
     * Integer  current item ID
     */
    private long mCurrentItemId;

    /**
     *  Constructor
     *
     * @param fm    FragmentManager used to creates fragments
     * @param callback ICallback provides interface to calling activity
     */
    public ViewPagerAdapter(FragmentManager fm, ICallback callback) {
        super(fm);
        mCallback = callback;
    }

    /**
     *   Informs the adapter of which item is currently considered to be the "primary"
     *
     * @param container ViewGroup containing View from which the page will be removed.
     * @param position int page position that is now the primary
     * @param object Object returned by instantiateItem() method
     */
    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        mCallback.onCallback((ArticleDetailFragment) object);
    }

    /**
     * Returns new Fragment by position
     * @param position int position of item
     * @return Fragment created by ArticleDetailFragment.newInstance()
     */
    @Override
    public Fragment getItem(int position) {
        mCursor.moveToPosition(position);
        mCurrentItemId = mCursor.getLong(ArticleLoader.Query._ID);
        return ArticleDetailFragment.newInstance(mStartingItemId, mCurrentItemId);
    }

    /**
     *  Returns number of items which is equal to size of Cursor data source
     *
     * @return int number of items.
     */
    @Override
    public int getCount() {
        return (mCursor != null) ? mCursor.getCount() : 0;
    }

    /**
     *  Set new cursor data source of ViewPagerAdapter
     *
     * @param cursor  Cursor data source parameter
     */
    public void swap(Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0) return;
        mCursor = cursor;
        notifyDataSetChanged();
    }

    /**
     *  Set starting item ID for ArticleDetailFragment.newInstance()ance() method.
     *
     * @param startingItemId
     */
    public void setStartingItemId(long startingItemId) {
        mStartingItemId = startingItemId;
    }
}