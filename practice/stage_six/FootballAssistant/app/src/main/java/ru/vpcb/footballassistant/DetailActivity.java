package ru.vpcb.footballassistant;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityOptionsCompat;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import ru.vpcb.footballassistant.data.FDCompetition;
import ru.vpcb.footballassistant.data.FDFixture;
import ru.vpcb.footballassistant.data.FDTeam;
import ru.vpcb.footballassistant.dbase.FDContract;
import ru.vpcb.footballassistant.dbase.FDLoader;
import ru.vpcb.footballassistant.dbase.FDProvider;
import ru.vpcb.footballassistant.services.UpdateService;
import ru.vpcb.footballassistant.utils.Config;
import ru.vpcb.footballassistant.utils.FDUtils;
import ru.vpcb.footballassistant.utils.FootballUtils;
import ru.vpcb.footballassistant.widgets.MatchWidgetService;
import timber.log.Timber;

import static android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;
import static ru.vpcb.footballassistant.utils.Config.BUNDLE_LOADER_DATA_BUNDLE;
import static ru.vpcb.footballassistant.utils.Config.BUNDLE_LOADER_DATA_URI;
import static ru.vpcb.footballassistant.utils.Config.BUNDLE_LOADER_DATE_CENTER;
import static ru.vpcb.footballassistant.utils.Config.BUNDLE_VIEWPAGER_POS;
import static ru.vpcb.footballassistant.utils.Config.BUNDLE_VIEWPAGER_POS_CLEAR;
import static ru.vpcb.footballassistant.utils.Config.BUNDLE_VIEWPAGER_SPAN_DEFAULT;

import static ru.vpcb.footballassistant.utils.Config.BUNDLE_VIEWPAGER_SPAN_LIMITS;
import static ru.vpcb.footballassistant.utils.Config.CALENDAR_DIALOG_ACTION_APPLY;
import static ru.vpcb.footballassistant.utils.Config.EMPTY_FIXTURE_DATE;
import static ru.vpcb.footballassistant.utils.Config.EMPTY_WIDGET_ID;
import static ru.vpcb.footballassistant.utils.Config.FRAGMENT_TEAM_TAG;
import static ru.vpcb.footballassistant.utils.Config.LOADERS_UPDATE_COUNTER;
import static ru.vpcb.footballassistant.utils.Config.MAIN_ACTIVITY_INDEFINITE;
import static ru.vpcb.footballassistant.utils.Config.MAIN_ACTIVITY_PROGRESS;
import static ru.vpcb.footballassistant.utils.Config.MATCH_FRAGMENT_TAG;
import static ru.vpcb.footballassistant.utils.Config.VIEWPAGER_BACK_DURATION;
import static ru.vpcb.footballassistant.utils.Config.VIEWPAGER_BACK_START_DELAY;
import static ru.vpcb.footballassistant.utils.Config.VIEWPAGER_OFF_SCREEN_PAGE_NUMBER;
import static ru.vpcb.footballassistant.utils.Config.WIDGET_BUNDLE_INTENT_EXTRA;
import static ru.vpcb.footballassistant.utils.Config.WIDGET_BUNDLE_WIDGET_ID;
import static ru.vpcb.footballassistant.utils.Config.WIDGET_INTENT_BUNDLE;
import static ru.vpcb.footballassistant.utils.FDUtils.cFx;
import static ru.vpcb.footballassistant.utils.FDUtils.formatDateFromSQLite;
import static ru.vpcb.footballassistant.utils.FDUtils.formatDateFromSQLiteZeroTime;
import static ru.vpcb.footballassistant.utils.FDUtils.formatDateToSQLite;
import static ru.vpcb.footballassistant.utils.FDUtils.setZeroTime;

public class DetailActivity extends AppCompatActivity
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
    private ImageView mViewPagerBack;
    private TabLayout mTabLayout;
    private View mWidgetBar;


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
    private Bundle mViewPagerBundle;
    private int mViewPagerPos;
    private Bundle mBundleExtra;


    // test!!!
