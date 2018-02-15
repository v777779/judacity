package ru.vpcb.notifications.Utils;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    public static Random getRandom() {
        if (rnd == null) rnd = new Random();
        return rnd;
    }

    public static String formatStringDate(Context context, Calendar c) {

        if (c == null) return EMPTY_LONG_DATE;

        return context.getString(R.string.notification_time,
                c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH),
                c.get(Calendar.YEAR), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
    }


    public static String formatStringDateWidget(Context context, Calendar c) {

        if (c == null) return context.getString(R.string.widget_empty_time);
        SimpleDateFormat df  = new SimpleDateFormat("EEE, dd MMM yyyy");
        return df.format(c.getTime());
    }


}
