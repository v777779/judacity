package ru.vpcb.builditbigger;

import android.os.AsyncTask;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.udacity.gradle.builditbigger.backend.myApi.MyApi;

import java.io.IOException;

/**
 * Google Cloud Endpoint AsyncTask support class.
 * Send API request to Google App Engine Module.
 * Gets response from  Google App Engine Module.
 * Calls callback onComplete(String) method provided by calling activity.
 */
class EndpointsAsyncTask extends AsyncTask<Void, Void, String> {
    /**
     * MyApi service object used to make request.
     */
    private static MyApi myApiService = null;
    /**
     * ICallback Interface object provided by calling activity.
     */
    private ICallback mCallback;
    /**
     * String value holds request from calling activity to Googla App Engine Module.
     */
    private String mRequest;

    /**
     * Constructor
     *
     * @param callback ICallback Interface object provided by calling activity.
     * @param request  String value of request.
     */
    public EndpointsAsyncTask(ICallback callback, String request) {
        mCallback = callback;
        mRequest = request;
    }

    /**
     * Creates MyApi service object and makes request to Google AppEngine module in background.
     * Returns result of request to onPostExecute() method.
     * Request can not be canceled.
     *
     * @param params
     * @return
     */
    @Override
    protected String doInBackground(Void... params) {
        if (myApiService == null) {  // Only do this once
            MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    // options for running against local devappserver
                    // - 10.0.2.2 is localhost's IP address in Android emulator
                    // - turn off compression when running against local devappserver
                    .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
            // end options for devappserver
            myApiService = builder.build();
        }
        try {
            return myApiService.sayHi(mRequest).execute().getData();
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    /**
     * Passes resukt of request to onComplete() method of calling activity
     *
     * @param s String  response value
     */
    @Override
    protected void onPostExecute(String s) {
//        Toast.makeText(context, result, Toast.LENGTH_LONG).show();
        if (s == null || s.isEmpty()) return;
        mCallback.onComplete(s);
    }
}