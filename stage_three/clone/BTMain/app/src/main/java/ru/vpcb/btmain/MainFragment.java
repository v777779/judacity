package ru.vpcb.btmain;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ru.vpcb.btmain.utils.FormatData;



public class MainFragment extends Fragment {

    private List<String> mCardList;
    private RecyclerView mRecyclerView;
    private FCAdapter mRecyclerAdapter;
    private int mSpan;

    public MainFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {


        final View rootView = inflater.inflate(R.layout.fragment_main_recycler, container, false);
        mCardList = FormatData.loadMockCards(); // load mock data

        mRecyclerView = rootView.findViewById(R.id.fc_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());


        mRecyclerView.setLayoutManager(layoutManager);                          // connect to LayoutManager
        mRecyclerView.setHasFixedSize(true);                                    // item size fixed
        mRecyclerAdapter = new FCAdapter(rootView.getContext(), mCardList);     //context  and data
        mRecyclerView.setAdapter(mRecyclerAdapter);



        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}
