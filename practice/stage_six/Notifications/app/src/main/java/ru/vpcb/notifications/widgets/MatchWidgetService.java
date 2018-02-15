package ru.vpcb.notifications.widgets;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ru.vpcb.notifications.R;
import ru.vpcb.notifications.Utils.TempUtils;
import ru.vpcb.notifications.data.FDCompetition;
import ru.vpcb.notifications.data.FDFixture;
import ru.vpcb.notifications.data.FDFixtures;
import ru.vpcb.notifications.data.FDTeam;
import ru.vpcb.notifications.data.FDTeams;
import ru.vpcb.notifications.data.PostProcessingEnabler;
import ru.vpcb.notifications.reciipe.RecipeItem;
import timber.log.Timber;


import static ru.vpcb.notifications.Utils.Config.WIDGET_INTENT_BUNDLE;
import static ru.vpcb.notifications.Utils.Config.EMPTY_FIXTURE_ID;
import static ru.vpcb.notifications.Utils.Config.EMPTY_LONG_DASH;
import static ru.vpcb.notifications.Utils.Config.EMPTY_WIDGET_ID;
import static ru.vpcb.notifications.Utils.Config.WIDGET_BUNDLE_FIXTURE_ID;
import static ru.vpcb.notifications.Utils.Config.WIDGET_SERVICE_FILL_ACTION;
import static ru.vpcb.notifications.Utils.Config.WIDGET_SERVICE_UPDATE_ACTION;
import static ru.vpcb.notifications.Utils.Config.WIDGET_BUNDLE_WIDGET_ID;
import static ru.vpcb.notifications.widgets.MatchWidgetProvider.getWidgetFixtureId;


