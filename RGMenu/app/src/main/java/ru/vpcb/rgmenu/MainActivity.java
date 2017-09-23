package ru.vpcb.rgmenu;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private final static float COLUMN_WIDTH_HIGH = 200;
    private final static float DP_WIDTH_LOW = 400;
    private final static float DP_WIDTH_MID = 800;

    private final static float COLUMN_WIDTH_LOW = 150;
    private final static int MAX_COLUMNS = 6;
    private final static int MIN_COLUMNS = 2;
    private final static int TEMP_MAX_ITEMS = 25;  //size if content
    private static Random rnd = new Random();

    private FlavorAdapter mFlavorAdapter;
    private ArrayList<Flavor> listFlavors;
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
            //  new Flavor("Lollipop", "5.0-5.1.1", R.drawable.lollipop),
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
            for (TextView textView : mTextViewList) {
                if (textView.getId() == v.getId()) {
                    textView.setTextColor(Color.WHITE);
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

        if (savedInstanceState == null || !savedInstanceState.containsKey("flavors")) {
            listFlavors = new ArrayList<>(Arrays.asList(arrayFlavors));
            listFlavors.addAll(Arrays.asList(arrayFlavors)); // 18 сразу
        } else {
            listFlavors = savedInstanceState.getParcelableArrayList("flavors");
        }

//menu loader
        android.support.v7.app.ActionBar abMainMenu = this.getSupportActionBar();
        abMainMenu.setDisplayShowCustomEnabled(true);
        LayoutInflater liAddActionBar = LayoutInflater.from(this);

        DisplayMetrics dp = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dp);
        View customActionBar;
        int menu_id = R.layout.r_layout_low;
        int dpWidth = (int)(dp.widthPixels / dp.density);
        if (dpWidth >= DP_WIDTH_LOW) {
            menu_id = R.layout.r_layout_mid;
        }
        if (dpWidth >= DP_WIDTH_MID) {
            menu_id = R.layout.r_layout_high;
        }
        customActionBar = liAddActionBar.inflate(menu_id, null);
        abMainMenu.setCustomView(customActionBar);


        int[] buttons = new int[]{R.id.button_001, R.id.button_002, R.id.button_003};

        // final TextView actionBarSearch = (TextView) findViewById(R.id.miBluetoothConnection);
        // actionBarSearch.setText("SEARCH");
        mTextViewList = new ArrayList<>();
        mClickListener = new ClickListener(this);
        for (int i = 0; i < buttons.length; i++) {
            TextView textView = customActionBar.findViewById(buttons[i]);
            textView.setOnClickListener(mClickListener);
            if (i == 0) {
                textView.setTextColor(Color.WHITE);
            } else {
                textView.setTextColor(Color.GRAY);
            }
            mTextViewList.add(textView);
        }

// menu loader


        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        final GridLayoutManager mLayoutManager = new GridLayoutManager(this,
                mSpan,
                GridLayoutManager.VERTICAL,
                false);

        mRecyclerView.setLayoutManager(mLayoutManager);                          // connect to LayoutManager
        mRecyclerView.setHasFixedSize(true);                                     // item size fixed
        mFlavorAdapter = new FlavorAdapter(this, listFlavors, mSpan);  //context  and data
        mRecyclerView.setAdapter(mFlavorAdapter);
// scrolled listener
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    int v = mLayoutManager.getChildCount();
                    int p = mLayoutManager.findFirstVisibleItemPosition();
                    int total = mLayoutManager.getItemCount();

                    if (v + p >= total) {
                        loadPage();
                    }
                }
            }
        });


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("flavors", listFlavors);
        super.onSaveInstanceState(outState);
    }

    private int getNumberOfColumns(Context context) {
        DisplayMetrics dp = context.getResources().getDisplayMetrics();
        float dpWidth = dp.widthPixels / dp.density;
        int nColumns;
        if (dpWidth <= DP_WIDTH_LOW) {
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


    private void loadPage() {
        if (mFlavorAdapter.getItemCount() < TEMP_MAX_ITEMS) {  // denial of service
            listFlavors.add(arrayFlavors[rnd.nextInt(arrayFlavors.length)]);
            mFlavorAdapter.setSize(mFlavorAdapter.getItemCount() + 1);
            mFlavorAdapter.notifyDataSetChanged();
        }
    }

}
