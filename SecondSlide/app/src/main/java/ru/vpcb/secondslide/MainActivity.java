package ru.vpcb.secondslide;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static Random rnd = new Random();
    private static int BASE_ID_TEXTVIEW = 8000;
    private static int BASE_ID_LISTVEW = 8200;
    private static int SCALE_SLIDER = 8;
    private static int SIZE_SLIDER_TEXT = 40;
    private static final int TEXT_MARGIN = 16;
    private AdapterFlavor mAdapterFlavor;
    private AdapterFlavor mAdapterFlavor2;
    private List<TextView> mListText;
    private int lastPos;
    private ViewPager viewPager;
    private LinearLayout mLinearView;
    private HorizontalScrollView mScrollView;
    private int mSize;

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
    private String[] arrayText = "MENU1 MENU2 MENU3".split(" ");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main_);

        LayoutInflater inflater = LayoutInflater.from(this);
        View pageView = inflater.inflate(R.layout.fragment_main, null);

        List<View> listPager = new ArrayList<>();
        listPager.add(getFlaforListView(pageView, arrayFlavors));
        listPager.add(getFlaforListView(pageView, arrayFlavors));
        listPager.add(getFlaforListView(pageView, arrayFlavors));

        setContentView(R.layout.page_main);
        View rootView = findViewById(android.R.id.content);

// fill linear view
        mLinearView = (LinearLayout) findViewById(R.id.linear_view);
        mListText = addTextViewList(mLinearView, arrayText);

        mScrollView = getScrollView(rootView);
        mSize = getDpWidth()*SCALE_SLIDER;  // scrollView slider length

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        ViewPagerAdapter listPagerAdapter = new ViewPagerAdapter(listPager);
        viewPager.setAdapter(listPagerAdapter);
        viewPager.setCurrentItem(lastPos);
        mListText.get(lastPos).setTextColor(Color.WHITE);

        viewPager.setOffscreenPageLimit(3);  //    ATTENTION  Prevents Adapter Exception
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                double scrollX = position * mSize + mSize * positionOffset;
                mScrollView.smoothScrollTo((int) (scrollX / 4), 0);
//                Log.v(TAG, " pos:" + position + " off:" + positionOffset + " x:" + mSize);
            }

            @Override
            public void onPageSelected(int position) {
                if (lastPos != position) {
                    if (lastPos >= 0) {
                        mListText.get(lastPos).setTextColor(Color.BLACK);
                    }
                    mListText.get(position).setTextColor(Color.WHITE);
                    lastPos = position;
                }
//                Log.v(this.getClass().getSimpleName(), " sel:" + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
//                Log.v(this.getClass().getSimpleName(), " state:" + state);
            }
        });
    }

    private class ClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            for (int i = 0; i < mLinearView.getChildCount(); i++) {
                View childView = mLinearView.getChildAt(i);
                if (childView.equals(view)) {                   // selected view found
                    viewPager.setCurrentItem(i);                // выбрать заданный
                }
            }
        }
    }


    private ListView getFlaforListView(View parent, Flavor[] array) {
        ListView listView = new ListView(parent.getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        listView.setId(View.generateViewId() + BASE_ID_TEXTVIEW);
        listView.setLayoutParams(lp);
        List<Flavor> list = getFlavorList(rnd.nextInt(20) + 5, array);           //5..25

        AdapterFlavor adapterFlavor = new AdapterFlavor(parent.getContext(), list);
        listView.setAdapter(adapterFlavor);
        return listView;
    }

    private List<Flavor> getFlavorList(int n, Flavor[] array) {
        List<Flavor> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            list.add(array[rnd.nextInt(array.length)]);
        }
        return list;
    }

    private List<TextView> addTextViewList(View parent, String[] array) {
        List<TextView> list = new ArrayList<>();

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,                                      // width
                ViewGroup.LayoutParams.WRAP_CONTENT                                        // height
        );
        lp.setMargins(TEXT_MARGIN, TEXT_MARGIN, TEXT_MARGIN, TEXT_MARGIN);

        for (int i = 0; i < array.length; i++) {
            TextView textView = new TextView(parent.getContext()); // setContentView
            textView.setId(View.generateViewId() + BASE_ID_TEXTVIEW);  // generate ID
            textView.setLayoutParams(lp);

            textView.setText(array[i]);
            textView.setTextColor(Color.BLACK);
            textView.setClickable(true);
            textView.setOnClickListener(new ClickListener());
            textView.setGravity(Gravity.CENTER);
            textView.setWidth((int) (getDpWidth() / arrayText.length - TEXT_MARGIN * 2));
//            textView.setPadding(16, 16, 16, 16);
            list.add(textView);
            mLinearView.addView(textView);
        }
        return list;
    }


    private HorizontalScrollView getScrollView(View parent) {
        HorizontalScrollView hScrollView = parent.findViewById(R.id.scroll_view);

        TextView sliderText = hScrollView.findViewById(R.id.slider_text);
        sliderText.setTextSize(SIZE_SLIDER_TEXT);

        int n = (int)( (double) getDpWidth() * SCALE_SLIDER / sliderText.getTextSize()); // n of virtual chars

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append("#");
        }
        sliderText.setText(sb.toString());
        return hScrollView;
    }

    private int getDpWidth() {
        DisplayMetrics dp = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dp);
        return dp.widthPixels;
    }

}
