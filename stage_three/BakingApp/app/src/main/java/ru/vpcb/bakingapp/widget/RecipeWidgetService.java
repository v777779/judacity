package ru.vpcb.bakingapp.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.gson.Gson;

import java.util.List;

import ru.vpcb.bakingapp.data.RecipeContract;
import ru.vpcb.bakingapp.data.RecipeItem;
import timber.log.Timber;

import static ru.vpcb.bakingapp.MainActivity.getIngredientString;
import static ru.vpcb.bakingapp.MainActivity.getRecipeName;
import static ru.vpcb.bakingapp.data.RecipeContract.RecipeEntry.COLUMN_RECIPE_ID;
import static ru.vpcb.bakingapp.data.RecipeContract.RecipeEntry.COLUMN_RECIPE_VALUE;
import static ru.vpcb.bakingapp.utils.Constants.BUNDLE_WIDGET_INTENT;
import static ru.vpcb.bakingapp.utils.Constants.WIDGET_RECIPE_ID;
import static ru.vpcb.bakingapp.utils.Constants.WIDGET_SERVICE_FILL_ACTION;
import static ru.vpcb.bakingapp.utils.Constants.WIDGET_SERVICE_UPDATE_ACTION;
import static ru.vpcb.bakingapp.utils.Constants.WIDGET_WIDGET_ID;


public class RecipeWidgetService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public RecipeWidgetService() {
        super(RecipeWidgetService.class.getSimpleName());
    }

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

    public static void startWidgetUpdateAction(Context context) {
        Intent intent = new Intent(context, RecipeWidgetService.class);
        intent.setAction(WIDGET_SERVICE_UPDATE_ACTION);
        context.startService(intent);
    }

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
                recipeName = getRecipeName(getResources(),recipeItem);
                recipeList = getIngredientString(getResources(),recipeItem.getIngredients());
            }
            RecipeWidgetProvider.updateWidget(this,appWidgetManager,recipeId,widgetId,recipeName,recipeList);


        }
    }


    RecipeItem getRecipeItem(String sRecipeId) {
        if (sRecipeId.isEmpty()) return null;
        int recipeId;
        try {
            Uri uri = RecipeContract.RecipeEntry.CONTENT_URI;
            uri = uri.buildUpon().appendPath(sRecipeId).build();
            Cursor cursor = getContentResolver().query(uri,
                    null,
                    COLUMN_RECIPE_ID + "=?",
                    new String[]{sRecipeId},
                    COLUMN_RECIPE_ID + " ASC");

            cursor.moveToFirst();
            RecipeItem recipeItem = new Gson().fromJson(
                    cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_VALUE)),
                    RecipeItem.class);

            return recipeItem;
        } catch (Exception e) {
            Timber.d(e.getMessage());

        }

        return null;

    }


    void startFillWidgetAction(Bundle args) {
        String recipeName = "";
        String recipeList = "";
        int widgetId;
        int recipeId;
        try {
            String sRecipeId = args.getString(WIDGET_RECIPE_ID, "");
            String sWidgetId = args.getString(WIDGET_WIDGET_ID, "");
            if (sRecipeId.isEmpty() || sWidgetId.isEmpty()) return;

            Uri uri = RecipeContract.RecipeEntry.CONTENT_URI;
            uri = uri.buildUpon().appendPath(sRecipeId).build();
            Cursor cursor = getContentResolver().query(uri,
                    null,
                    COLUMN_RECIPE_ID + "=?",
                    new String[]{sRecipeId},
                    COLUMN_RECIPE_ID + " ASC");

            cursor.moveToFirst();
            RecipeItem recipeItem = new Gson().fromJson(
                    cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_VALUE)),
                    RecipeItem.class);
            widgetId = Integer.valueOf(sWidgetId);
            recipeId = Integer.valueOf(sRecipeId);
            recipeName = getRecipeName(getResources(),recipeItem);
            recipeList = getIngredientString(getResources(),recipeItem.getIngredients());

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            RecipeWidgetProvider.fillWidget(this, appWidgetManager, recipeId, widgetId,
                    recipeName, recipeList);
        } catch (Exception e) {
            Timber.d(e.getMessage());

        }
    }

}
