package ru.vpcb.jokeoutput;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.MobileAds;

import java.util.Random;

import ru.vpcb.jokelibrary.JokeLibrary;

public class FragmentJoke extends Fragment {
    private static final String BUNDLE_JOKE_STRING = "bundle_joke_string";
    private Random mRnd;
    private TextView mTextJoke;
    private ImageView mImageJoke;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_fragment, container, false); // attention!!!

        Log.d("Fragment", "thread = " + Thread.currentThread().getName());

        mRnd = new Random();
        mTextJoke = rootView.findViewById(R.id.joke_text);
        mImageJoke = rootView.findViewById(R.id.joke_image);


        Bundle args = getArguments();
        if (args != null) {

            String textJoke = args.getString(BUNDLE_JOKE_STRING);
            if(textJoke == null) textJoke = "";
            mTextJoke.setText(textJoke);
            mImageJoke.setImageResource(getImageId());

        }

        return rootView;
    }


    private final int[] IMAGE_IDS = new int[]{
            R.drawable.joke_001, R.drawable.joke_002,
            R.drawable.joke_005, R.drawable.joke_006,
            R.drawable.joke_010, R.drawable.joke_011,
            R.drawable.joke_012, R.drawable.joke_014,
            R.drawable.joke_015, R.drawable.joke_016,
            R.drawable.joke_017, R.drawable.joke_018,
            R.drawable.joke_019

    };


//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        mRnd = new Random();
//        mTextBanner = (TextView) findViewById(R.id.text_banner);
//        mTextJoke = findViewById(R.id.joke_text);
//        mImageJoke = findViewById(R.id.joke_image);
//        mImageBanner = findViewById(R.id.image_banner);
//        mJoke = new JokeLibrary();
//
//        mTextJoke.setText(mJoke.getJoke());
//        mImageJoke.setImageResource(getImageId());
//        mTextBanner.setText(R.string.banner_joke);
//        mImageBanner.setImageResource(getImageId());
//
//        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
//        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
//
//
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.show();
//
//    }


    private int getImageId() {
        return IMAGE_IDS[mRnd.nextInt(IMAGE_IDS.length)];
    }
}