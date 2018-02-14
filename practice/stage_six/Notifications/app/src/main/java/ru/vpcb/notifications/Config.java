package ru.vpcb.notifications;

import android.content.Context;
import android.widget.Toast;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 13-Feb-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Config {
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


    public static final String EMPTY_LONG_DASH = "\u2014";
    public static final String EMPTY_LONG_DATE = "--/--/----  --:--";
    public static final String EMPTY_NOTIFICATION = " home vs away --/--/----  --:--";
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"; //"2015-11-03T19:45:00Z"
    public static final int EMPTY_NOTIFICATION_ID = -1;


    public static void showMessage(Context context, String message) {
        // test!!!
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();   // change with shared preference

    }

}
