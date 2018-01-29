package ru.vpcb.contentprovider.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import ru.vpcb.contentprovider.R;
import ru.vpcb.contentprovider.data.FDCompetition;
import ru.vpcb.contentprovider.data.FDFixture;
import ru.vpcb.contentprovider.data.FDPlayer;
import ru.vpcb.contentprovider.data.FDTable;
import ru.vpcb.contentprovider.data.FDTeam;
import ru.vpcb.contentprovider.data.IRetrofitAPI;
import ru.vpcb.contentprovider.utils.FDUtils;
import timber.log.Timber;

import static ru.vpcb.contentprovider.utils.Constants.UPDATE_SERVICE_TAG;
import static ru.vpcb.contentprovider.utils.FootballUtils.getPrefBool;
import static ru.vpcb.contentprovider.utils.FootballUtils.isOnline;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 25-Jan-18
 * Email: vadim.v.voronov@gmail.com
 */
public class UpdateService extends IntentService {

    private boolean isCursorEmpty;
    private boolean isSmartUpdate;

    private Retrofit mRetrofit;
    private IRetrofitAPI mRetrofitAPI;
    private OkHttpClient mOkHttpClient;

    private Map<Integer, FDCompetition> mMapCompetitions;

    private Map<Integer, FDFixture> mMapFixture;
    private Map<Integer, FDTable> mMapTables;
    private Map<Integer, List<FDPlayer>> mMapPlayers;


    private Map<Integer, List<Integer>> mapCpTeams;
    private Map<Integer, FDTeam> mMapTeams;


    public UpdateService() {
        super(UPDATE_SERVICE_TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (intent != null) {
            String action = intent.getAction();
            if (action == getString(R.string.action_update)) {
                onActionUpdate();
            }
        }
    }

    private void onActionUpdate() {
        if (!isOnline(this)) {                                     // no network
            sendBroadcast(new Intent(getString(R.string.broadcast_no_network)));
            return;
        }
        sendBroadcast(new Intent(getString(R.string.broadcast_update_started)));

        try {

//            mapCpTeams = FDUtils.readCompetitionTeams(this);
//            mMapTeams = FDUtils.readTeams(this);
//
//            mMapCompetitions = FDUtils.readCompetitions(this);

// loader imitation
            Map<Integer, List<Integer>> mapTeamKeys = FDUtils.readCompetitionTeams(this);
            Map<Integer, FDTeam> mapTeams = FDUtils.readTeams(this);
            Map<Integer, List<Integer>> mapFixtureKeys = FDUtils.readCompetitionFixtures(this);
            Map<Integer, FDFixture> mapFixtures = FDUtils.readFixtures(this);

// load competition
            mMapCompetitions = new HashMap<>(); // mandatory before getCompetitions()
            boolean requestUpdate = FDUtils.getCompetitions(
                    this, mMapCompetitions, mapTeamKeys, mapTeams,
                    mapFixtureKeys, mapFixtures, false);

// save competition and all
            if (requestUpdate) {
                FDUtils.writeCompetitions(this, mMapCompetitions, false);
            }
//            FDUtils.writeTeams(this, mMapCompetitions, false);
//
//            FDUtils.writeCompetitionTeams(this, mMapCompetitions, false);


        } catch (IOException e) {
// test !!!  catch errors
            Timber.d(getString(R.string.retrofit_response_exception), e.getMessage());
            sendBroadcast(new Intent(getString(R.string.broadcast_update_error)));
            return;
        } catch (NullPointerException | NumberFormatException e) {
            Timber.d(getString(R.string.retrofit_response_empty), e.getMessage());
            sendBroadcast(new Intent(getString(R.string.broadcast_update_error)));
            return;
        } catch (OperationApplicationException | RemoteException e) {
            Timber.d(getString(R.string.update_content_error) + e.getMessage());
            sendBroadcast(new Intent(getString(R.string.broadcast_update_error)));
            return;
        }

        sendBroadcast(new Intent(getString(R.string.broadcast_update_finished)));
    }


}
