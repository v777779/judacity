package ru.vpcb.notifications;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static ru.vpcb.notifications.Config.DATE_FORMAT;

public class MainActivity extends AppCompatActivity {

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
//                Toast.makeText(MainActivity.this, "Replace with your own action", Toast.LENGTH_LONG).show();

                Calendar c = Calendar.getInstance();
                c.add(Calendar.SECOND, 2);
                String date = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH).format(c.getTime());
                FDFixture fixture = new FDFixture(date, "Chelsea FC", "StockCity FC");
                NotificationUtils.scheduleReminder(MainActivity.this,fixture);

            }
        });

        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WebView webView = findViewById(R.id.webview);
                webView.setWebViewClient(new WebViewClient() {   // blocks errors
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                        return false;
                    }
                });

                String URL = "https://www.foxsports.com/arizona/video/1158836803834";
                openWebPage(URL);
//                webView.loadUrl(URL);

            }
        });

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


}
