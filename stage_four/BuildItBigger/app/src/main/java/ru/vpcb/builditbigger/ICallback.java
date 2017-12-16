package ru.vpcb.builditbigger;

/**
 *  Callback interface for EndpointAsyncTask, RecyclerView and Fragment callback
 */

public interface ICallback {
    /**
     *  Starts Activity or Fragment  and passes the string s value to called activity
     *  Called at onPostExecute() of EndpointAsyncTask object method
     *
     * @param s  String value of result that EndpoitnAsyncTask got from Google App Engine Module
     */
    void onComplete(String s);

    /**
     *  RecyclerView callback method
     *  Starts new Fragment activity and passes imageId to that activity
     *  Starting new Fragment done via mButton.click() method
     *
     * @param value  imageId index which is used to extract imageId from mList List<Integer> object
     */
    void onComplete(int value);

    /**
     *  Unlocks IdlingResource
     *  Called after Detail Activity or Fragment  set mJokText value
     *  Used for testing application
     */
    void onCompleteIdling();
}