package ru.vpcb.bottomactivity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

import ru.vpcb.jokelibrary.JokeLibrary;

public class MainActivity extends AppCompatActivity {
    private final int[] IMAGE_IDS = new int[]{
            R.drawable.joke_001, R.drawable.joke_002,
            R.drawable.joke_005, R.drawable.joke_006,
            R.drawable.joke_010, R.drawable.joke_011,
            R.drawable.joke_012, R.drawable.joke_014,
            R.drawable.joke_015, R.drawable.joke_016,
            R.drawable.joke_017, R.drawable.joke_018,
            R.drawable.joke_019

    };

    private Random mRnd;
    private TextView mTextBanner;
    private TextView mTextJoke;
    private JokeLibrary mJoke;
    private ImageView mImageJoke;
    private ImageView mImageBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRnd = new Random();
        mTextBanner = (TextView) findViewById(R.id.text_banner);
        mTextJoke = findViewById(R.id.joke_text);
        mImageJoke = findViewById(R.id.joke_image);
        mImageBanner = findViewById(R.id.image_banner);
        mJoke = new JokeLibrary();

        mTextJoke.setText(mJoke.getJoke());
        mImageJoke.setImageResource(getImageId());
        mTextBanner.setText(R.string.banner_joke);
        mImageBanner.setImageResource(getImageId());

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_prev:
//                    mTextJoke.setText(mJoke.getPrev());
//                    mImageJoke.setImageResource(getImageId());
//
//                    mTextBanner.setText(R.string.banner_prev);
//                    mImageBanner.setImageResource(getImageId());
                    finish();
                    return true;

                case R.id.navigation_dashboard:
                    mTextJoke.setText(mJoke.getJoke());
                    mImageJoke.setImageResource(getImageId());

                    mTextBanner.setText(R.string.banner_joke);
                    mImageBanner.setImageResource(getImageId());
                    return true;

                case R.id.navigation_next:
                    mTextJoke.setText(mJoke.getNext());
                    mImageJoke.setImageResource(getImageId());

                    mTextBanner.setText(R.string.banner_next);
                    mImageBanner.setImageResource(getImageId());
                    return true;
            }
            return false;
        }
    };

    private int getImageId() {
        return IMAGE_IDS[mRnd.nextInt(IMAGE_IDS.length)];
    }

}