// TODO  make parcelable for ViewPager and rotation
    private static ViewPagerData mViewPagerData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);

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
        mProgressValue = findViewById(R.id.progress_value);
        mToolbarLogo = findViewById(R.id.toolbar_logo);
        mBottomNavigation = findViewById(R.id.bottom_navigation);
        mViewPager = findViewById(R.id.viewpager_main);
        mViewPagerBack = findViewById(R.id.image_viewpager_back);
        mTabLayout = findViewById(R.id.toolbar_sliding_tabs);
        mWidgetBar = findViewById(R.id.match_widget_toolbar);


// params
        mState = MAIN_ACTIVITY_INDEFINITE;
        mCursors = new Cursor[5];
        // progress
        setupActionBar();
        setupBottomNavigation();
        setupProgress();
        setupReceiver();
        setupListeners();
        setupViewPager();

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            if (intent != null && intent.hasExtra(WIDGET_INTENT_BUNDLE)) {
                mBundleExtra = intent.getBundleExtra(WIDGET_INTENT_BUNDLE);
            }


            mViewPagerBundle = getDatesSpanBundle(Calendar.getInstance());
            mViewPagerPos = -1; // center of -span 0 span+
            mViewPagerBack.setVisibility(View.VISIBLE);
            mViewPager.setAlpha(0);
            mTabLayout.setAlpha(0);
        } else {
            mViewPagerBundle = savedInstanceState.getBundle(BUNDLE_LOADER_DATA_BUNDLE);
            mViewPagerPos = savedInstanceState.getInt(BUNDLE_VIEWPAGER_POS);
            mViewPagerBack.setVisibility(View.INVISIBLE);
            mBundleExtra = savedInstanceState.getBundle(WIDGET_BUNDLE_INTENT_EXTRA);
        }

        mViewPagerBack.setImageResource(FootballUtils.getImageBackId());


