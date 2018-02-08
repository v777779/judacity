package ru.vpcb.footballassistant;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import static android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;
import static ru.vpcb.footballassistant.utils.Config.FRAGMENT_TEAM_TAG;

public class MatchActivity extends AppCompatActivity {

    private View mViewTeamHome;
    private View mViewTeamAway;
    private TextView mViewLeague;
    private View mViewFavorite;
    private View mViewNotification;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        setupActionBar();
        setupListeners();

// test!!! protrusion check
//        ((AppBarLayout) findViewById(R.id.app_bar)).addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
//            @Override
//            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
//                if (Math.abs(verticalOffset) > 5) {
//                    int k = 1;
//                }
//            }
//        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_match, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        if (id == R.id.action_share) {
            Snackbar.make(getWindow().getDecorView(), "Action Share", Snackbar.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // methods
    private void startActivity(Class destination) {
        Intent intent = new Intent(this, destination);
        startActivity(intent);
    }

    private void startActivityTeam() {
        Intent intent = new Intent(this, TeamActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        startActivity(intent);
    }


    private void startFragmentLeague() {

    }

    private void startFragmentTeam() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = TeamFragment.newInstance();

        fm.popBackStackImmediate(FRAGMENT_TEAM_TAG, POP_BACK_STACK_INCLUSIVE);
        fm.beginTransaction()
                .add(R.id.container_match, fragment)
                .addToBackStack(FRAGMENT_TEAM_TAG)
                .commit();

    }

    private void setupListeners() {
        mViewTeamHome = findViewById(R.id.image_sm_team_home);
        mViewTeamAway = findViewById(R.id.image_sm_team_away);
        mViewLeague = findViewById(R.id.text_sm_item_league);
        mViewFavorite = findViewById(R.id.match_favorite);
        mViewNotification = findViewById(R.id.match_notification);


        mViewLeague.setPaintFlags(mViewLeague.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        mViewLeague.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startFragmentLeague();
                startActivity(LeagueActivity.class);
            }
        });

        mViewTeamHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startFragmentTeam();
                startActivity(TeamActivity.class);
            }
        });

        mViewTeamAway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startFragmentTeam();
                startActivity(TeamActivity.class);
            }
        });

        mViewFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Action favorites", Snackbar.LENGTH_SHORT).show();
            }
        });

        mViewNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Action notification", Snackbar.LENGTH_SHORT).show();
            }
        });


    }

    private void setupActionBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("");
        }

    }

}
