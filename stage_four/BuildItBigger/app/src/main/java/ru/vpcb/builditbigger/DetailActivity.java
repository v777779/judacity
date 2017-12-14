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

import static ru.vpcb.constants.Constants.BUNDLE_JOKE_IMAGE_ID;
import static ru.vpcb.constants.Constants.BUNDLE_JOKE_TEXT_ID;
import static ru.vpcb.constants.Constants.INTENT_STRING_EXTRA;

public class DetailActivity extends AppCompatActivity {

    // bind
    private Button mButton;
    private ImageView mJokeImage;
    private TextView mJokeText;
    private String mJokeTextId;
    private int mJokeImageId;


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

        if (savedInstanceState != null) {
            mJokeTextId = savedInstanceState.getString(BUNDLE_JOKE_TEXT_ID);
            mJokeImageId = savedInstanceState.getInt(BUNDLE_JOKE_IMAGE_ID);
        } else {
            Intent intent = getIntent();
            if (intent != null && intent.hasExtra(INTENT_STRING_EXTRA)) {
                mJokeTextId = intent.getStringExtra(INTENT_STRING_EXTRA);
            }
            if (mJokeTextId == null) mJokeTextId = "";
            mJokeImageId = JokeUtils.getGridImage();
        }
        mJokeText.setText(mJokeTextId);
        mJokeImage.setImageResource(mJokeImageId);

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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(BUNDLE_JOKE_TEXT_ID, mJokeTextId);
        outState.putInt(BUNDLE_JOKE_IMAGE_ID, mJokeImageId);

    }
}
