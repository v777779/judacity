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
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Scene;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import static ru.vpcb.footballassistant.utils.Constants.MAIN_ACTIVITY_PROGRESS;
import static ru.vpcb.footballassistant.utils.Constants.UPDATE_SERVICE_PROGRESS;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static boolean sIsTimber;
    private static Handler mHandler;

    private FloatingActionButton mFab;
    private FloatingActionButton mFab2;

    private ProgressBar mProgressBar;
    private ProgressBar mProgressValue;
    private TextView mProgressText;
    private ImageView mToolbarLogo;
    // progress


    private MessageReceiver mMessageReceiver;
    private boolean mIsProgressEinished;
    private int mActivityProgress;
    private int mServiceProgress;

    // mMap
    private Map<Integer, FDCompetition> mMap = new HashMap<>();
    private Map<Integer, List<Integer>> mMapTeamKeys = new HashMap<>();
    private Map<Integer, FDTeam> mMapTeams = new HashMap<>();
    private Map<Integer, List<Integer>> mMapFixtureKeys = new HashMap<>();
    private Map<Integer, FDFixture> mMapFixtures = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        // log
        if (!sIsTimber) {
            Timber.plant(new Timber.DebugTree());
            sIsTimber = true;
        }
// handler
        if(mHandler == null) {
            mHandler = new Handler();
        }

// bind
        mFab = findViewById(R.id.fab);
        mFab2 = findViewById(R.id.fab2);
        mProgressBar = findViewById(R.id.progress_bar);
        mProgressText = findViewById(R.id.progress_text);
        mProgressValue = findViewById(R.id.progress_value);
        mToolbarLogo = findViewById(R.id.toolbar_logo);


        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                makeTransition(R.layout.content_detail,R.transition.transition_fade);
            }
        });

        mFab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeTransition(R.layout.content_main,R.transition.transition_fade_back);
            }
        });

// progress
        setupProgress();
        setupReceiver();
        setupActionBar();

        refresh(getString(R.string.action_update));

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
                break;

            case FDContract.CpTmEntry.LOADER_ID:
                mMapTeamKeys = FDUtils.readCompetitionTeams(cursor);
                break;

            case FDContract.TmEntry.LOADER_ID:
                mMapTeams = FDUtils.readTeams(cursor);
                break;

            case FDContract.CpFxEntry.LOADER_ID:
                mMapFixtureKeys = FDUtils.readCompetitionFixtures(cursor);
                break;

            case FDContract.FxEntry.LOADER_ID:
                mMapFixtures = FDUtils.readFixtures(cursor);
                break;

            case FDContract.TbEntry.LOADER_ID:
                break;

            case FDContract.PlEntry.LOADER_ID:
                break;

            default:
                throw new IllegalArgumentException("Unknown id: " + loader.getId());
        }

        mActivityProgress = checkProgress();
        setProgressValue();

        boolean isUpdated = FDUtils.loadCompetitions(mMap, mMapTeamKeys, mMapTeams, mMapFixtureKeys, mMapFixtures);
        if (isUpdated) {
            getSupportActionBar().show();
            makeTransition(R.layout.content_detail,R.transition.transition_fade);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
// cursors will be closed by supportLoaderManager().CursorLoader()

    }


    // methods

    private void makeTransition(int layoutId, int setId) {
        TransitionManager.go(
                Scene.getSceneForLayout(
                        (ViewGroup) findViewById(R.id.container_layout),
                        layoutId,
                        MainActivity.this),
                TransitionInflater.from(MainActivity.this)
                        .inflateTransition(setId)
        );
    }


    private void startTransition() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
            makeTransition(R.layout.content_detail, R.transition.transition_fade);
            }
        });



    }

    private int checkProgress() {
        int count = 0;
        int step = MAIN_ACTIVITY_PROGRESS / 5;
        if (!mMap.isEmpty()) count += step;
        if (!mMapTeamKeys.isEmpty()) count += step;
        if (!mMapTeams.isEmpty()) count += step;
        if (!mMapFixtureKeys.isEmpty()) count += step;
        if (!mMapFixtures.isEmpty()) count += step;
        return count;
    }

    private void setProgressValue(boolean isIndeterminate) {
        mProgressValue.setIndeterminate(isIndeterminate);
    }

    private void setProgressValue() {
        int value = mActivityProgress + mServiceProgress;
        int max = MAIN_ACTIVITY_PROGRESS + UPDATE_SERVICE_PROGRESS;
        if (value < 0) return;
        if (value > max) value = max;
        mProgressBar.setProgress(value);
        mProgressText.setText(String.valueOf(value));
        mProgressValue.setProgress(value);

        if (value >= max) {
            mProgressValue.setIndeterminate(false);
            mProgressValue.setProgress(value);
        }
    }



    private void setupProgress() {
        mIsProgressEinished = false;            // local updates
        mActivityProgress = 0;
        mServiceProgress = 0;
        setProgressValue();
        setProgressValue(false);                // static at start
    }

    private void setupActionBar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mToolbarLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("");
        }

//        getSupportActionBar().hide();

    }

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
        intentFilter.addAction(getString(R.string.broadcast_update_progress));
        registerReceiver(mMessageReceiver, intentFilter);
    }

    private void unregisterReceiver() {
        unregisterReceiver(mMessageReceiver);
    }


    // classes
    private class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // an Intent broadcast.
            if (intent != null) {
                String action = intent.getAction();
                if (action.equals(context.getString(R.string.broadcast_update_started))) {
                    setProgressValue(true); // indeterminate

                } else if (action.equals(context.getString(R.string.broadcast_update_finished))) {
                    mServiceProgress = UPDATE_SERVICE_PROGRESS;
                    setProgressValue();

                } else if (action.equals(context.getString(R.string.broadcast_update_progress))) {
                    int value = intent.getIntExtra(getString(R.string.extra_progress_counter), -1);
                    if (value >= 0) {
                        mServiceProgress = value;
                        setProgressValue();
                    }
                } else if (action.equals(context.getString(R.string.broadcast_no_network))) {
                    Toast.makeText(context, "Broadcast message: no network", Toast.LENGTH_SHORT).show();
                } else {
                    throw new UnsupportedOperationException("Not yet implemented");
                }

            }

        }
    }
}
