package ru.vpcb.footballassistant;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.vpcb.footballassistant.data.FDCompetition;
import ru.vpcb.footballassistant.data.FDFixture;
import ru.vpcb.footballassistant.data.FDTeam;
import ru.vpcb.footballassistant.dbase.FDContract;
import ru.vpcb.footballassistant.dbase.FDLoader;
import ru.vpcb.footballassistant.services.UpdateService;
import ru.vpcb.footballassistant.utils.FDUtils;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static boolean sIsTimber;

    private Handler mHandler;
    private FloatingActionButton mFab;
    private FloatingActionButton mFab2;

    private ProgressBar mProgressBar;
    private ProgressBar mProgressValue;
    private TextView mProgressText;

    // progress
    private int mProgressCounter;
    private MessageReceiver mMessageReceiver;
    // mMap
    private Map<Integer, FDCompetition> mMap = new HashMap<>();
    private Map<Integer, List<Integer>> mMapTeamKeys = new HashMap<>();
    private Map<Integer, FDTeam> mMapTeams = new HashMap<>();
    private Map<Integer, List<Integer>> mMapFixtureKeys = new HashMap<>();
    private Map<Integer, FDFixture> mMapFixtures = new HashMap<>();
    private Cursor mCursor;
    private Cursor mTeamKeysCursor;
    private Cursor mTeamsCursor;
    private Cursor mFixtureKeysCursor;
    private Cursor mFixturesCursor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        getSupportActionBar().hide();


        // log
        if (!sIsTimber) {
            Timber.plant(new Timber.DebugTree());
            sIsTimber = true;
        }
// handler
        mHandler = new Handler();

// bind
        mFab = findViewById(R.id.fab);
        mFab2 = findViewById(R.id.fab2);
        mProgressBar = findViewById(R.id.progress_bar);
        mProgressText = findViewById(R.id.progress_text);
        mProgressValue = findViewById(R.id.progress_value);
        mProgressValue.setIndeterminate(true);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                startActivity(intent);
            }
        });

        mFab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh(getString(R.string.action_update));
            }
        });

        mFab.setVisibility(View.INVISIBLE);
        mFab2.setVisibility(View.INVISIBLE);


        setupReceiver();

        getSupportLoaderManager().initLoader(FDContract.CpEntry.LOADER_ID, null, this);
        getSupportLoaderManager().initLoader(FDContract.CpTmEntry.LOADER_ID, null, this);
        getSupportLoaderManager().initLoader(FDContract.CpFxEntry.LOADER_ID, null, this);
        getSupportLoaderManager().initLoader(FDContract.TmEntry.LOADER_ID, null, this);
        getSupportLoaderManager().initLoader(FDContract.FxEntry.LOADER_ID, null, this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver();
    }

