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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.vpcb.footballassistant.add.TempUtils;
import ru.vpcb.footballassistant.data.FDFixture;
import ru.vpcb.footballassistant.dbase.FDContract;
import ru.vpcb.footballassistant.dbase.FDLoader;
import ru.vpcb.footballassistant.news.NDArticle;
import ru.vpcb.footballassistant.news.NDNews;
import ru.vpcb.footballassistant.news.NDSource;
import ru.vpcb.footballassistant.news.NDSources;
import ru.vpcb.footballassistant.services.NewsService;
import ru.vpcb.footballassistant.utils.FDUtils;
import ru.vpcb.footballassistant.utils.FootballUtils;
import timber.log.Timber;

import static android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;
import static ru.vpcb.footballassistant.utils.Config.EMPTY_INT_VALUE;
import static ru.vpcb.footballassistant.utils.Config.EMPTY_LONG_DASH;
import static ru.vpcb.footballassistant.utils.Config.FRAGMENT_TEAM_TAG;
import static ru.vpcb.footballassistant.utils.Config.FD_LOADERS_UPDATE_COUNTER;
import static ru.vpcb.footballassistant.utils.Config.MAIN_ACTIVITY_INDEFINITE;
import static ru.vpcb.footballassistant.utils.Config.ND_LOADERS_UPDATE_COUNTER;
import static ru.vpcb.footballassistant.utils.Config.ND_SOURCE_SELECTED;
import static ru.vpcb.footballassistant.utils.Config.NEWS_FRAGMENT_TAG;
import static ru.vpcb.footballassistant.utils.Config.VIEWPAGER_OFF_SCREEN_PAGE_NUMBER;
import static ru.vpcb.footballassistant.utils.FDUtils.setZeroTime;

public class NewsActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>, ICallback {

    private static boolean sIsTimber;
    private static Handler mHandler;

    private FloatingActionButton mFab;
    private FloatingActionButton mFab2;

    private ProgressBar mProgressBar;
    private ProgressBar mProgressValue;
    private TextView mProgressText;


    private RecyclerView mRecyclerView;
    private ViewPager mViewPager;

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
    private Map<String, NDSource> mMap;
    private Map<String, List<NDArticle>> mMapArticles;


    private Cursor[] mCursors;
    private ViewPagerData mViewPagerData;
    private int mViewPagerPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.news_activity);

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
        mViewPager = findViewById(R.id.viewpager_main);
        mTabLayout = findViewById(R.id.toolbar_sliding_tabs);

// params
        mState = MAIN_ACTIVITY_INDEFINITE;
        mCursors = new Cursor[5];
        mMap = new HashMap<>();
        mMapArticles = new HashMap<>();
        mViewPagerPos = EMPTY_INT_VALUE;
        mUpdateCounter = 0;

// progress
        setupActionBar();
        setupBottomNavigation();
        setupProgress();
        setupReceiver();
        setupListeners();
        setupViewPager();


        if (savedInstanceState == null) {
            refresh(getString(R.string.action_update));


        }

        startLoaders();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_news, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_reload) {
            refresh(getString(R.string.action_force_update));
            setupProgress();
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
            case FDContract.NsEntry.LOADER_ID:
                Map<String, NDSource> map = FDUtils.readSources(cursor);
                if (map == null || map.isEmpty()) {
                    break;
                }
                mMap = new HashMap<>(); // fixed order
                mMap.putAll(map);
                mUpdateCounter++;
                break;
            case FDContract.NaEntry.LOADER_ID:
                Map<String, List<NDArticle>> mapArticles = FDUtils.readArticles(cursor);
                if (mapArticles == null || mapArticles.isEmpty()) {
                    break;
                }
                mMapArticles = new HashMap<>();
                mMapArticles.putAll(mapArticles);
                mUpdateCounter++;
                break;

            default:
                throw new IllegalArgumentException("Unknown id: " + loader.getId());
        }

        if(mUpdateCounter == ND_LOADERS_UPDATE_COUNTER) {  // protect from async load
            bindViews();
            mUpdateCounter = 0;
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
// cursors will be closed by supportLoaderManager().CursorLoader()

    }

    @Override
    public void onComplete(View view, int pos) {

    }

    @Override
    public void onComplete(int mode, Calendar calendar) {
    }

    @Override
    public void onComplete(View view, String link, String title) {
        if(link == null || link.isEmpty()) return;
        if (!FootballUtils.isWebViewAction(this)) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                startActivity(intent);
        }else {
            startNewsFragment(link, title);
        }

    }


    // methods
    private void bindViews() {
        if (mMap == null || mMap.isEmpty() || mMapArticles == null || mMapArticles.isEmpty()) {
            return;
        }

        updateViewPager(getViewPagerData());
    }

    private void restartLoaders() {
        mMap = null;
        mMapArticles = null;
        getSupportLoaderManager().restartLoader(FDContract.NsEntry.LOADER_ID, null, this);
        getSupportLoaderManager().restartLoader(FDContract.NaEntry.LOADER_ID, null, this);
    }

    private void startLoaders() {
        mMap = null;
        mMapArticles = null;
        getSupportLoaderManager().initLoader(FDContract.NsEntry.LOADER_ID, null, this);
        getSupportLoaderManager().initLoader(FDContract.NaEntry.LOADER_ID, null, this);
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
                .replace(R.id.container_news, fragment)
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

    private void startNewsFragment(String link, String title) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = NewsFragment.newInstance(link, title);
        fm.popBackStackImmediate(NEWS_FRAGMENT_TAG, POP_BACK_STACK_INCLUSIVE);
        fm.beginTransaction()
                .replace(R.id.container_news, fragment)
                .addToBackStack(NEWS_FRAGMENT_TAG)
                .commit();
    }

    private void startMatchActivity() {
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

    private void startCalendar() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = CalendarDialog.newInstance(this, getViewPagerDate());
        fm.beginTransaction()
                .add(fragment, getString(R.string.calendar_title))
                .commit();

    }

    private RecyclerView getRecycler(List<NDArticle> list) {
        View recyclerLayout = getLayoutInflater().inflate(R.layout.recycler_main, null);
        RecyclerView recyclerView = recyclerLayout.findViewById(R.id.recycler_main_container);

        RecyclerNewsAdapter adapter = new RecyclerNewsAdapter(this, list);
        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        return recyclerView;
    }


