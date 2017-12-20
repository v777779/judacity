package ru.vpcb.ex_04_07I;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Slide;
import android.view.Gravity;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.GridView;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_detail);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide slide = new Slide(Gravity.BOTTOM);
//        slide.addTarget(R.id.tr_text);
            slide.addTarget(R.id.tr_view1);
            slide.addTarget(R.id.tr_view2);
            slide.addTarget(R.id.tr_view3);
            slide.addTarget(R.id.tr_view4);
            slide.addTarget(R.id.tr_view5);
            slide.addTarget(R.id.tr_view6);
            slide.addTarget(R.id.tr_view7);
            slide.addTarget(R.id.tr_view8);
            slide.addTarget(R.id.tr_view9);

            slide.setInterpolator(AnimationUtils.loadInterpolator(
                    this,
                    android.R.interpolator.fast_out_slow_in));


            slide.setDuration(300);
            getWindow().setEnterTransition(slide);

        }
    }
}
