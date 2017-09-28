package ru.vpcb.rgdownload;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.URL;

import ru.vpcb.rgdownload.utils.NetworkUtils;

public class CheckTask extends AsyncTask<Context, Void, Boolean> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();                       // метод оставлен
    }


    @Override
    protected Boolean doInBackground(Context... params) {
        if(params == null || params.length == 0 || params[0]== null ) {
            return false;
        }

        try {
            boolean flag = isOnline(params[0]);
            return flag;
        }catch (Exception e) {
            e.printStackTrace();
        }

        return false;

    }

    @Override
    protected void onPostExecute(Boolean flag) {

    }

    private boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
