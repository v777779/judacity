package com.example.xyzreader.ui;


import android.content.Context;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import static android.support.v4.view.ViewCompat.SCROLL_AXIS_VERTICAL;

/**
 * Created by V1 on 03-Jan-18.
 */

public class ArticleDetailScroll extends CoordinatorLayout.Behavior {
    private static Runnable mRunnable;
    private AnimateCountDownTimer mCountDownTimer;

    private class AnimateCountDownTimer extends CountDownTimer {
        private View mChild;
        private boolean mIsActive;

        public AnimateCountDownTimer(long millisInFuture) {
            super(millisInFuture, millisInFuture + 1);

        }

        @Override
        public void onTick(long l) {

        }

        @Override
        public void onFinish() {
            if (mChild != null) {
//                mView.animate().alpha(0).setDuration(500).start();
                mChild.animate().translationY(mChild.getHeight()).setInterpolator(new LinearInterpolator()).start();
            }
            setActive(false);
        }

        private void startTimer() {
            super.start();
            setActive(true);
        }

        private synchronized boolean isActive() {
            return mIsActive;
        }

        private synchronized void setActive(boolean isActive) {
            mIsActive = isActive;
        }

    }


    // to inflate from XML this constructor
    public ArticleDetailScroll(Context context, AttributeSet attrs) {
        super(context, attrs);
        mCountDownTimer = new AnimateCountDownTimer(2500);

    }


    @Override
    public void onNestedScroll(
            @NonNull CoordinatorLayout coordinatorLayout,
            @NonNull View child,
            @NonNull View target,
            int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);

        if (child.getVisibility() == View.VISIBLE && dyConsumed > 0) {
//            child.animate().alpha(1.0f).setDuration(100).start();
            if(!mCountDownTimer.isActive()) {
                child.setAlpha(1.0f);
                child.animate().translationY(0).setInterpolator(new LinearInterpolator()).start();
            }
            mCountDownTimer.cancel();
            mCountDownTimer.mChild = child;
            mCountDownTimer.start();


        } else if (child.getVisibility() == View.VISIBLE && dyConsumed < 0) {
//            child.animate().alpha(1.0f).setDuration(100).start();
            if(!mCountDownTimer.isActive()) {
                child.setAlpha(1.0f);
                child.animate().translationY(0).setInterpolator(new LinearInterpolator()).start();
            }

            mCountDownTimer.cancel();
            mCountDownTimer.mChild = child;
            mCountDownTimer.start();
        }
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


}
