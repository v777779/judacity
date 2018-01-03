package com.example.xyzreader.ui;


import android.animation.Animator;
import android.content.Context;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewPropertyAnimator;

import java.util.Timer;
import java.util.TimerTask;

import static android.support.v4.view.ViewCompat.SCROLL_AXIS_VERTICAL;

/**
 * Created by V1 on 03-Jan-18.
 */

public class ArticleDetailFabScroll extends FloatingActionButton.Behavior {
    private static Runnable mRunnable;
    private AnimateCountDownTimer mCountDownTimer;

    private class AnimateCountDownTimer extends CountDownTimer {
        private FloatingActionButton mFab;

        public AnimateCountDownTimer(long millisInFuture) {
            super(millisInFuture, millisInFuture + 1);
        }

        @Override
        public void onTick(long l) {

        }

        @Override
        public void onFinish() {
            if (mFab != null) {
                mFab.animate().alpha(0).setDuration(500).start();
            }
        }
    }

    ;

    // to inflate from XML this constructor
    public ArticleDetailFabScroll(Context context, AttributeSet attrs) {
        super(context, attrs);
        mCountDownTimer = new AnimateCountDownTimer(2500);

    }


    @Override
    public void onNestedScroll(
            @NonNull CoordinatorLayout coordinatorLayout,
            @NonNull final FloatingActionButton child,
            @NonNull View target,
            int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);

        if (child.getVisibility() == View.VISIBLE && dyConsumed > 0) {
            child.animate().alpha(0.5f).setDuration(100).start();
            mCountDownTimer.cancel();
            mCountDownTimer.mFab = child;
            mCountDownTimer.start();



        } else if (child.getVisibility() == View.VISIBLE && dyConsumed < 0) {
            child.animate().alpha(0.5f).setDuration(100).start();
            mCountDownTimer.cancel();
            mCountDownTimer.mFab = child;
            mCountDownTimer.start();
        }
    }


    @Override
    public boolean onStartNestedScroll(
            @NonNull CoordinatorLayout coordinatorLayout,
            @NonNull FloatingActionButton child,
            @NonNull View directTargetChild,
            @NonNull View target, int axes, int type) {
//        return super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type);

        return axes == SCROLL_AXIS_VERTICAL;
    }


}