//    // test!!!
//// TODO SQLIte Date Check
//    private int getIndex(List<FDFixture> list, Calendar c) {
//        String dateSQLite = FDUtils.formatDateToSQLite(c.getTime());
//        FDFixture fixture = new FDFixture();
//        fixture.setDate(dateSQLite);
//        int index = Collections.binarySearch(list, fixture , cFx);  // for givent day
//        if (index < 0) index = -index - 1;
//        if (index > list.size()) index = list.size() - 1;
//        return index;
//
//    }

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

    private ViewPagerData getViewPagerData() {
// news
// sources
        List<View> recyclers = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        int pos = 0;
        int selected = 0;
        for (NDSource source : mMap.values()) {
            if (source == null) continue;
            String id = source.getId();
            if (id == null || id.isEmpty()) continue;
            if (id.contains(ND_SOURCE_SELECTED)) selected = pos;
            List<NDArticle> list = mMapArticles.get(id);
            if (list == null || list.isEmpty()) continue;
            recyclers.add(getRecycler(list));
            titles.add(pos++, id);

        }
        mViewPagerPos = selected;

        ViewPagerData viewPagerData = new ViewPagerData(recyclers, titles, 3, null, null);
        return viewPagerData;
    }

    private ViewPagerData getViewPagerDataTest() {
// news
        String json = TempUtils.readFileAssets(this, "everything.json");
        NDNews news = new Gson().fromJson(json, NDNews.class);
        List<NDArticle> list = news.getArticles();
// sources
        json = TempUtils.readFileAssets(this, "sources.json");

        NDSources sources = new Gson().fromJson(json, NDSources.class);
        List<NDSource> listSources = sources.getSources();


        List<View> recyclers = new ArrayList<>();
        List<String> titles = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            recyclers.add(getRecycler(list));
            titles.add(getRecyclerTitle(listSources, i));
        }
        ViewPagerData viewPagerData = new ViewPagerData(recyclers, titles, 3, null, null);
        return viewPagerData;
    }

    private String getRecyclerTitle(List<NDSource> list, int pos) {
        NDSource source = list.get(pos);
        try {
            return source.getName();
        } catch (NullPointerException e) {
            return EMPTY_LONG_DASH;
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
        stopProgress();
        if (mViewPager == null || data == null) return;

        mViewPagerData = data;
        ((ViewPagerAdapter) mViewPager.getAdapter()).swap(data.mRecyclers, data.mTitles);
        mViewPager.setCurrentItem(mViewPagerPos);  // works if viewpager is empty only


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


    private void stopProgress() {
        mProgressValue.setVisibility(View.INVISIBLE);
    }


    private void setupProgress() {
        mProgressValue.setIndeterminate(true);
        mProgressValue.setVisibility(View.VISIBLE);
    }


    private void setupActionBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
//        toolbar.setTitle(getString(R.string.screen_news));
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("");
            actionBar.show();
        }

    }

    private void refresh(String action) {
        Intent intent = new Intent(action, null, this, NewsService.class);
        startService(intent);
    }

    private void setupReceiver() {
        mMessageReceiver = new MessageReceiver();
    }

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(getString(R.string.broadcast_news_update_started));
        intentFilter.addAction(getString(R.string.broadcast_news_update_finished));
        intentFilter.addAction(getString(R.string.broadcast_news_no_network));
        intentFilter.addAction(getString(R.string.broadcast_news_update_progress));
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
                if (action.equals(context.getString(R.string.broadcast_news_update_started))) {

                } else if (action.equals(context.getString(R.string.broadcast_news_update_finished))) {

                } else if (action.equals(context.getString(R.string.broadcast_news_update_progress))) {

                } else if (action.equals(context.getString(R.string.broadcast_news_no_network))) {
                    FootballUtils.showMessage(context, getString(R.string.matches_no_network_message));
                } else {
                    throw new UnsupportedOperationException("Not yet implemented");
                }

            }

        }
    }

    private void setupBottomNavigation() {
        mBottomNavigation = findViewById(R.id.bottom_navigation);
        mBottomNavigation.setSelectedItemId(R.id.navigation_news);
        mBottomNavigation.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        View rootView = getWindow().getDecorView();

                        Context context = NewsActivity.this;
                        switch (item.getItemId()) {
                            case R.id.navigation_matches:
                                startActivityMatches();
                                return true;
                            case R.id.navigation_news:
                                Toast.makeText(context, getString(R.string.activity_same_message),
                                        Toast.LENGTH_SHORT).show();
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

//                mMap = FDUtils.readCompetitions(cursors[0][0]);
//                mMapFixtures = FDUtils.readFixtures(cursors[0][4]);
                return getViewPagerData();


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
