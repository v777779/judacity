package ru.vpcb.notifications.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.concurrent.ExecutionException;

import ru.vpcb.notifications.MainActivity;
import ru.vpcb.notifications.R;
import ru.vpcb.notifications.data.FDFixture;
import ru.vpcb.notifications.glide.SvgSoftwareLayerSetter;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static ru.vpcb.notifications.Utils.Config.EMPTY_WIDGET_ID;
import static ru.vpcb.notifications.Utils.Config.WIDGET_ANIMATE_TIMEOUT;
import static ru.vpcb.notifications.Utils.Config.WIDGET_INTENT_BUNDLE;
import static ru.vpcb.notifications.Utils.Config.EMPTY_FIXTURE_ID;
import static ru.vpcb.notifications.Utils.Config.WIDGET_PID_BASE;
import static ru.vpcb.notifications.Utils.Config.WIDGET_BUNDLE_FIXTURE_ID;
import static ru.vpcb.notifications.Utils.Config.WIDGET_PID_OFFSET0;
import static ru.vpcb.notifications.Utils.Config.WIDGET_PID_OFFSET1;
import static ru.vpcb.notifications.Utils.Config.WIDGET_PREFERENCES;
import static ru.vpcb.notifications.Utils.Config.WIDGET_PID_SCALE;
import static ru.vpcb.notifications.Utils.Config.WIDGET_BUNDLE_WIDGET_ID;
import static ru.vpcb.notifications.Utils.Config.WIDGET_SERVICE_REFRESH_ACTION;

/**
 * Implementation of App Widget functionality.
 */
public class MatchWidgetProvider extends AppWidgetProvider {

    private static RequestBuilder<PictureDrawable> mRequestBuilder;
    private static RequestBuilder<Drawable> mRequestBuilderCommon;


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
        int widgetPID = WIDGET_PID_BASE + WIDGET_PID_SCALE * widgetId + WIDGET_PID_OFFSET0;

        Intent intent = new Intent(context, MainActivity.class); // call activity
        Bundle args = new Bundle();
        args.putInt(WIDGET_BUNDLE_WIDGET_ID, widgetId);
        intent.putExtra(WIDGET_INTENT_BUNDLE, args);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, widgetId, intent, 0);
        views.setTextViewText(R.id.text_sm_item_league, widgetHead);
        views.setOnClickPendingIntent(R.id.app_widget_container, pendingIntent);

        appWidgetManager.updateAppWidget(widgetId, views);
    }

    private static PictureDrawable mPicture;

    public static void fillWidget(final Context context, final AppWidgetManager appWidgetManager,
                                 final int widgetId, FDFixture fixture) {

        if (widgetId <= 0 || fixture == null || fixture.getId() <= 0) return;

       final int widgetPID = WIDGET_PID_BASE + WIDGET_PID_SCALE * widgetId + WIDGET_PID_OFFSET0;
        int fixtureId = fixture.getId();

        putWidgetFixtureId(context, widgetId, fixtureId);
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.match_widget_provider);

        Intent intent = new Intent(context, MainActivity.class);       // call activity second time
        Bundle args = new Bundle();
        args.putInt(WIDGET_BUNDLE_FIXTURE_ID, fixtureId);
        intent.putExtra(WIDGET_INTENT_BUNDLE, args);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, widgetPID, intent, FLAG_UPDATE_CURRENT);

// test!!!
        views.setTextViewText(R.id.text_sm_item_league, fixture.getCompetitionName());
        views.setTextViewText(R.id.text_sm_team_home, fixture.getHomeTeamName());
        views.setTextViewText(R.id.text_sm_team_away, fixture.getAwayTeamName());
        views.setTextViewText(R.id.text_sm_item_time, fixture.getMatchTime());
        views.setTextViewText(R.id.text_sm_item_date, fixture.getMatchDateWidget());
        views.setTextViewText(R.id.text_sm_item_score_home, "" + fixture.getMatchScoreHome());
        views.setTextViewText(R.id.text_sm_item_score_away, "" + fixture.getMatchScoreAway());
        views.setTextViewText(R.id.text_sm_item_status, "" + fixture.getStatus());
        views.setOnClickPendingIntent(R.id.app_widget_container, pendingIntent);
        views.setOnClickPendingIntent(R.id.match_refresh, getPendingIntent(context, widgetId, fixtureId));

        setupRequestBuilder(context);

        views.setInt(R.id.app_widget_container,"setBackgroundResource",R.drawable.widget_back_light);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(WIDGET_ANIMATE_TIMEOUT);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                views.setInt(R.id.app_widget_container,"setBackgroundResource", R.drawable.widget_back);
                appWidgetManager.updateAppWidget(widgetId, views);
            }
        }).start();


