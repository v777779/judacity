package ru.vpcb.retrot02;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 20-Nov-17
 * Email: vadim.v.voronov@gmail.com
 */

public interface IRetrofitAPI {
    @GET("api/get")
    Call<List<PostModel>> getData(@Query("bash") String key,
                                  @Query("num") int coount);
    @GET("genre/movie/list")
    Call<GenreArray> getMovieGenre(@Query("api_key") String key,
                                   @Query("language") String lang);

    @GET("movie/popular/")
    Call<MovieArray> getData(@Query("api_key") String key,
                             @Query("language") String lang,
                             @Query("page") int page);
}