//            refresh(getString(R.string.action_update));

        startLoaders();


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle(BUNDLE_LOADER_DATA_BUNDLE, mViewPagerBundle);
        outState.putInt(BUNDLE_VIEWPAGER_POS, mViewPagerPos);
        outState.putBundle(WIDGET_BUNDLE_INTENT_EXTRA, mBundleExtra);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_match, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivitySettings();
            return true;
        }
        if (id == R.id.action_calendar) {
            startCalendar();
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
        return FDLoader.getInstance(this, id, args);
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

            mUpdateCounter = 0;
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
// cursors will be closed by supportLoaderManager().CursorLoader()

    }

    @Override
    public void onComplete(View view, int fixtureId) {
// widget
        if (mBundleExtra != null) {
            int widgetId = mBundleExtra.getInt(WIDGET_BUNDLE_WIDGET_ID, EMPTY_WIDGET_ID);
            MatchWidgetService.startFillWidgetAction(this, widgetId, fixtureId);
            mViewPagerBundle = null;
            mWidgetBar.setVisibility(View.INVISIBLE);
        }
//        startActivityMatch();
        FDFixture fixture = mMapFixtures.get(fixtureId);
        FDTeam homeTeam = mMapTeams.get(fixture.getHomeTeamId());
        FDTeam awayTeam = mMapTeams.get(fixture.getAwayTeamId());

        FDCompetition competition = mMap.get(fixture.getCompetitionId());
        if (competition != null) {
            fixture.setCompetitionName(competition.getCaption());
            fixture.setLeague(competition.getLeague());
        }

        startMatchFragment(fixture, homeTeam, awayTeam);
    }


    @Override
    public void onComplete(int mode, Calendar calendar) {

        try {
            if (mode == CALENDAR_DIALOG_ACTION_APPLY) {
                mViewPagerBundle = getDatesSpanBundle(calendar);
                mViewPagerPos = BUNDLE_VIEWPAGER_POS_CLEAR; // clear to fill
                restartLoaders();
            }

        } catch (NullPointerException e) {
            Timber.d(getString(R.string.calendar_set_date_exception, e.getMessage()));
        }

    }


    // methods


    public Map<Integer, FDCompetition> getMap() {
        return mMap;
    }

    public Map<Integer, FDTeam> getMapTeams() {
        return mMapTeams;
    }

    public Map<Integer, FDFixture> getMapFixtures() {
        return mMapFixtures;
    }

    // TODO Optimize loaders
    private void restartLoaders() {
        Bundle bundle = mViewPagerBundle;
        getSupportLoaderManager().initLoader(FDContract.CpEntry.LOADER_ID, null, this);
        getSupportLoaderManager().initLoader(FDContract.CpTmEntry.LOADER_ID, null, this);
        getSupportLoaderManager().initLoader(FDContract.CpFxEntry.LOADER_ID, null, this);
        getSupportLoaderManager().initLoader(FDContract.TmEntry.LOADER_ID, null, this);
        getSupportLoaderManager().restartLoader(FDContract.FxEntry.LOADER_ID, bundle, this);

    }

    private void startLoaders() {
        Bundle bundle = mViewPagerBundle;
        getSupportLoaderManager().initLoader(FDContract.CpEntry.LOADER_ID, null, this);
        getSupportLoaderManager().initLoader(FDContract.CpTmEntry.LOADER_ID, null, this);
        getSupportLoaderManager().initLoader(FDContract.CpFxEntry.LOADER_ID, null, this);
        getSupportLoaderManager().initLoader(FDContract.TmEntry.LOADER_ID, null, this);
        getSupportLoaderManager().initLoader(FDContract.FxEntry.LOADER_ID, bundle, this);

    }

    private int getFixtureDatesSpan() {
        String stringDateSpan = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getString(R.string.pref_date_span_key),
                        getString(R.string.pref_date_span_default));
        int dateSpan = BUNDLE_VIEWPAGER_SPAN_DEFAULT;
        try {
            dateSpan = Integer.valueOf(stringDateSpan);

        } catch (NumberFormatException e) {
        }
        return dateSpan;
    }

    private Bundle getDatesSpanBundle(Calendar c) {
        String dateBefore;
        String dateAfter;
        String dateCenter;

        int dateSpan = getFixtureDatesSpan();
        setZeroTime(c);

        if (dateSpan > 0) {

            dateCenter = formatDateToSQLite(c.getTime());
            c.add(Calendar.DATE, -dateSpan / 2);
            dateBefore = formatDateToSQLite(c.getTime());
            c.add(Calendar.DATE, dateSpan + (1 - (dateSpan % 2)));
            dateAfter = formatDateToSQLite(c.getTime());
        } else {
            dateCenter = formatDateToSQLite(c.getTime());
            c.add(Calendar.DATE, -BUNDLE_VIEWPAGER_SPAN_LIMITS);
            dateBefore = formatDateToSQLite(c.getTime());
            c.add(Calendar.DATE, BUNDLE_VIEWPAGER_SPAN_LIMITS * 2);
            dateAfter = formatDateToSQLite(c.getTime());
        }

        Bundle bundle = new Bundle();
//        bundle.putString(BUNDLE_LOADER_DATA_ID, dateBefore);
//        bundle.putString(BUNDLE_LOADER_DATA_ID2, dateAfter);
        bundle.putString(BUNDLE_LOADER_DATE_CENTER, dateCenter);

        Uri uri = FDProvider.buildLoaderIdUri(this, FDContract.FxEntry.LOADER_ID, dateBefore, dateAfter);
        bundle.putParcelable(BUNDLE_LOADER_DATA_URI, uri);
        return bundle;
    }

    private void startActivitySettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP); // clear stack  top parent remained
        Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(this,
                android.R.anim.fade_in, android.R.anim.fade_out)
                .toBundle();
        startActivity(intent, bundle);
        finish();
    }


    private void startActivityFavorites() {
        Intent intent = new Intent(this, FavoritesActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP); // clear stack  top parent remained
        Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(this,
                android.R.anim.fade_in, android.R.anim.fade_out)
                .toBundle();
        startActivity(intent, bundle);
        finish();
    }


//    private void startActivityNews() {
//        Intent intent = new Intent(this, NewsActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // clear stack hard but flashes fade in out
////        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP); // clear stack  top parent remained
//        startActivity(intent);
//        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);  // standard transition
//    }

    private void startActivityNews() {
        Intent intent = new Intent(this, NewsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP); // clear stack  top parent remained
        Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(this,
                android.R.anim.fade_in, android.R.anim.fade_out)
                .toBundle();
        startActivity(intent, bundle);
        finish();
    }

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

    private void startMatchFragment(FDFixture fixture, FDTeam homeTeam, FDTeam awayTeam) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = MatchFragment.newInstance(fixture, homeTeam, awayTeam);
        fm.popBackStackImmediate(MATCH_FRAGMENT_TAG, POP_BACK_STACK_INCLUSIVE);
        fm.beginTransaction()
                .replace(R.id.container_detail, fragment)
                .addToBackStack(MATCH_FRAGMENT_TAG)
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

        mFab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }


    private void startActivityMatch() {
        Intent intent = new Intent(this, MatchActivity.class);
        startActivity(intent);
    }

    // test!!!
