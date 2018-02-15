package ru.vpcb.notifications.Utils;

import android.content.Context;
import android.widget.Toast;

import ru.vpcb.notifications.widgets.RecipeWidgetProvider;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 13-Feb-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Config {

    // data
    public static final String FD_REGEX_TEAMS = ".*teams/";
    public static final String FD_REGEX_FIXTURES = ".*fixtures/";
    public static final String FD_REGEX_COMPETITIONS = ".*competitions/";


    // competitions
    public static final String EMPTY_TEAM_NAME = "-";
    public static final String EMPTY_TWO_DASH = "--";
    public static final String EMPTY_MATCH_TIME = "-- : --";
    public static final String EMPTY_FIXTURE_DATE = "--/--/--";
    public static final String EMPTY_PLAYER_DATE = "--.--.--";
    public static final String EMPTY_MATCH_SCORE = "- : -";

    // news
    public static final String EMPTY_DASH = "-";
    public static final String EMPTY_LONG_DASH = "\u2014";
    public static final String EMPTY_DATE = "--/--/--";
    public static final String EMPTY_LONG_DATE = "--/--/----  --:--";
    public static final String EMPTY_DASH_AGO = "- ago";
    public static final String EMPTY_NOTIFICATION = " home vs away --/--/----  --:--";


    public static final String DATE_FULL_SSS_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String DATE_FULL_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";


    // notifications
    public static final String NT_FB_JOB_DISPATCHER_ID = "nt_fb_job_dispatcher_id";
    public static final String NT_FB_JOB_CHANNEL_ID = "nt_fb_job_channel_id";
    public static final int NT_NOTIFICATION_ID = 1240;
    public static final int NT_FLEXTIME_SECONDS = 2; // seconds
    public static final int NT_DELAY_TIME_MINIMUM = 1; // seconds

    //    actions
    public static final String NT_ACTION_CANCEL = "ru.vpcb.footballassistant.ACTION_NOTIFICATION_CANCEL";
    public static final String NT_ACTION_APPLY = "ru.vpcb.footballassistant.ACTION_NOTIFCATION_APPLY";
    public static final String NT_ACTION_ACTIVITY = "ru.vpcb.footballassistant.ACTION_NOTIFICATION_ACTIVITY";
    public static final String NT_ACTION_CREATE = "ru.vpcb.footballassistant.ACTION_NOTIFCATION_CREATE";

    public static final int NT_ACTION_CANCEL_PENDING_ID = 1250;
    public static final int NT_ACTION_APPLY_PENDING_ID = 1260;
    public static final int NT_ACTION_ACTIVITY_PENDING_ID = 1270;
    public static final int NT_RANDOM_RANGE = 1000;

    public static final String NT_BUNDLE_INTENT_NOTIFICATION_BODY = "nt_bundle_intent_notification_body";
    public static final String NT_BUNDLE_INTENT_NOTIFICATION_ID = "nt_bundle_intent_notification_id";


    public static final int EMPTY_NOTIFICATION_ID = -1;


    // widgets
    public static final String WIDGET_BUNDLE_FIXTURE_ID = "widget_fixture_id";
    public static final String WIDGET_BUNDLE_WIDGET_ID = "widget_widget_id";
    public static final String WIDGET_PACKAGE = RecipeWidgetProvider.class.getPackage().getName() + ".";

    public static final String WIDGET_SERVICE_FILL_ACTION = WIDGET_PACKAGE + "ACTION_FILL";
    public static final String WIDGET_SERVICE_UPDATE_ACTION = WIDGET_PACKAGE + "ACTION_UPDATE";
    public static final String WIDGET_SERVICE_REFRESH_ACTION = WIDGET_PACKAGE + "ACTION_REFRESH";

    public static final String WIDGET_INTENT_BUNDLE = WIDGET_PACKAGE + "widget.intent.BUNDLE_ARGS";
    public static final String WIDGET_PREFERENCES = WIDGET_PACKAGE + "widget.preferences.";

    public static final int EMPTY_WIDGET_ID = -1;
    public static final int EMPTY_FIXTURE_ID = -1;

    public static final String DATE_WIDGET_PATTERN = "EEE, dd MMM yyyy";
    public static final int WIDGET_PID_BASE = 12800000;
    public static final int WIDGET_PID_SCALE = 1000;
    public static final int WIDGET_PID_OFFSET0 = 0;
    public static final int WIDGET_PID_OFFSET1 = 0;
    public static final int WIDGET_PID_OFFSET2 = 0;

    public static final int WIDGET_ANIMATE_TIMEOUT = 100;

    public static void showMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();   // change with shared preference
    }

}
