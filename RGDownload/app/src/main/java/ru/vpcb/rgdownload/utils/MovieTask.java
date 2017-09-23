package ru.vpcb.rgdownload.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import ru.vpcb.rgdownload.MovieData;

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
    protected void onPostExecute(String githubSearchResults) {

//            mProgressBar.setVisibility(View.INVISIBLE);
        if (githubSearchResults != null && !githubSearchResults.equals("")) {

//            showJsonDataView();
//            mSearchResultsTextView.setText(githubSearchResults);
   //         caller.setPageData(githubSearchResults);

        } else {
//            showErrorMessage();
   //         caller.setPageData("No Internet access");

        }

    }


}
