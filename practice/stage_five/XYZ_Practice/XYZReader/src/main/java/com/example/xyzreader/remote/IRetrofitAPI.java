package com.example.xyzreader.remote;

import com.example.xyzreader.data.ItemModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import static com.example.xyzreader.remote.RemoteEndpointUtil.RECIPES_QUERY;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 20-Nov-17
 * Email: vadim.v.voronov@gmail.com
 */

/**
 *  Retrofit API Interface
 *
 */
public interface IRetrofitAPI {

    /**
     * Returns parsed List<RecipeItem>  after JSON data have been downloaded
     *
     * @param s String empry parameter
     * @return List<RecipeItem>  list of recipes downloaded from the server
     */
    @GET(RECIPES_QUERY)
    Call<List<ItemModel>> getData(@Query("") String s);
}
