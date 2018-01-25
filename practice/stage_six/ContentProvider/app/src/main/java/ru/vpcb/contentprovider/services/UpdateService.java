package ru.vpcb.contentprovider.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.vpcb.contentprovider.BuildConfig;
import ru.vpcb.contentprovider.R;
import ru.vpcb.contentprovider.data.FDCompetition;
import ru.vpcb.contentprovider.data.FDFixture;
import ru.vpcb.contentprovider.data.FDFixtures;
import ru.vpcb.contentprovider.data.FDTeam;
import ru.vpcb.contentprovider.data.FDTeams;
import ru.vpcb.contentprovider.data.IRetrofitAPI;
import timber.log.Timber;

import static ru.vpcb.contentprovider.utils.Constants.FD_BASE_URI;
import static ru.vpcb.contentprovider.utils.Constants.UPDATE_SERVICE_TAG;
import static ru.vpcb.contentprovider.utils.FootballUtils.formatString;
import static ru.vpcb.contentprovider.utils.FootballUtils.getPrefBool;
import static ru.vpcb.contentprovider.utils.FootballUtils.isCursorEmpty;
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

    private List<FDCompetition> competitions;


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
        isCursorEmpty = isCursorEmpty(this);

        if (!isOnline(this)) {                                     // no network
            sendBroadcast(new Intent(getString(R.string.broadcast_no_network))
                    .putExtra(getString(R.string.extra_empty_cursor), isCursorEmpty));
            return;
        }

        sendBroadcast(new Intent(getString(R.string.broadcast_update_started)));

// load smart update mode
        isSmartUpdate = getPrefBool(this, R.string.pref_smart_update_key,
                R.bool.pref_smart_update_default);

// load data into local database
        try {
            competitions = getCompetitions();
// test!!!
            getMapTeams(competitions);
            getMapFixtures(competitions);


// test !!!  catch errors
        } catch (IOException e) {
            Timber.d(getString(R.string.retrofit_response_exception), e.getMessage());
            sendBroadcast(new Intent(getString(R.string.broadcast_update_error)));
            return;
        } catch (NullPointerException | NumberFormatException e) {
            Timber.d(getString(R.string.retrofit_response_empty), e.getMessage());
            sendBroadcast(new Intent(getString(R.string.broadcast_update_error)));
            return;
        }


        sendBroadcast(new Intent(getString(R.string.broadcast_update_finished)));
    }


    private List<FDCompetition> getCompetitions() throws NullPointerException, IOException {
        List<FDCompetition> competitions = new ArrayList<>();
        List<FDCompetition> list = loadCompetitions(getLastYear());
        if (list != null && !list.isEmpty()) {
            competitions.addAll(list);

        }
        list = loadCompetitions(getCurrentYear());
        if (list != null && !list.isEmpty()) {
            competitions.addAll(list);
        }

        return competitions;
    }

    private List<FDTeam> getTeams(FDCompetition competition) throws NumberFormatException, NullPointerException, IOException {
        String id = String.format("%d", competition.getId());
        FDTeams teams = loadTeams(id);
        if (teams == null) return null;

        List<FDTeam> list = new ArrayList<>();

        for (FDTeam team : teams.getTeams()) {
            if (team == null) continue;
            team.setId();
            list.add(team);
        }
        return list;
    }


    private Map<Integer, FDTeam> getMapTeams(List<FDCompetition> competitions)
            throws NumberFormatException, NullPointerException, IOException {
        Map<Integer, FDTeam> mapTeams = new HashMap<>();
        for (FDCompetition competition : competitions) {

            List<FDTeam> teams = getTeams(competition);
            if (teams == null || teams.isEmpty()) continue;

            for (FDTeam team : teams) {
                mapTeams.put(team.getId(), team);
            }
        }
        return mapTeams;
    }


    private List<FDFixture> getFixtures(FDCompetition competition)
            throws NumberFormatException, NullPointerException, IOException {

        String id = formatString(competition.getId());

        FDFixtures fixtures = loadFixtures(id);
        if (fixtures == null) return null;

        List<FDFixture> list = new ArrayList<>();
        for (FDFixture fixture : fixtures.getFixtures()) {
            if (fixture == null) continue;
            fixture.setId();
            list.add(fixture);
        }
        return list;
    }

    private Map<Integer, FDFixture> getMapFixtures(List<FDCompetition> competitions)
            throws NumberFormatException, NullPointerException, IOException {
        Map<Integer, FDFixture> mapFixtures = new HashMap<>();
        for (FDCompetition competition : competitions) {

            List<FDFixture> fixtures = getFixtures(competition);
            if (fixtures == null || fixtures.isEmpty()) continue;

            for (FDFixture fixture : fixtures) {
                mapFixtures.put(fixture.getId(), fixture);
            }
        }

        return mapFixtures;
    }
    // test!!!
// placeholder

    private void showProgress() {

    }

    // test!!!
// placeholder
    private void showResult() {

    }

    private String getCurrentYear() {
        Calendar calendar = Calendar.getInstance();
        return String.format("%4d", calendar.get(Calendar.YEAR));
    }

    private String getLastYear() {
        Calendar calendar = Calendar.getInstance();
        return String.format("%4d", calendar.get(Calendar.YEAR) - 1);
    }

    private void setupClient() {
        mOkHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request request = chain.request().newBuilder()
                                .addHeader("X-Auth-Token", BuildConfig.FD_API_KEY)
                                .build();
                        return chain.proceed(request);
                    }
                }).build();
    }

    private void setupRetrofit() {
        mRetrofit = new Retrofit.Builder()
                .client(mOkHttpClient)
                .baseUrl(FD_BASE_URI)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mRetrofitAPI = mRetrofit.create(IRetrofitAPI.class);
    }

// test!!!
    private int counter= 0;

    private List<FDCompetition> loadCompetitions(String season)
            throws NullPointerException, IOException {
        setupClient();
        setupRetrofit();
// test!!!
        Timber.d("retrofit: "+counter++);
        return mRetrofitAPI.getCompetitions(season).execute().body();
    }

    private FDTeams loadTeams(String competition)
            throws NullPointerException, IOException {
        setupClient();
        setupRetrofit();
// test!!!
        Timber.d("retrofit: "+counter++);
        return mRetrofitAPI.getTeams(competition).execute().body();
    }

    private FDFixtures loadFixtures(String competition)
            throws NullPointerException, IOException {
        setupClient();
        setupRetrofit();
// test!!!
        Timber.d("retrofit: "+counter++);
        return mRetrofitAPI.getFixtures(competition).execute().body();
    }
}
