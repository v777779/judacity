package ru.vpcb.secondslide;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static Random rnd = new Random();
    private static int BASE_ID_TEXTVIEW = 8000;
    private static int BASE_ID_RECYCLEVIEW = 8200;
    private static int SCALE_SLIDER = 8;

    private static final int TEXT_MARGIN = 16;

    private final static float DP_HEIGHT_LOW = 480;
    private final static float COLUMN_WIDTH_HIGH = 200;
    private final static float DP_WIDTH_LOW = 400;
    private final static float DP_WIDTH_MID = 800;

    private final static float COLUMN_WIDTH_LOW = 150;
    private final static int MAX_COLUMNS = 6;
    private final static int MIN_COLUMNS = 2;
    private final static int TEMP_MAX_ITEMS = 25;  //size if content
    private static final int PAGE_NUMBER_MAX = 3;
    private static final int[] BUTTON_IDS = new int[]{
            R.id.button_001, R.id.button_002, R.id.button_003
    };
    private List<TextView> mListText;
    private int lastPos;
    private ViewPager viewPager;
    private LinearLayout mLinearView;

    private RecyclerView mRecyclerView;
    private int mSpan;


    private Flavor[] arrayFlavors = {
            new Flavor("Cupcake", "1.5", R.drawable.cupcake),
            new Flavor("Donut", "1.6", R.drawable.donut),
            new Flavor("Eclair", "2.0-2.1", R.drawable.eclair),
            new Flavor("Froyo", "2.2-2.2.3", R.drawable.froyo),
            new Flavor("GingerBread", "2.3-2.3.7", R.drawable.gingerbread),
            new Flavor("Honeycomb", "3.0-3.2.6", R.drawable.honeycomb),
            new Flavor("Ice Cream Sandwich", "4.0-4.0.4", R.drawable.icecream),
            new Flavor("Jelly Bean", "4.1-4.3.1", R.drawable.jellybean),
            new Flavor("KitKat", "4.4-4.4.4", R.drawable.kitkat),
            new Flavor("Lollipop", "5.0-5.1.1", R.drawable.lollipop),
    };


    private ClickListener mClickListener;
    private List<TextView> mTextViewList;

    private class ClickListener implements View.OnClickListener {
        private Context context;

        private ClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            String s = "";
            for (int i = 0; i < mTextViewList.size(); i++) {
                TextView textView = mTextViewList.get(i);

                if (textView.getId() == v.getId()) {
                    textView.setTextColor(Color.WHITE);
                    viewPager.setCurrentItem(i);

                } else {
                    textView.setTextColor(Color.GRAY);
                }
            }
            Toast.makeText(context, ((TextView) v).getText(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSpan = getNumberOfColumns(this);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        List<View> listPager = new ArrayList<>();
        listPager.add(getFlaforRecycleView(viewPager));
        listPager.add(getFlaforRecycleView(viewPager));
        listPager.add(getFlaforRecycleView(viewPager));


//menu loader
        android.support.v7.app.ActionBar abMainMenu = this.getSupportActionBar();
        abMainMenu.setDisplayShowCustomEnabled(true);
        LayoutInflater liAddActionBar = LayoutInflater.from(this);
        View customActionBar;
        int menu_id = R.layout.r_layout_low;
        int dpWidth = getDpWidth();
        if (dpWidth >= DP_WIDTH_LOW) {
            menu_id = R.layout.r_layout_mid;
        }
        if (dpWidth >= DP_WIDTH_MID) {
            menu_id = R.layout.r_layout_high;
        }
        customActionBar = liAddActionBar.inflate(menu_id, null);
        abMainMenu.setCustomView(customActionBar);


        mTextViewList = new ArrayList<>();
        mClickListener = new ClickListener(this);
        for (int i = 0; i < BUTTON_IDS.length; i++) {
            TextView textView = customActionBar.findViewById(BUTTON_IDS[i]);
            textView.setOnClickListener(mClickListener);
            if (i == 0) {
                textView.setTextColor(Color.WHITE);
            } else {
                textView.setTextColor(Color.GRAY);
            }
            mTextViewList.add(textView);
        }

// menu loader


        ViewPagerAdapter listPagerAdapter = new ViewPagerAdapter(listPager);
        viewPager.setAdapter(listPagerAdapter);
        viewPager.setCurrentItem(lastPos);
        viewPager.setOffscreenPageLimit(PAGE_NUMBER_MAX);  //    ATTENTION  Prevents Adapter Exception
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                if (lastPos != position) {
                    if (lastPos >= 0) {
                        mTextViewList.get(lastPos).setTextColor(Color.GRAY);
                    }
                    mTextViewList.get(position).setTextColor(Color.WHITE);
                    lastPos = position;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }


    private RecyclerView getFlaforRecycleView(View parent) {

        RecyclerView recyclerView = new RecyclerView(parent.getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        recyclerView.setId(View.generateViewId() + BASE_ID_TEXTVIEW);
        recyclerView.setLayoutParams(lp);

        List<Flavor> list = getFlavorList(20, arrayFlavors);


        final GridLayoutManager mLayoutManager = new GridLayoutManager(this,
                mSpan,
                GridLayoutManager.VERTICAL,
                false);

        recyclerView.setLayoutManager(mLayoutManager);                       // connect to LayoutManager
        recyclerView.setHasFixedSize(true);                                  // item size fixed
        FlavorAdapter mFlavorAdapter = new FlavorAdapter(this, list, mSpan);  //context  and data
        recyclerView.setAdapter(mFlavorAdapter);
        return recyclerView;
    }

    private List<Flavor> getFlavorList(int n, Flavor[] array) {
        List<Flavor> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            list.add(array[rnd.nextInt(array.length)]);
        }
        return list;
    }


    private int getDpWidth() {
        DisplayMetrics dp = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dp);
        return dp.widthPixels;
    }

    private int getNumberOfColumns(Context context) {
        DisplayMetrics dp = context.getResources().getDisplayMetrics();
        float dpWidth = dp.widthPixels / dp.density;
        int nColumns;
        if (dp.heightPixels <= DP_HEIGHT_LOW) {
            nColumns = (int) (dpWidth / COLUMN_WIDTH_LOW);
        } else {
            nColumns = (int) (dpWidth / COLUMN_WIDTH_HIGH);
        }
        if (nColumns > MAX_COLUMNS) {
            nColumns = MAX_COLUMNS;
        }
        if (nColumns < MIN_COLUMNS) {
            nColumns = MIN_COLUMNS;
        }
        return nColumns;

    }
}