//        String imageURL = "http://upload.wikimedia.org/wikipedia/de/0/08/LOSC_Lille_Crest_2012.png";
//        Bitmap bitmap = loadBitmap(context, imageURL);
//        if (bitmap != null)
//            views.setImageViewBitmap(R.id.image_sm_team_home, bitmap);
//        views.setImageViewResource(R.id.image_sm_team_home,R.drawable.fc_logo_widget2);
//        views.setImageViewResource(R.id.image_sm_team_away,R.drawable.fc_logo_widget3);

        appWidgetManager.updateAppWidget(widgetId, views);
    }


    private static PendingIntent getPendingIntent(Context context, int widgetId, int fixtureId) {

        Intent intent = new Intent(context, MatchWidgetProvider.class);
        intent.setAction(WIDGET_SERVICE_REFRESH_ACTION);
        Bundle args = new Bundle();
        args.putInt(WIDGET_BUNDLE_WIDGET_ID, widgetId);
        args.putInt(WIDGET_BUNDLE_FIXTURE_ID, fixtureId);
        intent.putExtra(WIDGET_INTENT_BUNDLE, args);

        int widgetPID = WIDGET_PID_BASE + WIDGET_PID_SCALE * widgetId + WIDGET_PID_OFFSET1;
        return PendingIntent.getBroadcast(context, widgetPID, intent, 0);

    }

    public static int getWidgetFixtureId(Context context, int widgetId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getInt(WIDGET_PREFERENCES + WIDGET_BUNDLE_FIXTURE_ID + widgetId, EMPTY_FIXTURE_ID);
    }

    public static void putWidgetFixtureId(Context context, int widgetId, int fixtureId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(WIDGET_PREFERENCES + WIDGET_BUNDLE_FIXTURE_ID + widgetId, fixtureId);
        editor.apply();
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        MatchWidgetService.startWidgetUpdateAction(context);
    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        String action = intent.getAction();
        if (action == null) return;

        if (action.equals(WIDGET_SERVICE_REFRESH_ACTION)) {
            Bundle bundle = intent.getBundleExtra(WIDGET_INTENT_BUNDLE);
            if (bundle == null) return;

            int widgetId = bundle.getInt(WIDGET_BUNDLE_WIDGET_ID, EMPTY_WIDGET_ID);
            int fixtureId = bundle.getInt(WIDGET_BUNDLE_FIXTURE_ID, EMPTY_FIXTURE_ID);

            MatchWidgetService.startFillWidgetAction(context, widgetId, fixtureId);


        }


    }


    private static Bitmap loadBitmapStandard(Context context, String imageURL) {
        if (imageURL == null || imageURL.isEmpty()) return null;

        try {
            Drawable resource = mRequestBuilderCommon
                    .load(imageURL)
                    .submit()
                    .get();

            if (resource == null) return null;

            if (resource instanceof BitmapDrawable) {
                return ((BitmapDrawable) resource).getBitmap();
            }

            int size = context.getResources().getDimensionPixelSize(R.dimen.widget_image_team_size);
            Bitmap bitmap;
            if (resource.getIntrinsicWidth() <= 0 || resource.getIntrinsicHeight() <= 0) {
                bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
            } else {
                bitmap = Bitmap.createBitmap(resource.getIntrinsicWidth(), resource.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            }
            Canvas canvas = new Canvas(bitmap);
//            resource.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            resource.draw(canvas);
            return bitmap;

        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    private static void setupRequestBuilder(Context context) {
        if (mRequestBuilder == null) {
            mRequestBuilder = Glide.with(context)
                    .as(PictureDrawable.class)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.fc_logo_loading)
                            .error(R.drawable.fc_logo))

//                .transition(withCrossFade())
                    .listener(new SvgSoftwareLayerSetter());
        }

        if (mRequestBuilderCommon == null) {
            mRequestBuilderCommon = Glide.with(context)
                    .as(Drawable.class)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.fc_logo_loading)
                            .error(R.drawable.fc_logo))
//                .transition(withCrossFade())
                    .listener(new CommonRequestListener());
        }

    }

    private static class CommonRequestListener implements RequestListener<Drawable> {
        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
            return false;
        }

        @Override
        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
            Bitmap bitmap = Bitmap.createBitmap(resource.getIntrinsicWidth(), resource.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            resource.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            resource.draw(canvas);


            return false;
        }
    }

}

