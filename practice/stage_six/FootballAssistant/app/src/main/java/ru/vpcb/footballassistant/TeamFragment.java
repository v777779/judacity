package ru.vpcb.footballassistant;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class TeamFragment extends Fragment {


    private MatchActivity mActivity;
    private View mRootView;
    private ActionBar mActionBar;



    public static Fragment newInstance() {
        Fragment fragment = new TeamFragment();
        Bundle args = new Bundle();
// set arguments
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MatchActivity) getActivity();


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_team, container, false);

        if (savedInstanceState == null) {
        }
        setupActionBar();
        return mRootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_match, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            mActivity.onBackPressed();
            return true;
        }
        if (id == R.id.action_share) {
            Snackbar.make(mRootView, "Action Share", Snackbar.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        AppBarLayout appBarLayout = mActivity.getWindow().findViewById(R.id.app_bar);
        appBarLayout.getLayoutParams().height = mRootView.findViewById(R.id.app_bar).getHeight();
        if (mActionBar != null) mActionBar.show();

    }


    private void setupActionBar() {
        mActionBar = mActivity.getSupportActionBar();
        if (mActionBar != null) mActionBar.hide();
        AppBarLayout appBarLayout = mActivity.getWindow().getDecorView().findViewById(R.id.app_bar);
        appBarLayout.getLayoutParams().height = 0;

//
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        ActionBar actionBar = getSupportActionBar();
//        if(actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setTitle("");
//        }

    }

}
