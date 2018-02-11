package ru.vpcb.footballassistant.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import ru.vpcb.footballassistant.R;

import static ru.vpcb.footballassistant.utils.Config.IMAGE_IDS;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 24-Jan-18
 * Email: vadim.v.voronov@gmail.com
 */
public class FootballUtils {
    private static Random rnd = new Random();

    // strings
    public static String formatString(int value) {
        return String.format(Locale.ENGLISH, "%d", value);
    }

    public static String formatStringWide(int value) {
        return String.format(Locale.ENGLISH, "%4d", value);
    }

    public static String formatStringInt(String s) {
        return s.substring(s.lastIndexOf("/") + 1);
    }

    public static String formatStringDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        String day = String.format(Locale.ENGLISH, "%02d", c.get(Calendar.DAY_OF_MONTH));
        String month = String.format(Locale.ENGLISH, "%02d", c.get(Calendar.MONTH) + 1);
        String year = String.format(Locale.ENGLISH, "%04d", c.get(Calendar.YEAR));
        return ""+day + "/" + month + "/" + year.substring(2, year.length())+"";
    }
    public static String formatStringDate(Date date,String delim) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        String day = String.format(Locale.ENGLISH, "%02d", c.get(Calendar.DAY_OF_MONTH));
        String month = String.format(Locale.ENGLISH, "%02d", c.get(Calendar.MONTH) + 1);
        String year = String.format(Locale.ENGLISH, "%04d", c.get(Calendar.YEAR));
        return ""+day + delim + month + delim + year.substring(2, year.length())+"";
    }




    // images

    public static int getImageBackId() {
        return IMAGE_IDS[rnd.nextInt(IMAGE_IDS.length)];
    }

    // connection

    /**
     * Returns status of connection to network
     *
     * @param context Context of calling activity
     * @return boolean status of connection, true if connected, false if not
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


// test!!!

    /**
     * Placeholder
     *
     * @param context
     * @return
     */
    public static boolean isCursorEmpty(Context context) {

        return true;
    }

    // shared preferences
    public static boolean getPrefBool(Context context, int keyId, int valueId) {
        Resources res = context.getResources();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        boolean value = sp.getBoolean(res.getString(keyId), res.getBoolean(valueId));
        return value;
    }

    public static int getPrefInt(Context context, int keyId, int defaultId) {
        Resources res = context.getResources();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        int value = sp.getInt(res.getString(keyId), res.getInteger(defaultId));
        return value;
    }

    public static void setRefreshTime(Context context) {
        Resources res = context.getResources();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(res.getString(R.string.pref_data_update_time_key), FDUtils.currentTimeMinutes());
        editor.apply();
    }

    public static boolean isFootballDataRefreshed(Context context) {
        int time = getPrefInt(context,
                R.string.pref_data_update_time_key,
                R.integer.pref_data_update_time_default);
        int delay = getPrefInt(context,
                R.string.pref_data_delay_time_key,
                R.integer.pref_data_delay_time_default);

        return FDUtils.currentTimeMinutes() - time < delay;
    }

    public static boolean isNewsDataRefreshed(Context context) {
        int time = getPrefInt(context,
                R.string.pref_news_update_time_key,
                R.integer.pref_news_update_time_default);
        int delay = getPrefInt(context,
                R.string.pref_news_delay_time_key,
                R.integer.pref_news_delay_time_default);

        return FDUtils.currentTimeMinutes() - time < delay;
    }

}
