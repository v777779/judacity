package ru.vpcb.footballassistant.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import java.util.Locale;

import ru.vpcb.footballassistant.R;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 24-Jan-18
 * Email: vadim.v.voronov@gmail.com
 */
public class FootballUtils {

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
        boolean value = sp.getBoolean(res.getString(keyId),   res.getBoolean(valueId));
        return value;
    }

    public static int getPrefInt(Context context, int keyId, int defaultId) {
        Resources res = context.getResources();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        int value = sp.getInt(res.getString(keyId),   res.getInteger(defaultId));
        return value;
    }

    public static void setRefreshTime(Context context) {
        Resources res = context.getResources();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(res.getString(R.string.pref_update_time_key),FDUtils.currentTimeMinutes());
        editor.apply();
    }

    public static boolean  isRefreshTime(Context context) {
        int time = getPrefInt(context,
                R.string.pref_update_time_key,
                R.integer.pref_update_time_default);
        int delay = getPrefInt(context,
                R.string.pref_delay_time_key,
                R.integer.pref_delay_time_default);

        return FDUtils.currentTimeMinutes()-time < delay;
    }



    public static String formatString(int value) {
        return String.format(Locale.ENGLISH,"%d", value);
    }

    public static String formatStringWide(int value) {
        return String.format(Locale.ENGLISH,"%4d", value);
    }

    public static String formatStringInt(String s) {
        return  s.substring(s.lastIndexOf("/")+1);
    }




}
