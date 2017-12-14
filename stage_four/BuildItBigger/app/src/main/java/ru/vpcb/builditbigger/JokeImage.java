package ru.vpcb.builditbigger;

import java.util.Random;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 14-Dec-17
 * Email: vadim.v.voronov@gmail.com
 */

public class JokeImage {
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


    public static int getImage() {
        return IMAGE_GRID_IDS[mRnd.nextInt(IMAGE_GRID_IDS.length)];
    }

    public static int getFrontImage() {
        return IMAGE_FRONT_IDS[mRnd.nextInt(IMAGE_FRONT_IDS.length)];
    }


}
