package ru.vpcb.contentprovider;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.vpcb.contentprovider.data.FDCompetitions;
import ru.vpcb.contentprovider.data.FDDateType;
import ru.vpcb.contentprovider.data.FDFixtures;
import ru.vpcb.contentprovider.data.FDPlayers;
import ru.vpcb.contentprovider.data.FDTable;
import ru.vpcb.contentprovider.data.FDTeam;
import ru.vpcb.contentprovider.data.FDTeams;
import ru.vpcb.contentprovider.data.IRetrofitAPI;
import timber.log.Timber;


import static ru.vpcb.contentprovider.data.Constants.FD_BASE_URI;
import static ru.vpcb.contentprovider.data.Constants.FD_TIME_FUTUTRE;
import static ru.vpcb.contentprovider.data.Constants.FD_TIME_PAST;
import static ru.vpcb.contentprovider.data.FootballUtils.isOnline;

public class MainActivity extends AppCompatActivity {
    /**
     * The flag is true it Timber.Tree is exists
     */
    public static boolean mIsTimber;


    private ProgressBar mProgressBar;
    private FloatingActionButton mFab;
    private FloatingActionButton mFab2;

    private IRetrofitAPI mRetrofitAPI;
    private Retrofit mRetrofit;
    private OkHttpClient mOkHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // log
        if (!mIsTimber) {
            Timber.plant(new Timber.DebugTree());
            mIsTimber = true;
        }


        mFab = findViewById(R.id.fab);
        mFab2 = findViewById(R.id.fab2);
        mProgressBar = findViewById(R.id.progress_bar);


        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                Intent intent = new Intent(MainActivity.this, BottomActivity.class);
                startActivity(intent);
            }
        });


        mFab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                startRetrofitLoader();
