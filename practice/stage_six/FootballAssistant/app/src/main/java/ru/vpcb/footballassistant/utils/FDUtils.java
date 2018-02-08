package ru.vpcb.footballassistant.utils;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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
import ru.vpcb.footballassistant.BuildConfig;
import ru.vpcb.footballassistant.R;
import ru.vpcb.footballassistant.data.FDCompetition;
import ru.vpcb.footballassistant.data.FDFixture;
import ru.vpcb.footballassistant.data.FDFixtures;
import ru.vpcb.footballassistant.data.FDPlayer;
import ru.vpcb.footballassistant.data.FDPlayers;
import ru.vpcb.footballassistant.data.FDTable;
import ru.vpcb.footballassistant.data.FDTeam;
import ru.vpcb.footballassistant.data.FDTeams;
import ru.vpcb.footballassistant.data.IRetrofitAPI;
import ru.vpcb.footballassistant.dbase.FDContract;
import ru.vpcb.footballassistant.dbase.FDDbHelper;
import timber.log.Timber;

import static ru.vpcb.footballassistant.utils.Config.FD_BASE_URI;
import static ru.vpcb.footballassistant.utils.Config.UPDATE_SERVICE_PROGRESS;
import static ru.vpcb.footballassistant.utils.FootballUtils.formatString;
import static ru.vpcb.footballassistant.utils.FootballUtils.getPrefBool;
import static ru.vpcb.footballassistant.utils.FootballUtils.getPrefInt;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 28-Jan-18
 * Email: vadim.v.voronov@gmail.com
 */

public class FDUtils {


    public static Uri buildTableNameUri(String tableName) {
        return FDContract.BASE_CONTENT_URI.buildUpon().appendPath(tableName).build();
    }

    public static Uri buildItemIdUri(String tableName, long id) {
        return FDContract.BASE_CONTENT_URI.buildUpon().appendPath(tableName).appendPath(Long.toString(id)).build();
    }

    public static Uri buildItemIdUri(String tableName, long id, long id2) {
        return FDContract.BASE_CONTENT_URI.buildUpon()
                .appendPath(tableName)
                .appendPath(Long.toString(id))
                .appendPath(Long.toString(id2))
                .build();
    }

    public static int currentTimeMinutes() {
        return (int) TimeUnit.MILLISECONDS.toMinutes(Calendar.getInstance().getTimeInMillis());
    }

    private static Date currentTimeMinutesToDate() {
        return minutesToDate(currentTimeMinutes());
    }

    private static int dateToMinutes(Date date) {
        if (date == null) return 0;
        return (int) TimeUnit.MILLISECONDS.toMinutes(date.getTime());
    }

    private static Date minutesToDate(int time) {
        return new Date(TimeUnit.MINUTES.toMillis(time));
    }

    private static String format(Context context, int id, FDCompetition competition, String message) {
        return context.getString(R.string.load_database, id, competition.getId(), message);
    }

    // data
    private static boolean setListTeams(FDCompetition competition,
                                        Map<Integer, List<Integer>> mapKeys,
                                        Map<Integer, FDTeam> mapTeams) {
        if (competition == null || competition.getId() <= 0 ||
                mapKeys == null || mapKeys.isEmpty() ||
                mapTeams == null || mapTeams.isEmpty()) return false;

        List<Integer> listKeys = mapKeys.get(competition.getId());
//        if (listKeys == null) return false;
        if (listKeys == null) return true;   // map does not have keys by source

        List<FDTeam> list = new ArrayList<>();
        for (Integer key : listKeys) {
            FDTeam team = mapTeams.get(key);
            if (team == null || team.getId() != key) continue;
            list.add(team);
        }
//        if (list == null || list.isEmpty()) return false;
        competition.setTeams(list);
        return true;
    }

    private static boolean setListFixtures(FDCompetition competition,
                                           Map<Integer, List<Integer>> mapKeys,
                                           Map<Integer, FDFixture> mapFixtures) {
        if (competition == null || competition.getId() <= 0 ||
                mapKeys == null || mapKeys.isEmpty() ||
                mapFixtures == null || mapFixtures.isEmpty()) return false;


        List<Integer> listKeys = mapKeys.get(competition.getId());
//        if (listKeys == null) return false;
        if (listKeys == null) return true;  // map does not have keys by source
        List<FDFixture> list = new ArrayList<>();
        for (Integer key : listKeys) {
            FDFixture fixture = mapFixtures.get(key);
            if (fixture == null || fixture.getId() != key) continue;
            list.add(fixture);
        }
//        if (list == null || list.isEmpty()) return false;
        competition.setFixtures(list);
        return true;
    }


    // competitions
    public static void getCompetitionTeams(Context context,
                                           Map<Integer, FDCompetition> competitions,
                                           Map<Integer, List<Integer>> mapKeys,
                                           Map<Integer, FDTeam> mapTeams) {

        for (FDCompetition competition : competitions.values()) {
            setListTeams(competition, mapKeys, mapTeams);
        }
    }

    public static void getCompetitionFixtures(Context context,
                                              Map<Integer, FDCompetition> competitions,
                                              Map<Integer, List<Integer>> mapKeys,
                                              Map<Integer, FDFixture> mapFixtures) {

        for (FDCompetition competition : competitions.values()) {
            setListFixtures(competition, mapKeys, mapFixtures);

        }
    }


