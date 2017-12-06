package ru.vpcb.retrot02;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static boolean isTimber;


    private IRetrofitAPI mRetrofitAPI;
    private IRetrofitAPI mRetrofitMovieAPI;
    private Retrofit mRetrofit;
    private Retrofit mRetrofitMovie;


    private boolean isLogging;
    private static final String API_BASE_URL = "http://umorili.herokuapp.com/";
    private static final String API_BASE_MOVIE = "https://api.themoviedb.org/3/";
    private static final String API_KEY = "0c311071129c1ec0bdbcc648e978bedb";
    private static final String API_LANG = "en_US";
    private static final int API_PAGE = 1;
//      https://api.themoviedb.org/3/genre/movie/list?api_key=0c311071129c1ec0bdbcc648e978bedb&language=en_US
//      https://api.themoviedb.org/3/movie/popular?api_key=0c311071129c1ec0bdbcc648e978bedb&language=en_US&page=1

    @BindView(R.id.recipe_text)
    TextView mRecipe;
    @BindView(R.id.ingredients_text)
    TextView mIngredients;
    @BindView(R.id.steps_text)
    TextView mSteps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        isLogging = true;

        if (!isTimber) {
            Timber.plant(new Timber.DebugTree());
            isTimber = true;
        }


        if (isLogging) {
//logging
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);     // set your desired log level  NONE, BASIC, HEADERS, BODY
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(logging);  // <-- this is the important line!


            mRetrofit = new Retrofit.Builder()   // add your other interceptors …
                    .baseUrl(API_BASE_URL)       // add logging as last interceptor
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();

        } else {
// no logging
            mRetrofit = new Retrofit.Builder()
                    .baseUrl(API_BASE_URL) //Базовая часть адреса
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        mRetrofitAPI = mRetrofit.create(IRetrofitAPI.class);


        if (isLogging) {
//logging

//            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
// override logger with Timber
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    Timber.tag("Timber OkHttp").d(message);
                }
            });

            logging.setLevel(HttpLoggingInterceptor.Level.BODY);     // set your desired log level  NONE, BASIC, HEADERS, BODY
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(logging);  // <-- this is the important line!


            mRetrofitMovie = new Retrofit.Builder()   // add your other interceptors …
                    .baseUrl(API_BASE_MOVIE)       // add logging as last interceptor
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();


        } else {
            mRetrofitMovie = new Retrofit.Builder()
                    .baseUrl(API_BASE_MOVIE) //Базовая часть адреса
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        mRetrofitMovieAPI = mRetrofitMovie.create(IRetrofitAPI.class);
    }

    public void onClickButtonText(View view) {

        mRetrofitAPI.getData("bash", 5).enqueue(new Callback<List<PostModel>>() {
            @Override
            public void onResponse(Call<List<PostModel>> call, Response<List<PostModel>> response) {
                if (response.body() == null) {
                    return;
                }

                List<PostModel> list = response.body();
                int n = new Random().nextInt(list.size());
                mRecipe.setText(list.get(n).getName());
                mIngredients.setText(list.get(n).getDesc());
                mSteps.setText(list.get(n).getElementPureHtml());

            }

            @Override
            public void onFailure(Call<List<PostModel>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "rere", Toast.LENGTH_SHORT);
            }
        });

    }


    public void onClickButtonGenres(final View view) {
        if (!isOnline(this)) {
            Snackbar.make(view, "No Internet Connection", BaseTransientBottomBar.LENGTH_SHORT).show();
            return;
        }
        mRetrofitMovieAPI.getMovieGenre(API_KEY, API_LANG).enqueue(new Callback<GenreArray>() {
            @Override
            public void onResponse(Call<GenreArray> call, Response<GenreArray> response) {
                if (response.isSuccessful()) {
                    if (response.body() == null) {
                        return;
                    }

                    List<GenreModel> list = response.body().getGenres();

                    mRecipe.setText("Genres:");
                    mIngredients.setText("" + list.size());
                    StringBuilder stringBuilder = new StringBuilder();
                    for (GenreModel genreModel : list) {
                        stringBuilder.append(genreModel.getName() + ", ");
                    }
                    stringBuilder.append(".");
//                    Gson gson = new Gson();

                    Gson gson = new GsonBuilder()
                            .setPrettyPrinting()  // pretty printing
                            .create();

                    String toJson = gson.toJson(response.body());
                    stringBuilder.append("\n\n" + toJson);
                    mSteps.setText(stringBuilder.toString());
                } else {
                    Snackbar.make(view, "error code:" + response.code(),
                            BaseTransientBottomBar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GenreArray> call, Throwable t) {
                Timber.d(TAG + " from Timber " + t.getMessage());
            }
        });

    }

    public void onClickButtonMovies(View view) {
        mRetrofitMovieAPI.getData(API_KEY, API_LANG, API_PAGE).enqueue(new Callback<MovieArray>() {
            @Override
            public void onResponse(Call<MovieArray> call, Response<MovieArray> response) {
                if (response.body() == null) {
                    return;
                }

                MovieArray movieArray = response.body();
                List<MovieModel> list = movieArray.getResults();
                int n = new Random().nextInt(list.size());
                MovieModel movie = list.get(n);
                mRecipe.setText("Movies: page:" + movieArray.getPage());
                mIngredients.setText(movie.getTitle() + " " + String.format("rating: %.2f", movie.getVote_average()));
                mSteps.setText(movie.getOverview());

                  Gson gson = new Gson();       // standard

//                Gson gson = new GsonBuilder()  // pretty printing
//                        .setPrettyPrinting()
//                        .create();

                String toJson = gson.toJson(movie);
                mSteps.setText(toJson);
                MovieModel m = gson.fromJson(toJson, MovieModel.class);
                mIngredients.setText(m.getTitle() + " " + String.format("rating: %.2f", m.getVote_average()));
            }

            @Override
            public void onFailure(Call<MovieArray> call, Throwable t) {
                Timber.d(TAG + " from Timber Movie " + t.getMessage());
            }
        });

    }

    private boolean isOnline(Context context) {
        try {
            ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        } catch (Exception e) {
            return false;
        }
    }
}
