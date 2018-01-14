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
import static com.example.xyzreader.remote.Config.BOTTOM_BAR_SCROLLY_THRESHOLD;
import static com.example.xyzreader.remote.Config.BOTTOM_BAR_SCROLL_DY_THRESHOLD;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 03-Jan-18
 * Email: vadim.v.voronov@gmail.com
 */

/**
 * BottomBarScroll class defines pop up behaviour of bottom bar.
 * Active while scrolling over RecyclerView of Text of ArticleFragmentActivity.
 * In the instructive mode  flag Config.setInstructiveLock(true)
 * prevents BottomBarScroll access to given bottom bar view object
 * if user starts scrolling before motion finished.
 */
public class BottomBarScroll extends CoordinatorLayout.Behavior {

    /**
     * CountDownTimer  delay timer for pop up action
     */
    private CountDownTimer mCountDownTimer;
    /**
     * BottomBarScroll  instance used for external access to this object
     */
    public static BottomBarScroll mInstance;
    /**
     * View  bottom bar object for pop up action
     */
    private View mChild;
    /**
     * Boolean is true if bottom ba is up and visible
     */
    private boolean mIsActive;
    /**
     * Boolean true if scrolling position too close to beginning of text
     * Used to block pop up bar at the top of text
     */
    private boolean mIsLowScrollTextY;
    /**
     * Boolean is true for landscape layout
     */
    private boolean mIsLand;
    /**
     * Boolean is true for tablet with sw800dp
     */
    private boolean mIsWide;

    /**
     * Standard constructor for XML definition of behaviour.
     *
     * @param context Context of calling activity
     * @param attrs   AttributeSet attributes
     */
    public BottomBarScroll(Context context, AttributeSet attrs) {
        super(context, attrs);
        Resources res = context.getResources();
        mIsLand = res.getBoolean(R.bool.is_land);
        mIsWide = res.getBoolean(R.bool.is_wide);
    }

    /**
     * Scroll listener of bottom bar view object
     * Called when scrolling takes place.
     * If lock flag Config.isInstructiveLocked() is true or
     * if scroll distance less than BOTTOM_BAR_SCROLL_DY_THRESHOLD or
     * if scroll position less than  BOTTOM_BAR_SCROLLY_THRESHOLD exits
     * Calls setContinue(child) method to start pop up motion
     * Sets mInstance with this object
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
        mIsLowScrollTextY = coordinatorLayout.findViewById(R.id.nested_scrollview).getScrollY() < BOTTOM_BAR_SCROLLY_THRESHOLD;

        if (Config.isInstructiveLocked()) {
            return;
        }
        if (dyConsumed < Math.abs(BOTTOM_BAR_SCROLL_DY_THRESHOLD)) {
            return;
        }
        setContinue(child);
        mInstance = this;
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
     * @param child  View bottom bar object.
     */
    private void setTimer(final View child) {
        if (child == null) return;

        if (!mIsActive && mIsLowScrollTextY) return;  // выйти если неактивно

        if (!mIsActive) {
            child.setAlpha(1.0f);
            if (!mIsLand && !mIsWide) {
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

                if (!mIsLand && !mIsWide) {
                    child.animate().translationY(mChild.getHeight()).setInterpolator(new LinearInterpolator()).start();
                } else {
                    child.animate().translationX(-mChild.getWidth()).setInterpolator(new LinearInterpolator()).start();
                }

                mCountDownTimer = null;
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

    /**
     *  Start pop up motion, used for public access to privet setTimer method()
     *  Static method used to get access to object which is created by activity.
     */
    public static void setContinue() {
        if (mInstance == null) return;
        mInstance.setContinue(mInstance.mChild);
    }

}
