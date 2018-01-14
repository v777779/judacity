package com.example.xyzreader.ui;


import android.content.Context;
import android.content.res.Resources;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.example.xyzreader.R;
import com.example.xyzreader.remote.Config;

import static android.support.v4.view.ViewCompat.SCROLL_AXIS_VERTICAL;
import static com.example.xyzreader.remote.Config.BOTTOM_BAR_DELAY_HIDE;
import static com.example.xyzreader.remote.Config.BOTTOM_BAR_FAST_HIDE;
import static com.example.xyzreader.remote.Config.BOTTOM_BAR_SCROLL_DY_THRESHOLD;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 03-Jan-18
 * Email: vadim.v.voronov@gmail.com
 */

/**
 *  BottomBarRecycler class defines pop up behaviour of bottom bar.
 *  Active while scrolling over RecyclerView of ArticleListActivity.
 *  Two modes used  instructive and normal.
 *  In the instructive mode  used constructor without layout attributes.
 *  BottomBarRecycler  sets lock flag  Config.setInstructiveLock(true)
 *  performs motion and unlock flag at the end.
 *  This prevents BottomBarScroll access to given bottom bar view object
 *  if user starts scrolling before motion finished.
 *
 *
 */
public class BottomBarRecycler extends CoordinatorLayout.Behavior {

    private CountDownTimer mCountDownTimer;
    public static BottomBarRecycler mInstance;
    private View mChild;
    private boolean mIsActive;
    private boolean mIsLowScrollTextY;
    private boolean mIsLand;
    private boolean mIsInstructive;

    /**
     * Standard constructor for XML definition of behaviour.
     *
     * @param context Context of calling activity
     * @param attrs
     */
    public BottomBarRecycler(Context context, AttributeSet attrs) {
        super(context, attrs);
        mIsLand = context.getResources().getBoolean(R.bool.is_land);
        mIsInstructive = false;
    }

    public BottomBarRecycler(Context context) {
        Resources res = context.getResources();
        boolean isWide = res.getBoolean(R.bool.is_wide);
        boolean isLand = res.getBoolean(R.bool.is_land);
        if(isWide && !isLand) mIsLand = !isLand;
        mIsLand = (isWide && !isLand)?!isLand:isLand;
        mIsInstructive = true;
        Config.setInstructiveLock(true); // lock mutex
    }


    @Override
    public void onNestedScroll(
            @NonNull CoordinatorLayout coordinatorLayout,
            @NonNull View child,
            @NonNull View target,
            int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
        mIsLowScrollTextY = false;

        if(!mIsInstructive && Config.isInstrictiveLocked()) {
            return;   // стандартные блокируются
        }

        if(dyConsumed < Math.abs(BOTTOM_BAR_SCROLL_DY_THRESHOLD)){
            return;
        }
        setContinue(child);
        mInstance = this;
    }


    @Override
    public boolean onStartNestedScroll(
            @NonNull CoordinatorLayout coordinatorLayout,
            @NonNull View child,
            @NonNull View directTargetChild,
            @NonNull View target, int axes, int type) {
//        return super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type);

        return axes == SCROLL_AXIS_VERTICAL;
    }

    private void setTimer(final View child) {
        if (child == null) return;

        if (!mIsActive && mIsLowScrollTextY) return;  // выйти если неактивно

        if (!mIsActive) {
            child.setAlpha(1.0f);
            if (!mIsLand) {
                child.animate().translationY(0).setInterpolator(new LinearInterpolator()).start();
            } else {
                child.animate().translationX(0).setInterpolator(new LinearInterpolator()).start();
            }
        }

        if (mCountDownTimer != null) mCountDownTimer.cancel();

        int timerValue = mIsLowScrollTextY ? BOTTOM_BAR_FAST_HIDE : BOTTOM_BAR_DELAY_HIDE;  // если активно сократить

        mCountDownTimer = new CountDownTimer(timerValue, timerValue) {
            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                mIsActive = false;
                if (child == null) return;
//                child.animate().alpha(0).setDuration(500).start();
                if (!mIsLand) {
                    child.animate().translationY(mChild.getHeight()).setInterpolator(new LinearInterpolator()).start();
                } else {
                    child.animate().translationX(-mChild.getWidth()).setInterpolator(new LinearInterpolator()).start();
                }

                mCountDownTimer = null;
                Config.setInstructiveLock(false); // unLock mutex
            }
        };
        mCountDownTimer.start();
        mIsActive = true;
        mChild = child;
    }



    public void setContinue(View child) {

        setTimer(child);

    }


    public static void setContinue() {
        if (mInstance == null) return;
        mInstance.setContinue(mInstance.mChild);
    }

}
