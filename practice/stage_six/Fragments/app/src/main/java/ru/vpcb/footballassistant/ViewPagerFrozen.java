package ru.vpcb.footballassistant;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import java.lang.reflect.Field;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 10-Feb-18
 * Email: vadim.v.voronov@gmail.com
 */

public class ViewPagerFrozen extends ViewPager {

    public ViewPagerFrozen(Context context) {
        super(context);
    }

    public ViewPagerFrozen(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }



}
