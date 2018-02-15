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


    public static final String EMPTY_DASH = "-";
    public static final String EMPTY_LONG_DASH = "\u2014";
    public static final String EMPTY_LONG_DATE = "--/--/----  --:--";
    public static final String EMPTY_NOTIFICATION = " home vs away --/--/----  --:--";
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"; //"2015-11-03T19:45:00Z"
    public static final int EMPTY_NOTIFICATION_ID = -1;
    public static final String EMPTY_TIME = "- : -";





    // widget
    // widget data



    // old
//    public static final String WIDGET_PREFERENCES = "ru.vpcb.bakingapp.widget.";
//
//    public static final String WIDGET_RECIPE_ID = "widget_recipe_position";
//    public static final String WIDGET_RECIPE_NAME = "widget_recipe_name";
//
//    // fragment detail data
//    public static final String BUNDLE_DETAIL_EXPANDED = "bundle_detail_expanded";
//    public static final String BUNDLE_DETAIL_POSITION = "bundle_detail_position";
//    public static final String BUNDLE_DETAIL_INTENT = "bundle_detail_intent";
//    public static final String BUNDLE_DETAIL_WIDGET_FILLED = "bundle_detail_widget_filled";




    // widgets
    public static final String WIDGET_FIXTURE_ID = "widget_fixture_id";
    public static final String WIDGET_WIDGET_ID = "widget_widget_id";
    public static final String WIDGET_PACKAGE = RecipeWidgetProvider.class.getPackage().getName()+".";

    public static final String WIDGET_SERVICE_FILL_ACTION = WIDGET_PACKAGE+"widget.ACTION_FILL";
    public static final String WIDGET_SERVICE_UPDATE_ACTION = WIDGET_PACKAGE+"widget.ACTION_UPDATE";

    public static final String BUNDLE_MAIN_INTENT_ARGS = WIDGET_PACKAGE+"widget.intent.BUNDLE_MAIN_ARGS";
    public static final String BUNDLE_DETAIL_INTENT_ARGS = WIDGET_PACKAGE+"widget.intent.BUNDLE_DETAIL_ARGS";
    public static final String WIDGET_PREFERENCES = WIDGET_PACKAGE+"widget.preferences.";

    public static final int EMPTY_WIDGET_ID = -1;
    public static final int EMPTY_FIXTURE_ID = -1;




    public static void showMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();   // change with shared preference
    }

}
