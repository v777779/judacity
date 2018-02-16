package ru.vpcb.footballassistant.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.preference.PreferenceManager;
import android.view.Window;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import ru.vpcb.footballassistant.R;
import timber.log.Timber;

import static ru.vpcb.footballassistant.utils.Config.EMPTY_LONG_DATE;
import static ru.vpcb.footballassistant.utils.Config.IMAGE_IDS;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 24-Jan-18
 * Email: vadim.v.voronov@gmail.com
 */
public class FootballUtils {
    public static Random rnd = new Random();

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
        return "" + day + "/" + month + "/" + year.substring(2, year.length()) + "";
    }

    public static String formatStringDate(Date date, String delim) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        String day = String.format(Locale.ENGLISH, "%02d", c.get(Calendar.DAY_OF_MONTH));
        String month = String.format(Locale.ENGLISH, "%02d", c.get(Calendar.MONTH) + 1);
        String year = String.format(Locale.ENGLISH, "%04d", c.get(Calendar.YEAR));
        return "" + day + delim + month + delim + year.substring(2, year.length()) + "";
    }

    public static String formatStringDate(Context context, Calendar c) {

        if (c == null) return EMPTY_LONG_DATE;

        return context.getString(R.string.notification_time,
                c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH),
                c.get(Calendar.YEAR), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
    }

    private static boolean isSnackbarStyle(Context context) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(context.getString(R.string.pref_snackbar_key), false);
    }


    public static void showMessage(Context context, String s) {
        if (!(context instanceof Activity) || s == null || s.isEmpty()) return;

        try {
            if (isSnackbarStyle(context) ) {
                Snackbar.make(((Activity) context).getWindow().getDecorView(), s, Snackbar.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
            }
        } catch (NullPointerException e) {
            Timber.d(context.getString(R.string.notification_empty_activity_exception,e.getMessage()));
        }
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


}
