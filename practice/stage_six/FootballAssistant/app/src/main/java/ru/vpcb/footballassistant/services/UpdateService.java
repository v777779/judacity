package ru.vpcb.footballassistant.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import ru.vpcb.footballassistant.R;
import ru.vpcb.footballassistant.data.FDCompetition;
import ru.vpcb.footballassistant.data.FDFixture;
import ru.vpcb.footballassistant.data.FDPlayer;
import ru.vpcb.footballassistant.data.FDTable;
import ru.vpcb.footballassistant.data.FDTeam;
import ru.vpcb.footballassistant.data.IFDRetrofitAPI;
import ru.vpcb.footballassistant.utils.FDUtils;
import ru.vpcb.footballassistant.utils.FootballUtils;
import timber.log.Timber;

import static ru.vpcb.footballassistant.utils.Config.LOAD_DB_DELAY;
import static ru.vpcb.footballassistant.utils.Config.LOAD_DB_TIMEOUT;
import static ru.vpcb.footballassistant.utils.Config.MAIN_ACTIVITY_PROGRESS;
import static ru.vpcb.footballassistant.utils.Config.UPDATE_SERVICE_PROGRESS;
import static ru.vpcb.footballassistant.utils.Config.UPDATE_SERVICE_TAG;
import static ru.vpcb.footballassistant.utils.FootballUtils.isOnline;
import static ru.vpcb.footballassistant.utils.FootballUtils.rnd;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 25-Jan-18
 * Email: vadim.v.voronov@gmail.com
 */
public class UpdateService extends IntentService {


    public UpdateService() {
        super(UPDATE_SERVICE_TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (intent != null) {
            String action = intent.getAction();
            if (action.equals(getString(R.string.action_update))) {
                onActionUpdate();
            }
        }
    }

    private void onActionUpdate() {
        try {
            Map<Integer, FDCompetition> map = new HashMap<>();
            Map<Integer, List<Integer>> mapTeamKeys = new HashMap<>();
            Map<Integer, FDTeam> mapTeams = new HashMap<>();
            Map<Integer, List<Integer>> mapFixtureKeys = new HashMap<>();
            Map<Integer, FDFixture> mapFixtures = new HashMap<>();
            FDUtils.readDatabase(this, map, mapTeamKeys, mapTeams,
                    mapFixtureKeys, mapFixtures);

            if (checkEmpty(map, mapTeamKeys, mapTeams,
                    mapFixtureKeys, mapFixtures) && FDUtils.isFootballDataRefreshed(this)) {
                sendBroadcast(new Intent(getString(R.string.broadcast_data_update_finished)));
                return;
            }

//            map = FDUtils.readCompetitions(this);  // competitions only
//            if (map != null  && !map.isEmpty()) {
//                sendBroadcast(new Intent(getString(R.string.broadcast_data_update_finished)));
//                return;
//            }


            if (!isOnline(this)) {                                     // no network
                sendBroadcast(new Intent(getString(R.string.broadcast_data_no_network)));
                return;
            }
            sendBroadcast(new Intent(getString(R.string.broadcast_data_update_started)));
            FDUtils.sendProgress(this, 0);

            boolean isUpdated = FDUtils.loadDatabase(this, map, mapTeamKeys, mapTeams,
                    mapFixtureKeys, mapFixtures);
            FDUtils.sendProgress(this, (int) (UPDATE_SERVICE_PROGRESS * 0.8));
            if (isUpdated) {
                FDUtils.writeDatabase(this, map, false); //  true delete false update
            }
            FDUtils.setRefreshTime(this);
            sendBroadcast(new Intent(getString(R.string.broadcast_data_update_finished)));

        } catch (IOException e) {
            Timber.d(getString(R.string.retrofit_response_exception), e.getMessage());
            sendBroadcast(new Intent(getString(R.string.broadcast_data_update_error)));

        } catch (NullPointerException | NumberFormatException e) {
            Timber.d(getString(R.string.retrofit_response_empty), e.getMessage());
            sendBroadcast(new Intent(getString(R.string.broadcast_data_update_error)));

        } catch (OperationApplicationException | RemoteException e) {
            Timber.d(getString(R.string.update_content_error,e.getMessage()));
            sendBroadcast(new Intent(getString(R.string.broadcast_data_update_error)));

        }

    }


    private void testWriteFixtures(Map<Integer, FDCompetition> map,
                                   Map<Integer, FDFixture> mapFixture) throws OperationApplicationException, RemoteException {

        List<FDFixture> list = new ArrayList<>(mapFixture.values());
        FDFixture fixture = list.get(rnd.nextInt(list.size()));

        int fixtureId = fixture.getId();
        FDFixture fixture2 = FDUtils.readFixture(this, fixtureId);

        fixture2.setFavorite(true);
        FDUtils.updateFixtureProjection(this, fixture2, false); // update

        FDFixture fixture3 = FDUtils.readFixture(this, fixtureId);
        fixture3.setNotificationId(12200);
        fixture3.setNotified(true);
        FDUtils.updateFixtureProjection(this, fixture3, false); // update

        FDFixture fixture4 = FDUtils.readFixture(this, fixtureId);

//        FDUtils.writeFixtures(this,map,false);

        FDFixture fixture5 = FDUtils.readFixture(this, fixtureId);

        fixtureId = 714092;
        FDFixture fixture6 = new FDFixture(fixtureId, fixture5.getCompetitionId(),
                fixture5.getHomeTeamId(), fixture5.getAwayTeamId(), fixture5.getDate(),
                fixture5.getStatus(), fixture5.getMatchDay(), fixture5.getHomeTeamName(),
                fixture5.getAwayTeamName(), fixture5.getGoalsHomeTeam(), fixture5.getGoalsAwayTeam(),
                fixture5.getHomeWin(), fixture5.getDraw(), fixture5.getAwayWin(),
                fixture5.isFavorite(), fixture5.isNotified(), fixture5.getNotificationId());



        FDUtils.updateFixtureProjection(this, fixture6, false); // update
        FDFixture fixture7 = FDUtils.readFixture(this, fixtureId);
        fixtureId = 728092;


    }

    private boolean checkEmpty(Map<Integer, FDCompetition> map,
                               Map<Integer, List<Integer>> mapTeamKeys,
                               Map<Integer, FDTeam> mapTeams,
                               Map<Integer, List<Integer>> mapFixtureKeys,
                               Map<Integer, FDFixture> mapFixtures) {

        if (map.isEmpty() || mapTeamKeys.isEmpty() || mapTeams.isEmpty() ||
                mapFixtureKeys.isEmpty() || mapFixtures.isEmpty()) {
            return false;
        }
        return true;

    }

}
