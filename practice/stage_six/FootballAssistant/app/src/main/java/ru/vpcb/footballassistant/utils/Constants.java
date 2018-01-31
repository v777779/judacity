package ru.vpcb.footballassistant.utils;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 24-Jan-18
 * Email: vadim.v.voronov@gmail.com
 */

public class Constants {

    // data
    public static final String FD_BASE_URI = "http://api.football-data.org/v1/";
    public static final String FD_COMPETITIONS_GET = "competitions/";

    public static final String FD_QUERY_ID = "id";
    public static final String FD_QUERY_MATCHDAY = "matchday";
    public static final String FD_QUERY_TIMEFRAME = "timeFrame";
    public static final String FD_QUERY_SEASON = "season";

    public static final String FD_COMPETITIONS_TEAMS_GET = "competitions/{" + FD_QUERY_ID + "}/teams";
    public static final String FD_COMPETITIONS_FIXTURES_GET = "competitions/{" + FD_QUERY_ID + "}/fixtures";
    public static final String FD_COMPETITIONS_TABLE_GET = "competitions/{" + FD_QUERY_ID + "}/leagueTable";


    public static final String FD_TEAM_GET = "teams/{" + FD_QUERY_ID + "}";
    public static final String FD_TEAM_FIXTURES_GET = "teams/{" + FD_QUERY_ID + "}/fixtures";
    public static final String FD_TEAM_PLAYERS_GET = "teams/{" + FD_QUERY_ID + "}/players";

    public static final String FD_TIME_PAST = "p";
    public static final String FD_TIME_FUTUTRE = "n";

    public static final String FD_REGEX_TEAMS = ".*teams/";
    public static final String FD_REGEX_FIXTURES = ".*fixtures/";
    public static final String FD_REGEX_COMPETITIONS = ".*competitions/";

    // update service
    public static final String UPDATE_SERVICE_TAG = "UpdaterService";
    public static final int  UPDATE_SERVICE_PROGRESS = 60;
    public static final int  MAIN_ACTIVITY_PROGRESS = 40;


    // retrofit


    // loaders

}
