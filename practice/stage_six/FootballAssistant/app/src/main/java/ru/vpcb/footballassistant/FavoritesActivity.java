package ru.vpcb.footballassistant;


import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ru.vpcb.footballassistant.data.FDCompetition;
import ru.vpcb.footballassistant.data.FDFixture;
import ru.vpcb.footballassistant.dbase.FDContract;
import ru.vpcb.footballassistant.dbase.FDLoader;
import ru.vpcb.footballassistant.dbase.FDProvider;
import ru.vpcb.footballassistant.services.UpdateService;
import ru.vpcb.footballassistant.utils.Config;
import ru.vpcb.footballassistant.utils.FDUtils;
import ru.vpcb.footballassistant.utils.FootballUtils;
import timber.log.Timber;

import static android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;
import static ru.vpcb.footballassistant.utils.Config.BUNDLE_LOADER_DATA_URI;
import static ru.vpcb.footballassistant.utils.Config.CP_IS_FAVORITE_SEARCH_KEY;
import static ru.vpcb.footballassistant.utils.Config.EMPTY_DASH;
import static ru.vpcb.footballassistant.utils.Config.FRAGMENT_TEAM_TAG;
import static ru.vpcb.footballassistant.utils.Config.MAIN_ACTIVITY_INDEFINITE;
import static ru.vpcb.footballassistant.utils.FDUtils.cFx;
import static ru.vpcb.footballassistant.utils.FDUtils.formatDateFromSQLite;
import static ru.vpcb.footballassistant.utils.FDUtils.setDay;
import static ru.vpcb.footballassistant.utils.FDUtils.setZeroTime;

public class FavoritesActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>, ICallback, IReload {

    private static boolean sIsTimber;
    private static Handler mHandler;
    private static FavoriteAsyncTask mFavoriteTask;

    private FloatingActionButton mFab;
    private FloatingActionButton mFab2;

    private ProgressBar mProgressBar;
    private ProgressBar mProgressValue;
    private TextView mProgressText;


    private RecyclerView mRecycler;


    private BottomNavigationView mBottomNavigation;
    private BottomNavigationView.OnNavigationItemSelectedListener mBottomNavigationListener;


    private Map<Integer, FDFixture> mMapFixtures;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.favorites_activity);

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

        mBottomNavigation = findViewById(R.id.bottom_navigation);
        mRecycler = findViewById(R.id.recyclerview_main);


// params
        mMapFixtures = new LinkedHashMap<>();

