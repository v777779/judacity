package ru.vpcb.notifications;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.DownloadListener;

import android.webkit.WebResourceRequest;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.logging.Handler;

public class MainActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseAnalytics mFirebaseAnalytics2;
    private Tracker mTracker;

    private int click;

    private static Random mRnd;
    private BottomNavigationView mBottomNavigationView;
    private static Handler mHandler;

    // adMob
    private AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAdView = findViewById(R.id.adview_banner);
        mBottomNavigationView = findViewById(R.id.bottom_navigation);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String URL = "https://www.foxsports.com/arizona/video/1158836803834";

                openWebView(URL);  // webview
//                openWebPage(URL);  // browser

                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "fabID");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "fab_button");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "WebView Open");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

// Tracker
                mTracker.setScreenName("Image~" + " fab");
                mTracker.send(new HitBuilders.ScreenViewBuilder().build());
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("Open22")
                        .build());


            }
        });

        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Replace with your own action", Toast.LENGTH_LONG).show();

                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "fab2");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "fab2_button");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Toast Message Show");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                mTracker.setScreenName("Button: " + " fab2");
                mTracker.send(new HitBuilders.ScreenViewBuilder().build());
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("Write22")
                        .build());

            }
        });

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "button");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "buttonName");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "buttonImage");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
// tracker
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("Share232")
                        .build());

            }
        });
        Button button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "button2");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Clicks:" + click++);
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "buttonImage2");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("Settings2332323")
                        .build());

                Intent intent = new Intent(getBaseContext(), DetailActivity.class);
                startActivity(intent);

            }
        });


        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        mFirebaseAnalytics2 = FirebaseAnalytics.getInstance(this);


        // tracker
        // Obtain the shared Tracker instance.
        WebView application = (WebView) getApplication();
        mTracker = application.getDefaultTracker();



        if (mRnd == null) mRnd = new Random();
        if(savedInstanceState == null) {
            if (mRnd.nextInt(100) < 30) {
                mAdView.setAlpha(1);
                mBottomNavigationView.setAlpha(0);
                mAdView.animate().setStartDelay(30000).setDuration(750).alpha(0).start();
                mBottomNavigationView.animate().setStartDelay(30000).setDuration(750).alpha(1).start();
            }
        }else{
            mAdView.setVisibility(View.INVISIBLE);
        }
        setAdMob();

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


    // methods
    private void setAdMob() {
        MobileAds.initialize(this, getString(R.string.banner_ad_app_id));
// banner
//        mAdView = findViewById(R.id.adview_banner);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);
    }

    private void openWebView(String URL) {
        android.webkit.WebView webView = findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient() {   // without client issues an  Exception on readdress
            @Override
            public boolean shouldOverrideUrlLoading(android.webkit.WebView view, WebResourceRequest request) {
                return true;
            }

            @Override
            public void onPageFinished(android.webkit.WebView view, String url) {
                super.onPageFinished(view, url);
                findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
            }
        });


        webView.setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {

            }
        });

        URL = "https://www.foxsports.com/arizona/video/1158836803834";
        webView.loadUrl(URL);

        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "id");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "name");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }


    private void openWebPage(String sURL) {
        Uri webURI = Uri.parse(sURL);
        Intent intent = new Intent(Intent.ACTION_VIEW, webURI);
        if (intent.resolveActivity(getPackageManager()) != null) { // if found browser
            startActivity(intent);

        }
    }

    private String openWebPageRaw(int resourceId) {

        InputStream raw = getResources().openRawResource(resourceId);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int value;
        try {
            value = raw.read();
            while (value != -1) {
                out.write(value);
                value = raw.read();
            }
        } catch (IOException e) {

        }
        return out.toString();
    }


}
