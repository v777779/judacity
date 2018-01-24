package ru.vpcb.contentprovider.data;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.Path;
import retrofit2.http.Query;

import static ru.vpcb.contentprovider.data.Constants.FD_COMPETITIONS_FIXTURES_GET;
import static ru.vpcb.contentprovider.data.Constants.FD_COMPETITIONS_GET;
import static ru.vpcb.contentprovider.data.Constants.FD_COMPETITIONS_QUERY;
import static ru.vpcb.contentprovider.data.Constants.FD_COMPETITIONS_TABLE_GET;
import static ru.vpcb.contentprovider.data.Constants.FD_COMPETITIONS_TEAMS_GET;
import static ru.vpcb.contentprovider.data.Constants.FD_TEAM_FIXTURES_GET;
import static ru.vpcb.contentprovider.data.Constants.FD_TEAM_GET;
import static ru.vpcb.contentprovider.data.Constants.FD_TEAM_PLAYERS_GET;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 20-Nov-17
 * Email: vadim.v.voronov@gmail.com
 */

/**
 * Retrofit API Interface
 */
public interface IRetrofitAPI {

    /**
     * Returns parsed List<RecipeItem>  after JSON data have been downloaded
     *
     * @param s String empry parameter
     * @return List<RecipeItem>  list of recipes downloaded from the server
     */

    @GET(FD_COMPETITIONS_GET)
    // request with query      "baseURL/competitions/?seasons=2015"
    Call<List<FDCompetitions>> getData(@Query(FD_COMPETITIONS_QUERY) String s);

    @GET(FD_COMPETITIONS_GET)
    Call<FDCompetitions> getData();  // request with query   "baseURL/competitions"


    @GET(FD_COMPETITIONS_TEAMS_GET)
    Call<FDTeams> getTeams(@Path(value = "id", encoded = true) String id);

    @GET(FD_COMPETITIONS_FIXTURES_GET)
    Call<FDFixtures> getFixtures(@Path(value = "id", encoded = true) String s);


    @GET(FD_COMPETITIONS_FIXTURES_GET)
    Call<FDFixtures> getFixturesMatch(@Path(value = "id", encoded = true) String s,
                                      @Query("matchday") int day);


    @GET(FD_COMPETITIONS_FIXTURES_GET)
    Call<FDFixtures> getFixturesTime(@Path(value = "id", encoded = true) String s,
                                     @Query("timeFrame") String time);

    @GET(FD_COMPETITIONS_TABLE_GET)
    Call<FDTable> getTable(@Path(value = "id", encoded = true) String s);

    @GET(FD_TEAM_GET)
    Call<FDTeam> getTeam(@Path(value = "id", encoded = true) String id);

    @GET(FD_TEAM_FIXTURES_GET)
    Call<FDFixtures> getTeamFixtures(@Path(value = "id", encoded = true) String s);


    @GET(FD_TEAM_FIXTURES_GET)
    Call<FDFixtures> getTeamFixtures(@Path(value = "id", encoded = true) String s,
                                           @Query("timeFrame") String time,
                                           @Query("season") String season
    );


    @GET(FD_TEAM_PLAYERS_GET)
    Call<FDPlayers> getTeamPlayers(@Path(value = "id", encoded = true) String s);

}


