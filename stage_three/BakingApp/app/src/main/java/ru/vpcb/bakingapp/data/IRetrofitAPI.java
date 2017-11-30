package ru.vpcb.bakingapp.data;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import static ru.vpcb.bakingapp.utils.Constants.RECIPES_QUERY;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 20-Nov-17
 * Email: vadim.v.voronov@gmail.com
 */

public interface IRetrofitAPI {
    @GET(RECIPES_QUERY)
    Call<List<RecipeItem>> getData(@Query("") String s);
}
