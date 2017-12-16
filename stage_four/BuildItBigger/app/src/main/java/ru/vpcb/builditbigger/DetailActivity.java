package ru.vpcb.builditbigger;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import static ru.vpcb.constants.Constants.BUNDLE_JOKE_IMAGE_ID;
import static ru.vpcb.constants.Constants.BUNDLE_JOKE_TEXT_ID;
import static ru.vpcb.constants.Constants.INTENT_REQUEST_CODE;
import static ru.vpcb.constants.Constants.INTENT_STRING_EXTRA;

/**
 * DetailActivity class
 * Receives message from MainActivity via Intent and shows message and random image
 * Send back to MainActivity result to unlock IdlingResource, used for testing
 */
public class DetailActivity extends AppCompatActivity {

    /**
     * Button object used to back to MainActivity
     */
    private Button mButton;
    /**
     * ImageView used to show random image from local storage
     */
    private ImageView mJokeImage;
    /**
     * TextView used to output joke message received from MainActivity
     */
    private TextView mJokeText;
    /**
     * String  value of joke message, used for rotation support
     */
    private String mJokeTextId;
    /**
     * Integer value of companion image imageId, used for rotation support
     */
    private int mJokeImageId;

    /**
     * Creates layout of Detail Activity
     * Extract received String joke message and int imageId values
     * Set joke message and image values to show.
     * Set button and listener which closes activity in click.
     * Unlocks IdlingResource of MainActivity via calling setResult() method
     *
     * @param savedInstanceState Bundle storage of parameters <br>
     *                           Bundle parameters: <br>
     *                           String      mJokeTextId      value of joke message.<br>
     *                           Integer     mJokeImageId     value of joke companion imageId.<br>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_home_white_24dp);
        }

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

        setResult(INTENT_REQUEST_CODE);
    }

    /**
     * Processes Home and settings menu items clicks.
     * Settings Menu is empty.
     *
     * @param item MenuItem object that was selected.
     * @return true if item was processed.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Saves parameters to Bundle storage object
     *
     * @param outState Bundle storage object for parameters.
     *                 Bundle Parameters: <br>
     *                 String      mJokeTextId      value of joke message
     *                 Integer     mJokeImageId     value of joke companion imageId
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(BUNDLE_JOKE_TEXT_ID, mJokeTextId);
        outState.putInt(BUNDLE_JOKE_IMAGE_ID, mJokeImageId);

    }
}
