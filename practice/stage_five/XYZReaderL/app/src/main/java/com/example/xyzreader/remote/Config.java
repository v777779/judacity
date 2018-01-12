package com.example.xyzreader.remote;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewPropertyAnimator;

import com.example.xyzreader.R;
import com.example.xyzreader.ui.ArticleDetailFragment;
import com.example.xyzreader.ui.BottomBarRecycler;

public class Config {
    private static String TAG = Config.class.toString();


    // instructive motions
    public static boolean sIsInstructedPort;
    public static boolean sIsInstructedLand;

    public static boolean sIsInstructedBottomPort;
    public static boolean sIsInstructedBottomLand;

    public static final String BASE_URL = "https://go.udacity.com/xyz-reader-json";

    // articleListActivity
    public static final String ACTION_TIME_REFRESH = "action_time_refresh";
    public static final String ACTION_SWIPE_REFRESH = "action_swipe_refresh";
    public static final String FRAGMENT_ERROR_NAME = "fragment_error_name";
    public static final String FRAGMENT_ERROR_TAG = "fragment_error_tag";

    public static final int CALLBACK_ACTIVITY = 0;
    public static final int CALLBACK_FRAGMENT = 1;

    public static final int CALLBACK_FRAGMENT_RETRY = 2;
    public static final int CALLBACK_FRAGMENT_CLOSE = 3;
    public static final int CALLBACK_FRAGMENT_EXIT = 5;
    public static final int CALLBACK_FRAGMENT_MOTION = 7;
    public static final int CALLBACK_FRAGMENT_FULLSCREEN = 7;

    public static final String BUNDLE_ARTICLE_ITEM_URI = "bundle_article_item_uri";
    public static final String BUNDLE_STARTING_ITEM_ID = "bundle_starting_item_id";
    public static final String BUNDLE_CURRENT_ITEM_ID = "bundle_current_item_id";
    public static final String BUNDLE_STARTING_ITEM_POS = "bundle_starting_item_pos";
    public static final String BUNDLE_CURRENT_ITEM_POS = "bundle_current_item_pos";
    public static final String BUNDLE_FULL_SCREEN_MODE = "bundle_full_screen_mode";
    public static final String BUNDLE_IS_ITEM_SELECTED = "bundle_is_item_selected";

    public static final int ARTICLE_LIST_LOADER_ID = 1221;
    public static final int ARTICLE_DETAIL_LOADER_ID = 1222;

    public static final boolean FULL_SCREEN_MODE_ON = true;
    public static final boolean FULL_SCREEN_MODE_OFF = false;


    // recycler
    public static final int HIGH_SCALE_WIDTH = 180;     // dpi
    public static final int HIGH_SCALE_HEIGHT = 300;     // dpi 200
    public static final double SCALE_RATIO_VERT = 0.6;   // dw/dh
    public static final double SCALE_RATIO_HORZ = 0.45;   // dw/dh

    public static final int MIN_SPAN = 1;
    public static final int MIN_HEIGHT = 100;
    public static final int MIN_WIDTH = 100;

    // article detail fragment
    public static final String BUNDLE_FRAGMENT_STARTING_ID = "bundle_fragment_starting_id";
    public static final String BUNDLE_FRAGMENT_CURRENT_ID = "bundle_fragment_current_id";
    public static final String BUNDLE_FRAGMENT_STARTING_POS = "bundle_fragment_starting_pos";
    public static final String BUNDLE_FRAGMENT_CURRENT_POS = "bundle_fragment_current_pos";

    public static final int FRAGMENT_TEXT_SIZE = 2000;
    public static final int FRAGMENT_TEXT_OFFSET = 2000;

    public static final boolean LOAD_ALL_PAGES = false;
    public static final boolean LOAD_NEXT_PAGE = true;

    // article detail scroll
    public static final int BOTTOM_BAR_DELAY_HIDE = 2500;
    public static final int BOTTOM_BAR_FAST_HIDE = 10;
    public static final int BOTTOM_BAR_SCROLLY_THRESHOLD = 200;
    public static final int BOTTOM_BAR_SCROLL_DY_THRESHOLD = 30;
    public static final int BOTTOM_BAR_RECYCLER_DY_THRESHOLD = 500;

    // fragment error dialog
    public static final String BUNDLE_FRAGMENT_IS_CURSOR_EMPTY = "bundle_fragment_is_cursor_empty";
    public static final String BUNDLE_FRAGMENT_PARAMETERS = "bundle_fragment_parameters";
    public static final int FRAGMENT_INDEX_LAYOUT = 0;
    public static final int FRAGMENT_INDEX_TITLE = 1;
    public static final int FRAGMENT_INDEX_LINE1 = 2;
    public static final int FRAGMENT_INDEX_LINE2 = 3;
    public static final int FRAGMENT_INDEX_BUTTON1 = 4;
    public static final int FRAGMENT_INDEX_BUTTON2 = 5;

