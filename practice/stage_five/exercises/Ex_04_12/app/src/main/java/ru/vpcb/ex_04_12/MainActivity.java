package ru.vpcb.ex_04_12;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ScrollView;

public class MainActivity extends AppCompatActivity {
    private boolean isHero;
    private ScrollView mScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mScrollView = findViewById(R.id.scroll_layout);
        mScrollView.scrollTo(0, 250);
    }

    @Override
    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();

        final int startScrollPos = getResources().getDimensionPixelOffset(R.dimen.unit_scroll_up_distance);
        AnimatorSet as = new AnimatorSet();

        as.playSequentially(
                ObjectAnimator.ofInt(mScrollView, "scrollY", 0),                //.setDuration(750),
                ObjectAnimator.ofInt(mScrollView, "scrollY", startScrollPos/4), //.setDuration(250),
                ObjectAnimator.ofInt(mScrollView, "scrollY", 0)                 //.setDuration(200)

        );

        as.setDuration(350);
        as.start();

    }
}
