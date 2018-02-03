package ru.vpcb.footballassistant;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Scene;
import android.transition.Transition;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.vpcb.footballassistant.data.FDCompetition;
import ru.vpcb.footballassistant.data.FDFixture;
import ru.vpcb.footballassistant.data.FDTeam;
import ru.vpcb.footballassistant.dbase.FDContract;
import ru.vpcb.footballassistant.dbase.FDLoader;
import ru.vpcb.footballassistant.services.UpdateService;
import ru.vpcb.footballassistant.utils.Config;
import ru.vpcb.footballassistant.utils.FDUtils;
import ru.vpcb.footballassistant.utils.FootballUtils;
import timber.log.Timber;

import static ru.vpcb.footballassistant.utils.Config.LOADERS_UPDATE_COUNTER;
import static ru.vpcb.footballassistant.utils.Config.MAIN_ACTIVITY_INDEFINITE;
import static ru.vpcb.footballassistant.utils.Config.MAIN_ACTIVITY_PROGRESS;
import static ru.vpcb.footballassistant.utils.Config.MAIN_ACTIVITY_STATE_0;
import static ru.vpcb.footballassistant.utils.Config.MAIN_ACTIVITY_STATE_1;
import static ru.vpcb.footballassistant.utils.Config.MAIN_ACTIVITY_STATE_2;
import static ru.vpcb.footballassistant.utils.Config.MAIN_ACTIVITY_STATE_3;
import static ru.vpcb.footballassistant.utils.Config.MAIN_ACTIVITY_STATE_4;
import static ru.vpcb.footballassistant.utils.Config.MAIN_ACTIVITY_STATE_5;
import static ru.vpcb.footballassistant.utils.Config.UPDATE_SERVICE_PROGRESS;
import static ru.vpcb.footballassistant.utils.Config.VIEWPAGER_OFF_SCREEN_PAGE_NUMBER;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>, ICallback {

    private static boolean sIsTimber;
    private static Handler mHandler;

    private FloatingActionButton mFab;
    private FloatingActionButton mFab2;

    private ProgressBar mProgressBar;
    private ProgressBar mProgressValue;
    private TextView mProgressText;
    private ImageView mToolbarLogo;

    private RecyclerView mRecyclerView;
    private ViewPager mViewPager;


    private BottomNavigationView mBottomNavigation;
    private BottomNavigationView.OnNavigationItemSelectedListener mBottomNavigationListener;

    // receiver
    private MessageReceiver mMessageReceiver;
    // progress
    private boolean mIsProgressEinished;
    private int mActivityProgress;
    private int mServiceProgress;
    private int mState;
    private int mUpdateCounter;

    // mMap
    private Map<Integer, FDCompetition> mMap = new HashMap<>();
    private Map<Integer, List<Integer>> mMapTeamKeys = new HashMap<>();
    private Map<Integer, FDTeam> mMapTeams = new HashMap<>();
    private Map<Integer, List<Integer>> mMapFixtureKeys = new HashMap<>();
    private Map<Integer, FDFixture> mMapFixtures = new HashMap<>();

    private List<List<FDFixture>> mViewPagerList;
    private int mViewPagerPos;


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
        if (mHandler == null) {
            mHandler = new Handler();
        }

// bind
        mFab = findViewById(R.id.fab);
        mFab2 = findViewById(R.id.fab2);
        mProgressBar = findViewById(R.id.progress_bar);
        mProgressText = findViewById(R.id.progress_text);
        mProgressValue = findViewById(R.id.progress_value);
        mToolbarLogo = findViewById(R.id.toolbar_logo);
        mBottomNavigation = findViewById(R.id.bottom_navigation);

// params
        mState = MAIN_ACTIVITY_INDEFINITE;


        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                makeTransition(R.layout.content_detail, R.transition.transition_fade);
            }
        });

        mFab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeTransition(R.layout.content_main, R.transition.transition_fade_back);
            }
        });

