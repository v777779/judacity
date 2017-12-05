package ru.vpcb.popularmovie;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.URL;

import ru.vpcb.popularmovie.utils.NetworkUtils;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 29-Sep-17
 * Email: vadim.v.voronov@gmail.com
 */

public class MovieTask extends AsyncTask<URL, Void, String> {
    private MainActivity context;
    public MovieTask(Context context) {
        this.context = (MainActivity)context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();                       // метод оставлен
        context.showPB();
    }

    @Override
    protected String doInBackground(URL... params) {
        URL searchUrl = params[0];
        String githubSearchResults = null;
        try {
            githubSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return githubSearchResults;
    }

    @Override
    protected void onPostExecute(String s) {

        if (s != null && !s.equals("")) {
            context.showRV();
        } else {
            context.showError();
        }
    }

}