    public static final int[] FRAGMENT_ERROR_EXIT = new int[]{
            R.layout.fragment_error,     // layout
            R.string.text_title_error,   // title
            R.string.text_line1_error,   // line1
            R.string.text_line2_error,   // line2
            R.string.button_retry,       // button left
            R.string.button_exit       // button right
    };
    public static final int[] FRAGMENT_ERROR_CLOSE = new int[]{
            R.layout.fragment_error,     // layout
            R.string.text_title_error,   // title
            R.string.text_line1_error,   // line1
            R.string.text_line2_error,   // line2
            R.string.button_retry,       // button left
            R.string.button_close       // button right
    };
    public static final int[] FRAGMENT_ERROR_WAIT = new int[]{
            R.layout.fragment_error,     // layout
            R.string.text_title_wait,   // title
            R.string.text_line1_wait,   // line1
            R.string.text_line2_wait,   // line2
            R.string.button_close,       // button left
            R.string.button_exit        // button right
    };

    // update service
    public static final String UPDATE_SERVICE_TAG = "UpdaterService";

    public static final String BROADCAST_ACTION_UPDATE_STARTED
            = "com.example.xyzreader.intent.action.STATE_CHANGE";
    public static final String BROADCAST_ACTION_UPDATE_FINISHED
            = "com.example.xyzreader.intent.action.UPDATE_FINISHED";

    public static final String EXTRA_REFRESHING
            = "com.example.xyzreader.intent.extra.REFRESHING";

    public static final String BROADCAST_ACTION_NO_NETWORK
            = "com.example.xyzreader.intent.action.NO_NETWORK";

    public static final String EXTRA_EMPTY_CURSOR
            = "com.example.xyzreader.intent.extra.EMPTY_CURSOR";


    /**
     * Span class used for RecyclerView as storage of display item parameters
     */
    public static class Span {
        /**
         * int number items of RecyclerView in width
         */
        private int spanX;
        /**
         * int number items of RecyclerView in height
         */
        private int spanY;
        /**
         * int width of RecyclerView item
         */
        private int width;
        /**
         * int height of RecyclerView item
         */
        private int height;

        /**
         * Constructor
         *
         * @param spanX  int number items of RecyclerView in width
         * @param spanY  int number items of RecyclerView in height
         * @param width  int width of RecyclerView item
         * @param height int height of RecyclerView item
         */
        public Span(int spanX, int spanY, int width, int height) {
            this.spanX = spanX;
            this.spanY = spanY;
            this.width = width;
            this.height = height;
        }

        public int getSpanX() {
            return spanX;
        }

        public int getSpanY() {
            return spanY;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
    }

    /**
     * Returns double value from 0.0 to 1.0 the width between to guideline of ConstraintLayout
     * Used to get actual space occupied by RecyclerView
     *
     * @param context Context of calling activity
     * @param guideId int resource Id of guideline of ConstraintLayout
     * @return double value from 0.0 to 1.0 the width between to guideline of ConstraintLayout
     */
    private static double getPercent(AppCompatActivity context, int guideId) {
        return ((ConstraintLayout.LayoutParams)
                context.findViewById(guideId).getLayoutParams()).guidePercent;
    }

    /**
     * Returns  Span class object with number and size of items in width and height.
     * Used to build GridLayout for RecyclerView object.
     *
     * @param context Context of calling activity
     * @return Span class object with number and size of items in width and height.
     */
    public static Span getDisplayMetrics(AppCompatActivity context) {
        DisplayMetrics dp = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dp);
// tightened to layout
        double height_ratio = getPercent(context, R.id.guide_h2) - getPercent(context, R.id.guide_h1);
        double width_ratio = getPercent(context, R.id.guide_v2) - getPercent(context, R.id.guide_v1);

        double width = dp.widthPixels / dp.density * width_ratio;
        double height = dp.heightPixels / dp.density * height_ratio;  // real height

        int spanInWidth = (int) Math.round(width / HIGH_SCALE_WIDTH);
        int spanHeight = (int) (width * dp.density / spanInWidth / SCALE_RATIO_VERT);  // vertical only
        int spanInHeight = (int) Math.round(height / HIGH_SCALE_HEIGHT);
        int spanWidth = (int) (height * dp.density / spanInHeight * SCALE_RATIO_HORZ);  // horizontal only
        if (spanInWidth < MIN_SPAN) spanInWidth = MIN_SPAN;
        if (spanInHeight < MIN_SPAN) spanInHeight = MIN_SPAN;

        if (spanHeight < MIN_HEIGHT) spanHeight = MIN_HEIGHT;

