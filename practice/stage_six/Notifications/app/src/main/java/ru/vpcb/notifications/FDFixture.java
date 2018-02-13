package ru.vpcb.notifications;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static ru.vpcb.notifications.Config.DATE_FORMAT;
import static ru.vpcb.notifications.Config.EMPTY_LONG_DASH;
import static ru.vpcb.notifications.Config.EMPTY_LONG_DATE;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 13-Feb-18
 * Email: vadim.v.voronov@gmail.com
 */
public class FDFixture {
    private String date;
    private String home;
    private String away;

    public FDFixture(String date, String home, String away) {
        this.date = date;
        this.home = home;
        this.away = away;
    }

    public FDFixture() {
        date = "2015-11-03T19:45:00Z";
        this.home = "Manchester United FC";
        this.away = "CSKA Moscow";
    }

    public String getDate() {
        return date;
    }

    public Calendar getCalendar() {
        try {
            Date date = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH).parse(this.date);
            Calendar c = Calendar.getInstance();

            c.setTime(date);
            return c;
        } catch (ParseException | NullPointerException e) {
            return null;
        }
    }

    public String getFullDate(Context context) {
        Calendar c = getCalendar();
        if (c == null) return EMPTY_LONG_DATE;

        return context.getString(R.string.notification_time,
                c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH),
                c.get(Calendar.YEAR), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
    }

    public String getHome() {
        if(home == null || home.isEmpty())return EMPTY_LONG_DASH;
        return home;
    }

    public String getAway() {
        if(away == null || away.isEmpty())return EMPTY_LONG_DASH;
        return away;
    }

}