// progress
        setupActionBar();
        setupProgress();
        setupReceiver();

        moveState(MAIN_ACTIVITY_STATE_0);

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
                mUpdateCounter++;
                break;

            case FDContract.CpTmEntry.LOADER_ID:
                mMapTeamKeys = FDUtils.readCompetitionTeams(cursor);
                mUpdateCounter++;
                break;

            case FDContract.TmEntry.LOADER_ID:
                mMapTeams = FDUtils.readTeams(cursor);
                mUpdateCounter++;
                break;

            case FDContract.CpFxEntry.LOADER_ID:
                mMapFixtureKeys = FDUtils.readCompetitionFixtures(cursor);
                mUpdateCounter++;
                break;

            case FDContract.FxEntry.LOADER_ID:
                mMapFixtures = FDUtils.readFixtures(cursor);
                mUpdateCounter++;
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


        if (mUpdateCounter == LOADERS_UPDATE_COUNTER) {
            moveState(MAIN_ACTIVITY_STATE_1);
            setupViewPagerSource();

            mUpdateCounter = 0;
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
// cursors will be closed by supportLoaderManager().CursorLoader()

    }

    @Override
    public void onCallback(View view, int pos) {
        Snackbar.make(view, "Recycler item clicked", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

    }

    @Override
    public void onCallback(int mode) {

    }


    // methods


    private void setupState(int state) {
        switch (state) {
            case MAIN_ACTIVITY_STATE_0:
                break;
            case MAIN_ACTIVITY_STATE_1:
// bind
                mRecyclerView = findViewById(R.id.recycler_match);
                setupBottomNavigation();
//                changeActionBar(mState);
                setupRecycler();
                setupViewPager();

                break;
            case MAIN_ACTIVITY_STATE_2:
                break;
            case MAIN_ACTIVITY_STATE_3:
                break;
            case MAIN_ACTIVITY_STATE_4:
                break;
            case MAIN_ACTIVITY_STATE_5:
                break;
            default:

        }
    }


    private RecyclerView getRecycler(List<FDFixture> list) {
        Config.Span sp = Config.getDisplayMetrics(this);

        View recyclerLayout = getLayoutInflater().inflate(R.layout.recycler_main, null);
        RecyclerView recyclerView = recyclerLayout.findViewById(R.id.recycler_main_container);

        RecyclerAdapter adapter = new RecyclerAdapter(this, sp, list);
        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        return recyclerView;
    }

    private void setupRecycler() {
        Config.Span sp = Config.getDisplayMetrics(this);

        RecyclerAdapter adapter = new RecyclerAdapter(this, sp, null);
        adapter.setHasStableIds(true);
        mRecyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
    }


    private void moveState(int state) {
        if (mState == state) return;  // block repetitions

        switch (state) {
            case MAIN_ACTIVITY_STATE_0:
                changeActionBar(state);
                break;
            case MAIN_ACTIVITY_STATE_1:
                mState = MAIN_ACTIVITY_STATE_1;
                changeActionBar(mState);
                makeTransition(R.layout.content_detail, R.transition.transition_fade);

                break;
            case MAIN_ACTIVITY_STATE_2:
                break;
            case MAIN_ACTIVITY_STATE_3:
                break;

            case MAIN_ACTIVITY_STATE_4:
                break;
            case MAIN_ACTIVITY_STATE_5:
                break;
            default:

        }

    }

    //  test!!!
    private static Comparator<FDFixture> cFx = new Comparator<FDFixture>() {
        @Override
        public int compare(FDFixture o1, FDFixture o2) {
            if (o1 == null || o2 == null ||
                    o1.getDate() == null || o2.getDate() == null)
                throw new IllegalArgumentException();


            return o1.getDate().compareTo(o2.getDate());

        }
    };

    private void setZeroTime(Calendar c) {
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
    }


    private void setNextDay(Calendar c, Date date) {
        c.setTime(date);
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        c.add(Calendar.DATE, 1);  // next day
    }

    private int getIndex(List<FDFixture> list, Calendar c) {
        int index = Collections.binarySearch(list, new FDFixture(c.getTime()), cFx);  // for givent day
        if (index < 0) index = -index - 1;
        if (index > list.size()) index = list.size() - 1;
        return index;

    }

    private void setupViewPagerSource() {
        int last = 0;
        int next = 0;
        int current = 0;
        List<FDFixture> fixtures = new ArrayList<>(mMapFixtures.values()); // sorted by date

        Collections.sort(fixtures, cFx);
        List<List<FDFixture>> list = new ArrayList<>();

        Calendar c = Calendar.getInstance();
        setZeroTime(c);
        current = getIndex(fixtures, c);  // index of current day

        while (next < fixtures.size()) {
            setNextDay(c, fixtures.get(next).getDate());
            next = getIndex(fixtures, c);
            list.add(new ArrayList<>(fixtures.subList(last, next)));
            last = next;
            if (next == current) current = list.size();  // index of current day records
        }

        mViewPagerPos = current;
        mViewPagerList = list;
    }

    private void setupViewPager() {
        mViewPager = findViewById(R.id.viewpager_main);
        if (mViewPagerList == null) return;

        List<View> recyclers = new ArrayList<>();


        for (List<FDFixture> list : mViewPagerList) {
            recyclers.add(getRecycler(list));
        }

        ViewPagerAdapter listPagerAdapter = new ViewPagerAdapter(recyclers);
        mViewPager.setAdapter(listPagerAdapter);
        mViewPager.setCurrentItem(mViewPagerPos);
        mViewPager.setOffscreenPageLimit(VIEWPAGER_OFF_SCREEN_PAGE_NUMBER);  //    ATTENTION  Prevents Adapter Exception
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }

    // test!!!
    private void makeTransition(int layoutId, int setId) {
       final Scene scene = Scene.getSceneForLayout((ViewGroup) findViewById(R.id.container_layout),
                layoutId, MainActivity.this);
        Transition transition = TransitionInflater.from(MainActivity.this).inflateTransition(setId);

        scene.setEnterAction(new Runnable() {
            @Override
            public void run() {
                if(mState == MAIN_ACTIVITY_STATE_1) {
                    ImageView imageViewPagerBack = scene.getSceneRoot().findViewById(R.id.image_viewpager_back);
                    imageViewPagerBack.setImageResource(FootballUtils.getImageId());
                }
            }
        });
        transition.addListener(new TransitionAdapter() {
            @Override
            public void onTransitionEnd(Transition transition) {
                setupState(mState);
// test!!!


            }
        });

        TransitionManager.go(scene, transition);

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

    private void changeActionBar(int state) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) return;

        switch (state) {
            case MAIN_ACTIVITY_STATE_0:
                mToolbarLogo.setVisibility(View.VISIBLE);
                actionBar.hide();
                break;

            case MAIN_ACTIVITY_STATE_1:
                mToolbarLogo.setVisibility(View.INVISIBLE);
                Toolbar toolbar = findViewById(R.id.toolbar);
                toolbar.setTitle(getString(R.string.screen_match));
                actionBar.show();
                break;
            case MAIN_ACTIVITY_STATE_2:
                break;
            case MAIN_ACTIVITY_STATE_3:
                break;
            case MAIN_ACTIVITY_STATE_4:
                break;
            case MAIN_ACTIVITY_STATE_5:
                break;
            default:


        }
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

    private void setupBottomNavigation() {
        mBottomNavigation = findViewById(R.id.bottom_navigation);
        if (mBottomNavigation == null) return;

        mBottomNavigation.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.navigation_home:

                                return true;
                            case R.id.navigation_dashboard:

                                return true;
                            case R.id.navigation_notifications:

                                return true;
                        }
                        return false;
                    }
                });
    }

    private class TransitionAdapter implements Transition.TransitionListener {
        @Override
        public void onTransitionStart(Transition transition) {
        }

        @Override
        public void onTransitionEnd(Transition transition) {

        }

        @Override
        public void onTransitionCancel(Transition transition) {

        }

        @Override
        public void onTransitionPause(Transition transition) {

        }

        @Override
        public void onTransitionResume(Transition transition) {

        }
    }


}
