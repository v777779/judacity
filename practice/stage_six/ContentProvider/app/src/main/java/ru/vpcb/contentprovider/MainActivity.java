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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.vpcb.contentprovider.data.FDCompetitions;
import ru.vpcb.contentprovider.data.FDDateType;
import ru.vpcb.contentprovider.data.FDFixtures;
import ru.vpcb.contentprovider.data.FDTeams;
import ru.vpcb.contentprovider.data.IRetrofitAPI;
import timber.log.Timber;


import static ru.vpcb.contentprovider.data.Constants.FD_BASE_URI;
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

    private String mString;


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
//                startRetrofitLoaderT();  // ok
//                startRetrofitLoaderCF();
//                startRetrofitLoaderCFM();
                startRetrofitLoaderCFT();


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
        Gson gson = new GsonBuilder()
                .setDateFormat(DateFormat.FULL, DateFormat.FULL)
                .create();
        String year = getCurrentYear();

// setup Retrofit
        mRetrofit = new Retrofit.Builder()
                .baseUrl(FD_BASE_URI)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        mRetrofitAPI = mRetrofit.create(IRetrofitAPI.class);
        mRetrofitAPI.getData(year).enqueue(new Callback<List<FDCompetitions>>() {
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

    private void startRetrofitLoaderT() {
        if (!isOnline(this)) {
            return;
        }
        showProgress();
// setup data
        Gson gson = null;
        int id = 450;
        String competition = String.format("%d", id);
// setup Retrofit
        mRetrofit = new Retrofit.Builder()
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
        Gson gson = null;
        int id = 446;
        String competition = String.format("%d", id);
// setup Retrofit
        mRetrofit = new Retrofit.Builder()
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
                FDFixtures fdFixtures = response.body();
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
        Gson gson = null;
        int id = 446;
        String competition = String.format("%d", id);
        int matchDay = 25;
// setup Retrofit
        mRetrofit = new Retrofit.Builder()
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
        Gson gson = null;
        int id = 446;
        String competition = String.format("%d",id);
        int nDays = 50;
        String nTime = FD_TIME_PAST;

        String timeFrame = String.format("%s%d", nTime, nDays);

// setup Retrofit
        mRetrofit = new Retrofit.Builder()
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
