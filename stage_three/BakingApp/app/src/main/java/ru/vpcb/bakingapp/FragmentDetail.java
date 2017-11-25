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

import static android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;
import static ru.vpcb.bakingapp.utils.Constants.BUNDLE_DETAIL_EXPANDED;
import static ru.vpcb.bakingapp.utils.Constants.BUNDLE_DETAIL_POSITION;
import static ru.vpcb.bakingapp.utils.Constants.BUNDLE_PLAY_BACK_ENDED;
import static ru.vpcb.bakingapp.utils.Constants.BUNDLE_PLAY_PAUSE_READY;
import static ru.vpcb.bakingapp.utils.Constants.BUNDLE_PLAY_SEEK_POSITION;
import static ru.vpcb.bakingapp.utils.Constants.BUNDLE_PLAY_WINDOW_INDEX;
import static ru.vpcb.bakingapp.utils.Constants.DETAIL_IS_EXPANDED;
import static ru.vpcb.bakingapp.utils.Constants.ERROR_RECIPE_EMPTY;
import static ru.vpcb.bakingapp.utils.Constants.RECIPE_POSITION;
import static ru.vpcb.bakingapp.utils.Constants.RECIPE_SCREEN_WIDE;
import static ru.vpcb.bakingapp.utils.Constants.RECIPE_STEP_POSITION;
import static ru.vpcb.bakingapp.utils.Constants.STEP_DEFAULT_POSITION;
import static ru.vpcb.bakingapp.utils.Constants.SYSTEM_UI_SHOW_FLAGS;
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
        if (savedInstanceState != null) {
            mIsExpanded = savedInstanceState.getBoolean(BUNDLE_DETAIL_EXPANDED, false);
            mPosition = savedInstanceState.getInt(BUNDLE_DETAIL_POSITION, STEP_DEFAULT_POSITION);
        }else {
            mPosition = STEP_DEFAULT_POSITION;
            mIsExpanded = false;
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


        if (mIsWide && mRecipeItem != null && savedInstanceState == null ) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentPlayer playerFragment = getFragmentPlayer();
            fragmentManager.beginTransaction()
                    .replace(R.id.fc_p_container, playerFragment)
                    .addToBackStack("player")
                    .commit();
        }


        rootView.setSystemUiVisibility(SYSTEM_UI_SHOW_FLAGS);

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
        outState.putBoolean(BUNDLE_DETAIL_EXPANDED, mIsExpanded);
        outState.putInt(BUNDLE_DETAIL_POSITION, mPosition);
    }

    @Override
    public void onCallback(int position) {
        mPosition = position;
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentPlayer playerFragment = getFragmentPlayer();

        fragmentManager.popBackStack("player",POP_BACK_STACK_INCLUSIVE);

        if (mIsWide) {
            fragmentManager.beginTransaction()
                    .replace(R.id.fc_p_container, playerFragment)
                    .commit();
        } else {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, playerFragment)
                    .addToBackStack("player")
                    .commit();


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
