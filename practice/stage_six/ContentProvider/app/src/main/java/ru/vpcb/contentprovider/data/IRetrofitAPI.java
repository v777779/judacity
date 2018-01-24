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
import static ru.vpcb.contentprovider.data.Constants.FD_COMPETITIONS_TEAMS_GET;

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

//    @GET(FD_TABLE_GET)
//    Call<List<FDCompetitions>> getTeams(@Query(FD_TABLE_QUERY) String s);
//
//    @GET(FD_PLAYERS_GET)
//    Call<List<FDCompetitions>> getTeams(@Query(FD_PLAYERS_QUERY) String s);

}