// TODO Check SQLite Date Format
    private long getViewPagerDate(int index) {
        try {
            String s = mViewPagerData.mList.get(index).get(0).getDate();
            Calendar c = FDUtils.getCalendarFromSQLite(s);
            if (c == null) return -1;
            setZeroTime(c);
            return c.getTimeInMillis();
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            return -1;
        }
    }

    // test!!!
// TODO Check SQLite Date Format
    private Calendar getViewPagerDate() {
        try {
            String s = mViewPagerData.mList.get(mViewPager.getCurrentItem()).get(0).getDate();
            Calendar c = FDUtils.getCalendarFromSQLite(s);
            if (c == null) return null;
            setZeroTime(c);
            return c;
        } catch (NullPointerException e) {
            return null;
        }
    }

    // test!!!
// TODO SQLIte Date Check
    private int getIndex(List<FDFixture> list, Calendar c) {
        String dateSQLite = FDUtils.formatDateToSQLite(c.getTime());
        FDFixture fixture = new FDFixture();
        fixture.setDate(dateSQLite);
        int index = Collections.binarySearch(list, fixture, cFx);  // for givent day
        if (index < 0) index = -index - 1;
        if (index > list.size()) index = list.size() - 1;
        return index;

    }

    private void startCalendar() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = CalendarDialog.newInstance(this, getViewPagerDate());
        fm.beginTransaction()
                .add(fragment, getString(R.string.calendar_title))
                .commit();

    }

    private RecyclerView getRecycler(List<FDFixture> list) {
        Config.Span sp = Config.getDisplayMetrics(this);

        View recyclerLayout = getLayoutInflater().inflate(R.layout.recycler_main, null);
        RecyclerView recyclerView = recyclerLayout.findViewById(R.id.recycler_main_container);

        RecyclerDetailAdapter adapter = new RecyclerDetailAdapter(this, list, mMapTeams);
        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        return recyclerView;
    }


