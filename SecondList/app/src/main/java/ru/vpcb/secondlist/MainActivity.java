package ru.vpcb.secondlist;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private AdapterFlavor mAdapterFlavor;
    private AdapterFlavor mAdapterFlavor2;
    private List<TextView> mListText;
    private int lastPos;
    private int position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.fragment_main);
        setContentView(R.layout.page_main);


        LayoutInflater inflater = LayoutInflater.from(this);


        View pageView = inflater.inflate(R.layout.fragment_main, null);


        Flavor[] arrayFlavors = {
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
        Flavor[] arrayFlavors2 = {
                new Flavor("Cupcake", "1.5", R.drawable.cupcake),
                new Flavor("Donut", "1.6", R.drawable.donut),
                new Flavor("Eclair", "2.0-2.1", R.drawable.eclair),
                new Flavor("Donut", "1.6", R.drawable.donut),
                new Flavor("Eclair", "2.0-2.1", R.drawable.eclair),
                new Flavor("Froyo", "2.2-2.2.3", R.drawable.froyo),
                new Flavor("GingerBread", "2.3-2.3.7", R.drawable.gingerbread),
                new Flavor("Honeycomb", "3.0-3.2.6", R.drawable.honeycomb),
                new Flavor("Ice Cream Sandwich", "4.0-4.0.4", R.drawable.icecream),
                new Flavor("Jelly Bean", "4.1-4.3.1", R.drawable.jellybean),
        };

        Flavor[] arrayFlavors3 = {
                new Flavor("Cupcake", "1.5", R.drawable.cupcake),
                new Flavor("Donut", "1.6", R.drawable.donut),
                new Flavor("Donut", "1.6", R.drawable.donut),
                new Flavor("Eclair", "2.0-2.1", R.drawable.eclair),
                new Flavor("Donut", "1.6", R.drawable.donut),
                new Flavor("Eclair", "2.0-2.1", R.drawable.eclair),
                new Flavor("Froyo", "2.2-2.2.3", R.drawable.froyo),
                new Flavor("GingerBread", "2.3-2.3.7", R.drawable.gingerbread),
                new Flavor("Honeycomb", "3.0-3.2.6", R.drawable.honeycomb),
                new Flavor("Ice Cream Sandwich", "4.0-4.0.4", R.drawable.icecream),
        };
        List<Flavor> listFlavors = new ArrayList<>(Arrays.asList(arrayFlavors));
        mAdapterFlavor = new AdapterFlavor(
                this,                                       // context
                listFlavors                                 // list
        );
        ListView listView = (ListView) pageView.findViewById(R.id.listview_flavor);
        listView.setAdapter(mAdapterFlavor);
//        View header = inflater.inflate(R.layout.fragment_header,null);
//        listView.addHeaderView(header);

        List<Flavor> listFlavors2 = new ArrayList<>(Arrays.asList(arrayFlavors2));
        mAdapterFlavor2 = new AdapterFlavor(this, listFlavors2);
        ListView listView2 = (ListView) pageView.findViewById(R.id.listview_flavor2);
        listView2.setAdapter(mAdapterFlavor2);


        List<Flavor> listFlavors3 = new ArrayList<>(Arrays.asList(arrayFlavors3));
        AdapterFlavor mAdapterFlavor3 = new AdapterFlavor(this, listFlavors3);
        ListView listView3 = (ListView) pageView.findViewById(R.id.listview_flavor3);
        listView3.setAdapter(mAdapterFlavor3);


        List<View> listPager = new ArrayList<>();
        listPager.add(listView);
        listPager.add(listView2);
        listPager.add(listView3);



        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        mListText = new ArrayList<>();

        mListText.add((TextView) findViewById(R.id.text_item0));
        mListText.add((TextView) findViewById(R.id.text_item1));
        mListText.add((TextView) findViewById(R.id.text_item2));
        lastPos = 0;

        ViewPagerAdapter listPagerAdapter = new ViewPagerAdapter(listPager);

        viewPager.setAdapter(listPagerAdapter);
        viewPager.setCurrentItem(lastPos);
        mListText.get(lastPos).setText("121");
        viewPager.setOffscreenPageLimit(3);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.v(TAG, " pos:" + position + " off:" + positionOffset);

            }

            @Override
            public void onPageSelected(int position) {
                if (lastPos != position) {
                    if (lastPos >= 0) {
                        mListText.get(lastPos).setText("*");
                    }
                    mListText.get(position).setText("121");
                    lastPos = position;
                }
                Log.v(this.getClass().getSimpleName(), " sel:" + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.v(this.getClass().getSimpleName(), " state:" + state);


            }
        });



    }
}
