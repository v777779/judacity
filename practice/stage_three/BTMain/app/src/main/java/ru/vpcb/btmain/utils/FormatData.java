package ru.vpcb.btmain.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by V1 on 13-Nov-17.
 */

public class FormatData {
    private static final String[] cards = new String[] {
            "Recipe Green Card 1", "Recipe Blue Card 2",
            "Recipe Green Card 3", "Recipe Blue Card 4",
            "Recipe Green Card 5", "Recipe Blue Card 6",
            "Recipe Green Card 7", "Recipe Blue Card 8"
    };

    public static List<String>  loadMockCards() {

        return new ArrayList<>(Arrays.asList(cards));
    }
}
