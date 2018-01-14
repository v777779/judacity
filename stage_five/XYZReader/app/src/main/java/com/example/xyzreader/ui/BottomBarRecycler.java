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
import static com.example.xyzreader.remote.Config.BOTTOM_BAR_SCROLL_DY_THRESHOLD;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 03-Jan-18
 * Email: vadim.v.voronov@gmail.com
 */

/**
 * BottomBarRecycler class defines pop up behaviour of bottom bar.
 * Active while scrolling over RecyclerView of ArticleListActivity.
 * Two modes used  instructive and normal.
 * In the instructive mode  used constructor without layout attributes.
 * BottomBarRecycler  sets lock flag  Config.setInstructiveLock(true)
 * performs motion and unlock flag at the end.
 * This prevents BottomBarScroll access to given bottom bar view object
 * if user starts scrolling before motion finished.
 */
public class BottomBarRecycler extends CoordinatorLayout.Behavior {
    /**
     * CountDownTimer  delay timer for pop up action
     */
    private CountDownTimer mCountDownTimer;
    /**
     * View  bottom bar object for pop up action
     */
    private View mChild;
    /**
     * Boolean is true if bottom ba is up and visible
     */
    private boolean mIsActive;
    /**
     * Boolean true for landscape layout
     */
    private boolean mIsLand;

    /**
     * Standard constructor for XML definition of behaviour.
     *
     * @param context Context of calling activity
     * @param attrs   AttributeSet attributes
     */
    public BottomBarRecycler(Context context, AttributeSet attrs) {
        super(context, attrs);
        mIsLand = context.getResources().getBoolean(R.bool.is_land);
    }

    /**
     * Custom constructor for instructive motion support
     * Loads fields and locks flag with Config.setInstructiveLock()
     * to get exclusive acces to bottom bar view.
     *
     * @param context
     */
    public BottomBarRecycler(Context context) {
        Resources res = context.getResources();
        boolean isWide = res.getBoolean(R.bool.is_wide);
        boolean isLand = res.getBoolean(R.bool.is_land);
        if (isWide && !isLand) mIsLand = !isLand;
        mIsLand = (isWide && !isLand) ? !isLand : isLand;

        Config.setInstructiveLock(true); // lock mutex
    }

    /**
     * Scroll listener of bottom bar view object
     * Called when scrolling takes place.
     * If lock flag Config.isInstructiveLocked() is true exits
     * if scroll distance less than BOTTOM_BAR_SCROLL_DY_THRESHOLD exits.
     * Called setContinue(child) method to start pop up motion
     *
     * @param coordinatorLayout CoordinatorLayout parent of the view this Behavior is associated with
     * @param child             View child view of the CoordinatorLayout this Behavior is associated with
     * @param target            View descendant view of the CoordinatorLayout performing the nested scroll
     * @param dxConsumed        int horizontal pixels consumed by the target's own scrolling operation
     * @param dyConsumed        int vertical pixels consumed by the target's own scrolling operation
     * @param dxUnconsumed      int : horizontal pixels not consumed by the target's own scrolling operation, but requested by the user
     * @param dyUnconsumed      int vertical pixels not consumed by the target's own scrolling operation, but requested by the user
     * @param type              int type of input which cause this scroll event
     */
    @Override
    public void onNestedScroll(
            @NonNull CoordinatorLayout coordinatorLayout,
            @NonNull View child,
            @NonNull View target,
            int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);

        if (Config.isInstructiveLocked()) {
            return;   // стандартные блокируются
        }

        if (dyConsumed < Math.abs(BOTTOM_BAR_SCROLL_DY_THRESHOLD)) {
            return;
        }
        setContinue(child);
    }


    /**
     * Called when a descendant of the CoordinatorLayout attempts to initiate a nested scroll
     *
     * @param coordinatorLayout     CoordinatorLayout parent of the view this Behavior is associated with
     * @param child                 View child view of the CoordinatorLayout this Behavior is associated with
     * @param directTargetChild     View child view of the CoordinatorLayout that either is or contains the target of the nested scroll operation
     * @param target                View descendant view of the CoordinatorLayout initiating the nested scroll
     * @param axes                  int axes that this nested scroll applies to
     * @param type                  int type of input which cause this scroll event
     * @return
     */
    @Override
    public boolean onStartNestedScroll(
            @NonNull CoordinatorLayout coordinatorLayout,
            @NonNull View child,
            @NonNull View directTargetChild,
            @NonNull View target, int axes, int type) {
//        return super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type);

        return axes == SCROLL_AXIS_VERTICAL;
    }

    /**
     *  Performs pop up motion of botom bar view
     *  Uses animate() translationX() and translationY() methods to perform motion
     *  CountDownTimer onFinsh method perform back  motion to start position after  BOTTOM_BAR_DELAY_HIDE
     *
     * @param child
     */
    private void setTimer(final View child) {
        if (child == null) return;

        if (!mIsActive) {
            child.setAlpha(1.0f);
            if (!mIsLand) {
                child.animate().translationY(0).setInterpolator(new LinearInterpolator()).start();
            } else {
                child.animate().translationX(0).setInterpolator(new LinearInterpolator()).start();
            }
        }

        if (mCountDownTimer != null) mCountDownTimer.cancel();

        int timerValue = BOTTOM_BAR_DELAY_HIDE;  // если активно сократить

        mCountDownTimer = new CountDownTimer(timerValue, timerValue) {
            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                mIsActive = false;
                if (child == null) return;

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

    /**
     *  Start pop up motion, used for public access to privet setTimer method()
     *
     * @param child View bottom bar object.
     */
    public void setContinue(View child) {
        setTimer(child);
    }


}
