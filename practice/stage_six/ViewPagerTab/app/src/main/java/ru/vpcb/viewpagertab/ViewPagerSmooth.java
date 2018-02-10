package ru.vpcb.viewpagertab;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import java.lang.reflect.Field;

/**
 * Created by V1 on 10-Feb-18.
 */

public class ViewPagerSmooth extends ViewPager {

    public ViewPagerSmooth(Context context) {
        super(context);
        setMyScroller();
    }

    public ViewPagerSmooth(Context context, AttributeSet attrs) {
        super(context, attrs);
        setMyScroller();
    }


    private void setMyScroller() {
        try {
            Class<?> viewPager = ViewPager.class;
            Field scroller = viewPager.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            scroller.set(this, new MyScroller(getContext()));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private class MyScroller extends Scroller {

        public MyScroller(Context context) {
            super(context, new DecelerateInterpolator());
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            super.startScroll(startX, startY, dx, dy, 350);  // delay
        }
    }

}