    // read
    // competitions
    public static Map<Integer, FDCompetition> readCompetitions(Context context) {
        Uri uri = FDContract.CpEntry.CONTENT_URI;                               // вся таблица
        String sortOrder = FDContract.CpEntry.COLUMN_COMPETITION_ID + " ASC";   // sort by id

        Cursor cursor = context.getContentResolver().query(
                uri,
                null,
                null,
                null,
                sortOrder
        );
        if (cursor == null || cursor.getCount() == 0) return null;
        Map<Integer, FDCompetition> map = readCompetitions(cursor);
        cursor.close();      // disables notifications
        return map;
    }


    // competition_teams
    public static Map<Integer, List<Integer>> readCompetitionTeams(Context context) {
        Uri uri = FDContract.CpTmEntry.CONTENT_URI;                                     // all table

        String sortOrder = FDContract.CpTmEntry.COLUMN_COMPETITION_ID + " ASC";          // sort by id
        Cursor cursor = context.getContentResolver().query(
                uri,
                null,
                null,
                null,
                sortOrder
        );
        if (cursor == null || cursor.getCount() == 0) return null;
        Map<Integer, List<Integer>> map = readCompetitionTeams(cursor);
        cursor.close();     // disables notifications
        return map;
    }

    // teams
    public static Map<Integer, FDTeam> readTeams(Context context) {
        Uri uri = FDContract.TmEntry.CONTENT_URI;                               // вся таблица
//        String sortOrder = FDContract.CpEntry.COLUMN_LAST_UPDATE + " ASC";
        String sortOrder = FDContract.TmEntry.COLUMN_TEAM_ID + " ASC";   // sort by id

        Cursor cursor = context.getContentResolver().query(
                uri,
                null,
                null,
                null,
                sortOrder
        );
        if (cursor == null || cursor.getCount() == 0) return null;
        Map<Integer, FDTeam> map = readTeams(cursor);
        cursor.close();     // disables notifications
        return map;
    }


    // fixtures
    // competition_fixture
    public static Map<Integer, List<Integer>> readCompetitionFixtures(Context context) {
        Uri uri = FDContract.CpFxEntry.CONTENT_URI;                                     // all table

        String sortOrder = FDContract.CpFxEntry.COLUMN_COMPETITION_ID + " ASC";          // sort by id
        Cursor cursor = context.getContentResolver().query(
                uri,
                null,
                null,
                null,
                sortOrder
        );
        if (cursor == null || cursor.getCount() == 0) return null;
        Map<Integer, List<Integer>> map = readCompetitionFixtures(cursor);
        cursor.close();     // disables notifications
        return map;
    }

    // fixture
    public static Map<Integer, FDFixture> readFixtures(Context context) {
        Uri uri = FDContract.FxEntry.CONTENT_URI;                               // вся таблица
//        String sortOrder = FDContract.CpEntry.COLUMN_LAST_UPDATE + " ASC";
        String sortOrder = FDContract.FxEntry.COLUMN_FIXTURE_ID + " ASC";   // sort by id

        Cursor cursor = context.getContentResolver().query(
                uri,
                null,
                null,
                null,
                sortOrder
        );
        if (cursor == null || cursor.getCount() == 0) return null;
        Map<Integer, FDFixture> map = readFixtures(cursor);
        cursor.close();     // disables notifications
        return map;
    }

    // read database
    public static void readDatabase(
            Context context, Map<Integer, FDCompetition> map,
            Map<Integer, List<Integer>> mapTeamKeys, Map<Integer, FDTeam> mapTeams,
            Map<Integer, List<Integer>> mapFixtureKeys, Map<Integer, FDFixture> mapFixtures) {


        Map<Integer, FDCompetition> readMap = readCompetitions(context);
        Map<Integer, List<Integer>> readMapTeamKeys = readCompetitionTeams(context);
        Map<Integer, FDTeam> readMapTeams = readTeams(context);
        Map<Integer, List<Integer>> readMapFixtureKeys = readCompetitionFixtures(context);
        Map<Integer, FDFixture> readMapFixtures = readFixtures(context);

        if (readMap != null && readMap.size() > 0) {
            map.clear();
            map.putAll(readMap);
        }

        if (readMapTeamKeys != null && readMapTeamKeys.size() > 0) {
            mapTeamKeys.clear();
            mapTeamKeys.putAll(readMapTeamKeys);
        }

        if (readMapTeams != null && readMapTeams.size() > 0) {
            mapTeams.clear();
            mapTeams.putAll(readMapTeams);
        }

        if (readMapFixtureKeys != null && readMapFixtureKeys.size() > 0) {
            mapFixtureKeys.clear();
            mapFixtureKeys.putAll(readMapFixtureKeys);
        }

        if (readMapFixtures != null && readMapFixtures.size() > 0) {
            mapFixtures.clear();
            mapFixtures.putAll(readMapFixtures);
        }
    }

    // read cursors
// read
// competitions
    public static Map<Integer, FDCompetition> readCompetitions(Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0) return null;

        Map<Integer, FDCompetition> map = new HashMap<>();
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
            Date lastUpdated = minutesToDate(cursor.getInt(FDDbHelper.ICpEntry.COLUMN_LAST_UPDATE));
            Date lastRefresh = minutesToDate(cursor.getInt(FDDbHelper.ICpEntry.COLUMN_LAST_REFRESH));

            FDCompetition competition = new FDCompetition(id, caption, league,
                    year, currentMatchDay, numberOfMatchDays, numberOfTeams,
                    numberOfGames, lastUpdated, lastRefresh);

            map.put(competition.getId(), competition);
        }
