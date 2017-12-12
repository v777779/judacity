package ru.vpcb.basicactivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends AppCompatActivity implements ICallback{
    public static final String INTENT_STRING_EXTRA = "intent_string_extra";
    public static final String REQUEST_GET_TEMPLATE = "get";
    public static final String REQUEST_TEST_GET_TEMPLATE = "test";
    public static final String REQUEST_TEST_OUT_TEMPLATE = "test joke received";
    public static final int CONNECT_TIMEOUT = 5;
    public static final String MESSAGE_TEST_OK = "*** Endpont Test passed ***";

    private AdView mAdView;

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
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
// endpoints
                new EndpointsAsyncTask(MainActivity.this,REQUEST_GET_TEMPLATE).execute();
            }
        });

        // admob
        MobileAds.initialize(this,getString(R.string.banner_ad_app_id));


        mAdView = findViewById(R.id.adview_banner);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);


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

    @Override
    public void onComplete(String s) {
//        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(INTENT_STRING_EXTRA, s);
        startActivity(intent);

        if(getResources().getBoolean(R.bool.transition_light)) {
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        }else if(getResources().getBoolean(R.bool.transition_dark)) {
            overridePendingTransition(R.anim.slide_right, R.anim.slide_left_out);
        }

    }
}
