package ru.vpcb.contentprovider.services;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import ru.vpcb.contentprovider.data.FDPlayer;
import ru.vpcb.contentprovider.data.FDPlayers;
import ru.vpcb.contentprovider.data.FDStanding;
import ru.vpcb.contentprovider.data.FDStandingGroup;
import ru.vpcb.contentprovider.data.FDTable;
import ru.vpcb.contentprovider.data.FDTeam;
import ru.vpcb.contentprovider.data.FDTeams;
import ru.vpcb.contentprovider.data.IRetrofitAPI;
import ru.vpcb.contentprovider.fdbase.FDContract;
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

    private static Map<Integer, FDCompetition> mMapCompetitions;
    private static Map<Integer, FDTeam> mMapTeams;
    private Map<Integer, FDFixture> mMapFixture;
    private Map<Integer, FDTable> mMapTables;
    private Map<Integer, List<FDPlayer>> mMapPlayers;


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
        readCompetitions();
        try {
            if (mMapCompetitions == null) {
                mMapCompetitions = getMapCompetitions();
//                mMapTeams = getMapTeams(mMapCompetitions);
            }
//            getMapFixtures(mMapCompetitions);
//            mMapTables = getMapTables(mMapCompetitions);
//            mMapPlayers = getMapPlayers(mMapTeams);

// local
//            FDCompetition competition = mMapCompetitions.get(446); // find by id
//            Map<Integer, FDTeam> mapTeams = getMapTeams(competition);
//            mMapPlayers = getMapPlayers(mapTeams);  // for one competition


            writeCompetitions();

            readCompetitions();

// test !!!  catch errors
        } catch (IOException e) {
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


    private ContentProviderResult[] writeCompetitions()
            throws OperationApplicationException, RemoteException {

        ContentProviderResult[] results = null;

        ArrayList<ContentProviderOperation> listOperations = new ArrayList<ContentProviderOperation>();
        Uri uri = FDContract.CpEntry.CONTENT_URI;  // вся таблица
        DateFormat dateformat = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.SHORT);
//        String currentTime = dateformat.format(Calendar.getInstance().getTime());
        long refreshTime = Calendar.getInstance().getTime().getTime();


        if (mMapCompetitions == null || mMapCompetitions.size() == 0) return results;

        listOperations.add(ContentProviderOperation.newDelete(uri).build());  // delete for batch operation

        for (Integer key : mMapCompetitions.keySet()) {
            ContentValues values = new ContentValues();
            FDCompetition competition = mMapCompetitions.get(key);
            if (competition == null || competition.getId() <= 0) continue;
            long lastUpdate = competition.getLastUpdated().getTime();


            values.put(FDContract.CpEntry.COLUMN_COMPETITION_ID, competition.getId());                  // int
            values.put(FDContract.CpEntry.COLUMN_COMPETITION_CAPTION, competition.getCaption());        // string
            values.put(FDContract.CpEntry.COLUMN_COMPETITION_LEAGUE, competition.getLeague());          // string
            values.put(FDContract.CpEntry.COLUMN_COMPETITION_YEAR, competition.getYear());              // string
            values.put(FDContract.CpEntry.COLUMN_CURRENT_MATCHDAY, competition.getCurrentMatchDay());   // int
            values.put(FDContract.CpEntry.COLUMN_NUMBER_MATCHDAYS, competition.getNumberOfMatchDays()); // int
            values.put(FDContract.CpEntry.COLUMN_NUMBER_TEAMS, competition.getNumberOfTeams());         // int
            values.put(FDContract.CpEntry.COLUMN_NUMBER_GAMES, competition.getNumberOfGames());         // int
            values.put(FDContract.CpEntry.COLUMN_LAST_UPDATE, lastUpdate);                              // string from date
            values.put(FDContract.CpEntry.COLUMN_LAST_REFRESH, refreshTime);                            // string from date

            listOperations.add(ContentProviderOperation.newInsert(uri).withValues(values).build());
        }


        results = getContentResolver().applyBatch(FDContract.CONTENT_AUTHORITY, listOperations);

        return results;
    }


    private Cursor readCompetitions() {
        Uri uri = FDContract.CpEntry.CONTENT_URI;  // вся таблица
        String[] projection = {
                FDContract.CpEntry.COLUMN_COMPETITION_ID,
                FDContract.CpEntry.COLUMN_COMPETITION_CAPTION,
                FDContract.CpEntry.COLUMN_LAST_UPDATE,
                FDContract.CpEntry.COLUMN_LAST_REFRESH
        };
        String selection = FDContract.CpEntry.COLUMN_COMPETITION_YEAR + "= ?";
        String[] selectionArgs = {
                "2017"
        };
        String sortOrder = FDContract.CpEntry.COLUMN_LAST_UPDATE + " ASC";


        Cursor cursor = getContentResolver().query(
                uri,
                null,
                selection,
                selectionArgs,
                sortOrder
        );

        return cursor;

    }

    private Map<Integer, FDCompetition> getMapCompetitions() throws NullPointerException, IOException {
        Map<Integer, FDCompetition> map = new HashMap<>();
        List<FDCompetition> list = loadCompetitions(getLastYear());
        if (list != null && !list.isEmpty()) {
            for (FDCompetition competition : list) {
                if (competition == null || competition.getId() <= 0) {
                    continue;
                }
                map.put(competition.getId(), competition);
            }

        }
        list = loadCompetitions(getCurrentYear());
        if (list != null && !list.isEmpty()) {
            for (FDCompetition competition : list) {
                if (competition == null || competition.getId() <= 0) {
                    continue;
                }
                map.put(competition.getId(), competition);
            }
        }
        return map;
    }

    private List<FDTeam> getListTeams(FDCompetition competition) throws NumberFormatException, NullPointerException, IOException {
        String id = String.format("%d", competition.getId());
//        if(competition.getTeams()!= null) return competition.getTeams();  // skip load

        FDTeams teams = loadTeams(id);
        if (teams == null) return null;
        List<FDTeam> list = new ArrayList<>();
        for (FDTeam team : teams.getTeams()) {
            try {
                team.setId();
            } catch (NullPointerException | NumberFormatException e) {
                continue;
            }
            list.add(team);
        }
        return list;
    }


    private Map<Integer, FDTeam> getMapTeams(Map<Integer, FDCompetition> competitions)
            throws NumberFormatException, NullPointerException, IOException {
        Map<Integer, FDTeam> mapTeams = new HashMap<>();
        for (FDCompetition competition : competitions.values()) {
            List<FDTeam> teams = getListTeams(competition);
            if (teams == null || teams.isEmpty()) continue;
            competition.setTeams(teams);        // attach teams

            for (FDTeam team : teams) {
                mapTeams.put(team.getId(), team);
            }


        }
        return mapTeams;
    }

    private Map<Integer, FDTeam> getMapTeams(FDCompetition competition)
            throws NumberFormatException, NullPointerException, IOException {
        Map<Integer, FDTeam> map = new HashMap<>();
        if (competition == null) {
            return null;
        }
        List<FDTeam> teams = competition.getTeams();
        if (teams == null || teams.isEmpty()) {
            return null;
        }

        for (FDTeam team : teams) {
            map.put(team.getId(), team);
        }

        return map;
    }


    private List<FDFixture> getListFixtures(FDCompetition competition)
            throws NumberFormatException, NullPointerException, IOException {
//        if (competition.getFixtures() != null) return competition.getFixtures();
        String id = formatString(competition.getId());

        FDFixtures fixtures = loadFixtures(id);
        if (fixtures == null) return null;

        List<FDFixture> list = new ArrayList<>();
        for (FDFixture fixture : fixtures.getFixtures()) {
            try {
                fixture.setId();
            } catch (NullPointerException | NumberFormatException e) {
                continue;
            }
            list.add(fixture);
        }
        competition.setFixtures(list);  // set fixtures
        return list;
    }

    private Map<Integer, FDFixture> getMapFixtures(Map<Integer, FDCompetition> competitions)
            throws NumberFormatException, NullPointerException, IOException {
        Map<Integer, FDFixture> mapFixtures = new HashMap<>();
        for (FDCompetition competition : competitions.values()) {

            List<FDFixture> fixtures = getListFixtures(competition);
            if (fixtures == null || fixtures.isEmpty()) continue;

            for (FDFixture fixture : fixtures) {
                if (fixture == null || fixture.getId() <= 0) continue;
                mapFixtures.put(fixture.getId(), fixture);
            }
        }

        return mapFixtures;
    }


    private void checkList(FDTable table) {
        if (table == null) return;

        List<FDStanding> standingList = table.getStanding();
        if (standingList == null) return;

        List<FDStanding> list = new ArrayList<>();

        for (FDStanding standing : standingList) {
            try {
                standing.setId();
            } catch (NullPointerException | NumberFormatException e) {
                continue;
            }
            list.add(standing);
        }

        table.setStanding(list);
    }


    private List<FDStanding> getCheckedGroupList(List<FDStanding> standingList) {
        List<FDStanding> list = new ArrayList<>();
        if (standingList == null) return null;

        for (FDStanding standing : standingList) {
            if (standing == null || standing.getId() <= 0) continue;
            list.add(standing);

        }
        return list;
    }

    private void checkGroup(FDTable table) {
        List<FDStanding> list;

        if (table == null) return;

        FDStandingGroup standings = table.getStandings();

        if (standings == null) return;

        list = getCheckedGroupList(standings.getGroupA());  // check all id groupA
        standings.setGroupA(list);

        list = getCheckedGroupList(standings.getGroupB());  // check all id groupB
        standings.setGroupB(list);

        list = getCheckedGroupList(standings.getGroupC());  // check all id groupC
        standings.setGroupC(list);

        list = getCheckedGroupList(standings.getGroupD());  // check all id groupD
        standings.setGroupD(list);

        list = getCheckedGroupList(standings.getGroupE());  // check all id groupE
        standings.setGroupE(list);

        list = getCheckedGroupList(standings.getGroupF());  // check all id groupF
        standings.setGroupF(list);

        list = getCheckedGroupList(standings.getGroupG());  // check all id groupG
        standings.setGroupG(list);

        list = getCheckedGroupList(standings.getGroupH());  // check all id groupH
        standings.setGroupH(list);

        table.setChampionship(true);
    }


    private Map<Integer, FDTable> getMapTables(Map<Integer, FDCompetition> competitions) throws
            NullPointerException, IOException {
        Map<Integer, FDTable> map = new HashMap<>();
        for (FDCompetition competition : competitions.values()) {
            if (competition == null || competition.getId() <= 0) continue;
            String id = formatString(competition.getId());
            FDTable table = loadTable(id);
            try {
                table.setId(competition.getId());
                checkList(table);                   // check id for normal table
                checkGroup(table);
            } catch (NullPointerException | NumberFormatException e) {
                continue;
            }
            map.put(table.getId(), table);
        }
        return map;
    }


    private Map<Integer, List<FDPlayer>> getMapPlayers(Map<Integer, FDTeam> teams)
            throws NumberFormatException, NullPointerException, IOException {
        Map<Integer, List<FDPlayer>> map = new HashMap<>();
        for (FDTeam team : teams.values()) {
            List<FDPlayer> players = getTeamPlayers(team);
            if (players == null) continue;
            map.put(team.getId(), players);
        }
        return map;
    }


    private List<FDPlayer> getTeamPlayers(FDTeam team)
            throws NumberFormatException, NullPointerException, IOException {
        if (team == null || team.getId() <= 0) return null;
//        if(team.getPlayers()!= null )return team.getPlayers();

        String id = formatString(team.getId());
        FDPlayers fdPlayers = loadPlayers(id);
        if (fdPlayers == null) return null;
        List<FDPlayer> players = fdPlayers.getPlayers();
        team.setPlayers(players);
        return players;
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
    private int counter = 0;

    private List<FDCompetition> loadCompetitions(String season)
            throws NullPointerException, IOException {
        setupClient();
        setupRetrofit();
        return mRetrofitAPI.getCompetitions(season).execute().body();
    }

    private FDTeams loadTeams(String competition)
            throws NullPointerException, IOException {
        setupClient();
        setupRetrofit();
        return mRetrofitAPI.getTeams(competition).execute().body();
    }

    private FDFixtures loadFixtures(String competition)
            throws NullPointerException, IOException {
        setupClient();
        setupRetrofit();
        return mRetrofitAPI.getFixtures(competition).execute().body();
    }

    private FDTable loadTable(String competition)
            throws NullPointerException, IOException {
        setupClient();
        setupRetrofit();
        return mRetrofitAPI.getTable(competition).execute().body();
    }

    private FDTable mFDTable;

//    private FDTable loadTable2(String competition)
//            throws NullPointerException, IOException {
//        setupClient();
//        setupRetrofit();
//
//        mFDTable = null;
//        mRetrofitAPI.getTable(competition).enqueue(new Callback<FDTable>() {
//            @Override
//            public void onResponse(@Nullable Call<FDTable> call, @Nullable Response<FDTable> response) {
//                if (response == null || response.body() == null) {
//                    return;
//                }
//
//                mFDTable = response.body();
//            }
//
//            @Override
//            public void onFailure(@Nullable Call<FDTable> call, @NonNull Throwable t) {
//                Timber.d(t.getMessage());
//            }
//        });
//
//        return mFDTable;
//    }


    private FDPlayers loadPlayers(String team)
            throws NullPointerException, IOException {
        setupClient();
        setupRetrofit();
        return mRetrofitAPI.getTeamPlayers(team).execute().body();
    }
}