//        cursor.close();  // notification support
        return map;
    }

    // competition_teams
    public static Map<Integer, List<Integer>> readCompetitionTeams(Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0) return null;
        Map<Integer, List<Integer>> map = new HashMap<>();

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            int id = cursor.getInt(FDDbHelper.ICpTmEntry.COLUMN_COMPETITION_ID);
            int id2 = cursor.getInt(FDDbHelper.ICpTmEntry.COLUMN_TEAM_ID);
            if (id <= 0 || id2 <= 0) continue;

            List<Integer> list = map.get(id);
            if (list == null) list = new ArrayList<>();
            list.add(id2);
            map.put(id, list);
        }
//        cursor.close(); // notification support
        return map;
    }

    // teams
    public static Map<Integer, FDTeam> readTeams(Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0) return null;
        Map<Integer, FDTeam> map = new HashMap<>();

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            int id = cursor.getInt(FDDbHelper.ITmEntry.COLUMN_TEAM_ID);
            if (id <= 0) continue;

            String name = cursor.getString(FDDbHelper.ITmEntry.COLUMN_TEAM_NAME);
            String code = cursor.getString(FDDbHelper.ITmEntry.COLUMN_TEAM_CODE);
            String shortName = cursor.getString(FDDbHelper.ITmEntry.COLUMN_TEAM_SHORT_NAME);
            String squadMarketValue = cursor.getString(FDDbHelper.ITmEntry.COLUMN_TEAM_MARKET_VALUE);
            String crestURL = cursor.getString(FDDbHelper.ITmEntry.COLUMN_TEAM_CREST_URI);
            long lastRefresh = TimeUnit.MINUTES.toMillis(
                    cursor.getInt(FDDbHelper.ITmEntry.COLUMN_LAST_REFRESH));

            FDTeam team = new FDTeam(id, name, code, shortName, squadMarketValue, crestURL, lastRefresh);
            map.put(team.getId(), team);
        }
//        cursor.close();
        return map;
    }


    // fixtures
    // competition_fixture
    public static Map<Integer, List<Integer>> readCompetitionFixtures(Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0) return null;
        Map<Integer, List<Integer>> map = new HashMap<>();

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            int id = cursor.getInt(FDDbHelper.ICpFxEntry.COLUMN_COMPETITION_ID);
            int id2 = cursor.getInt(FDDbHelper.ICpFxEntry.COLUMN_FIXTURE_ID);
            if (id <= 0 || id2 <= 0) continue;

            List<Integer> list = map.get(id);
            if (list == null) list = new ArrayList<>();
            list.add(id2);
            map.put(id, list);
        }
//        cursor.close(); // notification support
        return map;
    }

    // fixture
    public static Map<Integer, FDFixture> readFixtures(Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0) return null;
        Map<Integer, FDFixture> map = new HashMap<>();

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            int id = cursor.getInt(FDDbHelper.IFxEntry.COLUMN_FIXTURE_ID);
            if (id <= 0) continue;

            Date date = minutesToDate(cursor.getInt(FDDbHelper.IFxEntry.COLUMN_FIXTURE_DATE));
            String status = cursor.getString(FDDbHelper.IFxEntry.COLUMN_FIXTURE_STATUS);
            int matchday = cursor.getInt(FDDbHelper.IFxEntry.COLUMN_FIXTURE_MATCHDAY);
            String homeTeamName = cursor.getString(FDDbHelper.IFxEntry.COLUMN_FIXTURE_TEAM_HOME);
            String awayTeamName = cursor.getString(FDDbHelper.IFxEntry.COLUMN_FIXTURE_TEAM_AWAY);

            int goalsHomeTeam = cursor.getInt(FDDbHelper.IFxEntry.COLUMN_FIXTURE_GOALS_HOME);
            int goalsAwayTeam = cursor.getInt(FDDbHelper.IFxEntry.COLUMN_FIXTURE_GOALS_AWAY);

            double homeWin = cursor.getDouble(FDDbHelper.IFxEntry.COLUMN_FIXTURE_ODDS_WIN);
            double draw = cursor.getDouble(FDDbHelper.IFxEntry.COLUMN_FIXTURE_ODDS_DRAW);
            double awayWin = cursor.getDouble(FDDbHelper.IFxEntry.COLUMN_FIXTURE_ODDS_AWAY);
            Date lastRefresh = minutesToDate(cursor.getInt(FDDbHelper.IFxEntry.COLUMN_LAST_REFRESH));

            FDFixture fixture = new FDFixture(id, date, status, matchday,
                    homeTeamName, awayTeamName, goalsHomeTeam, goalsAwayTeam,
                    homeWin, draw, awayWin, lastRefresh);

            map.put(fixture.getId(), fixture);
        }
