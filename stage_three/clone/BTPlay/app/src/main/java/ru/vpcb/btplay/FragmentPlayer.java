package ru.vpcb.btplay;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 15-Nov-17
 * Email: vadim.v.voronov@gmail.com
 */

public class FragmentPlayer extends Fragment implements IFragmentHelper {

    private List<String> mCardList;
    private RecyclerView mRecyclerView;
    private FragmentMainAdapter mRecyclerAdapter;
    private int mSpan;

    public FragmentPlayer() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_play, container, false);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCallback(0);
            }
        });
        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCallback(int position) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Toast.makeText(getContext(), "Clicked on On Player " + " stack: " +
                fragmentManager.getBackStackEntryCount(), Toast.LENGTH_SHORT).show();

    }

    @Override
    public List<String> getList() {
        return new ArrayList<>(mCardList);
    }
}
