package ru.vpcb.bakingapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.List;

import static ru.vpcb.bakingapp.utils.Constants.DETAIL_IS_EXPANDED;
import static ru.vpcb.bakingapp.utils.Constants.ERROR_RECIPE_EMPTY;
import static ru.vpcb.bakingapp.utils.Constants.RECIPE_POSITION;
import static ru.vpcb.bakingapp.utils.Constants.RECIPE_SCREEN_WIDE;
import static ru.vpcb.bakingapp.utils.Constants.RECIPE_STEP_POSITION;
import static ru.vpcb.bakingapp.utils.Constants.STEP_DEFAULT_POSITION;
import static ru.vpcb.bakingapp.utils.Constants.TAG_FDETAIL;


/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 15-Nov-17
 * Email: vadim.v.voronov@gmail.com
 */

public class FragmentDetail extends Fragment implements IFragmentHelper {


    private RecyclerView mRecyclerView;
    private FragmentDetailAdapter mRecyclerAdapter;
    private int mPosition;
    private boolean mIsWide;
    private RecipeItem mRecipeItem;
    private Context mContext;
    private boolean mIsExpanded;

    public FragmentDetail() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mIsExpanded = false;
        if (savedInstanceState != null) {
            mIsExpanded = savedInstanceState.getBoolean(DETAIL_IS_EXPANDED, false);
        }

        final View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        // load mock data


        mRecyclerView = rootView.findViewById(R.id.fc_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        Bundle detailArgs = getArguments();

        if (detailArgs != null) {
            try {
                mRecipeItem = new Gson().fromJson(detailArgs.getString(RECIPE_POSITION, null), RecipeItem.class);
            } catch (JsonSyntaxException e) {
                Log.d(TAG_FDETAIL, e.getMessage());
            }
        }


        mRecyclerView.setLayoutManager(layoutManager);                              // connect to LayoutManager
        mRecyclerView.setHasFixedSize(false);                                       // item size fixed
        mRecyclerAdapter = new FragmentDetailAdapter(mContext, this, mRecipeItem);      //context  and data
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerAdapter.setExpanded(mIsExpanded);
        mRecyclerView.setHasFixedSize(true);
        mIsWide = rootView.findViewById(R.id.fc_p_container) != null;
        mPosition = STEP_DEFAULT_POSITION;

        if (mIsWide && mRecipeItem != null ) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentPlayer playerFragment = getFragmentPlayer();
            fragmentManager.beginTransaction()
                    .replace(R.id.fc_p_container, playerFragment)
                    .commit();
        }
        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(DETAIL_IS_EXPANDED, mRecyclerAdapter.isExpanded());
    }

    @Override
    public void onCallback(int position) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentPlayer playerFragment = getFragmentPlayer();
        if (mIsWide) {
            mPosition = position;
            fragmentManager.beginTransaction()
                    .replace(R.id.fc_p_container, playerFragment)
                    .commit();
        } else {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, playerFragment)
                    .addToBackStack(null)
                    .commit();

//            Snackbar.make(getView(), "Clicked Fragment Detail position: " + position + " stack: " +
//                    fragmentManager.getBackStackEntryCount(), Snackbar.LENGTH_SHORT).show();

        }
    }


    @Override
    public List<FragmentDetailItem> getItemList() {
        return null;
    }


    @Override
    public int getSpanHeight() {
        return 0;
    }

    private FragmentPlayer getFragmentPlayer() {
        FragmentPlayer playerFragment = new FragmentPlayer();
        Bundle playerArgs = new Bundle();
        playerArgs.putString(RECIPE_POSITION, new Gson().toJson(mRecipeItem));
        playerArgs.putInt(RECIPE_STEP_POSITION, mPosition);
        playerArgs.putBoolean(RECIPE_SCREEN_WIDE, mIsWide);
        playerFragment.setArguments(playerArgs);
        return playerFragment;
    }
}
