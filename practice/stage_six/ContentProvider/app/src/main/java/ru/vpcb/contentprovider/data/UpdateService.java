package ru.vpcb.contentprovider.data;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.vpcb.contentprovider.BuildConfig;
import ru.vpcb.contentprovider.R;
import timber.log.Timber;

import static ru.vpcb.contentprovider.utils.Constants.FD_BASE_URI;
import static ru.vpcb.contentprovider.utils.Constants.UPDATE_SERVICE_TAG;
import static ru.vpcb.contentprovider.utils.FootballUtils.getPrefBool;
import static ru.vpcb.contentprovider.utils.FootballUtils.isCursorEmpty;
import static ru.vpcb.contentprovider.utils.FootballUtils.isOnline;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 25-Jan-18
 * Email: vadim.v.voronov@gmail.com
 */
public class UpdateService extends IntentService {

    private boolean isCursorEmpty;
    private boolean isSmartUpdate;

    private Retrofit mRetrofit;
    private IRetrofitAPI mRetrofitAPI;
    private OkHttpClient mOkHttpClient;


    public UpdateService() {
        super(UPDATE_SERVICE_TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (intent != null) {
            String action = intent.getAction();
            if (action == getString(R.string.action_update)) {
                onActionUpdate();
            }
        }
    }

    private void onActionUpdate() {
        isCursorEmpty = isCursorEmpty(this);

        if (!isOnline(this)) {                                     // no network
            sendBroadcast(new Intent(getString(R.string.broadcast_no_network))
                    .putExtra(getString(R.string.extra_empty_cursor), isCursorEmpty));
            return;
        }

        sendBroadcast(new Intent(getString(R.string.broadcast_update_started)));

// load smart update mode
        isSmartUpdate = getPrefBool(this, R.string.pref_smart_update_key,
                R.bool.pref_smart_update_default);

// load data into local database
        List<FDCompetitions> competitions = new ArrayList<>();
        List<FDCompetitions> list = loadCompetitions(getLastYear());
        if (list != null && !list.isEmpty()){
            competitions.addAll(list);

        }
        list = loadCompetitions(getCurrentYear());
        if (list != null && !list.isEmpty()) {
            competitions.addAll(list);
        }

        sendBroadcast(new Intent(getString(R.string.broadcast_update_finished)));
    }


    // test!!!
// placeholder
    private void showProgress() {

    }

    // test!!!
// placeholder
    private void showResult() {

    }

    private String getCurrentYear() {
        Calendar calendar = Calendar.getInstance();
        return String.format("%4d", calendar.get(Calendar.YEAR));
    }

    private String getLastYear() {
        Calendar calendar = Calendar.getInstance();
        return String.format("%4d", calendar.get(Calendar.YEAR) - 1);
    }

    private List<FDCompetitions> loadCompetitions(String season) {

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

        Response<List<FDCompetitions>> response = null;
        try {
            response = mRetrofitAPI.getCompetitions(season).execute();
        } catch (IOException e) {
            Timber.d(getString(R.string.retrofit_response_exception) + e.getMessage());
        }

        if (response == null || response.body() == null) {
            Timber.d(getString(R.string.retrofit_response_empty));
            return null;
        }

        List<FDCompetitions> list = response.body();
        return list;
    }


}
