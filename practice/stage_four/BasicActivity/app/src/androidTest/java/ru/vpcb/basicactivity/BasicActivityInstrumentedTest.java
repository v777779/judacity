package ru.vpcb.basicactivity;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static ru.vpcb.basicactivity.MainActivity.CONNECT_TIMEOUT;
import static ru.vpcb.basicactivity.MainActivity.MESSAGE_TEST_OK;
import static ru.vpcb.basicactivity.MainActivity.REQUEST_TEST_GET_TEMPLATE;
import static ru.vpcb.basicactivity.MainActivity.REQUEST_TEST_OUT_TEMPLATE;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class BasicActivityInstrumentedTest {
    private static final String TAG = BasicActivityInstrumentedTest.class.getSimpleName();
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
    }

    @Test
    public void useAppContext() throws Exception {
        EndpointsCallback endpointsCallback = new EndpointsCallback();
        new EndpointsAsyncTask(endpointsCallback, REQUEST_TEST_GET_TEMPLATE).execute();

        synchronized (mSyncObject) {
            mSyncObject.wait();
        }
        Log.i(TAG, MESSAGE_TEST_OK);
    }
}
