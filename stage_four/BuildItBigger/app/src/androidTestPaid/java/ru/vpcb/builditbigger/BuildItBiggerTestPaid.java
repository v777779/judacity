package ru.vpcb.builditbigger;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static ru.vpcb.constants.Constants.MESSAGE_TEST_OK;
import static ru.vpcb.constants.Constants.MESSAGE_TEST_PAID_OK;
import static ru.vpcb.constants.Constants.REQUEST_TEST_GET_TEMPLATE;
import static ru.vpcb.constants.Constants.REQUEST_TEST_OUT_TEMPLATE;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class BuildItBiggerTestPaid {
    private static final String TAG = BuildItBiggerTestPaid.class.getSimpleName();
    private final Object mSyncObject = new Object();


    private class EndpointsCallback implements ICallback {
        private String mResult;

        public EndpointsCallback() {
            this.mResult = "";
        }

        @Override
        public void onComplete(String s) {
            mResult = s;
            assertEquals(REQUEST_TEST_OUT_TEMPLATE, mResult);
            synchronized (mSyncObject) {
                mSyncObject.notify();
            }
        }

        @Override
        public void onComplete(int value) {

        }
    }

    @Test
    public void useAppContext() throws Exception {
        EndpointsCallback endpointsCallback = new EndpointsCallback();
        new EndpointsAsyncTask(endpointsCallback, REQUEST_TEST_GET_TEMPLATE).execute();

        synchronized (mSyncObject) {
            mSyncObject.wait();
        }
        Log.i(TAG,MESSAGE_TEST_PAID_OK);
    }
}
