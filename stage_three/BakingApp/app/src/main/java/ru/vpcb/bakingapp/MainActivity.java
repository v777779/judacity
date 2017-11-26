package ru.vpcb.bakingapp;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;

import java.util.List;

import timber.log.Timber;

import static android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;

public class MainActivity extends AppCompatActivity {
    private boolean mIsWide;
    private static boolean mIsTimber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);  // обязательно без Manifest.PARENT
            actionBar.setHomeAsUpIndicator(R.drawable.ic_home_white_24dp);
        }

        setContentView(R.layout.fragment_main);         // dynamic version container
        if (savedInstanceState == null) {
            FragmentMain mainFragment = new FragmentMain();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, mainFragment)
                    .commit();
        }

        if(!mIsTimber) {
            Timber.plant(new Timber.DebugTree());
            mIsTimber = true;
        }

        DisplayMetrics dp = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dp);
        if (dp.heightPixels < dp.widthPixels) {
            mIsWide = dp.widthPixels / dp.density >= 600;
        } else {
            mIsWide = dp.heightPixels / dp.density >= 600;
        }



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (mIsWide)
                getSupportFragmentManager().popBackStack("player", POP_BACK_STACK_INCLUSIVE);
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
