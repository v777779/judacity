package ru.vpcb.rgdownload;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by V1 on 27-Sep-17.
 */

public class MovieChild extends AppCompatActivity {
    private static final String TAG = MovieChild.class.getSimpleName();
    private static final String SIGNATURE = "ru.vpcb.rgdownload";

    private TextView mDisplayText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child);
        mDisplayText = (TextView) findViewById(R.id.child_text);

        Intent intent = getIntent();
        MovieItem movie;
        String s;
        if (intent.hasExtra(MovieItem.class.getCanonicalName())) {                        // check if it's our intent
            movie = (MovieItem)intent.getParcelableExtra(MovieItem.class.getCanonicalName());         // check for null already implemented
            Log.v(TAG,"received parcelable movie");
            if(movie!= null) {
                Log.v(TAG,"movie:"+movie.getTitle());
            }

            if(movie != null) {
                mDisplayText.setText(movie.getId()+" "+movie.getTitle());
            }

        }

        if (intent.hasExtra(SIGNATURE)) {                        // check if it's our intent
            s = intent.getStringExtra(SIGNATURE);
                mDisplayText.setText(s);
        }

    }

}
