package ru.vpcb.bakingapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import ru.vpcb.bakingapp.DetailActivity;
import ru.vpcb.bakingapp.MainActivity;
import ru.vpcb.bakingapp.R;
import timber.log.Timber;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static ru.vpcb.bakingapp.utils.Constants.BUNDLE_DETAIL_INTENT;
import static ru.vpcb.bakingapp.utils.Constants.BUNDLE_WIDGET_INTENT;
import static ru.vpcb.bakingapp.utils.Constants.WIDGET_PREFERENCES;
import static ru.vpcb.bakingapp.utils.Constants.WIDGET_RECIPE_ID;
import static ru.vpcb.bakingapp.utils.Constants.WIDGET_RECIPE_NAME;
import static ru.vpcb.bakingapp.utils.Constants.WIDGET_WIDGET_ID;

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

    /**
     *  Creates empty widget with valid WidgetId and PendingIntent with MainActivity as destination
     *  Prepare Bundle with parameters  WidgetID and PendingIntent
     *  WidgetID is string version of int widgetId, used to show when this parameter is empty
     *  Detail Activity recognizes not empty widgetID and shows "fill widget" button in response
     *  WidgetID used as address of widget in MainActivity and DetailActivity
     *  Pending Intent to start MainActivity on click on widget
     *
     * @param context Context of calling activity
     * @param appWidgetManager  AppWidgetManager widget manager
     * @param widgetId String version of int WidgetID, string used to show the case when WidgetID is empty
     */
    private static void createWidget(Context context, AppWidgetManager appWidgetManager, int widgetId) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_widget_provider);
        String widgetHead = context.getString(R.string.empty_widget_head);
        String widgetBody = context.getString(R.string.empty_widget_body);

        Intent intent = new Intent(context, MainActivity.class); // call activity
        Bundle args = new Bundle();
        args.putString(WIDGET_WIDGET_ID, new Integer(widgetId).toString());
        intent.putExtra(BUNDLE_WIDGET_INTENT, args);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, widgetId, intent, 0);
        views.setTextViewText(R.id.appwidget_header, widgetHead);
        views.setTextViewText(R.id.appwidget_body, widgetBody);
        views.setOnClickPendingIntent(R.id.app_widget_container, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(widgetId, views);
    }

    /**
     * Fills widget with List of ingredients , WidgetId, RecipeId and PendingIntent with DetailActivity as destination
     *  DetailActivity recognizes that widgetID is empty and does not show "fill widget" button
     *  RecipeID used for database query
     *  Pending Intent to start DetailActivity on click on widget
     *
     * @param context Context of calling activity
     * @param appWidgetManager  AppWidgetManager widget manager
     * @param recipeId String version of int RecipeID
     * @param widgetId String version of int WidgetID, string used to show the case when WidgetID is empty
     * @param recipeName String the name of Recipe
     * @param recipeList String the list of ingredients
     */
    public static void fillWidget(Context context, AppWidgetManager appWidgetManager,
                                   int recipeId, int widgetId,
                                   String recipeName, String recipeList) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(WIDGET_PREFERENCES + WIDGET_RECIPE_ID + widgetId, Integer.toString(recipeId));
        editor.putString(WIDGET_PREFERENCES + WIDGET_RECIPE_NAME + widgetId, recipeName);
        editor.apply();

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_widget_provider);

        Intent intent;
        intent = new Intent(context, DetailActivity.class); // call activity
        Bundle args = new Bundle();
//        args.putString(WIDGET_WIDGET_ID, new Integer(widgetId).toString());
        args.putString(WIDGET_RECIPE_ID, new Integer(recipeId).toString());
        intent.putExtra(BUNDLE_DETAIL_INTENT, args);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, widgetId, intent, FLAG_UPDATE_CURRENT);

        views.setTextViewText(R.id.appwidget_header, recipeName);
        views.setTextViewText(R.id.appwidget_body, recipeList);
        views.setOnClickPendingIntent(R.id.app_widget_container, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(widgetId, views);
    }

    /**
     * Returns String of RecipeId from Preferences of widget
     *
     * @param context Context of calling activity
     * @param widgetId int WidgetID that owner of RecipeID
     * @return String RecipeID value
     */
    public static String getWidgetRecipeId(Context context, int widgetId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(WIDGET_PREFERENCES + WIDGET_RECIPE_ID + widgetId, "");
    }

    /**
     * System method for updates all widgets of RecipeWidgetProvider
     *
     * @param context           Context of calling application
     * @param appWidgetManager  AppWidgetManager widget manager object
     * @param appWidgetIds      int[] WidgetID array to update
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        RecipeWidgetService.startWidgetUpdateAction(context);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

