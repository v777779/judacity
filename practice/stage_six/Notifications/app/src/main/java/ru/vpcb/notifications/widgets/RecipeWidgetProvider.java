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
import timber.log.Timber;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

import static ru.vpcb.notifications.Utils.Config.BUNDLE_DETAIL_INTENT_ARGS;

import static ru.vpcb.notifications.Utils.Config.BUNDLE_MAIN_INTENT_ARGS;

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
     *
     * This method called from WidgetService when user creates new widget
     * This method called by system when onUpdate() method called
     *
     * @param context  Context of calling activity
     * @param appWidgetManager  AppWidgetManager
     * @param sRecipeId String version of int RecipeID, string used to check if RecipeID is empty
     * @param widgetId String version of int WidgetID, string used to check if WidgetID is empty
     * @param recipeName String the name of recipe
     * @param recipeList String the list of ingredients
     */
    static void updateWidget(Context context, AppWidgetManager appWidgetManager,
                                String sRecipeId, int widgetId,
                                String recipeName, String recipeList) {

        int recipeId = 0;
        try {
            recipeId = Integer.valueOf(sRecipeId);
        }catch (Exception e) {
            Timber.d(e.getMessage());
            sRecipeId = "";
        }
        if (sRecipeId.isEmpty()) {
            createWidget(context, appWidgetManager, widgetId);
        } else {
            fillWidget(context, appWidgetManager, recipeId, widgetId, recipeName, recipeList);
        }
    }

    private static void createWidget(Context context, AppWidgetManager appWidgetManager, int widgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.match_widget_provider);
        String widgetHead = context.getString(R.string.text_test_rm_item_team_away);
        String widgetBody = context.getString(R.string.text_test_rm_item_team_away);;

        Intent intent = new Intent(context, MainActivity.class); // call activity
        Bundle args = new Bundle();
        args.putString(WIDGET_WIDGET_ID, new Integer(widgetId).toString());
        intent.putExtra(BUNDLE_MAIN_INTENT_ARGS, args);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, widgetId, intent, 0);
        views.setTextViewText(R.id.text_sm_team_home, widgetHead);
        views.setTextViewText(R.id.text_sm_team_away, widgetBody);
        views.setOnClickPendingIntent(R.id.app_widget_container, pendingIntent);

        appWidgetManager.updateAppWidget(widgetId, views);
    }

    public static void fillWidget(Context context, AppWidgetManager appWidgetManager,
                                   int recipeId, int widgetId,
                                   String recipeName, String recipeList) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(WIDGET_PREFERENCES + WIDGET_FIXTURE_ID + widgetId, Integer.toString(recipeId));
        editor.apply();

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.match_widget_provider);

        Intent intent;
        intent = new Intent(context, MainActivity.class); // call activity
        Bundle args = new Bundle();
        args.putString(WIDGET_FIXTURE_ID, new Integer(recipeId).toString());
        intent.putExtra(BUNDLE_DETAIL_INTENT_ARGS, args);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, widgetId, intent, FLAG_UPDATE_CURRENT);

        views.setTextViewText(R.id.text_sm_team_home, recipeName);
        views.setTextViewText(R.id.text_sm_team_away, recipeList);
        views.setOnClickPendingIntent(R.id.app_widget_container, pendingIntent);

        appWidgetManager.updateAppWidget(widgetId, views);
    }


    public static String getWidgetFixtureId(Context context, int widgetId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(WIDGET_PREFERENCES + WIDGET_FIXTURE_ID + widgetId, "");
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

