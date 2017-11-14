package ru.vpcb.btdetail;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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

import ru.vpcb.btdetail.utils.FragmentData;


public class FragmentMain extends Fragment implements IFragmentHelper {

    private List<String> mCardList;
    private RecyclerView mRecyclerView;
    private FragmentMainAdapter mRecyclerAdapter;
    private int mSpan;

    public FragmentMain() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {


        final View rootView = inflater.inflate(R.layout.fragment_main_recycler, container, false);
        mCardList = FragmentData.loadMockCards();                               // load mock data

        mRecyclerView = rootView.findViewById(R.id.fc_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());


        mRecyclerView.setLayoutManager(layoutManager);                          // connect to LayoutManager
        mRecyclerView.setHasFixedSize(true);                                    // item size fixed
        mRecyclerAdapter = new FragmentMainAdapter(rootView.getContext(), this);     //context  and data
        mRecyclerView.setAdapter(mRecyclerAdapter);


        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCLick(int position) {
//        Toast.makeText(getContext(),"Clicked position: "+position,Toast.LENGTH_SHORT).show();
        Snackbar.make(getView(), "Clicked position: "+position, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

        FragmentDetail detailFragment = new FragmentDetail();

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_middle_container, detailFragment)
                .addToBackStack(null)
                .commit();

    }

    @Override
    public List<String> getList() {
        return new ArrayList<>(mCardList);
    }
}
