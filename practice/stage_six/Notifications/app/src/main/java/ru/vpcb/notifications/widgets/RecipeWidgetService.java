package ru.vpcb.notifications.widgets;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import ru.vpcb.notifications.Utils.TempUtils;
import ru.vpcb.notifications.reciipe.RecipeItem;
import timber.log.Timber;

import static ru.vpcb.notifications.Utils.Config.BUNDLE_WIDGET_INTENT;
import static ru.vpcb.notifications.Utils.Config.WIDGET_RECIPE_ID;
import static ru.vpcb.notifications.Utils.Config.WIDGET_SERVICE_FILL_ACTION;
import static ru.vpcb.notifications.Utils.Config.WIDGET_SERVICE_UPDATE_ACTION;
import static ru.vpcb.notifications.Utils.Config.WIDGET_WIDGET_ID;


public class RecipeWidgetService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public RecipeWidgetService() {
        super(RecipeWidgetService.class.getSimpleName());
    }

    /**
     * Starts service with Intent to fill widget with recipe data
     * Uses bundle parameters added to Intent
     * Bundle parameters are Recipe ID in local database and Widget ID
     *
     * @param context  context of activity that starts service
     * @param recipeId string version of integer Recipe ID used to query recipe from local database
     * @param widgetId string version of integer Widget Id assigned by system
     */
    public static void startFillWidgetAction(Context context, String recipeId, String widgetId) {
        if (widgetId.isEmpty() || recipeId.isEmpty()) {
            return;
        }
        Bundle args = new Bundle();
        args.putString(WIDGET_RECIPE_ID, recipeId);
        args.putString(WIDGET_WIDGET_ID, widgetId);
        Intent intent = new Intent(context, RecipeWidgetService.class);
        intent.putExtra(BUNDLE_WIDGET_INTENT, args);
        intent.setAction(WIDGET_SERVICE_FILL_ACTION);
        context.startService(intent);
    }

    /**
     * Starts service with Intent to create new widget or update existed one
     * The only valid parameter in new widget is Widget ID
     * This method called by when user creates new widget
     * This method called by system when WidgetService.onUpdate() method called
     *
     * @param context
     */
    public static void startWidgetUpdateAction(Context context) {
        Intent intent = new Intent(context, RecipeWidgetService.class);
        intent.setAction(WIDGET_SERVICE_UPDATE_ACTION);
        context.startService(intent);
    }

    /**
     * Serves requests for actions to service
     *
     * @param intent Intent with action and bundle of parameters if exists
     */
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (action.equals(WIDGET_SERVICE_FILL_ACTION) &&
                    intent.hasExtra(BUNDLE_WIDGET_INTENT)) {
                startFillWidgetAction(intent.getBundleExtra(BUNDLE_WIDGET_INTENT));
            }
            if (action.equals(WIDGET_SERVICE_UPDATE_ACTION)) {

                startWidgetUpdateAction();
            }
        }
    }

    /**
     * Creates new empty widget as response of service WIDGET_SERVICE_UPDATE_ACTION action
     * RecipeWidgetProvider.updateWidget() method used to create new widget
     */
    private void startWidgetUpdateAction() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        ComponentName componentName = new ComponentName(this, RecipeWidgetProvider.class);
        int[] ids = appWidgetManager.getAppWidgetIds(componentName); // all widgets to update
        if (ids == null) return;
        for (int widgetId : ids) {
            String recipeId = RecipeWidgetProvider.getWidgetRecipeId(this, widgetId);
            String recipeName = "";
            String recipeList = "";

            RecipeItem recipeItem = getRecipeItem(recipeId);
            if (recipeItem != null) {
                recipeName = recipeItem.getName();
                recipeList = getIngredientString(recipeItem.getIngredients());
            }
            RecipeWidgetProvider.updateWidget(this, appWidgetManager, recipeId, widgetId, recipeName, recipeList);
        }
    }


    public String getIngredientString(List<RecipeItem.Ingredient> list) {
        StringBuilder sb = new StringBuilder();
        int count = 1;
        for (RecipeItem.Ingredient ingredient : list) {
            String s = ingredient.toString().substring(0, 1).toUpperCase() + ingredient.toString().substring(1);
            sb.append(count + ". " + s + "\n");
            count++;
            if (count == 4) break;
        }
        return sb.toString();
    }

    /**
     * Extracts RecipeItem from local database by Recipe ID
     *
     * @param sRecipeId string version of Recipe ID value used for query request
     * @return RecipeItem object or null
     */
    @Nullable
    private RecipeItem getRecipeItem(String sRecipeId) {
        if (sRecipeId.isEmpty()) return null;
        String json = TempUtils.readFileAssets(this, "baking.json");
        Type listType = new TypeToken<List<RecipeItem>>() {
        }.getType();
        List<RecipeItem> recipeItems = new Gson().fromJson(json, listType);
        for (RecipeItem recipeItem : recipeItems) {
            if (String.valueOf(recipeItem.getId()).equals(sRecipeId)) {
                return recipeItem;
            }
        }
        return null;
    }

    /**
     * Fills widget with selected RecipeItem data
     * Uses bundle as input parameter RecipeID and WidgetID
     * RecipeID used for database query
     * WidgetID used as address of widget is filled
     * Filling of widget RecipeWidgetProvider.fillWidget() method does
     * *
     *
     * @param args Bundle args with RecipeID and WidgetID
     */
    private void startFillWidgetAction(Bundle args) {
        String recipeName = "";
        String recipeList = "";
        int widgetId;
        int recipeId;

        try {
            String sRecipeId = args.getString(WIDGET_RECIPE_ID, "");
            String sWidgetId = args.getString(WIDGET_WIDGET_ID, "");

            RecipeItem recipeItem = getRecipeItem(sRecipeId);
            if (recipeItem == null) return;

            widgetId = Integer.valueOf(sWidgetId);
            recipeId = Integer.valueOf(sRecipeId);
            recipeName = recipeItem.getName();
            recipeList = getIngredientString(recipeItem.getIngredients());

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            RecipeWidgetProvider.fillWidget(this, appWidgetManager, recipeId, widgetId,
                    recipeName, recipeList);
        } catch (Exception e) {
            Timber.d(e.getMessage());
        }
    }

}
