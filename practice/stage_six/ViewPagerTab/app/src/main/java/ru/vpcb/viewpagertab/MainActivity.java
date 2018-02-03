package ru.vpcb.viewpagertab;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
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
    private List<List<String>> mViewPagerList;
    private int mViewPagerPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        setupViewPagerList();
        setupViewPager();



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

    private void setupViewPagerList() {
        List<List<String>> list = new ArrayList();

        for (int i = 0; i < 12; i++) {
            List<String> listTeams = new ArrayList<>();
            list.add(listTeams);

            for (int j = 0; j < rnd.nextInt(25) +25; j++) {
                listTeams.add(TEAMS[rnd.nextInt(TEAMS.length)]);
            }
        }

        mViewPagerPos = list.size() / 2;
        mViewPagerList = list;

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


    private void setupViewPager() {
        mViewPager = findViewById(R.id.viewpager_main);
        if (mViewPagerList == null) return;

        List<View> recyclers = new ArrayList<>();


        for (List<String> list : mViewPagerList) {
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
}
