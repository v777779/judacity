package ru.vpcb.footballassistant.services;

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

import static ru.vpcb.footballassistant.utils.Config.UPDATE_SERVICE_PROGRESS;
import static ru.vpcb.footballassistant.utils.Config.UPDATE_SERVICE_TAG;
import static ru.vpcb.footballassistant.utils.FootballUtils.isOnline;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 25-Jan-18
 * Email: vadim.v.voronov@gmail.com
 */
public class NewsService extends IntentService {

    private boolean isCursorEmpty;
    private boolean isSmartUpdate;

    private Retrofit mRetrofit;
    private IFDRetrofitAPI mRetrofitAPI;
    private OkHttpClient mOkHttpClient;

    private Map<Integer, FDCompetition> mMapCompetitions;

    private Map<Integer, FDFixture> mMapFixture;
    private Map<Integer, FDTable> mMapTables;
    private Map<Integer, List<FDPlayer>> mMapPlayers;


    private Map<Integer, List<Integer>> mapCpTeams;
    private Map<Integer, FDTeam> mMapTeams;


    public NewsService() {
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
        if (FDUtils.isNewsDataRefreshed(this)) {
            sendBroadcast(new Intent(getString(R.string.broadcast_data_update_finished)));
            return;  // data updated
        }

        if (!isOnline(this)) {                                     // no network
            sendBroadcast(new Intent(getString(R.string.broadcast_data_no_network)));
            return;
        }
        sendBroadcast(new Intent(getString(R.string.broadcast_data_update_started)));

//        try {
// loader body




//        } catch (IOException e) {
//// test !!!  catch errors
//            Timber.d(getString(R.string.retrofit_response_exception), e.getMessage());
//            sendBroadcast(new Intent(getString(R.string.broadcast_data_update_error)));
//            return;
//        } catch (NullPointerException | NumberFormatException e) {
//            Timber.d(getString(R.string.retrofit_response_empty), e.getMessage());
//            sendBroadcast(new Intent(getString(R.string.broadcast_data_update_error)));
//            return;
//        } catch (OperationApplicationException | RemoteException e) {
//            Timber.d(getString(R.string.update_content_error) + e.getMessage());
//            sendBroadcast(new Intent(getString(R.string.broadcast_data_update_error)));
//            return;
//        }
//60%
        sendBroadcast(new Intent(getString(R.string.broadcast_data_update_finished)));
    }


}
