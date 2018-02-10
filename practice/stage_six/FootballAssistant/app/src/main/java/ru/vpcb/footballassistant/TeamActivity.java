package ru.vpcb.footballassistant;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import java.util.concurrent.TimeUnit;

import ru.vpcb.footballassistant.data.FDCompetition;
import ru.vpcb.footballassistant.data.FDFixture;
import ru.vpcb.footballassistant.data.FDPlayer;
import ru.vpcb.footballassistant.data.FDTeam;
import ru.vpcb.footballassistant.dbase.FDContract;
import ru.vpcb.footballassistant.dbase.FDLoader;
import ru.vpcb.footballassistant.services.UpdateService;
import ru.vpcb.footballassistant.utils.Config;
import ru.vpcb.footballassistant.utils.FDUtils;
import ru.vpcb.footballassistant.utils.FootballUtils;
import timber.log.Timber;

import static android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;
import static ru.vpcb.footballassistant.utils.Config.BUNDLE_INTENT_TEAM_ID;
import static ru.vpcb.footballassistant.utils.Config.BUNDLE_TEAM_ID;
import static ru.vpcb.footballassistant.utils.Config.CALENDAR_DIALOG_ACTION_APPLY;
import static ru.vpcb.footballassistant.utils.Config.EMPTY_FIXTURE_DATE;
import static ru.vpcb.footballassistant.utils.Config.EMPTY_TEAM_ID;
import static ru.vpcb.footballassistant.utils.Config.BUNDLE_TEAM_VIEWPAGER_POS;
import static ru.vpcb.footballassistant.utils.Config.FRAGMENT_TEAM_TAG;
import static ru.vpcb.footballassistant.utils.Config.FRAGMENT_TEAM_VIEWPAGER_TEAM_POS;
import static ru.vpcb.footballassistant.utils.Config.LOADERS_UPDATE_COUNTER;
import static ru.vpcb.footballassistant.utils.Config.MAIN_ACTIVITY_INDEFINITE;
import static ru.vpcb.footballassistant.utils.Config.MAIN_ACTIVITY_PROGRESS;
import static ru.vpcb.footballassistant.utils.Config.VIEWPAGER_OFF_SCREEN_PAGE_NUMBER;

public class TeamActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>, ICallback {

    private static boolean sIsTimber;
    private static Handler mHandler;

    // test!!!
// TODO  make parcelable for ViewPager and rotation
    private static ViewPagerData mViewPagerData;


    private FloatingActionButton mFab;


    private ProgressBar mProgressBar;
    private ProgressBar mProgressValue;
    private TextView mProgressText;
    private ImageView mToolbarLogo;

    private RecyclerView mRecyclerView;
    private ViewPager mViewPager;
    private ImageView mViewPagerBack;
    private TabLayout mTabLayout;


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


    private Cursor[] mCursors;
    private boolean mIsRotated;
    private int mViewPagerPos;
    private int mTeamId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.team_activity);

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
        mProgressValue = findViewById(R.id.progress_value);
        mToolbarLogo = findViewById(R.id.toolbar_logo);
        mBottomNavigation = findViewById(R.id.bottom_navigation);
        mViewPager = findViewById(R.id.viewpager_main);
        mViewPagerBack = findViewById(R.id.image_viewpager_back);
        mTabLayout = findViewById(R.id.toolbar_sliding_tabs);


// params
        mState = MAIN_ACTIVITY_INDEFINITE;
        mCursors = new Cursor[5];

// progress
        setupActionBar();
        setupBottomNavigation();
        setupProgress();
        setupReceiver();
        setupListeners();

