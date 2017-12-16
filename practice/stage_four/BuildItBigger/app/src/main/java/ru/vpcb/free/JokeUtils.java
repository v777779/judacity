package ru.vpcb.free;

import android.app.Activity;
import android.content.res.Resources;
import android.support.constraint.ConstraintLayout;
import android.util.DisplayMetrics;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static ru.vpcb.constants.Constants.HIGH_SCALE_HEIGHT;
import static ru.vpcb.constants.Constants.HIGH_SCALE_WIDTH;
import static ru.vpcb.constants.Constants.MIN_HEIGHT;
import static ru.vpcb.constants.Constants.MIN_SPAN;
import static ru.vpcb.constants.Constants.MIN_WIDTH;
import static ru.vpcb.constants.Constants.SCALE_RATIO_HORZ;
import static ru.vpcb.constants.Constants.SCALE_RATIO_VERT;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 14-Dec-17
 * Email: vadim.v.voronov@gmail.com
 */

public class JokeUtils {
    public static class Span {
        int spanX;
        int spanY;
        int width;
        int height;

        public Span(int spanX, int spanY, int width, int height) {
            this.spanX = spanX;
            this.spanY = spanY;
            this.width = width;
            this.height = height;
        }
    }

    private static final int[] IMAGE_FRONT_IDS = new int[]{
            R.drawable.front_001, R.drawable.front_002, R.drawable.front_003,
            R.drawable.front_004, R.drawable.front_005, R.drawable.front_006,
            R.drawable.front_007
    };

    private static final int[] IMAGE_GRID_IDS = new int[]{
            R.drawable.joke_001, R.drawable.joke_002,
            R.drawable.joke_005, R.drawable.joke_006,
            R.drawable.joke_007, R.drawable.joke_009,
            R.drawable.joke_010, R.drawable.joke_011,
            R.drawable.joke_012, R.drawable.joke_014,
            R.drawable.joke_015, R.drawable.joke_016,
            R.drawable.joke_017, R.drawable.joke_018,
            R.drawable.joke_019
    };


    private static Random mRnd = new Random();


    public static int getGridImage() {
        return IMAGE_GRID_IDS[mRnd.nextInt(IMAGE_GRID_IDS.length)];
    }

    public static int getFrontImage() {
        return IMAGE_FRONT_IDS[mRnd.nextInt(IMAGE_FRONT_IDS.length)];
    }

    public static List<Integer> getImageList() {
        List<Integer> list = new ArrayList<>();
        for (int imageId : IMAGE_GRID_IDS) {
            list.add(imageId);
        }
        return list;
    }


    private static double getPercent(Activity context, int guideId) {
        return ((ConstraintLayout.LayoutParams)
                context.findViewById(guideId).getLayoutParams()).guidePercent;
    }


    public static Span getDisplayMetrics(Activity context) {
        DisplayMetrics dp = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dp);

        Resources res = context.getResources();


        double height_ratio = getPercent(context, R.id.guide_h2) - getPercent(context, R.id.guide_h1);  // tightened to layout
        double width_ratio = getPercent(context, R.id.guide_v2) - getPercent(context, R.id.guide_v1);   // tightened to layout

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


}
