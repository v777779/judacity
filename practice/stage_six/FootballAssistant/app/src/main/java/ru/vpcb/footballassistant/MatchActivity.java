package ru.vpcb.footballassistant;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;

import ru.vpcb.footballassistant.data.FDFixture;
import ru.vpcb.footballassistant.notifications.NotificationUtils;
import ru.vpcb.footballassistant.utils.FDUtils;

import static android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;
import static ru.vpcb.footballassistant.utils.Config.BUNDLE_INTENT_LEAGUE_ID;
import static ru.vpcb.footballassistant.utils.Config.BUNDLE_INTENT_TEAM_ID;
import static ru.vpcb.footballassistant.utils.Config.FRAGMENT_TEAM_TAG;

public class MatchActivity extends AppCompatActivity {

    private TextView mViewLeague;
    private View mViewTeamHome;
    private View mViewTeamAway;
    private View mViewNotification;
    private View mViewFavorite;


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
        getMenuInflater().inflate(R.menu.menu_share, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

//        if (id == android.R.id.home) {
//            onBackPressed();
//            return true;
//        }

        if (id == R.id.action_share) {
            Snackbar.make(getWindow().getDecorView(), "Action Share", Snackbar.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // methods
    private void startActivityLeague(int id) {
        Intent intent = new Intent(this, LeagueActivity.class);
        intent.putExtra(BUNDLE_INTENT_LEAGUE_ID, id);
        startActivity(intent);
    }

    private void startActivityTeam(int id) {
        Intent intent = new Intent(this, TeamActivity.class);
        intent.putExtra(BUNDLE_INTENT_TEAM_ID, id);
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
        mViewLeague = findViewById(R.id.text_sm_item_league);
        mViewTeamHome = findViewById(R.id.image_sm_team_home);
        mViewTeamAway = findViewById(R.id.image_sm_team_away);
        mViewNotification = findViewById(R.id.match_notification_back);
        mViewFavorite = findViewById(R.id.match_favorite_back);



        mViewLeague.setPaintFlags(mViewLeague.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        mViewLeague.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startFragmentLeague();
                startActivityLeague(548);
            }
        });

        mViewTeamHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startFragmentTeam();
                startActivityTeam(548);
            }
        });
        mViewTeamAway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startFragmentTeam();
                startActivityTeam( 535);
//                startActivityTeam(530);
            }
        });

        mViewNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
// test!!!
                Calendar c= Calendar.getInstance();
                c.add(Calendar.SECOND,60);
                String dateSQLite = FDUtils.formatDateToSQLite(c.getTime());
                FDFixture fixture = new FDFixture(dateSQLite);
                NotificationUtils.scheduleReminder(MatchActivity.this,fixture);
// test!!!
// TODO add flag for notification, database field for notification status, set and clear procedure
            }
        });

        mViewFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