// test!!!  check data
        if (mViewPagerData == null) {
            setupViewPager();
        } else {
            setupViewPager(mViewPagerData);
        }

        mViewPagerBack.setImageResource(FootballUtils.getImageBackId());


        if (savedInstanceState == null) {
            Intent intent = getIntent();
            mTeamId = EMPTY_TEAM_ID;
            if (intent != null && intent.hasExtra(BUNDLE_INTENT_TEAM_ID)) {
                mTeamId = intent.getIntExtra(BUNDLE_INTENT_TEAM_ID, EMPTY_TEAM_ID);
            }

            refresh(getString(R.string.action_update));
            getSupportLoaderManager().initLoader(FDContract.CpEntry.LOADER_ID, null, this);
            getSupportLoaderManager().initLoader(FDContract.CpTmEntry.LOADER_ID, null, this);
            getSupportLoaderManager().initLoader(FDContract.CpFxEntry.LOADER_ID, null, this);
            getSupportLoaderManager().initLoader(FDContract.TmEntry.LOADER_ID, null, this);
            getSupportLoaderManager().initLoader(FDContract.FxEntry.LOADER_ID, null, this);
        } else {
            mIsRotated = true;
            mViewPagerPos = savedInstanceState.getInt(BUNDLE_TEAM_VIEWPAGER_POS, FRAGMENT_TEAM_VIEWPAGER_TEAM_POS);
            mTeamId = savedInstanceState.getInt(BUNDLE_TEAM_ID, EMPTY_TEAM_ID);
        }


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(BUNDLE_TEAM_VIEWPAGER_POS, mViewPagerPos);
        outState.putInt(BUNDLE_TEAM_ID, mTeamId);
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
            Snackbar.make(getWindow().getDecorView(), "Action Share",
                    Snackbar.LENGTH_SHORT).show();
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
                mCursors[0] = cursor;
//                mMap = FDUtils.readCompetitions(cursor);
                mUpdateCounter++;
                break;

            case FDContract.CpTmEntry.LOADER_ID:
                mCursors[1] = cursor;
//                mMapTeamKeys = FDUtils.readCompetitionTeams(cursor);
                mUpdateCounter++;
                break;

            case FDContract.TmEntry.LOADER_ID:
                mCursors[2] = cursor;
//                mMapTeams = FDUtils.readTeams(cursor);
                mUpdateCounter++;
                break;

            case FDContract.CpFxEntry.LOADER_ID:
                mCursors[3] = cursor;
//                mMapFixtureKeys = FDUtils.readCompetitionFixtures(cursor);
                mUpdateCounter++;
                break;

            case FDContract.FxEntry.LOADER_ID:
                mCursors[4] = cursor;