//    private void setupViewPagerSource() {
//        int last = 0;
//        int next = 0;
//        int current = 0;
//        List<FDFixture> fixtures = new ArrayList<>(mMapFixtures.values()); // sorted by date
//        Map<Long, Integer> map = new HashMap<>();
//
//        Collections.sort(fixtures, cFx);
//        List<List<FDFixture>> list = new ArrayList<>();
//
//        Calendar c = Calendar.getInstance();
//        setZeroTime(c);
//        current = getIndex(fixtures, c);  // index of current day
//
//        while (next < fixtures.size()) {
//            setDay(c, fixtures.get(next).getDate());
//            map.put(c.getTimeInMillis(), list.size());
//            c.add(Calendar.DATE, 1);  // next day
//            next = getIndex(fixtures, c);
//            list.add(new ArrayList<>(fixtures.subList(last, next)));
//            last = next;
//            if (next == current) current = list.size();  // index of current day records
//        }
//
//        mViewPagerPos = current;
//        mViewPagerList = list;
//        mViewPagerMap = map;
//    }

    private List<Long> getTimesList(List<FDFixture> fixtures) {
        List<Long> list = new ArrayList<>();
        for (int i = 0; i < fixtures.size(); i++) {
            long time = formatDateFromSQLite(fixtures.get(i).getDate()).getTime();
            list.add(time);
        }
        return list;
    }

    private int getIndex(List<Long> list, long time) {
        int index = Collections.binarySearch(list, time);
        if (index < 0) index = -index - 1;
        if (index < 0) return 0;
        if (index >= list.size()) return list.size() - 1;
        return index;

    }

    private ViewPagerData getViewPagerData() {
        int last = 0;
        int next = 0;
        int current = 0;
        List<FDFixture> fixtures = new ArrayList<>(mMapFixtures.values()); // sorted by date
        Map<Long, Integer> map = new HashMap<>();

//        Collections.sort(fixtures, cFx);
        List<List<FDFixture>> list = new ArrayList<>();

        Date date = formatDateFromSQLiteZeroTime(mViewPagerBundle.getString(BUNDLE_LOADER_DATE_CENTER));

        List<Long> times = getTimesList(fixtures);
        current = getIndex(times, date.getTime());

        while (next < times.size() - 1) {
            date = formatDateFromSQLiteZeroTime(fixtures.get(next).getDate());
            if (date == null) {  // skip wrong date
                next++;
                continue;
            }
            long time = date.getTime();
//            map.put(time, list.size());
            time += TimeUnit.DAYS.toMillis(1);
            next = getIndex(times, time);
            list.add(new ArrayList<>(fixtures.subList(last, next)));
            last = next;
            if (next == current) {
                if (mViewPagerPos == -1)
                    mViewPagerPos = list.size();  // index of current day records
            }
        }


        List<View> recyclers = new ArrayList<>();
        List<String> titles = new ArrayList<>();


        for (List<FDFixture> listFixtures : list) {
            recyclers.add(getRecycler(listFixtures));
            titles.add(getRecyclerTitle(listFixtures));
        }

        ViewPagerData viewPagerData = new ViewPagerData(recyclers, titles, current, list, map);
        return viewPagerData;
    }

    private String getRecyclerTitle(List<FDFixture> list) {
        try {
            return FDUtils.formatMatchDate(list.get(0).getDate());
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
                mViewPagerPos = position;
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
        mViewPager.setCurrentItem(mViewPagerData.getPos());
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

    private void updateTabLayout(ViewPagerData data, ViewPagerData last) {
        try {

            List<String> titles = data.getTitles();
            int lastSize = last.getTitles().size();

            int size = data.getTitles().size();
            if (size < lastSize) {
                for (int i = size; i < lastSize; i++) {
                    mTabLayout.removeTabAt(size);
                }
            } else {
                for (int i = lastSize; i < size; i++) {
                    mTabLayout.addTab(mTabLayout.newTab());
                }
            }
            for (int i = 0; i < size; i++) {
                mTabLayout.getTabAt(i).setText(titles.get(i));
            }
        } catch (NullPointerException e) {
            Timber.d(getString(R.string.viewpager_tab_exception, e.getMessage()));
        }
    }

    private void updateViewPager(final ViewPagerData data) {
        if (mViewPager == null || data == null) return;
//        int pos = mViewPager.getCurrentItem();
//
//
//        if (pos == 0) {
//            pos = data.mPos;                    // current day
//        } else {
//
//// test!!!
//// TODO CHECK MAP  AFTER deletion  all works but Map
////            data.getList().remove(210);
////            data.getList().remove(211);
////            data.getList().remove(212);
////            data.getRecyclers().remove(210);
////            data.getRecyclers().remove(211);
////            data.getRecyclers().remove(212);
////            data.getTitles().remove(210);
////            data.getTitles().remove(211);
////            data.getTitles().remove(212);
////            List<Long> keys = new ArrayList<>(data.getMap().keySet());
////            data.getMap().remove(keys.get(210));
////            data.getMap().remove(keys.get(212));
////            data.getMap().remove(keys.get(214));
//// end test!!!
//
//            updateTabLayout(data, mViewPagerData);
//            if (pos >= data.mRecyclers.size()) pos = data.mRecyclers.size() - 1;
//        }


        mViewPagerData = data;
        ((ViewPagerAdapter) mViewPager.getAdapter()).swap(data.mRecyclers, data.mTitles);
        mViewPager.setCurrentItem(mViewPagerPos);
// animation
        mViewPagerBack.animate().alpha(0).setStartDelay(VIEWPAGER_BACK_START_DELAY)
                .setDuration(VIEWPAGER_BACK_DURATION).start();
        if (mViewPager.getAlpha() == 0) {
            mViewPager.animate()
                    .alpha(1)
                    .setStartDelay(VIEWPAGER_BACK_START_DELAY)
                    .setDuration(VIEWPAGER_BACK_DURATION).start();
            mTabLayout.animate()
                    .alpha(1)
                    .setStartDelay(VIEWPAGER_BACK_START_DELAY)
                    .setDuration(VIEWPAGER_BACK_DURATION).start();
        }
// tab_layout scrolling
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mTabLayout.setScrollPosition(mViewPagerPos, 0, false);
            }
        });

