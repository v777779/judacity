package ru.vpcb.contentprovider.services;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.vpcb.contentprovider.BuildConfig;
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
import ru.vpcb.contentprovider.dbase.FDContract;
import timber.log.Timber;

import static ru.vpcb.contentprovider.utils.FDUtils.buildItemIdUri;
import static ru.vpcb.contentprovider.utils.Constants.FD_BASE_URI;
import static ru.vpcb.contentprovider.utils.FootballUtils.formatString;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 28-Jan-18
 * Email: vadim.v.voronov@gmail.com
 */

public class FDOther extends AppCompatActivity {

    private Retrofit mRetrofit;
    private IRetrofitAPI mRetrofitAPI;
    private OkHttpClient mOkHttpClient;

    private Cursor cursor;

    private Map<Integer,FDCompetition> mMapCompetitions;
    private Map<Integer,FDTeam> mMapTeams;

    private void main() throws  Exception{
        Cursor cursor = readCompetitions();
        if (cursor == null || cursor.getCount() == 0) {
            mMapCompetitions = getMapCompetitions();
            mMapTeams = getMapTeams(mMapCompetitions);
        }
//            getMapFixtures(mMapCompetitions);
//            mMapTables = getMapTables(mMapCompetitions);
//            mMapPlayers = getMapPlayers(mMapTeams);

// local
//            FDCompetition competition = mMapCompetitions.get(446); // find by id
//            Map<Integer, FDTeam> mapTeams = getMapTeams(competition);
//            mMapPlayers = getMapPlayers(mapTeams);  // for one competition


        if (cursor == null || cursor.getCount() == 0) {
            writeCompetitions();
        }

        cursor = readCompetitions("2017");
        logCursor(cursor);

        cursor = readCompetitions("2018");
        logCursor(cursor);

        cursor = deleteRecords(447, 2018);
        logCursor(cursor);
        cursor = deleteRecords(447, 2017);
        logCursor(cursor);
        cursor = deleteRecords(467, 2017);
        logCursor(cursor);
        cursor = deleteRecords(467, 2018);
        logCursor(cursor);

        cursor = deleteRecords(464);
        logCursor(cursor);
    }
    private ContentProviderResult[] writeCompetitions()
            throws OperationApplicationException, RemoteException {

        ContentProviderResult[] results = null;

        ArrayList<ContentProviderOperation> listOperations = new ArrayList<ContentProviderOperation>();
        Uri uri = FDContract.CpEntry.CONTENT_URI;  // вся таблица
//        DateFormat dateformat = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
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

    public static ContentProviderResult[] writeTeams(Context context, Map<Integer, FDCompetition> map, boolean forceDelete)
            throws OperationApplicationException, RemoteException {

        ArrayList<ContentProviderOperation> listOperations = new ArrayList<ContentProviderOperation>();

        Uri uri = FDContract.TmEntry.CONTENT_URI;
        long refreshTime = Calendar.getInstance().getTime().getTime();

        if (map == null || map.size() == 0) return null;

        if (forceDelete) { // force clear Teams table
            listOperations.add(ContentProviderOperation.newDelete(uri).build());
        }

        for (FDCompetition competition : map.values()) {
            ContentValues values = new ContentValues();
            if (competition == null || competition.getId() <= 0) continue;
            List<FDTeam> teams = competition.getTeams();
            if (teams == null || teams.size() == 0) continue;
            for (FDTeam team : teams) {
                if (team == null || team.getId() <= 0) continue;
                values.put(FDContract.TmEntry.COLUMN_TEAM_ID, team.getId());                            // int
                values.put(FDContract.TmEntry.COLUMN_TEAM_NAME, team.getName());                        // string
                values.put(FDContract.TmEntry.COLUMN_TEAM_CODE, team.getCode());                        // string
                values.put(FDContract.TmEntry.COLUMN_TEAM_SHORT_NAME, team.getShortName());             // string
                values.put(FDContract.TmEntry.COLUMN_TEAM_MARKET_VALUE, team.getSquadMarketValue());    // string
                values.put(FDContract.TmEntry.COLUMN_TEAM_CREST_URI, team.getCrestURL());               // string
                values.put(FDContract.TmEntry.COLUMN_LAST_REFRESH, refreshTime);                        // int from date

                listOperations.add(ContentProviderOperation.newInsert(uri).withValues(values).build());
            }
        }
        return context.getContentResolver().applyBatch(FDContract.CONTENT_AUTHORITY, listOperations);
    }

    public static ContentProviderResult[] writeCompetitions(Context context, Map<Integer, FDCompetition> map, boolean forceDelete)
            throws OperationApplicationException, RemoteException {

        ArrayList<ContentProviderOperation> listOperations = new ArrayList<ContentProviderOperation>();

        Uri uri = FDContract.CpEntry.CONTENT_URI;
        int refreshTime = (int) (TimeUnit.MILLISECONDS.toMinutes(Calendar.getInstance().getTime().getTime()));

        if (map == null || map.size() == 0) return null;

        listOperations.add(ContentProviderOperation.newDelete(uri).build());  // delete all records from Competitions table


        for (FDCompetition competition : map.values()) {
            ContentValues values = new ContentValues();

            if (competition == null || competition.getId() <= 0) continue;
            int lastUpdate = (int) (TimeUnit.MILLISECONDS.toMinutes(competition.getLastUpdated().getTime()));

            values.put(FDContract.CpEntry.COLUMN_COMPETITION_ID, competition.getId());                  // int
            values.put(FDContract.CpEntry.COLUMN_COMPETITION_CAPTION, competition.getCaption());        // string
            values.put(FDContract.CpEntry.COLUMN_COMPETITION_LEAGUE, competition.getLeague());          // string
            values.put(FDContract.CpEntry.COLUMN_COMPETITION_YEAR, competition.getYear());              // string
            values.put(FDContract.CpEntry.COLUMN_CURRENT_MATCHDAY, competition.getCurrentMatchDay());   // int
            values.put(FDContract.CpEntry.COLUMN_NUMBER_MATCHDAYS, competition.getNumberOfMatchDays()); // int
            values.put(FDContract.CpEntry.COLUMN_NUMBER_TEAMS, competition.getNumberOfTeams());         // int
            values.put(FDContract.CpEntry.COLUMN_NUMBER_GAMES, competition.getNumberOfGames());         // int
            values.put(FDContract.CpEntry.COLUMN_LAST_UPDATE, lastUpdate);                              // int from date
            values.put(FDContract.CpEntry.COLUMN_LAST_REFRESH, refreshTime);                            // string from date
            listOperations.add(ContentProviderOperation.newInsert(uri).withValues(values).build());
        }

        return context.getContentResolver().applyBatch(FDContract.CONTENT_AUTHORITY, listOperations);
    }


    private Cursor readCompetitions(String year) {
        Uri uri = FDContract.CpEntry.CONTENT_URI;  // вся таблица
        String[] projection = {
                FDContract.CpEntry.COLUMN_COMPETITION_ID,
                FDContract.CpEntry.COLUMN_COMPETITION_CAPTION,
                FDContract.CpEntry.COLUMN_LAST_UPDATE,
                FDContract.CpEntry.COLUMN_LAST_REFRESH
        };
        String selection = FDContract.CpEntry.COLUMN_COMPETITION_YEAR + "= ?";
        String[] selectionArgs = {year};
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

    private Cursor readCompetitions() {
        Uri uri = FDContract.CpEntry.CONTENT_URI;  // вся таблица
        String sortOrder = FDContract.CpEntry.COLUMN_LAST_UPDATE + " ASC";
        Cursor cursor = getContentResolver().query(
                uri,
                null,
                null,
                null,
                sortOrder
        );
        return cursor;
    }

    private Cursor deleteRecords(int id, int year) {
        Uri uri = FDContract.CpEntry.CONTENT_URI;
        String[] selectionArgs = new String[]{"" + id, "" + year};

//        StringBuilder sb = new StringBuilder();
        String selection = FDContract.CpEntry.COLUMN_COMPETITION_ID + "=?" + " AND " +
                FDContract.CpEntry.COLUMN_COMPETITION_YEAR + "=?";

        getContentResolver().delete(uri, selection, selectionArgs);

        return readCompetitions();
    }


    private Cursor deleteRecords(int id) {
        Uri uri = buildItemIdUri(FDContract.CpEntry.TABLE_NAME, id);
        getContentResolver().delete(uri, null, null);
        return readCompetitions();
    }


    private void logCursor(Cursor cursor) {
        if (cursor == null) return;
        int counter = 1;
        Timber.d("  new session  ");
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Timber.d(counter++ + ". " + cursor.getString(0) + " " +
                    cursor.getString(1) + " " +
                    cursor.getString(2) + " " +
                    cursor.getString(3)

            );
        }
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
