package ru.vpcb.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import ru.vpcb.bakingapp.data.RecipeItem;
import timber.log.Timber;

import static android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;
import static ru.vpcb.bakingapp.utils.Constants.BUNDLE_DETAIL_EXPANDED;
import static ru.vpcb.bakingapp.utils.Constants.BUNDLE_DETAIL_INTENT;
import static ru.vpcb.bakingapp.utils.Constants.BUNDLE_DETAIL_POSITION;
import static ru.vpcb.bakingapp.utils.Constants.RECIPE_POSITION;
import static ru.vpcb.bakingapp.utils.Constants.RECIPE_SCREEN_WIDE;
import static ru.vpcb.bakingapp.utils.Constants.RECIPE_STEP_POSITION;
import static ru.vpcb.bakingapp.utils.Constants.STEP_DEFAULT_POSITION;
import static ru.vpcb.bakingapp.utils.Constants.SYSTEM_UI_SHOW_FLAGS;

public class DetailActivity extends AppCompatActivity implements IFragmentHelper {

    private RecyclerView mRecyclerView;
    private DetailAdapter mRecyclerAdapter;
    private int mPosition;
    private boolean mIsWide;
    private RecipeItem mRecipeItem;
    private Context mContext;
    private boolean mIsExpanded;
    private View mRootView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_detail);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);  // обязательно без Manifest.PARENT
            actionBar.setHomeAsUpIndicator(R.drawable.ic_home_white_24dp);
        }

        mContext = this;
        mRootView = findViewById(R.id.fragment_container);
        if (!MainActivity.mIsTimber) {
            Timber.plant(new Timber.DebugTree());
            MainActivity.mIsTimber = true;
        }


        if (savedInstanceState != null) {
            mIsExpanded = savedInstanceState.getBoolean(BUNDLE_DETAIL_EXPANDED, false);
            mPosition = savedInstanceState.getInt(BUNDLE_DETAIL_POSITION, STEP_DEFAULT_POSITION);
        } else {
            mPosition = STEP_DEFAULT_POSITION;
            mIsExpanded = false;
        }

        mRecyclerView = mRootView.findViewById(R.id.fc_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);

        try {
            Intent intent = getIntent();
            Bundle detailArgs = intent.getBundleExtra(BUNDLE_DETAIL_INTENT);
            mRecipeItem = new Gson().fromJson(detailArgs.getString(RECIPE_POSITION, null), RecipeItem.class);
        } catch (Exception e) {
            Timber.d(e.getMessage());
            finish();           // onBackPressed();
            return;
        }

        mRecyclerView.setLayoutManager(layoutManager);                              // connect to LayoutManager
        mRecyclerView.setHasFixedSize(false);                                       // item size fixed
        mRecyclerAdapter = new DetailAdapter(mContext, this, mRecipeItem);      //context  and data
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerAdapter.setExpanded(mIsExpanded);
        mRecyclerView.setHasFixedSize(true);
        mIsWide = mRootView.findViewById(R.id.fc_p_container) != null;

        if (mIsWide && mRecipeItem != null && savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentPlayer playerFragment = getFragmentPlayer();
            fragmentManager.beginTransaction()
                    .replace(R.id.fc_p_container, playerFragment)
                    .addToBackStack("player")
                    .commit();
        }
        mRootView.setSystemUiVisibility(SYSTEM_UI_SHOW_FLAGS);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (mIsWide) {
//                getSupportFragmentManager().popBackStack("player", POP_BACK_STACK_INCLUSIVE);
                finish();
            }else {
                onBackPressed();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentPlayer playerFragment = getFragmentPlayer();

        fragmentManager.popBackStack("player", POP_BACK_STACK_INCLUSIVE);

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