// callbacks

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return FDLoader.getInstance(this, id);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (loader == null || loader.getId() <= 0 || cursor == null || cursor.getCount() == 0)
            return;

        switch (loader.getId()) {
            case FDContract.CpEntry.LOADER_ID:
                mMap = FDUtils.readCompetitions(cursor);
                mCursor = cursor;
                break;

            case FDContract.CpTmEntry.LOADER_ID:
                mMapTeamKeys = FDUtils.readCompetitionTeams(cursor);
                mTeamKeysCursor = cursor;
                break;

            case FDContract.TmEntry.LOADER_ID:
                mMapTeams = FDUtils.readTeams(cursor);
                mTeamsCursor = cursor;
                break;

            case FDContract.CpFxEntry.LOADER_ID:
                mMapFixtureKeys = FDUtils.readCompetitionFixtures(cursor);
                mFixtureKeysCursor = cursor;
                break;

            case FDContract.FxEntry.LOADER_ID:
                mMapFixtures = FDUtils.readFixtures(cursor);
                mFixturesCursor = cursor;
                break;

            case FDContract.TbEntry.LOADER_ID:
                break;

            case FDContract.PlEntry.LOADER_ID:
                break;

            default:
                throw new IllegalArgumentException("Unknown id: " + loader.getId());
        }
        boolean isUpdated = FDUtils.loadCompetitions(mMap, mMapTeamKeys, mMapTeams, mMapFixtureKeys, mMapFixtures);
        if (isUpdated) {
// test!!!
            Timber.d("RecyclerView or ViewPager adapter notification update: " + mMap.size());
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader == null || loader.getId() <= 0) return;

        switch (loader.getId()) {
            case FDContract.CpEntry.LOADER_ID:
                if (mCursor != null) mCursor.close();
                mCursor = null;
                break;

            case FDContract.CpTmEntry.LOADER_ID:
                if (mTeamsCursor != null) mTeamsCursor.close();
                mTeamsCursor = null;
                break;

            case FDContract.TmEntry.LOADER_ID:
                if (mTeamsCursor != null) mTeamsCursor.close();
                mTeamsCursor = null;
                break;

            case FDContract.CpFxEntry.LOADER_ID:
                if (mFixtureKeysCursor != null) mFixtureKeysCursor.close();
                mFixtureKeysCursor = null;
                break;

            case FDContract.FxEntry.LOADER_ID:
                if (mFixturesCursor != null) mFixturesCursor.close();
                mFixturesCursor = null;
                break;

            case FDContract.TbEntry.LOADER_ID:
                break;

            case FDContract.PlEntry.LOADER_ID:
                break;

            default:
                throw new IllegalArgumentException("Unknown id: " + loader.getId());
        }
        boolean isUpdated = FDUtils.loadCompetitions(mMap, mMapTeamKeys, mMapTeams, mMapFixtureKeys, mMapFixtures);
        if (isUpdated) {
// test!!!
            Timber.d("RecyclerView or ViewPager adapter notification update: " + mMap.size());
        }

    }


    // methods
    private void refresh(String action) {
        Intent intent = new Intent(action, null, this, UpdateService.class);
        startService(intent);
    }

    private void setupReceiver() {
        mMessageReceiver = new MessageReceiver();
    }

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(getString(R.string.broadcast_update_started));
        intentFilter.addAction(getString(R.string.broadcast_update_finished));
        intentFilter.addAction(getString(R.string.broadcast_no_network));
        registerReceiver(mMessageReceiver, intentFilter);
    }

    private void unregisterReceiver() {
        unregisterReceiver(mMessageReceiver);
    }


    // test!!!
    private void testProgress() {
        mProgressCounter = 0;
        mProgressValue.setIndeterminate(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mProgressCounter < 100) {
                    mProgressCounter += 5;
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    mHandler.post(new Runnable() {  // access from thread to main views
                        @Override
                        public void run() {
                            mProgressBar.setProgress(mProgressCounter);
                            mProgressText.setText(String.valueOf(mProgressCounter));
                            mProgressValue.setProgress(mProgressCounter);

                            if (mProgressCounter >= 100) {
                                mProgressValue.setIndeterminate(false);
                            }

                        }
                    });
                }
            }
        }).start();

    }


    // classes
    private class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // an Intent broadcast.
            if (intent != null) {
                String action = intent.getAction();
                if (action.equals(context.getString(R.string.broadcast_update_started))) {
                    Toast.makeText(context, "Broadcast message: update started", Toast.LENGTH_SHORT).show();

                } else if (action.equals(context.getString(R.string.broadcast_update_finished))) {
                    Toast.makeText(context, "Broadcast message: update finished", Toast.LENGTH_SHORT).show();
                } else if (action.equals(context.getString(R.string.broadcast_no_network))) {
                    Toast.makeText(context, "Broadcast message: no network", Toast.LENGTH_SHORT).show();
                } else {
                    throw new UnsupportedOperationException("Not yet implemented");
                }

            }

        }
    }
}
