package ru.vpcb.btdetail.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by V1 on 13-Nov-17.
 */

public class FragmentData {
    private static final String[] cards = new String[] {
            "Recipe Green   Card 1",
            "Recipe Blue    Card 2",
            "Recipe Red     Card 3",
            "Recipe Yellow  Card 4",
            "Recipe Brown   Card 5",
            "Recipe Magenta Card 6",
            "Recipe White   Card 7",
            "Recipe Orange  Card 8"
    };

    private static final String[] details = new String[] {
            "Recipe Ingredients",
            "Recipe Purple Step 1",
            "Recipe Orange Card 2",
            "Recipe White  Card 3",
            "Recipe Red    Card 4",
            "Recipe Yellow Card 5",
            "Recipe Green  Card 6",
            "Recipe Black  Card 7",
            "Recipe Blue   Card 8"
    };



    public static List<String>  loadMockCards() {

        return new ArrayList<>(Arrays.asList(cards));
    }

    public static List<String>  loadMockDetails() {

        return new ArrayList<>(Arrays.asList(details));
    }
}
