package ru.vpcb.ex_03_25;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        final Button mButton = findViewById(R.id.button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mButton.animate()
                        .alpha(0f)
                        .translationY(-mButton.getHeight())
                        .setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                Snackbar sb = Snackbar.make(mButton, "Button Action!", Snackbar.LENGTH_SHORT);
                                sb.addCallback(new Snackbar.Callback() {
                                    @Override
                                    public void onDismissed(Snackbar transientBottomBar, int event) {
                                        super.onDismissed(transientBottomBar, event);
                                        mButton.animate()
                                                .alpha(1f)
                                                .translationY(0);
                                    }
                                });
                                sb.show();
                            }
                        });
            }
        });
        final Button mButton2 = findViewById(R.id.button2);

        mButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int textColor = mButton2.getCurrentTextColor();

                ObjectAnimator objectAnimator = ObjectAnimator.ofObject(
                        mButton2,
                        "textColor",
                        new ArgbEvaluator(),
                        textColor,
                        Color.GREEN);

                final ObjectAnimator objectAnimatorEnd = ObjectAnimator.ofObject(
                        mButton2,
                        "textColor",
                        new ArgbEvaluator(),
                        Color.GREEN,
                        textColor);

                objectAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        objectAnimatorEnd
                                .setDuration(1000)
                                .start();

                    }
                });

                objectAnimator
                        .setDuration(1000)
                        .start();


            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