//                startRetrofitLoaderC();  //ok
//                startRetrofitLoaderCT();  // ok
//                startRetrofitLoaderCF(); // ok
//                startRetrofitLoaderCFM(); // ok
//                startRetrofitLoaderCFT(); // ok
//                startRetrofitLoaderLT(); // ok
//                startRetrofitLoaderT(); // ok
//                startRetrofitLoaderF(); // ok
//                startRetrofitLoaderFT(); // ok
                startRetrofitLoaderP();


            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Shows ProgressBar View object
     */
    private void showProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    /**
     * Hides ProgressBar and ErrorMessage Views
     */
    private void showResult() {
        mProgressBar.setVisibility(View.INVISIBLE);
    }


    private String getCurrentYear() {
        Calendar calendar = Calendar.getInstance();
        return String.format("%4d", calendar.get(Calendar.YEAR));
    }

    private void startRetrofitLoaderC() {
        if (!isOnline(this)) {
            return;
        }
        showProgress();

// setup data
        String season = getCurrentYear();
// setup okHttpClient
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
// setup Retrofit
        mRetrofit = new Retrofit.Builder()
                .client(mOkHttpClient)
                .baseUrl(FD_BASE_URI)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mRetrofitAPI = mRetrofit.create(IRetrofitAPI.class);
        mRetrofitAPI.getData(season).enqueue(new Callback<List<FDCompetitions>>() {
            @Override
            public void onResponse(Call<List<FDCompetitions>> call, Response<List<FDCompetitions>> response) {
                if (response.body() == null) {
                    showResult();
                    return;
                }
                List<FDCompetitions> competitions = response.body();

// test!!! записать в ContentProvider
//                RecipeUtils.bulkInsertBackground(mContext.getContentResolver(), getSupportLoaderManager(), list, mLoaderDb);
// test!!!  сохранить  timestamp
//                saveReLoadTimePreference();
                showResult();
            }

            @Override
            public void onFailure(Call<List<FDCompetitions>> call, Throwable t) {
                Timber.d(t.getMessage());
                showResult();
            }
        });
    }

    private void startRetrofitLoaderCT() {
        if (!isOnline(this)) {
            return;
        }
        showProgress();

// setup data
        int id = 450;
        String competition = String.format("%d", id);
// setup okHttpClient
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
// setup Retrofit
        mRetrofit = new Retrofit.Builder()
                .client(mOkHttpClient)
                .baseUrl(FD_BASE_URI)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mRetrofitAPI = mRetrofit.create(IRetrofitAPI.class);
        mRetrofitAPI.getTeams(competition).enqueue(new Callback<FDTeams>() {
            @Override
            public void onResponse(Call<FDTeams> call, Response<FDTeams> response) {
                if (response.body() == null) {
                    showResult();
                    return;
                }
                FDTeams teams = response.body();
                showResult();
            }

            @Override
            public void onFailure(Call<FDTeams> call, Throwable t) {
                Timber.d(t.getMessage());
                showResult();
            }
        });
    }


    private void startRetrofitLoaderCF() {
        if (!isOnline(this)) {
            return;
        }
        showProgress();
// setup data
        int id = 446;
        String competition = String.format("%d", id);
// setup okHttpClient
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
// setup Retrofit
        mRetrofit = new Retrofit.Builder()
                .client(mOkHttpClient)
                .baseUrl(FD_BASE_URI)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mRetrofitAPI = mRetrofit.create(IRetrofitAPI.class);
        mRetrofitAPI.getFixtures(competition).enqueue(new Callback<FDFixtures>() {
            @Override
            public void onResponse(Call<FDFixtures> call, Response<FDFixtures> response) {
                if (response.body() == null) {
                    showResult();
                    return;
                }
                FDFixtures fixtures = response.body();
                showResult();
            }

            @Override
            public void onFailure(Call<FDFixtures> call, Throwable t) {
                Timber.d(t.getMessage());
                showResult();
            }
        });
    }


    private void startRetrofitLoaderCFM() {
        if (!isOnline(this)) {
            return;
        }
        showProgress();
// setup data
        int id = 446;
        String competition = String.format("%d", id);
        int matchDay = 25;
// setup okHttpClient
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
// setup Retrofit
        mRetrofit = new Retrofit.Builder()
                .client(mOkHttpClient)
                .baseUrl(FD_BASE_URI)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mRetrofitAPI = mRetrofit.create(IRetrofitAPI.class);
        mRetrofitAPI.getFixturesMatch(competition, matchDay).enqueue(new Callback<FDFixtures>() {
            @Override
            public void onResponse(Call<FDFixtures> call, Response<FDFixtures> response) {
                if (response.body() == null) {
                    showResult();
                    return;
                }
                FDFixtures fixtures = response.body();
                showResult();
            }

            @Override
            public void onFailure(Call<FDFixtures> call, Throwable t) {
                Timber.d(t.getMessage());
                showResult();
            }
        });
    }

    private void startRetrofitLoaderCFT() {
        if (!isOnline(this)) {
            return;
        }
        showProgress();
// setup data
        int id = 446;
        String competition = String.format("%d", id);
        int nDays = 50;
        String nTime = FD_TIME_PAST;

        String timeFrame = String.format("%s%d", nTime, nDays);
// setup okHttpClient
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
// setup Retrofit
        mRetrofit = new Retrofit.Builder()
                .client(mOkHttpClient)
                .baseUrl(FD_BASE_URI)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mRetrofitAPI = mRetrofit.create(IRetrofitAPI.class);
        mRetrofitAPI.getFixturesTime(competition, timeFrame).enqueue(new Callback<FDFixtures>() {
            @Override
            public void onResponse(Call<FDFixtures> call, Response<FDFixtures> response) {
                if (response.body() == null) {
                    showResult();
                    return;
                }
                FDFixtures fixtures = response.body();
                showResult();
            }

            @Override
            public void onFailure(Call<FDFixtures> call, Throwable t) {
                Timber.d(t.getMessage());
                showResult();
            }
        });
    }

    private void startRetrofitLoaderLT() {
        if (!isOnline(this)) {
            return;
        }
        showProgress();
// setup data
        int id = 446;
        String competition = String.format("%d", id);
// setup okHttpClient
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
// setup Retrofit
        mRetrofit = new Retrofit.Builder()
                .client(mOkHttpClient)
                .baseUrl(FD_BASE_URI)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mRetrofitAPI = mRetrofit.create(IRetrofitAPI.class);
        mRetrofitAPI.getTable(competition).enqueue(new Callback<FDTable>() {
            @Override
            public void onResponse(Call<FDTable> call, Response<FDTable> response) {
                if (response.body() == null) {
                    showResult();
                    return;
                }
                FDTable table = response.body();
                showResult();
            }

            @Override
            public void onFailure(Call<FDTable> call, Throwable t) {
                Timber.d(t.getMessage());
                showResult();
            }
        });
    }


    private void startRetrofitLoaderT() {
        if (!isOnline(this)) {
            return;
        }
        showProgress();
// setup data
        int id = 524;
        String team = String.format("%d", id);
// setup okHttpClient
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
// setup Retrofit
        mRetrofit = new Retrofit.Builder()
                .client(mOkHttpClient)
                .baseUrl(FD_BASE_URI)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mRetrofitAPI = mRetrofit.create(IRetrofitAPI.class);
        mRetrofitAPI.getTeam(team).enqueue(new Callback<FDTeam>() {
            @Override
            public void onResponse(Call<FDTeam> call, Response<FDTeam> response) {
                if (response.body() == null) {
                    showResult();
                    return;
                }
                FDTeam team = response.body();
                showResult();
            }

            @Override
            public void onFailure(Call<FDTeam> call, Throwable t) {
                Timber.d(t.getMessage());
                showResult();
            }
        });
    }


    private void startRetrofitLoaderF() {
        if (!isOnline(this)) {
            return;
        }
        showProgress();
// setup data
        int id = 524;
        String team = String.format("%d", id);
// setup okHttpClient
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
// setup Retrofit
        mRetrofit = new Retrofit.Builder()
                .client(mOkHttpClient)
                .baseUrl(FD_BASE_URI)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mRetrofitAPI = mRetrofit.create(IRetrofitAPI.class);
        mRetrofitAPI.getTeamFixtures(team).enqueue(new Callback<FDFixtures>() {
            @Override
            public void onResponse(Call<FDFixtures> call, Response<FDFixtures> response) {
                if (response.body() == null) {
                    showResult();
                    return;
                }
                FDFixtures fixtures = response.body();
                showResult();
            }

            @Override
            public void onFailure(Call<FDFixtures> call, Throwable t) {
                Timber.d(t.getMessage());
                showResult();
            }
        });
    }

    private int counter = 0;

    private void startRetrofitLoaderFT() {
        if (!isOnline(this)) {
            return;
        }
        showProgress();
// setup data
        int id = 524;
        String team = String.format("%d", id);
        int nDays = 5;
        String nTime = FD_TIME_FUTUTRE;
        String season = getCurrentYear();
        String timeFrame = String.format("%s%d", nTime, nDays);

// setup okHttpClient
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
// setup Retrofit
        mRetrofit = new Retrofit.Builder()
                .client(mOkHttpClient)
                .baseUrl(FD_BASE_URI)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mRetrofitAPI = mRetrofit.create(IRetrofitAPI.class);
        mRetrofitAPI.getTeamFixtures(team, timeFrame, season).enqueue(new Callback<FDFixtures>() {
            @Override
            public void onResponse(Call<FDFixtures> call, Response<FDFixtures> response) {
                if (response.body() == null) {
                    showResult();
                    Timber.d(counter++ + ": " + response.toString());
                    return;
                }
                FDFixtures fixtures = response.body();

                Timber.d(counter++ + ": " + fixtures.toString());
                showResult();

            }

            @Override
            public void onFailure(Call<FDFixtures> call, Throwable t) {
                Timber.d(t.getMessage());
                showResult();
            }
        });
    }

    private void startRetrofitLoaderP() {
        if (!isOnline(this)) {
            return;
        }
        showProgress();
// setup data
        int id = 338;
        final String team = String.format("%d", id);

// setup okHttpClient
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
// setup Retrofit
        mRetrofit = new Retrofit.Builder()
                .client(mOkHttpClient)
                .baseUrl(FD_BASE_URI)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mRetrofitAPI = mRetrofit.create(IRetrofitAPI.class);
        mRetrofitAPI.getTeamPlayers(team).enqueue(new Callback<FDPlayers>() {
            @Override
            public void onResponse(Call<FDPlayers> call, Response<FDPlayers> response) {
                if (response.body() == null) {
                    showResult();
                    return;
                }
                FDPlayers players = response.body();
                showResult();
            }

            @Override
            public void onFailure(Call<FDPlayers> call, Throwable t) {
                Timber.d(t.getMessage());
                showResult();
            }
        });
    }


    private void startRetrofitLoader() {
        if (!isOnline(this)) {
            return;
        }
        showProgress();

        Calendar calendar = Calendar.getInstance();
        String currentYear = String.format("%4d", calendar.get(Calendar.YEAR));

// setup Retrofit
        Gson gson = new GsonBuilder()
                .setDateFormat(DateFormat.FULL, DateFormat.FULL)
                .create();

        Gson gson2 = new GsonBuilder()
                .registerTypeAdapter(Date.class, new FDDateType())
                .create();


        mRetrofit = new Retrofit.Builder()
                .baseUrl(FD_BASE_URI)                                           //Базовая часть адреса
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        mRetrofitAPI = mRetrofit.create(IRetrofitAPI.class);
        mRetrofitAPI.getData(currentYear).enqueue(new Callback<List<FDCompetitions>>() {
            @Override
            public void onResponse(Call<List<FDCompetitions>> call, Response<List<FDCompetitions>> response) {
                if (response.body() == null) {
                    showResult();
                    return;
                }
                List<FDCompetitions> competitions = response.body();


// test!!! записать в ContentProvider
//                RecipeUtils.bulkInsertBackground(mContext.getContentResolver(), getSupportLoaderManager(), list, mLoaderDb);
// test!!!  сохранить  timestamp
//                saveReLoadTimePreference();
                showResult();
            }

            @Override
            public void onFailure(Call<List<FDCompetitions>> call, Throwable t) {
                Timber.d(t.getMessage());
                showResult();
            }
        });
    }

    private void startRetrofitLoaderDateFull() {
        if (!isOnline(this)) {
            return;
        }
        showProgress();

        Calendar calendar = Calendar.getInstance();
        String currentYear = String.format("%4d", calendar.get(Calendar.YEAR));
// setup Retrofit
        Gson gson = new GsonBuilder()
                .setDateFormat(DateFormat.FULL, DateFormat.FULL)
                .create();

        Gson gson2 = new GsonBuilder()
                .registerTypeAdapter(Date.class, new FDDateType())
                .create();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(FD_BASE_URI)                                           //Базовая часть адреса
                .addConverterFactory(GsonConverterFactory.create(gson2))
                .build();

        mRetrofitAPI = mRetrofit.create(IRetrofitAPI.class);
        mRetrofitAPI.getData(currentYear).enqueue(new Callback<List<FDCompetitions>>() {
            @Override
            public void onResponse(Call<List<FDCompetitions>> call, Response<List<FDCompetitions>> response) {
                if (response.body() == null) {
                    showResult();
                    return;
                }
                List<FDCompetitions> competitions = response.body();


// test!!! записать в ContentProvider
//                RecipeUtils.bulkInsertBackground(mContext.getContentResolver(), getSupportLoaderManager(), list, mLoaderDb);
// test!!!  сохранить  timestamp
//                saveReLoadTimePreference();
                showResult();
            }

            @Override
            public void onFailure(Call<List<FDCompetitions>> call, Throwable t) {
                Timber.d(t.getMessage());
                showResult();
            }
        });
    }

}
