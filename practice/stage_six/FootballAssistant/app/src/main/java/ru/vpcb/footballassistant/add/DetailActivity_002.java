package ru.vpcb.footballassistant.add;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ru.vpcb.footballassistant.R;

public class DetailActivity_002 extends AppCompatActivity {

    private TextView mTextMessage;
    private ImageView mToolbarLogo;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            View rootView = getWindow().getDecorView();

            switch (item.getItemId()) {
                case R.id.navigation_matches:
                    Snackbar.make(rootView, "Action matches", Snackbar.LENGTH_SHORT).show();
                    return true;
                case R.id.navigation_news:
                    Snackbar.make(rootView, "Action news", Snackbar.LENGTH_SHORT).show();
                    return true;
                case R.id.navigation_favorites:
                    Snackbar.make(rootView, "Action favorites", Snackbar.LENGTH_SHORT).show();
                    return true;
                case R.id.navigation_settings:
                    Snackbar.make(rootView, "Action settings", Snackbar.LENGTH_SHORT).show();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mTextMessage = findViewById(R.id.message);
        mToolbarLogo = findViewById(R.id.toolbar_logo);

        setupActionBar();

        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


    }

    private void setupActionBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mToolbarLogo.setVisibility(View.INVISIBLE);
//        mToolbarLogo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onBackPressed();
//            }
//        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("fefwefwe");
//            actionBar.hide();
        }

    }
}
