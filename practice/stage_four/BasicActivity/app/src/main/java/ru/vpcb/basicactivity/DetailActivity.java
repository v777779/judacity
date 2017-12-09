package ru.vpcb.basicactivity;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import static ru.vpcb.basicactivity.MainActivity.INTENT_STRING_EXTRA;

public class DetailActivity extends AppCompatActivity {
    private TextView mTextView;
    private FloatingActionButton mFabBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_home_white_24dp);
        }

        mTextView = findViewById(R.id.detail_text);
        mFabBack = findViewById(R.id.fab_detail);
        mFabBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
//                overridePendingTransition(R.anim.slide_in_main, R.anim.slide_out_main);
//                overridePendingTransition(R.anim.slide_left, R.anim.slide_right_out);
            }
        });

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(INTENT_STRING_EXTRA)) {
            String s = intent.getStringExtra(INTENT_STRING_EXTRA);
            if (s == null) s = "";
            mTextView.setText(s);
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