//                mMapFixtures = FDUtils.readFixtures(cursor);
                mUpdateCounter++;
                break;

            case FDContract.TbEntry.LOADER_ID:
                break;

            case FDContract.PlEntry.LOADER_ID:
                break;

            default:
                throw new IllegalArgumentException("Unknown id: " + loader.getId());
        }


        if (mUpdateCounter == LOADERS_UPDATE_COUNTER) {
//            setupViewPagerSource();
//            setupViewPager();
//            stopProgress();

            new DataLoader().execute(mCursors);
            new DataDownLoader().execute(mTeamId);

            mUpdateCounter = 0;
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
// cursors will be closed by supportLoaderManager().CursorLoader()

    }

    @Override
    public void onComplete(View view, int pos) {
        Snackbar.make(view, "Recycler item clicked", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        startMatchActivity();
    }

    @Override
    public void onComplete(int mode, Calendar calendar) {

        try {
            if (mode == CALENDAR_DIALOG_ACTION_APPLY) {
                setZeroTime(calendar);
                long time = calendar.getTimeInMillis();
                long first = getViewPagerDate(0);
                long last = getViewPagerDate(mViewPagerData.getList().size() - 1);
                if (first > 0 && time < first) time = first;
                if (last > 0 && time > last) time = last;


                Integer pos = mViewPagerData.getMap().get(time);

                while (pos == null && time < last) {
                    time += TimeUnit.DAYS.toMillis(1);
                    pos = mViewPagerData.getMap().get(time);
                }
                if (pos != null) mViewPager.setCurrentItem(pos, true);
            }

        } catch (NullPointerException e) {
            Timber.d(getString(R.string.calendar_set_date_exception, e.getMessage()));
        }

    }


    // methods

    private void startFragmentLeague() {

    }

    private void startFragmentTeam() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = TeamFragment.newInstance();

        fm.popBackStackImmediate(FRAGMENT_TEAM_TAG, POP_BACK_STACK_INCLUSIVE);
        fm.beginTransaction()
                .replace(R.id.container_detail, fragment)
                .addToBackStack(FRAGMENT_TEAM_TAG)
                .commit();

    }

    private void setupListeners() {
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

    }


    private void startMatchActivity() {
        Intent intent = new Intent(this, MatchActivity.class);
        startActivity(intent);
    }

    // test!!!
    private long getViewPagerDate(int index) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(mViewPagerData.mList.get(index).get(0).getDate());
            setZeroTime(calendar);
            return calendar.getTimeInMillis();
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            return -1;
        }
    }

    // test!!!
    private Calendar getViewPagerDate() {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(mViewPagerData.mList.get(mViewPager.getCurrentItem()).get(0).getDate());
            setZeroTime(calendar);
            return calendar;
        } catch (NullPointerException e) {
            return null;
        }
    }

    private void startCalendar() {

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = CalendarDialog.newInstance(this, getViewPagerDate());
        fm.beginTransaction()
                .add(fragment, getString(R.string.calendar_title))
                .commit();

    }

    private RecyclerView getRecyclerTeam(List<FDFixture> list) {
        Config.Span sp = Config.getDisplayMetrics(this);

        View recyclerLayout = getLayoutInflater().inflate(R.layout.recycler_team, null);
        RecyclerView recyclerView = recyclerLayout.findViewById(R.id.recycler_team_container);

        RecyclerTeamAdapter adapter = new RecyclerTeamAdapter(this, list, mMap, mMapTeams);
        adapter.setHasStableIds(true); // optimization, bindView() not called for the same position
        recyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        return recyclerView;
    }

    private RecyclerView getRecyclerPlayer(List<FDPlayer> list) {
        Config.Span sp = Config.getDisplayMetrics(this);

        View recyclerLayout = getLayoutInflater().inflate(R.layout.recycler_team, null);
        RecyclerView recyclerView = recyclerLayout.findViewById(R.id.recycler_team_container);

        RecyclerPlayerAdapter adapter = new RecyclerPlayerAdapter(this, sp, list);
        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        return recyclerView;
    }


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


    private void setDay(Calendar c, Date date) {
        c.setTime(date);
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
    }

    private int getIndex(List<FDFixture> list, Calendar c) {
        int index = Collections.binarySearch(list, new FDFixture(c.getTime()), cFx);  // for givent day
        if (index < 0) index = -index - 1;
        if (index > list.size()) index = list.size() - 1;
        return index;
    }

    private ViewPagerData getViewPagerData(List<FDFixture> fixtures, List<FDPlayer> players) {
// fixtures
// players of any team

        List<View> recyclers = new ArrayList<>();
        List<String> titles = new ArrayList<>();

        if (fixtures != null && fixtures.size() > 0) {
            recyclers.add(getRecyclerTeam(fixtures));
        }

        if (players != null && players.size() > 0) {
            recyclers.add(getRecyclerPlayer(players));
        }

        titles.add("Matches");
        titles.add("Players");

        ViewPagerData viewPagerData = new ViewPagerData(recyclers, titles, 0, null, null);
        return viewPagerData;
    }

    private String getRecyclerTitle(List<FDFixture> list) {
        try {
            return FootballUtils.formatStringDate(list.get(0).getDate());
        } catch (NullPointerException e) {
            return EMPTY_FIXTURE_DATE;
        }
    }

    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this, null, null);
        mViewPager.setAdapter(adapter);
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

        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void setupViewPager(ViewPagerData viewPagerData) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this, viewPagerData.getRecyclers(), viewPagerData.getTitles());
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(mViewPagerPos);
        mViewPager.setOffscreenPageLimit(VIEWPAGER_OFF_SCREEN_PAGE_NUMBER);  //    ATTENTION  Prevents Adapter Exception
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mViewPagerPos = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mTabLayout.setupWithViewPager(mViewPager, false);
    }

//    private void setupViewPager(ViewPagerDataExt data) {
//        ViewPagerAdapter adapter = new ViewPagerAdapter(data.mRecyclers, data.mTitles);
//        mViewPager.setAdapter(adapter);
//        mViewPager.setCurrentItem(data.mPos, true);
//        mViewPager.setOffscreenPageLimit(VIEWPAGER_OFF_SCREEN_PAGE_NUMBER);  //    ATTENTION  Prevents Adapter Exception
//        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//            }
//        });
//
//        mTabLayout.setupWithViewPager(mViewPager);
//    }

