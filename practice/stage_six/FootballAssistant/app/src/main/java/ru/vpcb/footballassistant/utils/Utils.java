package ru.vpcb.footballassistant.utils;

import android.content.Context;
import android.content.res.Resources;

import java.util.ResourceBundle;

import ru.vpcb.footballassistant.R;

/**
 * Created by V1 on 08-Feb-18.
 */

public class Utils {
    private static int prevTeamPos = -1;
    private static int prevLeaguePos = -1;

    public static int[] leagueIds = new int[]{
            R.string.text_test_rm_item_league,
            R.string.text_test_rm_item_league2,
            R.string.text_test_rm_item_league3,
            R.string.text_test_rm_item_league4
    };

    public static int[] teamIds = new int[]{
            R.drawable.logo_chelsea,
            R.drawable.logo_stoke_city
    };

    public static int[] flagIds = new int[]{
            R.drawable.flag_001,
            R.drawable.flag_002,
            R.drawable.flag_003,
    };


    public static int getLeagueId(int position) {
        return leagueIds[position % leagueIds.length];
    }

    public static int getTeamIconId(int position) {
        int pos = position % teamIds.length;
        if (pos == prevTeamPos) {
            pos = (position + 1) % teamIds.length;
        }
        prevTeamPos = pos;
        return teamIds[pos];
    }

    public static int getFlagIconId(int position) {

        return flagIds[position % flagIds.length];
    }


}
