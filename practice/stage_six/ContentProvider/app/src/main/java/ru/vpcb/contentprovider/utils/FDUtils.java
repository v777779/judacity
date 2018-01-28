package ru.vpcb.contentprovider.utils;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import ru.vpcb.contentprovider.R;
import ru.vpcb.contentprovider.data.FDCompetition;
import ru.vpcb.contentprovider.data.FDFixtures;
import ru.vpcb.contentprovider.data.FDPlayers;
import ru.vpcb.contentprovider.data.FDTable;
import ru.vpcb.contentprovider.data.FDTeam;
import ru.vpcb.contentprovider.data.FDTeams;
import ru.vpcb.contentprovider.data.IRetrofitAPI;
import ru.vpcb.contentprovider.dbase.FDContract;
import ru.vpcb.contentprovider.dbase.FDDbHelper;
import timber.log.Timber;

import static ru.vpcb.contentprovider.utils.Constants.FD_BASE_URI;
import static ru.vpcb.contentprovider.utils.FootballUtils.formatString;
import static ru.vpcb.contentprovider.utils.FootballUtils.getPrefBool;
import static ru.vpcb.contentprovider.utils.FootballUtils.getPrefInt;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 28-Jan-18
 * Email: vadim.v.voronov@gmail.com
 */

public class FDUtils {

    public static ContentProviderResult[] writeCompetitions(Context context, Map<Integer, FDCompetition> map)
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