public class MatchWidgetService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public MatchWidgetService() {
        super(MatchWidgetService.class.getSimpleName());
    }


    public static void startFillWidgetAction(Context context, int widgetId, int fixtureId) {
        if (widgetId <= 0 || fixtureId <= 0) {
            return;
        }
        Bundle args = new Bundle();
        args.putInt(WIDGET_BUNDLE_WIDGET_ID, widgetId);
        args.putInt(WIDGET_BUNDLE_FIXTURE_ID, fixtureId);
        Intent intent = new Intent(context, MatchWidgetService.class);
        intent.putExtra(WIDGET_INTENT_BUNDLE, args);
        intent.setAction(WIDGET_SERVICE_FILL_ACTION);
        context.startService(intent);
    }

    public static void startWidgetUpdateAction(Context context) {
        Intent intent = new Intent(context, MatchWidgetService.class);
        intent.setAction(WIDGET_SERVICE_UPDATE_ACTION);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (action == null) return;
            if (action.equals(WIDGET_SERVICE_FILL_ACTION)) {
                Bundle bundle = intent.getBundleExtra(WIDGET_INTENT_BUNDLE);
                startFillWidgetAction(bundle);
            }
            if (action.equals(WIDGET_SERVICE_UPDATE_ACTION)) {
                startWidgetUpdateAction();
            }
        }
    }

    private void startWidgetUpdateAction() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        ComponentName componentName = new ComponentName(this, MatchWidgetProvider.class);
        int[] ids = appWidgetManager.getAppWidgetIds(componentName);
        if (ids == null) return;
        for (int widgetId : ids) {
            int fixtureId = getWidgetFixtureId(this, widgetId);

            FDFixture fixture = getFixtureFromDatabase(fixtureId);

            MatchWidgetProvider.updateWidget(this, appWidgetManager, widgetId, fixture);
        }
    }


    public String getIngredientString(List<RecipeItem.Ingredient> list) {
        StringBuilder sb = new StringBuilder();
        int count = 1;
        for (RecipeItem.Ingredient ingredient : list) {
            String s = ingredient.toString().substring(0, 1).toUpperCase() + ingredient.toString().substring(1);
            sb.append(count);
            sb.append(". ");
            sb.append(s);
            sb.append("\n");
            count++;
            if (count == 4) break;
        }
        return sb.toString();
    }

    private static Comparator<FDFixture> cFx = new Comparator<FDFixture>() {
        @Override
        public int compare(FDFixture o1, FDFixture o2) {
            int cmp;
            if (o1 != null && o2 != null)
                return Integer.compare(o1.getId(), o2.getId());

            if (o1 == null && o2 != null) return -1;
            if (o1 != null && o2 == null) return 1;

            return 0;

        }
    };


    // test!!!
    private static Comparator<FDTeam> cFt = new Comparator<FDTeam>() {
        @Override
        public int compare(FDTeam o1, FDTeam o2) {
            if (o1 == null || o2 == null) throw new IllegalArgumentException();

            return Integer.compare(o1.getId(), o2.getId());
        }
    };

    // test!!!
    private FDTeam getTeamFromDatabase(int id) {
        if (id <= 0) return null;
        String json = TempUtils.readFileAssets(this, "teams.json");
        try {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapterFactory(new PostProcessingEnabler())
                    .create();
            FDTeams teams = gson.fromJson(json, FDTeams.class);
            List<FDTeam> list = teams.getTeams();
// test!!!

            FDTeam key = new FDTeam(id, null, null,null,
                    null, null, 0);
            Collections.sort(list, cFt);
            int index = Collections.binarySearch(list, key, cFt);
            if (index < 0) return null;

            return list.get(index);

        } catch (NullPointerException e) {
            Timber.d(getString(R.string.widget_read_fixture_exception, e.getMessage()));
        }
        return null;
    }


    // test!!!
    private static Comparator<FDCompetition> cFc = new Comparator<FDCompetition>() {
        @Override
        public int compare(FDCompetition o1, FDCompetition o2) {
            if (o1 == null || o2 == null) throw new IllegalArgumentException();

            return Integer.compare(o1.getId(), o2.getId());
        }
    };

    // test!!!
    private FDCompetition getCompetitionFromDatabase(FDFixture fixture) {
        if (fixture == null || fixture.getCompetitionId() <= 0) return null;

        String json = TempUtils.readFileAssets(this, "competitions.json");
        Type listType = new TypeToken<List<FDCompetition>>() {
        }.getType();
        List<FDCompetition> list = new Gson().fromJson(json, listType);
        if(list == null) return null;

        Collections.sort(list, cFc);
        FDCompetition key = new FDCompetition(fixture.getCompetitionId(), null, null, null,
                0, 0, 0, 0,
                null, null);
        int index = Collections.binarySearch(list, key, cFc);

        if (index <= 1) return null;
        return list.get(index);
    }


    // test!!!
    private FDFixture getFixtureFromDatabase(int id) {
        if (id <= 0) return null;
        String json = TempUtils.readFileAssets(this, "fixtures.json");
        try {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapterFactory(new PostProcessingEnabler())
                    .create();
            FDFixtures fixtures = gson.fromJson(json, FDFixtures.class);
            List<FDFixture> list = fixtures.getFixtures();
            FDFixture key = new FDFixture(id, null, null, 0,
                    null, null, 0,
                    0, 0, 0, 0, null);
            Collections.sort(list, cFx);
            int index = Collections.binarySearch(list, key, cFx);
            if (index < 0) return null;

            FDFixture fixture = list.get(index);

            if (fixture == null) return null;
            FDCompetition competition = getCompetitionFromDatabase(fixture);
            if (competition == null) {
                fixture.setCompetitionName(EMPTY_LONG_DASH);
            } else {
                fixture.setCompetitionName(competition.getCaption());
            }

            FDTeam  team = getTeamFromDatabase(fixture.getHomeTeamId());
            if (team == null) {
                fixture.setHomeTeamCrestURL("");
            } else {
                fixture.setHomeTeamCrestURL(team.getCrestURL());
            }
             team = getTeamFromDatabase(fixture.getAwayTeamId());
            if (team == null) {
                fixture.setAwayTeamCrestURL("");
            } else {
                fixture.setAwayTeamCrestURL(team.getCrestURL());
            }

            return fixture;

        } catch (NullPointerException e) {
            Timber.d(getString(R.string.widget_read_fixture_exception, e.getMessage()));
        }
        return null;
    }


    private void startFillWidgetAction(Bundle bundle) {
        if (bundle == null) return;
        try {
            int widgetId = bundle.getInt(WIDGET_BUNDLE_WIDGET_ID, EMPTY_WIDGET_ID);
            int fixtureId = bundle.getInt(WIDGET_BUNDLE_FIXTURE_ID, EMPTY_FIXTURE_ID);

            FDFixture fixture = getFixtureFromDatabase(fixtureId);

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            MatchWidgetProvider.fillWidget(this, appWidgetManager, widgetId, fixture);

        } catch (NullPointerException e) {
            Timber.d(getString(R.string.widget_read_fixture_exception, e.getMessage()));
        }
    }

}