// widget support
        if (mBundleExtra != null && mBundleExtra.getInt(WIDGET_BUNDLE_WIDGET_ID) > 0) {
            mWidgetBar.setVisibility(View.VISIBLE);
            mWidgetBar.animate()
                    .alpha(1)
                    .setStartDelay(VIEWPAGER_BACK_START_DELAY)
                    .setDuration(VIEWPAGER_BACK_DURATION).start();
        }

    }

//    private void setupViewPager2() {
//        if (mViewPagerList == null) return;
//
//        List<View> recyclers = new ArrayList<>();
//        List<String> titles = new ArrayList<>();
//
//
//        for (List<FDFixture> list : mViewPagerList) {
//            recyclers.add(getRecycler(list));
//            titles.add(getRecyclerTitle(list));
//        }
//
//        ViewPagerAdapter listPagerAdapter = new ViewPagerAdapter(recyclers, titles);
//        mViewPager.setAdapter(listPagerAdapter);
//        mViewPager.setCurrentItem(mViewPagerPos, true);
//        mViewPager.setOffscreenPageLimit(VIEWPAGER_OFF_SCREEN_PAGE_NUMBER);  //    ATTENTION  Prevents Adapter Exception
//        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//            }
//        });
//
//        mTabLayout.setupWithViewPager(mViewPager);
//    }


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
        toolbar.setTitle(getString(R.string.screen_match));
        setSupportActionBar(toolbar);

        mToolbarLogo.setVisibility(View.INVISIBLE);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("");
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
        intentFilter.addAction(getString(R.string.broadcast_data_update_started));
        intentFilter.addAction(getString(R.string.broadcast_data_update_finished));
        intentFilter.addAction(getString(R.string.broadcast_data_no_network));
        intentFilter.addAction(getString(R.string.broadcast_data_update_progress));
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
                if (action.equals(context.getString(R.string.broadcast_data_update_started))) {

                } else if (action.equals(context.getString(R.string.broadcast_data_update_finished))) {

                } else if (action.equals(context.getString(R.string.broadcast_data_update_progress))) {

                } else if (action.equals(context.getString(R.string.broadcast_data_no_network))) {
                    Toast.makeText(context, "Broadcast message: no network", Toast.LENGTH_SHORT).show();
                } else {
                    throw new UnsupportedOperationException("Not yet implemented");
                }

            }

        }
    }

    private void setupBottomNavigation() {
        mBottomNavigation = findViewById(R.id.bottom_navigation);
        mBottomNavigation.setSelectedItemId(R.id.navigation_matches);
        mBottomNavigation.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        View rootView = getWindow().getDecorView();

                        Context context = DetailActivity.this;
                        switch (item.getItemId()) {
                            case R.id.navigation_matches:
                                Toast.makeText(context, getString(R.string.activity_same_message),
                                        Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.navigation_news:
                                startActivityNews();
                                return true;
                            case R.id.navigation_favorites:
                                startActivityFavorites();
                                return true;
                            case R.id.navigation_settings:
                                startActivitySettings();
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

    // TODO Make encapsulation data and maps to ViewPager and other Activities
    private class DataLoader extends AsyncTask<Cursor[], Void, ViewPagerData> {


        @Override
        protected ViewPagerData doInBackground(Cursor[]... cursors) {
            try {

                mMap = FDUtils.readCompetitions(cursors[0][0]);
                mMapTeamKeys = FDUtils.readCompetitionTeams(cursors[0][1]);
                mMapTeams = FDUtils.readTeams(cursors[0][2]);
                mMapFixtureKeys = FDUtils.readCompetitionFixtures(cursors[0][3]);
                mMapFixtures = FDUtils.readFixtures(cursors[0][4]);
                return getViewPagerData();


            } catch (NullPointerException e) {
                return null;

            }
        }

        @Override
        protected void onPostExecute(ViewPagerData viewPagerData) {
            stopProgress();
            if (viewPagerData == null) return;

            mViewPagerData = viewPagerData;
            updateViewPager(viewPagerData);


        }

    }


}