// progress
        setupActionBar();
        setupBottomNavigation();
        setupProgress();
        setupListeners();
        setupRecycler();


        startLoaders();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_reload, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_reload) {
            restartLoaders();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // callbacks
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return FDLoader.getInstance(this, id, args);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (loader == null || loader.getId() <= 0 || cursor == null) {
            return;
        }

        if (cursor.getCount() == 0) {
            stopProgress();
            FootballUtils.showMessage(this, getString(R.string.favorites_database_empty));
            return;
        }
        switch (loader.getId()) {
            case FDContract.FxEntry.LOADER_ID:
                Map<Integer, FDFixture> mapFixtures = FDUtils.readFixtures(cursor);
                if (mapFixtures == null || mapFixtures.isEmpty()) {
                    break;
                }
                if (mMapFixtures == null) mMapFixtures = new LinkedHashMap<>(); // fixed order
                mMapFixtures.clear();
                mMapFixtures.putAll(mapFixtures);
                if (mRecycler == null) return;


                bindViews();

                break;

            default:
                throw new IllegalArgumentException("Unknown id: " + loader.getId());
        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
// cursors will be closed by supportLoaderManager().CursorLoader()
    }

    @Override
    public void onComplete(View view, int fixtureId) {
        FDFixture fixture = mMapFixtures.get(fixtureId);
        if (fixture == null || fixture.getId() <= 0) return;


        fixture.setFavorite(!fixture.isFavorite());  // check/uncheck
        mFavoriteTask = new FavoriteAsyncTask(this, fixture, this);
        mFavoriteTask.execute();

    }

    @Override
    public void onComplete(int mode, Calendar calendar) {

    }

    @Override
    public void onComplete(View view, String link, String title) {

    }

    @Override
    public void onReload() {
        bindViews();
    }


    // methods
    private List<FDFixture> getList(Map<Integer, FDFixture> map) {
        List<FDFixture> list = new ArrayList<>();

        for (FDFixture fixture : map.values()) {
            if (fixture == null || fixture.getId() <= 0) continue;
            list.add(fixture);
        }
        return list;
    }

    private int count = 0;

    private void bindViews() {
        stopProgress();
        if (mMapFixtures == null || mRecycler == null) return;

        final List<FDFixture> list = getList(mMapFixtures);

        RecyclerDetailAdapter adapter = (RecyclerDetailAdapter) mRecycler.getAdapter();
        adapter.swap(list);

    }


    private void restartLoaders() {

        Uri uri = FDProvider.buildLoaderIdUri(this, FDContract.FxEntry.LOADER_ID,
                CP_IS_FAVORITE_SEARCH_KEY, EMPTY_DASH);
        Bundle bundle = new Bundle();
        bundle.putParcelable(BUNDLE_LOADER_DATA_URI, uri);              // uri/#/*


        getSupportLoaderManager().restartLoader(FDContract.FxEntry.LOADER_ID, bundle, this);

    }

    private void startLoaders() {

        Uri uri = FDProvider.buildLoaderIdUri(this, FDContract.FxEntry.LOADER_ID,
                CP_IS_FAVORITE_SEARCH_KEY, EMPTY_DASH);
        Bundle bundle = new Bundle();
        bundle.putParcelable(BUNDLE_LOADER_DATA_URI, uri);              // uri/#/*

        getSupportLoaderManager().initLoader(FDContract.FxEntry.LOADER_ID, bundle, this);

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

    private void startActivityNews() {
        Intent intent = new Intent(this, NewsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP); // clear stack  top parent remained
        Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(this,
                android.R.anim.fade_in, android.R.anim.fade_out)
                .toBundle();
        startActivity(intent, bundle);
        finish();
    }

    private void startActivityMatches() {
        Intent intent = new Intent(this, DetailActivity.class);
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
                .replace(R.id.container_favorites, fragment)
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

        mFab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


    }


    private void startMatchActivity() {
        Intent intent = new Intent(this, MatchActivity.class);
        startActivity(intent);
    }

//    // test!!!
//// TODO Check SQLite Date Format
//    private long getViewPagerDate(int index) {
//        try {
//            String s = mViewPagerData.mList.get(index).get(0).getDate();
//            Calendar c = FDUtils.getCalendarFromSQLite(s);
//            if (c == null) return -1;
//            setZeroTime(c);
//            return c.getTimeInMillis();
//        } catch (NullPointerException | IndexOutOfBoundsException e) {
//            return -1;
//        }
//    }


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

    // test!!!
    private RecyclerView getRecycler(List<FDFixture> list) {
        Config.Span sp = Config.getDisplayMetrics(this);

        View recyclerLayout = getLayoutInflater().inflate(R.layout.recycler_main, null);
        RecyclerView recyclerView = recyclerLayout.findViewById(R.id.recycler_main_container);

        RecyclerDetailAdapter adapter = new RecyclerDetailAdapter(this, list, null);
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


    private void setupRecycler() {
        RecyclerDetailAdapter adapter = new RecyclerDetailAdapter(this, null, null);
        adapter.setHasStableIds(true);
        mRecycler.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecycler.setLayoutManager(layoutManager);

    }

    private void setupRecycler(List<FDFixture> list) {
        RecyclerDetailAdapter adapter = new RecyclerDetailAdapter(this, list, null);
        adapter.setHasStableIds(true);
        mRecycler.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecycler.setLayoutManager(layoutManager);

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


    private void stopProgress() {
        mProgressValue.setVisibility(View.INVISIBLE);
    }


    private void setupProgress() {
        mProgressValue.setIndeterminate(true);
        mProgressValue.setVisibility(View.VISIBLE);
    }


    private void setupActionBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
//        toolbar.setTitle(getString(R.string.screen_match));
        setSupportActionBar(toolbar);

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


    private void setupBottomNavigation() {
        mBottomNavigation = findViewById(R.id.bottom_navigation);
        mBottomNavigation.setSelectedItemId(R.id.navigation_favorites);
        mBottomNavigation.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        View rootView = getWindow().getDecorView();

                        Context context = FavoritesActivity.this;
                        switch (item.getItemId()) {
                            case R.id.navigation_matches:
                                startActivityMatches();
                                return true;
                            case R.id.navigation_news:
                                startActivityNews();
                                return true;
                            case R.id.navigation_favorites:
                                FootballUtils.showMessage(context, getString(R.string.activity_same_message));
//                                restartLoaders();
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


    private static class FavoriteAsyncTask extends AsyncTask<Void, Void, FDFixture> {
        private final WeakReference<Context> weakContext;
        private FDFixture mFixture;
        private ICallback mCallback;

        FavoriteAsyncTask(Context context, FDFixture fixture, ICallback callback) {
            this.weakContext = new WeakReference<>(context);
            this.mFixture = fixture;
            this.mCallback = callback;
        }

        @Override
        protected FDFixture doInBackground(Void... params) {
            Context context = weakContext.get();
            if (context == null || mFixture == null || mFixture.getId() <= 0) return null;
            try {
                FDUtils.updateFixtureProjection(context, mFixture, false); // update
                return FDUtils.readFixture(context, mFixture.getId());


            } catch (OperationApplicationException | RemoteException e) {
                Timber.d(context.getString(R.string.favorites_database_exception, e.getMessage()));
            }
            return null;
        }

        @Override
        protected void onPostExecute(FDFixture fixture) {
            Context context = weakContext.get();
            if (fixture == null || context == null) return;

            if (mFixture.getId() != fixture.getId() || mFixture.isFavorite() != fixture.isFavorite() ||
                    mFixture.isNotified() != fixture.isNotified()) {
                FootballUtils.showMessage(context, context.getString(R.string.favorites_change_error));
                return;
            }
// notificationID does not updated when load database fisrst time and =null
            String id = mFixture.getNotificationId();
            String newId = fixture.getNotificationId();
            if (id != null && newId != null && !id.equals(newId)) {
                FootballUtils.showMessage(context, context.getString(R.string.favorites_change_error));
                return;
            }


            ((IReload) context).onReload();             // restart activity loaders
        }
    }


//    private static class FavoritesTask extends AsyncTask<Void, Void, ViewPagerData> {
//        private final WeakReference<Context> weakContext;
//        private Cursor[] mCursors;
//        private ICallback mCallback;
//
//        public FavoritesTask(Context context, Cursor[] cursors, ICallback mCallback) {
//            this.weakContext = new WeakReference<>(context);
//            this.mCursors = cursors;
//            this.mCallback = mCallback;
//        }
//
//        @Override
//        protected ViewPagerData doInBackground(Void... params) {
//            Context context = weakContext.get();
//            if (context == null || mCursors == null || mCursors.length < 2) return null;
//            for (Cursor cursor : mCursors) {
//                if (cursor == null || cursor.getCount() == 0) return null;
//            }
//
//            try {
//
//                Map<Integer, FDCompetition> map = FDUtils.readCompetitions(mCursors[0]);
//                Map<Integer, FDFixture> mapFixtures = FDUtils.readFixtures(mCursors[1]);
//                return getViewPagerData(mapFixtures);
//
//
//            } catch (NullPointerException e) {
//                return null;
//
//            }
//        }
//
//        @Override
//        protected void onPostExecute(ViewPagerData viewPagerData) {
//
////            updateViewPager(viewPagerData);
//            mCallback.onComplete(null,1);
//
//        }
//
//    }


}
