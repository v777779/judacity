package ru.vpcb.footballassistant;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import ru.vpcb.footballassistant.services.UpdateService;
import timber.log.Timber;

import static ru.vpcb.footballassistant.utils.Config.UPDATE_SERVICE_PROGRESS;

public class MainActivity extends AppCompatActivity  {

    // TODO использовать ViewPagerFragment_003
    // TODO  MainActivity остальные Fragments FrameLayout индивидуальные toolbar и replacement
    // TODO MainActivity  NewsAPI 1. Add link or similar  "powered by NewsAPI.org"
    // TODO CalendarView and API 17
    // TODO ViewPager Detail сделать один источник данных, RecyclerView перенести внутрь ViewPager
    // TODO ViewPager Rotation загружать ViewPager в onCreate() то есть Activity и все Fragments
    // TODO Calendar auto select Date и выбор по dismiss, double click или OK
    // TODO проставить FDFixture competition при загрузке
    // TODO Player Date Content Provider remove conversion
    // TODO Сделать управление через  DPad по Accessibility
    // TODO Database Loading Date сделать String и убрать конвертацию при сохранении и чтении Db
    // TODO Database Dates и Links добавить и сделать полностью текстовыми

    // _TODO Football-data  1. Key 70e68c465fd24d2e84c17aa8d71ca9b3
    //                2. Key 3dbbb32e16f747e582119f20967996bc
    // TODO Database News Contract and Table
    // TODO Database News Read and Write Cursor Utils
    // TODO News Retrofit2 Utils
    // TODO Database Favorites Contract and Table
    // TODO Database Favorites Read and Write Cursor Utils
    // TODO Match Screen add BottomNavigation Menu
    // TODO Check Activity to Activity Stack
    // TODO Notifications set minimum delay and flex time
    // TODO Notifications add flag for notification, database field for notification status, set and clear procedure
    // TODO Notifications cut names of teams to 20 characters and add ... at the end
    // TODO Widget FDFixture изменить класс, имена команд и соревнования
    // TODO Widget FDFixture FDTeam добавить пост обработку в Gson



    // TODO Options  Add Logo Transition to Collapsed Toolbar  see Collapsing Toolbar dependency


    private static boolean sIsTimber;
    private static Handler mHandler;

    private FloatingActionButton mFab;
    private FloatingActionButton mFab2;

    private ProgressBar mProgressBar;
    private ProgressBar mProgressValue;
    private TextView mProgressText;
    private ImageView mToolbarLogo;

    // receiver
    private MessageReceiver mMessageReceiver;
    // progress
    private int mServiceProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // log
        if (!sIsTimber) {
            Timber.plant(new Timber.DebugTree());
            sIsTimber = true;
        }
// handler
        if (mHandler == null) {
            mHandler = new Handler();
        }

// bind
        mFab = findViewById(R.id.fab);
        mFab2 = findViewById(R.id.fab2);
        mProgressBar = findViewById(R.id.progress_bar);
        mProgressText = findViewById(R.id.progress_text);
        mProgressValue = findViewById(R.id.progress_value);
        mToolbarLogo = findViewById(R.id.toolbar_logo);

// params


// fab
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

            }
        });

        mFab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

// progress
        setupActionBar();
        setupProgress();
        setupReceiver();

        refresh(getString(R.string.action_update));


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_match, menu);
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
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver();
    }

// callbacks


// methods



    private void makeTransition() {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);


        //        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//        if (getResources().getBoolean(R.bool.transition_light)) {
//            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
//        }
//        if (getResources().getBoolean(R.bool.transition_dark)) {
//            overridePendingTransition(R.anim.slide_right, R.anim.slide_left_out);
//        }

    }


    private void startTransition() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(MainActivity.this,
                        android.R.anim.fade_in, android.R.anim.fade_out)
                        .toBundle();
                startActivity(intent, bundle);

            }
        }, 250);


    }


    private void setProgressValue(boolean isIndeterminate) {
        mProgressValue.setIndeterminate(isIndeterminate);
    }

    private void setProgressValue() {
        int value = mServiceProgress;
        int max = UPDATE_SERVICE_PROGRESS;
        if (value < 0) return;
        if (value > max) value = max;
        mProgressBar.setProgress(value);
        mProgressText.setText(String.valueOf(value));
        mProgressValue.setProgress(value);
        mProgressText.setVisibility(View.VISIBLE);

        if (value >= max) {
            mProgressValue.setIndeterminate(false);
            mProgressValue.setProgress(value);
        }
    }


    private void setupProgress() {
        mServiceProgress = 0;
        setProgressValue();
        setProgressValue(false);                // static at start
    }


    private void setupActionBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mToolbarLogo.setVisibility(View.VISIBLE);
        mToolbarLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("");
            actionBar.hide();
        }

    }

    private void refresh(String action) {
        Intent intent = new Intent(action, null, this, UpdateService.class);
        startService(intent);
    }


    private void setupReceiver() {
        mMessageReceiver = new MessageReceiver();
    }

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(getString(R.string.broadcast_data_update_started));
        intentFilter.addAction(getString(R.string.broadcast_data_update_finished));
        intentFilter.addAction(getString(R.string.broadcast_data_no_network));
        intentFilter.addAction(getString(R.string.broadcast_data_update_progress));
        registerReceiver(mMessageReceiver, intentFilter);
    }

    private void unregisterReceiver() {
        unregisterReceiver(mMessageReceiver);
    }


    // classes
    private class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // an Intent broadcast.
            if (intent != null) {
                String action = intent.getAction();
                if (action.equals(context.getString(R.string.broadcast_data_update_started))) {
                    setProgressValue(true); // indeterminate

                } else if (action.equals(context.getString(R.string.broadcast_data_update_finished))) {
                    mServiceProgress = UPDATE_SERVICE_PROGRESS;
                    setProgressValue();
                    startTransition();

                } else if (action.equals(context.getString(R.string.broadcast_data_update_progress))) {
                    int value = intent.getIntExtra(getString(R.string.extra_progress_counter), -1);
                    if (value >= 0) {
                        mServiceProgress = value;
                        setProgressValue();
                    }
                } else if (action.equals(context.getString(R.string.broadcast_data_no_network))) {
                    Toast.makeText(context, "Broadcast message: no network", Toast.LENGTH_SHORT).show();
                } else {
                    throw new UnsupportedOperationException("Not yet implemented");
                }

            }

        }
    }


}
