package ru.vpcb.notifications;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;

import ru.vpcb.notifications.Utils.TempUtils;
import ru.vpcb.notifications.data.FDFixture;
import ru.vpcb.notifications.data.FDFixtures;
import ru.vpcb.notifications.data.PostProcessingEnabler;
import ru.vpcb.notifications.notifications.NotificationUtils;

import static ru.vpcb.notifications.Utils.Config.WIDGET_INTENT_BUNDLE;
import static ru.vpcb.notifications.Utils.Config.WIDGET_BUNDLE_WIDGET_ID;
import static ru.vpcb.notifications.Utils.Config.showMessage;
import static ru.vpcb.notifications.Utils.FootballUtils.getRnd;
import static ru.vpcb.notifications.widgets.MatchWidgetService.startFillWidgetAction;


public class MainActivity extends AppCompatActivity {

    private FloatingActionButton mFab;
    private FloatingActionButton mFab2;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mFab = findViewById(R.id.fab);
        mFab2 = findViewById(R.id.fab2);
        mProgressBar = findViewById(R.id.progressBar);


        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(MainActivity.this, "Replace with your own action", Toast.LENGTH_LONG).show();

                Calendar c = Calendar.getInstance();
                c.add(Calendar.SECOND, 2);
                FDFixture fixture = getFixture();
                fixture.setDate(c.getTime());
                NotificationUtils.scheduleReminder(MainActivity.this, fixture);

            }
        });

        mFab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = getIntent().getBundleExtra(WIDGET_INTENT_BUNDLE);
                if (bundle != null) {
                    int widgetId = bundle.getInt(WIDGET_BUNDLE_WIDGET_ID);
// test!!!
                    FDFixture fixture = getFixture();
                    startFillWidgetAction(MainActivity.this, widgetId, fixture.getId());
                    showMessage(MainActivity.this, "Widget added");


                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    WebView webView = findViewById(R.id.webview);
                    webView.setWebViewClient(new WebViewClient() {   // blocks errors
                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                            return false;
                        }

                        @Override
                        public void onPageFinished(WebView view, String url) {
                            super.onPageFinished(view, url);
                            mProgressBar.setVisibility(View.INVISIBLE);
                        }
                    });

                    String URL = "https://www.foxsports.com/arizona/video/1158836803834";
//                openWebPage(URL);
                    webView.loadUrl(URL);
                }

            }
        });

        if (getIntent().hasExtra("this_is_click_on_notification")) {
            Toast.makeText(this, "Notifications clicked", Toast.LENGTH_SHORT).show();
        }

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

    private FDFixture getFixture() {
        String json = TempUtils.readFileAssets(MainActivity.this, "fixtures.json");
        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new PostProcessingEnabler())
                .create();

        FDFixtures fixtures = gson.fromJson(json, FDFixtures.class);
        List<FDFixture> list = fixtures.getFixtures();
        return list.get(getRnd().nextInt(list.size()));
    }





}