//        cursor.close(); // notification support
        return map;
    }


    // write
    // competition
    public static ArrayList<ContentProviderOperation> writeCompetition(
            FDCompetition competition, boolean forceDelete) {

        if (competition == null || competition.getId() <= 0) return null;

        Uri uri = buildItemIdUri(FDContract.CpEntry.TABLE_NAME, competition.getId());


        ArrayList<ContentProviderOperation> operations = new ArrayList<>();
        if (forceDelete) {
            operations.add(ContentProviderOperation.newDelete(uri).build());  // delete one record from Competitions table
        }

        ContentValues values = new ContentValues();
        int lastUpdate = dateToMinutes(competition.getLastUpdated());
        int lastRefresh = currentTimeMinutes();

        values.put(FDContract.CpEntry.COLUMN_COMPETITION_ID, competition.getId());                  // int
        values.put(FDContract.CpEntry.COLUMN_COMPETITION_CAPTION, competition.getCaption());        // string
        values.put(FDContract.CpEntry.COLUMN_COMPETITION_LEAGUE, competition.getLeague());          // string
        values.put(FDContract.CpEntry.COLUMN_COMPETITION_YEAR, competition.getYear());              // string
        values.put(FDContract.CpEntry.COLUMN_CURRENT_MATCHDAY, competition.getCurrentMatchDay());   // int
        values.put(FDContract.CpEntry.COLUMN_NUMBER_MATCHDAYS, competition.getNumberOfMatchDays()); // int
        values.put(FDContract.CpEntry.COLUMN_NUMBER_TEAMS, competition.getNumberOfTeams());         // int
        values.put(FDContract.CpEntry.COLUMN_NUMBER_GAMES, competition.getNumberOfGames());         // int
        values.put(FDContract.CpEntry.COLUMN_LAST_UPDATE, lastUpdate);                              // int from date
        values.put(FDContract.CpEntry.COLUMN_LAST_REFRESH, lastRefresh);                            // string from date
        operations.add(ContentProviderOperation.newInsert(uri).withValues(values).build());

        return operations;
    }

    // competition
    public static ContentProviderResult[] writeCompetition(Context context, FDCompetition
            competition, boolean forceDelete)
            throws OperationApplicationException, RemoteException {
        return context.getContentResolver().applyBatch(FDContract.CONTENT_AUTHORITY, writeCompetition(competition, forceDelete));
    }

    // competitions
    public static ContentProviderResult[] writeCompetitions(
            Context context, Map<Integer, FDCompetition> map, boolean forceDelete)
            throws OperationApplicationException, RemoteException {

        if (map == null || map.size() == 0) return null;
        Uri uri = FDContract.CpEntry.CONTENT_URI;
        ArrayList<ContentProviderOperation> listOperations = new ArrayList<>();
        if (forceDelete) {
            listOperations.add(ContentProviderOperation.newDelete(uri).build());    // delete all records from Competitions table
        }

        for (FDCompetition competition : map.values()) {
            List<ContentProviderOperation> operations = writeCompetition(competition, false);
            if (operations == null) continue;
            listOperations.addAll(operations);
        }

        return context.getContentResolver().applyBatch(FDContract.CONTENT_AUTHORITY, listOperations);
    }

    // competition_team
    public static ArrayList<ContentProviderOperation> writeCompetitionTeams(FDCompetition competition) {

        int refreshTime = (int) (TimeUnit.MILLISECONDS.toMinutes(Calendar.getInstance().getTime().getTime()));
        ArrayList<ContentProviderOperation> operations = new ArrayList<>();
        // must be deleted because of ids are not keys
        Uri uri = buildItemIdUri(FDContract.CpTmEntry.TABLE_NAME, competition.getId());
        operations.add(ContentProviderOperation.newDelete(uri).build());  // delete all records for Competition from table


        for (FDTeam team : competition.getTeams()) {
            if (team == null || team.getId() <= 0) continue;
            uri = buildItemIdUri(FDContract.CpTmEntry.TABLE_NAME, competition.getId(), team.getId());

            ContentValues values = new ContentValues();
            values.put(FDContract.CpTmEntry.COLUMN_COMPETITION_ID, competition.getId());            // int
            values.put(FDContract.CpTmEntry.COLUMN_TEAM_ID, team.getId());                          // int
            values.put(FDContract.CpTmEntry.COLUMN_LAST_REFRESH, refreshTime);                      // string from date
            operations.add(ContentProviderOperation.newInsert(uri).withValues(values).build());
        }
        return operations;
    }

    public static ContentProviderResult[] writeCompetitionTeams(Context context, FDCompetition
            competition,
                                                                boolean forceDelete)
            throws OperationApplicationException, RemoteException {
        return context.getContentResolver().applyBatch(FDContract.CONTENT_AUTHORITY, writeCompetitionTeams(competition));
    }

    public static ContentProviderResult[] writeCompetitionTeams(
            Context context, Map<Integer, FDCompetition> map, boolean forceDelete)
            throws OperationApplicationException, RemoteException {

        if (map == null || map.size() == 0) return null;

        ArrayList<ContentProviderOperation> listOperations = new ArrayList<>();

        if (forceDelete) {
            Uri uri = FDContract.CpTmEntry.CONTENT_URI;
            listOperations.add(ContentProviderOperation.newDelete(uri).build());
        }

        for (FDCompetition competition : map.values()) {
            if (competition == null || competition.getId() <= 0 ||
                    competition.getTeams() == null) continue;
            List<ContentProviderOperation> operations = writeCompetitionTeams(competition);
            if (operations == null) continue;
            listOperations.addAll(operations);

        }
        return context.getContentResolver().applyBatch(FDContract.CONTENT_AUTHORITY, listOperations);
    }

    // teams
    // team
    public static ArrayList<ContentProviderOperation> writeTeam(FDTeam team, boolean forceDelete) {

        if (team == null || team.getId() <= 0) return null;
        ArrayList<ContentProviderOperation> operations = new ArrayList<>();

        Uri uri = buildItemIdUri(FDContract.TmEntry.TABLE_NAME, team.getId());
        int refreshTime = (int) (TimeUnit.MILLISECONDS.toMinutes(Calendar.getInstance().getTime().getTime()));

        if (forceDelete) { // force clear Teams table
            operations.add(ContentProviderOperation.newDelete(uri).build());
        }

        ContentValues values = new ContentValues();
        values.put(FDContract.TmEntry.COLUMN_TEAM_ID, team.getId());                                // int
        values.put(FDContract.TmEntry.COLUMN_TEAM_NAME, team.getName());                            // string
        values.put(FDContract.TmEntry.COLUMN_TEAM_CODE, team.getCode());                            // string
        values.put(FDContract.TmEntry.COLUMN_TEAM_SHORT_NAME, team.getShortName());                 // string
        values.put(FDContract.TmEntry.COLUMN_TEAM_MARKET_VALUE, team.getSquadMarketValue());        // string
        values.put(FDContract.TmEntry.COLUMN_TEAM_CREST_URI, team.getCrestURL());                   // string
        values.put(FDContract.TmEntry.COLUMN_LAST_REFRESH, refreshTime);                            // int from date

        operations.add(ContentProviderOperation.newInsert(uri).withValues(values).build());
        return operations;
    }

    // team
    public static ContentProviderResult[] writeTeam(Context context, FDTeam team,
                                                    boolean forceDelete)
            throws OperationApplicationException, RemoteException {
        return context.getContentResolver().applyBatch(FDContract.CONTENT_AUTHORITY, writeTeam(team, forceDelete));
    }

    // teams
    public static ContentProviderResult[] writeTeams(Context
                                                             context, Map<Integer, FDCompetition> map, boolean forceDelete)
            throws OperationApplicationException, RemoteException {

        if (map == null || map.size() == 0) return null;


        ArrayList<ContentProviderOperation> listOperations = new ArrayList<>();

        if (forceDelete) {                                          // force clear Teams table
            Uri uri = FDContract.TmEntry.CONTENT_URI;
            listOperations.add(ContentProviderOperation.newDelete(uri).build());
        }

        for (FDCompetition competition : map.values()) {
            if (competition == null || competition.getId() <= 0) continue;
            List<FDTeam> teams = competition.getTeams();
            if (teams == null || teams.size() == 0) continue;
            for (FDTeam team : teams) {
                List<ContentProviderOperation> operations = writeTeam(team, false);
                if (operations == null) continue;
                listOperations.addAll(operations);
            }
        }
        return context.getContentResolver().applyBatch(FDContract.CONTENT_AUTHORITY, listOperations);
    }

    // competition fixture
    public static ArrayList<ContentProviderOperation> writeCompetitionFixtures(
            FDCompetition competition) {


        ArrayList<ContentProviderOperation> operations = new ArrayList<>();
        // all fixtures of given competition  must be deleted because ids are not key
        Uri uri = buildItemIdUri(FDContract.CpFxEntry.TABLE_NAME, competition.getId());
        operations.add(ContentProviderOperation.newDelete(uri).build());  // delete all records for Competition from table


        for (FDFixture fixture : competition.getFixtures()) {
            if (fixture == null || fixture.getId() <= 0) continue;
            uri = buildItemIdUri(FDContract.CpFxEntry.TABLE_NAME, competition.getId(), fixture.getId());
            int refreshTime = dateToMinutes(fixture.getLastRefresh());

            ContentValues values = new ContentValues();
            values.put(FDContract.CpFxEntry.COLUMN_COMPETITION_ID, competition.getId());            // int
            values.put(FDContract.CpFxEntry.COLUMN_FIXTURE_ID, fixture.getId());                    // int
            values.put(FDContract.CpFxEntry.COLUMN_LAST_REFRESH, refreshTime);                      // string from date
            operations.add(ContentProviderOperation.newInsert(uri).withValues(values).build());
        }
        return operations;
    }

    public static ContentProviderResult[] writeCompetitionFixtures(
            Context context, FDCompetition competition, boolean forceDelete)
            throws OperationApplicationException, RemoteException {
        return context.getContentResolver().applyBatch(FDContract.CONTENT_AUTHORITY, writeCompetitionTeams(competition));
    }

    public static ContentProviderResult[] writeCompetitionFixtures(
            Context context, Map<Integer, FDCompetition> map, boolean forceDelete)
            throws OperationApplicationException, RemoteException {

        if (map == null || map.size() == 0) return null;

        ArrayList<ContentProviderOperation> listOperations = new ArrayList<>();

        if (forceDelete) {
            Uri uri = FDContract.CpFxEntry.CONTENT_URI;
            listOperations.add(ContentProviderOperation.newDelete(uri).build());
        }

        for (FDCompetition competition : map.values()) {
            if (competition == null || competition.getId() <= 0 || competition.getFixtures() == null)
                continue;
            List<ContentProviderOperation> operations = writeCompetitionFixtures(competition);
            if (operations == null) continue;
            listOperations.addAll(operations);

        }
        return context.getContentResolver().applyBatch(FDContract.CONTENT_AUTHORITY, listOperations);
    }

    // fixture
    public static ArrayList<ContentProviderOperation> writeFixture(FDFixture fixture, boolean forceDelete) {

        if (fixture == null || fixture.getId() <= 0) return null;
        ArrayList<ContentProviderOperation> operations = new ArrayList<>();

        Uri uri = buildItemIdUri(FDContract.FxEntry.TABLE_NAME, fixture.getId());

        if (forceDelete) { // force clear Teams table
            operations.add(ContentProviderOperation.newDelete(uri).build());
        }

        int refreshTime = dateToMinutes(fixture.getLastRefresh());
        int fixtureDate = dateToMinutes(fixture.getDate());


        ContentValues values = new ContentValues();
        values.put(FDContract.FxEntry.COLUMN_FIXTURE_ID, fixture.getId());                          // int
        values.put(FDContract.FxEntry.COLUMN_FIXTURE_DATE, fixtureDate);                            // int
        values.put(FDContract.FxEntry.COLUMN_FIXTURE_STATUS, fixture.getStatus());                  // string
        values.put(FDContract.FxEntry.COLUMN_FIXTURE_MATCHDAY, fixture.getMatchDay());              // string
        values.put(FDContract.FxEntry.COLUMN_FIXTURE_TEAM_HOME, fixture.getHomeTeamName());         // string
        values.put(FDContract.FxEntry.COLUMN_FIXTURE_TEAM_AWAY, fixture.getAwayTeamName());         // string
        values.put(FDContract.FxEntry.COLUMN_FIXTURE_GOALS_HOME, fixture.getGoalsHome());           // int
        values.put(FDContract.FxEntry.COLUMN_FIXTURE_GOALS_AWAY, fixture.getGoalsAway());           // string
        values.put(FDContract.FxEntry.COLUMN_FIXTURE_ODDS_WIN, fixture.getHomeWin());               // string
        values.put(FDContract.FxEntry.COLUMN_FIXTURE_ODDS_DRAW, fixture.getDraw());                 // string
        values.put(FDContract.FxEntry.COLUMN_FIXTURE_ODDS_AWAY, fixture.getAwayWin());              // string
        values.put(FDContract.FxEntry.COLUMN_LAST_REFRESH, refreshTime);                            // int from date

        operations.add(ContentProviderOperation.newInsert(uri).withValues(values).build());
        return operations;
    }

    // team
    public static ContentProviderResult[] writeFixture(Context context, FDFixture fixture,
                                                       boolean forceDelete)
            throws OperationApplicationException, RemoteException {
        return context.getContentResolver().applyBatch(FDContract.CONTENT_AUTHORITY, writeFixture(fixture, forceDelete));
    }

    // teams
    public static ContentProviderResult[] writeFixtures(
            Context context, Map<Integer, FDCompetition> map, boolean forceDelete)
            throws OperationApplicationException, RemoteException {

        if (map == null || map.size() == 0) return null;

        ArrayList<ContentProviderOperation> listOperations = new ArrayList<>();

        if (forceDelete) {                                          // force clear Teams table
            Uri uri = FDContract.FxEntry.CONTENT_URI;
            listOperations.add(ContentProviderOperation.newDelete(uri).build());
        }

        for (FDCompetition competition : map.values()) {
            if (competition == null || competition.getId() <= 0) continue;
            List<FDFixture> fixtures = competition.getFixtures();
            if (fixtures == null || fixtures.size() == 0) continue;
            for (FDFixture fixture : fixtures) {
                List<ContentProviderOperation> operations = writeFixture(fixture, false);
                if (operations == null) continue;
                listOperations.addAll(operations);
            }
        }
        return context.getContentResolver().applyBatch(FDContract.CONTENT_AUTHORITY, listOperations);
    }

    // write database
    public static void writeDatabase(Context context, Map<Integer, FDCompetition> map, boolean forceDelete)
            throws OperationApplicationException, RemoteException {

        writeCompetitions(context, map, forceDelete);
        writeCompetitionTeams(context, map, forceDelete);
        writeTeams(context, map, forceDelete);
        writeCompetitionFixtures(context, map, forceDelete);
        writeFixtures(context, map, forceDelete);
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
    private static FDFixtures loadListFixtures(String competition)
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

    private static FDFixtures loadListTeamFixtures(String team)
            throws NullPointerException, IOException {

        IRetrofitAPI retrofitAPI = setupRetrofit();
        return retrofitAPI.getTeamFixtures(team).execute().body();
    }

    private static FDPlayers loadListTeamPlayers(String team)
            throws NullPointerException, IOException {

        IRetrofitAPI retrofitAPI = setupRetrofit();
        return retrofitAPI.getTeamPlayers(team).execute().body();
    }



    // data
    private static boolean isRefreshed(Context context, Date lastRefresh) {
        if (lastRefresh == null) return false;

        if (!getPrefBool(context, R.string.pref_smart_update_key, R.bool.pref_smart_update_default)) {
            return false; // update always
        }

        long delay = TimeUnit.MINUTES.toMillis(
                getPrefInt(context, R.string.pref_delay_time_key, R.integer.pref_delay_time_default));
        long currentTime = Calendar.getInstance().getTimeInMillis();
        long refreshTime = lastRefresh.getTime();

        return currentTime - refreshTime < delay;
    }

    // load
    // competitions
    public static Map<Integer, FDCompetition> loadCompetitions(Context context, Map<Integer, FDCompetition> map)
            throws NullPointerException, IOException {

        map = new HashMap<>();
        List<FDCompetition> list = loadListCompetitions();          // NullPointerException, IOException
        for (FDCompetition competition : list) {
            try {
                map.put(competition.getId(), competition);          // NullPointerException, NumberFromatException
                List<FDTeam> teams = loadListTeams(competition);    // NullPointerException, NumberFromatException, IOException
                competition.setTeams(teams);

                List<FDFixture> fixtures = loadListFixtures(competition);  // load
                competition.setFixtures(fixtures);

            } catch (NullPointerException | NumberFormatException | IOException | InterruptedException e) {
                Timber.d(format(context, 1, competition, e.getMessage()));
            }
        }
        return map;
    }

    private static boolean isCompetitionsRefreshed(Context context, Map<Integer, FDCompetition> map) {
        if (map == null || map.isEmpty()) return false;
        for (FDCompetition competition : map.values()) {
            if (competition == null || competition.getId() <= 0 ||
                    !isRefreshed(context, competition.getLastRefresh())) return false;
        }
        return true; // all data ok and refreshed
    }

    // all maps read by loaders
// load competitions
    public static boolean loadDatabaseRefresh(
            Context context, Map<Integer, FDCompetition> map,
            Map<Integer, List<Integer>> mapTeamKeys, Map<Integer, FDTeam> mapTeams,
            Map<Integer, List<Integer>> mapFixtureKeys, Map<Integer, FDFixture> mapFixtures,
            boolean forceUpdate) throws NullPointerException, IOException {

// progress
        double step;
        double progress = 0;

// load teams or skip
        boolean isUpdated = false;
        boolean isCompetitionUpdated = false;
// load map
        if (forceUpdate || !isCompetitionsRefreshed(context, map)) {
            isUpdated = true;
            isCompetitionUpdated = true;
            map.clear();
            List<FDCompetition> list = loadListCompetitions();  // NullPointerException, IOException
            Date lastRefresh = currentTimeMinutesToDate();
            for (FDCompetition competition : list) {
                if (competition == null || competition.getId() <= 0) continue;
                competition.setLastRefresh(lastRefresh);
                map.put(competition.getId(), competition);
            }
        }

        step = UPDATE_SERVICE_PROGRESS * 0.8 / (map.size() + 1); // 1 + t.map.size + f.map.size
        progress = step;
        sendProgress(context, (int) progress);  // +1

        for (FDCompetition competition : map.values()) {
            if (competition == null || competition.getId() <= 0) continue;
// teams
            boolean isRefreshed = false;
            if (!forceUpdate && !isCompetitionUpdated &&                                // if competitions updated load teams
                    isRefreshed(context, competition.getLastRefresh())) {               // check smart update
                isRefreshed = true;
                if (competition.getTeams() == null) {
                    isRefreshed = setListTeams(competition, mapTeamKeys, mapTeams);     // restore teams from keys
                }
            }

            if (!isRefreshed) {
                try {
                    isUpdated = true;                                                   // one item changed
                    List<FDTeam> teams = loadListTeams(competition);                    // load
                    competition.setTeams(teams);
                } catch (NullPointerException | NumberFormatException | IOException | InterruptedException e) {
                    Timber.d(format(context, 2, competition, e.getMessage()));
                }
            }
// fixtures
            isRefreshed = false;
            if (!forceUpdate && !isCompetitionUpdated &&                                // if competitions updated load fixtures
                    isRefreshed(context, competition.getLastRefresh())) {               // check smart update
                isRefreshed = true;
                if (competition.getFixtures() == null) {
                    isRefreshed = setListFixtures(competition, mapFixtureKeys, mapFixtures);
                }
            }

            if (!isRefreshed) {
                try {
                    isUpdated = true;                                                   // one item changed
                    List<FDFixture> fixtures = loadListFixtures(competition);           // load
                    competition.setFixtures(fixtures);
                } catch (NullPointerException | NumberFormatException | IOException | InterruptedException e) {
                    Timber.d(format(context, 3, competition, e.getMessage()));
                }

            }
// progress
            progress += step;
            sendProgress(context, (int) progress);// t,f
        }
        return isUpdated;
    }


    // all maps read by loaders
// load competitions
    public static boolean loadDatabase(
            Context context, Map<Integer, FDCompetition> map,
            Map<Integer, List<Integer>> mapTeamKeys, Map<Integer, FDTeam> mapTeams,
            Map<Integer, List<Integer>> mapFixtureKeys, Map<Integer, FDFixture> mapFixtures
    ) throws NullPointerException, IOException {

// progress
        double step;
        double progress = 0;

// load map
        map.clear();
        List<FDCompetition> list = loadListCompetitions();  // NullPointerException, IOException
        Date lastRefresh = currentTimeMinutesToDate();
        for (FDCompetition competition : list) {
            if (competition == null || competition.getId() <= 0) continue;
            competition.setLastRefresh(lastRefresh);
            map.put(competition.getId(), competition);
        }

        step = UPDATE_SERVICE_PROGRESS * 0.8 / (map.size() + 1); // 1 + t.map.size + f.map.size
        progress = step;
        sendProgress(context, (int) progress);  // +1

        for (FDCompetition competition : map.values()) {
            if (competition == null || competition.getId() <= 0) continue;
// teams
            try {
                List<FDTeam> teams = loadListTeams(competition);                    // load
                competition.setTeams(teams);
            } catch (NullPointerException | NumberFormatException | IOException | InterruptedException e) {
                Timber.d(format(context, 4, competition, e.getMessage()));
            }
// fixtures
            try {
                List<FDFixture> fixtures = loadListFixtures(competition);           // load
                competition.setFixtures(fixtures);

            } catch (NullPointerException | NumberFormatException | IOException | InterruptedException e) {
                Timber.d(format(context, 5, competition, e.getMessage()));
            }
// progress
            progress += step;
            sendProgress(context, (int) progress);// t,f
        }
        return true;
    }

    // teams
    // list from competition
    private static List<FDTeam> loadListTeams(FDCompetition competition)
            throws NumberFormatException, NullPointerException, IOException, InterruptedException {
        if (competition == null || competition.getId() <= 0) return null;

        String id = formatString(competition.getId());
        long lastRefresh = Calendar.getInstance().getTimeInMillis();
        FDTeams teams = loadListTeams(id);      // NullPointerException
        if (teams == null) {
// test!!!
//            Thread.sleep(100);
//            teams = loadListTeams(id); // second trial
        }
        List<FDTeam> list = new ArrayList<>();
        for (FDTeam team : teams.getTeams()) {
            try {
                team.setId();
                team.setLastRefresh(lastRefresh);
            } catch (NullPointerException | NumberFormatException e) {
                continue;
            }
            list.add(team);
        }
        return list;
    }

    // list from competition
    private static List<FDTeam> getListTeams(
            Context context, FDCompetition competition, boolean forceUpdate)
            throws NumberFormatException, NullPointerException, IOException {
        if (competition == null || competition.getId() <= 0) return null;
// smart update check
        List<FDTeam> list = competition.getTeams();
        if (!forceUpdate && list != null && isRefreshed(context, competition.getLastRefresh())) {  // check smart update
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


    // list from competition
    private static List<FDFixture> loadListFixtures(FDCompetition competition)
            throws NumberFormatException, NullPointerException, IOException, InterruptedException {
        if (competition == null || competition.getId() <= 0) return null;

        int competitionId = competition.getId();
        String id = formatString(competitionId);
        long lastRefresh = Calendar.getInstance().getTimeInMillis();
        FDFixtures fixtures = loadListFixtures(id);      // NullPointerException
        if (fixtures == null) {
// test!!!
//            Thread.sleep(100);
//            fixtures = loadListFixtures(id); // second trial
        }
        List<FDFixture> list = new ArrayList<>();
        for (FDFixture fixture : fixtures.getFixtures()) {
            try {
                fixture.setId();
                fixture.setCompetitionId();
                fixture.setLastRefresh(lastRefresh);
            } catch (NullPointerException | NumberFormatException e) {
                continue;
            }
            list.add(fixture);
        }
        return list;
    }

    // load competitions
    public static boolean loadCompetitions(
            Map<Integer, FDCompetition> map,
            Map<Integer, List<Integer>> mapTeamKeys, Map<Integer, FDTeam> mapTeams,
            Map<Integer, List<Integer>> mapFixtureKeys, Map<Integer, FDFixture> mapFixtures) {

        if (map == null || map.size() == 0 ||
                mapTeamKeys == null || mapTeamKeys.isEmpty() ||
                mapTeams == null || mapTeams.isEmpty() ||
                mapFixtureKeys == null || mapFixtureKeys.isEmpty() ||
                mapFixtures == null || mapFixtures.isEmpty()) return false;

// load map
        for (FDCompetition competition : map.values()) {
            if (competition == null || competition.getId() <= 0) continue;
// teams
            if (competition.getTeams() == null) {
                setListTeams(competition, mapTeamKeys, mapTeams);    // restore teams from keys
            }
// fixtures
            if (competition.getFixtures() == null) {
                setListFixtures(competition, mapFixtureKeys, mapFixtures);
            }
        }
        return true;
    }


    // load fixtures from team
    private static List<FDFixture> loadListTeamFixtures(int teamId)
            throws NumberFormatException, NullPointerException, IOException, InterruptedException {
        if (teamId <= 0) return null;

        String id = formatString(teamId);
        long lastRefresh = Calendar.getInstance().getTimeInMillis();
        FDFixtures fixtures = loadListTeamFixtures(id);      // NullPointerException
        List<FDFixture> list = new ArrayList<>();
        for (FDFixture fixture : fixtures.getFixtures()) {
            try {
                fixture.setId();
                fixture.setCompetitionId();
                fixture.setLastRefresh(lastRefresh);
            } catch (NullPointerException | NumberFormatException e) {
                continue;
            }
            list.add(fixture);
        }
        return list;
    }

    public static List<FDFixture> loadListTeamFixtures(Context context, int id) {
        try {
            return loadListTeamFixtures(id);
        }catch (NumberFormatException|NullPointerException |IOException |InterruptedException e) {
            Timber.d(context.getString(R.string.retrofit_response_empty), e.getMessage());
            return null;
        }
    }

    // load players from team
    private static List<FDPlayer> loadListTeamPlayers(int teamId)
            throws NumberFormatException, NullPointerException, IOException, InterruptedException {
        if (teamId <= 0) return null;

        String id = formatString(teamId);

        FDPlayers players = loadListTeamPlayers(id);      // NullPointerException
        List<FDPlayer> list = new ArrayList<>();
        for (FDPlayer player : players.getPlayers()) {
            if(player == null) continue;
            list.add(player);
        }
        return list;
    }

    public static List<FDPlayer> loadListTeamPlayers(Context context,int teamId) {
        try {
            return loadListTeamPlayers(teamId);
        }catch (NumberFormatException|NullPointerException |IOException |InterruptedException e) {
            Timber.d(context.getString(R.string.retrofit_response_empty), e.getMessage());
            return null;
        }
    }








    public static void sendProgress(Context context, int value) {
        if (value < 0) return;
        if (value > UPDATE_SERVICE_PROGRESS) value = UPDATE_SERVICE_PROGRESS;

        context.sendBroadcast(new Intent(context.getString(R.string.broadcast_update_progress))
                .putExtra(context.getString(R.string.extra_progress_counter), value));

    }

}
