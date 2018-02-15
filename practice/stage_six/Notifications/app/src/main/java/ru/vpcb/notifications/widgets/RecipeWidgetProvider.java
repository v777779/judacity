package ru.vpcb.notifications.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import ru.vpcb.notifications.MainActivity;
import ru.vpcb.notifications.R;
import ru.vpcb.notifications.data.FDFixture;
import timber.log.Timber;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

import static ru.vpcb.notifications.Utils.Config.BUNDLE_DETAIL_INTENT_ARGS;

import static ru.vpcb.notifications.Utils.Config.BUNDLE_MAIN_INTENT_ARGS;

import static ru.vpcb.notifications.Utils.Config.EMPTY_DASH;
import static ru.vpcb.notifications.Utils.Config.EMPTY_FIXTURE_ID;
import static ru.vpcb.notifications.Utils.Config.EMPTY_LONG_DASH;
import static ru.vpcb.notifications.Utils.Config.EMPTY_LONG_DATE;

import static ru.vpcb.notifications.Utils.Config.WIDGET_FIXTURE_ID;
import static ru.vpcb.notifications.Utils.Config.WIDGET_PREFERENCES;
import static ru.vpcb.notifications.Utils.Config.WIDGET_WIDGET_ID;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeWidgetProvider extends AppWidgetProvider {

    /**
     * Updates widget with parameters or creates empty new one
     * createWidget() method used for new widget
     * fillWidget()   method used to fill widget
     * <p>
     * This method called from WidgetService when user creates new widget
     * This method called by system when onUpdate() method called
     */
    static void updateWidget(Context context, AppWidgetManager appWidgetManager,
                             int widgetId, FDFixture fixture) {


        if (fixture == null || fixture.getId() <= 0) {
            createWidget(context, appWidgetManager, widgetId);
        } else {
            fillWidget(context, appWidgetManager, widgetId, fixture);
        }
    }

    private static void createWidget(Context context, AppWidgetManager appWidgetManager, int widgetId) {
        if (widgetId <= 0) return;

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.match_widget_provider);
        String widgetHead = context.getString(R.string.widget_head_text);



        Intent intent = new Intent(context, MainActivity.class); // call activity
        Bundle args = new Bundle();
        args.putInt(WIDGET_WIDGET_ID, widgetId);
        intent.putExtra(BUNDLE_MAIN_INTENT_ARGS, args);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, widgetId, intent, 0);
        views.setTextViewText(R.id.text_sm_item_league, widgetHead);
        views.setOnClickPendingIntent(R.id.app_widget_container, pendingIntent);

        appWidgetManager.updateAppWidget(widgetId, views);
    }

    public static void fillWidget(Context context, AppWidgetManager appWidgetManager,
                                  int widgetId, FDFixture fixture) {
        if (widgetId <= 0 || fixture == null || fixture.getId() <= 0) return;

        putWidgetFixtureId(context, widgetId, fixture.getId());
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.match_widget_provider);

        Intent intent;
        intent = new Intent(context, MainActivity.class);       // call activity second time
        Bundle args = new Bundle();
        args.putInt(WIDGET_FIXTURE_ID, fixture.getId());
        intent.putExtra(BUNDLE_DETAIL_INTENT_ARGS, args);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, widgetId, intent, FLAG_UPDATE_CURRENT);

// test!!!
        views.setTextViewText(R.id.text_sm_item_league, "League ID: "+fixture.getCompetitionId());


        views.setTextViewText(R.id.text_sm_team_home, fixture.getHomeTeamName());
        views.setTextViewText(R.id.text_sm_team_away, fixture.getAwayTeamName());
        views.setTextViewText(R.id.text_sm_item_time, fixture.getMatchTime());
        views.setTextViewText(R.id.text_sm_item_date, fixture.getMatchDateWidget());
        views.setTextViewText(R.id.text_sm_item_score_home, ""+fixture.getMatchScoreHome());
        views.setTextViewText(R.id.text_sm_item_score_away, ""+fixture.getMatchScoreAway());
        views.setTextViewText(R.id.text_sm_item_status, ""+fixture.getStatus());

        views.setOnClickPendingIntent(R.id.app_widget_container, pendingIntent);

        appWidgetManager.updateAppWidget(widgetId, views);
    }

    public static int getWidgetFixtureId(Context context, int widgetId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getInt(WIDGET_PREFERENCES + WIDGET_FIXTURE_ID + widgetId, EMPTY_FIXTURE_ID);
    }

    public static void putWidgetFixtureId(Context context, int widgetId, int fixtureId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(WIDGET_PREFERENCES + WIDGET_FIXTURE_ID + widgetId, fixtureId);
        editor.apply();
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        RecipeWidgetService.startWidgetUpdateAction(context);
    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
    }
}

