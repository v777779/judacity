package ru.vpcb.viewpagertab;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements IFragment {
    private final String[] TEAMS = new String[]{
            "HolstenKiel", "Jahn Regensburg", "Sd Eibar", "Sevilla FC",
            "FC Ingolstadt 04", "SpBff Greuther Furth", "Burnley FC", "Manchester City FC",
            "FC Lorient", "RC Lens", "Foggia", "Brescia Calcio",
            "Parma FC", "Perugia", "AS Bari", "Carpi FC",
            "Cremonese", "Pro Vercelli", "Ascoli", "VfL Wolfsburg"
    };
    private static final int VIEWPAGER_OFF_SCREEN_PAGE_NUMBER = 3;
    private static Random rnd = new Random();


    private ViewPager mViewPager;
    private ViewPager mViewPager2;
    private List<List<String>> mViewPagerList;
    private int mViewPagerPos;
    private TabLayout mTabLayout;

    private MotionEvent mMotionEvent;
    private int mScrollState = ViewPager.SCROLL_STATE_IDLE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mViewPager = findViewById(R.id.viewpager_main);
        mTabLayout = findViewById(R.id.toolbar_sliding_tabs);
        mViewPager2 = findViewById(R.id.viewpager_toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                if (mMotionEvent != null)
                    mViewPager.onTouchEvent(mMotionEvent);
            }
        });

        setupActionBar();
        setupViewPagerList();
        setupViewPager();
        setupViewPager2();

        mTabLayout.setupWithViewPager(mViewPager);


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
        if (id == R.id.action_calendar) {
            startFragment();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startFragment() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = FragmentDialog.newInstance(this, R.layout.fragment_calendar);

        fm.beginTransaction()
                .add(fragment, "fragment")
                .commit();

    }

    private void setupViewPagerList() {
        List<List<String>> list = new ArrayList();

        for (int i = 0; i < 12; i++) {
            List<String> listTeams = new ArrayList<>();
            list.add(listTeams);

            for (int j = 0; j < rnd.nextInt(25) + 25; j++) {
                listTeams.add(TEAMS[rnd.nextInt(TEAMS.length)]);
            }
        }

        mViewPagerPos = list.size() / 2;
        mViewPagerList = list;

    }


    private void setupActionBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Title");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.show();
        }

    }

    private RecyclerView getRecycler(List<String> list) {
        View recyclerLayout = getLayoutInflater().inflate(R.layout.recycler_main, null);
        RecyclerView recyclerView = recyclerLayout.findViewById(R.id.recycler_main_container);

        RecyclerAdapter adapter = new RecyclerAdapter(this, list);
        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        return recyclerView;
    }


    private String getDate(Calendar c) {
        String day = String.format(Locale.ENGLISH, "%02d", c.get(Calendar.DAY_OF_MONTH));

        String month = String.format(Locale.ENGLISH, "%02d", c.get(Calendar.MONTH) + 1);

        String year = String.format(Locale.ENGLISH, "%04d", c.get(Calendar.YEAR));


        return day + "/" + month + "/" + year.substring(2, year.length());


    }

    private void setupViewPager() {
        mViewPager = findViewById(R.id.viewpager_main);
        if (mViewPagerList == null) return;

        List<View> listRecyclers = new ArrayList<>();
        List<String> listRecyclerTitles = new ArrayList<>();

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -mViewPagerList.size() / 2);

        for (List<String> list : mViewPagerList) {
            listRecyclers.add(getRecycler(list));
            listRecyclerTitles.add(getDate(c));
            c.add(Calendar.DATE, 1);
        }

        ViewPagerAdapter listPagerAdapter = new ViewPagerAdapter(listRecyclers, listRecyclerTitles);
        mViewPager.setAdapter(listPagerAdapter);
        mViewPager.setCurrentItem(mViewPagerPos);
        mViewPager.setOffscreenPageLimit(VIEWPAGER_OFF_SCREEN_PAGE_NUMBER);  //    ATTENTION  Prevents Adapter Exception
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(mScrollState == ViewPager.SCROLL_STATE_IDLE) return;
                mViewPager2.scrollTo(mViewPager.getScrollX(),mViewPager2.getScrollY());
            }

            @Override
            public void onPageSelected(int position) {
//                mViewPager2.setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                mScrollState = state;
                if(state == ViewPager.SCROLL_STATE_IDLE) {
                    mViewPager2.setCurrentItem(mViewPager.getCurrentItem(),false);
                }
            }
        });




    }

    private void setupViewPager2() {

        if (mViewPagerList == null) return;

        List<View> listRecyclers = new ArrayList<>();
        List<String> listRecyclerTitles = new ArrayList<>();

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -mViewPagerList.size() / 2);

        for (List<String> list : mViewPagerList) {
            listRecyclers.add(getRecycler(list));
            listRecyclerTitles.add(getDate(c));
            c.add(Calendar.DATE, 1);
        }

        ViewPagerAdapter listPagerAdapter = new ViewPagerAdapter(listRecyclers, listRecyclerTitles);
        mViewPager2.setAdapter(listPagerAdapter);
        mViewPager2.setCurrentItem(mViewPagerPos);
        mViewPager2.setOffscreenPageLimit(VIEWPAGER_OFF_SCREEN_PAGE_NUMBER);  //    ATTENTION  Prevents Adapter Exception
        mViewPager2.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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

    @Override
    public void onComplete(int value) {
        String s = (value == 0 ? "Action_OK" : "Action_Cancel");
        Snackbar.make(getWindow().getDecorView(), s, Snackbar.LENGTH_SHORT).show();
    }
}
