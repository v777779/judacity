package ru.vpcb.footballassistant;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import ru.vpcb.footballassistant.services.UpdateService;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private static boolean sIsTimber;

    private Handler mHandler;
    private FloatingActionButton mFab;
    private FloatingActionButton mFab2;

    private ProgressBar mProgressBar;
    private ProgressBar mProgressValue;
    private TextView mProgressText;
    private int mProgressCounter;

    private ProgressBar mProgressValue3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().hide();


        // log
        if (!sIsTimber) {
            Timber.plant(new Timber.DebugTree());
            sIsTimber = true;
        }
// handler
        mHandler = new Handler();

// bind
        mFab = findViewById(R.id.fab);
        mFab2 = findViewById(R.id.fab2);
        mProgressBar = findViewById(R.id.progress_bar);
        mProgressText = findViewById(R.id.progress_text);
        mProgressValue = findViewById(R.id.progress_value);
        mProgressValue.setIndeterminate(true);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                startActivity(intent);
            }
        });

        mFab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressCounter = 0;
                mProgressValue.setIndeterminate(true);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (mProgressCounter < 100) {
                            mProgressCounter += 1;
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            mHandler.post(new Runnable() {  // access from thread to main views
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(mProgressCounter);
                                    mProgressText.setText(String.valueOf(mProgressCounter));
                                    mProgressValue.setProgress(mProgressCounter);

                                    if (mProgressCounter >= 100) {
                                        mProgressValue.setIndeterminate(false);
                                    }

                                }
                            });
                        }


                    }
                }).start();


            }
        });

        mFab.setVisibility(View.INVISIBLE);
        mFab2.setVisibility(View.INVISIBLE);
        mFab2.performClick();
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
    private void refresh(String action) {
        Intent intent = new Intent(action, null, this, UpdateService.class);
        startService(intent);
    }


    // classes
    private class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // an Intent broadcast.
            if (intent != null) {
                String action = intent.getAction();
                if (action.equals(context.getString(R.string.broadcast_update_started))) {
                    Toast.makeText(context, "Broadcast message: update started", Toast.LENGTH_SHORT).show();

                } else if (action.equals(context.getString(R.string.broadcast_update_finished))) {
                    Toast.makeText(context, "Broadcast message: update finished", Toast.LENGTH_SHORT).show();
                } else if (action.equals(context.getString(R.string.broadcast_no_network))) {
                    Toast.makeText(context, "Broadcast message: no network", Toast.LENGTH_SHORT).show();
                } else {
                    throw new UnsupportedOperationException("Not yet implemented");
                }

            }

        }
    }
}