// test!!!  update mTabLayout workaround
// TODO Check workaround of TabLayout update for better solution

    private void updateViewPager(final ViewPagerData data) {
        stopProgress();
        if (mViewPager == null || data == null) return;


        mViewPagerData = data;
        ((ViewPagerAdapter) mViewPager.getAdapter()).swap(data.mRecyclers, data.mTitles);
        mViewPager.setCurrentItem(mViewPagerPos);

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

    private void stopProgress() {
        mProgressValue.setVisibility(View.INVISIBLE);
    }


    private void setupProgress() {
        mProgressValue.setIndeterminate(true);
        mProgressValue.setVisibility(View.INVISIBLE);
    }


    private void setupActionBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.screen_team));
        setSupportActionBar(toolbar);
        if (mToolbarLogo != null) {
            mToolbarLogo.setVisibility(View.INVISIBLE);
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.show();
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

                } else if (action.equals(context.getString(R.string.broadcast_update_finished))) {

                } else if (action.equals(context.getString(R.string.broadcast_update_progress))) {

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
        mBottomNavigation.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        View rootView = getWindow().getDecorView();

                        Context context = TeamActivity.this;
                        switch (item.getItemId()) {
                            case R.id.navigation_matches:
                                Toast.makeText(context, "Action matches", Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.navigation_news:
                                Toast.makeText(context, "Action news", Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.navigation_favorites:
                                Toast.makeText(context, "Action favorites", Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.navigation_settings:
                                Toast.makeText(context, "Action settings", Toast.LENGTH_SHORT).show();
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

    private class DataParam {
        private Cursor[] cursors;

    }

    private class ViewPagerData {
        private List<View> mRecyclers;
        private List<String> mTitles;
        private int mPos;
        private List<List<FDFixture>> mList;
        private Map<Long, Integer> mMap;


        public ViewPagerData(List<View> recyclers, List<String> titles, int pos,
                             List<List<FDFixture>> list,
                             Map<Long, Integer> map) {
            this.mRecyclers = recyclers;
            this.mTitles = titles;
            this.mPos = pos;
            this.mList = list;
            this.mMap = map;

        }

        public List<View> getRecyclers() {
            return mRecyclers;
        }

        public List<String> getTitles() {
            return mTitles;
        }

        public int getPos() {
            return mPos;
        }

        public List<List<FDFixture>> getList() {
            return mList;
        }

        public Map<Long, Integer> getMap() {
            return mMap;
        }
    }

    // test!!!
// TODO  Replace AsyncTask with Static or move code to standard Loader
// TODO Make encapsulation data and maps to ViewPager and other Activities
    private class DataLoader extends AsyncTask<Cursor[], Void, ViewPagerData> {
        @Override
        protected ViewPagerData doInBackground(Cursor[]... cursors) {
            try {
                mMap = FDUtils.readCompetitions(cursors[0][0]);
//                mMapTeamKeys = FDUtils.readCompetitionTeams(cursors[0][1]);
                mMapTeams = FDUtils.readTeams(cursors[0][2]);
//                mMapFixtureKeys = FDUtils.readCompetitionFixtures(cursors[0][3]);
//                mMapFixtures = FDUtils.readFixtures(cursors[0][4]);
                return null;
            } catch (NullPointerException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(ViewPagerData viewPagerData) {

        }
    }

    // test!!!
// TODO  Replace AsyncTask with Static or move code to standard Loader
    private class DataDownLoader extends AsyncTask<Integer, Void, ViewPagerData> {
        private Context mContext;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mContext = TeamActivity.this;
        }

        @Override
        protected ViewPagerData doInBackground(Integer... integers) {
            try {

                if (integers[0] <= 0) return null;
                int teamId = integers[0];

                List<FDFixture> fixtures = FDUtils.loadListTeamFixtures(mContext, teamId);
                List<FDPlayer> players = FDUtils.loadListTeamPlayers(mContext, teamId);
                return getViewPagerData(fixtures, players);
            } catch (NullPointerException e) {
                return null;

            }
        }

        @Override
        protected void onPostExecute(ViewPagerData viewPagerData) {
            updateViewPager(viewPagerData);


        }

    }


}
