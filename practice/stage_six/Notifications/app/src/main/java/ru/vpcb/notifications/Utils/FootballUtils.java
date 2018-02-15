package ru.vpcb.notifications.Utils;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import ru.vpcb.notifications.R;

import static ru.vpcb.notifications.Utils.Config.EMPTY_LONG_DATE;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 14-Feb-18
 * Email: vadim.v.voronov@gmail.com
 */

public class FootballUtils {

   private static Random rnd = new Random();

   public static Random getRnd(){
       if(rnd == null) rnd = new Random();
       return rnd;
   }

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


}
