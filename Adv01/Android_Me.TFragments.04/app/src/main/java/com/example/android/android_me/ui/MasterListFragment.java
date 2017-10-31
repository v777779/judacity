package com.example.android.android_me.ui;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.android.android_me.R;
import com.example.android.android_me.data.AndroidImageAssets;

import java.util.ArrayList;
import java.util.List;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 30-Oct-17
 * Email: vadim.v.voronov@gmail.com
 */
public class MasterListFragment extends Fragment {
    private static final String TAG = MasterListFragment.class.getSimpleName();
    private List<Integer> mImageIds;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_master_list, container, false);

        // Get a reference to the ImageView in the fragment layout
        final GridView gridView = (GridView) rootView.findViewById(R.id.master_list_gridview);

        mImageIds = AndroidImageAssets.getAll();
        MasterListAdapter adapter = new MasterListAdapter(this.getContext(),mImageIds);
        gridView.setAdapter(adapter);
        if(mImageIds == null){
            Log.v(TAG, "This fragment has a null list of image id's");
        }

        // Return the rootView
        return gridView;
    }



}
