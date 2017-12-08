package ru.vpcb.basicactivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;


import ru.vpcb.builditbigger.backend.MyBean;
import ru.vpcb.builditbigger.backend.MyEndpoint;

import static ru.vpcb.basicactivity.MainActivity.INTENT_STRING_EXTRA;

public class DetailActivity extends AppCompatActivity {
    private static AsyncGEC mAsyncGec;
    private static TextView mTextView;
    private MyBean myBean;
    private static MyEndpoint myEndpoint;


    private static class AsyncGEC extends AsyncTask<Void, Void, MyBean> {
        @Override
        protected MyBean doInBackground(Void... params) {
            if (myEndpoint == null) {
                myEndpoint = new MyEndpoint();
            }
            return myEndpoint.sayHi("MyString");
        }

        @Override
        protected void onPostExecute(MyBean myBean) {
            if (myBean == null || myBean.getData() == null || myBean.getData().isEmpty()) {
                return;
            }
            onComplete(myBean.getData());
        }
    }

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

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(INTENT_STRING_EXTRA)) {
            String s = intent.getStringExtra(INTENT_STRING_EXTRA);
            if (s == null) s = "";
            mTextView.setText(s);
        }

        new AsyncGEC().execute();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static void onComplete(String s) {
        mTextView.setText(s);
    }
}
