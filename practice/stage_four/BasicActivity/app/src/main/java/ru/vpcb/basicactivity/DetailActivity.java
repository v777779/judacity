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
            actionBar.setTitle("DetailActivity");
        }

        mTextView = findViewById(R.id.detail_text);
        mFabBack = findViewById(R.id.fab_back);
        mFabBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                if (getResources().getBoolean(R.bool.transition_light)) {
                    overridePendingTransition(R.anim.slide_in_main, R.anim.slide_out_main);
                } else if (getResources().getBoolean(R.bool.transition_dark)) {
                    overridePendingTransition(R.anim.slide_left, R.anim.slide_right_out);
                }
            }
        });

        Intent intent = getIntent();
        if(intent.getBooleanExtra("EXIT",false)) {
            Intent intent1 = new Intent(this, DetailActivity.class);
            startActivity(intent1);
            finish();
        }


        if (intent != null && intent.hasExtra(INTENT_STRING_EXTRA)) {
            String s = intent.getStringExtra(INTENT_STRING_EXTRA);
            if (s == null) s = "";
            mTextView.setText(s);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
}
