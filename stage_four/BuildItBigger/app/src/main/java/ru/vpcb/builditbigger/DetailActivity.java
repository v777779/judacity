package ru.vpcb.builditbigger;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import static ru.vpcb.constants.Constants.INTENT_STRING_EXTRA;

public class DetailActivity extends AppCompatActivity {

// bind
    private Button mButton;
    private ImageView mJokeImage;
    private TextView mJokeText;


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
// bind
        mButton = findViewById(R.id.joke_button);
        mJokeImage = findViewById(R.id.joke_image);
        mJokeText = findViewById(R.id.joke_text);


        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

// intent
        Intent intent = getIntent();
        if (intent.getBooleanExtra("EXIT", false)) {
            Intent intent1 = new Intent(this, DetailActivity.class);
            startActivity(intent1);
            finish();
        }

        if (intent != null && intent.hasExtra(INTENT_STRING_EXTRA)) {
            String s = intent.getStringExtra(INTENT_STRING_EXTRA);
            if (s == null) s = "";
            mJokeText.setText(s);
        }
        mJokeImage.setImageResource(JokeImage.getImage());

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