        int minWidth = (int) (MIN_WIDTH);  // horizontal
        if (spanWidth < minWidth) spanWidth = minWidth;

// vertical
//        mSpan = spanInWidth;
//        mSpanHeight = spanHeight;
// horizontal
//        mSpan = spanInHeight;
//        mSpanWidth = spanWidth;

        return new Span(spanInWidth, spanInHeight, spanWidth, spanHeight);
    }


    public static void motion(Resources res, View view, int id) {
        int scrollPos = res.getDimensionPixelOffset(id);
        int upDuration = res.getInteger(R.integer.instructive_up_duration);
        int downDuration = res.getInteger(R.integer.instructive_down_duration);
        int startDelay = res.getInteger(R.integer.instructive_start_delay);

        AnimatorSet as = new AnimatorSet();
        as.playSequentially(
                ObjectAnimator.ofInt(view, "scrollY", 0).setDuration(0),
                ObjectAnimator.ofInt(view, "scrollY", scrollPos).setDuration(upDuration),
                ObjectAnimator.ofInt(view, "scrollY", 0).setDuration(downDuration)

        );
        as.setStartDelay(startDelay);
        as.start();
    }

    public static void motion(final Context context, View view, int id) {
        Resources res = context.getResources();
        int scrollPos = res.getDimensionPixelOffset(id);
        int upDuration = res.getInteger(R.integer.instructive_up_duration);
        int downDuration = res.getInteger(R.integer.instructive_down_duration);
        int startDelay = res.getInteger(R.integer.instructive_start_delay);

        final View bottomBar = view.findViewById(R.id.bottom_toolbar);

        AnimatorSet as = new AnimatorSet();
        as.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                motionBottom(context, bottomBar);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        as.playSequentially(
                ObjectAnimator.ofInt(view, "scrollY", 0).setDuration(0),
                ObjectAnimator.ofInt(view, "scrollY", scrollPos).setDuration(upDuration),
                ObjectAnimator.ofInt(view, "scrollY", 0).setDuration(downDuration)
        );

        as.setStartDelay(startDelay);

        as.start();
    }


    public static void collapse(Resources res, View view, int startDelayId) {
        final AppBarLayout appBarLayout = ((AppBarLayout) view.findViewById(R.id.app_bar_detail));
        int startDelay = res.getInteger(startDelayId);
        view.animate().setStartDelay(startDelay).withEndAction(new Runnable() {
            @Override
            public void run() {
                appBarLayout.setExpanded(false, true);
            }
        }).start();

    }

    public static void collapse(final Context context, View view, int startDelayId) {
        final AppBarLayout appBarLayout = ((AppBarLayout) view.findViewById(R.id.app_bar_detail));
        final int startDelay = context.getResources().getInteger(startDelayId);
        final View bottomBar =  view.findViewById(R.id.bottom_toolbar);

        ViewPropertyAnimator animator = view.animate().setStartDelay(startDelay);

        animator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                appBarLayout.setExpanded(false, true);
                motionBottom(context, bottomBar);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });

        animator.start();

    }
    public static void instructiveMotion(ArticleDetailFragment fragment) {
        if (fragment.getActivity() == null) return;

        Context context = fragment.getActivity();
        Resources res = context.getResources();
        View view = fragment.getRootView();
        boolean isLand = res.getBoolean(R.bool.is_land);
        boolean isWide = res.getBoolean(R.bool.is_wide);

        if (view == null || fragment.getBitmap() == null) return;
        View bottomBar = view.findViewById(R.id.bottom_toolbar);


        if (!isLand && !sIsInstructedPort) {
//            motion(res, view, R.dimen.instructive_scroll_pos_port);
            motion(context, view, R.dimen.instructive_scroll_pos_port);
            sIsInstructedPort = true;
        }
        if (isLand && !sIsInstructedLand) {
            if (!isWide) {
                collapse(context, view, R.integer.instructive_start_delay);
            } else {
                motion(context, view, R.dimen.instructive_scroll_pos_port);
//                motionBottom(context, bottomBar);
            }

            sIsInstructedLand = true;
        }

    }

    // bottom bar
    public static void instructiveMotion(Context context, View view) {
        boolean isLand = context.getResources().getBoolean(R.bool.is_land);
        boolean isWide = context.getResources().getBoolean(R.bool.is_wide);

        if (!isLand && !sIsInstructedBottomPort) {
            motionBottom(context, view);
            sIsInstructedBottomPort = true;

        }
        if (isLand && !sIsInstructedBottomLand) {
            motionBottom(context, view);
            sIsInstructedBottomLand = true;
        }

    }

    public static void motionBottom(Context context, View view) {
        BottomBarRecycler br = new BottomBarRecycler(context, null);
        br.setContinue(view);
    }
}
