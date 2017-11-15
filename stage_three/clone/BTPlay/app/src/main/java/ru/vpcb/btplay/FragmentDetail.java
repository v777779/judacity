package ru.vpcb.btplay;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ru.vpcb.btplay.utils.FragmentData;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 15-Nov-17
 * Email: vadim.v.voronov@gmail.com
 */

public class FragmentDetail extends Fragment implements IFragmentHelper {

    private List<String> mCardList;
    private RecyclerView mRecyclerView;
    private FragmentDetailAdapter mRecyclerAdapter;
    private int mSpan;

    public FragmentDetail() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {


        final View rootView = inflater.inflate(R.layout.fragment_detail_recycler, container, false);
        mCardList = FragmentData.loadMockDetails();                               // load mock data


        mRecyclerView = rootView.findViewById(R.id.fc_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());


        mRecyclerView.setLayoutManager(layoutManager);                          // connect to LayoutManager
        mRecyclerView.setHasFixedSize(false);                                    // item size fixed
        mRecyclerAdapter = new FragmentDetailAdapter(rootView.getContext(), this);     //context  and data
        mRecyclerView.setAdapter(mRecyclerAdapter);


        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCallback(int position) {
//        Toast.makeText(getContext(), "Clicked position: " + position, Toast.LENGTH_SHORT).show();
//        Snackbar.make(getView(), "Clicked position: "+position, Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show();

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentPlayer playerFragment = new FragmentPlayer();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, playerFragment)
                .addToBackStack(null)
                .commit();

        Toast.makeText(getContext(), "Clicked position: " + position + " stack: " +
                fragmentManager.getBackStackEntryCount(), Toast.LENGTH_SHORT).show();

    }

    @Override
    public List<String> getList() {
        return new ArrayList<>(mCardList);
    }
}
