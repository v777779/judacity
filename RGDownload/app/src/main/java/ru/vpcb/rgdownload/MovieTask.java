package ru.vpcb.rgdownload;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.URL;

import ru.vpcb.rgdownload.utils.NetworkUtils;

public class MovieTask extends AsyncTask<URL, Void, String> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();                       // метод оставлен
//            mSearchResultsTextView.setVisibility(View.INVISIBLE);
//            mProgressBar.setVisibility(View.VISIBLE);
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
//            showJsonDataView();
        } else {
//            showErrorMessage();
        }
    }
}