    public static ContentProviderResult[] writeTeams(Context context, Map<Integer, FDCompetition> map)
            throws OperationApplicationException, RemoteException {

        ArrayList<ContentProviderOperation> listOperations = new ArrayList<ContentProviderOperation>();

        long refreshTime = Calendar.getInstance().getTime().getTime();

        if (map == null || map.size() == 0) return null;

        for (FDCompetition competition : map.values()) {
            ContentValues values = new ContentValues();

            Uri uri = FDContract.TmEntry.CONTENT_URI;
            listOperations.add(ContentProviderOperation.newDelete(uri).build());  // delete all records from Teams table

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
                values.put(FDContract.TmEntry.COLUMN_REFRESH_DATE, refreshTime);                        // int from date
                listOperations.add(ContentProviderOperation.newInsert(uri).withValues(values).build());
            }
        }
        return context.getContentResolver().applyBatch(FDContract.CONTENT_AUTHORITY, listOperations);
    }


    public static Map<Integer, FDCompetition> readCompetitions(Context context, Map<Integer, FDCompetition> map) {
        Uri uri = FDContract.CpEntry.CONTENT_URI;                               // вся таблица
//        String sortOrder = FDContract.CpEntry.COLUMN_LAST_UPDATE + " ASC";
        String sortOrder = FDContract.CpEntry.COLUMN_COMPETITION_ID + " ASC";   // sort by id

        Cursor cursor = context.getContentResolver().query(
                uri,
                null,
                null,
                null,
                sortOrder
        );

        if (cursor == null || cursor.getCount() == 0) return null;

        if(map == null)  map = new HashMap<>();

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

            int id = cursor.getInt(FDDbHelper.ICpEntry.COLUMN_COMPETITION_ID);
            if (id <= 0) continue;

            String caption = cursor.getString(FDDbHelper.ICpEntry.COLUMN_COMPETITION_CAPTION);
            String league = cursor.getString(FDDbHelper.ICpEntry.COLUMN_COMPETITION_LEAGUE);
            String year = cursor.getString(FDDbHelper.ICpEntry.COLUMN_COMPETITION_YEAR);
            int currentMatchDay = cursor.getInt(FDDbHelper.ICpEntry.COLUMN_CURRENT_MATCHDAY);
            int numberOfMatchDays = cursor.getInt(FDDbHelper.ICpEntry.COLUMN_NUMBER_MATCHDAYS);
            int numberOfTeams = cursor.getInt(FDDbHelper.ICpEntry.COLUMN_NUMBER_TEAMS);
            int numberOfGames = cursor.getInt(FDDbHelper.ICpEntry.COLUMN_NUMBER_GAMES);
            long lastUpdated = TimeUnit.MINUTES.toMillis(
                    cursor.getInt(FDDbHelper.ICpEntry.COLUMN_LAST_UPDATE));
            long lastRefresh = TimeUnit.MINUTES.toMillis(
                    cursor.getInt(FDDbHelper.ICpEntry.COLUMN_LAST_REFRESH));


            FDCompetition competition = new FDCompetition(id, caption, league,
                    year, currentMatchDay, numberOfMatchDays, numberOfTeams,
                    numberOfGames, lastUpdated, lastRefresh);

            map.put(competition.getId(), competition);
        }
        return map;
    }


    public static Uri buildItemIdUri(String tableName, long id) {
        return FDContract.BASE_CONTENT_URI.buildUpon().appendPath(tableName).appendPath(Long.toString(id)).build();
    }


    // network

    private static IRetrofitAPI setupRetrofit() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request request = chain.request().newBuilder()
                                .addHeader("X-Auth-Token", BuildConfig.FD_API_KEY)
                                .build();
                        return chain.proceed(request);
                    }
                }).build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(FD_BASE_URI)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(IRetrofitAPI.class);
    }

    private static List<FDCompetition> loadListCompetitions()
            throws NullPointerException, IOException {

        IRetrofitAPI retrofitAPI = setupRetrofit();
        return retrofitAPI.getCompetitions().execute().body();
    }

    // competitions
    private List<FDCompetition> loadListCompetitions(String season)
            throws NullPointerException, IOException {

        IRetrofitAPI retrofitAPI = setupRetrofit();
        return retrofitAPI.getCompetitions(season).execute().body();
    }

    // teams
    private static FDTeams loadListTeams(String competition)
            throws NullPointerException, IOException {

        IRetrofitAPI retrofitAPI = setupRetrofit();
        return retrofitAPI.getTeams(competition).execute().body();
    }

    // fixtures
    private FDFixtures loadListFixtures(String competition)
            throws NullPointerException, IOException {

        IRetrofitAPI retrofitAPI = setupRetrofit();
        return retrofitAPI.getFixtures(competition).execute().body();
    }

    // table
    private FDTable loadTable(String competition)
            throws NullPointerException, IOException {

        IRetrofitAPI retrofitAPI = setupRetrofit();
        return retrofitAPI.getTable(competition).execute().body();
    }

    // players
    private FDPlayers loadListPlayers(String team)
            throws NullPointerException, IOException {

        IRetrofitAPI retrofitAPI = setupRetrofit();
        return retrofitAPI.getTeamPlayers(team).execute().body();
    }

    // data
    private static boolean checkLastRefresh(Context context, Date lastRefresh) {
        if (lastRefresh == null) return false;

        if (!getPrefBool(context, R.string.pref_smart_update_key, R.bool.pref_smart_update_default)) {
            return false; // no smart update mode
        }

        long delay = TimeUnit.MINUTES.toMillis(
                getPrefInt(context,
                        R.string.pref_update_time_key,
                        R.integer.pref_update_time_default));
        long currentTime = Calendar.getInstance().getTimeInMillis();
        long refreshTime = lastRefresh.getTime();

        return currentTime - refreshTime < delay;
    }

    // competitions
    public static Map<Integer,FDCompetition> getCompetitions(
            Context context, Map<Integer, FDCompetition> map, boolean forceUpdate)
            throws NullPointerException, IOException {

        if (!forceUpdate && map != null && map.size() > 0) {
            FDCompetition competition = map.values().iterator().next();
            if (checkLastRefresh(context, competition.getLastRefresh())) {  // check smart update
                return map;
            }
        }

        map = new HashMap<>();  // new HashMap()
        List<FDCompetition> list = loadListCompetitions();  // NullPointerException
        for (FDCompetition competition : list) {
            if (competition == null || competition.getId() <= 0) {
                continue;
            }
            map.put(competition.getId(), competition);
        }

        return map;
    }


    // teams
    // list from competition
    private static List<FDTeam> getListTeams(
            Context context, FDCompetition competition, boolean forceUpdate)
            throws NumberFormatException, NullPointerException, IOException {
        if (competition == null || competition.getId() <= 0) return null;

        List<FDTeam> list = competition.getTeams();
        if (!forceUpdate && list != null && checkLastRefresh(context, competition.getLastRefresh())) {  // check smart update
            return list;
        }

// no teams
        String id = formatString(competition.getId());
        FDTeams teams = loadListTeams(id);      // NullPointerException
        list = new ArrayList<>();
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

    // map from competition
    private static Map<Integer, FDTeam> getTeams(
            Context context, FDCompetition competition, boolean forceUpdate)
            throws NumberFormatException, NullPointerException, IOException {
        Map<Integer, FDTeam> map = new HashMap<>();
        List<FDTeam> teams = getListTeams(context, competition, forceUpdate);
        competition.setTeams(teams);                        // NullPointerException
        for (FDTeam team : teams) {
            map.put(team.getId(), team);

        }
        return map;
    }

    // map from competitions
    public static Map<Integer, FDTeam> getTeams(
            Context context, Map<Integer, FDCompetition> competitions, boolean forceUpdate) {
        if (competitions == null) return null;

        Map<Integer, FDTeam> map = new HashMap<>();
        for (FDCompetition competition : competitions.values()) {
            try {
                map.putAll(getTeams(context, competition, forceUpdate));
            } catch (NumberFormatException | NullPointerException | IOException e) {
                Timber.d(context.getString(R.string.get_competitions_teams_null) + e.getMessage());
            }
        }
        return map;
    }


}
